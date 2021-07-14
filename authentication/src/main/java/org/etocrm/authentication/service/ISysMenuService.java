package org.etocrm.authentication.service;

import com.alibaba.fastjson.JSONArray;
import org.etocrm.authentication.entity.VO.auth.*;
import org.etocrm.authentication.service.impl.SysMenu;
import org.etocrm.core.util.ResponseVO;

import java.util.List;

/**
 * @Author peter.li
 * @date 2020/8/17 14:20
 */
public interface ISysMenuService {

    ResponseVO saveSysMenu(SysMenuAddVO menuVO);

    ResponseVO getSysMenuById(Integer id);

    ResponseVO removeSysMenu(Integer id);

    ResponseVO updateSysMenu(SysMenuUpdateVO menuVO);

    ResponseVO getSysMenuListByParentId(Integer id);

    ResponseVO getChildSysMenuList(Integer parentId);

    ResponseVO changeStatus(SysMenuChangeStatusVO sysMenuChangeStatusVO);

    JSONArray getAuthorizedTree(Integer userId);

    List<String> getUrlPermission(Integer userId);

    ResponseVO changeMenuParnetId(SysMenuChangePIdVO sysMenuChangePIdVO);

    ResponseVO getMenusByRoleId(Integer roleId);

    Boolean hasParentMenuAuth(Integer userId,String buttonName);

    List<String> getMenusByUserId(Integer userId);

    List<SysMenu> getAllMneuTree();

    /*ResponseVO autoCreateButtonMenu();*/


}
