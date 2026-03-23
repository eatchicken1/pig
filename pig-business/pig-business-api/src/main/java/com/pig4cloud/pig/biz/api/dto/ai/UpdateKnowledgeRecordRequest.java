package com.pig4cloud.pig.biz.api.dto.ai;

import lombok.Data;

@Data
public class UpdateKnowledgeRecordRequest {

	private String vectorStatus;

	private Integer tokenCount;

	private String summary;

	private String updateBy;

}
