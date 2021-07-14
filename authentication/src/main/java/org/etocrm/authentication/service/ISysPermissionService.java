package org.etocrm.authentication.service;

import org.etocrm.authentication.entity.DO.SysPermissionDO;

import java.util.List;

/**
 * @Author peter.li
 * @date 2020/8/17 14:20
 */
public interface ISysPermissionService {

    Boolean saveSysPermission(SysPermissionDO sysPermissionDO);

    Integer getCountByRoleId(Integer roleId);

    List<SysPermissionDO> getSysPermissionsByUserId(Integer sysUserId);

    List<Integer> getUserIdsByRoleId(Integer roleId);

    Boolean removeByRoleId(Integer roleId,Integer userId);

    Boolean removeByUserId(Integer userId);

}
