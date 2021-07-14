package org.etocrm.tagManager.model.VO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.etocrm.tagManager.model.DO.SysPermissionDO;


import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @Author chengrong.yang
 * @date 2020/8/18 14:27
 */
@ApiModel(value = "系统用户出参VO ")
@Data
public class SysUserOutVO implements Serializable {

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
    private Long brandsId ;
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
    @ApiModelProperty(value = "用户角色关联关系" )
    private List<SysPermissionDO> sysPerms;

}
