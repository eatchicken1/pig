package com.pig4cloud.pig.biz.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.time.LocalDateTime;

/**
 * 用户积分钱包表
 *
 * @author luotaisheng
 * @date 2025-12-23 16:15:05
 */
@Data
@TableName("biz_wallet")
@EqualsAndHashCode(callSuper = true)
@Schema(description = "用户积分钱包表")
public class BizWalletEntity extends Model<BizWalletEntity> {


	/**
	* 用户ID
	*/
    @TableId(type = IdType.ASSIGN_ID)
    @Schema(description="用户ID")
    private Long userId;

	/**
	* 当前积分余额
	*/
    @Schema(description="当前积分余额")
    private Integer balance;

	/**
	* 历史总收入
	*/
    @Schema(description="历史总收入")
    private Integer totalIncome;

	/**
	* 历史总支出
	*/
    @Schema(description="历史总支出")
    private Integer totalExpenditure;

	/**
	* 冻结金额 (交易中)
	*/
    @Schema(description="冻结金额 (交易中)")
    private Integer frozenAmount;
 
	/**
	* updateTime
	*/
	@TableField(fill = FieldFill.INSERT_UPDATE)
    @Schema(description="updateTime")
    private LocalDateTime updateTime;

	/**
	* 租户ID
	*/
    @Schema(description="租户ID")
    private Long tenantId;
}
