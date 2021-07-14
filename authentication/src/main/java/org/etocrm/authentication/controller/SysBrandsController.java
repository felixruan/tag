package org.etocrm.authentication.controller;

import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.etocrm.authentication.entity.VO.brands.*;
import org.etocrm.authentication.service.ISysBrandsService;
import org.etocrm.core.util.ResponseVO;
import org.etocrm.dynamicDataSource.model.DO.WoaapBrandsDO;
import org.etocrm.dynamicDataSource.util.BasePage;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;


/**
 * @author lingshuang.pang
 */
@Api(value = "品牌", tags = "2期有修改==== 品牌管理API")
@RestController
@RefreshScope
@RequestMapping("/sysBrands")
public class SysBrandsController {

    @Resource
    private ISysBrandsService sysBrandsService;


    /**
     * 新增品牌
     */
    @ApiOperation(value = "2期有修改==== 新增品牌 ", notes = "新增品牌 ")
    @ApiOperationSupport(order = 1)
    @PostMapping("/save")
    public ResponseVO save(@RequestBody @Valid SysBrandsSaveVO saveVO) {
        return sysBrandsService.saveSysBrands(saveVO);
    }

    /**
     * 修改品牌
     */
    @ApiOperation(value = "2期=====修改品牌", notes = "修改品牌")
    @ApiOperationSupport(order = 2)
    @PostMapping("/update")
    public ResponseVO update(@RequestBody @Valid SysBrandsUpdateVO updateVO) {
        return sysBrandsService.updateById(updateVO);
    }

    /**
     * 删除品牌
     */
    @ApiOperation(value = "删除品牌", notes = "删除品牌")
    @ApiImplicitParams({@ApiImplicitParam(name = "id", value = "品牌的id", dataType = "Long", required = true)})
    @GetMapping("/delete/{id}")
    public ResponseVO delete(@PathVariable("id") Long id) {
        return sysBrandsService.deleteById(id);
    }

    /**
     * 根据Id获取品牌
     */
    @ApiOperation(value = "根据品牌Id获取品牌", notes = "根据品牌Id获取品牌")
    @ApiImplicitParams({@ApiImplicitParam(name = "id", value = "品牌的id", dataType = "Long", required = true)})
    @GetMapping("/getById/{id}")
    public ResponseVO<SysBrandsGetResponseVO> getById(@PathVariable("id") Long id) {
        return sysBrandsService.getById(id);
    }

    /**
     * 含有标签数量
     * 分页查询品牌列表
     */
    @ApiOperation(value = "2期有修改==== 分页查询品牌列表", notes = "分页查询品牌列表")
    @GetMapping("/getListByPage")
    public ResponseVO<BasePage<List<SysBrandsPageResponseVO>>> getListByPage(SysBrandsPageRequestVO brandsPageRequestVO) {
        return sysBrandsService.getListByPage(brandsPageRequestVO);
    }

    /**
     * 查询品牌列表
     */
    @ApiOperation(value = "2期修改==== 查询品牌列表", notes = "查询品牌列表")
    @GetMapping("/getList")
    public ResponseVO<List<SysBrandsListResponseVO>> getList(SysBrandsListRequestVO listRequestVO) {
        return sysBrandsService.getList(listRequestVO);
    }

    /**
     * 查询品牌列表 dataFeign 调用
     */
    @GetMapping("/getListAll")
    public ResponseVO<List<SysBrandsListAllResponseVO>> getListAll() {
        return sysBrandsService.getListAll();
    }


    /**
     * todo
     * @param brandsId
     * @return
     */
    @ApiOperation(value = "2期==== 根据品牌id获取平均复购周期", notes = "根据品牌id获取平均复购周期")
    @GetMapping("/getAvgRepurchaseCycle/{brandsId}")
    public ResponseVO getAvgRepurchaseCycle(@PathVariable("brandsId") Long brandsId) {
        return sysBrandsService.getAvgRepurchaseCycle(brandsId);
    }


    @GetMapping("/getWoaapBrands")
    public List<WoaapBrandsDO> getWoaapBrands(@RequestParam Long brandsId) {
        return sysBrandsService.getWoaapBrands(brandsId);
    }

}