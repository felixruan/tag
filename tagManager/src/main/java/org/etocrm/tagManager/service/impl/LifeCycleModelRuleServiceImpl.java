package org.etocrm.tagManager.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.etocrm.tagManager.mapper.ISysLifeCycleModelRuleMapper;
import org.etocrm.tagManager.model.DO.SysLifeCycleModelRuleDO;
import org.etocrm.tagManager.service.ILifeCycleModelRuleService;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class LifeCycleModelRuleServiceImpl extends ServiceImpl<ISysLifeCycleModelRuleMapper, SysLifeCycleModelRuleDO> implements ILifeCycleModelRuleService {

}
