package com.pig4cloud.pig.biz.controller;

import cn.hutool.core.io.IoUtil;
import com.pig4cloud.pig.biz.api.dto.ChatRequestDTO;
import com.pig4cloud.pig.biz.service.BizChatService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.InputStream;

@RestController
@RequiredArgsConstructor
@RequestMapping("/chat")
@Slf4j
public class BizChatController {

	private final BizChatService chatService;

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
						   HttpServletResponse response) {
		// 1. 设置 SSE 标准响应头
		response.setContentType("text/event-stream;charset=UTF-8");
		response.setCharacterEncoding("UTF-8");
		response.setHeader("Cache-Control", "no-cache");
		response.setHeader("Connection", "keep-alive");
		// 2. 调用 Service 获取业务流，并写入响应
		// try-with-resources 自动关闭流
		try (InputStream inputStream = chatService.streamChat(echoId, query)) {
			// 将输入流直接拷贝到输出流 (Hutool工具类)
			IoUtil.copy(inputStream, response.getOutputStream());
			// 确保缓冲区数据发送
			response.flushBuffer();
		} catch (Exception e) {
			log.error("流式对话处理异常: echoId={}, query={}", echoId, query, e);
			// 3. 异常处理：尝试向前端发送一条报错数据的 SSE 事件
			try {
				// SSE 格式: data: <内容>\n\n
				String errorMsg = String.format("data: %s\n\n", "系统繁忙: " + e.getMessage());
				response.getWriter().write(errorMsg);
				response.flushBuffer();
			} catch (IOException ignored) {
				// 如果连接已断开，忽略写入错误
			}
		}
	}
}