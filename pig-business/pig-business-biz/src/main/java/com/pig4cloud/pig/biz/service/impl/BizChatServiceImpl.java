package com.pig4cloud.pig.biz.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.pig4cloud.pig.ai.api.chat.AiChatCommand;
import com.pig4cloud.pig.ai.api.chat.AiChatGateway;
import com.pig4cloud.pig.ai.api.chat.AiChatMessage;
import com.pig4cloud.pig.biz.entity.BizChatHistoryEntity;
import com.pig4cloud.pig.biz.entity.BizEchoProfileEntity;
import com.pig4cloud.pig.biz.service.BizEchoProfileService;
import com.pig4cloud.pig.biz.service.BizChatHistoryService;
import com.pig4cloud.pig.biz.service.BizChatService;
import com.pig4cloud.pig.common.security.util.SecurityUtils;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class BizChatServiceImpl implements BizChatService {
	private final AiChatGateway chatGateway;
	private final BizChatHistoryService chatHistoryService;
	private final BizEchoProfileService echoProfileService;

	@Override
	public InputStream streamChat(String query, String conversationId) {
		// 1. 获取当前登录用户的 ID (业务逻辑)
		Long userId = SecurityUtils.getUser().getId();
		log.info("用户[{}]发起对话请求, echoId={}, query={}", userId, userId, query);
		saveUserMessage(conversationId, query, userId);

		// ---获取分身人设 ---
		//TODO: 获取分身人设,后续可做缓存
		BizEchoProfileEntity profile = echoProfileService.getById(userId);

		// 2. 组装请求参数
		AiChatCommand chatRequest = new AiChatCommand();
		chatRequest.setEchoId(userId);
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
			List<AiChatMessage> context = historyList.stream()
					.filter(h -> !h.getContent().equals(query)) // 简单去重，可选
					.map(h -> new AiChatMessage(h.getRole(), h.getContent()))
					.collect(Collectors.toList());
			chatRequest.setHistory(context);
		} else {
			chatRequest.setHistory(Collections.emptyList());
		}

		return chatGateway.stream(chatRequest);
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
