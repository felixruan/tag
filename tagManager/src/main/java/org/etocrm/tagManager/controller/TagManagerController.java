package org.etocrm.tagManager.controller;

import org.etocrm.core.util.ResponseVO;
import org.etocrm.dynamicDataSource.service.IDynamicService;
import org.etocrm.tagManager.model.DO.SysTagDO;
import org.etocrm.tagManager.model.DO.SysTagPropertyDO;
import org.etocrm.tagManager.model.DO.SysTagPropertyRuleDO;
import org.etocrm.tagManager.model.VO.DBProcessorVO;
import org.etocrm.tagManager.model.VO.tag.SysTagBrandsInfoVO;
import org.etocrm.tagManager.model.VO.tag.TagBrandsInfoVO;
import org.etocrm.tagManager.model.VO.tagManager.GetTagNameRequestInfoVO;
import org.etocrm.tagManager.service.ISysTagPropertyRuleService;
import org.etocrm.tagManager.service.ISysTagPropertyService;
import org.etocrm.tagManager.service.ISysTagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.*;

/**
 * @Author: dkx
 * @Date: 9:48 2020/9/16
 * @Desc:
 */
@RestController
@RequestMapping("/tagManager")
public class TagManagerController {

    @Autowired
    IDynamicService dynamicService;

    @Autowired
    ISysTagPropertyService sysTagPropertyService;

    @Autowired
    ISysTagPropertyRuleService sysTagPropertyRuleService;

    @Autowired
    ISysTagService sysTagService;

    /**
     * dataManager 调用，不可删除
     *
     * @param dbProcessorVO
     * @return
     */
    @PostMapping("selectDbProcessorList")
    public ResponseVO<List<TreeMap>> selectDbProcessorList(@RequestBody DBProcessorVO dbProcessorVO) {
        List<String> tableNames = new ArrayList<>();
        tableNames.add(dbProcessorVO.getTableName());
        String whereClause = dbProcessorVO.getWhereCase();
        String orderStr = "";
        List<TreeMap> resultMap = dynamicService.selectList(tableNames, dbProcessorVO.getColumn(), whereClause, orderStr);
        return ResponseVO.success(resultMap);
    }

    @PostMapping("getTagByIds")
    public ResponseVO<List<SysTagDO>> getTagByIds(@RequestParam Set<Long> ids) {
        return sysTagService.selectListByIds(ids);
    }

    @PostMapping("getTagByMasterIds")
    public ResponseVO<List<SysTagDO>> getTagByMasterIds(@RequestParam Set<Long> ids,@RequestBody @Valid SysTagBrandsInfoVO tagBrandsInfoVO) {
        return sysTagService.getTagByMasterIds(ids,tagBrandsInfoVO);
    }

    @PostMapping("/tag/getList")
    public ResponseVO<List<SysTagDO>> getSysTagList(@RequestBody SysTagBrandsInfoVO brandsInfoVO) {
        return sysTagService.getSysTagListByAuth(brandsInfoVO);
    }

    /**
     * @param : sysTagVO
     * @Description: 添加标签
     * @return:
     **/
    @PostMapping("/tag/saveTags")
    public ResponseVO saveSysTags(@RequestBody SysTagDO sysTagDO) {
        return sysTagService.insert(sysTagDO);
    }

    @PostMapping("/tag/saveSysTagPropertys")
    public ResponseVO saveSysTagPropertys(@RequestBody SysTagPropertyDO sysTagPropertyDO) {
        return sysTagPropertyService.insert(sysTagPropertyDO);
    }

    @PostMapping("/tag/saveSysTagPropertyRules")
    public ResponseVO saveSysTagPropertyRules(@RequestBody SysTagPropertyRuleDO sysTagPropertyRuleDO) {
        return sysTagPropertyRuleService.insert(sysTagPropertyRuleDO);
    }

    //主键更新从库tag数据
    @PostMapping("/tag/updateTags")
    public ResponseVO updateTags(@RequestParam Set<Long> ids) {
        return sysTagService.update(ids);
    }

    //主键更新从库tag属性数据
    @PostMapping("/tag/updateSysTagPropertys")
    public ResponseVO updateSysTagPropertys(@RequestParam Set<Long> ids) {
        return sysTagPropertyService.updateSysTagPropertysById(ids);
    }

    //主键更新从库tag属性规则数据
    @PostMapping("/tag/updateSysTagPropertyRules")
    public ResponseVO updateSysTagPropertyRules(@RequestParam Set<Long> ids) {
        return sysTagPropertyRuleService.updateSysTagPropertyRulesById(ids);
    }

    @PostMapping("/tag/selectTagNameByTagId")
    public ResponseVO<Boolean> selectTagNameByTagId(@RequestBody GetTagNameRequestInfoVO requestInfoVO) {
        return sysTagService.selectTagNameByTagId(requestInfoVO);
    }
}
