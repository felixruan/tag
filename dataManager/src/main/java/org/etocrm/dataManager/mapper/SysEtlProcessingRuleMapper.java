package org.etocrm.dataManager.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.etocrm.dataManager.model.DO.EtlProcessingRuleDO;

@Mapper
public interface SysEtlProcessingRuleMapper extends BaseMapper<EtlProcessingRuleDO> {
}
