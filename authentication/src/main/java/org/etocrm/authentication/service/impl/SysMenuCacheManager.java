package org.etocrm.authentication.service.impl;


import java.util.List;

/**
 * Create By peter.li
 */
public interface SysMenuCacheManager {

    /**
     * 获取用户被授权的菜单树
     * @param userId
     * @return
     */
    String getAuthorizedMenuTree(Integer userId);

    /**
     * 缓存用户被授权的菜单树
     * @param userId
     * @param treeJson
     * @throws Exception
     */
    void cacheAuthorizedMenuTree(Integer userId,String treeJson) throws Exception;

    /**
     * 删除用户菜单树的缓存
     * @param userId
     */
    void removeAuthorizedMenuTree(Integer userId);

    /**
     * 删除用户按钮权限的缓存
     * @param userId
     */
    void removeButtonPerms(Integer userId);

    /**
     * 缓存用户按钮权限
     * @param userId
     */
    void cacheButtonPerms(Integer userId,List<String> buttonPerms);

    /**
     * 缓存用户非按钮菜单权限ids
     * @param userId
     */
    void cacheMenuIds(Integer userId,List<String> menuIds);

    /**
     * 删除用户非按钮菜单权限ids
     * @param userId
     */
    void removeMenuIds(Integer userId);

    /**
     * 获取用户非按钮菜单权限ids
     * @param userId
     */
    List<String> getMenuIds(Integer userId);




}
