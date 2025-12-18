package com.pig4cloud.pig.business.api.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import com.pig4cloud.pig.business.common.core.util.TenantTable;
import java.time.LocalDateTime;

/**
 * 对话历史记录表
 *
 * @author taishengluo
 * @date 2025-12-18 14:48:25
 */
@Data
@TenantTable
@TableName("biz_chat_history")
@EqualsAndHashCode(callSuper = true)
@Schema(description = "对话历史记录表")
public class BizChatHistoryEntity extends Model<BizChatHistoryEntity> {


	/**
	* 主键
	*/
    @TableId(type = IdType.ASSIGN_ID)
    @Schema(description="主键")
    private Long id;

	/**
	* 会话ID (唯一标识一次聊天)
	*/
    @Schema(description="会话ID (唯一标识一次聊天)")
    private String sessionId;

	/**
	* 发送者ID (真实用户或Echo)
	*/
    @Schema(description="发送者ID (真实用户或Echo)")
    private Long senderId;

	/**
	* 接收者ID (Echo)
	*/
    @Schema(description="接收者ID (Echo)")
    private Long receiverId;

	/**
	* 对话内容
	*/
    @Schema(description="对话内容")
    private String content;

	/**
	* 角色 (user/assistant/system)
	*/
    @Schema(description="角色 (user/assistant/system)")
    private String role;

	/**
	* 是否AI生成
	*/
    @Schema(description="是否AI生成")
    private String isAiGenerated;

	/**
	* 发送时间
	*/
	@TableField(fill = FieldFill.INSERT)
    @Schema(description="发送时间")
    private LocalDateTime createTime;

	/**
	* 租户ID
	*/
    @Schema(description="租户ID")
    private Long tenantId;
}
