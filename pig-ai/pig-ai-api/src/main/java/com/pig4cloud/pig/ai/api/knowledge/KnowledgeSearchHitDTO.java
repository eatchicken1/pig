package com.pig4cloud.pig.ai.api.knowledge;

import lombok.Data;

@Data
public class KnowledgeSearchHitDTO {

	private Long knowledgeId;

	private Long echoId;

	private String fileName;

	private String fileType;

	private String snippet;

	private Double score;
}
