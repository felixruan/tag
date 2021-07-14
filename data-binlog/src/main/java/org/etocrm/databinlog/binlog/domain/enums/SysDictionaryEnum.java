package org.etocrm.databinlog.binlog.domain.enums;

/**
 * 系统字典枚举
 *

 */
public enum SysDictionaryEnum {

    /**
     * 枚举
     */
    BIN_LOG_FILE_NAME("BIN_LOG_FILE_NAME", "binlog文件名"),
    BIN_LOG_NEXT_POSITION("BIN_LOG_NEXT_POSITION", "binlog读取位置");

    SysDictionaryEnum(String key, String des) {
        this.key = key;
        this.des = des;
    }

    private final String key;

    private final String des;

    public String getKey() {
        return key;
    }

    public String getDes() {
        return des;
    }
}
