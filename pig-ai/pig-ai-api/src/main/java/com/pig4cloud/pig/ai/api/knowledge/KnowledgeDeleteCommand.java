package com.pig4cloud.pig.ai.api.knowledge;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "知识删除命令")
public class KnowledgeDeleteCommand {

	@Schema(description = "知识记录 ID")
	private Long knowledgeId;

	@Schema(description = "分身 ID")
	private String echoId;

	@Schema(description = "用户 ID")
	private String userId;
}
