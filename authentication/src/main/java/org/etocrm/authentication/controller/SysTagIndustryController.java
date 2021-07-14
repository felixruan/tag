package org.etocrm.authentication.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.etocrm.authentication.entity.VO.tagBrands.SysTagClassesTreeVO;
import org.etocrm.authentication.entity.VO.tagIndustry.SysTagIndustryModifyVO;
import org.etocrm.authentication.entity.VO.tagIndustry.SysTagIndustryRequestVO;
import org.etocrm.authentication.service.ISysTagIndustryService;
import org.etocrm.core.util.ResponseVO;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

@Api(value = "标签行业", tags = "标签行业API")
@RestController
@RefreshScope
@RequestMapping("/sysTagIndustry")
public class SysTagIndustryController {

    @Resource
    private ISysTagIndustryService sysTagIndustryService;


    /**
     * 编辑标签行业
     */
    @ApiOperation(value = "编辑标签行业 ", notes = "编辑标签行业 ")
    @PostMapping("/modify")
    public ResponseVO modify(@RequestBody @Valid SysTagIndustryModifyVO modifyVO) {
        return sysTagIndustryService.modifyTagIndustry(modifyVO);
    }


    /**
     * 查询标签行业列表
     */
    @ApiOperation(value = "查询标签行业列表", notes = "查询标签行业列表")
    @GetMapping("/getList")
    public ResponseVO<List<SysTagClassesTreeVO>> getListByIndustryId(@Valid SysTagIndustryRequestVO tagIndustryRequestVO) {
        return sysTagIndustryService.getTagIndustryList(tagIndustryRequestVO);
    }


    /**
     * 根据行业查询标签树   选中的标签要显示
     */
    @ApiOperation(value = "根据行业查询标签树", notes = "根据行业查询标签树")
    @GetMapping("/getTagTreeWithIndustry")
    public ResponseVO<List<SysTagClassesTreeVO>> getTagTreeWithIndustry(@Valid SysTagIndustryRequestVO tagIndustryRequestVO) {
        return sysTagIndustryService.getTagTreeWithIndustry(tagIndustryRequestVO);
    }

    /**
     * 根据标签id删除行业-标签 关联  tagManager 调用
     * @param tagId
     * @return
     */
    @GetMapping("/deleteByTagId/{tagId}")
    public ResponseVO deleteByTagId(@PathVariable Long tagId) {
        return sysTagIndustryService.deleteByTagId(tagId);
    }

}