package com.pig4cloud.pig.ai.api.match;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "启动匹配流程请求")
public class AiMatchStartRequest {

	@Schema(description = "当前登录用户 ID")
	private Long currentUserId;

	@Schema(description = "目标用户 ID")
	private Long targetUserId;

	@Schema(description = "对话轮数")
	private Integer rounds = 3;
}
