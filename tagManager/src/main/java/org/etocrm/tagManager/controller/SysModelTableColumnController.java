package org.etocrm.tagManager.controller;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.etocrm.core.util.ResponseVO;
import org.etocrm.tagManager.model.VO.ModelTable.AddSysModelTableColumnVO;
import org.etocrm.tagManager.model.VO.ModelTable.ListPageSysModelTableColumnVO;
import org.etocrm.tagManager.model.VO.ModelTable.SysModelTableColumnVO;
import org.etocrm.tagManager.model.VO.ModelTable.UpdateSysModelTableColumnVO;
import org.etocrm.tagManager.service.ISysModelTableColumnService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;


@Api(tags = "系统模型字段")
@RestController
@RefreshScope
@RequestMapping("/sysModelTableColumn")
public class SysModelTableColumnController {

    @Autowired
    ISysModelTableColumnService sysModelTableColumnService;

    /**
     * 添加系统模型字段
     * @param sysModelTableColumnVO
     * @return
     */
    @ApiOperation(value = "添加系统模型字段",notes = "添加系统模型字段")
    @PostMapping("/saveSysModelColumnTable")
    public ResponseVO saveSysModelColumnTable(@RequestBody @Valid AddSysModelTableColumnVO sysModelTableColumnVO){
        return sysModelTableColumnService.saveSysModelColumnTable(sysModelTableColumnVO);
    }


    /**
     * 修改系统模型字段根据id
     * @param sysModelTableColumnVO
     * @return
     */
    @ApiOperation(value = "修改系统模型字段根据id",notes = "修改系统模型字段根据id")
    @PostMapping("/updateSysModelColumnTableById")
    public ResponseVO updateSysModelColumnTableById(@RequestBody @Valid UpdateSysModelTableColumnVO sysModelTableColumnVO){
        return sysModelTableColumnService.updateSysModelColumnTableById(sysModelTableColumnVO);
    }


    /**
     * 根据ID查询系统模型字段
     * @param listPageSysModelTableColumnVO
     * @return
     */
    @ApiOperation(value = "根据ID查询系统模型字段",notes = "根据ID查询系统模型字段")
    @GetMapping("/getSysModelColumnTableById/{id}")
    public ResponseVO getSysModelColumnTableById(ListPageSysModelTableColumnVO listPageSysModelTableColumnVO){
        return sysModelTableColumnService.getSysModelColumnTableById(listPageSysModelTableColumnVO);
    }


    /**
     * 根据模型表ID查询系统模型字段(标签管理编辑标签调用)
     * @param id
     * @return
     */
    @ApiOperation(value = "根据ID查询系统模型字段",notes = "根据ID查询系统模型字段")
    @GetMapping("/getTagSysModelColumnTableById/{id}")
    public ResponseVO getTagSysModelColumnTableById(@PathVariable Long id){
        return sysModelTableColumnService.getTagSysModelColumnTableById(id);
    }


    /**
     * 根据ID查询系统模型字段（标签管理编辑标签调用 两种标签数据类型：动态数据类型）
     * @param id
     * @return
     */
    @ApiOperation(value = "根据ID查询系统模型字段",notes = "根据ID查询系统模型字段")
    @GetMapping("/getSysModelColumnTableDynamicListById/{id}")
    public ResponseVO getSysModelColumnTableDynamicListById(@PathVariable Long id){
        return sysModelTableColumnService.getSysModelColumnTableDynamicListById(id);
    }


    /**
     * 根据ID查询系统模型字段（标签管理编辑标签调用 两种标签数据类型：关联数据类型）
     * @param id
     * @return
     */
    @ApiOperation(value = "根据ID查询系统模型字段",notes = "根据ID查询系统模型字段")
    @GetMapping("/getSysModelColumnTableRelationListById/{id}")
    public ResponseVO getSysModelColumnTableRelationListById(@PathVariable Long id){
        return sysModelTableColumnService.getSysModelColumnTableRelationListById(id);
    }

    /**
     * 查询所有系统模型字段
     * @return
     */
    @ApiOperation(value = "查询所有系统模型字段",notes = "查询所有系统模型字段")
    @GetMapping("/getSysModelColumnTableListAll")
    public ResponseVO getSysModelColumnTableListAll(){
        return sysModelTableColumnService.getSysModelColumnTableListAll();
    }

    /**
     * 根据模型id查询下面所有字段
     * @return
     */
    @ApiOperation(value = "根据模型id查询下面所有字段",notes = "根据模型id查询下面所有字段")
    @GetMapping("/getSysModelColumnTableListAllById/{id}")
    public ResponseVO getSysModelColumnTableListAllById(@PathVariable Long id){
        return sysModelTableColumnService.getSysModelColumnTableListAllById(id);
    }



    /**
     * 分页查询所有系统模型字段
     * @param sysModelTableColumnVO
     * @return
     */
    @ApiOperation(value = "分页查询所有系统模型字段",notes = "分页查询所有系统模型字段")
    @GetMapping("/getSysModelColumnTableListAllByPage")
    public ResponseVO getSysModelColumnTableListAllByPage(@Valid ListPageSysModelTableColumnVO sysModelTableColumnVO){
        return sysModelTableColumnService.getSysModelColumnTableListAllByPage(sysModelTableColumnVO);
    }


    /**
     * 条件查询系统模型字段
     * @param sysModelTableColumnVO
     * @return
     */
//    @ApiOperation(value = "条件查询系统模型字段",notes = "条件查询系统模型字段")
    @GetMapping("/getSysModelColumnTableListByParam")
    public ResponseVO getSysModelColumnTableListByParam(SysModelTableColumnVO sysModelTableColumnVO){
        return sysModelTableColumnService.getSysModelColumnTableListByParam(sysModelTableColumnVO);
    }


    /**
     * 分页条件查询系统模型数据字段
     * @param sysModelTableColumnVO
     * @return
     */
//    @ApiOperation(value = "分页条件查询系统模型数据字段",notes = "分页条件查询系统模型数据字段")
    @GetMapping("/getSysModelColumnTableListByParamByPage")
    public ResponseVO getSysModelColumnTableListByParamByPage(SysModelTableColumnVO sysModelTableColumnVO){
        return sysModelTableColumnService.getSysModelColumnTableListByParamByPage(sysModelTableColumnVO);
    }


    /**
     * 删除系统模型字段根据id
     * @param id
     * @return
     */
    @GetMapping("/delete/{id}")
    @ApiOperation(value = "删除系统模型字段根据id",notes = "删除系统模型字段根据id")
    @ApiImplicitParam(paramType="path", name = "id", value = "系统模型字段id", required = true, dataType = "Long")
    public ResponseVO deleteById(@PathVariable Long id){
        return sysModelTableColumnService.deleteById(id);
    }

}
