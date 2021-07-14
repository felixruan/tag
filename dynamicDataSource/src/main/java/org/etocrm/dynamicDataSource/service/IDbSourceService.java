package org.etocrm.dynamicDataSource.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.etocrm.core.util.ResponseVO;
import org.etocrm.dynamicDataSource.model.DO.SysDbSourceDO;

/**
 * @Author: dkx
 * @Date: 20:18 2020/9/7
 * @Desc:
 */
public interface IDbSourceService {

    SysDbSourceDO selectSysDbSourceById(Long originDatabaseId);

    ResponseVO selectOne(LambdaQueryWrapper<SysDbSourceDO> sysDbSourceDOLambdaQueryWrapper);

    SysDbSourceDO selectByDbName(String tableSchema);
}
