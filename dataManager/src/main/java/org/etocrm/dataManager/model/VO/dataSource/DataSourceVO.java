package org.etocrm.dataManager.model.VO.dataSource;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.io.Serializable;

/**
 * 数据源VO
 * @Date 2020/9/8 16:07
 */
@Data
@Api(value = "编辑数据源实体类")
public class DataSourceVO implements Serializable {

    @ApiModelProperty(value = "数据源主键" )
    @NotNull(message = "数据源主键id不能为空")
    private Long id;

    //数据源名称
    @ApiModelProperty(value = "数据源名称" )
    @NotBlank(message = "数据源名称不能为空")
    private String dataName;

    //数据库类型
    @ApiModelProperty(value = "数据库类型" )
    @NotNull(message = "数据库类型不能为空")
    private Long dbType;

    //服务器地址
//    @ApiModelProperty(value = "服务器地址" )
//    @NotBlank(message = "服务器地址不能为空")
    private String dbUrl;

    //端口号
    @ApiModelProperty(value = "端口号" )
    @NotNull(message = "端口号不能为空")
    private Integer dbPort;

    //数据库名称
    @ApiModelProperty(value = "数据库名称" )
    @NotBlank(message = "数据库名称不能为空")
    @Pattern(regexp = "^[a-zA-Z][a-zA-Z0-9_]{0,30}[a-zA-Z0-9]$",message = "数据库名称不合法:大小写字母开始,中间为字母|数字|下划线,不可以下划线结尾,长度限制2~30位")
    private String dbName;

    //登录名
    @ApiModelProperty(value = "登录名" )
    @NotBlank(message = "登录名不能为空")
    private String dbUserName;

    //登入密码
    @ApiModelProperty(value = "登入密码" )
    @NotBlank(message = "登入密码不能为空")
    private String dbPassword ;

    //显示顺序
    @ApiModelProperty(value = "显示顺序" )
    @NotNull(message = "显示顺序不能为空")
    private Integer orderNumber;


    //品牌id
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

    //数据源类型
    @ApiModelProperty(value = "数据源详情Id" )
    @NotNull(message = "数据源详情Id不能为空")
    private Long dbSourceId;

    //数据源连接参数
    @ApiModelProperty(value = "数据源连接参数" )
    private String urlParams;

    //数据源ip地址
    @ApiModelProperty(value = "数据源ip地址" )
    @NotBlank(message = "数据源ip不能为空")
    private String dbHost;

    @ApiModelProperty(value = "0 会员库  1粉丝库" )
    @NotNull(message = "数据库标志")
    private Integer dataFlag;

}
