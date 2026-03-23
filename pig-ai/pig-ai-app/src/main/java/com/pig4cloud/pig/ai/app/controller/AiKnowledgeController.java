package com.pig4cloud.pig.ai.app.controller;

import com.pig4cloud.pig.ai.api.knowledge.KnowledgeSearchHitDTO;
import com.pig4cloud.pig.ai.app.service.AiKnowledgeBaseApplicationService;
import com.pig4cloud.pig.biz.api.dto.ai.KnowledgeSearchRequest;
import com.pig4cloud.pig.common.core.util.R;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/knowledge")
public class AiKnowledgeController {

	private final AiKnowledgeBaseApplicationService aiKnowledgeBaseApplicationService;

	@PostMapping("/search")
	@Operation(summary = "向量检索知识片段")
	public R<List<KnowledgeSearchHitDTO>> search(@RequestBody KnowledgeSearchRequest request) {
		return R.ok(aiKnowledgeBaseApplicationService.search(request));
	}
}
