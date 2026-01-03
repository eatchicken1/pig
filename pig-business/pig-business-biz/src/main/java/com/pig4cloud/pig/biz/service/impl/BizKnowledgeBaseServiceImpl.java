package com.pig4cloud.pig.biz.service.impl;


import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pig4cloud.pig.biz.api.dto.DeleteKnowledgeRequest;
import com.pig4cloud.pig.biz.api.dto.TrainKnowledgeRequest;
import com.pig4cloud.pig.biz.api.feign.FrequencyAiClient;
import com.pig4cloud.pig.biz.entity.BizKnowledgeBaseEntity;
import com.pig4cloud.pig.biz.event.KnowledgeDeleteEvent;
import com.pig4cloud.pig.biz.mapper.BizKnowledgeBaseMapper;
import com.pig4cloud.pig.biz.service.BizKnowledgeBaseService;
import com.pig4cloud.pig.common.core.util.R;
import com.pig4cloud.pig.common.file.core.FileProperties;
import com.pig4cloud.pig.common.file.core.FileTemplate;
import com.pig4cloud.pig.common.security.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 知识库文档表
 *
 * @author pig
 * @date 2025-12-18 14:56:17
 */
@RequiredArgsConstructor
@Slf4j
@Service
@EnableAsync
public class BizKnowledgeBaseServiceImpl extends ServiceImpl<BizKnowledgeBaseMapper, BizKnowledgeBaseEntity> implements BizKnowledgeBaseService {

	private final FrequencyAiClient frequencyAiClient;
	private final FileTemplate fileTemplate;
	private final FileProperties fileProperties;
	private final ApplicationContext applicationContext;

	//@Async() // 异步处理，防止 PDF 解析过久导致前端请求超时
	@Override
	@Transactional
	public Long uploadAndTrain(MultipartFile file, Long userId) {
		try {
			//上传 OSS
			String ossUrl = this.upload(file);

			//保存 DB 记录（状态 = UPLOADED）
			BizKnowledgeBaseEntity entity = new BizKnowledgeBaseEntity();
			entity.setEchoId(userId);
			entity.setFileName(file.getOriginalFilename());
			entity.setFileType(FilenameUtils.getExtension(file.getOriginalFilename()));
			entity.setFileUrl(ossUrl);
			entity.setVectorStatus("0");
			entity.setCreateTime(LocalDateTime.now());
			entity.setCreateBy(SecurityUtils.getUser().getUsername());

			this.save(entity);

			//异步触发 AI 训练（不阻塞）
			this.asyncStartTrain(entity.getId());

			return entity.getId();
		} catch (Exception e) {
			log.error("上传文件失败", e);
			return null;
		}
	}
	@Override
	@Transactional(rollbackFor = Exception.class) // 添加事务，保证数据库操作的原子性
	public Boolean removeKnowledgeBatchByIds(List<Long> ids) {
		if (CollUtil.isEmpty(ids)) {
			return false;
		}
		// 1. 查询待删除数据（为了构建 AI 删除所需的参数）
		List<BizKnowledgeBaseEntity> knowledgeList = this.listByIds(ids);
		if (CollUtil.isEmpty(knowledgeList)) {
			return false;
		}
		// 2. 先执行数据库删除
		boolean removed = this.removeBatchByIds(ids);
		if (removed) {
			// 3. 构建事件参数
			List<DeleteKnowledgeRequest> deleteRequests = knowledgeList.stream().map(entity -> {
				DeleteKnowledgeRequest req = new DeleteKnowledgeRequest();
				req.setKnowledgeId(entity.getId());
				req.setEchoId(String.valueOf(entity.getEchoId()));
				req.setUserId(String.valueOf(entity.getCreateBy()));
				return req;
			}).collect(Collectors.toList());
			// 4. 发布事件 (由监听器去异步、批量处理)
			applicationContext.publishEvent(new KnowledgeDeleteEvent(this, deleteRequests));
		}
		return removed;
	}


	@Async("aiTrainExecutor") // 使用异步注解
	public void asyncStartTrain(Long knowledgeId) {
		try {
			this.startTrain(knowledgeId);
		} catch (Exception e) {
			log.error("AI 训练失败", e);
			BizKnowledgeBaseEntity entity = this.getById(knowledgeId);
			if (entity != null) {
				entity.setVectorStatus("9");
				entity.setUpdateTime(LocalDateTime.now());
				entity.setUpdateBy(SecurityUtils.getUser().getUsername());
				this.updateById(entity);
			}
		}
	}
	private void startTrain(Long knowledgeId) {
		//更新状态
		BizKnowledgeBaseEntity entityBase = this.getById(knowledgeId);
		entityBase.setVectorStatus("1");
		entityBase.setUpdateTime(LocalDateTime.now());
		entityBase.setUpdateBy(SecurityUtils.getUser().getUsername());
		this.updateById(entityBase);
		BizKnowledgeBaseEntity entity = this.getById(knowledgeId);
		//调用 AI 服务
		TrainKnowledgeRequest req = new TrainKnowledgeRequest();
		req.setKnowledge_id(knowledgeId);
		req.setUser_id(entityBase.getEchoId().toString());
		req.setEcho_id(entityBase.getEchoId().toString());
		req.setFile_url(entity.getFileUrl());
		req.setFile_type(entity.getFileType());
		req.setSource_name(entity.getFileName());
		frequencyAiClient.trainKnowledge(req);

		//成功
		entity.setVectorStatus("2");
		this.updateById(entity);
	}
	private String upload(MultipartFile file) throws IOException {
		// 1. 生成唯一文件名（避免重复）
		String originalFilename = file.getOriginalFilename();
		String fileSuffix = null;
		if (originalFilename != null) {
			fileSuffix = originalFilename.substring(originalFilename.lastIndexOf("."));
		}
		String fileName = UUID.randomUUID().toString() + fileSuffix;
		// 2. 获取存储桶名称（从配置文件读取）
		String bucketName = fileProperties.getBucketName();
		// 3. 上传文件到OSS
		try (InputStream inputStream = file.getInputStream()) {
			// 调用OSS模板上传，指定文件类型
			fileTemplate.putObject(bucketName, fileName, inputStream, file.getContentType());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		// 4. 生成访问URL（阿里云OSS访问路径格式）
		// 格式：https://bucketName.endpoint/fileName
		String endpoint = fileProperties.getOss().getEndpoint();
		if (endpoint.startsWith("http://") || endpoint.startsWith("https://")) {
			endpoint = endpoint.substring(endpoint.indexOf("://") + 3);
		}
		return String.format("https://%s.%s/%s", bucketName, endpoint, fileName);
	}

}
