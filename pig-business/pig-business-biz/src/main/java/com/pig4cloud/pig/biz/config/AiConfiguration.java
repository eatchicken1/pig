package com.pig4cloud.pig.biz.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;

@Configuration
public class AiConfiguration {

	@Bean
	public ChatClient chatClient(ChatClient.Builder builder, ChatMemory redisChatMemory) {
		return builder
				.defaultAdvisors(
						// 使用 Redis 存储上下文，防止内存泄露并支持集群
						new MessageChatMemoryAdvisor(redisChatMemory),
						// 默认日志审计，便于排查 AI 回答异常
						new SimpleLoggerAdvisor()
				)
				.build();
	}

	@Bean
	public RedisChatMemory redisChatMemory(RedisConnectionFactory connectionFactory) {
		// 建议设置过期时间（如 30 分钟），自动清理非活跃会话资源
		return new RedisChatMemory(connectionFactory);
	}
}
