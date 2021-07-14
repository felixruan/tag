package org.etocrm.authentication.tagApi;

import org.etocrm.authentication.entity.DO.SysTagDO;
import org.etocrm.authentication.entity.DO.SysTagGroupRuleDO;
import org.etocrm.authentication.entity.DO.SysTagPropertyDO;
import org.etocrm.authentication.entity.DO.SysTagPropertyRuleDO;
import org.etocrm.authentication.entity.VO.GetTagNameRequestInfoVO;
import org.etocrm.authentication.entity.VO.brands.TagCountParam;
import org.etocrm.authentication.entity.VO.tagBrands.SysTagBrandsInfoVO;
import org.etocrm.authentication.entity.VO.tagBrands.SysTagClassesTreeVO;
import org.etocrm.authentication.tagApi.fallback.ITagManagerServiceFallBackFactory;
import org.etocrm.core.util.ResponseVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Set;

/**
 * @Author dkx
 * @date 2020/9/10 21:06
 */
@FeignClient(name = "tag-etocrm-tagManager-server", fallbackFactory = ITagManagerServiceFallBackFactory.class)
public interface ITagManagerService {

    @GetMapping("/tagClasses/getListTree")
    ResponseVO<List<SysTagClassesTreeVO>> getTagClassesTree(@RequestParam(required = false) Integer tagStatus, @RequestParam(required = false) Integer querySystem);

    /**
     * 查询tag所有  条件  master_tag_id != 0
     */
    @GetMapping("/tagManager/tag/getList")
    ResponseVO<List<SysTagDO>> getSysTagList(SysTagBrandsInfoVO brandsInfoVO);

    // 去品牌群组规则中查询
    @PostMapping("/tagGroupRule/getGroupRuleByTagIds")
    ResponseVO<List<SysTagGroupRuleDO>> getGroupRuleByTagIds(@RequestBody SysTagBrandsInfoVO brandsInfoVO);

    //根据标签id 查询标签数据
    @PostMapping("/tagManager/getTagByIds")
    ResponseVO<List<SysTagDO>> getTagByIds(@RequestParam Set<Long> ids);

    //根据标签id 查询属性信息
    @PostMapping("/sysTagProperty/authManager/getTagPropertyByTagIds")
    ResponseVO<List<SysTagPropertyDO>> getTagPropertyByTagIds(@RequestParam Set<Long> ids);

    //根据属性id查询属性规则信息
    @PostMapping("/sysTagPropertyRule/authManager/getSysTagPropertyRuleByIds")
    ResponseVO<List<SysTagPropertyRuleDO>> getSysTagPropertyRuleIds(@RequestParam Set<Long> ids);

    @PostMapping("/tagManager/tag/saveTags")
    ResponseVO saveSysTags(@RequestBody SysTagDO sysTagDO);

    @PostMapping("/tagManager/tag/saveSysTagPropertys")
    ResponseVO saveSysTagPropertys(@RequestBody SysTagPropertyDO sysTagPropertyDO);

    @PostMapping("/tagManager/tag/saveSysTagPropertyRules")
    ResponseVO saveSysTagPropertyRules(@RequestBody SysTagPropertyRuleDO sysTagPropertyRuleDO);

    //品牌主键Id为条件更新
    @PostMapping("/tagManager/tag/updateTags")
    ResponseVO updateTags(@RequestParam Set<Long> ids);

    //品牌主键Id为条件更新
    @PostMapping("/tagManager/tag/updateSysTagPropertys")
    ResponseVO updateSysTagPropertys(@RequestParam Set<Long> ids);

    //品牌propId为条件更新
    @PostMapping("/tagManager/tag/updateSysTagPropertyRules")
    ResponseVO updateSysTagPropertyRules(@RequestParam Set<Long> ids);

    //获取品牌masterId为条件的
    @PostMapping("/tagManager/getTagByMasterIds")
    ResponseVO<List<SysTagDO>> getTagByMasterIds(@RequestParam Set<Long> ids,@RequestBody SysTagBrandsInfoVO tagBrandsInfoVO);

    @PostMapping("/tag/authentication/getTagCount")
    ResponseVO<Integer> getTagCount(TagCountParam tagCountParam);

    //品牌查询名称是否存在
    @PostMapping("tagManager/tag/selectTagNameByTagId")
    ResponseVO<Boolean> selectTagNameByTagId(@RequestBody GetTagNameRequestInfoVO requestInfoVO);

    /**
     * 查询品牌下生命周期模型数量
     * @param countRequestVO
     * @return
     */
    @PostMapping("/lifeCycle/getLifeCycleModelCount")
    ResponseVO getLifeCycleModelCount(@RequestBody LifeCycelModelCountRequestVO countRequestVO);
}
