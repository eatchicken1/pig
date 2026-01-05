package com.pig4cloud.pig.biz.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
public class ChatRequestDTO {
	@JsonProperty("user_id")
	private String userId;

	@JsonProperty("echo_id")
	private String echoId;

	private String query;

	@Schema(description = "分身昵称")
	@JsonProperty("echo_nickname")
	private String echoNickname;

	@Schema(description = "人设提示词")
	@JsonProperty("echo_prompt")
	private String echoPrompt; // 对应 personalityPrompt

	@Schema(description = "语言风格")
	@JsonProperty("echo_tone")
	private String echoTone;   // 对应 voiceTone

	@Schema(description = "擅长领域")
	@JsonProperty("echo_tags")
	private String echoTags;   // 对应 tags

	@Schema(description = "历史对话上下文")
	private List<Message> history; // 新增 history 字段

	/**
	 * 内部类 Message，定义单条对话消息结构
	 */
	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	public static class Message {
		@Schema(description = "角色: user/assistant")
		private String role;

		@Schema(description = "消息内容")
		private String content;
	}
}