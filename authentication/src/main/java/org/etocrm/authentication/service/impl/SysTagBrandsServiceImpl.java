package org.etocrm.authentication.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.etocrm.authentication.entity.DO.*;
import org.etocrm.authentication.entity.VO.GetTagNameRequestInfoVO;
import org.etocrm.authentication.entity.VO.dataSource.SysDataSourceVO;
import org.etocrm.authentication.entity.VO.tagBrands.*;
import org.etocrm.authentication.mapper.ISysBrandsMapper;
import org.etocrm.authentication.service.ISysTagIndustryService;
import org.etocrm.authentication.service.SysTagBrandsService;
import org.etocrm.authentication.service.TagBrandFeignService;
import org.etocrm.authentication.tagApi.IDataManagerService;
import org.etocrm.authentication.tagApi.ITagManagerService;
import org.etocrm.core.enums.BusinessEnum;
import org.etocrm.core.enums.ResponseEnum;
import org.etocrm.core.util.BeanUtils;
import org.etocrm.core.util.ResponseVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 系统标签与品牌关联表 服务实现类
 * </p>
 *
 * @author dkx
 * @Date 2020-09-10
 */
@Service
@Slf4j
public class SysTagBrandsServiceImpl implements SysTagBrandsService {

    @Resource
    ITagManagerService iTagManagerService;

    @Autowired
    IDataManagerService iDataManagerService;

    @Resource
    ISysTagIndustryService iSysTagIndustryService;

    @Autowired
    TagBrandFeignService tagBrandFeignService;

    @Autowired
    private ISysBrandsMapper iSysBrandsMapper;

    /**
     * 1.查询品牌群组规则使用的tagIds （查到的tagId不能取消关联）
     * 2.根据1中 查询的，转成mastertagId,比较新传的是否都包含，有不包含的，删除失败
     * 3.保存
     * @param vo
     * @return
     */
    @Override
    public ResponseVO saveSysTagBrands(SysTagBrandsAddVO vo) {
        try {
            String[] split = vo.getTagIds().split(",");
            if (split.length <= 0) {
                return ResponseVO.errorParams(ResponseEnum.INCORRECT_PARAMS.getMessage());
            }
            SysBrandsDO brandsDOFind = iSysBrandsMapper.selectOne(new LambdaQueryWrapper<SysBrandsDO>().eq(SysBrandsDO::getId, vo.getBrandsId()));
            if (null == brandsDOFind) {
                return ResponseVO.errorParams("品牌不存在");
            }
            Set<Long> newTags = new TreeSet<>();
            for (String tagId : split) {
                newTags.add(Long.valueOf(tagId));
            }
            Set<Long> request = new HashSet<>();
            request.addAll(newTags);

            // 去群组规则中查询品牌所有tagid
            SysTagBrandsInfoVO brandsInfoVO = new SysTagBrandsInfoVO();
            brandsInfoVO.setBrandsId(brandsDOFind.getId());
            brandsInfoVO.setOrgId(brandsDOFind.getOrgId());
            //使用中的品牌标签ids
            ResponseVO<Set<Long>> groupRuleByTagIds = tagBrandFeignService.getGroupRuleByTagIds(brandsInfoVO);
            Set<Long> salse = groupRuleByTagIds.getData();
            if (CollectionUtil.isNotEmpty(salse)) {
                //由品牌标签id 获取 系统标签id
                ResponseVO<Set<Long>> tagByIds1 = tagBrandFeignService.getTagByIds(salse);
                Set<Long> masterIds = tagByIds1.getData();
                //排除masterid = 0
                masterIds.remove(0L);
                //对比
                int size = newTags.size();
                newTags.addAll(masterIds);
                int aa = newTags.size();
                if (size != aa) {
                    return ResponseVO.errorParams("标签已经在群组中使用，不可更改！");
                }
            }
            //查询品牌所有从系统同步的标签ids  条件  master_tag_id != 0     获取masterTagId
            ResponseVO<Set<Long>> sysTagList1 = tagBrandFeignService.getSysTagList(brandsInfoVO);
            Set<Long> allMasterIds = sysTagList1.getData();
            //拿到即将新增的
            newTags.removeAll(allMasterIds);

            //拿到删除的
            allMasterIds.removeAll(request);
            if (newTags.isEmpty() && allMasterIds.isEmpty()) {
                return ResponseVO.success();
            }
            log.info("新增的tags:" + newTags.toString() + " 删除的tags:" + allMasterIds.toString());
            return this.saveTagsToBrandsData(newTags, allMasterIds, brandsInfoVO);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ResponseVO.errorParams(ResponseEnum.DATA_ADD_ERROR.getMessage());
        }
    }

    /**
     * 保存品牌标签属性规则  第一步 校验查询系统数据
     *
     * @param newTags 新增的系统标签id
     * @param deleteTagIds 删除的系统标签id
     * @param brandsInfoVO 品牌信息
     * @return
     */
    private ResponseVO saveTagsToBrandsData(Set<Long> newTags, Set<Long> deleteTagIds, SysTagBrandsInfoVO brandsInfoVO) {
        if (!newTags.isEmpty()) {
            //查询系统tag信息
            ResponseVO<List<SysTagDO>> tagMasterByIds = iTagManagerService.getTagByIds(newTags);
            if (tagMasterByIds.getCode() != 0 || tagMasterByIds.getData().isEmpty()) {
                return ResponseVO.errorParams("保存失败,查不到对应的标签相关信息！");
            }
            //查询系统tag属性
            ResponseVO<List<SysTagPropertyDO>> tagPropertyByTagIdsMaster = iTagManagerService.getTagPropertyByTagIds(newTags);
            if (tagPropertyByTagIdsMaster.getCode() != 0 || tagPropertyByTagIdsMaster.getData().isEmpty()) {
                return ResponseVO.errorParams("保存失败,查不到对应的标签属性相关信息！");
            }
            Set<Long> propertyIdsMaster = tagPropertyByTagIdsMaster.getData().stream().map(SysTagPropertyDO::getId).collect(Collectors.toSet());
            //查询系统属性规则
            log.info("拿到属性id 查找规则ids:" + propertyIdsMaster.toString());
            ResponseVO<List<SysTagPropertyRuleDO>> sysTagPropertyRuleIdsMaster = iTagManagerService.getSysTagPropertyRuleIds(propertyIdsMaster);
            if (sysTagPropertyRuleIdsMaster.getCode() != 0 || sysTagPropertyRuleIdsMaster.getData().isEmpty()) {
                return ResponseVO.errorParams("保存失败,查不到对应的标签属性规则相关信息！");
            }
            log.info("拿到属性规则条数:" + sysTagPropertyRuleIdsMaster.getData().size());
            return this.getTagsToBrandsDataDetails(tagMasterByIds.getData(), tagPropertyByTagIdsMaster.getData(),
                    sysTagPropertyRuleIdsMaster.getData(), deleteTagIds, brandsInfoVO);
        }
        if (!deleteTagIds.isEmpty()) {
            //删除品牌
            this.deleteTagInfo(deleteTagIds,brandsInfoVO);
        }
        return ResponseVO.success();
    }

    /**
     * 保存品牌标签属性规则  第二步 保存品牌
     *
     * @param sysTagDOS
     * @param sysTagPropertyDOS
     * @param sysTagPropertyRuleDOS
     * @return
     */
    private ResponseVO getTagsToBrandsDataDetails(List<SysTagDO> sysTagDOS, List<SysTagPropertyDO> sysTagPropertyDOS, List<SysTagPropertyRuleDO> sysTagPropertyRuleDOS,
                                                  Set<Long> deleteTagIds, SysTagBrandsInfoVO brandsInfoVO) {
        GetTagNameRequestInfoVO tagNameRequestInfoVO = new GetTagNameRequestInfoVO();
        tagNameRequestInfoVO.setOrgId(brandsInfoVO.getOrgId());
        tagNameRequestInfoVO.setBrandsId(brandsInfoVO.getBrandsId());

        Iterator<SysTagDO> sysTagDOIterator = sysTagDOS.iterator();
        while (sysTagDOIterator.hasNext()) {
            SysTagDO sysTagDO = sysTagDOIterator.next();

            tagNameRequestInfoVO.setId(sysTagDO.getId());
            tagNameRequestInfoVO.setTagName(sysTagDO.getTagName());
            //先查询品牌是否存在同名标签，
            ResponseVO<Boolean> booleanResponseVO = iTagManagerService.selectTagNameByTagId(tagNameRequestInfoVO);
            if (booleanResponseVO.getCode() != 0) {
                return ResponseVO.errorParams("查询品牌标签失败！");
            }
            if (booleanResponseVO.getData()) {
                return ResponseVO.errorParams(sysTagDO.getTagName() + "已经存在品牌中！");
            }
            sysTagDO.setMasterTagId(sysTagDO.getId());
            sysTagDO.setBrandsId(brandsInfoVO.getBrandsId());
            sysTagDO.setOrgId(brandsInfoVO.getOrgId());
            //sysTagDO.setId(null);
            ResponseVO responseVO = iTagManagerService.saveSysTags(sysTagDO);
            if (responseVO.getCode() != 0) {
                return ResponseVO.errorParams("保存品牌标签失败！");
            }
            log.info("即将同步标签id：" + sysTagDO.getMasterTagId());
            Long tagId = ((Integer) responseVO.getData()).longValue();
            Iterator<SysTagPropertyDO> sysTagPropertyDOIterator = sysTagPropertyDOS.iterator();
            while (sysTagPropertyDOIterator.hasNext()) {
                SysTagPropertyDO sysTagPropertyDO = sysTagPropertyDOIterator.next();
                //如果标签相等可以进入
                log.info("系统标签：" + sysTagDO.getMasterTagId() + " 系统属性tagid:" + sysTagPropertyDO.getTagId());
                if (sysTagDO.getMasterTagId().equals(sysTagPropertyDO.getTagId())) {
                    log.info("进入了对比  包存品牌tagid：" + tagId);
                    Long propId = sysTagPropertyDO.getId();
                    //sysTagPropertyDO.setId(null);
                    sysTagPropertyDO.setTagId(tagId);
                    ResponseVO responseVOProp = iTagManagerService.saveSysTagPropertys( sysTagPropertyDO);
                    if (responseVOProp.getCode() != 0) {
                        return ResponseVO.errorParams("保存品牌标签属性失败！");
                    }
                    Long tagPropId = ((Integer) responseVOProp.getData()).longValue();
                    Iterator<SysTagPropertyRuleDO> sysTagPropertyRuleDOIterator = sysTagPropertyRuleDOS.iterator();
                    while (sysTagPropertyRuleDOIterator.hasNext()) {
                        SysTagPropertyRuleDO sysTagPropertyRuleDO = sysTagPropertyRuleDOIterator.next();
                        log.info("系统标签：" + sysTagDO.getMasterTagId() + " 系统属性id:" + propId + " 系统规则propid:" + sysTagPropertyRuleDO.getPropertyId());
                        //如果标签属性相等可以进入
                        Long propertyId = sysTagPropertyRuleDO.getPropertyId();
                        if (propId.equals(propertyId)) {
                            log.info("进入了对比  保存品牌tagPropid：" + tagPropId);
                            //sysTagPropertyRuleDO.setId(null);
                            sysTagPropertyRuleDO.setPropertyId(tagPropId);
                            ResponseVO responseVOPropRule = iTagManagerService.saveSysTagPropertyRules( sysTagPropertyRuleDO);
                            if (responseVOPropRule.getCode() != 0) {
                                return ResponseVO.errorParams("保存品牌标签属性规则失败！");
                            }
                            sysTagPropertyRuleDOIterator.remove();
                        }
                    }
                    sysTagPropertyDOIterator.remove();
                }
            }
            sysTagDOIterator.remove();
        }
        if (CollectionUtil.isNotEmpty(deleteTagIds)) {
            //删除品牌标签
            this.deleteTagInfo(deleteTagIds,brandsInfoVO);
        }
        return ResponseVO.success();
    }

    /**
     * 删除品牌数据
     *
     ** @return
     */
    private void deleteTagInfo(Set<Long> ids,SysTagBrandsInfoVO brandsInfoVO) {
        log.info("删除品牌tag");
        ResponseVO<List<SysTagDO>> tagMasterByIds = iTagManagerService.getTagByMasterIds(ids,brandsInfoVO);
        Set<Long> tagIds = tagMasterByIds.getData().stream().map(SysTagDO::getId).collect(Collectors.toSet());
        iTagManagerService.updateTags(tagIds);
        log.info("删除品牌tagProp");
        ResponseVO<List<SysTagPropertyDO>> tagPropertyByTagIdsMaster = iTagManagerService.getTagPropertyByTagIds(tagIds);
        Set<Long> tagPropIds = tagPropertyByTagIdsMaster.getData().stream().map(SysTagPropertyDO::getId).collect(Collectors.toSet());
        iTagManagerService.updateSysTagPropertys(tagPropIds);
        log.info("删除品牌tagPropRule");
        iTagManagerService.updateSysTagPropertyRules(tagPropIds);
    }

    @Override
    public ResponseVO<List<SysTagClassesTreeVO>> detailByBrandId(SysTagBrandsVO sysTagBrandsVO) {
        try {
            SysBrandsDO brandsDOFind = iSysBrandsMapper.selectOne(new LambdaQueryWrapper<SysBrandsDO>().eq(SysBrandsDO::getId, sysTagBrandsVO.getBrandsId()));
            if (null == brandsDOFind) {
                return ResponseVO.errorParams("品牌不存在");
            }
            SysTagBrandsInfoVO brandsInfoVO = new SysTagBrandsInfoVO();
            brandsInfoVO.setBrandsId(brandsDOFind.getId());
            brandsInfoVO.setOrgId(brandsDOFind.getOrgId());
            //查询tag所有  条件  master_tag_id != 0      获取品牌中的所有系统mastertagId
            ResponseVO<Set<Long>> sysTagList1 = tagBrandFeignService.getSysTagList(brandsInfoVO);
            if (sysTagList1.getCode() != 0) {
                return ResponseVO.errorParams("获取标签数据错误!");
            }
            Set<Long> allMasterIds = sysTagList1.getData();
            // 去品牌群组规则中查询所有tagid
            ResponseVO<Set<Long>> groupRuleByTagIds1 = tagBrandFeignService.getGroupRuleByTagIds(brandsInfoVO);
            if(0!=groupRuleByTagIds1.getCode()){
                return ResponseVO.errorParams("获取标签信息时异常!");
            }
            Set<Long> salse = groupRuleByTagIds1.getData();
            Set<Long> tags = new HashSet<>();
            if (!salse.isEmpty()) {
                //查询品牌关联关系以规则id为条件  并获取mastertagId
                ResponseVO<Set<Long>> tagByIds1 = tagBrandFeignService.getTagByIds( salse);
                Set<Long> masterIds = tagByIds1.getData();
                //排除masterid = 0
                masterIds.remove(0L);
                tags.addAll(masterIds);
            }
            allMasterIds.removeAll(tags);
            //获取系统中所有的标签分类树
            ResponseVO<List<SysTagClassesTreeVO>> tagClassesListTree = iTagManagerService.getTagClassesTree(BusinessEnum.USING.getCode(), BusinessEnum.USING.getCode());
            if (null == sysTagBrandsVO.getIndustryId() || sysTagBrandsVO.getIndustryId() == 0) {
                //如果行业id为空,调用全部
                return getResponseVOByCondition(allMasterIds, tagClassesListTree, tags);
            } else {
                //查行业标签树数据
                List<SysTagIndustryDO> tagIndustryDOList = iSysTagIndustryService.getTagIndustryListById(sysTagBrandsVO.getIndustryId());
                List<Long> tagIdList = tagIndustryDOList.stream().map(sysTagIndustryDO -> sysTagIndustryDO.getTagId()).collect(Collectors.toList());
                tagIdList.removeAll(tags);
                allMasterIds.addAll(tagIdList);
                return getResponseVOByCondition(allMasterIds, tagClassesListTree, tags);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ResponseVO.errorParams(ResponseEnum.DATA_GET_ERROR.getMessage());
        }
    }

    /**
     * 最终展示判断
     *
     * @param allMasterIds
     * @param tagClassesListTree
     * @param tags
     * @return
     */
    private ResponseVO<List<SysTagClassesTreeVO>> getResponseVOByCondition(Set<Long> allMasterIds, ResponseVO<List<SysTagClassesTreeVO>> tagClassesListTree, Set<Long> tags) {
        if (tagClassesListTree.getCode() == 0 && CollectionUtil.isNotEmpty(tagClassesListTree.getData())) {
            List<SysTagClassesTreeVO> data = tagClassesListTree.getData();
            //循环读取
            for (SysTagClassesTreeVO datum : data) {
                if (CollectionUtil.isNotEmpty(datum.getTagVOList())) {
                    for (SysTagTreeVO tagTreeVO : datum.getTagVOList()) {
                        if (allMasterIds.contains(tagTreeVO.getId())) {
                            //选中
                            tagTreeVO.setTagFlag(BusinessEnum.YES.getCode());
                        } else {
                            //未选中
                            tagTreeVO.setTagFlag(BusinessEnum.NO.getCode());
                        }
                        //引用
                        if (tags.contains(tagTreeVO.getId())) {
                            tagTreeVO.setTagFlag(BusinessEnum.USER.getCode());
                        }
                    }
                }
            }
            return ResponseVO.success(data);
        }
        return tagClassesListTree;
    }
}
