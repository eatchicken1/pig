package com.pig4cloud.pig.biz.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.time.LocalDateTime;

/**
 * 知识库文档表
 *
 * @author pig
 * @date 2025-12-18 14:56:17
 */
@Data
@TableName("biz_knowledge_base")
@EqualsAndHashCode(callSuper = true)
@Schema(description = "知识库文档表")
public class BizKnowledgeBaseEntity extends Model<BizKnowledgeBaseEntity> {


	/**
	* 主键
	*/
    @TableId(type = IdType.ASSIGN_ID)
    @Schema(description="主键")
    private Long id;

	/**
	* 所属分身ID
	*/
    @Schema(description="所属分身ID")
    private Long echoId;

	/**
	* 文件名称
	*/
    @Schema(description="文件名称")
    private String fileName;

	/**
	* OSS文件地址
	*/
    @Schema(description="OSS文件地址")
    private String fileUrl;

	/**
	* 文件类型 (PDF/MD/TXT)
	*/
    @Schema(description="文件类型 (PDF/MD/TXT)")
    private String fileType;

	/**
	* 向量化状态 (0待处理 1处理中 2完成 9失败)
	*/
    @Schema(description="向量化状态 (0待处理 1处理中 2完成 9失败)")
    private String vectorStatus;

	/**
	* 消耗Token数
	*/
    @Schema(description="消耗Token数")
    private Integer tokenCount;

	/**
	* 内容摘要
	*/
    @Schema(description="内容摘要")
    private String summary;

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
	@TableField(fill = FieldFill.UPDATE)
    @Schema(description="修改人")
    private String updateBy;

	/**
	 * 修改时间
	 */
	@TableField(fill = FieldFill.UPDATE)
    @Schema(description="修改时间")
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

	/**
	 * 是否上架黑市 (0否 1是)
	 */
	@Schema(description = "是否上架黑市 (0否 1是)")
	private String isTradable;

	/**
	 * 售价 (积分)
	 */
	@Schema(description = "售价 (积分)")
	private Integer price;

	/**
	 * 销量
	 */
	@Schema(description = "销量")
	private Integer salesCount;

	/**
	 * 封面图URL (用于黑市展示)
	 */
	@Schema(description = "封面图URL (用于黑市展示)")
	private String coverUrl;

}
