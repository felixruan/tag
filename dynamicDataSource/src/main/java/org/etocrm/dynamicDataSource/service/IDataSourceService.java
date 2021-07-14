package org.etocrm.dynamicDataSource.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.etocrm.core.util.ResponseVO;
import org.etocrm.dynamicDataSource.model.DO.SysDataSourceDO;
import org.etocrm.dynamicDataSource.model.DO.SysDbSourceDO;

import java.util.List;

/**
 * @Author chengrong.yang
 * @date 2020/8/29 16:15
 */
public interface IDataSourceService {

    ResponseVO addDataSource(SysDataSourceDO sysDataSource);

    List<SysDataSourceDO> getDataSource(SysDataSourceDO sysDataSource);

    ResponseVO updateDataSource(SysDataSourceDO sysDataSource);

    ResponseVO selectById(Long originDatabaseId);

    ResponseVO selectListAll(LambdaQueryWrapper<SysDataSourceDO> sysDataSourceDOLambdaQueryWrapper);

    ResponseVO<List<SysDataSourceDO>> selectAllDestination();

    ResponseVO<SysDataSourceDO> selectBrandsId(Long originDatabaseId);

    SysDataSourceDO selectBrandsIdAndOrgId(Long originDatabaseId);
}
