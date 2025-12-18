package com.pig4cloud.pig.business.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.pig4cloud.pig.business.api.entity.BizKnowledgeBaseEntity;
import org.springframework.web.multipart.MultipartFile;

public interface BizKnowledgeBaseService extends IService<BizKnowledgeBaseEntity> {
	/**
	 * 上传文档并启动 AI 训练流程
	 */
	void uploadAndTrain(MultipartFile file, Long echoId);
}
