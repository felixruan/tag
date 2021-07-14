package org.etocrm.authentication.entity.VO.auth;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @Author peter.li
 * @date 2020/8/18 14:27
 */
@ApiModel(value = "SSO DataVO ")
@Data
public class SysSSODataVO implements Serializable {

    private static final long serialVersionUID = -7028400169595408879L;

    @ApiModelProperty(value = "用户信息data")
    private SysSSOUserInfoVO userInfo;

}
