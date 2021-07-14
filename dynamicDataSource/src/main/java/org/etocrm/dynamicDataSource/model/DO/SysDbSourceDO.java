package org.etocrm.dynamicDataSource.model.DO;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.etocrm.dynamicDataSource.util.BasePojo;

import java.io.Serializable;

/**
 * @Author chengrong.yang
 * @date 2020/8/29 12:24
 */
@Data
@TableName("sys_db_source")
public class SysDbSourceDO extends BasePojo implements Serializable {

    private static final long serialVersionUID = 8249474886175724755L;

    private Long id;

    private Long dataSourceId;

    private Long dbTypeId;

    private String dbDriverClassName;

    private String dbUrl;

    private String dbName ;

    private String dbUsername;

    private String dbPassword;

    private String filters;

    private Integer initialSize;

    private String initializationMode;

    private Integer maxActive;

    private Integer minIdle;

    private Integer maxWait;

    private Integer minEvictableIdleTimeMillis;

    private Boolean poolPreparedStatements;

    private Integer maxPoolPreparedStatementPerConnectionSize;

    private Boolean testOnBorrow;

    private Boolean testOnReturn;

    private Boolean testWhileIdle;

    private Integer timeBetweenEvictionRunsMillis;

    private String validationQuery;

    private Integer dbPort;

    private String urlParams;

    private String dbHost;

}
