package com.pig4cloud.pig.biz.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pig4cloud.pig.biz.entity.BizKnowledgeBaseEntity;
import com.pig4cloud.pig.biz.mapper.BizKnowledgeBaseMapper;
import com.pig4cloud.pig.biz.service.BizKnowledgeBaseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

/**
 * 知识库文档表
 *
 * @author pig
 * @date 2025-12-18 14:56:17
 */
@Slf4j
@Service
public class BizKnowledgeBaseServiceImpl extends ServiceImpl<BizKnowledgeBaseMapper, BizKnowledgeBaseEntity> implements BizKnowledgeBaseService {


	@Async // 异步处理，防止 PDF 解析过久导致前端请求超时
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void uploadAndTrain(MultipartFile file, Long echoId) {
		// 1. 保存元数据并初始化状态
		BizKnowledgeBaseEntity kb = new BizKnowledgeBaseEntity();
		kb.setEchoId(echoId);
		kb.setFileName(file.getOriginalFilename());
		kb.setVectorStatus("1"); // 状态：处理中
		this.save(kb);
	}
}
