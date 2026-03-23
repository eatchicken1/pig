package com.pig4cloud.pig.biz.api.dto.ai;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ChatHistoryMessageDTO {

	private String sessionId;

	private Long senderId;

	private Long receiverId;

	private String content;

	private String role;

	private String isAiGenerated;

	private LocalDateTime createTime;

}
