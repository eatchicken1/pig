package com.pig4cloud.pig.biz.controller;

import com.pig4cloud.pig.biz.service.BizChatService;
import com.pig4cloud.pig.biz.api.dto.ChatRequestDTO;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

@RestController
@RequiredArgsConstructor
@RequestMapping("/chat")
public class BizChatController {

	private final BizChatService chatService;

	/**
	 * 流式对话接口 (Server-Sent Events)
	 */
	@PostMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	@Operation(summary = "与Echo流式对话")
	public Flux<String> stream(@RequestBody ChatRequestDTO request) {
		return chatService.streamChat(request);
	}
}