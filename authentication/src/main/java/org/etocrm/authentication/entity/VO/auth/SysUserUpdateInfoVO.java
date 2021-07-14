package org.etocrm.authentication.entity.VO.auth;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.etocrm.authentication.entity.DO.SysPermissionDO;

import java.io.Serializable;
import java.util.List;

/**
 * @Author chengrong.yang
 * @date 2020/8/18 14:27
 */
@ApiModel(value = "更新用户基本信息VO ")
@Data
public class SysUserUpdateInfoVO implements Serializable {

    private static final long serialVersionUID = 5120376903111094997L;

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

   /* @ApiModelProperty(value = "出生日期")
    private String brithday;*/

    @ApiModelProperty(value = "手机号")
    private String phone;

    @ApiModelProperty(value = "邮箱")
    private String email;

    /*@ApiModelProperty(value = "性别")
    private String sex;
*/
    @ApiModelProperty(value = "单点登陆用户ID")
    private Long ssoId;

}
