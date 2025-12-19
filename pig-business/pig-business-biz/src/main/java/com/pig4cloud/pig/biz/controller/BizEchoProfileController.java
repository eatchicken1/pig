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
import com.pig4cloud.pig.biz.entity.BizEchoProfileEntity;
import com.pig4cloud.pig.biz.service.BizEchoProfileService;

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
 * Echo数字分身配置表
 *
 * @author taishengluo
 * @date 2025-12-18 14:53:32
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/bizEchoProfile" )
@Tag(description = "bizEchoProfile" , name = "Echo数字分身配置表管理" )
@SecurityRequirement(name = HttpHeaders.AUTHORIZATION)
public class BizEchoProfileController {

    private final  BizEchoProfileService bizEchoProfileService;

    /**
     * 分页查询
     * @param page 分页对象
     * @param bizEchoProfile Echo数字分身配置表
     * @return
     */
    @Operation(summary = "分页查询" , description = "分页查询" )
    @GetMapping("/page" )
    @HasPermission("admin_bizEchoProfile_view")
    public R getBizEchoProfilePage(@ParameterObject Page page, @ParameterObject BizEchoProfileEntity bizEchoProfile) {
        LambdaQueryWrapper<BizEchoProfileEntity> wrapper = Wrappers.lambdaQuery();
        return R.ok(bizEchoProfileService.page(page, wrapper));
    }


    /**
     * 通过条件查询Echo数字分身配置表
     * @param bizEchoProfile 查询条件
     * @return R  对象列表
     */
    @Operation(summary = "通过条件查询" , description = "通过条件查询对象" )
    @GetMapping("/details" )
    @HasPermission("admin_bizEchoProfile_view")
    public R getDetails(@ParameterObject BizEchoProfileEntity bizEchoProfile) {
        return R.ok(bizEchoProfileService.list(Wrappers.query(bizEchoProfile)));
    }

    /**
     * 新增Echo数字分身配置表
     * @param bizEchoProfile Echo数字分身配置表
     * @return R
     */
    @Operation(summary = "新增Echo数字分身配置表" , description = "新增Echo数字分身配置表" )
    @SysLog("新增Echo数字分身配置表" )
    @PostMapping
    @HasPermission("admin_bizEchoProfile_add")
    public R save(@RequestBody BizEchoProfileEntity bizEchoProfile) {
        return R.ok(bizEchoProfileService.save(bizEchoProfile));
    }

    /**
     * 修改Echo数字分身配置表
     * @param bizEchoProfile Echo数字分身配置表
     * @return R
     */
    @Operation(summary = "修改Echo数字分身配置表" , description = "修改Echo数字分身配置表" )
    @SysLog("修改Echo数字分身配置表" )
    @PutMapping
    @HasPermission("admin_bizEchoProfile_edit")
    public R updateById(@RequestBody BizEchoProfileEntity bizEchoProfile) {
        return R.ok(bizEchoProfileService.updateById(bizEchoProfile));
    }

    /**
     * 通过id删除Echo数字分身配置表
     * @param ids echoId列表
     * @return R
     */
    @Operation(summary = "通过id删除Echo数字分身配置表" , description = "通过id删除Echo数字分身配置表" )
    @SysLog("通过id删除Echo数字分身配置表" )
    @DeleteMapping
    @HasPermission("admin_bizEchoProfile_del")
    public R removeById(@RequestBody Long[] ids) {
        return R.ok(bizEchoProfileService.removeBatchByIds(CollUtil.toList(ids)));
    }


    /**
     * 导出excel 表格
     * @param bizEchoProfile 查询条件
   	 * @param ids 导出指定ID
     * @return excel 文件流
     */
    @ResponseExcel
    @GetMapping("/export")
    @HasPermission("admin_bizEchoProfile_export")
    public List<BizEchoProfileEntity> exportExcel(BizEchoProfileEntity bizEchoProfile,Long[] ids) {
        return bizEchoProfileService.list(Wrappers.lambdaQuery(bizEchoProfile).in(ArrayUtil.isNotEmpty(ids), BizEchoProfileEntity::getEchoId, ids));
    }

    /**
     * 导入excel 表
     * @param bizEchoProfileList 对象实体列表
     * @param bindingResult 错误信息列表
     * @return ok fail
     */
    @PostMapping("/import")
    @HasPermission("admin_bizEchoProfile_export")
    public R importExcel(@RequestExcel List<BizEchoProfileEntity> bizEchoProfileList, BindingResult bindingResult) {
        return R.ok(bizEchoProfileService.saveBatch(bizEchoProfileList));
    }
}
