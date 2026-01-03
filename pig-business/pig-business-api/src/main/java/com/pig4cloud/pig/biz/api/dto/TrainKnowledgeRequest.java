package com.pig4cloud.pig.biz.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 文档级知识训练请求
 * 用于触发 AI 侧下载 OSS 文件并完成向量化
 */
@Data
public class TrainKnowledgeRequest {

	/**
	 * 知识记录 ID（业务侧主键，用于回写状态）
	 */
	@NotNull
	private Long knowledge_id;

	/**
	 * 用户 ID（所属真实用户）
	 */
	@NotBlank
	private String user_id;

	/**
	 * 数字分身 ID（向量隔离的关键字段）
	 */
	@NotBlank
	private String echo_id;

	/**
	 * OSS 文件访问地址
	 */
	@NotBlank
	private String file_url;

	/**
	 * 文件类型（pdf / md / txt / docx）
	 */
	@NotBlank
	private String file_type;

	/**
	 * 原始文件名（用于溯源 & 调试）
	 */
	@NotBlank
	private String source_name;
}