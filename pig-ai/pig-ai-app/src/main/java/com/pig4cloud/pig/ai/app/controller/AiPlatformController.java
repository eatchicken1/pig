package com.pig4cloud.pig.ai.app.controller;

import com.pig4cloud.pig.ai.app.service.AiPlatformOverviewService;
import com.pig4cloud.pig.common.core.util.R;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/internal/ai/platform")
@Tag(name = "AI 平台骨架接口")
public class AiPlatformController {

	private final AiPlatformOverviewService overviewService;

	public AiPlatformController(AiPlatformOverviewService overviewService) {
		this.overviewService = overviewService;
	}

	@GetMapping
	@Operation(summary = "查看 AI 模块骨架状态")
	public R<Map<String, Object>> overview() {
		return R.ok(overviewService.overview());
	}
}
