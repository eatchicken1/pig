package com.pig4cloud.pig.biz.listener;

import com.pig4cloud.pig.biz.api.dto.BatchDeleteKnowledgeRequest;
import com.pig4cloud.pig.biz.api.feign.FrequencyAiClient;
import com.pig4cloud.pig.biz.event.KnowledgeDeleteEvent;
import com.pig4cloud.pig.common.core.util.R;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class KnowledgeDeleteListener {

	private final FrequencyAiClient frequencyAiClient;

	/**
	 * 监听删除事件
	 * phase = TransactionPhase.AFTER_COMMIT: 只有数据库事务提交成功后才执行
	 * @Async: 在单独的线程池中执行，不阻塞主线程
	 */
	@Async
	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public void handleKnowledgeDelete(KnowledgeDeleteEvent event) {
		if (event.getDeleteList() == null || event.getDeleteList().isEmpty()) {
			return;
		}
		log.info("监听到知识库删除事件，开始异步同步删除向量，数量: {}", event.getDeleteList().size());
		try {
			BatchDeleteKnowledgeRequest batchReq = new BatchDeleteKnowledgeRequest();
			batchReq.setItems(event.getDeleteList());
			// 单次批量调用
			R result = frequencyAiClient.batchDeleteKnowledge(batchReq);
			if (result.getCode() != 0) {
				log.warn("AI向量批量删除失败: {}", result.getMsg());
				// TODO: 企业级方案这里可以接入消息队列(RabbitMQ/RocketMQ)的死信队列进行重试
			} else {
				log.info("AI向量批量删除成功");
			}
		} catch (Exception e) {
			log.error("AI向量批量删除异常", e);
		}
	}
}