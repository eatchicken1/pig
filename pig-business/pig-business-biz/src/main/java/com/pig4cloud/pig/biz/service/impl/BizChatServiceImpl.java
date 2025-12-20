package com.pig4cloud.pig.biz.service.impl;

import com.pig4cloud.pig.biz.service.BizChatService;
import com.pig4cloud.pig.biz.service.BizEchoProfileService;
import com.pig4cloud.pig.biz.api.dto.ChatRequestDTO;
import com.pig4cloud.pig.biz.entity.BizEchoProfileEntity;
import com.pig4cloud.pig.biz.service.FrequencyVectorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.document.Document;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import org.springframework.ai.openai.OpenAiChatModel;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class BizChatServiceImpl implements BizChatService {

	private final OpenAiChatModel chatClient; // 注入我们手动配置的 Bean
	private final FrequencyVectorService vectorService;
	private final BizEchoProfileService echoProfileService;

	@Override
	public Flux<String> streamChat(ChatRequestDTO request) {
		log.info("收到聊天请求: echoId={}, content={}", request.getEchoId(), request.getContent());

		// 1. 获取 Echo 人设
		BizEchoProfileEntity profile = echoProfileService.getById(request.getEchoId());
		String personality = (profile != null) ? profile.getPersonalityPrompt() : "一个乐于助人的 AI 助手";

		// 2. RAG 检索
		List<Document> documents = vectorService.similaritySearchWithFilter(request.getContent(), request.getEchoId());
		String context = documents.isEmpty() ? "（无相关背景知识）" :
				documents.stream().map(Document::getText).collect(Collectors.joining("\n"));

		// 3. 构建 Prompt
		String systemPromptStr = """
            你现在的身份是: %s。
            请严格基于以下知识库内容回答问题，如果知识库没有提及，请用你的人设委婉告知。
            
            知识库内容:
            ---
            %s
            ---
            """.formatted(personality, context);

		SystemMessage systemMessage = new SystemMessage(systemPromptStr);
		UserMessage userMessage = new UserMessage(request.getContent());
		Prompt prompt = new Prompt(List.of(systemMessage, userMessage));

		// 4. 流式返回
		return chatClient.stream(prompt)
				.map(response -> {
					String text = response.getResult().getOutput().getText();
					return text != null ? text : ""; // 防止 null
				})
				.doOnError(e -> log.error("AI 对话流异常", e));
	}
}