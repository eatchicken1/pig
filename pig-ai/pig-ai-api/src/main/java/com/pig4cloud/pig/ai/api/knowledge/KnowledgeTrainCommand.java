package com.pig4cloud.pig.ai.api.knowledge;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "知识训练命令")
public class KnowledgeTrainCommand {

	@Schema(description = "知识记录 ID")
	private Long knowledgeId;

	@Schema(description = "用户 ID")
	private String userId;

	@Schema(description = "分身 ID")
	private String echoId;

	@Schema(description = "文件地址")
	private String fileUrl;

	@Schema(description = "文件类型")
	private String fileType;

	@Schema(description = "原始文件名")
	private String sourceName;
}
