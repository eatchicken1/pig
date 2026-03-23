package com.pig4cloud.pig.ai.app.service;

import com.pig4cloud.pig.ai.app.config.AiModuleProperties;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class AiPlatformOverviewService {

	private final AiModuleProperties properties;

	public AiPlatformOverviewService(AiModuleProperties properties) {
		this.properties = properties;
	}

	public Map<String, Object> overview() {
		Map<String, Object> result = new LinkedHashMap<>();
		result.put("defaultProvider", properties.getDefaultProvider());
		result.put("pythonRuntimeUrl", properties.getPythonRuntimeUrl());
		result.put("mcpEnabled", properties.isMcpEnabled());
		result.put("layers", new String[] {
			"business-core",
			"ai-application",
			"mcp-tools",
			"python-agent-runtime"
		});
		result.put("status", "scaffold-ready");
		return result;
	}
}
