package com.pig4cloud.pig.biz.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.time.LocalDateTime;

/**
 * 积分交易订单表
 *
 * @author luotaisheng
 * @date 2025-12-23 16:16:01
 */
@Data
@TableName("biz_order")
@EqualsAndHashCode(callSuper = true)
@Schema(description = "积分交易订单表")
public class BizOrderEntity extends Model<BizOrderEntity> {


	/**
	* 订单ID
	*/
    @TableId(type = IdType.ASSIGN_ID)
    @Schema(description="订单ID")
    private Long orderId;

	/**
	* 订单号 (唯一)
	*/
    @Schema(description="订单号 (唯一)")
    private String orderNo;

	/**
	* 买家ID
	*/
    @Schema(description="买家ID")
    private Long buyerId;

	/**
	* 卖家ID (系统充值或解锁时为空)
	*/
    @Schema(description="卖家ID (系统充值或解锁时为空)")
    private Long sellerId;

	/**
	* 商品类型 (1:知识文档 2:强制解锁匹配 3:充值)
	*/
    @Schema(description="商品类型 (1:知识文档 2:强制解锁匹配 3:充值)")
    private String productType;

	/**
	* 关联商品ID (文档ID或匹配ID)
	*/
    @Schema(description="关联商品ID (文档ID或匹配ID)")
    private Long productId;

	/**
	* 交易金额 (积分)
	*/
    @Schema(description="交易金额 (积分)")
    private Integer amount;

	/**
	* 状态 (0:待支付 1:已完成 9:已取消)
	*/
    @Schema(description="状态 (0:待支付 1:已完成 9:已取消)")
    private String status;
 
	/**
	* createTime
	*/
	@TableField(fill = FieldFill.INSERT)
    @Schema(description="createTime")
    private LocalDateTime createTime;

	/**
	* 租户ID
	*/
    @Schema(description="租户ID")
    private Long tenantId;
}
