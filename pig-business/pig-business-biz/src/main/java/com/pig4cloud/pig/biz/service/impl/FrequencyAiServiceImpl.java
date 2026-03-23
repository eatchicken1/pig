package com.pig4cloud.pig.biz.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.pig4cloud.pig.admin.api.dto.UserDTO;
import com.pig4cloud.pig.admin.api.dto.UserInfo;
import com.pig4cloud.pig.admin.api.feign.RemoteUserService;
import com.pig4cloud.pig.ai.api.match.AiDialogueMessage;
import com.pig4cloud.pig.ai.api.match.AiMatchGateway;
import com.pig4cloud.pig.ai.api.match.AiVibeCheckCommand;
import com.pig4cloud.pig.ai.api.match.AiVibeCheckResult;
import com.pig4cloud.pig.ai.api.match.AiVibeProfile;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class FrequencyAiServiceImpl {

	private static final int PASS_SCORE = 75;

	private final RemoteUserService remoteUserService;
	private final BizEchoProfileMapper bizEchoProfileMapper;
	private final BizMatchRecordMapper bizMatchRecordMapper;
	private final BizChatHistoryMapper bizChatHistoryMapper;
	private final AiMatchGateway aiMatchGateway;

	@Transactional(rollbackFor = Exception.class)
	public AiVibeCheckResultVO startVibeCheck(AiVibeCheckDTO dto) {
		Long currentUserId = SecurityUtils.getUser().getId();
		UserInfo userA = getSysUserFromRemote(currentUserId);
		UserInfo userB = getSysUserFromRemote(dto.getTargetUserId());

		if (userA == null || userB == null) {
			throw new RuntimeException("无法获取用户信息");
		}

		String sessionId = IdUtil.simpleUUID();

		BizMatchRecordEntity matchRecord = new BizMatchRecordEntity();
		matchRecord.setInitiatorId(userA.getUserId());
		matchRecord.setTargetId(userB.getUserId());
		matchRecord.setSessionId(sessionId);
		matchRecord.setStatus("0");
		matchRecord.setUnlockType("0");
		matchRecord.setMatchScore(0);
		matchRecord.setCreateTime(LocalDateTime.now());
		matchRecord.setUpdateTime(LocalDateTime.now());

		bizMatchRecordMapper.insert(matchRecord);
		log.info("AI 匹配任务已创建，SessionID: {}, 等待 AI 响应...", sessionId);

		try {
			AiVibeCheckCommand command = new AiVibeCheckCommand();
			command.setUserA(buildUserProfile(userA));
			command.setUserB(buildUserProfile(userB));
			command.setRounds(dto.getRounds());
			command.setSessionId(sessionId);

			AiVibeCheckResult result = aiMatchGateway.start(command);
			updateMatchResult(matchRecord, result);
			return toVo(result);
		}
		catch (Exception e) {
			log.error("AI 引擎调用失败，更新记录状态为异常", e);
			matchRecord.setStatus("3");
			matchRecord.setMatchSummary("系统异常：" + e.getMessage());
			matchRecord.setUpdateTime(LocalDateTime.now());
			bizMatchRecordMapper.updateById(matchRecord);
			throw e;
		}
	}

	private void updateMatchResult(BizMatchRecordEntity record, AiVibeCheckResult result) {
		Integer score = result.getScore();
		record.setMatchScore(score);
		record.setMatchSummary(result.getSummary());
		record.setStatus(score >= PASS_SCORE ? "1" : "2");
		record.setUpdateTime(LocalDateTime.now());
		bizMatchRecordMapper.updateById(record);

		if (result.getDialogue() != null) {
			for (AiDialogueMessage msg : result.getDialogue()) {
				BizChatHistoryEntity history = new BizChatHistoryEntity();
				history.setSessionId(record.getSessionId());
				history.setSenderId(record.getInitiatorId());
				history.setReceiverId(record.getTargetId());
				history.setRole(msg.getRole());
				history.setContent(msg.getContent());
				bizChatHistoryMapper.insert(history);
			}
		}

		log.info("AI 匹配完成，MatchID: {}, 最终得分: {}, 状态: {}", record.getMatchId(), score, record.getStatus());
	}

	private AiVibeProfile buildUserProfile(UserInfo user) {
		AiVibeProfile profile = new AiVibeProfile();
		BizEchoProfileEntity echo = bizEchoProfileMapper.selectById(user.getUserId());
		if (echo != null) {
			profile.setName(StrUtil.isNotBlank(echo.getNickname()) ? echo.getNickname() : user.getUsername());
			profile.setMbti(StrUtil.isNotBlank(echo.getTags()) ? echo.getTags() : "未知人格");
			profile.setInterests(echo.getTags());
			profile.setStyle(echo.getVoiceTone());
		}
		else {
			profile.setName(user.getUsername());
			profile.setMbti("ISFJ");
			profile.setInterests("神秘用户");
			profile.setStyle("温和");
		}
		return profile;
	}

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
		}
		catch (Exception e) {
			log.error("远程获取用户失败 userId={}", userId, e);
		}
		return null;
	}

	private AiVibeCheckResultVO toVo(AiVibeCheckResult result) {
		AiVibeCheckResultVO vo = new AiVibeCheckResultVO();
		vo.setStatus("success");
		vo.setScore(result.getScore());
		vo.setSummary(result.getSummary());
		if (result.getDialogue() != null) {
			vo.setDialogue(result.getDialogue().stream().map(item -> {
				Map<String, Object> dialog = new HashMap<>();
				dialog.put("role", item.getRole());
				dialog.put("content", item.getContent());
				return dialog;
			}).collect(Collectors.toList()));
		}
		return vo;
	}
}
