package org.etocrm.dataManager.model.VO.dataSource;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

/**
 * @Date 2020/9/9 20:51
 */

@Data
@Api(value = "添加数据源实体类")
public class AddDataSourceVO {

    @ApiModelProperty(value = "数据源名称" )
    @NotBlank(message = "数据源名称不能为空")
    private String dataName;

    @ApiModelProperty(value = "数据库类型" )
    @NotNull(message = "数据库类型不能为空")
    private Long dbType;

//    @ApiModelProperty(value = "服务器地址" )
//    @NotBlank(message = "服务地址服务器不能为空")
    private String dbUrl;

    @ApiModelProperty(value = "端口号" )
    @NotNull(message = "端口号不能为空")
    private Integer dbPort;

    @ApiModelProperty(value = "数据库名称" )
    @NotBlank(message = "数据库名称不能为空")
    @Pattern(regexp = "^[a-zA-Z][a-zA-Z0-9_]{0,30}[a-zA-Z0-9]$",message = "数据库名称不合法:大小写字母开始,中间为字母|数字|下划线,不可以下划线结尾,长度限制2~30位")
    private String dbName;

    @ApiModelProperty(value = "登录名" )
    @NotBlank(message = "登录名不能为空")
    private String dbUserName;

    @ApiModelProperty(value = "登入密码" )
    @NotBlank(message = "登入密码不能为空")
    private String dbPassword ;

    @ApiModelProperty(value = "显示顺序" )
    @NotNull(message = "显示顺序不能为空")
    private Integer orderNumber;

    @ApiModelProperty(value = "品牌id" )
    @NotNull(message = "品牌id不能为空")
    private Long brandsId;

    @ApiModelProperty(value = "机构id" )
    @NotNull(message = "机构id不能为空")
    private Long orgId;

    @ApiModelProperty(value = "品牌名称" )
    private String brandName;

    @ApiModelProperty(value = "机构名称" )
    private String orgName;

    @ApiModelProperty(value = "数据源连接参数" )
    private String urlParams;

    @ApiModelProperty(value = "数据源ip地址" )
    @NotBlank(message = "数据源ip不能为空")
    private String dbHost;

    @ApiModelProperty(value = "数据源类型，根据dictParentCode:data_source_type 查询字典表获得dictValue" )
    @NotNull(message = "数据源类型不能为空")
    private Integer dataFlag;

    private Long id;



}