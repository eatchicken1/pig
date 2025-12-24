package com.pig4cloud.pig.biz.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pig4cloud.pig.biz.entity.BizEchoProfileEntity;
import com.pig4cloud.pig.biz.mapper.BizEchoProfileMapper;
import com.pig4cloud.pig.biz.service.BizEchoProfileService;
import com.pig4cloud.pig.common.security.util.SecurityUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Echo数字分身配置表
 *
 * @author taishengluo
 * @date 2025-12-18 14:53:32
 */
@Service
public class BizEchoProfileServiceImpl extends ServiceImpl<BizEchoProfileMapper, BizEchoProfileEntity> implements BizEchoProfileService {
	@Override
	public BizEchoProfileEntity getMyEchoProfile() {
		Long userId = SecurityUtils.getUser().getId();
		BizEchoProfileEntity profile = this.getById(userId);

		// 懒加载策略：如果还没创建过，就返回一个初始化的对象（但不入库，等用户保存时再入库）
		// 或者直接入库一个默认值
		if (profile == null) {
			profile = new BizEchoProfileEntity();
			profile.setEchoId(userId);
			profile.setNickname(SecurityUtils.getUser().getUsername());
			profile.setIsPublic("1"); // 默认公开
			profile.setHeat(0);
			profile.setVoiceTone("正常");
		}
		return profile;
	}

	@Transactional(rollbackFor = Exception.class)
	@Override
	public boolean updateMyEchoProfile(BizEchoProfileEntity profile) {
		Long userId = SecurityUtils.getUser().getId();

		// 强制绑定当前用户ID，防止恶意修改他人数据
		profile.setEchoId(userId);

		// 检查是否存在
		if (this.getById(userId) == null) {
			// 不存在则新增
			// 补充租户ID等信息
			// profile.setTenantId(...); // PIG Mybatis Plus 插件会自动处理
			return this.save(profile);
		} else {
			// 存在则更新
			return this.updateById(profile);
		}
	}
}
