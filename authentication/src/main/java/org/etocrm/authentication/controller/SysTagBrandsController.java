package org.etocrm.authentication.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.etocrm.authentication.entity.VO.tagBrands.SysTagBrandsAddVO;
import org.etocrm.authentication.entity.VO.tagBrands.SysTagBrandsVO;
import org.etocrm.authentication.entity.VO.tagBrands.SysTagClassesTreeVO;
import org.etocrm.authentication.service.SysTagBrandsService;
import org.etocrm.core.util.ResponseVO;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author dkx
 * @Date 2020-09-10
 */
@Api(value = "标签品牌", tags = "标签品牌")
@RestController
@RefreshScope
@RequestMapping("/sysTagBrands")
public class SysTagBrandsController {

    @Resource
    private SysTagBrandsService sysTagBrandsService;


    /**
     * 新增标签品牌
     */
    @ApiOperation(value = "新增标签品牌", notes = "新增标签品牌")
    @PostMapping("/save")
    public ResponseVO save(@RequestBody @Validated SysTagBrandsAddVO sysTagBrands) {
        return sysTagBrandsService.saveSysTagBrands(sysTagBrands);
    }


    /**
     * 标签品牌详情
     */
    @ApiOperation(value = "标签品牌详情", notes = "标签品牌详情")
    @PostMapping(value = "/detailByBrandId")
    public ResponseVO<List<SysTagClassesTreeVO>> detailByBrandId(@RequestBody SysTagBrandsVO sysTagBrandsVO) {
        return sysTagBrandsService.detailByBrandId(sysTagBrandsVO);
    }
}