package com.pig4cloud.pig.ai.api.match;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "同频测试命令")
public class AiVibeCheckCommand {

	@Schema(description = "发起方画像")
	private AiVibeProfile userA;

	@Schema(description = "目标方画像")
	private AiVibeProfile userB;

	@Schema(description = "对话轮数")
	private Integer rounds = 3;

	@Schema(description = "会话 ID")
	private String sessionId;
}
