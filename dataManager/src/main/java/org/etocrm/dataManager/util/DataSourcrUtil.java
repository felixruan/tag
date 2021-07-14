package org.etocrm.dataManager.util;

import com.alibaba.druid.pool.DruidDataSource;
import lombok.extern.slf4j.Slf4j;
import org.etocrm.dynamicDataSource.model.DO.SysDbSourceDO;

import java.sql.SQLException;

/**
 * @Author chengrong.yang
 * @date 2020/9/7 18:29
 */
@Slf4j
public class DataSourcrUtil {

    public static DruidDataSource dataSource(SysDbSourceDO config){
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
