package com.pig4cloud.pig.admin.api.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.time.LocalDateTime;

/**
 * 高校信息表
 *
 * @author pig
 * @date 2025-12-18 14:27:33
 */
@Data
@TableName("sys_school")
@EqualsAndHashCode(callSuper = true)
@Schema(description = "高校信息表")
public class SysSchoolEntity extends Model<SysSchoolEntity> {


	/**
	* 学校ID
	*/
    @TableId(type = IdType.ASSIGN_ID)
    @Schema(description="学校ID")
    private Long schoolId;

	/**
	* 学校名称
	*/
    @Schema(description="学校名称")
    private String name;

	/**
	* 院校代码
	*/
    @Schema(description="院校代码")
    private String code;

	/**
	* 邮箱后缀
	*/
    @Schema(description="邮箱后缀")
    private String domain;

	/**
	* 校徽URL
	*/
    @Schema(description="校徽URL")
    private String logo;
 
	/**
	* createBy
	*/
	@TableField(fill = FieldFill.INSERT)
    @Schema(description="createBy")
    private String createBy;
 
	/**
	* createTime
	*/
	@TableField(fill = FieldFill.INSERT)
    @Schema(description="createTime")
    private LocalDateTime createTime;
 
	/**
	* updateBy
	*/
	@TableField(fill = FieldFill.INSERT_UPDATE)
    @Schema(description="updateBy")
    private String updateBy;
 
	/**
	* updateTime
	*/
	@TableField(fill = FieldFill.INSERT_UPDATE)
    @Schema(description="updateTime")
    private LocalDateTime updateTime;
 
	/**
	* delFlag
	*/
    @TableLogic
	@TableField(fill = FieldFill.INSERT)
    @Schema(description="delFlag")
    private String delFlag;
}
