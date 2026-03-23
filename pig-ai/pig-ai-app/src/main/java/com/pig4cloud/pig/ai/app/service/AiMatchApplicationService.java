package com.pig4cloud.pig.ai.app.service;

import com.pig4cloud.pig.ai.api.feign.RemoteAiAgentClient;
import com.pig4cloud.pig.ai.api.match.AiMatchStartRequest;
import com.pig4cloud.pig.ai.api.match.AiVibeCheckResult;
import com.pig4cloud.pig.biz.api.dto.AiVibeCheckDTO;
import com.pig4cloud.pig.biz.api.vo.AiVibeCheckResultVO;
import com.pig4cloud.pig.common.core.constant.CommonConstants;
import com.pig4cloud.pig.common.core.util.R;
import com.pig4cloud.pig.common.security.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AiMatchApplicationService {

	private final RemoteAiAgentClient remoteAiAgentClient;

	public AiVibeCheckResultVO startVibeCheck(AiVibeCheckDTO dto) {
		AiMatchStartRequest request = new AiMatchStartRequest();
		request.setCurrentUserId(SecurityUtils.getUser().getId());
		request.setTargetUserId(dto.getTargetUserId());
		request.setRounds(dto.getRounds());
		AiVibeCheckResult result = unwrap(remoteAiAgentClient.startVibeCheck(request), "发起同频测试失败");
		AiVibeCheckResultVO vo = new AiVibeCheckResultVO();
		vo.setStatus("success");
		vo.setScore(result.getScore());
		vo.setSummary(result.getSummary());
		if (result.getDialogue() != null) {
			vo.setDialogue(result.getDialogue().stream().map(item -> {
				HashMap<String, Object> map = new HashMap<>();
				map.put("role", item.getRole());
				map.put("content", item.getContent());
				return map;
			}).collect(Collectors.toList()));
		}
		return vo;
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
