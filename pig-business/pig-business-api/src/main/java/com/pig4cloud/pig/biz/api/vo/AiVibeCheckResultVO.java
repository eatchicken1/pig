package com.pig4cloud.pig.biz.api.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.util.List;
import java.util.Map;

/**
 * AI 同频测试结果 VO
 */
@Data
@Schema(description = "AI 同频测试结果")
public class AiVibeCheckResultVO {

	@Schema(description = "状态 (success/fail)")
	private String status;

	@Schema(description = "匹配分数 (0-100)")
	private Integer score;

	@Schema(description = "AI 总结评价")
	private String summary;

	@Schema(description = "完整对话记录")
	private List<Map<String, Object>> dialogue;
}