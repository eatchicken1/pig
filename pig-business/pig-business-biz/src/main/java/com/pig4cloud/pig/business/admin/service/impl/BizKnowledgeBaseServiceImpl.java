package com.pig4cloud.pig.business.admin.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pig4cloud.pig.business.api.entity.BizKnowledgeBaseEntity;
import com.pig4cloud.pig.business.admin.mapper.BizKnowledgeBaseMapper;
import com.pig4cloud.pig.business.admin.service.BizKnowledgeBaseService;
import com.pig4cloud.pig.business.service.FrequencyVectorService;
import com.pig4cloud.pig.common.file.oss.service.OssTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.core.io.InputStreamResource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 知识库文档表
 *
 * @author pig
 * @date 2025-12-18 14:56:17
 */
@Slf4j
@Service
public class BizKnowledgeBaseServiceImpl extends ServiceImpl<BizKnowledgeBaseMapper, BizKnowledgeBaseEntity> implements BizKnowledgeBaseService {

	private final FrequencyVectorService vectorService;
	private final OssTemplate ossTemplate;

	public BizKnowledgeBaseServiceImpl(FrequencyVectorService vectorService, OssTemplate ossTemplate) {
		this.vectorService = vectorService;
		this.ossTemplate = ossTemplate;
	}

	@Async // 异步处理，防止 PDF 解析过久导致前端请求超时
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void uploadAndTrain(MultipartFile file, Long echoId) {
		// 1. 保存元数据并初始化状态
		BizKnowledgeBaseEntity kb = new BizKnowledgeBaseEntity();
		kb.setEchoId(echoId);
		kb.setFileName(file.getOriginalFilename());
		kb.setVectorStatus("1"); // 状态：处理中
		this.save(kb);

		try {
			// 2. 将文件存入 OSS (PIG自带功能)
			String path = String.format("frequency/echo_%d/%s", echoId, file.getOriginalFilename());
			ossTemplate.putObject("frequency", path, file.getInputStream());

			// 3. 解析文档内容 (Spring AI + Tika)
			TikaDocumentReader reader = new TikaDocumentReader(new InputStreamResource(file.getInputStream()));
			List<Document> rawDocs = reader.get();

			// 4. 文档切片 (Token 级别切片，防止上下文溢出)
			// 参数：每片500字，重叠50字，以保持语义连贯
			TokenTextSplitter splitter = new TokenTextSplitter(500, 50, 5, 10000, true);
			List<Document> chunks = splitter.apply(rawDocs);

			// 5. 注入 Metadata (核心：确保检索时只查到自己的 Echo)
			for (Document chunk : chunks) {
				chunk.getMetadata().put("echo_id", echoId);
				chunk.getMetadata().put("kb_id", kb.getId());
			}

			// 6. 执行向量化并存入 Redis Vector Store
			vectorService.addDocuments(chunks);

			// 7. 更新状态为“已完成”
			kb.setVectorStatus("2");
			this.updateById(kb);
			log.info("Echo [{}] 的文档 [{}] 训练成功！", echoId, kb.getFileName());

		} catch (Exception e) {
			log.error("训练流程异常", e);
			kb.setVectorStatus("9"); // 状态：失败
			this.updateById(kb);
		}
	}
}
