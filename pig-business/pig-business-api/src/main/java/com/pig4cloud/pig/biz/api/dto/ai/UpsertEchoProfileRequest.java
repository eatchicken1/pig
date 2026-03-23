package com.pig4cloud.pig.biz.api.dto.ai;

import lombok.Data;

@Data
public class UpsertEchoProfileRequest {

	private Long echoId;

	private String nickname;

	private String avatar;

	private String personalityPrompt;

	private String voiceTone;

	private String isPublic;

	private Integer heat;

	private String tags;

	private String updateBy;
}
