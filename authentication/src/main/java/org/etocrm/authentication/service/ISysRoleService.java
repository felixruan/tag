package org.etocrm.authentication.service;

import org.etocrm.authentication.entity.VO.auth.*;
import org.etocrm.core.util.ResponseVO;

/**
 * @Author peter.li
 * @date 2020/8/17 14:20
 */
public interface ISysRoleService {

    ResponseVO getListByPage(SysRoleInPageVO sysRoleInPageVO);

    ResponseVO findAll();

    ResponseVO getRoleById(Integer id);

    ResponseVO getRoleByUserId(Integer userId);

    ResponseVO saveRole(SysRoleAddVO roleVO);

    ResponseVO removeSysRole(Integer id);

    ResponseVO updateSysRole(SysRoleUpdateVO roleVO);

  //  ResponseVO updateSysRoleAuth(SysRoleUpdateAuthVO sysRoleUpdateAuthVO);

}
