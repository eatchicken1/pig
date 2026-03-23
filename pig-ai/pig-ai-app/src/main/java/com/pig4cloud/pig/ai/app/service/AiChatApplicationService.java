package com.pig4cloud.pig.ai.app.service;

import com.pig4cloud.pig.ai.app.config.AiModuleProperties;
import com.pig4cloud.pig.biz.api.dto.ai.ChatHistoryMessageDTO;
import com.pig4cloud.pig.biz.api.dto.ai.ChatHistoryPageDTO;
import com.pig4cloud.pig.biz.api.dto.ai.EchoProfileDTO;
import com.pig4cloud.pig.biz.api.dto.ai.SaveChatMessageRequest;
import com.pig4cloud.pig.biz.api.feign.RemoteBizAiDataClient;
import com.pig4cloud.pig.common.core.constant.CommonConstants;
import com.pig4cloud.pig.common.core.util.R;
import com.pig4cloud.pig.common.security.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Flux;

import java.io.IOException;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AiChatApplicationService {

	private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy年MM月dd日 HH:mm:ss");

	private final ChatClient aiChatClient;

	private final RemoteBizAiDataClient remoteBizAiDataClient;

	private final KnowledgeVectorStoreService knowledgeVectorStoreService;

	private final AiModuleProperties aiModuleProperties;

	public InputStream streamChat(String query, String conversationId, Long echoId) {
		Long currentUserId = SecurityUtils.getUser().getId();
		Long targetEchoId = resolveEchoId(echoId, currentUserId);
		saveChatMessage(conversationId, currentUserId, targetEchoId, query, "user", "0");
		EchoProfileDTO profile = unwrap(remoteBizAiDataClient.getEchoProfile(targetEchoId), "读取分身资料失败");
		List<ChatHistoryMessageDTO> historyList = unwrap(
				remoteBizAiDataClient.listChatHistory(conversationId, aiModuleProperties.getRagHistoryLimit()), "读取聊天上下文失败");
		String knowledgeContext = knowledgeVectorStoreService.buildContext(query, targetEchoId, aiModuleProperties.getRagTopK());
		List<Message> messages = buildMessages(profile, historyList, query, knowledgeContext);
		return toInputStream(aiChatClient.prompt().messages(messages).stream().content());
	}

	public void saveAssistantMessage(String conversationId, String content, Long echoId) {
		if (!StringUtils.hasText(content)) {
			return;
		}
		Long currentUserId = SecurityUtils.getUser().getId();
		Long targetEchoId = resolveEchoId(echoId, currentUserId);
		saveChatMessage(conversationId, targetEchoId, currentUserId, content, "assistant", "1");
	}

	public ChatHistoryPageDTO pageChatHistory(long current, long size, String conversationId) {
		Long currentUserId = SecurityUtils.getUser().getId();
		return unwrap(remoteBizAiDataClient.pageChatHistory(currentUserId, current, size, conversationId), "读取历史聊天分页失败");
	}

	private void saveChatMessage(String conversationId, Long senderId, Long receiverId, String content, String role,
			String isAiGenerated) {
		SaveChatMessageRequest request = new SaveChatMessageRequest();
		request.setSessionId(conversationId);
		request.setSenderId(senderId);
		request.setReceiverId(receiverId);
		request.setContent(content);
		request.setRole(role);
		request.setIsAiGenerated(isAiGenerated);
		Boolean saved = unwrap(remoteBizAiDataClient.saveChatMessage(request), "保存聊天记录失败");
		if (!Boolean.TRUE.equals(saved)) {
			log.warn("聊天记录保存返回 false, sessionId={}", conversationId);
		}
	}

	private Long resolveEchoId(Long echoId, Long currentUserId) {
		return echoId == null ? currentUserId : echoId;
	}

	private List<Message> buildMessages(EchoProfileDTO profile, List<ChatHistoryMessageDTO> historyList, String query,
			String knowledgeContext) {
		List<Message> messages = new ArrayList<>();
		messages.add(new SystemMessage(buildSystemPrompt(profile, knowledgeContext)));
		if (historyList != null) {
			for (ChatHistoryMessageDTO item : historyList) {
				if (!StringUtils.hasText(item.getContent()) || query.equals(item.getContent())) {
					continue;
				}
				if ("assistant".equalsIgnoreCase(item.getRole()) || "ai".equalsIgnoreCase(item.getRole())) {
					messages.add(new AssistantMessage(item.getContent()));
				}
				else if ("system".equalsIgnoreCase(item.getRole())) {
					messages.add(new SystemMessage(item.getContent()));
				}
				else {
					messages.add(new UserMessage(item.getContent()));
				}
			}
		}
		messages.add(new UserMessage(query));
		return messages;
	}

	private String buildSystemPrompt(EchoProfileDTO profile, String knowledgeContext) {
		String nickname = profile != null && StringUtils.hasText(profile.getNickname()) ? profile.getNickname() : "AI助手";
		String prompt = profile != null && StringUtils.hasText(profile.getPersonalityPrompt())
				? profile.getPersonalityPrompt()
				: "你是一个通用的智能助手。";
		String tone = profile != null && StringUtils.hasText(profile.getVoiceTone()) ? profile.getVoiceTone() : "友好、乐于助人";
		String tags = profile != null && StringUtils.hasText(profile.getTags()) ? profile.getTags() : "校园问答, 日常陪伴";
		String currentTime = LocalDateTime.now().format(DATE_TIME_FORMATTER);
		String ragSection = StringUtils.hasText(knowledgeContext) ? knowledgeContext : "暂无可用知识片段";
		return """
				你现在的身份是：【%s】
				你的核心人设：%s
				你的语言风格：%s
				你擅长的领域：%s
				
				【客观世界状态 - 必须以此为准】
				当前真实时间：%s
				
				请严格遵守以下规则：
				1. 你是用户正在对话的数字分身，不要说自己是模型或程序。
				2. 不要机械重复自我介绍，只回答用户当前的问题。
				3. 先保证回答有用，再保持%s的语气风格。
				4. 如引用知识，请优先基于以下片段，用自然口语表达，不要照抄原文。
				
				【可参考知识片段】
				%s
				""".formatted(nickname, prompt, tone, tags, currentTime, tone, ragSection);
	}

	private InputStream toInputStream(Flux<String> contentFlux) {
		try {
			PipedOutputStream outputStream = new PipedOutputStream();
			PipedInputStream inputStream = new PipedInputStream(outputStream, 16 * 1024);
			contentFlux.doOnNext(chunk -> writeChunk(outputStream, chunk))
					.doOnError(error -> {
						log.error("Spring AI 流式对话失败", error);
						writeChunk(outputStream, "系统繁忙，请稍后重试。");
						closeQuietly(outputStream);
					})
					.doOnComplete(() -> closeQuietly(outputStream))
					.subscribe();
			return inputStream;
		}
		catch (IOException e) {
			throw new RuntimeException("创建流式输出失败", e);
		}
	}

	private void writeChunk(PipedOutputStream outputStream, String chunk) {
		try {
			outputStream.write(chunk.getBytes(StandardCharsets.UTF_8));
			outputStream.flush();
		}
		catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private void closeQuietly(PipedOutputStream outputStream) {
		try {
			outputStream.close();
		}
		catch (IOException ignored) {
		}
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
