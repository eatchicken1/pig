package com.pig4cloud.pig.biz.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FrequencyVectorService {

	private final VectorStore vectorStore;

	/**
	 * 将切片后的文档存入 Redis 向量库
	 */
	public void addDocuments(List<Document> documents) {
		log.info("正在向量化存储 {} 条文档片段...", documents.size());
		vectorStore.add(documents);
		log.info("向量化存储完成。");
	}

	/**
	 * 带元数据过滤的相似度检索
	 * 修正点：使用 SearchRequest.builder() 构建请求
	 */
	public List<Document> similaritySearchWithFilter(String query, Long echoId) {
		// 使用 Builder 模式构建搜索请求
		SearchRequest searchRequest = SearchRequest.builder()
				.query(query) // 设置查询关键词
				.topK(5)     // 取最相关的5条
				.similarityThreshold(0.7) // 相似度阈值
				// 核心过滤逻辑：确保只搜索当前 Echo 的知识库
				.filterExpression("echo_id == " + echoId)
				.build();

		return vectorStore.similaritySearch(searchRequest);
	}
}