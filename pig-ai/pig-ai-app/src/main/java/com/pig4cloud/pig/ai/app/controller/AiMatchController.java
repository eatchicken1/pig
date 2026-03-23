package com.pig4cloud.pig.ai.app.controller;

import com.pig4cloud.pig.ai.app.service.AiMatchApplicationService;
import com.pig4cloud.pig.biz.api.dto.AiVibeCheckDTO;
import com.pig4cloud.pig.biz.api.vo.AiVibeCheckResultVO;
import com.pig4cloud.pig.common.core.util.R;
import com.pig4cloud.pig.common.log.annotation.SysLog;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/match")
public class AiMatchController {

	private final AiMatchApplicationService aiMatchApplicationService;

	@Operation(summary = "发起同频测试")
	@SysLog("发起 AI 同频测试")
	@PostMapping("/vibe-check")
	public R<AiVibeCheckResultVO> startVibeCheck(@RequestBody AiVibeCheckDTO dto) {
		return R.ok(aiMatchApplicationService.startVibeCheck(dto));
	}
}
