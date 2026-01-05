package com.pig4cloud.pig.biz.controller;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.StrUtil;
import com.pig4cloud.pig.biz.api.dto.ChatRequestDTO;
import com.pig4cloud.pig.biz.entity.BizChatHistoryEntity;
import com.pig4cloud.pig.biz.service.BizChatHistoryService;
import com.pig4cloud.pig.biz.service.BizChatService;
import com.pig4cloud.pig.common.security.util.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
@RequestMapping("/chat")
@Slf4j
public class BizChatController {

	private final BizChatService chatService;
	private final BizChatHistoryService chatHistoryService;

	/**
	 * 与数字分身流式对话
	 *
	 * @param echoId   分身ID
	 * @param query    用户提问
	 * @param response HTTP响应对象
	 */
	@Operation(summary = "流式对话", description = "SSE流式返回AI回复")
	@GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	public void streamChat(@RequestParam("echoId") String echoId,
						   @RequestParam("query") String query,
						   @RequestParam(value = "conversationId", required = false) String conversationId,
						   HttpServletResponse response) {

		String finalConversationId = StrUtil.isBlank(conversationId)
				? String.valueOf(System.currentTimeMillis())
				: conversationId;

		// 1. 设置标准的 SSE 响应头，防止 Nginx 或浏览器缓存
		response.setContentType("text/event-stream");
		response.setCharacterEncoding("UTF-8");
		response.setHeader("Cache-Control", "no-cache");
		response.setHeader("Connection", "keep-alive");
		response.setHeader("X-Accel-Buffering", "no"); // 关键：禁用 Nginx 缓冲
		ByteArrayOutputStream capturedOutput = new ByteArrayOutputStream();
		// 2. 获取业务流并透传
		try (InputStream inputStream = chatService.streamChat(echoId, query, finalConversationId);
			 OutputStream outputStream = response.getOutputStream()) {

			byte[] buffer = new byte[1024]; // 缓冲区设小一点，或者直接 byte-by-byte
			int bytesRead;
			while ((bytesRead = inputStream.read(buffer)) != -1) {
				outputStream.write(buffer, 0, bytesRead);
				// 核心修改：每次写入后立即 Flush，确保前端实时收到
				outputStream.flush();
				response.flushBuffer();

				capturedOutput.write(buffer, 0, bytesRead);
			}
			String aiContent = capturedOutput.toString(StandardCharsets.UTF_8);
			saveChatHistory(finalConversationId, "assistant", aiContent);
		} catch (Exception e) {
			log.error("流式对话异常", e);
			// 流式响应开始后很难返回标准 JSON 错误，通常在流中写入错误标记，或由前端处理连接断开
		}
	}

	/**
	 * 异步或同步保存聊天记录
	 */
	private void saveChatHistory(String conversationId, String role, String content) {
		if (StrUtil.isBlank(content)) return;

		try {
			BizChatHistoryEntity history = new BizChatHistoryEntity();
			history.setSessionId(conversationId);
			history.setRole(role); // "assistant"
			history.setContent(content);
			history.setCreateTime(LocalDateTime.now());
			history.setSenderId(SecurityUtils.getUser().getId());
			history.setReceiverId(SecurityUtils.getUser().getId());
			history.setCreateTime(LocalDateTime.now());
			history.setIsAiGenerated("1");
			chatHistoryService.save(history);
		} catch (Exception e) {
			log.error("保存聊天记录失败: {}", e.getMessage());
		}
	}
}