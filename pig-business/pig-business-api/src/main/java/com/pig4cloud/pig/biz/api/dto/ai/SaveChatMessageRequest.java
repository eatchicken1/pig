package com.pig4cloud.pig.biz.api.dto.ai;

import lombok.Data;

@Data
public class SaveChatMessageRequest {

	private String sessionId;

	private Long senderId;

	private Long receiverId;

	private String content;

	private String role;

	private String isAiGenerated;

}
