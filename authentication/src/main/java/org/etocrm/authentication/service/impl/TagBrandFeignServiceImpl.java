package org.etocrm.authentication.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.etocrm.authentication.entity.DO.SysTagDO;
import org.etocrm.authentication.entity.DO.SysTagGroupRuleDO;
import org.etocrm.authentication.entity.VO.tagBrands.SysTagBrandsInfoVO;
import org.etocrm.authentication.service.TagBrandFeignService;
import org.etocrm.authentication.tagApi.IDataManagerService;
import org.etocrm.authentication.tagApi.ITagManagerService;
import org.etocrm.core.util.ResponseVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @Author: dkx
 * @Date: 18:49 2020/9/25
 * @Desc:
 */

@Service
@Slf4j
public class TagBrandFeignServiceImpl implements TagBrandFeignService {

    @Resource
    ITagManagerService iTagManagerService;

    @Autowired
    IDataManagerService iDataManagerService;




    /**
     * 查询从库中所有的tagId
     *
     * @return
     */
    @Override
    public ResponseVO<Set<Long>> getSysTagList(SysTagBrandsInfoVO brandsInfoVO) {
        //查询tag所有  条件  master_tag_id != 0
        ResponseVO<List<SysTagDO>> sysTagList = iTagManagerService.getSysTagList(brandsInfoVO);
        if (sysTagList.getCode() != 0) {
            return ResponseVO.errorParams("调用tagManager获取标签数据错误!");
        }
        //获取从库中的所有主库mastertagId
        Set<Long> allMasterIds = sysTagList.getData().stream().map(SysTagDO::getMasterTagId).collect(Collectors.toSet());
        return ResponseVO.success(allMasterIds);
    }

    /**
     * 根据品牌机构id查询所有群组规则tagid
     *
     * @return
     */
    @Override
    public ResponseVO<Set<Long>> getGroupRuleByTagIds(SysTagBrandsInfoVO brandsInfoVO) {
        ResponseVO<List<SysTagGroupRuleDO>> groupRuleByTagIds = iTagManagerService.getGroupRuleByTagIds(brandsInfoVO);
        if (groupRuleByTagIds.getCode() != 0) {
            return ResponseVO.errorParams("调用tagManager获取标签数据错误");
        }
        Set<Long> salse = groupRuleByTagIds.getData().stream().map(SysTagGroupRuleDO::getTagId).collect(Collectors.toSet());
        return ResponseVO.success(salse);
    }

    /**
     * 根据标签ids 获取系统标签ids
     * @param salse
     * @return
     */
    @Override
    public ResponseVO<Set<Long>> getTagByIds(Set<Long> salse) {
        //查询品牌关联关系以规则id为条件
        ResponseVO<List<SysTagDO>> tagByIds = iTagManagerService.getTagByIds(salse);
        if (tagByIds.getCode() != 0) {
            return ResponseVO.errorParams("查询品牌tag数据错误！");
        }
        //获取品牌中的系统mastertagId以规则tagid为条件
        Set<Long> masterIds = tagByIds.getData().stream().map(SysTagDO::getMasterTagId).collect(Collectors.toSet());
        return ResponseVO.success(masterIds);
    }


}
