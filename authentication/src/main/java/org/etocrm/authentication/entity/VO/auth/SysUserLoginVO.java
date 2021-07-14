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
@ApiModel(value = "用户登录入参VO ")
@Data
public class SysUserLoginVO implements Serializable {

    private static final long serialVersionUID = 5543023286742559358L;

    @ApiModelProperty(value = "登录名")
    @NotBlank(message = "登录名不能为空")
    private String userAccount;
    @ApiModelProperty(value = "用户密码")
    @NotBlank(message = "用户密码不能为空")
    private String password;

}
