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
@ApiModel(value = "系统菜单新增VO ")
@Data
public class SysMenuAddVO implements Serializable {


    private static final long serialVersionUID = 3595410877291857704L;

    @ApiModelProperty(value = "页面名称" )
    @NotBlank(message = "页面名称不能为空")
    private String menuName ;

    @ApiModelProperty(value = "父页面id " )
    @NotNull(message = "父页面id不能为空")
    private Integer menuParentId ;

    @ApiModelProperty(value = "页面地址" )
    private String menuRoute ;

    @ApiModelProperty(value = "可见状态 1表示可见,0表示隐藏" )
    @NotNull(message = "可见状态只能是0或1")
    private Integer menuStatus ;

    @ApiModelProperty(value = "菜单排序" )
    @NotNull(message = "菜单排序不能为空")
    private Integer menuOrder ;

    @ApiModelProperty(value = "页面说明" )
    private String menuMemo ;

    @ApiModelProperty(value = "菜单icon" )
    @NotBlank(message = "菜单icon不能为空")
    private String icon ;


}
