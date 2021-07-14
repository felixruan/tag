package org.etocrm.dynamicDataSource.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.etocrm.dynamicDataSource.model.DO.DictionaryDO;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 数据字典 Mapper 接口
 * </p>
 *
 * @author dkx
 * @Date 2020-09-04
 */
@Mapper
public interface DictionaryMapper extends BaseMapper<DictionaryDO> {

}