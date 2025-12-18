package com.pig4cloud.pig.business.admin.service.impl;

import com.pig4cloud.pig.business.admin.service.BizChatService;
import com.pig4cloud.pig.business.admin.service.BizEchoProfileService;
import com.pig4cloud.pig.business.api.dto.ChatRequestDTO;
import com.pig4cloud.pig.business.api.entity.BizEchoProfileEntity;
import com.pig4cloud.pig.business.service.FrequencyVectorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import org.springframework.ai.openai.OpenAiChatModel;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class BizChatServiceImpl implements BizChatService {

	private final OpenAiChatModel chatClient;
	private final FrequencyVectorService vectorService;
	private final BizEchoProfileService echoProfileService;

	public Flux<String> streamChat(ChatRequestDTO request) {
		// 1. 获取 Echo 人设
		BizEchoProfileEntity profile = echoProfileService.getById(request.getEchoId());
		String personality = (profile != null) ? profile.getPersonalityPrompt() : "一个乐于助人的学生";

		// 2. 相似度检索 (RAG: 基于 echoId 过滤)
		// 注意：此处需要扩展 vectorService 的 similaritySearch 方法，带上 Metadata 过滤
		List<Document> documents = vectorService.similaritySearchWithFilter(request.getContent(), request.getEchoId());
		String context = documents.stream().map(Document::getText).collect(Collectors.joining("\n"));

		// 3. 构建 System Prompt (注入灵魂和知识)
		String systemTemplate = """
            你现在的身份是: {personality}。
            你必须以此人设的口吻进行回复。
            以下是你可以参考的知识背景:
            ---
            {context}
            ---
            如果背景信息中没有相关内容，请根据你的人设灵活回答，但不要编造事实。
            """;

		SystemPromptTemplate template = new SystemPromptTemplate(systemTemplate);
		SystemMessage systemMessage = (SystemMessage) template.createMessage(Map.of(
				"personality", personality,
				"context", context
		));

		// 4. 调用大模型并返回流
		UserMessage userMessage = new UserMessage(request.getContent());
		Prompt prompt = new Prompt(List.of(systemMessage, userMessage));

		return chatClient.stream(prompt)
				.map(response -> response.getResult().getOutput().getText())
				.filter(Objects::nonNull);
	}
}