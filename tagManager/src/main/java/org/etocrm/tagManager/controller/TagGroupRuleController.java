package org.etocrm.tagManager.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.etocrm.core.util.ResponseVO;
import org.etocrm.tagManager.model.DO.SysTagGroupRuleDO;
import org.etocrm.tagManager.model.VO.tag.SysTagBrandsInfoVO;
import org.etocrm.tagManager.model.VO.tagGroup.SysTagGroupRuleGetResponseVO;
import org.etocrm.tagManager.model.VO.tagGroup.SysTagGroupRuleVO;
import org.etocrm.tagManager.service.ITagGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Api(tags = "标签群组规则")
@RefreshScope
@RestController
@RequestMapping("/tagGroupRule")
public class TagGroupRuleController {

    @Autowired
    private ITagGroupService iTagGroupService;


    /**
     * authManager 调用
     * 获取getGroupRuleByTagIds
     * @return
     */
    @PostMapping("/getGroupRuleByTagIds")
    public ResponseVO<List<SysTagGroupRuleDO>> getGroupRuleByTagIds(@RequestBody @Valid SysTagBrandsInfoVO brandsInfoVO) {
        return iTagGroupService.getGroupRuleByTagIds(brandsInfoVO);
    }

    @ApiOperation(value = "编辑标签群组规则",notes = "编辑标签群组规则")
    @PostMapping("/edit")
    public ResponseVO editGroupRule(@RequestBody @Valid SysTagGroupRuleVO sysTagGroupRuleVO){
        return iTagGroupService.editGroupRule(sysTagGroupRuleVO);
    }

    @ApiOperation(value = "根据群组id获取群组规则",notes = "根据群组id获取群组规则")
    @GetMapping("/getByGroupId/{tagGroupId}")
    public ResponseVO<SysTagGroupRuleGetResponseVO> getGroupRuleByGroupId(@PathVariable Long tagGroupId){
        return iTagGroupService.getGroupRuleByGroupId(tagGroupId);
    }
}
