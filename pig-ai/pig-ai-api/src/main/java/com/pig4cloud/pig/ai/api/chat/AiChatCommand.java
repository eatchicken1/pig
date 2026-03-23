package com.pig4cloud.pig.ai.api.chat;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Collections;
import java.util.List;

@Data
@Schema(description = "AI 对话命令")
public class AiChatCommand {

	@Schema(description = "用户 ID")
	private String userId;

	@Schema(description = "分身 ID")
	private Long echoId;

	@Schema(description = "当前问题")
	private String query;

	@Schema(description = "分身昵称")
	private String echoNickname;

	@Schema(description = "分身提示词")
	private String echoPrompt;

	@Schema(description = "语言风格")
	private String echoTone;

	@Schema(description = "擅长领域")
	private String echoTags;

	@Schema(description = "历史对话")
	private List<AiChatMessage> history = Collections.emptyList();
}
