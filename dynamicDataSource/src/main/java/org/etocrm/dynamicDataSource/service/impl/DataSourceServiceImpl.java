package org.etocrm.dynamicDataSource.service.impl;

import com.alibaba.druid.pool.DruidDataSource;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.etocrm.core.enums.BusinessEnum;
import org.etocrm.core.enums.ResponseEnum;
import org.etocrm.core.util.ResponseVO;
import org.etocrm.dynamicDataSource.mapper.IDataSourceMapper;
import org.etocrm.dynamicDataSource.model.DO.SysDataSourceDO;
import org.etocrm.dynamicDataSource.model.DO.SysDbSourceDO;
import org.etocrm.dynamicDataSource.service.IDataSourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.List;

/**
 * @Author chengrong.yang
 * @date 2020/8/29 16:15
 */
@Service
@Slf4j
public class DataSourceServiceImpl implements IDataSourceService {

    @Autowired
    IDataSourceMapper dataSourceMapper;

    @Override
    public ResponseVO addDataSource(SysDataSourceDO sysDbSource) {
        try {
            dataSourceMapper.insert(sysDbSource);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ResponseVO.error(ResponseEnum.DATA_SOURCE_ADD_ERROR);
        }
        return ResponseVO.success();
    }

    @Override
    public List<SysDataSourceDO> getDataSource(SysDataSourceDO sysDataSourceDO) {
        try {
            LambdaQueryWrapper<SysDataSourceDO> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(SysDataSourceDO::getDataStatus, BusinessEnum.USING.getCode());
            lambdaQueryWrapper.eq(SysDataSourceDO::getDeleted, BusinessEnum.NOTDELETED.getCode());
//            lambdaQueryWrapper.eq(SysDataSourceDO::getDataFlag,BusinessEnum.ORIGIN.getCode());
            lambdaQueryWrapper.eq(SysDataSourceDO::getId, sysDataSourceDO.getId());
            List<SysDataSourceDO> sysDataSourceDOS = dataSourceMapper.selectList(lambdaQueryWrapper);
            return sysDataSourceDOS;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    @Override
    public ResponseVO updateDataSource(SysDataSourceDO sysDbSource) {
        return null;
    }

    @Override
    public ResponseVO selectById(Long originDatabaseId) {
        try {
            return ResponseVO.success(dataSourceMapper.selectById(originDatabaseId));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ResponseVO.error(ResponseEnum.DATA_SOURCE_CONNECTION_ERROR);
        }
    }

    @Override
    public ResponseVO selectListAll(LambdaQueryWrapper<SysDataSourceDO> sysDataSourceDOLambdaQueryWrapper) {
        try {
            return ResponseVO.success(dataSourceMapper.selectList(sysDataSourceDOLambdaQueryWrapper));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ResponseVO.error(ResponseEnum.DATA_SOURCE_CONNECTION_ERROR);
        }
    }

    @Override
    public ResponseVO selectAllDestination() {
        try {
            return ResponseVO.success(dataSourceMapper.selectList(new LambdaQueryWrapper<SysDataSourceDO>().eq(SysDataSourceDO::getDeleted, BusinessEnum.NOTDELETED.getCode())));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ResponseVO.error(ResponseEnum.DATA_SOURCE_CONNECTION_ERROR);
        }
    }

    @Override
    public ResponseVO selectBrandsId(Long originDatabaseId) {
        try {
            return ResponseVO.success(dataSourceMapper.selectById(originDatabaseId));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ResponseVO.error(ResponseEnum.DATA_SOURCE_CONNECTION_ERROR);
        }
    }

    @Override
    public SysDataSourceDO selectBrandsIdAndOrgId(Long originDatabaseId) {
        SysDataSourceDO sysDataSourceDO = dataSourceMapper.selectById(originDatabaseId);
        return sysDataSourceDO;

    }

    public DruidDataSource dataSource(SysDbSourceDO config) {
        DruidDataSource datasource = new DruidDataSource();
        datasource.setUrl(config.getDbUrl());
        datasource.setUsername(config.getDbUsername());
        datasource.setPassword(config.getDbPassword());
        datasource.setDriverClassName(config.getDbDriverClassName()); //configuration
        datasource.setInitialSize(config.getInitialSize());
        datasource.setMinIdle(config.getMinIdle());
        datasource.setMaxActive(config.getMaxActive());
        datasource.setMaxWait(config.getMaxWait());
        datasource.setTimeBetweenEvictionRunsMillis(config.getTimeBetweenEvictionRunsMillis());
        datasource.setMinEvictableIdleTimeMillis(config.getMinEvictableIdleTimeMillis());
        datasource.setValidationQuery(config.getValidationQuery());
        datasource.setTestWhileIdle(config.getTestWhileIdle());
        datasource.setTestOnBorrow(config.getTestOnBorrow());
        datasource.setTestOnReturn(config.getTestOnReturn());
        datasource.setPoolPreparedStatements(config.getPoolPreparedStatements());
        try {
            datasource.setFilters(config.getFilters());
            log.info("=======================druid connection initialization finished===============================");
        } catch (SQLException e) {
            log.error("druid configuration initialization filter", e);
        }
        return datasource;
    }
}
