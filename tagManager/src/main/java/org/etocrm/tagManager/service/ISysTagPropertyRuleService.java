package org.etocrm.tagManager.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.etocrm.core.util.ResponseVO;
import org.etocrm.tagManager.model.DO.SysTagPropertyRuleDO;
import org.etocrm.tagManager.model.VO.tagProperty.SysTagPropertyRuleVO;

import java.util.List;
import java.util.Set;

/**
 * <p>
 * 系统标签属性规则表  服务类
 * </p>
 */
public interface ISysTagPropertyRuleService extends IService<SysTagPropertyRuleDO> {

    /**
     * 根据属性id删除
     * @param propertyId
     * @return
     */
    ResponseVO deleteByPropertyId(Long propertyId);

    List<SysTagPropertyRuleDO> getTagPropertyRuleByTagId(Long propertyId);

    ResponseVO<List<SysTagPropertyRuleDO>> getSysTagPropertyRuleIds(Set<Long> ids);

    ResponseVO insert(SysTagPropertyRuleDO sysTagPropertyRuleDO);

    ResponseVO updateSysTagPropertyRulesById(Set<Long> ids);
}