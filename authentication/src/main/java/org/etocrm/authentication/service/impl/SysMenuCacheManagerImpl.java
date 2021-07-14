package org.etocrm.authentication.service.impl;

import org.etocrm.dynamicDataSource.util.RedisConfig;
import org.etocrm.dynamicDataSource.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Create By peter.li
 */
@Component
public class SysMenuCacheManagerImpl implements SysMenuCacheManager {

    @Autowired
    private RedisUtil redisUtil;

    /**
     * 获取用户被授权的菜单树
     * @param userId
     * @return
     */
    @Override
    public String getAuthorizedMenuTree(Integer userId) {
        return redisUtil.getRefresh(userId.toString(),String.class);
    }

    /**
     * 缓存用户被授权的菜单树
     * @param userId
     * @param treeJson
     * @throws Exception
     */
    @Override
    public void cacheAuthorizedMenuTree(Integer userId, String treeJson) throws Exception {
        redisUtil.set(userId.toString(),treeJson, RedisConfig.expire);
    }

    /**
     * 删除用户菜单树的缓存
     * @param userId
     */
    @Override
    public void removeAuthorizedMenuTree(Integer userId) {
        if(userId != null){
            redisUtil.deleteCache(userId.toString());
        }
    }

    /**
     * 删除用户按钮权限的缓存
     * @param userId
     */
    public void cacheButtonPerms(Integer userId, List<String> buttonPerms){
        redisUtil.set("button_"+userId,buttonPerms,RedisConfig.expire);
    }

    /**
     * 删除用户按钮权限的缓存
     * @param userId
     */
    public void removeButtonPerms(Integer userId){
        redisUtil.deleteCache("button_"+userId);
    }

    /**
     * 缓存用户非按钮菜单权限ids
     * @param userId
     */
    public void cacheMenuIds(Integer userId,List<String> menuIds){
        redisUtil.set("menu_ids_"+userId,menuIds,RedisConfig.expire);
    }

    /**
     * 删除用户非按钮菜单权限ids
     * @param userId
     */
    public void removeMenuIds(Integer userId){
        redisUtil.deleteCache("menu_ids_"+userId);
    }


    /**
     * 获取用户非按钮菜单权限ids
     * @param userId
     */
    public List<String> getMenuIds(Integer userId){
        return redisUtil.getRefresh("menu_ids_"+ userId,List.class);
    }

}
