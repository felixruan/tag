package org.etocrm.authentication.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.etocrm.authentication.entity.VO.industry.*;
import org.etocrm.authentication.service.ISysIndustryService;
import org.etocrm.core.util.ResponseVO;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

@Api(value = "行业", tags = "行业API")
@RestController
@RefreshScope
@RequestMapping("/sysIndustry")
public class SysIndustryController {

    @Resource
    private ISysIndustryService sysIndustryService;


    /**
     * 新增行业
     */
    @ApiOperation(value = "新增行业 ", notes = "新增行业 ")
    @PostMapping("/save")
    public ResponseVO save(@RequestBody @Valid SysIndustrySaveVO saveVO) {
        return sysIndustryService.saveSysIndustry(saveVO);
    }

    /**
     * 修改行业
     */
    @ApiOperation(value = "修改行业 ", notes = "修改行业 ")
    @PostMapping("/update")
    public ResponseVO update(@RequestBody @Valid SysIndustryUpdateVO sysIndustryUpdateVO) {
        return sysIndustryService.updateById(sysIndustryUpdateVO);
    }

    /**
     * 删除行业
     */
    @ApiOperation(value = "删除行业 ", notes = "删除行业 ")
    @ApiImplicitParams({@ApiImplicitParam(name = "id", value = "行业id", dataType = "Long", required = true)})
    @GetMapping("/delete/{id}")
    public ResponseVO delete(@PathVariable("id") Long id) {
        return sysIndustryService.deleteById(id);
    }


    /**
     * 查询含有标签数量的行业列表   id，name,标签数量
     */
    @ApiOperation(value = "查询含有标签数量的行业列表", notes = "查询含有标签数量的行业列表")
    @GetMapping("/getIndustryList")
    public ResponseVO<List<SysIndustryListWithTagResponseVO>> getIndustryList() {
        return sysIndustryService.getIndustryList();
    }


    /**
     * 查询行业列表   id，name
     */
    @ApiOperation(value = "查询行业列表", notes = "查询行业列表")
    @GetMapping("/getList")
    public ResponseVO<List<SysIndustryListResponseVO>> getList() {
        return sysIndustryService.getList();
    }


    /**
     * 根据id查询行业
     */
    @ApiOperation(value = "根据行业id查询行业", notes = "根据行业id查询行业")
    @ApiImplicitParams({@ApiImplicitParam(name = "id", value = "行业id", dataType = "Long", required = true)})
    @GetMapping("/getById/{id}")
    public ResponseVO<SysIndustryGetResponseVO> getById(@PathVariable("id") Long id) {
        return sysIndustryService.getById(id);
    }

}