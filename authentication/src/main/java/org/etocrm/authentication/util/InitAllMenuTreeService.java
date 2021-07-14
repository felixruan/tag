package org.etocrm.authentication.util;

import lombok.extern.slf4j.Slf4j;
import org.etocrm.authentication.service.ISysMenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * 系统启动时初始化一个全量菜单树缓存
 * Create By peter.li
 */
@Component
@Order(value=200)
@Slf4j
public class InitAllMenuTreeService implements ApplicationRunner {

    @Autowired
    private ISysMenuService iSysMenuService;

    @Override
    public void run(ApplicationArguments args) throws Exception {

        CacheManager.getInstance().setCacheMap("allMenusTree", iSysMenuService.getAllMneuTree());
        log.info("========================================初始化全量菜单树缓存结束========================================");
    }
}
