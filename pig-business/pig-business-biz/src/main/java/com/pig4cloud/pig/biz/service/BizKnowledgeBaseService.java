package com.pig4cloud.pig.biz.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.pig4cloud.pig.admin.api.dto.UserInfo;
import com.pig4cloud.pig.biz.entity.BizKnowledgeBaseEntity;
import com.pig4cloud.pig.common.core.util.R;
import com.pig4cloud.pig.common.security.service.PigUser;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.Future;

public interface BizKnowledgeBaseService extends IService<BizKnowledgeBaseEntity> {
	/**
	 * 上传文档并启动 AI 训练流程
	 */
	public Long uploadAndTrain(MultipartFile file, Long userId);

	/**
	 * 批量删除知识库（同步删除AI向量数据）
	 *
	 * @param ids ID列表
	 * @return 是否成功
	 */
	Boolean removeKnowledgeBatchByIds(List<Long> ids);
}
