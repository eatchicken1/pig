package com.pig4cloud.pig.biz.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.pig4cloud.pig.biz.api.feign.FrequencyAiClient;
import com.pig4cloud.pig.biz.entity.BizChatHistoryEntity;
import com.pig4cloud.pig.biz.entity.BizEchoProfileEntity;
import com.pig4cloud.pig.biz.service.BizChatHistoryService;
import com.pig4cloud.pig.biz.service.BizChatService;
import com.pig4cloud.pig.biz.api.dto.ChatRequestDTO;
import com.pig4cloud.pig.biz.service.BizEchoProfileService;
import com.pig4cloud.pig.common.security.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import feign.Response;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class BizChatServiceImpl implements BizChatService {
	private final FrequencyAiClient frequencyAiClient;
	private final BizChatHistoryService chatHistoryService;
	private final BizEchoProfileService echoProfileService;

	@Override
	public InputStream streamChat(String echoId, String query, String conversationId) {
		// 1. 获取当前登录用户的 ID (业务逻辑)
		Long userId = SecurityUtils.getUser().getId();
		log.info("用户[{}]发起对话请求, echoId={}, query={}", userId, echoId, query);
		saveUserMessage(conversationId, query, userId);

		// ---获取分身人设 ---
		//TODO: 获取分身人设,后续可做缓存
		BizEchoProfileEntity profile = echoProfileService.getById(Long.valueOf(echoId));

		// 2. 组装请求参数
		ChatRequestDTO chatRequest = new ChatRequestDTO();
		chatRequest.setEchoId(echoId);
		chatRequest.setQuery(query);
		chatRequest.setUserId(String.valueOf(userId));
		if (profile != null) {
			chatRequest.setEchoNickname(profile.getNickname());
			chatRequest.setEchoPrompt(profile.getPersonalityPrompt()); // e.g. "高冷学霸..."
			chatRequest.setEchoTone(profile.getVoiceTone());           // e.g. "严肃、犀利"
			chatRequest.setEchoTags(profile.getTags());                // e.g. "Java, 架构"
		} else {
			// 兜底策略：如果找不到分身，给默认值
			chatRequest.setEchoNickname("AI助手");
			chatRequest.setEchoPrompt("你是一个通用的智能助手。");
			chatRequest.setEchoTone("友好、乐于助人");
		}

		// 取最近 20 条，按时间正序排列
		List<BizChatHistoryEntity> historyList = chatHistoryService.list(
				Wrappers.<BizChatHistoryEntity>lambdaQuery()
						.eq(BizChatHistoryEntity::getSessionId, conversationId)
						.orderByAsc(BizChatHistoryEntity::getCreateTime)
						.last("LIMIT 20")
		);
		// 转换历史记录格式
		if (!historyList.isEmpty()) {
			// 排除掉刚刚插入的那条当前问题（如果查询策略包含它），或者全部传给AI由AI去重
			// 通常做法：History 不包含当前 query，当前 query 单独传
			List<ChatRequestDTO.Message> context = historyList.stream()
					.filter(h -> !h.getContent().equals(query)) // 简单去重，可选
					.map(h -> {
						ChatRequestDTO.Message msg = new ChatRequestDTO.Message();
						msg.setRole(h.getRole()); // "user" 或 "assistant"
						msg.setContent(h.getContent());
						return msg;
					}).collect(Collectors.toList());
			chatRequest.setHistory(context);
		} else {
			chatRequest.setHistory(Collections.emptyList());
		}

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

	private void saveUserMessage(String conversationId, String query, Long userId) {
		BizChatHistoryEntity userMsg = new BizChatHistoryEntity();
		userMsg.setSessionId(conversationId);
		userMsg.setRole("user");
		userMsg.setContent(query);
		userMsg.setCreateTime(LocalDateTime.now());
		userMsg.setSenderId(userId);
		userMsg.setReceiverId(userId);//自己和分身对话
		userMsg.setIsAiGenerated("0");
		chatHistoryService.save(userMsg);
	}
}