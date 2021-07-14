package org.etocrm.tagManager.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.etocrm.core.enums.BusinessEnum;
import org.etocrm.core.util.BeanUtils;
import org.etocrm.tagManager.mapper.ISysTagGroupRuleMapper;
import org.etocrm.tagManager.mapper.ISysTagMapper;
import org.etocrm.tagManager.mapper.ISysTagPropertyMapper;
import org.etocrm.tagManager.model.DO.SysTagDO;
import org.etocrm.tagManager.model.DO.SysTagGroupRuleDO;
import org.etocrm.tagManager.model.DO.SysTagPropertyDO;
import org.etocrm.tagManager.model.VO.tagGroup.*;
import org.etocrm.tagManager.service.ITagGroupRuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TagGroupRuleServiceImpl extends ServiceImpl<ISysTagGroupRuleMapper, SysTagGroupRuleDO> implements ITagGroupRuleService {

    @Autowired
    private ISysTagGroupRuleMapper sysTagGroupRuleMapper;

    @Autowired
    private ISysTagMapper sysTagMapper;

    @Autowired
    private ISysTagPropertyMapper sysTagPropertyMapper;

    /**
     * 获取群组下的规则
     *
     * @param tagGroupId
     * @return
     */
    @Override
    public List<SysTagGroupRuleResponseVO> getRule(Long tagGroupId) {
        List<SysTagGroupRuleResponseVO> rule = new ArrayList<>();//群组一 群组二 关系

        //群组规则信息
        List<SysTagGroupRuleDO> ruleParentDOList = getRuleDOList(tagGroupId, 0L);

        List<SysTagGroupRuleInfoResponseVO> tagGroupRule;//每个群组内的标签
        SysTagGroupRuleResponseVO ruleResponseVO;
        SysTagGroupRuleInfoResponseVO ruleInfoResponseVO;

        for (SysTagGroupRuleDO ruleParentDO : ruleParentDOList) {
            //父群组转成返回类型
            ruleResponseVO = new SysTagGroupRuleResponseVO();
            BeanUtils.copyPropertiesIgnoreNull(ruleParentDO, ruleResponseVO);

            tagGroupRule = new ArrayList<>();//每个群组内的标签
            //查询
            List<SysTagGroupRuleDO> ruleDOList = getRuleDOList(tagGroupId, ruleParentDO.getId());
            SysTagDO tagDO;
            for (SysTagGroupRuleDO ruleDO : ruleDOList) {
                ruleInfoResponseVO = JSONObject.parseObject(ruleDO.getTagGroupRule(), SysTagGroupRuleInfoResponseVO.class);

                //set 标签名称
                tagDO = sysTagMapper.selectById(ruleInfoResponseVO.getTagId());
                if (null != tagDO) {
                    ruleInfoResponseVO.setTagName(tagDO.getTagName());
                }
                //set 标签属性名称
                dealTagProperty(ruleInfoResponseVO.getLogicalOperationValue());

                tagGroupRule.add(ruleInfoResponseVO);
            }

            ruleResponseVO.setTagGroupRule(tagGroupRule);
            rule.add(ruleResponseVO);
        }
        return rule;
    }

    @Override
    public List<SysTagGroupRuleRequestVO> getCopyRule(Long tagGroupId) {
        List<SysTagGroupRuleRequestVO> rule = new ArrayList<>();//群组一 群组二 关系

        //群组规则信息
        List<SysTagGroupRuleDO> ruleParentDOList = getRuleDOList(tagGroupId, 0L);

        List<SysTagGroupRuleInfoRequestVO> tagGroupRule;//每个群组内的标签
        SysTagGroupRuleRequestVO ruleRequestVO;
        SysTagGroupRuleInfoRequestVO ruleInfoRequestVO;

        for (SysTagGroupRuleDO ruleParentDO : ruleParentDOList) {
            //父群组转成返回类型
            ruleRequestVO = new SysTagGroupRuleRequestVO();
            BeanUtils.copyPropertiesIgnoreNull(ruleParentDO, ruleRequestVO);

            tagGroupRule = new ArrayList<>();//每个群组内的标签
            //查询
            List<SysTagGroupRuleDO> ruleDOList = getRuleDOList(tagGroupId, ruleParentDO.getId());
            for (SysTagGroupRuleDO ruleDO : ruleDOList) {
                ruleInfoRequestVO = JSONObject.parseObject(ruleDO.getTagGroupRule(), SysTagGroupRuleInfoRequestVO.class);
                tagGroupRule.add(ruleInfoRequestVO);
            }

            ruleRequestVO.setTagGroupRule(tagGroupRule);
            rule.add(ruleRequestVO);
        }
        return rule;
    }

    /**
     * 获取群组规则list
     *
     * @param tagGroupId
     * @param ruleParentId
     * @return
     */
    private List<SysTagGroupRuleDO> getRuleDOList(Long tagGroupId, Long ruleParentId) {
        SysTagGroupRuleDO groupRuleDO = new SysTagGroupRuleDO();
        groupRuleDO.setTagGroupId(tagGroupId);
        groupRuleDO.setTagGroupRuleParentId(ruleParentId);
        groupRuleDO.setDeleted(BusinessEnum.NOTDELETED.getCode());
        return sysTagGroupRuleMapper.selectList(new LambdaQueryWrapper<>(groupRuleDO));
    }
    /**
     * 处理标签属性名称
     *
     * @param propertyList
     */
    private void dealTagProperty(List<LogicalOperationValueResponseVO> propertyList) {
        for (LogicalOperationValueResponseVO property : propertyList) {
            property.setTagPropertyName(getPropertyNameById(property.getTagPropertyId()));
        }
    }
    /**
     * 根据属性id获取属性名称
     *
     * @param propertyId
     * @return
     */
    private String getPropertyNameById(Long propertyId) {
        SysTagPropertyDO propertyDO = sysTagPropertyMapper.selectById(propertyId);
        return null != propertyDO ? propertyDO.getPropertyName() : "";
    }
}
