package com.pig4cloud.pig.biz.event;

import com.pig4cloud.pig.biz.api.dto.DeleteKnowledgeRequest;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;
import java.util.List;

/**
 * 知识库删除事件
 */
@Getter
public class KnowledgeDeleteEvent extends ApplicationEvent {

	private final List<DeleteKnowledgeRequest> deleteList;

	public KnowledgeDeleteEvent(Object source, List<DeleteKnowledgeRequest> deleteList) {
		super(source);
		this.deleteList = deleteList;
	}
}