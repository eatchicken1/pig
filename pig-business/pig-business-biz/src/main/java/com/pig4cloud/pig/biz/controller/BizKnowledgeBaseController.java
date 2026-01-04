package com.pig4cloud.pig.biz.controller;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pig4cloud.pig.common.core.util.R;
import com.pig4cloud.pig.common.log.annotation.SysLog;
import com.pig4cloud.pig.common.security.annotation.HasPermission;
import com.pig4cloud.pig.common.security.util.SecurityUtils;
import com.pig4cloud.plugin.excel.annotation.ResponseExcel;
import com.pig4cloud.plugin.excel.annotation.RequestExcel;
import com.pig4cloud.pig.biz.entity.BizKnowledgeBaseEntity;
import com.pig4cloud.pig.biz.service.BizKnowledgeBaseService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpHeaders;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

/**
 * 知识库文档表
 *
 * @author pig
 * @date 2025-12-18 14:56:17
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/bizKnowledgeBase" )
@Tag(description = "bizKnowledgeBase" , name = "知识库文档表管理" )
@Slf4j
@SecurityRequirement(name = HttpHeaders.AUTHORIZATION)
public class BizKnowledgeBaseController {

    private final  BizKnowledgeBaseService bizKnowledgeBaseService;

    /**
     * 分页查询
     * @param page 分页对象
     * @param bizKnowledgeBase 知识库文档表
     * @return
     */
    @Operation(summary = "分页查询" , description = "分页查询" )
    @GetMapping("/page" )
    @HasPermission("admin_bizKnowledgeBase_view")
    public R getBizKnowledgeBasePage(@ParameterObject Page page, @ParameterObject BizKnowledgeBaseEntity bizKnowledgeBase) {
        LambdaQueryWrapper<BizKnowledgeBaseEntity> wrapper = Wrappers.lambdaQuery();
        return R.ok(bizKnowledgeBaseService.page(page, wrapper));
    }


    /**
     * 通过条件查询知识库文档表
     * @param bizKnowledgeBase 查询条件
     * @return R  对象列表
     */
    @Operation(summary = "通过条件查询" , description = "通过条件查询对象" )
    @GetMapping("/details" )
    @HasPermission("admin_bizKnowledgeBase_view")
    public R getDetails(@ParameterObject BizKnowledgeBaseEntity bizKnowledgeBase) {
        return R.ok(bizKnowledgeBaseService.list(Wrappers.query(bizKnowledgeBase)));
    }

    /**
     * 新增知识库文档表
     * @param bizKnowledgeBase 知识库文档表
     * @return R
     */
    @Operation(summary = "新增知识库文档表" , description = "新增知识库文档表" )
    @SysLog("新增知识库文档表" )
    @PostMapping
    @HasPermission("admin_bizKnowledgeBase_add")
    public R save(@RequestBody BizKnowledgeBaseEntity bizKnowledgeBase) {
        return R.ok(bizKnowledgeBaseService.save(bizKnowledgeBase));
    }

    /**
     * 修改知识库文档表
     * @param bizKnowledgeBase 知识库文档表
     * @return R
     */
    @Operation(summary = "修改知识库文档表" , description = "修改知识库文档表" )
    @SysLog("修改知识库文档表" )
    @PutMapping
    @HasPermission("admin_bizKnowledgeBase_edit")
    public R updateById(@RequestBody BizKnowledgeBaseEntity bizKnowledgeBase) {
        return R.ok(bizKnowledgeBaseService.updateById(bizKnowledgeBase));
    }

    /**
     * 通过id删除知识库文档表
     * @param ids id列表
     * @return R
     */
    @Operation(summary = "通过id删除知识库文档表" , description = "通过id删除知识库文档表" )
    @SysLog("通过id删除知识库文档表" )
    @DeleteMapping
    @HasPermission("admin_bizKnowledgeBase_del")
    public R removeById(@RequestBody Long[] ids) {
        return R.ok(bizKnowledgeBaseService.removeBatchByIds(CollUtil.toList(ids)));
    }


    /**
     * 导出excel 表格
     * @param bizKnowledgeBase 查询条件
   	 * @param ids 导出指定ID
     * @return excel 文件流
     */
    @ResponseExcel
    @GetMapping("/export")
    @HasPermission("admin_bizKnowledgeBase_export")
    public List<BizKnowledgeBaseEntity> exportExcel(BizKnowledgeBaseEntity bizKnowledgeBase,Long[] ids) {
        return bizKnowledgeBaseService.list(Wrappers.lambdaQuery(bizKnowledgeBase).in(ArrayUtil.isNotEmpty(ids), BizKnowledgeBaseEntity::getId, ids));
    }

    /**
     * 导入excel 表
     * @param bizKnowledgeBaseList 对象实体列表
     * @param bindingResult 错误信息列表
     * @return ok fail
     */
    @PostMapping("/import")
    @HasPermission("admin_bizKnowledgeBase_export")
    public R importExcel(@RequestExcel List<BizKnowledgeBaseEntity> bizKnowledgeBaseList, BindingResult bindingResult) {
        return R.ok(bizKnowledgeBaseService.saveBatch(bizKnowledgeBaseList));
    }

	@PostMapping("/train")
	@Operation(summary = "投喂文档进行 AI 训练")
	//@HasPermission("admin_bizKnowledgeBase_train")
	public R<Long> train(
			@RequestParam("file") MultipartFile file,
			@RequestParam("userId") Long userId) {
		if (file.isEmpty()) {
			return R.failed("文件不能为空");
		}
		Long recordId = bizKnowledgeBaseService.uploadAndTrain(file, userId);
		return R.ok(recordId);
	}
	/**
	 * 删除知识库
	 * @param ids id列表
	 * @return R
	 */
	@Operation(summary = "删除知识库", description = "删除知识库")
	@SysLog("删除知识库")
	@DeleteMapping("/removeByIds")
	@PreAuthorize("@pms.hasPermission('biz_knowledgebase_del')")
	public R removeByIds(@RequestBody Long[] ids) {
		return R.ok(bizKnowledgeBaseService.removeKnowledgeBatchByIds(Arrays.asList(ids)));
	}
}
