package org.etocrm.tagManager.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.etocrm.core.util.ResponseVO;
import org.etocrm.dynamicDataSource.util.BasePage;
import org.etocrm.tagManager.enums.TagMethodEnum;
import org.etocrm.tagManager.model.VO.tagGroup.*;
import org.etocrm.tagManager.service.ITagGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Api(tags = "2期有修改==== 标签群组")
@RefreshScope
@RestController
@RequestMapping("/tagGroup")
public class TagGroupController {

    @Autowired
    private ITagGroupService tagGroupService;

    /**
     * 添加标签群组
     *
     * @param tagGroupAddVO
     * @return
     */
    @ApiOperation(value = "2期有修改==== 添加标签群组", notes = "添加标签群组")
    @PostMapping("/add")
    public ResponseVO addTagGroup(@RequestBody @Valid SysTagGroupAddVO tagGroupAddVO) {
        return tagGroupService.groupMethod(TagMethodEnum.GROUP_ADD, tagGroupAddVO);
    }

    /**
     * 修改标签群组
     *
     * @param modifyVO
     * @return
     */
    @ApiOperation(value = "2期有修改==== 修改标签群组", notes = "修改标签群组")
    @PostMapping("/modify")
    public ResponseVO modifyTagGroup(@RequestBody @Valid SysTagGroupModifyVO modifyVO) {
        return tagGroupService.groupMethod(TagMethodEnum.GROUP_UPDATE, modifyVO);
    }

    /**
     * 更新标签群组状态
     *
     * @param updateStatusVO
     * @return
     */
    @ApiOperation(value = "更新状态", notes = "更新状态")
    @PostMapping("/updateStatus")
    public ResponseVO updateStatus(@RequestBody @Valid SysTagGroupUpdateStatusVO updateStatusVO) {
        return tagGroupService.groupMethod(TagMethodEnum.GROUP_UPDATE_STATUS, updateStatusVO);
    }

    /**
     * 更新标签群组名称
     *
     * @param updateNameVO
     * @return
     */
    @ApiOperation(value = "更新标签群组名称", notes = "更新标签群组名称")
    @PostMapping("/updateTagGroupName")
    public ResponseVO updateTagGroupName(@RequestBody @Valid SysTagGroupUpdateNameVO updateNameVO) {
        return tagGroupService.groupMethod(TagMethodEnum.GROUP_UPDATE_NAME, updateNameVO);
    }

    /**
     * 复制
     *
     * @param tagGroupId
     * @return
     */
    @ApiOperation(value = "复制", notes = "复制")
    @GetMapping("/copy/{tagGroupId}")
    public ResponseVO copyTagGroup(@PathVariable Long tagGroupId) {
        return tagGroupService.groupMethod(TagMethodEnum.GROUP_COPY, tagGroupId);
    }

    @ApiOperation(value = "预估人数", notes = "预估人数")
    @PostMapping("/predict")
    public ResponseVO predict(@RequestBody @Valid SysTagGroupPredictVO predictVO) {
        return tagGroupService.groupMethod(TagMethodEnum.GROUP_PREDICT, predictVO);
    }

    /**
     * 根据群组id查询群组信息
     *
     * @param tagGroupId
     * @return
     */
    @ApiOperation(value = "2期有修改==== 根据群组id查询群组信息", notes = "根据群组id查询群组信息")
    @GetMapping("/getGroupById/{tagGroupId}")
    public ResponseVO<SysTagGroupResponseVO> getGroupById(@PathVariable Long tagGroupId) {
        return tagGroupService.groupMethod(TagMethodEnum.GROUP_GET_BY_ID, tagGroupId);
    }

    /**
     * 分页查询标签群组
     *
     * @param queryRequestVO
     * @return
     */
    @ApiOperation(value = "2期有修改==== 分页查询标签群组", notes = "分页查询标签群组")
    @GetMapping("/getListByPage")
    public ResponseVO<BasePage<ListPageSysTagGroupQueryResponseVO>> getListByPage(@Validated SysTagGroupQueryRequestVO queryRequestVO) {
        return tagGroupService.groupMethod(TagMethodEnum.GROUP_LIST_PAGE, queryRequestVO);
    }


    /**
     * 删除标签群组
     *
     * @param tagGroupId
     * @return
     */
    @ApiOperation(value = "删除标签群组", notes = "删除标签群组")
    @GetMapping("/delete/{tagGroupId}")
    public ResponseVO deleteTagGroup(@PathVariable Long tagGroupId) {
        return tagGroupService.groupMethod(TagMethodEnum.GROUP_DELETE, tagGroupId);
    }

    /**
     * 重新计算
     *
     * @param tagGroupId
     * @return
     */
    @ApiOperation(value = "重新计算", notes = "重新计算")
    @GetMapping("/recalculate/{tagGroupId}")
    public ResponseVO recalculate(@PathVariable Long tagGroupId) {
        return tagGroupService.recalculate(tagGroupId);
    }


    /**
     * 查询群组列表  id,name
     *
     * @return
     */
    @ApiOperation(value = "查询群组列表", notes = "查询群组列表")
    @GetMapping("/getEnableList")
    public ResponseVO<List<SysTagGroupListResponseVO>> getEnableList() {
        return tagGroupService.getEnableList();
    }


    @ApiOperation(value = "根据群组id导出人群明细", notes = "勾选的属性加入，没有勾选不要加入，tagGroupId属性必加：" +
            "{\n" +
            "\t\"tagGroupId\":\"2\",\n" +
            "\t\"分包号\":\"subcontract_no\",\n" +
            "\t\"会员编号\":\"number\",\n" +
            "\t\"姓名\":\"name\",\n" +
            "\t\"性别\":\"gender\",\n" +
            "\t\"生日\":\"birthday\",\n" +
            "\t\"会员等级\":\"vip_level\",\n" +
            "\t\"积分余额\":\"integral\",\n" +
            "\t\"注册时间\":\"registered_time\",\n" +
            "\t\"手机号\":\"mobile\"\n" +
            "}")
    @GetMapping("/downLoadMemberPackage")
    public ResponseVO downLoadMemberPackage(@RequestParam("excelJson") String excelJson) {
        return tagGroupService.groupMethod(TagMethodEnum.GROUP_DOWNLOAD, excelJson);
    }
}
