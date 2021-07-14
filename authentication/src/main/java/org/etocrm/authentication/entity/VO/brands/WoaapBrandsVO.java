package org.etocrm.authentication.entity.VO.brands;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @Author: dkx
 * @Date: 13:48 2020/12/8
 * @Desc:
 */
@Data
public class WoaapBrandsVO implements Serializable {

    private static final long serialVersionUID = 3628137062900359592L;

    @ApiModelProperty(value = "微信的appid")
    private String appId;

    @NotNull(message = "对应woaapId不能为空")
    @ApiModelProperty(value = "对应woaapId")
    private Long woaapId;

    private String name;
}
