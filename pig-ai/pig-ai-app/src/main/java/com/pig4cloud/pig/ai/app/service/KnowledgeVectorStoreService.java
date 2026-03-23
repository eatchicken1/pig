package com.pig4cloud.pig.ai.app.service;

import com.pig4cloud.pig.ai.api.knowledge.KnowledgeSearchHitDTO;
import com.pig4cloud.pig.ai.app.config.AiModuleProperties;
import com.pig4cloud.pig.biz.api.dto.ai.KnowledgeRecordDTO;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.ai.document.Document;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.filter.FilterExpressionBuilder;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class KnowledgeVectorStoreService {

	private static final int SNIPPET_LIMIT = 240;

	private final SimpleVectorStore vectorStore;

	private final TokenTextSplitter tokenTextSplitter;

	private final AiModuleProperties properties;

	private final Object storeMonitor = new Object();

	public KnowledgeTrainResult train(KnowledgeRecordDTO record) {
		String content = extractText(record.getFileUrl(), record.getFileType());
		if (!StringUtils.hasText(content)) {
			throw new RuntimeException("知识文档解析结果为空");
		}
		Document source = new Document(content, metadataForRecord(record));
		List<Document> chunks = tokenTextSplitter.split(source)
				.stream()
				.limit(properties.getRagMaxNumChunks())
				.collect(Collectors.toList());
		if (chunks.isEmpty()) {
			throw new RuntimeException("知识文档切分结果为空");
		}
		synchronized (storeMonitor) {
			vectorStore.delete(new FilterExpressionBuilder().eq("knowledgeId", record.getId()).build());
			vectorStore.add(enrichChunks(record, chunks));
			persistStore();
		}
		KnowledgeTrainResult result = new KnowledgeTrainResult();
		result.setChunkCount(chunks.size());
		result.setTokenCount(Math.max(content.length() / 2, chunks.size()));
		result.setSummary(buildSummary(content));
		return result;
	}

	public List<KnowledgeSearchHitDTO> search(String query, Long echoId, Integer limit) {
		return similaritySearch(query, echoId, limit).stream().map(this::toSearchHit).collect(Collectors.toList());
	}

	public String buildContext(String query, Long echoId, Integer limit) {
		List<KnowledgeSearchHitDTO> hits = search(query, echoId, limit);
		if (hits.isEmpty()) {
			return "";
		}
		StringBuilder context = new StringBuilder();
		for (int i = 0; i < hits.size(); i++) {
			KnowledgeSearchHitDTO hit = hits.get(i);
			context.append("[").append(i + 1).append("] ")
					.append(hit.getFileName())
					.append(": ")
					.append(hit.getSnippet())
					.append("\n");
		}
		return context.toString().trim();
	}

	public void deleteKnowledge(Long knowledgeId) {
		synchronized (storeMonitor) {
			vectorStore.delete(new FilterExpressionBuilder().eq("knowledgeId", knowledgeId).build());
			persistStore();
		}
	}

	private List<Document> similaritySearch(String query, Long echoId, Integer limit) {
		SearchRequest.Builder builder = SearchRequest.builder()
				.query(query)
				.topK(limit == null ? properties.getRagTopK() : limit)
				.similarityThreshold(properties.getRagSimilarityThreshold());
		if (echoId != null) {
			builder.filterExpression(new FilterExpressionBuilder().eq("echoId", String.valueOf(echoId)).build());
		}
		return vectorStore.similaritySearch(builder.build());
	}

	private List<Document> enrichChunks(KnowledgeRecordDTO record, List<Document> chunks) {
		List<Document> result = new ArrayList<>(chunks.size());
		for (int i = 0; i < chunks.size(); i++) {
			Document chunk = chunks.get(i);
			Map<String, Object> metadata = new LinkedHashMap<>(chunk.getMetadata());
			metadata.put("knowledgeId", record.getId());
			metadata.put("echoId", String.valueOf(record.getEchoId()));
			metadata.put("fileName", record.getFileName());
			metadata.put("fileType", record.getFileType());
			metadata.put("chunkIndex", i);
			result.add(new Document(record.getId() + "-" + i, chunk.getText(), metadata));
		}
		return result;
	}

	private Map<String, Object> metadataForRecord(KnowledgeRecordDTO record) {
		Map<String, Object> metadata = new LinkedHashMap<>();
		metadata.put("knowledgeId", record.getId());
		metadata.put("echoId", String.valueOf(record.getEchoId()));
		metadata.put("fileName", record.getFileName());
		metadata.put("fileType", record.getFileType());
		return metadata;
	}

	private KnowledgeSearchHitDTO toSearchHit(Document document) {
		KnowledgeSearchHitDTO hit = new KnowledgeSearchHitDTO();
		Object knowledgeId = document.getMetadata().get("knowledgeId");
		if (knowledgeId instanceof Number number) {
			hit.setKnowledgeId(number.longValue());
		}
		else if (knowledgeId != null) {
			hit.setKnowledgeId(Long.valueOf(String.valueOf(knowledgeId)));
		}
		Object echoId = document.getMetadata().get("echoId");
		if (echoId != null) {
			hit.setEchoId(Long.valueOf(String.valueOf(echoId)));
		}
		hit.setFileName(String.valueOf(document.getMetadata().getOrDefault("fileName", "unknown")));
		hit.setFileType(String.valueOf(document.getMetadata().getOrDefault("fileType", "")));
		hit.setScore(document.getScore());
		hit.setSnippet(buildSnippet(document.getText()));
		return hit;
	}

	private String extractText(String fileUrl, String fileType) {
		try (InputStream inputStream = URI.create(fileUrl).toURL().openStream()) {
			byte[] bytes = StreamUtils.copyToByteArray(inputStream);
			String normalizedType = normalizeFileType(fileType, fileUrl);
			if ("pdf".equals(normalizedType)) {
				try (PDDocument document = Loader.loadPDF(bytes)) {
					return new PDFTextStripper().getText(document);
				}
			}
			if ("txt".equals(normalizedType) || "md".equals(normalizedType) || "markdown".equals(normalizedType)) {
				return new String(bytes, StandardCharsets.UTF_8);
			}
			throw new RuntimeException("暂不支持的知识文件类型: " + normalizedType);
		}
		catch (Exception e) {
			throw new RuntimeException("下载或解析知识文件失败", e);
		}
	}

	private String normalizeFileType(String fileType, String fileUrl) {
		if (StringUtils.hasText(fileType)) {
			return fileType.trim().toLowerCase();
		}
		int lastDot = fileUrl.lastIndexOf('.');
		return lastDot >= 0 ? fileUrl.substring(lastDot + 1).toLowerCase() : "txt";
	}

	private String buildSummary(String content) {
		return buildSnippet(content);
	}

	private String buildSnippet(String text) {
		if (!StringUtils.hasText(text)) {
			return "";
		}
		String normalized = text.replaceAll("\\s+", " ").trim();
		if (normalized.length() <= SNIPPET_LIMIT) {
			return normalized;
		}
		return normalized.substring(0, SNIPPET_LIMIT) + "...";
	}

	private void persistStore() {
		try {
			File storeFile = new File(properties.getRagStoreFile());
			File parent = storeFile.getParentFile();
			if (parent != null && !parent.exists()) {
				parent.mkdirs();
			}
			vectorStore.save(storeFile);
		}
		catch (Exception e) {
			throw new RuntimeException("持久化向量索引失败", e);
		}
	}

	@Data
	public static class KnowledgeTrainResult {

		private int chunkCount;

		private int tokenCount;

		private String summary;
	}
}
