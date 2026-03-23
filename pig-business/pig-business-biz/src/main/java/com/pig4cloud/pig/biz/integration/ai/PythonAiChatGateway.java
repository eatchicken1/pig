package com.pig4cloud.pig.biz.integration.ai;

import com.pig4cloud.pig.ai.api.chat.AiChatCommand;
import com.pig4cloud.pig.ai.api.chat.AiChatGateway;
import com.pig4cloud.pig.ai.api.chat.AiChatMessage;
import com.pig4cloud.pig.biz.api.dto.ChatRequestDTO;
import com.pig4cloud.pig.biz.api.feign.FrequencyAiClient;
import feign.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class PythonAiChatGateway implements AiChatGateway {

	private final FrequencyAiClient frequencyAiClient;

	@Override
	public InputStream stream(AiChatCommand command) {
		Response response;
		try {
			response = frequencyAiClient.streamChat(toDto(command));
		}
		catch (Exception e) {
			log.error("调用 Python AI 对话流失败", e);
			throw new RuntimeException("AI服务暂时不可用，请稍后再试");
		}

		if (response == null || response.body() == null) {
			throw new RuntimeException("AI服务响应为空");
		}

		try {
			return response.body().asInputStream();
		}
		catch (IOException e) {
			log.error("读取 Python AI 对话流失败", e);
			throw new RuntimeException("数据流读取失败");
		}
	}

	private ChatRequestDTO toDto(AiChatCommand command) {
		ChatRequestDTO dto = new ChatRequestDTO();
		dto.setUserId(command.getUserId());
		dto.setEchoId(command.getEchoId());
		dto.setQuery(command.getQuery());
		dto.setEchoNickname(command.getEchoNickname());
		dto.setEchoPrompt(command.getEchoPrompt());
		dto.setEchoTone(command.getEchoTone());
		dto.setEchoTags(command.getEchoTags());
		dto.setHistory(toMessages(command.getHistory()));
		return dto;
	}

	private List<ChatRequestDTO.Message> toMessages(List<AiChatMessage> history) {
		if (history == null || history.isEmpty()) {
			return Collections.emptyList();
		}

		return history.stream()
				.map(item -> new ChatRequestDTO.Message(item.getRole(), item.getContent()))
				.collect(Collectors.toList());
	}
}
