package com.pig4cloud.pig.biz.controller;

import com.pig4cloud.pig.biz.api.dto.AiVibeCheckDTO;
import com.pig4cloud.pig.biz.api.vo.AiVibeCheckResultVO;
import com.pig4cloud.pig.biz.service.impl.FrequencyAiServiceImpl;
import com.pig4cloud.pig.common.core.util.R;
import com.pig4cloud.pig.common.log.annotation.SysLog;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/ai-experiment")
@Tag(name = "AI 实验室接口", description = "用于测试 Python 引擎")
@SecurityRequirement(name = HttpHeaders.AUTHORIZATION)
public class AiExperimentController {

	private final FrequencyAiServiceImpl frequencyAiService;

	@Operation(summary = "发起同频测试 (Vibe Check)", description = "触发两个 AI Agent 进行多轮对话并评分")
	@SysLog("发起 AI 同频测试")
	@PostMapping("/vibe-check")
	public R<AiVibeCheckResultVO> startVibeCheck(@RequestBody AiVibeCheckDTO dto) {
		return R.ok(frequencyAiService.startVibeCheck(dto));
	}
}