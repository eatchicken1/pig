package com.pig4cloud.pig.ai.app.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;

@Slf4j
@Configuration
public class AiInfrastructureConfiguration {

	@Bean
	public ChatClient aiChatClient(ChatModel chatModel) {
		return ChatClient.create(chatModel);
	}

	@Bean
	public TokenTextSplitter tokenTextSplitter(AiModuleProperties properties) {
		return new TokenTextSplitter(properties.getRagChunkSize(), 0, properties.getRagChunkSize(),
				properties.getRagMinChunkSizeChars(), true);
	}

	@Bean
	public SimpleVectorStore aiVectorStore(EmbeddingModel embeddingModel, AiModuleProperties properties) {
		SimpleVectorStore vectorStore = SimpleVectorStore.builder(embeddingModel).build();
		File storeFile = new File(properties.getRagStoreFile());
		if (storeFile.exists()) {
			try {
				vectorStore.load(storeFile);
				log.info("已加载本地向量索引: {}", storeFile.getAbsolutePath());
			}
			catch (Exception e) {
				log.warn("加载本地向量索引失败，将使用空索引继续运行, file={}", storeFile.getAbsolutePath(), e);
			}
		}
		return vectorStore;
	}
}
