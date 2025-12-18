package com.pig4cloud.pig.business.admin.controller;

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
import com.pig4cloud.pig.business.api.entity.BizSocialMomentEntity;
import com.pig4cloud.pig.business.admin.service.BizSocialMomentService;

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
 * 社交动态胶囊表
 *
 * @author pig
 * @date 2025-12-18 14:58:29
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/bizSocialMoment" )
@Tag(description = "bizSocialMoment" , name = "社交动态胶囊表管理" )
@SecurityRequirement(name = HttpHeaders.AUTHORIZATION)
public class BizSocialMomentController {

    private final  BizSocialMomentService bizSocialMomentService;

    /**
     * 分页查询
     * @param page 分页对象
     * @param bizSocialMoment 社交动态胶囊表
     * @return
     */
    @Operation(summary = "分页查询" , description = "分页查询" )
    @GetMapping("/page" )
    @HasPermission("admin_bizSocialMoment_view")
    public R getBizSocialMomentPage(@ParameterObject Page page, @ParameterObject BizSocialMomentEntity bizSocialMoment) {
        LambdaQueryWrapper<BizSocialMomentEntity> wrapper = Wrappers.lambdaQuery();
        return R.ok(bizSocialMomentService.page(page, wrapper));
    }


    /**
     * 通过条件查询社交动态胶囊表
     * @param bizSocialMoment 查询条件
     * @return R  对象列表
     */
    @Operation(summary = "通过条件查询" , description = "通过条件查询对象" )
    @GetMapping("/details" )
    @HasPermission("admin_bizSocialMoment_view")
    public R getDetails(@ParameterObject BizSocialMomentEntity bizSocialMoment) {
        return R.ok(bizSocialMomentService.list(Wrappers.query(bizSocialMoment)));
    }

    /**
     * 新增社交动态胶囊表
     * @param bizSocialMoment 社交动态胶囊表
     * @return R
     */
    @Operation(summary = "新增社交动态胶囊表" , description = "新增社交动态胶囊表" )
    @SysLog("新增社交动态胶囊表" )
    @PostMapping
    @HasPermission("admin_bizSocialMoment_add")
    public R save(@RequestBody BizSocialMomentEntity bizSocialMoment) {
        return R.ok(bizSocialMomentService.save(bizSocialMoment));
    }

    /**
     * 修改社交动态胶囊表
     * @param bizSocialMoment 社交动态胶囊表
     * @return R
     */
    @Operation(summary = "修改社交动态胶囊表" , description = "修改社交动态胶囊表" )
    @SysLog("修改社交动态胶囊表" )
    @PutMapping
    @HasPermission("admin_bizSocialMoment_edit")
    public R updateById(@RequestBody BizSocialMomentEntity bizSocialMoment) {
        return R.ok(bizSocialMomentService.updateById(bizSocialMoment));
    }

    /**
     * 通过id删除社交动态胶囊表
     * @param ids momentId列表
     * @return R
     */
    @Operation(summary = "通过id删除社交动态胶囊表" , description = "通过id删除社交动态胶囊表" )
    @SysLog("通过id删除社交动态胶囊表" )
    @DeleteMapping
    @HasPermission("admin_bizSocialMoment_del")
    public R removeById(@RequestBody Long[] ids) {
        return R.ok(bizSocialMomentService.removeBatchByIds(CollUtil.toList(ids)));
    }


    /**
     * 导出excel 表格
     * @param bizSocialMoment 查询条件
   	 * @param ids 导出指定ID
     * @return excel 文件流
     */
    @ResponseExcel
    @GetMapping("/export")
    @HasPermission("admin_bizSocialMoment_export")
    public List<BizSocialMomentEntity> exportExcel(BizSocialMomentEntity bizSocialMoment,Long[] ids) {
        return bizSocialMomentService.list(Wrappers.lambdaQuery(bizSocialMoment).in(ArrayUtil.isNotEmpty(ids), BizSocialMomentEntity::getMomentId, ids));
    }

    /**
     * 导入excel 表
     * @param bizSocialMomentList 对象实体列表
     * @param bindingResult 错误信息列表
     * @return ok fail
     */
    @PostMapping("/import")
    @HasPermission("admin_bizSocialMoment_export")
    public R importExcel(@RequestExcel List<BizSocialMomentEntity> bizSocialMomentList, BindingResult bindingResult) {
        return R.ok(bizSocialMomentService.saveBatch(bizSocialMomentList));
    }
}
