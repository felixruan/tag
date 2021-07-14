package org.etocrm.tagManager.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.etocrm.core.enums.BusinessEnum;
import org.etocrm.core.util.BeanUtils;
import org.etocrm.core.util.ResponseVO;
import org.etocrm.dynamicDataSource.util.BasePojo;
import org.etocrm.tagManager.enums.TagDictEnum;
import org.etocrm.tagManager.enums.TagErrorMsgEnum;
import org.etocrm.tagManager.mapper.ISysTagGroupMapper;
import org.etocrm.tagManager.mapper.ISysTagGroupRuleMapper;
import org.etocrm.tagManager.mapper.ISysTagPropertyMapper;
import org.etocrm.tagManager.model.DO.*;
import org.etocrm.tagManager.model.VO.tag.TagBrandsInfoVO;
import org.etocrm.tagManager.model.VO.tagProperty.SysTagPropertyEditRequestVO;
import org.etocrm.tagManager.model.VO.tagProperty.SysTagPropertyEditVO;
import org.etocrm.tagManager.model.VO.tagProperty.SysTagPropertyRuleEditVO;
import org.etocrm.tagManager.model.VO.tagProperty.SysTagPropertyRuleLogicalOperationVO;
import org.etocrm.tagManager.service.AsyncServiceManager;
import org.etocrm.tagManager.service.ISysTagPropertyRuleService;
import org.etocrm.tagManager.service.ISysTagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;


/**
 * <p>
 * 系统标签属性表  服务实现类
 * </p>
 */
@Service
@Slf4j
public class SysTagPropertyServiceTransactionImpl extends ServiceImpl<ISysTagPropertyMapper, SysTagPropertyDO> {

    @Autowired
    private ISysTagPropertyMapper sysTagPropertyMapper;

    @Autowired
    private ISysTagPropertyRuleService sysTagPropertyRuleService;

    @Autowired
    private ISysTagService sysTagService;

    @Autowired
    private ISysTagGroupRuleMapper tagGroupRuleMapper;

    @Autowired
    private ISysTagGroupMapper tagGroupMapper;

    @Autowired
    private AsyncServiceManager asyncServiceManager;

    /**
     * 编辑标签属性
     *
     * @param editVO
     * @return
     */
    @Transactional(rollbackFor = {Exception.class})
    public ResponseVO editSysTagProperty(SysTagPropertyEditRequestVO editVO, TagBrandsInfoVO brandsInfoVO, boolean masterFlag) {
        Long tagId = editVO.getTagId();

        //======= 1.检查标签是否存在
        SysTagDO tagDOFind = sysTagService.getTagById(tagId, brandsInfoVO);
        if (null == tagDOFind) {
            return ResponseVO.errorParams(TagErrorMsgEnum.TAG_NOT_EXISTS.getMessage());
        }

        //标签存在的属性ids
        final Set<Long> propertyIdList = this.getPropertyIdList(tagId);
        List<SysTagPropertyEditVO> propertyVOList = editVO.getTagPropertyList();

        //======= 2.启用状态下，检查标签属性
        int checkPropertyResult = 0;
        if (BusinessEnum.USING.getCode().equals(tagDOFind.getTagStatus())) {
            checkPropertyResult = this.checkPropertyModify(propertyIdList, propertyVOList, masterFlag, tagId);
        }
        //0 检查通过 1.启用群组使用，不可以修改  2.被群组引用的属性，不能删除。  3:有非此标签的属性id  4.属性名称重复
        switch (checkPropertyResult) {
            case 1:
                return ResponseVO.errorParams(TagErrorMsgEnum.TAG_USING.getMessage());
            case 2:
                return ResponseVO.errorParams("有被群组引用的属性被删除，修改失败");
            case 4:
                return ResponseVO.errorParams(TagErrorMsgEnum.TAG_PROPERTY_NAME_REPEAT.getMessage());
            case 0:
            case 3:
            default:
                break;
        }

        // TODO: 2020/9/15 检查数据是否规范
        //3.======= 检查 属性条件值
//        ResponseVO checkResponse = checkParamEditProperty(masterFlag, propertyVOList);
//        if (null != checkResponse) {
//            return checkResponse;
//        }

        //4.=======  删除 属性&规则
        this.removeProperty(propertyVOList,propertyIdList);

        //5.=======  saveOrUpdate 属性 & save规则
        this.saveProperty(tagId,propertyVOList,checkPropertyResult,propertyIdList);

        //6.=======  删除人群
        asyncServiceManager.asyncDeleteTag(tagId, tagDOFind.getTagType());
        
        //7.=======  覆盖人数置空 ,下次执行时间改成now,执行状态：未执行
        this.updateTagInfo(tagId);

        return ResponseVO.success();
    }

    private Set<Long> getPropertyIdList(Long tagId) {
        List<SysTagPropertyDO> propertyDOList = sysTagPropertyMapper.selectList(new LambdaQueryWrapper<SysTagPropertyDO>()
                .eq(SysTagPropertyDO::getTagId, tagId)
        );
        Set<Long> propertyIdList = new HashSet<>();
        if (CollUtil.isNotEmpty(propertyDOList)) {
            propertyIdList = propertyDOList.stream().map(propertyDO -> propertyDO.getId()).collect(Collectors.toSet());
        }
        return propertyIdList;
    }

    /**
     * @param propertyIdOldList 标签存在的属性ids
     * @param propertyVOList    请求的属性
     * @param masterFlag        是否是系统标签 true
     * @param tagId             标签id
     * @return 0:通过 1，标签被启用群组引用    2.标签属性被群组引用   3.传的属性id有非此标签的  4.属性名重复
     */
    private int checkPropertyModify(Set<Long> propertyIdOldList, List<SysTagPropertyEditVO> propertyVOList, boolean masterFlag, Long tagId) {
        int result = 0;
        //品牌标签 有属性
        Set<Long> needSavePropertyIds = new HashSet<Long>();
        if (CollUtil.isNotEmpty(propertyIdOldList) && !masterFlag) {
            //1.被启用群组引用标签
            if (this.checkTagGroupUsing(tagId)) {
                return 1;
            }

            //2.被群组引用属性 属性不能被删除
            Set<Long> usingPropertyIds = this.usingPropertyIdList(tagId);
            Set<Long> propertyIdNewList = propertyVOList.stream().filter(p -> null != p.getId()).map(property -> property.getId()).collect(Collectors.toSet());

            //存在的属性和传的属性的交集
            needSavePropertyIds.addAll(propertyIdOldList);
            needSavePropertyIds.retainAll(propertyIdNewList);
            //传的属性大于交集，说明传了非现有属性的id,保存时记得把传的非现有属性的id置空
            if (needSavePropertyIds.size() != propertyIdNewList.size()) {
                result = 3;
            }
            //使用的属性移除 传的属性
            usingPropertyIds.removeAll(propertyIdNewList);
            //如果移除传的属性之后还有数据，说明有使用中的属性被删除了
            if (CollUtil.isNotEmpty(usingPropertyIds)) {
                return 2;
            }
        }

        //检查属性名称是否重复
        Set<String> propertyNameList = propertyVOList.stream().map(propertyVO -> propertyVO.getPropertyName().trim()).collect(Collectors.toSet());
        if (propertyNameList.size() != propertyVOList.size()) {
            result = 4;
        }

        return result;
    }

    /**
     *     查询被群组引用的标签属性id
     */
    private Set<Long> usingPropertyIdList(Long tagId) {
        Set<Long> propertyId = new HashSet<>();
        List<SysTagGroupRuleDO> ruleDOList = tagGroupRuleMapper.selectList(Wrappers.lambdaQuery(SysTagGroupRuleDO.class).eq(SysTagGroupRuleDO::getTagId, tagId));
        ruleDOList.stream().forEach(rule -> {
            String ids = rule.getPropertyIds();
            if (StrUtil.isNotBlank(ids)) {
                String[] idArr = ids.split(",");
                for (String id : idArr) {
                    propertyId.add(Long.valueOf(id));
                }
            }
        });
        return propertyId;
    }

    /**
     *     启用状态下引用的标签id
     */
    private boolean checkTagGroupUsing(Long tagId) {
        //查引用了该标签的群组ids
        List<SysTagGroupRuleDO> ruleDOList = tagGroupRuleMapper.selectList(Wrappers.lambdaQuery(SysTagGroupRuleDO.class).eq(SysTagGroupRuleDO::getTagId, tagId));
        if (CollUtil.isEmpty(ruleDOList)) {
            return false;
        }
        //根据群组ids查询启用群组 ids
        Set<Long> existsGroupId = ruleDOList.stream().map(SysTagGroupRuleDO::getTagGroupId).collect(Collectors.toSet());
        Integer usingGroupSize = tagGroupMapper.selectCount(Wrappers.lambdaQuery(SysTagGroupDO.class).eq(SysTagGroupDO::getTagGroupStatus, BusinessEnum.USING).in(SysTagGroupDO::getId, existsGroupId));
        //判断数量是否相等
        return existsGroupId.size() != usingGroupSize;
    }

    /**
     * 删除属性&规则
     * @param propertyVOList
     * @param propertyIdList
     */
    private void removeProperty(List<SysTagPropertyEditVO> propertyVOList,Set<Long> propertyIdList) {
        Set<Long> needRemovePropertyIdList = this.getNeedRemovePropertyIds(propertyVOList, propertyIdList);

        if (CollUtil.isNotEmpty(needRemovePropertyIdList)) {
            //4.1 删除属性
            update(new LambdaUpdateWrapper<SysTagPropertyDO>()
                    .set(BasePojo::getDeleted, BusinessEnum.DELETED.getCode())
                    .in(SysTagPropertyDO::getId, needRemovePropertyIdList));
        }
        //4.2 删除属性规则
        if (CollUtil.isNotEmpty(propertyIdList)){
            sysTagPropertyRuleService.update(new LambdaUpdateWrapper<SysTagPropertyRuleDO>()
                    .set(SysTagPropertyRuleDO::getDeleted, BusinessEnum.DELETED.getCode())
                    .in(SysTagPropertyRuleDO::getPropertyId, propertyIdList));
        }
    }

    /**
     * 获取需要删除的属性ids
     * @param propertyVOList
     * @param propertyIdList
     * @return
     */
    private Set<Long> getNeedRemovePropertyIds(List<SysTagPropertyEditVO> propertyVOList, Set<Long> propertyIdList) {
        Set<Long> needRemovePropertyIdList  = new HashSet<Long>();

        if (CollUtil.isNotEmpty(propertyIdList)) {
            needRemovePropertyIdList.addAll(propertyIdList);
            Set<Long> propertyIdNewList = propertyVOList.stream().filter(p -> null != p.getId()).map(property -> property.getId()).collect(Collectors.toSet());
            needRemovePropertyIdList.removeAll(propertyIdNewList);
        }
        return needRemovePropertyIdList;
    }

    /**
     * 保存属性&规则
     * @param tagId             标签id
     * @param propertyVOList    保存的属性信息
     * @param checkPropertyResult 属性检查结果  3:传的属性id有非此标签的
     * @param propertyIdList     标签存在的属性ids
     */
    private void saveProperty(Long tagId,List<SysTagPropertyEditVO> propertyVOList,int checkPropertyResult,Set<Long> propertyIdList) {
        SysTagPropertyDO propertyDO;
        Long propertyId;
        for (SysTagPropertyEditVO propertyVO : propertyVOList) {
            propertyDO = new SysTagPropertyDO();
            propertyVO.setPropertyName(propertyVO.getPropertyName().trim());
            BeanUtils.copyPropertiesIgnoreNull(propertyVO, propertyDO);
            propertyDO.setTagId(tagId);
            if (checkPropertyResult == 3 && !propertyIdList.contains(propertyVO.getId())) {
                propertyDO.setId(null);
            }

            this.saveOrUpdate(propertyDO);

            propertyId = propertyDO.getId();
            List<SysTagPropertyRuleDO> ruleDOList = transferRuleEditVOList2RuleDOList(propertyId, propertyVO.getRule());
            sysTagPropertyRuleService.saveBatch(ruleDOList);
        }
    }

    private void updateTagInfo(Long tagId) {
        SysTagDO sysTagDOUpdate = new SysTagDO();
        sysTagDOUpdate.setId(tagId);
        sysTagDOUpdate.setCoveredPeopleNum(0);
        sysTagDOUpdate.setTagNextUpdateDate(new Date());
        sysTagDOUpdate.setTagPropertyChangeExecuteStatus(BusinessEnum.UNEXECUTED.getCode());
        sysTagService.update(sysTagDOUpdate);
    }


    /**
     * 编辑标签属性时检查参数
     *
     * @param masterFlag
     * @return
     */
    private ResponseVO checkParamEditProperty(boolean masterFlag, List<SysTagPropertyEditVO> propertyVOList) {
        if (!masterFlag) {
            List<SysTagPropertyRuleEditVO> ruleList;
            SysTagPropertyRuleLogicalOperationVO ruleInfo;
            String dictCode;
            String startValue;
            String endValue;
            for (SysTagPropertyEditVO propertyVO : propertyVOList) {
                ruleList = propertyVO.getRule();
                for (SysTagPropertyRuleEditVO rule : ruleList) {
                    ruleInfo = rule.getLogicalOperation();
                    dictCode = (null != ruleInfo.getChild() ? ruleInfo.getChild().getDictCode() : ruleInfo.getDictCode());
                    startValue = (null != ruleInfo.getChild() ? ruleInfo.getChild().getStartValue() : ruleInfo.getStartValue());
                    endValue = (null != ruleInfo.getChild() ? ruleInfo.getChild().getEndValue() : ruleInfo.getEndValue());
                    if ((needStartValue(dictCode) && StringUtils.isBlank(startValue)) || (needEndValue(dictCode) && StringUtils.isBlank(endValue))) {
                        return ResponseVO.errorParams("请输入属性为" + propertyVO.getPropertyName() + " 的条件运算值");
                    }
                }
            }
        }
        return null;
    }

    //需要开始值
    private boolean needStartValue(String dictCode) {
        // != 有值 无值
        String[] notNeedDictCodeArray = {TagDictEnum.TAG_DICT_NULL.getCode(), TagDictEnum.TAG_DICT_NOT_NULL.getCode()};
        return !Arrays.asList(notNeedDictCodeArray).contains(dictCode);
    }

    //需要结束值
    private boolean needEndValue(String dictCode) {
        String[] needDictCodeArray = {TagDictEnum.TAG_SITUATED_BETWEEN.getCode()
                , TagDictEnum.TAG_SUM_SITUATED_BETWEEN.getCode()
                , TagDictEnum.TAG_COUNT_SITUATED_BETWEEN.getCode()
                , TagDictEnum.TAG_DISTINCT_COUNT_SITUATED_BETWEEN.getCode()
                , TagDictEnum.TAG_AVG_SITUATED_BETWEEN.getCode()
        };
        return Arrays.asList(needDictCodeArray).contains(dictCode);
    }

    /**
     * 检查名称是否重复
     *
     * @param propertyNameList
     * @return
     */
    private boolean checkPropertyNameRepeat(List<String> propertyNameList) {
        HashSet propertyNameSet = new HashSet();
        propertyNameSet.addAll(propertyNameList);
        return propertyNameSet.size() != propertyNameList.size();
    }

    /**
     * @param ruleEditVOList
     * @return
     */
    private List<SysTagPropertyRuleDO> transferRuleEditVOList2RuleDOList(Long propertyId, List<SysTagPropertyRuleEditVO> ruleEditVOList) {
        List<SysTagPropertyRuleDO> ruleDOList = new ArrayList<>();
        SysTagPropertyRuleDO ruleDO;
        for (SysTagPropertyRuleEditVO ruleEditVO : ruleEditVOList) {
            ruleDO = new SysTagPropertyRuleDO();
            BeanUtils.copyPropertiesIgnoreNull(ruleEditVO, ruleDO);
            ruleDO.setPropertyId(propertyId);
            //todo 是否要验证数据？ 目前通过注解验证的。测试的时候关注一下
            // 根据id 获取子节点id>如果有子节点id>验证child>验证chid startValue ,endValue
            //进行数据的验证
            ruleDO.setColumns(JSON.toJSONString(ruleEditVO));
            ruleDOList.add(ruleDO);
        }
        return ruleDOList;
    }

//    private void checkRuleLogicalOperationParam(List<SysTagPropertyEditVO> propertyVOList) {
//        Long dictId = null;
//        for (SysTagPropertyEditVO editVO : propertyVOList) {
//            List<SysTagPropertyRuleEditVO> ruleEditVOList = editVO.getRule();
//            for (SysTagPropertyRuleEditVO ruleEditVO : ruleEditVOList) {
//                dictId = ruleEditVO.getLogicalOperation().getId();
//                dataManagerService.getDictByIdWithChild(dictId);
//
//            }
//        }
//
//    }

}
