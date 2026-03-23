package com.pig4cloud.pig.ai.app.controller;

import com.pig4cloud.pig.ai.app.service.AiEchoProfileApplicationService;
import com.pig4cloud.pig.biz.api.dto.ai.EchoProfileDTO;
import com.pig4cloud.pig.common.core.util.R;
import com.pig4cloud.pig.common.log.annotation.SysLog;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/profile")
public class AiEchoProfileController {

	private final AiEchoProfileApplicationService aiEchoProfileApplicationService;

	@GetMapping("/my")
	@Operation(summary = "获取我的分身配置")
	public R<EchoProfileDTO> getMyProfile() {
		return R.ok(aiEchoProfileApplicationService.getMyProfile());
	}

	@PutMapping("/my")
	@SysLog("更新 AI 分身配置")
	@Operation(summary = "保存或更新我的分身配置")
	public R<EchoProfileDTO> updateMyProfile(@RequestBody EchoProfileDTO profile) {
		return R.ok(aiEchoProfileApplicationService.updateMyProfile(profile));
	}

	@GetMapping("/{id}")
	@Operation(summary = "查看公开分身资料")
	public R<EchoProfileDTO> getProfile(@PathVariable("id") Long id) {
		return R.ok(aiEchoProfileApplicationService.getPublicProfile(id));
	}
}
