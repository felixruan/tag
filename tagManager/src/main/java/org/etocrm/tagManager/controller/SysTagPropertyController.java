package org.etocrm.tagManager.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.etocrm.core.util.ResponseVO;
import org.etocrm.tagManager.model.DO.SysTagPropertyDO;
import org.etocrm.tagManager.model.VO.tagProperty.SysTagPropertyEditRequestVO;
import org.etocrm.tagManager.model.VO.tagProperty.SysTagPropertyQueryResponseVO;
import org.etocrm.tagManager.model.VO.tagProperty.SysTagPropertyResponseVO;
import org.etocrm.tagManager.service.ISysTagPropertyService;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;
import java.util.Set;


@Slf4j
@Api(value = "标签属性 ", tags = "标签属性 ")
@RestController
@RefreshScope
@RequestMapping("/sysTagProperty")
public class SysTagPropertyController {

    @Resource
    private ISysTagPropertyService sysTagPropertyService;


    /**
     * 编辑标签属性
     */
    @ApiOperation(value = "编辑标签属性 ", notes = "编辑标签属性 ")
    @PostMapping("/edit")
    public ResponseVO editSysTagProperty(@RequestBody @Valid SysTagPropertyEditRequestVO editVO) {
        return sysTagPropertyService.editSysTagProperty(editVO);
    }

    /**
     * 查询标签属性
     */
    @ApiOperation(value = "查询标签属性 ", notes = "查询标签属性 ")
    @GetMapping("/query/{tagId}")
    public ResponseVO<List<SysTagPropertyQueryResponseVO>> getSysTagProperty(@PathVariable Long tagId) {
        return sysTagPropertyService.getSysTagProperty(tagId);
    }

    /**
     * 根据标签ID查询属性列表
     */
    @ApiOperation(value = "根据标签ID查询属性列表 ", notes = "根据标签ID查询属性列表 ")
    @GetMapping("/getListByTagId/{tagId}")
    public ResponseVO<List<SysTagPropertyResponseVO>> getListByTagId(@PathVariable Long tagId) {
        return sysTagPropertyService.getListByTagId(tagId);
    }


    /**
     * 更新标签属性  dataManager调用,不要改
     */
    @PostMapping("/dataManager/update")
    public ResponseVO updateTagPropertyByTagId(@RequestBody SysTagPropertyDO sysTagPropertyDO) {
        return sysTagPropertyService.updateTagPropertyByTagId(sysTagPropertyDO);
    }

    /**
     *  authManager调用,不要改
     */
    @PostMapping("/authManager/getTagPropertyByTagIds")
    public ResponseVO<List<SysTagPropertyDO>> getTagPropertyByTagIds(@RequestParam Set<Long> ids) {
        return sysTagPropertyService.getTagPropertyByTagIds(ids);
    }


}