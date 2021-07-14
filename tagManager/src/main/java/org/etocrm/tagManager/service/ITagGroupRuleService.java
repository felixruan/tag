package org.etocrm.tagManager.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.etocrm.tagManager.model.DO.SysTagGroupRuleDO;
import org.etocrm.tagManager.model.VO.tagGroup.SysTagGroupRuleRequestVO;
import org.etocrm.tagManager.model.VO.tagGroup.SysTagGroupRuleResponseVO;

import java.util.List;

public interface ITagGroupRuleService extends IService<SysTagGroupRuleDO> {

    List<SysTagGroupRuleResponseVO> getRule(Long tagGroupId);

    List<SysTagGroupRuleRequestVO> getCopyRule(Long tagGroupId);
}
