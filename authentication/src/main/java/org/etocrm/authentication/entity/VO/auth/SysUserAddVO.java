package org.etocrm.authentication.entity.VO.auth;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NonNull;
import org.etocrm.authentication.entity.DO.SysPermissionDO;
import org.etocrm.core.enums.ResponseEnum;
import org.etocrm.core.util.ResponseVO;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * @Author chengrong.yang
 * @date 2020/8/18 14:27
 */
@ApiModel(value = "系统用户新增VO ")
@Data
public class SysUserAddVO implements Serializable {

    private static final long serialVersionUID = 5795725071621795072L;

    @ApiModelProperty(value = "登录名")
    @NotBlank(message = "登录名不能为空")
    private String userAccount;

    //   @ApiModelProperty(value = "用户密码")
    //   @NotBlank(message = "用户密码不能为空")
    //   private String password;
    @ApiModelProperty(value = "所属机构id")
    @NotNull(message = "所属机构id不能为空")
    private Long organization;

    @ApiModelProperty(value = "姓名")
    @NotBlank(message = "姓名不能为空")
    private String userName;

    @ApiModelProperty(value = "所属品牌id")
    @NotNull(message = "所属品牌id不能为空")
    private Long brandsId;

    /*@ApiModelProperty(value = "出生日期")
    private String brithday;*/

    @ApiModelProperty(value = "手机号")
    @NotBlank(message = "手机号不能为空")
    private String phone;

    @ApiModelProperty(value = "邮箱")
    @NotBlank(message = "邮箱不能为空")
    private String email;

    /*@ApiModelProperty(value = "性别")
    @NotBlank(message = "性别不能为空")
    private String sex;*/

    @ApiModelProperty(value = "单点登陆用户ID")
    @NotNull(message = "单点登陆用户ID不能为空")
    private Long ssoId;

}
