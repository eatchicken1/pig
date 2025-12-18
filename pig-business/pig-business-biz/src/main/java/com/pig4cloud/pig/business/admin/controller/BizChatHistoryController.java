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
import com.pig4cloud.pig.business.api.entity.BizChatHistoryEntity;
import com.pig4cloud.pig.business.admin.service.BizChatHistoryService;

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
 * 对话历史记录表
 *
 * @author taishengluo
 * @date 2025-12-18 14:48:25
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/bizChatHistory" )
@Tag(description = "bizChatHistory" , name = "对话历史记录表管理" )
@SecurityRequirement(name = HttpHeaders.AUTHORIZATION)
public class BizChatHistoryController {

    private final  BizChatHistoryService bizChatHistoryService;

    /**
     * 分页查询
     * @param page 分页对象
     * @param bizChatHistory 对话历史记录表
     * @return
     */
    @Operation(summary = "分页查询" , description = "分页查询" )
    @GetMapping("/page" )
    @HasPermission("admin_bizChatHistory_view")
    public R getBizChatHistoryPage(@ParameterObject Page page, @ParameterObject BizChatHistoryEntity bizChatHistory) {
        LambdaQueryWrapper<BizChatHistoryEntity> wrapper = Wrappers.lambdaQuery();
        return R.ok(bizChatHistoryService.page(page, wrapper));
    }


    /**
     * 通过条件查询对话历史记录表
     * @param bizChatHistory 查询条件
     * @return R  对象列表
     */
    @Operation(summary = "通过条件查询" , description = "通过条件查询对象" )
    @GetMapping("/details" )
    @HasPermission("admin_bizChatHistory_view")
    public R getDetails(@ParameterObject BizChatHistoryEntity bizChatHistory) {
        return R.ok(bizChatHistoryService.list(Wrappers.query(bizChatHistory)));
    }

    /**
     * 新增对话历史记录表
     * @param bizChatHistory 对话历史记录表
     * @return R
     */
    @Operation(summary = "新增对话历史记录表" , description = "新增对话历史记录表" )
    @SysLog("新增对话历史记录表" )
    @PostMapping
    @HasPermission("admin_bizChatHistory_add")
    public R save(@RequestBody BizChatHistoryEntity bizChatHistory) {
        return R.ok(bizChatHistoryService.save(bizChatHistory));
    }

    /**
     * 修改对话历史记录表
     * @param bizChatHistory 对话历史记录表
     * @return R
     */
    @Operation(summary = "修改对话历史记录表" , description = "修改对话历史记录表" )
    @SysLog("修改对话历史记录表" )
    @PutMapping
    @HasPermission("admin_bizChatHistory_edit")
    public R updateById(@RequestBody BizChatHistoryEntity bizChatHistory) {
        return R.ok(bizChatHistoryService.updateById(bizChatHistory));
    }

    /**
     * 通过id删除对话历史记录表
     * @param ids id列表
     * @return R
     */
    @Operation(summary = "通过id删除对话历史记录表" , description = "通过id删除对话历史记录表" )
    @SysLog("通过id删除对话历史记录表" )
    @DeleteMapping
    @HasPermission("admin_bizChatHistory_del")
    public R removeById(@RequestBody Long[] ids) {
        return R.ok(bizChatHistoryService.removeBatchByIds(CollUtil.toList(ids)));
    }


    /**
     * 导出excel 表格
     * @param bizChatHistory 查询条件
   	 * @param ids 导出指定ID
     * @return excel 文件流
     */
    @ResponseExcel
    @GetMapping("/export")
    @HasPermission("admin_bizChatHistory_export")
    public List<BizChatHistoryEntity> exportExcel(BizChatHistoryEntity bizChatHistory,Long[] ids) {
        return bizChatHistoryService.list(Wrappers.lambdaQuery(bizChatHistory).in(ArrayUtil.isNotEmpty(ids), BizChatHistoryEntity::getId, ids));
    }

    /**
     * 导入excel 表
     * @param bizChatHistoryList 对象实体列表
     * @param bindingResult 错误信息列表
     * @return ok fail
     */
    @PostMapping("/import")
    @HasPermission("admin_bizChatHistory_export")
    public R importExcel(@RequestExcel List<BizChatHistoryEntity> bizChatHistoryList, BindingResult bindingResult) {
        return R.ok(bizChatHistoryService.saveBatch(bizChatHistoryList));
    }
}
