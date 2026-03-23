package com.pig4cloud.pig.ai.api.knowledge;

public interface AiKnowledgeGateway {

	void train(KnowledgeTrainCommand command);

	boolean delete(KnowledgeDeleteCommand command);

	boolean batchDelete(KnowledgeBatchDeleteCommand command);
}
