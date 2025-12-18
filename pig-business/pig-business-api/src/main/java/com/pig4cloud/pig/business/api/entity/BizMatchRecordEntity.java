package com.pig4cloud.pig.business.api.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.time.LocalDateTime;

/**
 * AI匹配相亲记录表
 *
 * @author pig
 * @date 2025-12-18 14:57:00
 */
@Data
@TableName("biz_match_record")
@EqualsAndHashCode(callSuper = true)
@Schema(description = "AI匹配相亲记录表")
public class BizMatchRecordEntity extends Model<BizMatchRecordEntity> {


	/**
	* 匹配记录ID
	*/
    @TableId(type = IdType.ASSIGN_ID)
    @Schema(description="匹配记录ID")
    private Long matchId;

	/**
	* 发起人ID (User/Echo)
	*/
    @Schema(description="发起人ID (User/Echo)")
    private Long initiatorId;

	/**
	* 目标人ID (User/Echo)
	*/
    @Schema(description="目标人ID (User/Echo)")
    private Long targetId;

	/**
	* 关联的会话ID (对应 biz_chat_history)
	*/
    @Schema(description="关联的会话ID (对应 biz_chat_history)")
    private String sessionId;

	/**
	* AI计算的匹配度 (0-100)
	*/
    @Schema(description="AI计算的匹配度 (0-100)")
    private Integer matchScore;

	/**
	* AI生成的匹配总结/评价
	*/
    @Schema(description="AI生成的匹配总结/评价")
    private String matchSummary;

	/**
	* 状态 (0:进行中 1:匹配成功/解锁 2:匹配失败/婉拒 3:已取消)
	*/
    @Schema(description="状态 (0:进行中 1:匹配成功/解锁 2:匹配失败/婉拒 3:已取消)")
    private String status;

	/**
	* 解锁方式 (0:双方同意 1:积分强制解锁)
	*/
    @Schema(description="解锁方式 (0:双方同意 1:积分强制解锁)")
    private String unlockType;

	/**
	* 创建人
	*/
	@TableField(fill = FieldFill.INSERT)
    @Schema(description="创建人")
    private String createBy;

	/**
	* 创建时间
	*/
	@TableField(fill = FieldFill.INSERT)
    @Schema(description="创建时间")
    private LocalDateTime createTime;

	/**
	* 修改人
	*/
	@TableField(fill = FieldFill.INSERT_UPDATE)
    @Schema(description="修改人")
    private String updateBy;

	/**
	* 更新时间
	*/
	@TableField(fill = FieldFill.INSERT_UPDATE)
    @Schema(description="更新时间")
    private LocalDateTime updateTime;

	/**
	* 删除标记
	*/
    @TableLogic
	@TableField(fill = FieldFill.INSERT)
    @Schema(description="删除标记")
    private String delFlag;

	/**
	* 所属租户(通常为发起人学校)
	*/
    @Schema(description="所属租户(通常为发起人学校)")
    private Long tenantId;
}
