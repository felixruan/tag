package org.etocrm.authentication.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.etocrm.authentication.service.AuthAsyncServiceManager;
import org.etocrm.authentication.service.ISysBrandsService;
import org.etocrm.authentication.service.ISysMenuService;
import org.etocrm.dynamicDataSource.model.VO.UniteUserAuthOutVO;
import org.etocrm.dynamicDataSource.util.RedisConfig;
import org.etocrm.dynamicDataSource.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * Create By peter.li
 */
@Slf4j
@Component
public class AuthAsyncServiceManagerImpl implements AuthAsyncServiceManager {

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private ISysMenuService sysMenuService;


    @Async//@Async("asyncServiceExecutor")
    public void asyncLoadUserAuths(UniteUserAuthOutVO tokenVO){
        try{
            log.info("------------开始异步缓存用户按钮权限-------------");
            redisUtil.set("button_" + tokenVO.getId(),
                    sysMenuService.getUrlPermission(tokenVO.getId()), RedisConfig.expire);//缓存用户按钮权限
            sysMenuService.getMenusByUserId(tokenVO.getId());//缓存用户非按钮菜单权限ids

            log.info("------------异步缓存用户按钮权限结束-------------");
        }catch (Exception e){
            log.error(e.getMessage(), e);
        }
    }
}
