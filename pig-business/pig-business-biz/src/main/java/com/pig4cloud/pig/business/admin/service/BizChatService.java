package com.pig4cloud.pig.business.admin.service;

import com.pig4cloud.pig.business.api.dto.ChatRequestDTO;
import reactor.core.publisher.Flux;

public interface BizChatService {
	/**
	 * AI 流式对话核心业务
	 */
	Flux<String> streamChat(ChatRequestDTO request);
}