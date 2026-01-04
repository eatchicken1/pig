package com.pig4cloud.pig.biz.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
public class ChatRequestDTO {
	@JsonProperty("user_id")
	private String userId;

	@JsonProperty("echo_id")
	private String echoId;

	private String query;

	// 历史记录，前端传简单的 List<Map> 即可，例如 [{"role":"user","content":"hi"}]
	private List<Map<String, String>> history;
}