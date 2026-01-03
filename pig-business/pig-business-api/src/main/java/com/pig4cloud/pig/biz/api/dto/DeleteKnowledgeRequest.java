package com.pig4cloud.pig.biz.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 删除知识库向量请求 DTO
 * 对应 Python 端 KnowledgeDeleteRequest
 */
@Data
@Schema(description = "删除知识库向量请求")
public class DeleteKnowledgeRequest {

	@Schema(description = "知识库ID")
	@JsonProperty("knowledge_id")
	private Long knowledgeId;

	@Schema(description = "数字分身ID")
	@JsonProperty("echo_id")
	private String echoId;

	@Schema(description = "用户ID")
	@JsonProperty("user_id")
	private String userId;
}