package org.etocrm.authentication.entity.VO.auth;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @Author chengrong.yang
 * @date 2020/8/18 14:27
 */
@ApiModel(value = "菜单更新入参VO ")
@Data
public class SysMenuUpdateVO implements Serializable {

    private static final long serialVersionUID = -8588296310310668537L;

    @ApiModelProperty(value = "主键")
    private Integer id;

    @ApiModelProperty(value = "页面名称")
    private String menuName;

    @ApiModelProperty(value = "可见状态 1表示可见,0表示隐藏")
    @NotNull(message = "可见状态不能为空")
    private Integer menuStatus;

    @ApiModelProperty(value = "菜单排序")
    @NotNull(message = "菜单排序不能为空")
    private Integer menuOrder;

    @ApiModelProperty(value = "页面地址")
    private String menuRoute;

    @ApiModelProperty(value = "页面说明")
    private String menuMemo;

    @ApiModelProperty(value = "页面icon")
    private String icon;


}
