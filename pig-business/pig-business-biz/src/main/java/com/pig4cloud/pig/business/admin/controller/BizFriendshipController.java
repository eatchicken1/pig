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
import com.pig4cloud.pig.business.api.entity.BizFriendshipEntity;
import com.pig4cloud.pig.business.admin.service.BizFriendshipService;

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
 * 好友关系表
 *
 * @author taishengluo
 * @date 2025-12-18 14:55:20
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/bizFriendship" )
@Tag(description = "bizFriendship" , name = "好友关系表管理" )
@SecurityRequirement(name = HttpHeaders.AUTHORIZATION)
public class BizFriendshipController {

    private final  BizFriendshipService bizFriendshipService;

    /**
     * 分页查询
     * @param page 分页对象
     * @param bizFriendship 好友关系表
     * @return
     */
    @Operation(summary = "分页查询" , description = "分页查询" )
    @GetMapping("/page" )
    @HasPermission("admin_bizFriendship_view")
    public R getBizFriendshipPage(@ParameterObject Page page, @ParameterObject BizFriendshipEntity bizFriendship) {
        LambdaQueryWrapper<BizFriendshipEntity> wrapper = Wrappers.lambdaQuery();
        return R.ok(bizFriendshipService.page(page, wrapper));
    }


    /**
     * 通过条件查询好友关系表
     * @param bizFriendship 查询条件
     * @return R  对象列表
     */
    @Operation(summary = "通过条件查询" , description = "通过条件查询对象" )
    @GetMapping("/details" )
    @HasPermission("admin_bizFriendship_view")
    public R getDetails(@ParameterObject BizFriendshipEntity bizFriendship) {
        return R.ok(bizFriendshipService.list(Wrappers.query(bizFriendship)));
    }

    /**
     * 新增好友关系表
     * @param bizFriendship 好友关系表
     * @return R
     */
    @Operation(summary = "新增好友关系表" , description = "新增好友关系表" )
    @SysLog("新增好友关系表" )
    @PostMapping
    @HasPermission("admin_bizFriendship_add")
    public R save(@RequestBody BizFriendshipEntity bizFriendship) {
        return R.ok(bizFriendshipService.save(bizFriendship));
    }

    /**
     * 修改好友关系表
     * @param bizFriendship 好友关系表
     * @return R
     */
    @Operation(summary = "修改好友关系表" , description = "修改好友关系表" )
    @SysLog("修改好友关系表" )
    @PutMapping
    @HasPermission("admin_bizFriendship_edit")
    public R updateById(@RequestBody BizFriendshipEntity bizFriendship) {
        return R.ok(bizFriendshipService.updateById(bizFriendship));
    }

    /**
     * 通过id删除好友关系表
     * @param ids id列表
     * @return R
     */
    @Operation(summary = "通过id删除好友关系表" , description = "通过id删除好友关系表" )
    @SysLog("通过id删除好友关系表" )
    @DeleteMapping
    @HasPermission("admin_bizFriendship_del")
    public R removeById(@RequestBody Long[] ids) {
        return R.ok(bizFriendshipService.removeBatchByIds(CollUtil.toList(ids)));
    }


    /**
     * 导出excel 表格
     * @param bizFriendship 查询条件
   	 * @param ids 导出指定ID
     * @return excel 文件流
     */
    @ResponseExcel
    @GetMapping("/export")
    @HasPermission("admin_bizFriendship_export")
    public List<BizFriendshipEntity> exportExcel(BizFriendshipEntity bizFriendship,Long[] ids) {
        return bizFriendshipService.list(Wrappers.lambdaQuery(bizFriendship).in(ArrayUtil.isNotEmpty(ids), BizFriendshipEntity::getId, ids));
    }

    /**
     * 导入excel 表
     * @param bizFriendshipList 对象实体列表
     * @param bindingResult 错误信息列表
     * @return ok fail
     */
    @PostMapping("/import")
    @HasPermission("admin_bizFriendship_export")
    public R importExcel(@RequestExcel List<BizFriendshipEntity> bizFriendshipList, BindingResult bindingResult) {
        return R.ok(bizFriendshipService.saveBatch(bizFriendshipList));
    }
}
