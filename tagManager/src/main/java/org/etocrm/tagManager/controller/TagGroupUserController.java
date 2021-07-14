package org.etocrm.tagManager.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.etocrm.core.util.ResponseVO;
import org.etocrm.tagManager.model.VO.tagGroup.*;
import org.etocrm.tagManager.service.ITagGroupUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;


@Api(tags = "2期有修改==== 标签群组人群相关")
@RefreshScope
@RestController
@RequestMapping("/tagGroupUser")
public class TagGroupUserController {

    @Autowired
    private ITagGroupUserService iTagGroupUserService;

    /**
     * 根据标签群组id获取人群明细列表
     *
     * @param pageVO
     * @return
     */
    @ApiOperation(value = "根据标签群组id获取人群明细列表", notes = "根据标签群组id获取人群明细列表")
    @GetMapping("/getUsersDetail")
    public ResponseVO getTagGroupUsersDetail(@Valid SysTagGroupUserDetailPageVO pageVO) {
        return iTagGroupUserService.getTagGroupUsersDetail(pageVO);
    }

    /**
     * 根据群组id拆分人群明细列表（分包）
     *
     * @param splitVO
     * @return
     */
    @ApiOperation(value = "根据群组id拆分人群明细列表（分包）", notes = "根据群组id拆分人群明细列表（分包）")
    @PostMapping("/splitUsersDetailList")
    public ResponseVO splitTagGroupUsersDetail(@RequestBody @Valid SysTagGroupUserSplitDetailVO splitVO) {
        return iTagGroupUserService.splitTagGroupUsersDetail(splitVO);
    }

    /**
     * 通过获取明细count判断能否拆分子群组接口（能否分包）
     *
     * @param tagGroupId
     * @return
     */
    @ApiOperation(value = "判断能否拆分子群组接口", notes = "判断能否拆分子群组接口")
    @GetMapping("/getTagGroupUsersDetailCount/{tagGroupId}")
    public ResponseVO getTagGroupUsersDetailCount(@PathVariable("tagGroupId") Long tagGroupId) {
        return iTagGroupUserService.getTagGroupUsersDetailCount(tagGroupId);
    }

    @ApiOperation(value = "2期===== 获取画像表格信息", notes = "获取画像表格信息")
    @GetMapping("/getTableInfo")
    @ApiImplicitParam(name = "tagGroupIds",value = "群组id List",required = true)
    public ResponseVO<TagGroupTableResponseVO> getTableInfo(@RequestParam(required = true) List<Long> tagGroupIds){
        return iTagGroupUserService.getTableInfo(tagGroupIds);
    }

    @ApiOperation(value = "2期===== 获取画像图形信息", notes = "获取画像图形信息")
    @PostMapping("/getChartInfo")
    public ResponseVO<TagGroupChartResponseVO> getChartInfo(@RequestBody @Valid TagGroupChartRequestVO requestVO){
        return iTagGroupUserService.getChartInfo(requestVO);
    }

//    @ApiOperation(value = "获取画像标签分类列表", notes = "获取画像标签分类列表")
    @GetMapping("/getTagClassesList")
    public ResponseVO<List<TagClassesInfoVO>> getTagClassesList(){
        return iTagGroupUserService.getTagClassesList();
    }
}
