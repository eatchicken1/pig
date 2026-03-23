package com.pig4cloud.pig.biz.api.dto.ai;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SocialMomentPublishRequest {

	private Long userId;

	private Long echoId;

	private String content;

	private String mediaUrls;

	private String type;

	private String visibility;

	private String isAnonymous;

	private String burnAfterReading;

	private LocalDateTime expiresTime;

	private String location;

	private String createBy;

}
