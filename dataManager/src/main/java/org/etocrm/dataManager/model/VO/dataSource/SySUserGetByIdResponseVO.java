package org.etocrm.dataManager.model.VO.dataSource;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @Date 2020/9/15 9:51
 *
 */
@Data
@Api(value = "用户信息实体类")
public class SySUserGetByIdResponseVO implements Serializable {

    private static final long serialVersionUID = -818543446288211661L;
    @ApiModelProperty(value = "主键" )
    private Integer id ;
    @ApiModelProperty(value = "用户编码" )
    private String userCode ;
    @ApiModelProperty(value = "用户账号" )
    private String userAccount ;
    @ApiModelProperty(value = "用户密码" )
    private String password ;
    @ApiModelProperty(value = "用户名" )
    private String userName ;
    @ApiModelProperty(value = "用户英文名" )
    private String userNameEn ;
    @ApiModelProperty(value = "所属品牌" )
    private Integer brandsId ;
    @ApiModelProperty(value = "出生日期" )
    private String brithday ;
    @ApiModelProperty(value = "手机号" )
    private String phone ;
    @ApiModelProperty(value = "邮箱" )
    private String email ;
    @ApiModelProperty(value = "性别" )
    private String sex ;
    @ApiModelProperty(value = "最后登录时间" )
    private Date lastLoginTime ;
    @ApiModelProperty(value = "用户状态;数据字典" )
    private Integer status ;
    @ApiModelProperty(value = "单点登录id" )
    private Long ssoId;


}
