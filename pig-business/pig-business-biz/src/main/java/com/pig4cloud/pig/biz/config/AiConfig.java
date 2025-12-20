package com.pig4cloud.pig.biz.config;

import org.springframework.ai.document.MetadataMode;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.OpenAiEmbeddingModel;
import org.springframework.ai.openai.OpenAiEmbeddingOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.redis.RedisVectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import redis.clients.jedis.JedisPooled;

@Configuration
public class AiConfig {

	@Value("${spring.ai.openai.api-key}")
	private String apiKey;

	@Value("${spring.ai.openai.base-url}")
	private String baseUrl;

	/**
	 * 1. 基础设施：构建 OpenAI API 客户端
	 * 架构意图：使用 builder() 并显式指定 baseUrl，彻底绕过 PIG 框架中 LoadBalancer 对外部域名的拦截。
	 * 解决痛点：避免 "Service Instance cannot be null" 和 "404" 错误。
	 */
	@Bean
	public OpenAiApi openAiApi() {
		return OpenAiApi.builder()
				.baseUrl(baseUrl)
				.apiKey(apiKey)
				.build();
	}

	/**
	 * 2. 核心能力：构建聊天模型 (ChatModel)
	 * 架构意图：集中管理模型参数 (qwen-plus)，避免分散在各处代码中。
	 */
	@Bean
	@Primary
	public OpenAiChatModel openAiChatModel(OpenAiApi openAiApi) {
		return OpenAiChatModel.builder()
				.openAiApi(openAiApi)
				.defaultOptions(OpenAiChatOptions.builder()
						.model("qwen-plus") // 阿里云通义千问推荐模型
						.temperature(0.7)   // 更有创造力一点
						.build())
				.build();
	}

	/**
	 * 3. 核心能力：构建向量模型 (EmbeddingModel)
	 * 架构意图：DashScope 不支持 OpenAI 默认的 ada-002，必须显式指定 text-embedding-v1。
	 * 注意：M6 版本中 OpenAiEmbeddingModel 暂无 Builder，需用构造函数注入 Options。
	 */
	@Bean
	@Primary
	public EmbeddingModel embeddingModel(OpenAiApi openAiApi) {
		OpenAiEmbeddingOptions options = OpenAiEmbeddingOptions.builder()
				.model("text-embedding-v1") // 必须指定，否则默认走 ada-002 会报错
				.build();

		return new OpenAiEmbeddingModel(openAiApi, MetadataMode.EMBED, options);
	}

	/**
	 * 4. 数据存储：配置 Redis 向量库
	 * 架构意图：连接 Redis Stack，启用 schema 自动初始化。
	 */
	@Bean
	public VectorStore vectorStore(EmbeddingModel embeddingModel) {
		// 确保你的 Redis 是 Docker 启动的 Redis Stack 版本 (带 RediSearch)
		JedisPooled jedisPooled = new JedisPooled("localhost", 6379);

		return RedisVectorStore.builder(jedisPooled, embeddingModel)
				.indexName("frequency_idx")
				.prefix("freq:emb:")
				.initializeSchema(true)
				.build();
	}
}