package com.pig4cloud.pig.ai.mcp.tools;

import com.pig4cloud.pig.ai.api.feign.RemoteAiAppClient;
import com.pig4cloud.pig.ai.api.knowledge.KnowledgeSearchHitDTO;
import com.pig4cloud.pig.biz.api.dto.ai.EchoProfileDTO;
import com.pig4cloud.pig.biz.api.dto.ai.KnowledgeSearchRequest;
import com.pig4cloud.pig.biz.api.dto.ai.MatchRecordCreateRequest;
import com.pig4cloud.pig.biz.api.dto.ai.MatchRecordDTO;
import com.pig4cloud.pig.biz.api.dto.ai.SocialMomentDTO;
import com.pig4cloud.pig.biz.api.dto.ai.SocialMomentPublishRequest;
import com.pig4cloud.pig.biz.api.feign.RemoteBizAiDataClient;
import com.pig4cloud.pig.common.core.constant.CommonConstants;
import com.pig4cloud.pig.common.core.util.R;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CampusAgentTools {

	private final RemoteBizAiDataClient remoteBizAiDataClient;

	private final RemoteAiAppClient remoteAiAppClient;

	@Tool(name = "echo_profile.read", description = "读取指定 Echo 的真实分身资料")
	public EchoProfileDTO echoProfileRead(Long echoId) {
		if (echoId == null) {
			throw new IllegalArgumentException("echoId 不能为空");
		}
		return unwrap(remoteBizAiDataClient.getEchoProfile(echoId), "读取分身资料失败");
	}

	@Tool(name = "knowledge_base.search", description = "按关键词和 echoId 进行真实向量检索，返回命中的知识片段")
	public List<KnowledgeSearchHitDTO> knowledgeBaseSearch(String query, Long echoId, Integer limit) {
		KnowledgeSearchRequest request = new KnowledgeSearchRequest();
		request.setQuery(query);
		request.setEchoId(echoId);
		request.setLimit(limit);
		return unwrap(remoteAiAppClient.searchKnowledge(request), "搜索知识库失败");
	}

	@Tool(name = "match_record.create", description = "创建一条真实匹配记录")
	public MatchRecordDTO matchRecordCreate(Long initiatorId, Long targetId, String matchSummary, Integer matchScore,
			String status, String unlockType) {
		if (initiatorId == null || targetId == null) {
			throw new IllegalArgumentException("initiatorId 和 targetId 不能为空");
		}
		MatchRecordCreateRequest request = new MatchRecordCreateRequest();
		request.setInitiatorId(initiatorId);
		request.setTargetId(targetId);
		request.setMatchSummary(matchSummary);
		request.setMatchScore(matchScore);
		request.setStatus(StringUtils.hasText(status) ? status : "0");
		request.setUnlockType(StringUtils.hasText(unlockType) ? unlockType : "0");
		request.setCreateBy("mcp");
		return unwrap(remoteBizAiDataClient.createMatchRecord(request), "创建匹配记录失败");
	}

	@Tool(name = "social_moment.publish", description = "发布一条真实社交动态")
	public SocialMomentDTO socialMomentPublish(Long userId, Long echoId, String content, String mediaUrls, String type,
			String visibility, String isAnonymous, String burnAfterReading, String location) {
		if (userId == null || !StringUtils.hasText(content)) {
			throw new IllegalArgumentException("userId 和 content 不能为空");
		}
		SocialMomentPublishRequest request = new SocialMomentPublishRequest();
		request.setUserId(userId);
		request.setEchoId(echoId);
		request.setContent(content);
		request.setMediaUrls(mediaUrls);
		request.setType(type);
		request.setVisibility(visibility);
		request.setIsAnonymous(isAnonymous);
		request.setBurnAfterReading(burnAfterReading);
		request.setLocation(location);
		request.setCreateBy("mcp");
		return unwrap(remoteBizAiDataClient.publishSocialMoment(request), "发布社交动态失败");
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
