package com.pig4cloud.pig.ai.api.match;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "同频测试对话消息")
public class AiDialogueMessage {

	@Schema(description = "对话角色")
	private String role;

	@Schema(description = "对话内容")
	private String content;
}
