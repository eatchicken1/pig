package com.pig4cloud.pig.biz.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.time.LocalDateTime;

/**
 * 社交互动表
 *
 * @author pig
 * @date 2025-12-18 14:57:42
 */
@Data
@TableName("biz_social_interaction")
@EqualsAndHashCode(callSuper = true)
@Schema(description = "社交互动表")
public class BizSocialInteractionEntity extends Model<BizSocialInteractionEntity> {


	/**
	* 主键
	*/
    @TableId(type = IdType.ASSIGN_ID)
    @Schema(description="主键")
    private Long id;

	/**
	* 目标ID (动态ID 或 匹配记录ID)
	*/
    @Schema(description="目标ID (动态ID 或 匹配记录ID)")
    private Long targetId;

	/**
	* 目标类型 (1:动态点赞 2:动态评论 3:直播弹幕)
	*/
    @Schema(description="目标类型 (1:动态点赞 2:动态评论 3:直播弹幕)")
    private String targetType;

	/**
	* 操作用户ID
	*/
    @Schema(description="操作用户ID")
    private Long userId;

	/**
	* 互动内容 (评论/弹幕内容)
	*/
    @Schema(description="互动内容 (评论/弹幕内容)")
    private String content;

	/**
	* 创建时间
	*/
	@TableField(fill = FieldFill.INSERT)
    @Schema(description="创建时间")
    private LocalDateTime createTime;

	/**
	* 租户ID
	*/
    @Schema(description="租户ID")
    private Long tenantId;
}
