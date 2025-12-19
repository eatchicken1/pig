package com.pig4cloud.pig.biz.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.time.LocalDateTime;

/**
 * Echo数字分身配置表
 *
 * @author taishengluo
 * @date 2025-12-18 14:53:32
 */
@Data
@TableName("biz_echo_profile")
@EqualsAndHashCode(callSuper = true)
@Schema(description = "Echo数字分身配置表")
public class BizEchoProfileEntity extends Model<BizEchoProfileEntity> {


	/**
	* 分身ID (通常等于User ID)
	*/
    @TableId(type = IdType.ASSIGN_ID)
    @Schema(description="分身ID (通常等于User ID)")
    private Long echoId;

	/**
	* 分身昵称
	*/
    @Schema(description="分身昵称")
    private String nickname;

	/**
	* 分身头像
	*/
    @Schema(description="分身头像")
    private String avatar;

	/**
	* 人设Prompt (如: 高冷学霸，说话不超过10个字)
	*/
    @Schema(description="人设Prompt (如: 高冷学霸，说话不超过10个字)")
    private String personalityPrompt;

	/**
	* 语言风格 (幽默/严肃/温柔)
	*/
    @Schema(description="语言风格 (幽默/严肃/温柔)")
    private String voiceTone;

	/**
	* 是否公开 (1公开 0私密)
	*/
    @Schema(description="是否公开 (1公开 0私密)")
    private String isPublic;

	/**
	* 热度值 (被对话次数)
	*/
    @Schema(description="热度值 (被对话次数)")
    private Integer heat;

	/**
	* 擅长领域 (考研, 恋爱, 编程)
	*/
    @Schema(description="擅长领域 (考研, 恋爱, 编程)")
    private String tags;

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
