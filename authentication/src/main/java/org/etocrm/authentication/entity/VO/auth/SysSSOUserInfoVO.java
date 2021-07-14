package org.etocrm.authentication.entity.VO.auth;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @Author peter.li
 * @date 2020/8/18 14:27
 */
@ApiModel(value = "用户信息data VO ")
@Data
public class SysSSOUserInfoVO implements Serializable {

    private static final long serialVersionUID = -315885924832454549L;

    @ApiModelProperty(value = "用户ssoId")
    private Long userId;

    @ApiModelProperty(value = "用户邮箱")
    private String email;

}
