package org.etocrm.databinlog.binlog.service;

import org.etocrm.databinlog.binlog.domain.enums.SysDictionaryEnum;

/**
 * 系统字典业务类
 *
 */
public interface SysDictionaryService {

    /**
     * 根据Key更新值
     *
     * @param key 键
     * @param val 值
     */
    void updateByKey(SysDictionaryEnum key, String val);

    /**
     * 根据Key获取值
     *
     * @param key 键
     * @return {@link String} 获取键的值
     */
    String getValByKey(SysDictionaryEnum key);
}
