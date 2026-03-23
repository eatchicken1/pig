package com.pig4cloud.pig.biz.api.dto.ai;

import lombok.Data;

@Data
public class MatchRecordCreateRequest {

	private Long initiatorId;

	private Long targetId;

	private String sessionId;

	private Integer matchScore;

	private String matchSummary;

	private String status;

	private String unlockType;

	private String createBy;

}
