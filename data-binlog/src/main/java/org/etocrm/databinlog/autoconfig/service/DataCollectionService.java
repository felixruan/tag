package org.etocrm.databinlog.autoconfig.service;

import com.github.shyiko.mysql.binlog.event.Event;

import java.util.List;
import java.util.Map;

/**
 * 数据收集业务类
 *
 */
public interface DataCollectionService {

    /**
     * 收集增量数据
     *
     * @param event binlog 事件
     */
    void collectionIncrementalData(Event event);

    /**
     * 获取数据库 数据表列的编码 -> 字段名 的Map
     *
     * @param schema    数据库名
     * @param tableName 表名
     * @return {@link Map< Integer, String>}
     */
    Map<Integer, String> getDbPosMap(String schema, String tableName);

    /**
     * 获取数据库主键
     *
     * @param schema    数据库名
     * @param tableName 表名
     * @return {@link Map< Integer, String>}
     */
    List<String> getPrimaryKeys(String schema, String tableName);
}
