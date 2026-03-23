package com.pig4cloud.pig.biz.api.dto.ai;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class KnowledgeRecordDTO {

	private Long id;

	private Long echoId;

	private String fileName;

	private String fileUrl;

	private String fileType;

	private String vectorStatus;

	private Integer tokenCount;

	private String summary;

	private String createBy;

	private LocalDateTime createTime;

	private String updateBy;

	private LocalDateTime updateTime;

	private String isTradable;

	private Integer price;

	private Integer salesCount;

	private String coverUrl;

}
