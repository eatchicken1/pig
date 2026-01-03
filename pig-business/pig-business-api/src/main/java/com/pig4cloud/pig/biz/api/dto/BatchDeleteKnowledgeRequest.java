package com.pig4cloud.pig.biz.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.List;

@Data
public class BatchDeleteKnowledgeRequest {
	@JsonProperty("items")
	private List<DeleteKnowledgeRequest> items;
}