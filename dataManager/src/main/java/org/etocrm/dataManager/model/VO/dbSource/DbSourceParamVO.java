package org.etocrm.dataManager.model.VO.dbSource;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.io.Serializable;

/**
 * @Date 2020/9/15 18:58
 */
@Component
@ConfigurationProperties(prefix = "data-source-params")
public class DbSourceParamVO implements Serializable {
    //数据源拼接参数
    private static String mysqlUrlParams;
    //数据源corn表达式
    private static String dataSourceCorn;

    public  String getMysqlUrlParams() {
        return mysqlUrlParams;
    }

    public  void setMysqlUrlParams(String mysqlUrlParams) {
        DbSourceParamVO.mysqlUrlParams = mysqlUrlParams;
    }

    public  String getDataSourceCorn() {
        return dataSourceCorn;
    }

    public  void setDataSourceCorn(String dataSourceCorn) {
        DbSourceParamVO.dataSourceCorn = dataSourceCorn;
    }
}
