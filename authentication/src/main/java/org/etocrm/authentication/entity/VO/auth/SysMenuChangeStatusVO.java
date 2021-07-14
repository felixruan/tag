package org.etocrm.authentication.entity.VO.auth;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @Author chengrong.yang
 * @date 2020/8/18 14:27
 */
@ApiModel(value = "系统菜单启停VO ")
@Data
public class SysMenuChangeStatusVO implements Serializable {

    private static final long serialVersionUID = -4972623487227742955L;
    @ApiModelProperty(value = "页面id" )
    @NotNull(message = "页面id不能为空")
    private Integer id ;
    @ApiModelProperty(value = "启/停状态，0：启，1：停" )
    @NotNull(message = "状态不能为空,只能为0或1")
    private Integer status;


}
