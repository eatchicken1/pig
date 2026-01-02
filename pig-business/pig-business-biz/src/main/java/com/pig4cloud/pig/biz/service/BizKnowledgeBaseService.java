package com.pig4cloud.pig.biz.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.pig4cloud.pig.admin.api.dto.UserInfo;
import com.pig4cloud.pig.biz.entity.BizKnowledgeBaseEntity;
import com.pig4cloud.pig.common.core.util.R;
import com.pig4cloud.pig.common.security.service.PigUser;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.util.concurrent.Future;

public interface BizKnowledgeBaseService extends IService<BizKnowledgeBaseEntity> {
	/**
	 * 上传文档并启动 AI 训练流程
	 */
	void uploadAndTrain(Path file, Long echoId, PigUser userInfo);
}
