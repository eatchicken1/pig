package com.pig4cloud.pig.biz.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONUtil;
import com.pig4cloud.pig.admin.api.dto.UserDTO;
import com.pig4cloud.pig.admin.api.dto.UserInfo;
import com.pig4cloud.pig.admin.api.feign.RemoteUserService;
import com.pig4cloud.pig.biz.api.dto.AiVibeCheckDTO;
import com.pig4cloud.pig.biz.api.vo.AiVibeCheckResultVO;
import com.pig4cloud.pig.biz.entity.BizChatHistoryEntity;
import com.pig4cloud.pig.biz.entity.BizEchoProfileEntity;
import com.pig4cloud.pig.biz.entity.BizMatchRecordEntity;
import com.pig4cloud.pig.biz.mapper.BizChatHistoryMapper;
import com.pig4cloud.pig.biz.mapper.BizEchoProfileMapper;
import com.pig4cloud.pig.biz.mapper.BizMatchRecordMapper;
import com.pig4cloud.pig.common.core.constant.CommonConstants;
import com.pig4cloud.pig.common.core.util.R;
import com.pig4cloud.pig.common.security.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class FrequencyAiServiceImpl {

	private final RemoteUserService remoteUserService;
	private final BizEchoProfileMapper bizEchoProfileMapper;
	private final BizMatchRecordMapper bizMatchRecordMapper;
	private final BizChatHistoryMapper bizChatHistoryMapper;

	@Value("${frequency.ai.url:http://localhost:8000/api/v1/ai/vibe-check}")
	private String aiEngineUrl;

	// 定义通过分数的阈值 (比如 75 分及格)
	private static final int PASS_SCORE = 75;

	/**
	 * 发起同频测试 (Vibe Check)
	 */
	@Transactional(rollbackFor = Exception.class)
	public AiVibeCheckResultVO startVibeCheck(AiVibeCheckDTO dto) {
		// 1. 获取用户信息
		Long currentUserId = SecurityUtils.getUser().getId();
		UserInfo userA = getSysUserFromRemote(currentUserId);
		UserInfo userB = getSysUserFromRemote(dto.getTargetUserId());

		if (userA == null || userB == null) {
			throw new RuntimeException("无法获取用户信息");
		}

		// 2. 预先生成 SessionID
		String sessionId = IdUtil.simpleUUID();

		// 3. --- 关键优化：先初始化记录 (状态: 0 进行中) ---
		BizMatchRecordEntity matchRecord = new BizMatchRecordEntity();
		matchRecord.setInitiatorId(userA.getUserId());
		matchRecord.setTargetId(userB.getUserId());
		matchRecord.setSessionId(sessionId);
		matchRecord.setStatus("0"); // 状态 0: 进行中
		matchRecord.setUnlockType("0");
		matchRecord.setMatchScore(0); // 初始0分
		// 显式设置时间（防止 MyBatis 自动填充失效）
		matchRecord.setCreateTime(LocalDateTime.now());
		matchRecord.setUpdateTime(LocalDateTime.now());

		bizMatchRecordMapper.insert(matchRecord);
		log.info("AI 匹配任务已创建，SessionID: {}, 等待 AI 响应...", sessionId);

		// 4. 组装参数并调用 Python
		try {
			Map<String, Object> requestMap = new HashMap<>();
			requestMap.put("user_a", buildUserProfile(userA));
			requestMap.put("user_b", buildUserProfile(userB));
			requestMap.put("rounds", dto.getRounds());
			requestMap.put("session_id", sessionId);

			// --- 同步阻塞调用 (耗时操作) ---
			AiVibeCheckResultVO result = callAiEngine(requestMap);

			// 5. --- 调用成功：更新结果 ---
			updateMatchResult(matchRecord, result);
			return result;

		} catch (Exception e) {
			// 6. --- 异常处理：更新状态为取消/失败 ---
			log.error("AI 引擎调用失败，更新记录状态为异常", e);
			matchRecord.setStatus("3"); // 状态 3: 已取消/异常
			matchRecord.setMatchSummary("系统异常：" + e.getMessage());
			matchRecord.setUpdateTime(LocalDateTime.now());
			bizMatchRecordMapper.updateById(matchRecord);
			throw e; // 继续抛出异常给前端
		}
	}

	/**
	 * 更新匹配结果和保存聊天记录
	 */
	private void updateMatchResult(BizMatchRecordEntity record, AiVibeCheckResultVO result) {
		// A. 更新主表状态
		Integer score = result.getScore();
		record.setMatchScore(score);
		record.setMatchSummary(result.getSummary());

		// 核心逻辑：根据分数判断是否匹配成功
		if (score >= PASS_SCORE) {
			record.setStatus("1"); // 匹配成功/解锁
		} else {
			record.setStatus("2"); // 匹配失败/婉拒
		}
		record.setUpdateTime(LocalDateTime.now());

		bizMatchRecordMapper.updateById(record);

		// B. 批量保存对话记录
		if (result.getDialogue() != null) {
			for (Map<String, Object> msg : result.getDialogue()) {
				BizChatHistoryEntity history = new BizChatHistoryEntity();
				history.setSessionId(record.getSessionId());
				history.setSenderId(record.getInitiatorId());
				history.setReceiverId(record.getTargetId());
				history.setRole((String) msg.get("role"));
				Object contentObj = msg.get("content");
				history.setContent(contentObj != null ? contentObj.toString() : "");
				// history.setTenantId(1L); // 如果有多租户逻辑

				bizChatHistoryMapper.insert(history);
			}
		}
		log.info("AI 匹配完成，MatchID: {}, 最终得分: {}, 状态: {}", record.getMatchId(), score, record.getStatus());
	}

	/**
	 * 辅助方法：构建智能画像
	 * 策略：数据库有人设用人设，没人设用默认值
	 */
	private Map<String, Object> buildUserProfile(UserInfo user) {
		Map<String, Object> profile = new HashMap<>();
		BizEchoProfileEntity echo = bizEchoProfileMapper.selectById(user.getUserId());
		if (echo != null) {
			profile.put("name", StrUtil.isNotBlank(echo.getNickname()) ? echo.getNickname() : user.getUsername());
			profile.put("mbti", StrUtil.isNotBlank(echo.getTags()) ? echo.getTags() : "未知人格");
			profile.put("interests", echo.getTags());
			profile.put("style", echo.getVoiceTone());
		} else {
			profile.put("name", user.getUsername());
			profile.put("mbti", "ISFJ");
			profile.put("interests", "神秘用户");
			profile.put("style", "温和");
		}
		return profile;
	}

	/**
	 * 辅助方法：通过 Feign 获取用户信息
	 */
	private UserInfo getSysUserFromRemote(Long userId) {
		try {
			UserDTO userDTO = new UserDTO();
			userDTO.setUserId(userId);
			R<UserInfo> result = remoteUserService.info(userDTO);
			if (result != null && result.getCode() == CommonConstants.SUCCESS && result.getData() != null) {
				UserInfo info = result.getData();
				UserInfo userInfo = new UserInfo();
				Object innerUser = BeanUtil.getProperty(info, "userInfo");
				BeanUtil.copyProperties(Objects.requireNonNullElse(innerUser, info), userInfo);
				if (userInfo.getUserId() == null) {
					userInfo.setUserId(userId);
				}
				return userInfo;
			}
		} catch (Exception e) {
			log.error("远程获取用户失败 userId={}", userId, e);
		}
		return null;
	}

	/**
	 * 辅助方法：发送 HTTP 请求给 Python
	 */
	private AiVibeCheckResultVO callAiEngine(Map<String, Object> payload) {
		try {
			String jsonBody = JSONUtil.toJsonStr(payload);
			log.info(">>>> 调用 Python 引擎: URL={}, Payload={}", aiEngineUrl, jsonBody);

			// 设置超时时间 60秒 (AI 生成比较慢)
			HttpResponse response = HttpRequest.post(aiEngineUrl)
					.body(jsonBody)
					.timeout(60000)
					.execute();

			String responseBody = response.body();
			// log.info("<<<< Python 引擎响应: {}", responseBody); // 响应可能很长，调试时再开

			if (!response.isOk()) {
				throw new RuntimeException("AI 引擎返回错误状态码: " + response.getStatus());
			}

			return JSONUtil.toBean(responseBody, AiVibeCheckResultVO.class);

		} catch (Exception e) {
			log.error("调用 AI 引擎异常", e);
			throw new RuntimeException("AI 服务暂时不可用，请稍后重试");
		}
	}
}