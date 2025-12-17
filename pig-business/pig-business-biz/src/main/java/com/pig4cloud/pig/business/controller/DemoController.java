package com.pig4cloud.pig.business.controller;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pig4cloud.pig.business.entity.DemoEntity;
import com.pig4cloud.pig.business.service.DemoService;
import com.pig4cloud.pig.common.core.util.R;
import com.pig4cloud.pig.common.log.annotation.SysLog;
import com.pig4cloud.pig.common.security.annotation.HasPermission;
import com.pig4cloud.plugin.excel.annotation.ResponseExcel;
import com.pig4cloud.plugin.excel.annotation.RequestExcel;
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
 * demo 表
 *
 * @author taishengluo
 * @date 2025-12-17 17:05:39
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/demo" )
@Tag(description = "demo" , name = "demo 表管理" )
@SecurityRequirement(name = HttpHeaders.AUTHORIZATION)
public class DemoController {


    private final DemoService demoService;

    /**
     * 分页查询
     * @param page 分页对象
     * @param demo demo 表
     * @return
     */
    @Operation(summary = "分页查询" , description = "分页查询" )
    @GetMapping("/page" )
    @HasPermission("pig-business-biz_demo_view")
    public R getDemoPage(@ParameterObject Page page, @ParameterObject DemoEntity demo) {
        LambdaQueryWrapper<DemoEntity> wrapper = Wrappers.lambdaQuery();
        return R.ok(demoService.page(page, wrapper));
    }


    /**
     * 通过条件查询demo 表
     * @param demo 查询条件
     * @return R  对象列表
     */
    @Operation(summary = "通过条件查询" , description = "通过条件查询对象" )
    @GetMapping("/details" )
    @HasPermission("pig-business-biz_demo_view")
    public R getDetails(@ParameterObject DemoEntity demo) {
        return R.ok(demoService.list(Wrappers.query(demo)));
    }

    /**
     * 新增demo 表
     * @param demo demo 表
     * @return R
     */
    @Operation(summary = "新增demo 表" , description = "新增demo 表" )
    @SysLog("新增demo 表" )
    @PostMapping
    @HasPermission("pig-business-biz_demo_add")
    public R save(@RequestBody DemoEntity demo) {
        return R.ok(demoService.save(demo));
    }

    /**
     * 修改demo 表
     * @param demo demo 表
     * @return R
     */
    @Operation(summary = "修改demo 表" , description = "修改demo 表" )
    @SysLog("修改demo 表" )
    @PutMapping
    @HasPermission("pig-business-biz_demo_edit")
    public R updateById(@RequestBody DemoEntity demo) {
        return R.ok(demoService.updateById(demo));
    }

    /**
     * 通过id删除demo 表
     * @param ids id列表
     * @return R
     */
    @Operation(summary = "通过id删除demo 表" , description = "通过id删除demo 表" )
    @SysLog("通过id删除demo 表" )
    @DeleteMapping
    @HasPermission("pig-business-biz_demo_del")
    public R removeById(@RequestBody Long[] ids) {
        return R.ok(demoService.removeBatchByIds(CollUtil.toList(ids)));
    }


    /**
     * 导出excel 表格
     * @param demo 查询条件
   	 * @param ids 导出指定ID
     * @return excel 文件流
     */
    @ResponseExcel
    @GetMapping("/export")
    @HasPermission("pig-business-biz_demo_export")
    public List<DemoEntity> exportExcel(DemoEntity demo,Long[] ids) {
        return demoService.list(Wrappers.lambdaQuery(demo).in(ArrayUtil.isNotEmpty(ids), DemoEntity::getId, ids));
    }

    /**
     * 导入excel 表
     * @param demoList 对象实体列表
     * @param bindingResult 错误信息列表
     * @return ok fail
     */
    @PostMapping("/import")
    @HasPermission("pig-business-biz_demo_export")
    public R importExcel(@RequestExcel List<DemoEntity> demoList, BindingResult bindingResult) {
        return R.ok(demoService.saveBatch(demoList));
    }
}
