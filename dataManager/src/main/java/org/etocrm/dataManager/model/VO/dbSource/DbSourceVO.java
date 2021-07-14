package org.etocrm.dataManager.model.VO.dbSource;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @Date 2020/9/8 16:56
 */
@Configuration
@ConfigurationProperties(prefix = "spring.datasource")
public class DbSourceVO {

    //数据库插件

    private static String filters;
    //初始化连接个数
    private static Integer initialSize;
    //初始化模式;数据字典
    private static String initializationMode;
    //"最大连接池数量
    private static Integer maxActive;
    //最小连接池数量
    private static Integer minIdle;
    //最大等待时间
    private static Integer maxWait;
    //最小生存时间
    private static Integer minEvictableIdleTimeMillis;
    //是否开启pscache
    private static Boolean poolPreparedStatements;
    //pscache最大缓存数量
    private static Integer maxPoolPreparedStatementPerConnectionSize;
    //申请连接时是否检查有效
    private static Boolean testOnBorrow;
    //归还连接时是否检查有效
    private static Boolean testOnReturn;
    //申请连接时如果在空闲时间内是否检查有效
    private static Boolean testWhileIdle;
    //连接间隔时间
    private static Integer timeBetweenEvictionRunsMillis;
    //检查SQL
    private static String validationQuery;

    public  String getFilters() {
        return filters;
    }

    public  void setFilters(String filters) {
        DbSourceVO.filters = filters;
    }

    public  Integer getInitialSize() {
        return initialSize;
    }

    public  void setInitialSize(Integer initialSize) {
        DbSourceVO.initialSize = initialSize;
    }

    public  String getInitializationMode() {
        return initializationMode;
    }

    public  void setInitializationMode(String initializationMode) {
        DbSourceVO.initializationMode = initializationMode;
    }

    public  Integer getMaxActive() {
        return maxActive;
    }

    public  void setMaxActive(Integer maxActive) {
        DbSourceVO.maxActive = maxActive;
    }

    public  Integer getMinIdle() {
        return minIdle;
    }

    public  void setMinIdle(Integer minIdle) {
        DbSourceVO.minIdle = minIdle;
    }

    public  Integer getMaxWait() {
        return maxWait;
    }

    public  void setMaxWait(Integer maxWait) {
        DbSourceVO.maxWait = maxWait;
    }

    public  Integer getMinEvictableIdleTimeMillis() {
        return minEvictableIdleTimeMillis;
    }

    public void setMinEvictableIdleTimeMillis(Integer minEvictableIdleTimeMillis) {
        DbSourceVO.minEvictableIdleTimeMillis = minEvictableIdleTimeMillis;
    }

    public  Boolean getPoolPreparedStatements() {
        return poolPreparedStatements;
    }

    public  void setPoolPreparedStatements(Boolean poolPreparedStatements) {
        DbSourceVO.poolPreparedStatements = poolPreparedStatements;
    }

    public  Integer getMaxPoolPreparedStatementPerConnectionSize() {
        return maxPoolPreparedStatementPerConnectionSize;
    }

    public  void setMaxPoolPreparedStatementPerConnectionSize(Integer maxPoolPreparedStatementPerConnectionSize) {
        DbSourceVO.maxPoolPreparedStatementPerConnectionSize = maxPoolPreparedStatementPerConnectionSize;
    }

    public  Boolean getTestOnBorrow() {
        return testOnBorrow;
    }

    public  void setTestOnBorrow(Boolean testOnBorrow) {
        DbSourceVO.testOnBorrow = testOnBorrow;
    }

    public  Boolean getTestOnReturn() {
        return testOnReturn;
    }

    public  void setTestOnReturn(Boolean testOnReturn) {
        DbSourceVO.testOnReturn = testOnReturn;
    }

    public  Boolean getTestWhileIdle() {
        return testWhileIdle;
    }

    public  void setTestWhileIdle(Boolean testWhileIdle) {
        DbSourceVO.testWhileIdle = testWhileIdle;
    }

    public  Integer getTimeBetweenEvictionRunsMillis() {
        return timeBetweenEvictionRunsMillis;
    }

    public  void setTimeBetweenEvictionRunsMillis(Integer timeBetweenEvictionRunsMillis) {
        DbSourceVO.timeBetweenEvictionRunsMillis = timeBetweenEvictionRunsMillis;
    }

    public  String getValidationQuery() {
        return validationQuery;
    }

    public  void setValidationQuery(String validationQuery) {
        DbSourceVO.validationQuery = validationQuery;
    }
}
