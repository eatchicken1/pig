package com.pig4cloud.pig.ai.api.feign;

import com.pig4cloud.pig.ai.api.match.AiVibeCheckCommand;
import com.pig4cloud.pig.ai.api.match.AiVibeCheckResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(contextId = "remoteAgentRuntimeClient", name = "frequency-agent-runtime",
		url = "${frequency.agent-runtime.url:${frequency.ai.python-runtime-url:http://localhost:8000}}")
public interface RemoteAgentRuntimeClient {

	@PostMapping("/workflow/resonance_match/run")
	AiVibeCheckResult runResonanceMatch(@RequestBody AiVibeCheckCommand request);
}
