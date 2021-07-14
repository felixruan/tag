package org.etocrm.dataManager.model.VO.dbSource;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.etocrm.dynamicDataSource.util.BasePojo;

import java.io.Serializable;

/**
 * @Author chengrong.yang
 * @date 2020/8/31 0:54
 */
@Api(value = "数据源关系" )
@Data
public class SysDbSourceVO implements Serializable {
    private static final long serialVersionUID = 1498722085296589674L;
    @ApiModelProperty(value = "主键" )
    private Long id ;
    @ApiModelProperty(value = "数据源ID" )
    private Long dataSourceId ;
    @ApiModelProperty(value = "数据库类型;数据字典" )
    private Long dbTypeId ;
    @ApiModelProperty(value = "数据库驱动类名称" )
    private String dbDriverClassName ;
    @ApiModelProperty(value = "数据库url" )
    private String dbUrl ;
    @ApiModelProperty(value = "数据库连接库名" )
    private String dbName ;
    @ApiModelProperty(value = "数据库用户名" )
    private String dbUsername ;
    @ApiModelProperty(value = "数据库密码" )
    private String dbPassword ;
    @ApiModelProperty(value = "数据库插件" )
    private String filters ;
    @ApiModelProperty(value = "初始化连接个数" )
    private Integer initialSize ;
    @ApiModelProperty(value = "初始化模式;数据字典 " )
    private String initializationMode ;
    @ApiModelProperty(value = "最大连接池数量" )
    private Integer maxActive ;
    @ApiModelProperty(value = "最小连接池数量" )
    private Integer minIdle ;
    @ApiModelProperty(value = "最大等待时间" )
    private Integer maxWait ;
    @ApiModelProperty(value = "最小生存时间" )
    private Integer minEvictableIdleTimeMillis ;
    @ApiModelProperty(value = "是否开启pscache" )
    private Boolean poolPreparedStatements ;
    @ApiModelProperty(value = "pscache最大缓存数量" )
    private Integer maxPoolPreparedStatementPerConnectionSize ;
    @ApiModelProperty(value = "申请连接时是否检查有效" )
    private Boolean testOnBorrow ;
    @ApiModelProperty(value = "归还连接时是否检查有效" )
    private Boolean testOnReturn ;
    @ApiModelProperty(value = "申请连接时如果在空闲时间内是否检查有效" )
    private Boolean testWhileIdle ;
    @ApiModelProperty(value = "连接间隔时间" )
    private Integer timeBetweenEvictionRunsMillis ;
    @ApiModelProperty(value = "检查SQL" )
    private String validationQuery ;

}
