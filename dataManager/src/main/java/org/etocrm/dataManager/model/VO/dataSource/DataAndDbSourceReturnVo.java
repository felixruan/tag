package org.etocrm.dataManager.model.VO.dataSource;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @Date 2020/9/17 11:24
 */
@Data
@Api(value = "系统数据源编辑信息 ")
public class DataAndDbSourceReturnVo {
    @ApiModelProperty(value = "主键")
    private Long id;

    @ApiModelProperty(value = "所属品牌")
    private Long brandsId;

    @ApiModelProperty(value = "所属机构")
    private Long orgId;

    @ApiModelProperty(value = "数据源名称")
    private String dataName;

    @ApiModelProperty(value = "数据源描述")
    private String dataMemo;

    @ApiModelProperty(value = "排序字段")
    private Integer orderNumber;

    @ApiModelProperty(value = "数据库ID" )
    private Long DbSourceId ;

    @ApiModelProperty(value = "数据库类型;数据字典" )
    private Long dbType ;

    @ApiModelProperty(value = "数据库驱动类名称" )
    private String dbDriverClassName ;

//    @ApiModelProperty(value = "数据库url" )
    private String dbUrl ;

    @ApiModelProperty(value = "数据库连接库名" )
    private String dbName ;

    @ApiModelProperty(value = "数据库用户名" )
    private String dbUsername ;

    @ApiModelProperty(value = "数据库密码" )
    private String dbPassword ;
    //端口号
    @ApiModelProperty(value = "端口号" )
    private Integer dbPort;
    //数据源连接参数
    @ApiModelProperty(value = "数据源连接参数" )
    private String urlParams;

    //数据源ip地址
    @ApiModelProperty(value = "数据源ip地址" )
    private String dbHost;

    @ApiModelProperty(value = "数据源类型" )
    private String dataFlag;
}
