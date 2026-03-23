package com.pig4cloud.pig.ai.app.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "frequency.ai")
public class AiModuleProperties {

	private String defaultProvider = "dashscope";

	private String pythonRuntimeUrl = "http://localhost:8000";

	private boolean mcpEnabled = false;

	private String ragStoreFile = "data/ai/simple-vector-store.json";

	private int ragTopK = 6;

	private double ragSimilarityThreshold = 0.15D;

	private int ragHistoryLimit = 20;

	private int ragChunkSize = 500;

	private int ragMinChunkSizeChars = 100;

	private int ragMinChunkLengthToEmbed = 5;

	private int ragMaxNumChunks = 10000;
}
