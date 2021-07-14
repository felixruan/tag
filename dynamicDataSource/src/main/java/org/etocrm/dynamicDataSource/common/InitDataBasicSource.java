package org.etocrm.dynamicDataSource.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;

/**
 * @Author chengrong.yang
 * @date 2020/8/29 14:08
 */
//@Component
@Slf4j
public class InitDataBasicSource implements ApplicationRunner {

//    @Autowired
//    IDataSourceMapper dataSourceMapper;
//
//    @Autowired
//    IDBSourceMapper dbSourceMapper;

    @Override
    public void run(ApplicationArguments args) throws Exception {
//        log.info("=================== initialization dataSource start ===================");
//        LambdaQueryWrapper<SysDataSourceDO> dataSourceQueryWrapper = new LambdaQueryWrapper<>();
//        dataSourceQueryWrapper.eq(SysDataSourceDO::getDataStatus, BusinessEnum.USING.getCode());
//        dataSourceQueryWrapper.eq(SysDataSourceDO::getDeleted, BusinessEnum.NOTDELETED.getCode());
//        List<SysDataSourceDO> dataSourceList = dataSourceMapper.selectList(dataSourceQueryWrapper);
//        for (SysDataSourceDO dataSource : dataSourceList) {
//            log.info("=======================" + dataSource.getDataName() + "connection initialization start ===============================");
//            LambdaQueryWrapper<SysDbSourceDO> sysDbSourceDO = new LambdaQueryWrapper<>();
//            sysDbSourceDO.eq(SysDbSourceDO::getDataSourceId, dataSource.getId());
//            List<SysDbSourceDO> dbSourceList = dbSourceMapper.selectList(sysDbSourceDO);
//            for (SysDbSourceDO config : dbSourceList) {
//                log.info("=======================" + config.getDataSourceId() + "connection initialization finished===============================");
//                DynamicDataSource.addDataSource(config.getDataSourceId(), dataSource(config));
//            }
//            log.info("=======================" + dataSource.getDataName() + "connection initialization finished ===============================");
//        }
//        log.info("=================== initialization dataSource finished ===================");
    }

//    public DruidDataSource dataSource(SysDbSourceDO config) {
//        DruidDataSource datasource = new DruidDataSource();
//        datasource.setUrl(config.getDbUrl());
//        datasource.setUsername(config.getDbUsername());
//        datasource.setPassword(config.getDbPassword());
//        datasource.setDriverClassName(config.getDbDriverClassName()); //configuration
//        datasource.setInitialSize(config.getInitialSize());
//        datasource.setMinIdle(config.getMinIdle());
//        datasource.setMaxActive(config.getMaxActive());
//        datasource.setMaxWait(config.getMaxWait());
//        datasource.setTimeBetweenEvictionRunsMillis(config.getTimeBetweenEvictionRunsMillis());
//        datasource.setMinEvictableIdleTimeMillis(config.getMinEvictableIdleTimeMillis());
//        datasource.setValidationQuery(config.getValidationQuery());
//        datasource.setTestWhileIdle(config.getTestWhileIdle());
//        datasource.setTestOnBorrow(config.getTestOnBorrow());
//        datasource.setTestOnReturn(config.getTestOnReturn());
//        datasource.setPoolPreparedStatements(config.getPoolPreparedStatements());
//        try {
//            datasource.setFilters(config.getFilters());
//            log.info("=======================druid connection initialization finished===============================");
//        } catch (SQLException e) {
//            log.error("druid configuration initialization filter", e);
//        }
//        return datasource;
//    }
}
