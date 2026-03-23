package com.pig4cloud.pig.biz.controller;

import com.pig4cloud.pig.ai.api.feign.RemoteAiAppClient;
import feign.Response;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.InputStream;
import java.io.OutputStream;

@RestController
@RequiredArgsConstructor
@RequestMapping("/chat")
@Slf4j
public class BizChatController {

	private final RemoteAiAppClient remoteAiAppClient;

	/**
	 * 与数字分身流式对话
	 *
	 * @param query    用户提问
	 * @param response HTTP响应对象
	 */
	@Operation(summary = "流式对话", description = "SSE流式返回AI回复")
	@GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	public void streamChat(@RequestParam("query") String query,
						   @RequestParam(value = "conversationId", required = false) String conversationId,
						   @RequestParam(value = "echoId", required = false) Long echoId,
						   HttpServletResponse response) {
		response.setContentType("text/event-stream");
		response.setCharacterEncoding("UTF-8");
		response.setHeader("Cache-Control", "no-cache");
		response.setHeader("Connection", "keep-alive");
		response.setHeader("X-Accel-Buffering", "no");
		try (Response remoteResponse = remoteAiAppClient.streamChat(query, conversationId, echoId);
			 InputStream inputStream = remoteResponse.body().asInputStream();
			 OutputStream outputStream = response.getOutputStream()) {
			byte[] buffer = new byte[1024];
			int bytesRead;
			while ((bytesRead = inputStream.read(buffer)) != -1) {
				outputStream.write(buffer, 0, bytesRead);
				outputStream.flush();
				response.flushBuffer();
			}
		} catch (Exception e) {
			log.error("代理 AI 对话流失败", e);
		}
	}
}
