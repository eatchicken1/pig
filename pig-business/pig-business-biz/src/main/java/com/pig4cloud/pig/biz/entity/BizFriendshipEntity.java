package com.pig4cloud.pig.biz.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.time.LocalDateTime;

/**
 * 好友关系表
 *
 * @author taishengluo
 * @date 2025-12-18 14:55:20
 */
@Data
@TableName("biz_friendship")
@EqualsAndHashCode(callSuper = true)
@Schema(description = "好友关系表")
public class BizFriendshipEntity extends Model<BizFriendshipEntity> {


	/**
	* 主键
	*/
    @TableId(type = IdType.ASSIGN_ID)
    @Schema(description="主键")
    private Long id;

	/**
	* 用户ID
	*/
    @Schema(description="用户ID")
    private Long userId;

	/**
	* 好友ID
	*/
    @Schema(description="好友ID")
    private Long friendId;

	/**
	* 来源 (MATCH:AI匹配, QR:扫码, MARKET:知识黑市)
	*/
    @Schema(description="来源 (MATCH:AI匹配, QR:扫码, MARKET:知识黑市)")
    private String source;

	/**
	* 关联的匹配记录ID
	*/
    @Schema(description="关联的匹配记录ID")
    private Long matchId;

	/**
	* 好友备注
	*/
    @Schema(description="好友备注")
    private String remark;

	/**
	* 状态 (0:申请中 1:已添加 2:拉黑)
	*/
    @Schema(description="状态 (0:申请中 1:已添加 2:拉黑)")
    private String status;

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
	* 租户ID
	*/
    @Schema(description="租户ID")
    private Long tenantId;
}
