package com.pig4cloud.pig.biz.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.io.Serializable;
import java.util.Map;

/**
 * AI 同频测试请求参数
 */
@Data
public class AiVibeCheckDTO implements Serializable {
	@Schema(description = "目标用户ID (你要匹配的对象)", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
	private Long targetUserId;
	@Schema(description = "对话轮数", defaultValue = "3")
	private Integer rounds = 3;
}