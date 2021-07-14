package org.etocrm.dataManager.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.etocrm.core.util.ResponseVO;
import org.etocrm.dataManager.model.VO.location.SysLocationPageVO;
import org.etocrm.dataManager.model.VO.location.SysLocationVO;
import org.etocrm.dataManager.service.SysLocationService;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author dkx
 * @Date 2020-09-02
 */
@Api(value = "系统国家地区表", tags = "系统国家地区表")
@RestController
@RefreshScope
@RequestMapping("/sysLocation")
public class SysLocationController {

    @Resource
    private SysLocationService sysLocationService;

    /**
     * 系统国家地区表 分页列表
     */
    @ApiOperation(value = "系统国家地区表 分页列表", notes = "系统国家地区表 分页列表")
    @PostMapping("/list")
    public ResponseVO list(@RequestBody SysLocationPageVO sysLocation) {
        return sysLocationService.list(sysLocation);
    }

    /**
     * 全查系统国家地区表 列表
     */
    @ApiOperation(value = "全查系统国家地区表 列表", notes = "全查系统国家地区表 列表")
    @PostMapping("/findAll")
    public ResponseVO findAll(@RequestBody(required = false) SysLocationVO sysLocation) {
        return sysLocationService.findAll(sysLocation);
    }

    /**
     * 系统国家地区表 详情
     */
    @ApiOperation(value = "系统国家地区表 详情", notes = "系统国家地区表 详情")
    @ApiImplicitParams({@ApiImplicitParam(name = "id", value = "取得SysLocation详情", dataType = "Long", required = true)})
    @GetMapping(value = "/detail/{id}")
    public ResponseVO<SysLocationVO> detail(@PathVariable("id") Long id) {
        return sysLocationService.detailByPk(id);
    }
}