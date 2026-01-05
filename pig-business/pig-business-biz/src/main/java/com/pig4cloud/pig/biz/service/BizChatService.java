package com.pig4cloud.pig.biz.service;

import com.pig4cloud.pig.biz.api.dto.ChatRequestDTO;
import reactor.core.publisher.Flux;

import java.io.InputStream;

public interface BizChatService {
	/**
	 * 发起流式对话
	 *
	 * @param echoId 数字分身ID
	 * @param query  用户提问
	 * @return AI响应的数据流
	 */
	InputStream streamChat(String echoId, String query, String conversationId);
}