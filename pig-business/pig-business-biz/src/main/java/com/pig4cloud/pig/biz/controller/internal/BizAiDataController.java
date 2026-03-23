package com.pig4cloud.pig.biz.controller.internal;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
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
import com.pig4cloud.pig.biz.entity.BizChatHistoryEntity;
import com.pig4cloud.pig.biz.entity.BizEchoProfileEntity;
import com.pig4cloud.pig.biz.entity.BizKnowledgeBaseEntity;
import com.pig4cloud.pig.biz.entity.BizMatchRecordEntity;
import com.pig4cloud.pig.biz.entity.BizSocialMomentEntity;
import com.pig4cloud.pig.biz.service.BizChatHistoryService;
import com.pig4cloud.pig.biz.service.BizEchoProfileService;
import com.pig4cloud.pig.biz.service.BizKnowledgeBaseService;
import com.pig4cloud.pig.biz.service.BizMatchRecordService;
import com.pig4cloud.pig.biz.service.BizSocialMomentService;
import com.pig4cloud.pig.common.core.util.R;
import com.pig4cloud.pig.common.security.annotation.Inner;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Inner
@RestController
@RequiredArgsConstructor
@RequestMapping("/internal/ai/data")
public class BizAiDataController {

	private final BizEchoProfileService bizEchoProfileService;

	private final BizChatHistoryService bizChatHistoryService;

	private final BizKnowledgeBaseService bizKnowledgeBaseService;

	private final BizMatchRecordService bizMatchRecordService;

	private final BizSocialMomentService bizSocialMomentService;

	@GetMapping("/echo-profile/{echoId}")
	public R<EchoProfileDTO> getEchoProfile(@PathVariable Long echoId) {
		BizEchoProfileEntity entity = bizEchoProfileService.getById(echoId);
		if (entity == null) {
			return R.ok(null);
		}
		EchoProfileDTO dto = new EchoProfileDTO();
		BeanUtils.copyProperties(entity, dto);
		return R.ok(dto);
	}

	@PostMapping("/echo-profile/upsert")
	public R<EchoProfileDTO> upsertEchoProfile(@RequestBody UpsertEchoProfileRequest request) {
		BizEchoProfileEntity entity = bizEchoProfileService.getById(request.getEchoId());
		if (entity == null) {
			entity = new BizEchoProfileEntity();
			entity.setEchoId(request.getEchoId());
		}
		entity.setNickname(request.getNickname());
		entity.setAvatar(request.getAvatar());
		entity.setPersonalityPrompt(request.getPersonalityPrompt());
		entity.setVoiceTone(request.getVoiceTone());
		entity.setIsPublic(request.getIsPublic());
		entity.setHeat(request.getHeat());
		entity.setTags(request.getTags());
		if (bizEchoProfileService.getById(request.getEchoId()) == null) {
			entity.setCreateBy(request.getUpdateBy());
			bizEchoProfileService.save(entity);
		}
		else {
			bizEchoProfileService.updateById(entity);
		}
		EchoProfileDTO dto = new EchoProfileDTO();
		BeanUtils.copyProperties(entity, dto);
		return R.ok(dto);
	}

	@GetMapping("/chat-history")
	public R<List<ChatHistoryMessageDTO>> listChatHistory(@RequestParam String sessionId,
			@RequestParam(defaultValue = "20") Integer limit) {
		int size = limit == null ? 20 : Math.max(1, Math.min(limit, 100));
		List<BizChatHistoryEntity> records = bizChatHistoryService.list(Wrappers.<BizChatHistoryEntity>lambdaQuery()
				.eq(BizChatHistoryEntity::getSessionId, sessionId)
				.orderByDesc(BizChatHistoryEntity::getCreateTime)
				.last("LIMIT " + size));
		Collections.reverse(records);
		return R.ok(records.stream().map(this::toChatMessageDto).collect(Collectors.toList()));
	}

	@GetMapping("/chat-history/page")
	public R<ChatHistoryPageDTO> pageChatHistory(@RequestParam Long userId, @RequestParam long current,
			@RequestParam long size, @RequestParam(required = false) String conversationId) {
		Page<BizChatHistoryEntity> page = bizChatHistoryService.page(new Page<>(current, size),
				Wrappers.<BizChatHistoryEntity>lambdaQuery()
						.and(wrapper -> wrapper.eq(BizChatHistoryEntity::getSenderId, userId)
								.or()
								.eq(BizChatHistoryEntity::getReceiverId, userId))
						.eq(StringUtils.hasText(conversationId), BizChatHistoryEntity::getSessionId, conversationId)
						.orderByDesc(BizChatHistoryEntity::getCreateTime));
		ChatHistoryPageDTO result = new ChatHistoryPageDTO();
		result.setCurrent(page.getCurrent());
		result.setSize(page.getSize());
		result.setTotal(page.getTotal());
		result.setPages(page.getPages());
		result.setRecords(page.getRecords().stream().map(this::toChatMessageDto).collect(Collectors.toList()));
		return R.ok(result);
	}

	@PostMapping("/chat-history/save")
	public R<Boolean> saveChatMessage(@RequestBody SaveChatMessageRequest request) {
		BizChatHistoryEntity entity = new BizChatHistoryEntity();
		entity.setSessionId(request.getSessionId());
		entity.setSenderId(request.getSenderId());
		entity.setReceiverId(request.getReceiverId());
		entity.setContent(request.getContent());
		entity.setRole(request.getRole());
		entity.setIsAiGenerated(request.getIsAiGenerated());
		entity.setCreateTime(LocalDateTime.now());
		return R.ok(bizChatHistoryService.save(entity));
	}

	@PostMapping("/knowledge/create")
	public R<KnowledgeRecordDTO> createKnowledgeRecord(@RequestBody CreateKnowledgeRecordRequest request) {
		BizKnowledgeBaseEntity entity = new BizKnowledgeBaseEntity();
		entity.setEchoId(request.getEchoId());
		entity.setFileName(request.getFileName());
		entity.setFileUrl(request.getFileUrl());
		entity.setFileType(request.getFileType());
		entity.setVectorStatus(request.getVectorStatus());
		entity.setCreateBy(request.getCreateBy());
		entity.setCreateTime(LocalDateTime.now());
		bizKnowledgeBaseService.save(entity);
		return R.ok(toKnowledgeRecordDto(entity));
	}

	@GetMapping("/knowledge/{id}")
	public R<KnowledgeRecordDTO> getKnowledgeRecord(@PathVariable Long id) {
		return R.ok(toKnowledgeRecordDto(bizKnowledgeBaseService.getById(id)));
	}

	@PutMapping("/knowledge/{id}/status")
	public R<Boolean> updateKnowledgeRecord(@PathVariable Long id, @RequestBody UpdateKnowledgeRecordRequest request) {
		BizKnowledgeBaseEntity entity = bizKnowledgeBaseService.getById(id);
		if (entity == null) {
			return R.failed("知识库记录不存在");
		}
		if (StringUtils.hasText(request.getVectorStatus())) {
			entity.setVectorStatus(request.getVectorStatus());
		}
		entity.setTokenCount(request.getTokenCount());
		entity.setSummary(request.getSummary());
		entity.setUpdateBy(request.getUpdateBy());
		entity.setUpdateTime(LocalDateTime.now());
		return R.ok(bizKnowledgeBaseService.updateById(entity));
	}

	@PostMapping("/knowledge/list-by-ids")
	public R<List<KnowledgeRecordDTO>> listKnowledgeRecords(@RequestBody List<Long> ids) {
		List<KnowledgeRecordDTO> records = bizKnowledgeBaseService.listByIds(ids)
				.stream()
				.sorted(Comparator.comparing(BizKnowledgeBaseEntity::getCreateTime, Comparator.nullsLast(Comparator.naturalOrder())))
				.map(this::toKnowledgeRecordDto)
				.collect(Collectors.toList());
		return R.ok(records);
	}

	@PostMapping("/knowledge/delete")
	public R<Boolean> deleteKnowledgeRecords(@RequestBody List<Long> ids) {
		return R.ok(bizKnowledgeBaseService.removeBatchByIds(ids));
	}

	@PostMapping("/knowledge/search")
	public R<List<KnowledgeRecordDTO>> searchKnowledge(@RequestBody KnowledgeSearchRequest request) {
		int size = request.getLimit() == null ? 5 : Math.max(1, Math.min(request.getLimit(), 20));
		List<BizKnowledgeBaseEntity> records = bizKnowledgeBaseService.list(Wrappers.<BizKnowledgeBaseEntity>lambdaQuery()
				.eq(request.getEchoId() != null, BizKnowledgeBaseEntity::getEchoId, request.getEchoId())
				.and(StringUtils.hasText(request.getQuery()), wrapper -> wrapper
						.like(BizKnowledgeBaseEntity::getFileName, request.getQuery())
						.or()
						.like(BizKnowledgeBaseEntity::getSummary, request.getQuery())
						.or()
						.like(BizKnowledgeBaseEntity::getFileType, request.getQuery()))
				.orderByDesc(BizKnowledgeBaseEntity::getUpdateTime)
				.orderByDesc(BizKnowledgeBaseEntity::getCreateTime)
				.last("LIMIT " + size));
		return R.ok(records.stream().map(this::toKnowledgeRecordDto).collect(Collectors.toList()));
	}

	@PostMapping("/match-record/create")
	public R<MatchRecordDTO> createMatchRecord(@RequestBody MatchRecordCreateRequest request) {
		BizMatchRecordEntity entity = new BizMatchRecordEntity();
		entity.setInitiatorId(request.getInitiatorId());
		entity.setTargetId(request.getTargetId());
		entity.setSessionId(StringUtils.hasText(request.getSessionId())
				? request.getSessionId()
				: UUID.randomUUID().toString().replace("-", ""));
		entity.setMatchScore(request.getMatchScore() == null ? 0 : request.getMatchScore());
		entity.setMatchSummary(request.getMatchSummary());
		entity.setStatus(StringUtils.hasText(request.getStatus()) ? request.getStatus() : "0");
		entity.setUnlockType(StringUtils.hasText(request.getUnlockType()) ? request.getUnlockType() : "0");
		entity.setCreateBy(request.getCreateBy());
		entity.setCreateTime(LocalDateTime.now());
		entity.setUpdateBy(request.getCreateBy());
		entity.setUpdateTime(LocalDateTime.now());
		bizMatchRecordService.save(entity);
		return R.ok(toMatchRecordDto(entity));
	}

	@PutMapping("/match-record/{matchId}")
	public R<Boolean> updateMatchRecord(@PathVariable Long matchId, @RequestBody UpdateMatchRecordRequest request) {
		BizMatchRecordEntity entity = bizMatchRecordService.getById(matchId);
		if (entity == null) {
			return R.failed("匹配记录不存在");
		}
		entity.setMatchScore(request.getMatchScore());
		entity.setMatchSummary(request.getMatchSummary());
		if (StringUtils.hasText(request.getStatus())) {
			entity.setStatus(request.getStatus());
		}
		entity.setUpdateBy(request.getUpdateBy());
		entity.setUpdateTime(LocalDateTime.now());
		return R.ok(bizMatchRecordService.updateById(entity));
	}

	@PostMapping("/social-moment/publish")
	public R<SocialMomentDTO> publishSocialMoment(@RequestBody SocialMomentPublishRequest request) {
		BizSocialMomentEntity entity = new BizSocialMomentEntity();
		entity.setUserId(request.getUserId());
		entity.setEchoId(request.getEchoId());
		entity.setContent(request.getContent());
		entity.setMediaUrls(request.getMediaUrls());
		entity.setType(StringUtils.hasText(request.getType()) ? request.getType() : "0");
		entity.setVisibility(StringUtils.hasText(request.getVisibility()) ? request.getVisibility() : "0");
		entity.setIsAnonymous(StringUtils.hasText(request.getIsAnonymous()) ? request.getIsAnonymous() : "0");
		entity.setBurnAfterReading(StringUtils.hasText(request.getBurnAfterReading()) ? request.getBurnAfterReading() : "0");
		entity.setExpiresTime(request.getExpiresTime());
		entity.setLocation(request.getLocation());
		entity.setCreateBy(request.getCreateBy());
		entity.setCreateTime(LocalDateTime.now());
		entity.setUpdateBy(request.getCreateBy());
		entity.setUpdateTime(LocalDateTime.now());
		entity.setLikeCount(0);
		entity.setCommentCount(0);
		bizSocialMomentService.save(entity);
		return R.ok(toSocialMomentDto(entity));
	}

	private ChatHistoryMessageDTO toChatMessageDto(BizChatHistoryEntity entity) {
		ChatHistoryMessageDTO dto = new ChatHistoryMessageDTO();
		BeanUtils.copyProperties(entity, dto);
		return dto;
	}

	private KnowledgeRecordDTO toKnowledgeRecordDto(BizKnowledgeBaseEntity entity) {
		if (entity == null) {
			return null;
		}
		KnowledgeRecordDTO dto = new KnowledgeRecordDTO();
		BeanUtils.copyProperties(entity, dto);
		return dto;
	}

	private MatchRecordDTO toMatchRecordDto(BizMatchRecordEntity entity) {
		MatchRecordDTO dto = new MatchRecordDTO();
		BeanUtils.copyProperties(entity, dto);
		return dto;
	}

	private SocialMomentDTO toSocialMomentDto(BizSocialMomentEntity entity) {
		SocialMomentDTO dto = new SocialMomentDTO();
		BeanUtils.copyProperties(entity, dto);
		return dto;
	}

}
