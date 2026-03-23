package com.pig4cloud.pig.ai.app.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "frequency.ai")
public class AiModuleProperties {

	private String defaultProvider = "dashscope";

	private String pythonRuntimeUrl = "http://localhost:8000";

	private boolean mcpEnabled = false;
}
