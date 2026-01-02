package com.pig4cloud.pig.biz.api.feign;

import com.pig4cloud.pig.common.core.util.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

/**
 * AI 引擎远程调用客户端
 * url 从配置文件 frequency.ai.url 读取，默认 http://localhost:8000
 */
@FeignClient(contextId = "frequencyAiClient", name = "frequency-ai-engine", url = "${frequency.ai.url:http://localhost:8000}")
public interface FrequencyAiClient {

	/**
	 * 调用 AI 侧的 /knowledge/add 接口
	 */
	@PostMapping("/knowledge/add")
	R<Map<String, Object>> ingestKnowledge(@RequestBody Map<String, Object> request);
}