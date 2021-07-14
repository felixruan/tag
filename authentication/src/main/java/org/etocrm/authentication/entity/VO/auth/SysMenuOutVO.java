package org.etocrm.authentication.entity.VO.auth;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @Author chengrong.yang
 * @date 2020/8/18 14:27
 */
@ApiModel(value = "系统菜单出参VO ")
@Data
public class SysMenuOutVO implements Serializable,Comparable<SysMenuOutVO> {

    private static final long serialVersionUID = -5779277770468460132L;
    @ApiModelProperty(value = "主键")
    private Integer id;

    @ApiModelProperty(value = "页面名称")
    private String menuName;

   /* @ApiModelProperty(value = "菜单级别 0表示1级菜单,1表示是2级菜单")
    private Integer menuLev;*/

    @ApiModelProperty(value = "父页面id ")
    private Integer menuParentId;

    @ApiModelProperty(value = "页面地址")
    private String menuRoute;

    @ApiModelProperty(value = "可见状态 0表示可见,1表示隐藏")
    private Integer menuStatus;

    @ApiModelProperty(value = "页面排序")
    private Integer menuOrder;

    @ApiModelProperty(value = "菜单描述")
    private String menuMemo;

    @ApiModelProperty(value = "图标")
    private String icon;


    @Override
    public int compareTo(SysMenuOutVO o) {
        return o.getMenuOrder().compareTo(this.getMenuOrder());
    }
}
