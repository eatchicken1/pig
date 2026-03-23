package com.pig4cloud.pig.ai.api.feign;

import com.pig4cloud.pig.ai.api.knowledge.KnowledgeSearchHitDTO;
import com.pig4cloud.pig.common.core.util.R;
import com.pig4cloud.pig.biz.api.dto.ai.KnowledgeSearchRequest;
import feign.Response;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@FeignClient(contextId = "remoteAiAppClient", name = "pig-ai-app", url = "${frequency.ai.app.url:http://localhost:5010}")
public interface RemoteAiAppClient {

	@GetMapping("/chat/stream")
	Response streamChat(@RequestParam("query") String query,
			@RequestParam(value = "conversationId", required = false) String conversationId,
			@RequestParam(value = "echoId", required = false) Long echoId);

	@PostMapping(value = "/bizKnowledgeBase/train", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	R<Long> trainKnowledge(@RequestPart("file") MultipartFile file, @RequestParam("userId") Long userId);

	@DeleteMapping("/bizKnowledgeBase/removeByIds")
	R<Boolean> removeKnowledgeByIds(@RequestBody Long[] ids);

	@PostMapping("/knowledge/search")
	R<List<KnowledgeSearchHitDTO>> searchKnowledge(@RequestBody KnowledgeSearchRequest request);

}
