package org.etocrm.authentication.entity.VO.auth;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @Author peter.li
 * @date 2020/8/18 14:27
 */
@ApiModel(value = "SSO RequestDataVO ")
@Data
public class SysSSORequestDataVO implements Serializable {

    private static final long serialVersionUID = -315885924832454549L;

    @ApiModelProperty(value = "请求响应code")
    private Long errcode;

    @ApiModelProperty(value = "请求响应data")
    private SysSSODataVO data;

}
