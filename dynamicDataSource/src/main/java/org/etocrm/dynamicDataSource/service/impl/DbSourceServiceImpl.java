package org.etocrm.dynamicDataSource.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.etocrm.core.enums.BusinessEnum;
import org.etocrm.core.enums.ResponseEnum;
import org.etocrm.core.util.ResponseVO;
import org.etocrm.dynamicDataSource.mapper.IDBSourceMapper;
import org.etocrm.dynamicDataSource.model.DO.SysDbSourceDO;
import org.etocrm.dynamicDataSource.service.IDbSourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author: dkx
 * @Date: 20:19 2020/9/7
 * @Desc:
 */
@Service
@Slf4j
public class DbSourceServiceImpl implements IDbSourceService {

    @Autowired
    private IDBSourceMapper idbSourceMapper;

    @Override
    public SysDbSourceDO selectSysDbSourceById(Long originDatabaseId) {
        SysDbSourceDO sysDbSourceDO = null;
        try {
            LambdaQueryWrapper<SysDbSourceDO> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(SysDbSourceDO::getDataSourceId, originDatabaseId);
            lambdaQueryWrapper.eq(SysDbSourceDO::getDeleted, BusinessEnum.NOTDELETED.getCode());
            sysDbSourceDO = idbSourceMapper.selectOne(lambdaQueryWrapper);
            return sysDbSourceDO;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    @Override
    public ResponseVO selectOne(LambdaQueryWrapper<SysDbSourceDO> sysDbSourceDOLambdaQueryWrapper) {
        try {
            return ResponseVO.success(idbSourceMapper.selectOne(sysDbSourceDOLambdaQueryWrapper));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ResponseVO.error(ResponseEnum.TIMEOUT);
        }
    }

    @Override
    public SysDbSourceDO selectByDbName(String tableSchema) {
        return idbSourceMapper.selectOne(new LambdaQueryWrapper<SysDbSourceDO>()
                .eq(SysDbSourceDO::getDbName, tableSchema)
                .eq(SysDbSourceDO::getDeleted, BusinessEnum.NOTDELETED.getCode()));
    }
}
