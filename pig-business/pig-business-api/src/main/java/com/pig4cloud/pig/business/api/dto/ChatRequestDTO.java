package com.pig4cloud.pig.business.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ChatRequestDTO {
	@NotNull(message = "目标Echo不能为空")
	private Long echoId;

	@NotBlank(message = "内容不能为空")
	private String content;

	private String sessionId; // 用于关联上下文连贯对话
}