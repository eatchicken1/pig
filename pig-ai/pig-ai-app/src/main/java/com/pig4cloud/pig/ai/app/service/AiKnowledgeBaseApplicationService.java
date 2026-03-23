package com.pig4cloud.pig.ai.app.service;

import com.pig4cloud.pig.ai.api.knowledge.KnowledgeSearchHitDTO;
import com.pig4cloud.pig.biz.api.dto.ai.CreateKnowledgeRecordRequest;
import com.pig4cloud.pig.biz.api.dto.ai.KnowledgeRecordDTO;
import com.pig4cloud.pig.biz.api.dto.ai.KnowledgeSearchRequest;
import com.pig4cloud.pig.biz.api.dto.ai.UpdateKnowledgeRecordRequest;
import com.pig4cloud.pig.biz.api.feign.RemoteBizAiDataClient;
import com.pig4cloud.pig.common.core.constant.CommonConstants;
import com.pig4cloud.pig.common.core.util.R;
import com.pig4cloud.pig.common.file.core.FileProperties;
import com.pig4cloud.pig.common.file.core.FileTemplate;
import com.pig4cloud.pig.common.security.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AiKnowledgeBaseApplicationService {

	private final RemoteBizAiDataClient remoteBizAiDataClient;

	private final FileTemplate fileTemplate;

	private final FileProperties fileProperties;

	private final KnowledgeVectorStoreService knowledgeVectorStoreService;

	public Long uploadAndTrain(MultipartFile file, Long userId) {
		try {
			String ossUrl = upload(file);
			CreateKnowledgeRecordRequest request = new CreateKnowledgeRecordRequest();
			request.setEchoId(userId);
			request.setFileName(file.getOriginalFilename());
			request.setFileType(getFileSuffix(file.getOriginalFilename()));
			request.setFileUrl(ossUrl);
			request.setVectorStatus("0");
			request.setCreateBy(SecurityUtils.getUser().getUsername());
			KnowledgeRecordDTO record = unwrap(remoteBizAiDataClient.createKnowledgeRecord(request), "创建知识库记录失败");
			asyncStartTrain(record.getId(), SecurityUtils.getUser().getUsername());
			return record.getId();
		}
		catch (Exception e) {
			log.error("上传文件并启动训练失败", e);
			throw new RuntimeException("上传文件失败");
		}
	}

	public Boolean removeKnowledgeBatchByIds(List<Long> ids) {
		if (ids == null || ids.isEmpty()) {
			return false;
		}
		List<KnowledgeRecordDTO> records = unwrap(remoteBizAiDataClient.listKnowledgeRecords(ids), "读取知识库记录失败");
		if (records == null || records.isEmpty()) {
			return false;
		}
		for (KnowledgeRecordDTO record : records) {
			knowledgeVectorStoreService.deleteKnowledge(record.getId());
		}
		return unwrap(remoteBizAiDataClient.deleteKnowledgeRecords(ids), "删除知识库记录失败");
	}

	public List<KnowledgeSearchHitDTO> search(KnowledgeSearchRequest request) {
		return knowledgeVectorStoreService.search(request.getQuery(), request.getEchoId(), request.getLimit());
	}

	@Async("aiTrainExecutor")
	public void asyncStartTrain(Long knowledgeId, String operator) {
		try {
			updateKnowledgeStatus(knowledgeId, "1", null, null, operator);
			KnowledgeRecordDTO record = unwrap(remoteBizAiDataClient.getKnowledgeRecord(knowledgeId), "读取知识库详情失败");
			KnowledgeVectorStoreService.KnowledgeTrainResult result = knowledgeVectorStoreService.train(record);
			updateKnowledgeStatus(knowledgeId, "2", result.getTokenCount(), result.getSummary(), operator);
		}
		catch (Exception e) {
			log.error("AI 训练失败，knowledgeId={}", knowledgeId, e);
			updateKnowledgeStatus(knowledgeId, "9", null, null, operator);
		}
	}

	private void updateKnowledgeStatus(Long knowledgeId, String vectorStatus, Integer tokenCount, String summary,
			String operator) {
		UpdateKnowledgeRecordRequest request = new UpdateKnowledgeRecordRequest();
		request.setVectorStatus(vectorStatus);
		request.setTokenCount(tokenCount);
		request.setSummary(summary);
		request.setUpdateBy(operator);
		unwrap(remoteBizAiDataClient.updateKnowledgeRecord(knowledgeId, request), "更新知识库状态失败");
	}

	private String upload(MultipartFile file) throws Exception {
		String fileName = UUID.randomUUID() + getFileSuffix(file.getOriginalFilename(), true);
		String bucketName = fileProperties.getBucketName();
		try (InputStream inputStream = file.getInputStream()) {
			fileTemplate.putObject(bucketName, fileName, inputStream, file.getContentType());
		}
		String endpoint = fileProperties.getOss().getEndpoint();
		if (endpoint.startsWith("http://") || endpoint.startsWith("https://")) {
			endpoint = endpoint.substring(endpoint.indexOf("://") + 3);
		}
		return String.format("https://%s.%s/%s", bucketName, endpoint, fileName);
	}

	private String getFileSuffix(String fileName) {
		return getFileSuffix(fileName, false);
	}

	private String getFileSuffix(String fileName, boolean includeDot) {
		if (fileName == null || !fileName.contains(".")) {
			return includeDot ? "" : null;
		}
		String suffix = fileName.substring(fileName.lastIndexOf('.') + (includeDot ? 0 : 1));
		return suffix;
	}

	private <T> T unwrap(R<T> response, String action) {
		if (response == null) {
			throw new RuntimeException(action + "，远程返回为空");
		}
		if (response.getCode() != CommonConstants.SUCCESS) {
			throw new RuntimeException(action + "，原因: " + response.getMsg());
		}
		return response.getData();
	}

}
