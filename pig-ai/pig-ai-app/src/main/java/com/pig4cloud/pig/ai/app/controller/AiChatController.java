package com.pig4cloud.pig.ai.app.controller;

import com.pig4cloud.pig.ai.app.service.AiChatApplicationService;
import com.pig4cloud.pig.biz.api.dto.ai.ChatHistoryPageDTO;
import com.pig4cloud.pig.common.core.util.R;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/chat")
public class AiChatController {

	private final AiChatApplicationService aiChatApplicationService;

	@Operation(summary = "流式对话", description = "SSE 流式返回 AI 回复")
	@GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	public void streamChat(@RequestParam("query") String query,
			@RequestParam(value = "conversationId", required = false) String conversationId,
			@RequestParam(value = "echoId", required = false) Long echoId,
			HttpServletResponse response) {
		String finalConversationId = StringUtils.hasText(conversationId) ? conversationId
				: String.valueOf(System.currentTimeMillis());
		response.setContentType("text/event-stream");
		response.setCharacterEncoding("UTF-8");
		response.setHeader("Cache-Control", "no-cache");
		response.setHeader("Connection", "keep-alive");
		response.setHeader("X-Accel-Buffering", "no");
		ByteArrayOutputStream capturedOutput = new ByteArrayOutputStream();
		try (InputStream inputStream = aiChatApplicationService.streamChat(query, finalConversationId, echoId);
			 OutputStream outputStream = response.getOutputStream()) {
			byte[] buffer = new byte[1024];
			int bytesRead;
			while ((bytesRead = inputStream.read(buffer)) != -1) {
				outputStream.write(buffer, 0, bytesRead);
				outputStream.flush();
				response.flushBuffer();
				capturedOutput.write(buffer, 0, bytesRead);
			}
			aiChatApplicationService.saveAssistantMessage(finalConversationId,
					capturedOutput.toString(StandardCharsets.UTF_8), echoId);
		}
		catch (Exception e) {
			log.error("AI 分身流式对话失败", e);
		}
	}

	@Operation(summary = "分页获取聊天历史")
	@GetMapping("/history/page")
	public R<ChatHistoryPageDTO> pageHistory(@RequestParam(value = "current", defaultValue = "1") long current,
			@RequestParam(value = "size", defaultValue = "20") long size,
			@RequestParam(value = "conversationId", required = false) String conversationId) {
		return R.ok(aiChatApplicationService.pageChatHistory(current, size, conversationId));
	}

}
