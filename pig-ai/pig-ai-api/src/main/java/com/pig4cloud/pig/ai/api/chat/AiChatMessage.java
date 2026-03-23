package com.pig4cloud.pig.ai.api.chat;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "AI 对话消息")
public class AiChatMessage {

	@Schema(description = "角色: user/assistant")
	private String role;

	@Schema(description = "消息内容")
	private String content;
}
