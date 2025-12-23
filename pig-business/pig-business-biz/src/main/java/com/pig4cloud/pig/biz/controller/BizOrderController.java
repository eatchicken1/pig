package com.pig4cloud.pig.biz.controller;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pig4cloud.pig.biz.entity.BizOrderEntity;
import com.pig4cloud.pig.biz.service.BizOrderService;
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
 * 积分交易订单表
 *
 * @author luotaisheng
 * @date 2025-12-23 16:16:01
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/bizOrder" )
@Tag(description = "bizOrder" , name = "积分交易订单表管理" )
@SecurityRequirement(name = HttpHeaders.AUTHORIZATION)
public class BizOrderController {

    private final BizOrderService bizOrderService;

    /**
     * 分页查询
     * @param page 分页对象
     * @param bizOrder 积分交易订单表
     * @return
     */
    @Operation(summary = "分页查询" , description = "分页查询" )
    @GetMapping("/page" )
    @HasPermission("admin_bizOrder_view")
    public R getBizOrderPage(@ParameterObject Page page, @ParameterObject BizOrderEntity bizOrder) {
        LambdaQueryWrapper<BizOrderEntity> wrapper = Wrappers.lambdaQuery();
        return R.ok(bizOrderService.page(page, wrapper));
    }


    /**
     * 通过条件查询积分交易订单表
     * @param bizOrder 查询条件
     * @return R  对象列表
     */
    @Operation(summary = "通过条件查询" , description = "通过条件查询对象" )
    @GetMapping("/details" )
    @HasPermission("admin_bizOrder_view")
    public R getDetails(@ParameterObject BizOrderEntity bizOrder) {
        return R.ok(bizOrderService.list(Wrappers.query(bizOrder)));
    }

    /**
     * 新增积分交易订单表
     * @param bizOrder 积分交易订单表
     * @return R
     */
    @Operation(summary = "新增积分交易订单表" , description = "新增积分交易订单表" )
    @SysLog("新增积分交易订单表" )
    @PostMapping
    @HasPermission("admin_bizOrder_add")
    public R save(@RequestBody BizOrderEntity bizOrder) {
        return R.ok(bizOrderService.save(bizOrder));
    }

    /**
     * 修改积分交易订单表
     * @param bizOrder 积分交易订单表
     * @return R
     */
    @Operation(summary = "修改积分交易订单表" , description = "修改积分交易订单表" )
    @SysLog("修改积分交易订单表" )
    @PutMapping
    @HasPermission("admin_bizOrder_edit")
    public R updateById(@RequestBody BizOrderEntity bizOrder) {
        return R.ok(bizOrderService.updateById(bizOrder));
    }

    /**
     * 通过id删除积分交易订单表
     * @param ids orderId列表
     * @return R
     */
    @Operation(summary = "通过id删除积分交易订单表" , description = "通过id删除积分交易订单表" )
    @SysLog("通过id删除积分交易订单表" )
    @DeleteMapping
    @HasPermission("admin_bizOrder_del")
    public R removeById(@RequestBody Long[] ids) {
        return R.ok(bizOrderService.removeBatchByIds(CollUtil.toList(ids)));
    }


    /**
     * 导出excel 表格
     * @param bizOrder 查询条件
   	 * @param ids 导出指定ID
     * @return excel 文件流
     */
    @ResponseExcel
    @GetMapping("/export")
    @HasPermission("admin_bizOrder_export")
    public List<BizOrderEntity> exportExcel(BizOrderEntity bizOrder,Long[] ids) {
        return bizOrderService.list(Wrappers.lambdaQuery(bizOrder).in(ArrayUtil.isNotEmpty(ids), BizOrderEntity::getOrderId, ids));
    }

    /**
     * 导入excel 表
     * @param bizOrderList 对象实体列表
     * @param bindingResult 错误信息列表
     * @return ok fail
     */
    @PostMapping("/import")
    @HasPermission("admin_bizOrder_export")
    public R importExcel(@RequestExcel List<BizOrderEntity> bizOrderList, BindingResult bindingResult) {
        return R.ok(bizOrderService.saveBatch(bizOrderList));
    }
}
