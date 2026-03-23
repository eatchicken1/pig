package com.pig4cloud.pig.ai.agent.controller.internal;

import com.pig4cloud.pig.ai.agent.service.AiMatchApplicationService;
import com.pig4cloud.pig.ai.api.match.AiMatchStartRequest;
import com.pig4cloud.pig.ai.api.match.AiVibeCheckResult;
import com.pig4cloud.pig.common.core.util.R;
import com.pig4cloud.pig.common.security.annotation.Inner;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Inner
@RestController
@RequiredArgsConstructor
@RequestMapping("/internal/agent/match")
public class AiMatchInternalController {

	private final AiMatchApplicationService aiMatchApplicationService;

	@PostMapping("/vibe-check")
	public R<AiVibeCheckResult> startVibeCheck(@RequestBody AiMatchStartRequest request) {
		return R.ok(aiMatchApplicationService.startVibeCheck(request));
	}
}
