package com.pig4cloud.pig.biz.api.feign;

import com.pig4cloud.pig.biz.api.dto.ai.ChatHistoryMessageDTO;
import com.pig4cloud.pig.biz.api.dto.ai.ChatHistoryPageDTO;
import com.pig4cloud.pig.biz.api.dto.ai.CreateKnowledgeRecordRequest;
import com.pig4cloud.pig.biz.api.dto.ai.EchoProfileDTO;
import com.pig4cloud.pig.biz.api.dto.ai.KnowledgeRecordDTO;
import com.pig4cloud.pig.biz.api.dto.ai.KnowledgeSearchRequest;
import com.pig4cloud.pig.biz.api.dto.ai.MatchRecordCreateRequest;
import com.pig4cloud.pig.biz.api.dto.ai.MatchRecordDTO;
import com.pig4cloud.pig.biz.api.dto.ai.SaveChatMessageRequest;
import com.pig4cloud.pig.biz.api.dto.ai.SocialMomentDTO;
import com.pig4cloud.pig.biz.api.dto.ai.SocialMomentPublishRequest;
import com.pig4cloud.pig.biz.api.dto.ai.UpdateMatchRecordRequest;
import com.pig4cloud.pig.biz.api.dto.ai.UpdateKnowledgeRecordRequest;
import com.pig4cloud.pig.biz.api.dto.ai.UpsertEchoProfileRequest;
import com.pig4cloud.pig.common.core.util.R;
import com.pig4cloud.pig.common.feign.annotation.NoToken;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(contextId = "remoteBizAiDataClient", name = "pig-business-biz", url = "${frequency.business.url:http://localhost:5008}")
public interface RemoteBizAiDataClient {

	@NoToken
	@GetMapping("/internal/ai/data/echo-profile/{echoId}")
	R<EchoProfileDTO> getEchoProfile(@PathVariable("echoId") Long echoId);

	@NoToken
	@PostMapping("/internal/ai/data/echo-profile/upsert")
	R<EchoProfileDTO> upsertEchoProfile(@RequestBody UpsertEchoProfileRequest request);

	@NoToken
	@GetMapping("/internal/ai/data/chat-history")
	R<List<ChatHistoryMessageDTO>> listChatHistory(@RequestParam("sessionId") String sessionId,
			@RequestParam(value = "limit", defaultValue = "20") Integer limit);

	@NoToken
	@GetMapping("/internal/ai/data/chat-history/page")
	R<ChatHistoryPageDTO> pageChatHistory(@RequestParam("userId") Long userId,
			@RequestParam("current") long current,
			@RequestParam("size") long size,
			@RequestParam(value = "conversationId", required = false) String conversationId);

	@NoToken
	@PostMapping("/internal/ai/data/chat-history/save")
	R<Boolean> saveChatMessage(@RequestBody SaveChatMessageRequest request);

	@NoToken
	@PostMapping("/internal/ai/data/knowledge/create")
	R<KnowledgeRecordDTO> createKnowledgeRecord(@RequestBody CreateKnowledgeRecordRequest request);

	@NoToken
	@GetMapping("/internal/ai/data/knowledge/{id}")
	R<KnowledgeRecordDTO> getKnowledgeRecord(@PathVariable("id") Long id);

	@NoToken
	@PutMapping("/internal/ai/data/knowledge/{id}/status")
	R<Boolean> updateKnowledgeRecord(@PathVariable("id") Long id, @RequestBody UpdateKnowledgeRecordRequest request);

	@NoToken
	@PostMapping("/internal/ai/data/knowledge/list-by-ids")
	R<List<KnowledgeRecordDTO>> listKnowledgeRecords(@RequestBody List<Long> ids);

	@NoToken
	@PostMapping("/internal/ai/data/knowledge/delete")
	R<Boolean> deleteKnowledgeRecords(@RequestBody List<Long> ids);

	@NoToken
	@PostMapping("/internal/ai/data/knowledge/search")
	R<List<KnowledgeRecordDTO>> searchKnowledge(@RequestBody KnowledgeSearchRequest request);

	@NoToken
	@PostMapping("/internal/ai/data/match-record/create")
	R<MatchRecordDTO> createMatchRecord(@RequestBody MatchRecordCreateRequest request);

	@NoToken
	@PutMapping("/internal/ai/data/match-record/{matchId}")
	R<Boolean> updateMatchRecord(@PathVariable("matchId") Long matchId,
			@RequestBody UpdateMatchRecordRequest request);

	@NoToken
	@PostMapping("/internal/ai/data/social-moment/publish")
	R<SocialMomentDTO> publishSocialMoment(@RequestBody SocialMomentPublishRequest request);

}
