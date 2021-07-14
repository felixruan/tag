package org.etocrm.authentication.service.impl;

/**
 * Create By peter.li
 */
public interface MenuOperation<T> {

    /**
     * 执行这个操作
     * @param sysMenu
     * @return
     * @throws Exception
     */
    T doExecute(SysMenu sysMenu) throws Exception;
}
