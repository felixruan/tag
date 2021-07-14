package org.etocrm.authentication.entity.DO;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.etocrm.dynamicDataSource.util.BasePojo;

import java.io.Serializable;

/**
 * @Author chengrong.yang
 * @date 2020/8/18 14:27
 */
@ApiModel(value = "系统菜单表DO ")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@JsonInclude(value = JsonInclude.Include.NON_NULL)
@TableName("sys_menu" )
public class SysMenuDO extends BasePojo implements Serializable {

    private static final long serialVersionUID = 6663573653141267898L;
    /** 主键 */
    @TableId
    @ApiModelProperty(value = "主键" )
    private Integer id ;
    @ApiModelProperty(value = "菜单名称" )
    private String menuName ;
    @ApiModelProperty(value = "所属品牌" )
    private Long brandsId ;
    @ApiModelProperty(value = "菜单级别 0表示1级菜单,1表示是2级菜单" )
    private Integer menuLev ;
    @ApiModelProperty(value = "上级菜单id " )
    private Integer menuParentId ;
    @ApiModelProperty(value = "菜单路由" )
    private String menuRoute ;
    @ApiModelProperty(value = "启用状态 0表示停用,1表示已启用" )
    private Integer menuStatus ;
    @ApiModelProperty(value = "菜单排序" )
    private Integer menuOrder ;
    @ApiModelProperty(value = "菜单描述" )
    private String menuMemo ;
    @ApiModelProperty(value = "是否是按钮,0不是按钮,1是按钮" )
    private Integer isButton ;
    @ApiModelProperty(value = "按钮名称" )
    private String buttonName ;
    @ApiModelProperty(value = "图标" )
    private String icon ;

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((menuName == null) ? 0 : menuName.hashCode());
        result = prime * result + ((brandsId == null) ? 0 : brandsId.hashCode());
        result = prime * result + ((menuLev == null) ? 0 : menuLev.hashCode());
        result = prime * result + ((menuParentId == null) ? 0 : menuParentId.hashCode());
        result = prime * result + ((menuRoute == null) ? 0 : menuRoute.hashCode());
        result = prime * result + ((menuStatus == null) ? 0 : menuStatus.hashCode());
        result = prime * result + ((menuMemo == null) ? 0 : menuMemo.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        SysMenuDO other = (SysMenuDO) obj;
        if (id == null) {
            if (other.id != null) {
                return false;
            }
        } else if (!id.equals(other.id)) {
            return false;
        }
        if (menuName == null) {
            if (other.menuName != null) {
                return false;
            }
        } else if (!menuName.equals(other.menuName)) {
            return false;
        }
        if (brandsId == null) {
            if (other.brandsId != null) {
                return false;
            }
        } else if (!brandsId.equals(other.brandsId)) {
            return false;
        }
        if (id == null) {
            if (other.id != null) {
                return false;
            }
        } else if (!id.equals(other.id)) {
            return false;
        }
        if (menuLev == null) {
            if (other.menuLev != null) {
                return false;
            }
        } else if (!menuLev.equals(other.menuLev)) {
            return false;
        }
        if (menuParentId == null) {
            if (other.menuParentId != null) {
                return false;
            }
        } else if (!menuParentId.equals(other.menuParentId)) {
            return false;
        }
        if (menuRoute == null) {
            if (other.menuRoute != null) {
                return false;
            }
        } else if (!menuRoute.equals(other.menuRoute)) {
            return false;
        }
        if (menuStatus == null) {
            if (other.menuStatus != null) {
                return false;
            }
        } else if (!menuStatus.equals(other.menuStatus)) {
            return false;
        }
        if (menuMemo == null) {
            if (other.menuMemo != null) {
                return false;
            }
        } else if (!menuMemo.equals(other.menuMemo)) {
            return false;
        }
        return true;
    }
}
