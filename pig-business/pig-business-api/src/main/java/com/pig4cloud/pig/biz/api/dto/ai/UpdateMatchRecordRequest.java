package com.pig4cloud.pig.biz.api.dto.ai;

import lombok.Data;

@Data
public class UpdateMatchRecordRequest {

	private Integer matchScore;

	private String matchSummary;

	private String status;

	private String updateBy;
}
