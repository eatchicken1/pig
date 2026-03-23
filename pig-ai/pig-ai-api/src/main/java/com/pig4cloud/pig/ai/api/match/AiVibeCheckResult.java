package com.pig4cloud.pig.ai.api.match;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Collections;
import java.util.List;

@Data
@Schema(description = "同频测试结果")
public class AiVibeCheckResult {

	@Schema(description = "匹配分数")
	private Integer score;

	@Schema(description = "总结")
	private String summary;

	@Schema(description = "对话明细")
	private List<AiDialogueMessage> dialogue = Collections.emptyList();
}
