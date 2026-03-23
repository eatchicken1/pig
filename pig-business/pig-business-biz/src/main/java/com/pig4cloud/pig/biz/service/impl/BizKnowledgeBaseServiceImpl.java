package com.pig4cloud.pig.biz.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pig4cloud.pig.biz.entity.BizKnowledgeBaseEntity;
import com.pig4cloud.pig.biz.mapper.BizKnowledgeBaseMapper;
import com.pig4cloud.pig.biz.service.BizKnowledgeBaseService;
import org.springframework.stereotype.Service;

@Service
public class BizKnowledgeBaseServiceImpl extends ServiceImpl<BizKnowledgeBaseMapper, BizKnowledgeBaseEntity>
		implements BizKnowledgeBaseService {
}
