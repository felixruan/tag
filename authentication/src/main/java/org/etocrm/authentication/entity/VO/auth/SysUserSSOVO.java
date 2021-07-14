package org.etocrm.authentication.entity.VO.auth;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * @Author chengrong.yang
 * @date 2020/8/18 14:27
 */
@ApiModel(value = "SSO登录入参VO ")
@Data
public class SysUserSSOVO implements Serializable {


    private static final long serialVersionUID = 1466100518472463599L;
    @ApiModelProperty(value = "用户id" )
    private Integer id ;
    @ApiModelProperty(value = "登录名" )
    @NotBlank(message = "登录名不能为空")
    private String userAccount ;

}
