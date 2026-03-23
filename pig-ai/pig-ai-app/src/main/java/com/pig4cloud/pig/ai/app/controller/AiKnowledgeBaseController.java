package com.pig4cloud.pig.ai.app.controller;

import com.pig4cloud.pig.ai.app.service.AiKnowledgeBaseApplicationService;
import com.pig4cloud.pig.common.core.util.R;
import com.pig4cloud.pig.common.log.annotation.SysLog;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;

@RestController
@RequiredArgsConstructor
@RequestMapping("/bizKnowledgeBase")
public class AiKnowledgeBaseController {

	private final AiKnowledgeBaseApplicationService aiKnowledgeBaseApplicationService;

	@PostMapping("/train")
	@Operation(summary = "投喂文档进行 AI 训练")
	public R<Long> train(@RequestParam("file") MultipartFile file, @RequestParam("userId") Long userId) {
		if (file.isEmpty()) {
			return R.failed("文件不能为空");
		}
		return R.ok(aiKnowledgeBaseApplicationService.uploadAndTrain(file, userId));
	}

	@Operation(summary = "删除知识库", description = "删除知识库")
	@SysLog("删除知识库")
	@DeleteMapping("/removeByIds")
	@PreAuthorize("@pms.hasPermission('biz_knowledgebase_del')")
	public R<Boolean> removeByIds(@RequestBody Long[] ids) {
		return R.ok(aiKnowledgeBaseApplicationService.removeKnowledgeBatchByIds(Arrays.asList(ids)));
	}

}
