package com.pig4cloud.pig.biz.api.dto.ai;

import lombok.Data;

@Data
public class KnowledgeSearchRequest {

	private String query;

	private Long echoId;

	private Integer limit;

}
