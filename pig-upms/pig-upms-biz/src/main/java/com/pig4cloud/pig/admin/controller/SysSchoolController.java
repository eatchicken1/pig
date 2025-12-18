package com.pig4cloud.pig.admin.controller;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pig4cloud.pig.admin.api.entity.SysSchoolEntity;
import com.pig4cloud.pig.admin.service.SysSchoolService;
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
 * 高校信息表
 *
 * @author pig
 * @date 2025-12-18 14:27:33
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/sysSchool" )
@Tag(description = "sysSchool" , name = "高校信息表管理" )
@SecurityRequirement(name = HttpHeaders.AUTHORIZATION)
public class SysSchoolController {

    private final SysSchoolService sysSchoolService;

    /**
     * 分页查询
     * @param page 分页对象
     * @param sysSchool 高校信息表
     * @return
     */
    @Operation(summary = "分页查询" , description = "分页查询" )
    @GetMapping("/page" )
    @HasPermission("admin_sysSchool_view")
    public R getSysSchoolPage(@ParameterObject Page page, @ParameterObject SysSchoolEntity sysSchool) {
        LambdaQueryWrapper<SysSchoolEntity> wrapper = Wrappers.lambdaQuery();
        return R.ok(sysSchoolService.page(page, wrapper));
    }


    /**
     * 通过条件查询高校信息表
     * @param sysSchool 查询条件
     * @return R  对象列表
     */
    @Operation(summary = "通过条件查询" , description = "通过条件查询对象" )
    @GetMapping("/details" )
    @HasPermission("admin_sysSchool_view")
    public R getDetails(@ParameterObject SysSchoolEntity sysSchool) {
        return R.ok(sysSchoolService.list(Wrappers.query(sysSchool)));
    }

    /**
     * 新增高校信息表
     * @param sysSchool 高校信息表
     * @return R
     */
    @Operation(summary = "新增高校信息表" , description = "新增高校信息表" )
    @SysLog("新增高校信息表" )
    @PostMapping
    @HasPermission("admin_sysSchool_add")
    public R save(@RequestBody SysSchoolEntity sysSchool) {
        return R.ok(sysSchoolService.save(sysSchool));
    }

    /**
     * 修改高校信息表
     * @param sysSchool 高校信息表
     * @return R
     */
    @Operation(summary = "修改高校信息表" , description = "修改高校信息表" )
    @SysLog("修改高校信息表" )
    @PutMapping
    @HasPermission("admin_sysSchool_edit")
    public R updateById(@RequestBody SysSchoolEntity sysSchool) {
        return R.ok(sysSchoolService.updateById(sysSchool));
    }

    /**
     * 通过id删除高校信息表
     * @param ids schoolId列表
     * @return R
     */
    @Operation(summary = "通过id删除高校信息表" , description = "通过id删除高校信息表" )
    @SysLog("通过id删除高校信息表" )
    @DeleteMapping
    @HasPermission("admin_sysSchool_del")
    public R removeById(@RequestBody Long[] ids) {
        return R.ok(sysSchoolService.removeBatchByIds(CollUtil.toList(ids)));
    }


    /**
     * 导出excel 表格
     * @param sysSchool 查询条件
   	 * @param ids 导出指定ID
     * @return excel 文件流
     */
    @ResponseExcel
    @GetMapping("/export")
    @HasPermission("admin_sysSchool_export")
    public List<SysSchoolEntity> exportExcel(SysSchoolEntity sysSchool,Long[] ids) {
        return sysSchoolService.list(Wrappers.lambdaQuery(sysSchool).in(ArrayUtil.isNotEmpty(ids), SysSchoolEntity::getSchoolId, ids));
    }

    /**
     * 导入excel 表
     * @param sysSchoolList 对象实体列表
     * @param bindingResult 错误信息列表
     * @return ok fail
     */
    @PostMapping("/import")
    @HasPermission("admin_sysSchool_export")
    public R importExcel(@RequestExcel List<SysSchoolEntity> sysSchoolList, BindingResult bindingResult) {
        return R.ok(sysSchoolService.saveBatch(sysSchoolList));
    }
}
