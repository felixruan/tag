package org.etocrm.authentication.service.impl;

import lombok.Data;
import org.etocrm.dynamicDataSource.util.BasePojo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author chengrong.yang
 * @date 2020/8/18 14:27
 */
@Data
public class SysMenu implements Serializable,Comparable<SysMenu> {

    private static final long serialVersionUID = 7904434256378383978L;
    /**
     * 主键
     */
    private Integer id;

    /**
     * 菜单名称
     */
    private String menuName;

    /**
     * 是否子菜单
     */
    private String menuLev;

    /**
     * 上级菜单id
     */
    private Integer menuParentId;

    /**
     * 菜单路由
     */
    private String menuRoute;

    /**
     * 启用状态
     */
    private Integer menuStatus;

    /**
     * 菜单排序
     */
    private Integer menuOrder;

    /**
     * 图标
     */
    private String icon;

    /**
     * 是否已经勾选，0为未勾选，1为已勾选
     */
    private Integer isSelect;
  /**
     * 子菜单节点
     */
    private List<SysMenu> children = new ArrayList<SysMenu>();

    /**
     * 接收一个权限树访问者
     *
     * @param operation
     * @param <T>
     * @return
     * @throws Exception
     */
    public <T> T execute(MenuOperation<T> operation) throws Exception {
        return operation.doExecute(this);
    }

    @Override
    public int compareTo(SysMenu o) {
        return o.getMenuOrder().compareTo(this.menuOrder);
    }
}
