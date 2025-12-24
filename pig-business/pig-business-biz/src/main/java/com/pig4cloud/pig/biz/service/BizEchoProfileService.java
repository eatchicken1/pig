package com.pig4cloud.pig.biz.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.pig4cloud.pig.biz.entity.BizEchoProfileEntity;

public interface BizEchoProfileService extends IService<BizEchoProfileEntity> {
	/**
	 * 获取当前登录用户的 Echo 配置
	 * 如果不存在，是否自动创建默认值？(策略：是)
	 */
	BizEchoProfileEntity getMyEchoProfile();

	/**
	 * 更新当前用户的 Echo 配置
	 */
	boolean updateMyEchoProfile(BizEchoProfileEntity profile);
}
