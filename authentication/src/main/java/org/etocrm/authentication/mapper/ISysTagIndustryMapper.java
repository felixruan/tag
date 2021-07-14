package org.etocrm.authentication.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.etocrm.authentication.entity.DO.SysTagIndustryDO;

/**
 * <p>
 * 系统标签与行业关联表  Mapper 接口
 * </p>
 */
@Mapper
public interface ISysTagIndustryMapper extends BaseMapper<SysTagIndustryDO> {

}