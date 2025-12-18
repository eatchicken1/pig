package com.pig4cloud.pig.business.api.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import com.pig4cloud.pig.business.common.core.util.TenantTable;
import java.time.LocalDateTime;

/**
 * 社交动态胶囊表
 *
 * @author pig
 * @date 2025-12-18 14:58:29
 */
@Data
@TenantTable
@TableName("biz_social_moment")
@EqualsAndHashCode(callSuper = true)
@Schema(description = "社交动态胶囊表")
public class BizSocialMomentEntity extends Model<BizSocialMomentEntity> {


	/**
	* 动态ID
	*/
    @TableId(type = IdType.ASSIGN_ID)
    @Schema(description="动态ID")
    private Long momentId;

	/**
	* 发布者ID
	*/
    @Schema(description="发布者ID")
    private Long userId;

	/**
	* 关联Echo ID (若以分身名义发布)
	*/
    @Schema(description="关联Echo ID (若以分身名义发布)")
    private Long echoId;

	/**
	* 文本内容
	*/
    @Schema(description="文本内容")
    private String content;

	/**
	* 媒体资源URL集合 (JSON数组)
	*/
    @Schema(description="媒体资源URL集合 (JSON数组)")
    private String mediaUrls;

	/**
	* 类型 (0:普通动态 1:情绪胶囊 2:求助悬赏)
	*/
    @Schema(description="类型 (0:普通动态 1:情绪胶囊 2:求助悬赏)")
    private String type;

	/**
	* 可见性 (0:全网公开 1:仅本校 2:仅好友)
	*/
    @Schema(description="可见性 (0:全网公开 1:仅本校 2:仅好友)")
    private String visibility;

	/**
	* 是否匿名 (0:否 1:是)
	*/
    @Schema(description="是否匿名 (0:否 1:是)")
    private String isAnonymous;

	/**
	* 是否阅后即焚 (0:否 1:是)
	*/
    @Schema(description="是否阅后即焚 (0:否 1:是)")
    private String burnAfterReading;

	/**
	* 过期/销毁时间
	*/
    @Schema(description="过期/销毁时间")
    private LocalDateTime expiresTime;

	/**
	* 地理位置标签
	*/
    @Schema(description="地理位置标签")
    private String location;

	/**
	* 点赞数
	*/
    @Schema(description="点赞数")
    private Integer likeCount;

	/**
	* 评论数
	*/
    @Schema(description="评论数")
    private Integer commentCount;

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
	* 所属学校ID
	*/
    @Schema(description="所属学校ID")
    private Long tenantId;
}
