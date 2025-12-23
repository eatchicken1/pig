package com.pig4cloud.pig.biz.controller;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pig4cloud.pig.biz.entity.BizWalletLogEntity;
import com.pig4cloud.pig.biz.service.BizWalletLogService;
import com.pig4cloud.pig.common.core.util.R;
import com.pig4cloud.pig.common.log.annotation.SysLog;
import com.pig4cloud.plugin.excel.annotation.ResponseExcel;
import com.pig4cloud.plugin.excel.annotation.RequestExcel;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import com.pig4cloud.pig.common.security.annotation.HasPermission;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpHeaders;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

/**
 * 钱包变动流水表
 *
 * @author luotaisheng
 * @date 2025-12-23 16:15:23
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/bizWalletLog" )
@Tag(description = "bizWalletLog" , name = "钱包变动流水表管理" )
@SecurityRequirement(name = HttpHeaders.AUTHORIZATION)
public class BizWalletLogController {

    private final BizWalletLogService bizWalletLogService;

    /**
     * 分页查询
     * @param page 分页对象
     * @param bizWalletLog 钱包变动流水表
     * @return
     */
    @Operation(summary = "分页查询" , description = "分页查询" )
    @GetMapping("/page" )
    @HasPermission("admin_bizWalletLog_view")
    public R getBizWalletLogPage(@ParameterObject Page page, @ParameterObject BizWalletLogEntity bizWalletLog) {
        LambdaQueryWrapper<BizWalletLogEntity> wrapper = Wrappers.lambdaQuery();
        return R.ok(bizWalletLogService.page(page, wrapper));
    }


    /**
     * 通过条件查询钱包变动流水表
     * @param bizWalletLog 查询条件
     * @return R  对象列表
     */
    @Operation(summary = "通过条件查询" , description = "通过条件查询对象" )
    @GetMapping("/details" )
    @HasPermission("admin_bizWalletLog_view")
    public R getDetails(@ParameterObject BizWalletLogEntity bizWalletLog) {
        return R.ok(bizWalletLogService.list(Wrappers.query(bizWalletLog)));
    }

    /**
     * 新增钱包变动流水表
     * @param bizWalletLog 钱包变动流水表
     * @return R
     */
    @Operation(summary = "新增钱包变动流水表" , description = "新增钱包变动流水表" )
    @SysLog("新增钱包变动流水表" )
    @PostMapping
    @HasPermission("admin_bizWalletLog_add")
    public R save(@RequestBody BizWalletLogEntity bizWalletLog) {
        return R.ok(bizWalletLogService.save(bizWalletLog));
    }

    /**
     * 修改钱包变动流水表
     * @param bizWalletLog 钱包变动流水表
     * @return R
     */
    @Operation(summary = "修改钱包变动流水表" , description = "修改钱包变动流水表" )
    @SysLog("修改钱包变动流水表" )
    @PutMapping
    @HasPermission("admin_bizWalletLog_edit")
    public R updateById(@RequestBody BizWalletLogEntity bizWalletLog) {
        return R.ok(bizWalletLogService.updateById(bizWalletLog));
    }

    /**
     * 通过id删除钱包变动流水表
     * @param ids id列表
     * @return R
     */
    @Operation(summary = "通过id删除钱包变动流水表" , description = "通过id删除钱包变动流水表" )
    @SysLog("通过id删除钱包变动流水表" )
    @DeleteMapping
    @HasPermission("admin_bizWalletLog_del")
    public R removeById(@RequestBody Long[] ids) {
        return R.ok(bizWalletLogService.removeBatchByIds(CollUtil.toList(ids)));
    }


    /**
     * 导出excel 表格
     * @param bizWalletLog 查询条件
   	 * @param ids 导出指定ID
     * @return excel 文件流
     */
    @ResponseExcel
    @GetMapping("/export")
    @HasPermission("admin_bizWalletLog_export")
    public List<BizWalletLogEntity> exportExcel(BizWalletLogEntity bizWalletLog,Long[] ids) {
        return bizWalletLogService.list(Wrappers.lambdaQuery(bizWalletLog).in(ArrayUtil.isNotEmpty(ids), BizWalletLogEntity::getId, ids));
    }

    /**
     * 导入excel 表
     * @param bizWalletLogList 对象实体列表
     * @param bindingResult 错误信息列表
     * @return ok fail
     */
    @PostMapping("/import")
    @HasPermission("admin_bizWalletLog_export")
    public R importExcel(@RequestExcel List<BizWalletLogEntity> bizWalletLogList, BindingResult bindingResult) {
        return R.ok(bizWalletLogService.saveBatch(bizWalletLogList));
    }
}
