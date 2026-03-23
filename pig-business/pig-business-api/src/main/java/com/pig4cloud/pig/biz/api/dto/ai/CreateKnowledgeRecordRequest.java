package com.pig4cloud.pig.biz.api.dto.ai;

import lombok.Data;

@Data
public class CreateKnowledgeRecordRequest {

	private Long echoId;

	private String fileName;

	private String fileUrl;

	private String fileType;

	private String vectorStatus;

	private String createBy;

}
