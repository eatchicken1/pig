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
import com.pig4cloud.pig.biz.entity.BizMatchRecordEntity;
import com.pig4cloud.pig.biz.service.BizMatchRecordService;

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
 * AI匹配相亲记录表
 *
 * @author pig
 * @date 2025-12-18 14:57:00
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/bizMatchRecord" )
@Tag(description = "bizMatchRecord" , name = "AI匹配相亲记录表管理" )
@SecurityRequirement(name = HttpHeaders.AUTHORIZATION)
public class BizMatchRecordController {

    private final  BizMatchRecordService bizMatchRecordService;

    /**
     * 分页查询
     * @param page 分页对象
     * @param bizMatchRecord AI匹配相亲记录表
     * @return
     */
    @Operation(summary = "分页查询" , description = "分页查询" )
    @GetMapping("/page" )
    @HasPermission("admin_bizMatchRecord_view")
    public R getBizMatchRecordPage(@ParameterObject Page page, @ParameterObject BizMatchRecordEntity bizMatchRecord) {
        LambdaQueryWrapper<BizMatchRecordEntity> wrapper = Wrappers.lambdaQuery();
        return R.ok(bizMatchRecordService.page(page, wrapper));
    }


    /**
     * 通过条件查询AI匹配相亲记录表
     * @param bizMatchRecord 查询条件
     * @return R  对象列表
     */
    @Operation(summary = "通过条件查询" , description = "通过条件查询对象" )
    @GetMapping("/details" )
    @HasPermission("admin_bizMatchRecord_view")
    public R getDetails(@ParameterObject BizMatchRecordEntity bizMatchRecord) {
        return R.ok(bizMatchRecordService.list(Wrappers.query(bizMatchRecord)));
    }

    /**
     * 新增AI匹配相亲记录表
     * @param bizMatchRecord AI匹配相亲记录表
     * @return R
     */
    @Operation(summary = "新增AI匹配相亲记录表" , description = "新增AI匹配相亲记录表" )
    @SysLog("新增AI匹配相亲记录表" )
    @PostMapping
    @HasPermission("admin_bizMatchRecord_add")
    public R save(@RequestBody BizMatchRecordEntity bizMatchRecord) {
        return R.ok(bizMatchRecordService.save(bizMatchRecord));
    }

    /**
     * 修改AI匹配相亲记录表
     * @param bizMatchRecord AI匹配相亲记录表
     * @return R
     */
    @Operation(summary = "修改AI匹配相亲记录表" , description = "修改AI匹配相亲记录表" )
    @SysLog("修改AI匹配相亲记录表" )
    @PutMapping
    @HasPermission("admin_bizMatchRecord_edit")
    public R updateById(@RequestBody BizMatchRecordEntity bizMatchRecord) {
        return R.ok(bizMatchRecordService.updateById(bizMatchRecord));
    }

    /**
     * 通过id删除AI匹配相亲记录表
     * @param ids matchId列表
     * @return R
     */
    @Operation(summary = "通过id删除AI匹配相亲记录表" , description = "通过id删除AI匹配相亲记录表" )
    @SysLog("通过id删除AI匹配相亲记录表" )
    @DeleteMapping
    @HasPermission("admin_bizMatchRecord_del")
    public R removeById(@RequestBody Long[] ids) {
        return R.ok(bizMatchRecordService.removeBatchByIds(CollUtil.toList(ids)));
    }


    /**
     * 导出excel 表格
     * @param bizMatchRecord 查询条件
   	 * @param ids 导出指定ID
     * @return excel 文件流
     */
    @ResponseExcel
    @GetMapping("/export")
    @HasPermission("admin_bizMatchRecord_export")
    public List<BizMatchRecordEntity> exportExcel(BizMatchRecordEntity bizMatchRecord,Long[] ids) {
        return bizMatchRecordService.list(Wrappers.lambdaQuery(bizMatchRecord).in(ArrayUtil.isNotEmpty(ids), BizMatchRecordEntity::getMatchId, ids));
    }

    /**
     * 导入excel 表
     * @param bizMatchRecordList 对象实体列表
     * @param bindingResult 错误信息列表
     * @return ok fail
     */
    @PostMapping("/import")
    @HasPermission("admin_bizMatchRecord_export")
    public R importExcel(@RequestExcel List<BizMatchRecordEntity> bizMatchRecordList, BindingResult bindingResult) {
        return R.ok(bizMatchRecordService.saveBatch(bizMatchRecordList));
    }
}
