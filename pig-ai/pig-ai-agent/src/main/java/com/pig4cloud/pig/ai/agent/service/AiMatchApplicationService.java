package com.pig4cloud.pig.ai.agent.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.pig4cloud.pig.admin.api.dto.UserDTO;
import com.pig4cloud.pig.admin.api.dto.UserInfo;
import com.pig4cloud.pig.admin.api.feign.RemoteUserService;
import com.pig4cloud.pig.ai.api.feign.RemoteAgentRuntimeClient;
import com.pig4cloud.pig.ai.api.match.AiDialogueMessage;
import com.pig4cloud.pig.ai.api.match.AiMatchStartRequest;
import com.pig4cloud.pig.ai.api.match.AiVibeCheckCommand;
import com.pig4cloud.pig.ai.api.match.AiVibeCheckResult;
import com.pig4cloud.pig.ai.api.match.AiVibeProfile;
import com.pig4cloud.pig.biz.api.dto.ai.EchoProfileDTO;
import com.pig4cloud.pig.biz.api.dto.ai.MatchRecordCreateRequest;
import com.pig4cloud.pig.biz.api.dto.ai.MatchRecordDTO;
import com.pig4cloud.pig.biz.api.dto.ai.SaveChatMessageRequest;
import com.pig4cloud.pig.biz.api.dto.ai.UpdateMatchRecordRequest;
import com.pig4cloud.pig.biz.api.feign.RemoteBizAiDataClient;
import com.pig4cloud.pig.common.core.constant.CommonConstants;
import com.pig4cloud.pig.common.core.util.R;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Objects;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AiMatchApplicationService {

	private static final int PASS_SCORE = 75;

	private final RemoteUserService remoteUserService;

	private final RemoteBizAiDataClient remoteBizAiDataClient;

	private final RemoteAgentRuntimeClient remoteAgentRuntimeClient;

	public AiVibeCheckResult startVibeCheck(AiMatchStartRequest request) {
		UserInfo userA = getSysUserFromRemote(request.getCurrentUserId());
		UserInfo userB = getSysUserFromRemote(request.getTargetUserId());
		if (userA == null || userB == null) {
			throw new RuntimeException("无法获取用户信息");
		}

		String sessionId = UUID.randomUUID().toString().replace("-", "");
		MatchRecordCreateRequest createRequest = new MatchRecordCreateRequest();
		createRequest.setInitiatorId(userA.getUserId());
		createRequest.setTargetId(userB.getUserId());
		createRequest.setSessionId(sessionId);
		createRequest.setStatus("0");
		createRequest.setUnlockType("0");
		createRequest.setMatchScore(0);
		createRequest.setCreateBy("ai-agent");
		MatchRecordDTO matchRecord = unwrap(remoteBizAiDataClient.createMatchRecord(createRequest), "创建匹配记录失败");

		try {
			AiVibeCheckCommand command = new AiVibeCheckCommand();
			command.setSessionId(sessionId);
			command.setRounds(request.getRounds() == null ? 3 : request.getRounds());
			command.setUserA(buildUserProfile(userA));
			command.setUserB(buildUserProfile(userB));

			AiVibeCheckResult result = remoteAgentRuntimeClient.runResonanceMatch(command);
			updateMatchRecord(matchRecord, result);
			saveDialogue(matchRecord, result);
			return result;
		}
		catch (Exception e) {
			log.error("执行匹配工作流失败, matchId={}", matchRecord.getMatchId(), e);
			UpdateMatchRecordRequest updateRequest = new UpdateMatchRecordRequest();
			updateRequest.setStatus("3");
			updateRequest.setMatchSummary("系统异常：" + e.getMessage());
			updateRequest.setUpdateBy("ai-agent");
			unwrap(remoteBizAiDataClient.updateMatchRecord(matchRecord.getMatchId(), updateRequest), "更新匹配记录失败");
			throw new RuntimeException("AI 匹配流程执行失败");
		}
	}

	private void updateMatchRecord(MatchRecordDTO matchRecord, AiVibeCheckResult result) {
		UpdateMatchRecordRequest updateRequest = new UpdateMatchRecordRequest();
		Integer score = result.getScore() == null ? 0 : result.getScore();
		updateRequest.setMatchScore(score);
		updateRequest.setMatchSummary(result.getSummary());
		updateRequest.setStatus(score >= PASS_SCORE ? "1" : "2");
		updateRequest.setUpdateBy("ai-agent");
		unwrap(remoteBizAiDataClient.updateMatchRecord(matchRecord.getMatchId(), updateRequest), "更新匹配记录失败");
	}

	private void saveDialogue(MatchRecordDTO matchRecord, AiVibeCheckResult result) {
		if (result.getDialogue() == null || result.getDialogue().isEmpty()) {
			return;
		}
		for (AiDialogueMessage message : result.getDialogue()) {
			SaveChatMessageRequest request = new SaveChatMessageRequest();
			request.setSessionId(matchRecord.getSessionId());
			request.setContent(message.getContent());
			request.setRole(message.getRole());
			request.setIsAiGenerated("1");
			if ("B".equalsIgnoreCase(message.getRole())) {
				request.setSenderId(matchRecord.getTargetId());
				request.setReceiverId(matchRecord.getInitiatorId());
			}
			else {
				request.setSenderId(matchRecord.getInitiatorId());
				request.setReceiverId(matchRecord.getTargetId());
			}
			unwrap(remoteBizAiDataClient.saveChatMessage(request), "保存匹配对话失败");
		}
	}

	private AiVibeProfile buildUserProfile(UserInfo userInfo) {
		AiVibeProfile profile = new AiVibeProfile();
		EchoProfileDTO echoProfile = unwrap(remoteBizAiDataClient.getEchoProfile(userInfo.getUserId()), "读取分身资料失败");
		if (echoProfile != null) {
			profile.setName(StringUtils.hasText(echoProfile.getNickname()) ? echoProfile.getNickname() : userInfo.getUsername());
			profile.setMbti(StringUtils.hasText(echoProfile.getTags()) ? echoProfile.getTags() : "未知人格");
			profile.setInterests(echoProfile.getTags());
			profile.setStyle(StringUtils.hasText(echoProfile.getVoiceTone()) ? echoProfile.getVoiceTone() : "温和");
			return profile;
		}
		profile.setName(userInfo.getUsername());
		profile.setMbti("ISFJ");
		profile.setInterests("神秘用户");
		profile.setStyle("温和");
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

	private <T> T unwrap(R<T> response, String action) {
		if (response == null) {
			throw new RuntimeException(action + "，远程返回为空");
		}
		if (response.getCode() != CommonConstants.SUCCESS) {
			throw new RuntimeException(action + "，原因: " + response.getMsg());
		}
		return response.getData();
	}
}
