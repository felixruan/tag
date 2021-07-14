package org.etocrm.dataManager.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.etocrm.core.util.ResponseVO;
import org.etocrm.dataManager.model.VO.column.SysColumnAddVO;
import org.etocrm.dataManager.model.VO.column.SysColumnPageVO;
import org.etocrm.dataManager.model.VO.column.SysColumnUpdateVO;
import org.etocrm.dataManager.model.VO.column.SysColumnVO;
import org.etocrm.dataManager.model.VO.dict.SysDictPageVO;
import org.etocrm.dataManager.model.VO.dict.SysDictUpdateVO;
import org.etocrm.dataManager.model.VO.dict.SysDictVO;
import org.etocrm.dataManager.service.SysColumnService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Api(value = "系统配置", tags = "系统配置")
@RestController
@RefreshScope
@RequestMapping("/sysColumn")
public class SysColumnController {

    @Autowired
    private SysColumnService sysColumnService;

    /**
     * 新增系统配置
     */
    @ApiOperation(value = "新增系统配置 ", notes = "新增系统配置 ")
    @PostMapping("/save")
    public ResponseVO save(@RequestBody @Valid SysColumnAddVO sysColumnVO){
        return sysColumnService.saveSysColumn(sysColumnVO);
    }


    /**
     * 修改系统配置
     */
    @ApiOperation(value = "修改系统配置 ", notes = "修改系统配置 ")
    @PostMapping("/update")
    public ResponseVO update(@RequestBody @Valid SysColumnUpdateVO sysColumnUpdateVO){
        return sysColumnService.updateById(sysColumnUpdateVO);
    }

    /**
     * 删除系统配置
     */
    @ApiOperation(value = "删除系统配置 ", notes = "删除系统配置 ")
    @GetMapping("/delete/{id}")
    public ResponseVO delete(@PathVariable("id") Long id){
        return sysColumnService.deleteById(id);
    }

    /**
     * 系统配置 分页列表
     */
    @ApiOperation(value = "系统配置 分页列表", notes = "系统配置 分页列表")
    @GetMapping("/list")
    public ResponseVO<List<SysColumnVO>> list(@Valid SysColumnPageVO vo){
        return sysColumnService.list(vo);
    }


    /**
     * 系统配置 列表
     */
    @ApiOperation(value = "系统配置 分页列表", notes = "系统配置 分页列表")
    @GetMapping("/listAll")
    public ResponseVO<List<SysColumnVO>> listAll(SysColumnPageVO vo){
        return sysColumnService.listAll(vo);
    }



    /**
     * 刷新系统配置进redis
     */
    @ApiOperation(value = "刷新系统配置进redis", notes = "刷新系统配置进redis")
    @GetMapping("/cache")
    public ResponseVO cache(String name){
        return sysColumnService.cache(name);
    }
}
