package com.pig4cloud.pig.ai.app.service;

import com.pig4cloud.pig.biz.api.dto.ai.EchoProfileDTO;
import com.pig4cloud.pig.biz.api.dto.ai.UpsertEchoProfileRequest;
import com.pig4cloud.pig.biz.api.feign.RemoteBizAiDataClient;
import com.pig4cloud.pig.common.core.constant.CommonConstants;
import com.pig4cloud.pig.common.core.util.R;
import com.pig4cloud.pig.common.security.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class AiEchoProfileApplicationService {

	private final RemoteBizAiDataClient remoteBizAiDataClient;

	public EchoProfileDTO getMyProfile() {
		Long currentUserId = SecurityUtils.getUser().getId();
		EchoProfileDTO profile = unwrap(remoteBizAiDataClient.getEchoProfile(currentUserId), "读取我的分身配置失败");
		if (profile != null) {
			return profile;
		}
		EchoProfileDTO initialized = new EchoProfileDTO();
		initialized.setEchoId(currentUserId);
		initialized.setNickname(SecurityUtils.getUser().getUsername());
		initialized.setIsPublic("1");
		initialized.setHeat(0);
		initialized.setVoiceTone("正常");
		return initialized;
	}

	public EchoProfileDTO updateMyProfile(EchoProfileDTO profile) {
		UpsertEchoProfileRequest request = new UpsertEchoProfileRequest();
		request.setEchoId(SecurityUtils.getUser().getId());
		request.setNickname(profile.getNickname());
		request.setAvatar(profile.getAvatar());
		request.setPersonalityPrompt(profile.getPersonalityPrompt());
		request.setVoiceTone(profile.getVoiceTone());
		request.setIsPublic(profile.getIsPublic());
		request.setHeat(profile.getHeat());
		request.setTags(profile.getTags());
		request.setUpdateBy(SecurityUtils.getUser().getUsername());
		return unwrap(remoteBizAiDataClient.upsertEchoProfile(request), "保存分身配置失败");
	}

	public EchoProfileDTO getPublicProfile(Long echoId) {
		EchoProfileDTO profile = unwrap(remoteBizAiDataClient.getEchoProfile(echoId), "读取分身资料失败");
		if (profile == null) {
			throw new RuntimeException("该用户暂未开通 AI 分身");
		}
		Long currentUserId = SecurityUtils.getUser().getId();
		if (!echoId.equals(currentUserId) && "0".equals(profile.getIsPublic())) {
			throw new RuntimeException("该用户已设置隐私保护");
		}
		if (!echoId.equals(currentUserId) && StringUtils.hasText(profile.getPersonalityPrompt())) {
			profile.setPersonalityPrompt(null);
		}
		return profile;
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
