package org.etocrm.databinlog.autoconfig.service;

/**
 * binlog 信息业务类
 *
 */
public interface BinlogInfoService {

    /**
     * 获取 binlog 文件名
     *
     * @param param 参数
     * @return {@link String} 文件名
     */
    String getBinlogFileName(Object... param);

    /**
     * 获取 binlog 下一个读取位置
     *
     * @param param 参数
     * @return {@link String} 文件名
     */
    Long getBinlogNextPosition(Object... param);
}
