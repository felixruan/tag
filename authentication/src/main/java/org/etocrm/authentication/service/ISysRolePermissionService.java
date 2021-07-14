package org.etocrm.authentication.service;

import org.etocrm.authentication.entity.DO.SysRolePermissionDO;

import java.util.List;

/**
 * @Author peter.li
 * @date 2020/8/17 14:20
 */
public interface ISysRolePermissionService {

    void save(SysRolePermissionDO sysRolePermissionDO);

    Integer getCountByMenuId(Integer menuId);

    List<SysRolePermissionDO> getListByRoleId(Integer roleId);

    Boolean removeByRoleId(Integer roleId);

    Boolean removeByList(List<Integer> menuIds,Integer roleId);

}
