package com.pig4cloud.pig.biz.service.impl;

import com.pig4cloud.pig.biz.service.BizChatService;
import com.pig4cloud.pig.biz.api.dto.ChatRequestDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Slf4j
@Service
@RequiredArgsConstructor
public class BizChatServiceImpl implements BizChatService {


	@Override
	public Flux<String> streamChat(ChatRequestDTO request) {
		log.info("收到聊天请求: echoId={}, content={}", request.getEchoId(), request.getContent());
		return null;
	}
}