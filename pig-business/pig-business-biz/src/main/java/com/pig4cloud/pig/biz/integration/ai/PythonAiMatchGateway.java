package com.pig4cloud.pig.biz.integration.ai;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONUtil;
import com.pig4cloud.pig.ai.api.match.AiMatchGateway;
import com.pig4cloud.pig.ai.api.match.AiVibeCheckCommand;
import com.pig4cloud.pig.ai.api.match.AiVibeCheckResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PythonAiMatchGateway implements AiMatchGateway {

	@Value("${frequency.ai.url:http://localhost:8000}")
	private String aiEngineUrl;

	@Override
	public AiVibeCheckResult start(AiVibeCheckCommand command) {
		try {
			String endpoint = aiEngineUrl + "/api/v1/ai/vibe-check";
			HttpResponse response = HttpRequest.post(endpoint)
					.body(JSONUtil.toJsonStr(command))
					.timeout(60000)
					.execute();

			if (!response.isOk()) {
				throw new RuntimeException("AI 引擎返回错误状态码: " + response.getStatus());
			}

			return JSONUtil.toBean(response.body(), AiVibeCheckResult.class);
		}
		catch (Exception e) {
			log.error("调用 Python AI 同频测试失败", e);
			throw new RuntimeException("AI 服务暂时不可用，请稍后重试");
		}
	}
}
