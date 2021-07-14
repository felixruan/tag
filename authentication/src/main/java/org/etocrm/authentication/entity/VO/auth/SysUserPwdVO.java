package org.etocrm.authentication.entity.VO.auth;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.etocrm.authentication.entity.DO.SysPermissionDO;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * @Author chengrong.yang
 * @date 2020/8/18 14:27
 */
@ApiModel(value = "系统用户更新密码VO ")
@Data
public class SysUserPwdVO implements Serializable {

    private static final long serialVersionUID = 6722823651089444269L;

    @ApiModelProperty(value = "主键")
    private Integer id;

    @ApiModelProperty(value = "登录名")
    private String userAccount;

    @ApiModelProperty(value = "原用户密码")
    @NotBlank(message = "原密码不能为空")
    private String password;

    @ApiModelProperty(value = "新用户密码")
    @NotBlank(message = "新密码不能为空")
    private String newPassword;


}
