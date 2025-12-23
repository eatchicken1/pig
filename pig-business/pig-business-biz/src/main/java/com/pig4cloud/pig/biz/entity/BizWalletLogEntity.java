package com.pig4cloud.pig.biz.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.time.LocalDateTime;

/**
 * 钱包变动流水表
 *
 * @author luotaisheng
 * @date 2025-12-23 16:15:23
 */
@Data
@TableName("biz_wallet_log")
@EqualsAndHashCode(callSuper = true)
@Schema(description = "钱包变动流水表")
public class BizWalletLogEntity extends Model<BizWalletLogEntity> {


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
	* 变动金额 (+/-)
	*/
    @Schema(description="变动金额 (+/-)")
    private Integer amount;

	/**
	* 变动后余额
	*/
    @Schema(description="变动后余额")
    private Integer balanceAfter;

	/**
	* 业务类型 (BUY_DOC, UNLOCK_MATCH, REWARD)
	*/
    @Schema(description="业务类型 (BUY_DOC, UNLOCK_MATCH, REWARD)")
    private String bizType;

	/**
	* 关联订单ID
	*/
    @Schema(description="关联订单ID")
    private Long orderId;
 
	/**
	* createTime
	*/
	@TableField(fill = FieldFill.INSERT)
    @Schema(description="createTime")
    private LocalDateTime createTime;
}
