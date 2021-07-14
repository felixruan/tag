package org.etocrm.dataManager.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.etocrm.core.util.ResponseVO;
import org.etocrm.dataManager.model.VO.dict.*;
import org.etocrm.dataManager.service.SysDictService;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

/**
 *
 * @author dkx
 * @Date 2020-09-01
 */
@Api(value = "系统字典", tags = "系统字典")
@RestController
@RefreshScope
@RequestMapping("/sysDict")
public class SysDictController {

    @Resource
    private SysDictService sysDictService;

    /**
     * 系统字典 分页列表
     */
    @ApiOperation(value = "系统字典 分页列表", notes = "系统字典 分页列表")
    @PostMapping("/list")
    public ResponseVO<List<SysDictVO>> list(@RequestBody SysDictPageVO vo){
        return sysDictService.list(vo);
    }

    /**
     * tag  feign 有调用
     * 全查系统字典 列表
     */
    @ApiOperation(value = "全查系统字典 列表", notes = "全查系统字典 列表")
    @PostMapping("/findAll")
    public ResponseVO<List<SysDictVO>> findAll(@RequestBody DictFindAllVO dictFindAllVO){
        return sysDictService.findAll(dictFindAllVO);
    }

    /**
     * 新增系统字典
     */
    @ApiOperation(value = "新增系统字典 ", notes = "新增系统字典 ")
    @PostMapping("/save")
    public ResponseVO save(@RequestBody SysDictAddVO sysDictAddVO){
        return sysDictService.saveSysDict(sysDictAddVO);
    }

    /**
     * 修改系统字典
     */
    @ApiOperation(value = "修改系统字典 ", notes = "修改系统字典 ")
    @PostMapping("/update")
    public ResponseVO update(@RequestBody @Valid SysDictUpdateVO sysDictUpdateVO){
        return sysDictService.updateByPk(sysDictUpdateVO);
    }

    /**
     * 删除系统字典
     */
    @ApiOperation(value = "删除系统字典 ", notes = "删除系统字典 ")
    @ApiImplicitParams({@ApiImplicitParam(name = "id", value = "删除SysDict", dataType = "Long", required = true)})
    @GetMapping("/delete/{id}")
    public ResponseVO delete(@PathVariable("id") Long id){
        return sysDictService.deleteByPk(id);
    }

    /**
     * 系统字典 详情
     */
    @ApiOperation(value = "系统字典 详情", notes = "系统字典 详情")
    @ApiImplicitParams({@ApiImplicitParam(name = "id", value = "取得SysDict详情", dataType = "Long", required = true)})
    @GetMapping(value = "/detail/{id}")
    public ResponseVO<SysDictVO> detail(@PathVariable("id") Long id){
        return sysDictService.detailByPk(id);
        }


    /**
     * 修改字典的状态
     */
    @ApiOperation(value = "修改字典的状态", notes = "修改字典的状态")
    @PostMapping(value = "/updateStatusById")
    public ResponseVO updateStatusById(@RequestBody @Valid UpdateStatusVO updateStatusVO ){
        return sysDictService.updateStatusById(updateStatusVO);
    }


    /**
     * 根据数据字典多个id查询详情
     */
    @ApiOperation(value = "根据数据字典多个id查询详情", notes = "根据数据字典多个id查询详情")
    @GetMapping(value = "getDetailByIds")
    public ResponseVO<List<SysDictVO>> getDetailByIds(@RequestBody List<Long> batchId){
        return sysDictService.getDetailByIds(batchId);
    }

    /**
     * tag  feign 有调用
     * 根据字典id查询字典以及子节点数据
     */
    @ApiOperation(value = "根据字典id查询字典以及子节点数据", notes = "根据字典id查询字典以及子节点数据")
    @GetMapping(value = "getByIdWithChild/{id}")
    public ResponseVO<List<SysDictVO>> getDictByIdWithChild(@PathVariable Long id){
        return sysDictService.getByIdWithChild(id);
    }
}