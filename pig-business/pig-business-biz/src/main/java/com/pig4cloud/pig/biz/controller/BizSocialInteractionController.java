package com.pig4cloud.pig.biz.controller;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pig4cloud.pig.common.core.util.R;
import com.pig4cloud.pig.common.log.annotation.SysLog;
import com.pig4cloud.pig.common.security.annotation.HasPermission;
import com.pig4cloud.plugin.excel.annotation.ResponseExcel;
import com.pig4cloud.plugin.excel.annotation.RequestExcel;
import com.pig4cloud.pig.biz.entity.BizSocialInteractionEntity;
import com.pig4cloud.pig.biz.service.BizSocialInteractionService;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpHeaders;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 社交互动表
 *
 * @author pig
 * @date 2025-12-18 14:57:42
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/bizSocialInteraction" )
@Tag(description = "bizSocialInteraction" , name = "社交互动表管理" )
@SecurityRequirement(name = HttpHeaders.AUTHORIZATION)
public class BizSocialInteractionController {

    private final  BizSocialInteractionService bizSocialInteractionService;

    /**
     * 分页查询
     * @param page 分页对象
     * @param bizSocialInteraction 社交互动表
     * @return
     */
    @Operation(summary = "分页查询" , description = "分页查询" )
    @GetMapping("/page" )
    @HasPermission("admin_bizSocialInteraction_view")
    public R getBizSocialInteractionPage(@ParameterObject Page page, @ParameterObject BizSocialInteractionEntity bizSocialInteraction) {
        LambdaQueryWrapper<BizSocialInteractionEntity> wrapper = Wrappers.lambdaQuery();
        return R.ok(bizSocialInteractionService.page(page, wrapper));
    }


    /**
     * 通过条件查询社交互动表
     * @param bizSocialInteraction 查询条件
     * @return R  对象列表
     */
    @Operation(summary = "通过条件查询" , description = "通过条件查询对象" )
    @GetMapping("/details" )
    @HasPermission("admin_bizSocialInteraction_view")
    public R getDetails(@ParameterObject BizSocialInteractionEntity bizSocialInteraction) {
        return R.ok(bizSocialInteractionService.list(Wrappers.query(bizSocialInteraction)));
    }

    /**
     * 新增社交互动表
     * @param bizSocialInteraction 社交互动表
     * @return R
     */
    @Operation(summary = "新增社交互动表" , description = "新增社交互动表" )
    @SysLog("新增社交互动表" )
    @PostMapping
    @HasPermission("admin_bizSocialInteraction_add")
    public R save(@RequestBody BizSocialInteractionEntity bizSocialInteraction) {
        return R.ok(bizSocialInteractionService.save(bizSocialInteraction));
    }

    /**
     * 修改社交互动表
     * @param bizSocialInteraction 社交互动表
     * @return R
     */
    @Operation(summary = "修改社交互动表" , description = "修改社交互动表" )
    @SysLog("修改社交互动表" )
    @PutMapping
    @HasPermission("admin_bizSocialInteraction_edit")
    public R updateById(@RequestBody BizSocialInteractionEntity bizSocialInteraction) {
        return R.ok(bizSocialInteractionService.updateById(bizSocialInteraction));
    }

    /**
     * 通过id删除社交互动表
     * @param ids id列表
     * @return R
     */
    @Operation(summary = "通过id删除社交互动表" , description = "通过id删除社交互动表" )
    @SysLog("通过id删除社交互动表" )
    @DeleteMapping
    @HasPermission("admin_bizSocialInteraction_del")
    public R removeById(@RequestBody Long[] ids) {
        return R.ok(bizSocialInteractionService.removeBatchByIds(CollUtil.toList(ids)));
    }


    /**
     * 导出excel 表格
     * @param bizSocialInteraction 查询条件
   	 * @param ids 导出指定ID
     * @return excel 文件流
     */
    @ResponseExcel
    @GetMapping("/export")
    @HasPermission("admin_bizSocialInteraction_export")
    public List<BizSocialInteractionEntity> exportExcel(BizSocialInteractionEntity bizSocialInteraction,Long[] ids) {
        return bizSocialInteractionService.list(Wrappers.lambdaQuery(bizSocialInteraction).in(ArrayUtil.isNotEmpty(ids), BizSocialInteractionEntity::getId, ids));
    }

    /**
     * 导入excel 表
     * @param bizSocialInteractionList 对象实体列表
     * @param bindingResult 错误信息列表
     * @return ok fail
     */
    @PostMapping("/import")
    @HasPermission("admin_bizSocialInteraction_export")
    public R importExcel(@RequestExcel List<BizSocialInteractionEntity> bizSocialInteractionList, BindingResult bindingResult) {
        return R.ok(bizSocialInteractionService.saveBatch(bizSocialInteractionList));
    }
}
