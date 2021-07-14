package org.etocrm.authentication.controller;

import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.etocrm.authentication.entity.VO.org.*;
import org.etocrm.authentication.service.ISysBrandsOrgService;
import org.etocrm.core.util.ResponseVO;
import org.etocrm.dynamicDataSource.util.BasePage;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

/**
 * @author lingshuang.pang
 * @Date 2020-09-01
 */
@Api(value = "机构", tags = "2期有修改==== 机构管理API")
@RestController
@RefreshScope
@RequestMapping("/sysBrandsOrg")
public class SysBrandsOrgController {

    @Resource
    private ISysBrandsOrgService sysBrandsOrgService;


    /**
     * 新增机构
     */
    @ApiOperation(value = "2期有修改==== 新增机构 ", notes = "新增机构 ")
    @ApiOperationSupport(order = 1)
    @PostMapping("/save")
    public ResponseVO saveBrandsOrg(@Valid @RequestBody SysBrandsOrgSaveVO saveVO) {
        return sysBrandsOrgService.saveSysBrandsOrg(saveVO);
    }

    /**
     * 修改机构
     */
    @ApiOperation(value = "2期有修改==== 修改机构 ", notes = "修改机构 ")
    @ApiOperationSupport(order = 2)
    @PostMapping("/update")
    public ResponseVO update(@Valid @RequestBody SysBrandsOrgUpdateVO updateVO) {
        return sysBrandsOrgService.updateById(updateVO);
    }


    /**
     * 删除机构
     */
    @ApiOperation(value = "删除机构 ", notes = "删除机构 ")
    @ApiOperationSupport(order = 3)
    @ApiImplicitParams({@ApiImplicitParam(name = "id", value = "机构id", dataType = "Long", required = true)})
    @GetMapping("/delete/{id}")
    public ResponseVO delete(@PathVariable("id") Long id) {
        return sysBrandsOrgService.deleteById(id);
    }

    /**
     * 根据Id查询机构
     */
    @ApiOperation(value = "根据机构id查询机构", notes = "根据机构id查询机构")
    @ApiOperationSupport(order = 4)
    @ApiImplicitParams({@ApiImplicitParam(name = "id", value = "机构id", dataType = "Long", required = true)})
    @GetMapping("/getById/{id}")
    public ResponseVO<SysBrandsOrgGetResponseVO> getById(@PathVariable("id") Long id) {
        return sysBrandsOrgService.getById(id);
    }

    /**
     * 分页查询机构列表     id,name,nameEn,brandsCount
     */
    @ApiOperation(value = "分页查询机构列表", notes = "分页查询机构列表")
    @ApiOperationSupport(order = 5)
    @GetMapping("/getListByPage")
    public ResponseVO<BasePage<List<SysBrandsOrgPageResponseVO>>> getListByPage(SysBrandsOrgPageRequestVO pageRequestVO) {
        return sysBrandsOrgService.getListByPage(pageRequestVO);
    }


    /**
     * 查询机构列表  id,name
     */
    @ApiOperation(value = "2期修改==== 查询机构列表", notes = "查询机构列表")
    @ApiOperationSupport(order = 6)
    @GetMapping("/getList")
    @ApiImplicitParam(name = "withSysOrg",value = "查询系统机构，1-查询，其他不查，只有用户管理模块需要系统机构")
    public ResponseVO<List<SysBrandsOrgListResponseVO>> getList(Integer withSysOrg) {
        return sysBrandsOrgService.getList(withSysOrg);
    }


}