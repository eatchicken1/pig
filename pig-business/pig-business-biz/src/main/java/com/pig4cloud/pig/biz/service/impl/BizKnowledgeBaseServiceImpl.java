package com.pig4cloud.pig.biz.service.impl;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pig4cloud.pig.admin.api.dto.UserInfo;
import com.pig4cloud.pig.biz.api.feign.FrequencyAiClient;
import com.pig4cloud.pig.biz.entity.BizKnowledgeBaseEntity;
import com.pig4cloud.pig.biz.mapper.BizKnowledgeBaseMapper;
import com.pig4cloud.pig.biz.service.BizKnowledgeBaseService;
import com.pig4cloud.pig.common.core.util.R;
import com.pig4cloud.pig.common.security.service.PigUser;
import com.pig4cloud.pig.common.security.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Future;

/**
 * 知识库文档表
 *
 * @author pig
 * @date 2025-12-18 14:56:17
 */
@RequiredArgsConstructor
@Slf4j
@Service
public class BizKnowledgeBaseServiceImpl extends ServiceImpl<BizKnowledgeBaseMapper, BizKnowledgeBaseEntity> implements BizKnowledgeBaseService {

	private final FrequencyAiClient frequencyAiClient;

	//@Async() // 异步处理，防止 PDF 解析过久导致前端请求超时
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void uploadAndTrain(Path filePath, Long echoId, PigUser userInfo) {
		File file = new File(String.valueOf(filePath));

		if (!file.exists() || file.length() == 0) {
			throw new RuntimeException("训练文件不存在或为空");
		}
		// 1. 保存元数据并初始化状态
		BizKnowledgeBaseEntity kb = new BizKnowledgeBaseEntity();
		kb.setEchoId(echoId);
		kb.setFileName(file.getName());
		kb.setVectorStatus("1"); // 状态：处理中
		kb.setCreateBy(userInfo.getUsername());
		kb.setFileUrl(String.valueOf(filePath));
		this.save(kb);

		try {
			String filename = file.getName();
			String content = "";

			// 确保文件有效且未被关闭
			content = Files.readString(filePath, StandardCharsets.UTF_8);

			if (StrUtil.isBlank(content)) {
				throw new IllegalArgumentException("文件内容为空");
			}
			// 2. 组装请求参数 (对应 Python 侧的 KnowledgeIngestRequest)
			Map<String, Object> aiRequest = new HashMap<>();
			// 获取当前登录用户 ID (Pig 框架标准用法)
			aiRequest.put("user_id", userInfo.getId());
			aiRequest.put("echo_id", echoId.toString());
			aiRequest.put("content", content);
			aiRequest.put("source_name", filename);
			// 可选：添加一些元数据
			Map<String, Object> metadata = new HashMap<>();
			metadata.put("upload_time", System.currentTimeMillis());
			aiRequest.put("metadata", metadata);
			log.info("正在向 AI 引擎投喂文件: {}, 长度: {}", filename, content.length());
			kb.setVectorStatus("2");
			this.updateById(kb);
			// 3. 远程调用
			frequencyAiClient.ingestKnowledge(aiRequest);
		} catch (Exception e) {
			log.error("投喂失败", e);
			kb.setVectorStatus("9");
			this.updateById(kb);
			//return R.failed("投喂失败: " + e.getMessage());
			throw new RuntimeException("投喂失败: " + e.getMessage());
		}
	}
}
