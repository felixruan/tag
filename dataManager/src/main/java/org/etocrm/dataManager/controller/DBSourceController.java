package org.etocrm.dataManager.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.etocrm.core.util.ResponseVO;
import org.etocrm.dataManager.model.VO.dbSource.DbSourceGetInfoVO;
import org.etocrm.dataManager.model.VO.dbSource.SysDbSourceVO;
import org.etocrm.dataManager.service.SysDbSourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(value = "系统数据源详细 ",tags = "系统数据源详细")
@RestController
@RefreshScope
@RequestMapping("/dbSource")
public class DBSourceController {
    @Autowired
    SysDbSourceService sysDbSourceService;

    /**
     * 增加系统数据源详细数据
     * @param sysDbSourceVO
     * @return
     */
    @ApiOperation(value = "增加系统数据源详细数据", notes = "增加系统数据源详细数据" )
    @PostMapping("/addSysDBSource")
    public ResponseVO addSysDBSource(@RequestBody SysDbSourceVO sysDbSourceVO){
        return sysDbSourceService.addSysDBSource(sysDbSourceVO);
    }


    /**
     * 查询系统数据源详细数据
     * @return
     */
    @ApiOperation(value = "查询系统数据源详细数据", notes = "查询系统数据源详细数据" )
    @GetMapping("/getSysDBSourceListAll")
    public ResponseVO<List<SysDbSourceVO>> getSysDBSourceListAll(){
        return sysDbSourceService.getSysDBSourceListAll();
    }

    /**
     * 查询系统数据源详细数据根据id
     * @return
     */
    @ApiOperation(value = "查询系统数据源详细数据根据id", notes = "查询系统数据源详细数据根据id" )
    @ApiImplicitParams({@ApiImplicitParam(name = "id", value = "取得SysDBSource详情", dataType = "Long", required = true)})
    @GetMapping("/getSysDBSourceById/{id}")
    public ResponseVO<SysDbSourceVO> getSysDBSourceById(@PathVariable Long id){
        return sysDbSourceService.getSysDBSourceById(id);
    }

    /**
     * 查询系统数据源详细数据分页
     * @return
     */
    @ApiOperation(value = "查询系统数据源详细数据分页", notes = "查询系统数据源详细数据分页" )
    @GetMapping("/getSysDBSourceListAllByPage")
    public ResponseVO getSysDBSourceListAllByPage(DbSourceGetInfoVO dbSourceGetInfoVO){
        return sysDbSourceService.getSysDBSourceListAllByPage(dbSourceGetInfoVO);
    }

    @ApiOperation(value = "通过数据源参数获取对应数据源详细信息", notes = "通过数据源参数获取对应数据源详细信息" )
    @GetMapping("/getSysDBSourceByParam")
    public ResponseVO<List<SysDbSourceVO>> getSysDBSourceByParam(DbSourceGetInfoVO dbSourceGetInfoVO){
        return sysDbSourceService.getSysDBSourceByParam(dbSourceGetInfoVO);
    }

    /**
     * 修改系统数据源详细数据
     */
    @ApiOperation(value = "修改系统数据源详细数据", notes = "修改系统数据源详细数据" )
    @PostMapping("/updateSysDBSource")
    public ResponseVO updateSysDBSource(@RequestBody SysDbSourceVO sysDbSourceVO) {
        return sysDbSourceService.updateSysDBSource(sysDbSourceVO);
    }


    /**
     * 根据ID删除系统数据源详细数据信息
     */
    @ApiOperation(value = "根据ID删除系统数据源详细数据信息", notes = "根据ID删除系统数据源详细数据信息" )
    @ApiImplicitParams({@ApiImplicitParam(name = "id", value = "删除SysDBSource", dataType = "Long", required = true)})
    @GetMapping("/deleteSysDBSource/{id}")
    public ResponseVO deleteSysDBSource(@PathVariable Long id) {
        return sysDbSourceService.deleteSysDBSource(id);
    }
}
