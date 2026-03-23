package com.pig4cloud.pig.biz.api.dto.ai;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MatchRecordDTO {

	private Long matchId;

	private Long initiatorId;

	private Long targetId;

	private String sessionId;

	private Integer matchScore;

	private String matchSummary;

	private String status;

	private String unlockType;

	private LocalDateTime createTime;

	private LocalDateTime updateTime;

}
