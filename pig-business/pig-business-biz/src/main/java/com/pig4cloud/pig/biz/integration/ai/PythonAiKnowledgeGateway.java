package com.pig4cloud.pig.biz.integration.ai;

import com.pig4cloud.pig.ai.api.knowledge.AiKnowledgeGateway;
import com.pig4cloud.pig.ai.api.knowledge.KnowledgeBatchDeleteCommand;
import com.pig4cloud.pig.ai.api.knowledge.KnowledgeDeleteCommand;
import com.pig4cloud.pig.ai.api.knowledge.KnowledgeTrainCommand;
import com.pig4cloud.pig.biz.api.dto.BatchDeleteKnowledgeRequest;
import com.pig4cloud.pig.biz.api.dto.DeleteKnowledgeRequest;
import com.pig4cloud.pig.biz.api.dto.TrainKnowledgeRequest;
import com.pig4cloud.pig.biz.api.feign.FrequencyAiClient;
import com.pig4cloud.pig.common.core.util.R;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class PythonAiKnowledgeGateway implements AiKnowledgeGateway {

	private final FrequencyAiClient frequencyAiClient;

	@Override
	public void train(KnowledgeTrainCommand command) {
		TrainKnowledgeRequest request = new TrainKnowledgeRequest();
		request.setKnowledge_id(command.getKnowledgeId());
		request.setUser_id(command.getUserId());
		request.setEcho_id(command.getEchoId());
		request.setFile_url(command.getFileUrl());
		request.setFile_type(command.getFileType());
		request.setSource_name(command.getSourceName());
		frequencyAiClient.trainKnowledge(request);
	}

	@Override
	public boolean delete(KnowledgeDeleteCommand command) {
		DeleteKnowledgeRequest request = new DeleteKnowledgeRequest();
		request.setKnowledgeId(command.getKnowledgeId());
		request.setEchoId(command.getEchoId());
		request.setUserId(command.getUserId());
		R<?> result = frequencyAiClient.deleteKnowledge(request);
		return result != null && result.getCode() == 0;
	}

	@Override
	public boolean batchDelete(KnowledgeBatchDeleteCommand command) {
		BatchDeleteKnowledgeRequest request = new BatchDeleteKnowledgeRequest();
		request.setItems(command.getItems() == null
				? Collections.emptyList()
				: command.getItems().stream().map(this::toDeleteRequest).collect(Collectors.toList()));
		R<?> result = frequencyAiClient.batchDeleteKnowledge(request);
		return result != null && result.getCode() == 0;
	}

	private DeleteKnowledgeRequest toDeleteRequest(KnowledgeDeleteCommand command) {
		DeleteKnowledgeRequest request = new DeleteKnowledgeRequest();
		request.setKnowledgeId(command.getKnowledgeId());
		request.setEchoId(command.getEchoId());
		request.setUserId(command.getUserId());
		return request;
	}
}
