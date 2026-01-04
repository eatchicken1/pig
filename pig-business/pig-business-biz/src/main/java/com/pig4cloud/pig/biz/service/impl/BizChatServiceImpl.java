package com.pig4cloud.pig.biz.service.impl;

import com.pig4cloud.pig.biz.api.feign.FrequencyAiClient;
import com.pig4cloud.pig.biz.service.BizChatService;
import com.pig4cloud.pig.biz.api.dto.ChatRequestDTO;
import com.pig4cloud.pig.common.security.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import feign.Response;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

@Slf4j
@Service
@RequiredArgsConstructor
public class BizChatServiceImpl implements BizChatService {
	private final FrequencyAiClient frequencyAiClient;

	@Override
	public InputStream streamChat(String echoId, String query) {
		// 1. 获取当前登录用户的 ID (业务逻辑)
		Long userId = SecurityUtils.getUser().getId();

		log.info("用户[{}]发起对话请求, echoId={}, query={}", userId, echoId, query);

		// 2. 组装请求参数
		ChatRequestDTO chatRequest = new ChatRequestDTO();
		chatRequest.setEchoId(echoId);
		chatRequest.setQuery(query);
		chatRequest.setUserId(String.valueOf(userId));
		chatRequest.setHistory(new ArrayList<>());
		// TODO: 未来可以在这里查询数据库，填充 chatRequest.setHistory(...)

		// 3. 调用远程 AI 服务
		Response response;
		try {
			response = frequencyAiClient.streamChat(chatRequest);
		} catch (Exception e) {
			log.error("调用AI引擎失败", e);
			throw new RuntimeException("AI服务暂时不可用，请稍后再试");
		}

		// 4. 校验响应状态
		if (response == null || response.body() == null) {
			throw new RuntimeException("AI服务响应为空");
		}

		// 5. 返回流 (注意：流的关闭交由调用方 Controller 处理)
		try {
			return response.body().asInputStream();
		} catch (IOException e) {
			log.error("获取AI响应流异常", e);
			throw new RuntimeException("数据流读取失败");
		}
	}
}