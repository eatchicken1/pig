package com.pig4cloud.pig.ai.api.feign;

import com.pig4cloud.pig.ai.api.match.AiMatchStartRequest;
import com.pig4cloud.pig.ai.api.match.AiVibeCheckResult;
import com.pig4cloud.pig.common.core.util.R;
import com.pig4cloud.pig.common.feign.annotation.NoToken;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(contextId = "remoteAiAgentClient", name = "pig-ai-agent",
		url = "${frequency.ai.agent.url:http://localhost:5011}")
public interface RemoteAiAgentClient {

	@NoToken
	@PostMapping("/internal/agent/match/vibe-check")
	R<AiVibeCheckResult> startVibeCheck(@RequestBody AiMatchStartRequest request);
}
