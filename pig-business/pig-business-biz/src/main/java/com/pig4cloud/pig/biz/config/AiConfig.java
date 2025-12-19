package com.pig4cloud.pig.biz.config;

import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.redis.RedisVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.JedisPooled;

@Configuration
public class AiConfig {

	/**
	 * 在 Spring AI 1.0.0-M6 中，推荐使用 RedisVectorStore.builder()
	 * 取代之前的 RedisVectorStoreConfig 和构造函数。
	 */
	@Bean
	public VectorStore vectorStore(EmbeddingModel embeddingModel) {
		// 1. 创建 Jedis 连接池 (Spring AI Redis 默认使用 Jedis)
		// 注意：如果你在 Nacos/application.yml 中配置了 redis，
		// 这里应确保地址一致。
		JedisPooled jedisPooled = new JedisPooled("localhost", 6379);

		// 2. 使用 Builder 模式构建 VectorStore
		return RedisVectorStore.builder(jedisPooled, embeddingModel)
				.indexName("frequency_idx") // 对应 application.yml 中的 index
				.prefix("freq:emb:")       // 对应 application.yml 中的 prefix
				.initializeSchema(true)    // 对应 application.yml 中的 initialize-schema
				.build();
	}
}