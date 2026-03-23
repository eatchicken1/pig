package com.pig4cloud.pig.ai.api.match;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "同频测试参与者画像")
public class AiVibeProfile {

	@Schema(description = "昵称")
	private String name;

	@Schema(description = "MBTI 或人格标签")
	private String mbti;

	@Schema(description = "兴趣标签")
	private String interests;

	@Schema(description = "表达风格")
	private String style;
}
