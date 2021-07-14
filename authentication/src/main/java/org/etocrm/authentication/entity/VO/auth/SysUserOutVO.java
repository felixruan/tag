package org.etocrm.authentication.entity.VO.auth;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.etocrm.authentication.entity.DO.SysPermissionDO;
import org.etocrm.dynamicDataSource.util.BasePojo;

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

    private static final long serialVersionUID = -331370901905277696L;

    @ApiModelProperty(value = "主键")
    private Integer id;

    @ApiModelProperty(value = "登录名")
    private String userAccount;

    @ApiModelProperty(value = "姓名")
    private String userName;

    /*@ApiModelProperty(value = "用户英文名")
    private String userNameEn;*/

    @ApiModelProperty(value = "所属品牌id")
    private Long brandsId;
    @ApiModelProperty(value = "所属机构id")
    private Long organization;
    @ApiModelProperty(value = "所属品牌名称")
    private String brandsName;
    @ApiModelProperty(value = "所属机构名称")
    private String organizationName;

    /*@ApiModelProperty(value = "出生日期")
    private String brithday;*/

    @ApiModelProperty(value = "手机号")
    private String phone;

    @ApiModelProperty(value = "邮箱")
    private String email;

    /*@ApiModelProperty(value = "性别")
    private String sex;*/

    /*@ApiModelProperty(value = "最后登录时间")
    private Date lastLoginTime;*/

    @ApiModelProperty(value = "用户状态")
    private Integer status;

    @ApiModelProperty(value = "单点登陆用户ID")
    private Long ssoId;

    @ApiModelProperty(value = "创建时间")
    private String createdTime;

    @ApiModelProperty(value = "创建人id")
    private Integer createdBy;

    @ApiModelProperty(value = "创建人姓名")
    private String createdByName;



}
