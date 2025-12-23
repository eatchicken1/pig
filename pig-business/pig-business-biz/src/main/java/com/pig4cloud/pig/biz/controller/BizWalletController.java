package com.pig4cloud.pig.biz.controller;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pig4cloud.pig.biz.entity.BizWalletEntity;
import com.pig4cloud.pig.biz.service.BizWalletService;
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
 * 用户积分钱包表
 *
 * @author luotaisheng
 * @date 2025-12-23 16:15:05
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/bizWallet" )
@Tag(description = "bizWallet" , name = "用户积分钱包表管理" )
@SecurityRequirement(name = HttpHeaders.AUTHORIZATION)
public class BizWalletController {

    private final BizWalletService bizWalletService;

    /**
     * 分页查询
     * @param page 分页对象
     * @param bizWallet 用户积分钱包表
     * @return
     */
    @Operation(summary = "分页查询" , description = "分页查询" )
    @GetMapping("/page" )
    @HasPermission("admin_bizWallet_view")
    public R getBizWalletPage(@ParameterObject Page page, @ParameterObject BizWalletEntity bizWallet) {
        LambdaQueryWrapper<BizWalletEntity> wrapper = Wrappers.lambdaQuery();
        return R.ok(bizWalletService.page(page, wrapper));
    }


    /**
     * 通过条件查询用户积分钱包表
     * @param bizWallet 查询条件
     * @return R  对象列表
     */
    @Operation(summary = "通过条件查询" , description = "通过条件查询对象" )
    @GetMapping("/details" )
    @HasPermission("admin_bizWallet_view")
    public R getDetails(@ParameterObject BizWalletEntity bizWallet) {
        return R.ok(bizWalletService.list(Wrappers.query(bizWallet)));
    }

    /**
     * 新增用户积分钱包表
     * @param bizWallet 用户积分钱包表
     * @return R
     */
    @Operation(summary = "新增用户积分钱包表" , description = "新增用户积分钱包表" )
    @SysLog("新增用户积分钱包表" )
    @PostMapping
    @HasPermission("admin_bizWallet_add")
    public R save(@RequestBody BizWalletEntity bizWallet) {
        return R.ok(bizWalletService.save(bizWallet));
    }

    /**
     * 修改用户积分钱包表
     * @param bizWallet 用户积分钱包表
     * @return R
     */
    @Operation(summary = "修改用户积分钱包表" , description = "修改用户积分钱包表" )
    @SysLog("修改用户积分钱包表" )
    @PutMapping
    @HasPermission("admin_bizWallet_edit")
    public R updateById(@RequestBody BizWalletEntity bizWallet) {
        return R.ok(bizWalletService.updateById(bizWallet));
    }

    /**
     * 通过id删除用户积分钱包表
     * @param ids userId列表
     * @return R
     */
    @Operation(summary = "通过id删除用户积分钱包表" , description = "通过id删除用户积分钱包表" )
    @SysLog("通过id删除用户积分钱包表" )
    @DeleteMapping
    @HasPermission("admin_bizWallet_del")
    public R removeById(@RequestBody Long[] ids) {
        return R.ok(bizWalletService.removeBatchByIds(CollUtil.toList(ids)));
    }


    /**
     * 导出excel 表格
     * @param bizWallet 查询条件
   	 * @param ids 导出指定ID
     * @return excel 文件流
     */
    @ResponseExcel
    @GetMapping("/export")
    @HasPermission("admin_bizWallet_export")
    public List<BizWalletEntity> exportExcel(BizWalletEntity bizWallet,Long[] ids) {
        return bizWalletService.list(Wrappers.lambdaQuery(bizWallet).in(ArrayUtil.isNotEmpty(ids), BizWalletEntity::getUserId, ids));
    }

    /**
     * 导入excel 表
     * @param bizWalletList 对象实体列表
     * @param bindingResult 错误信息列表
     * @return ok fail
     */
    @PostMapping("/import")
    @HasPermission("admin_bizWallet_export")
    public R importExcel(@RequestExcel List<BizWalletEntity> bizWalletList, BindingResult bindingResult) {
        return R.ok(bizWalletService.saveBatch(bizWalletList));
    }
}
