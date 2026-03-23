package com.pig4cloud.pig.ai.api.knowledge;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Collections;
import java.util.List;

@Data
@Schema(description = "知识批量删除命令")
public class KnowledgeBatchDeleteCommand {

	@Schema(description = "批量删除项")
	private List<KnowledgeDeleteCommand> items = Collections.emptyList();
}
