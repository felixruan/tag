package org.etocrm.databinlog.binlog.service.impl;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.lang.TypeReference;
import cn.hutool.db.Entity;
import cn.hutool.db.handler.EntityListHandler;
import cn.hutool.db.handler.RsHandler;
import cn.hutool.db.handler.StringHandler;
import cn.hutool.db.sql.SqlExecutor;
import com.github.shyiko.mysql.binlog.event.*;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.etocrm.databinlog.autoconfig.service.DataCollectionService;
import org.etocrm.databinlog.binlog.domain.dto.BinlogRowDataDTO;
import org.etocrm.databinlog.binlog.domain.dto.CanalDB;
import org.etocrm.databinlog.binlog.domain.enums.SysDictionaryEnum;
import org.etocrm.databinlog.binlog.service.SysDictionaryService;
import org.etocrm.dynamicDataSource.util.RedisUtil;
import org.etocrm.kafkaServer.service.IKafkaProducerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 数据收集业务实现类
 */
@Service
@Slf4j
public class DataCollectionServiceImpl implements DataCollectionService {


    @Autowired
    IKafkaProducerService producerService;


    @Value("${CUSTOM.KAFKA.TOPIC.ETL_CANAL_INSTALL}")
    String ETL_CANAL_INSTALL;

    @Value("${CUSTOM.KAFKA.TOPIC.ETL_CANAL_DELETE}")
    String ETL_CANAL_DELETE;

    @Value("${CUSTOM.KAFKA.TOPIC.ETL_CANAL_UPDATE}")
    String ETL_CANAL_UPDATE;

    @Value("${mysql-binlog-connect-java.datasource.hostname}")
    private String ip;

    @Value("${mysql-binlog-connect-java.datasource.port}")
    private String port;

    @Value("${mysql-binlog-connect-java.datasource.username}")
    private String username;

    @Value("${mysql-binlog-connect-java.datasource.password}")
    private String password;

    @Autowired
    private RedisUtil redisUtil;

    /**
     * 可以直接转字符串的类型
     */
    private static List<Class<?>> NORMAL_TYPE;

    @Autowired
    private SysDictionaryService sysDictionaryService;

    /**
     * 查询表信息
     */
    private static String SQL_SCHEMA;

    /**
     * 查询表主键
     */
    private static String SQL_PRIMARY_SCHEMA;

    /**
     * 允许收集的数据类型
     */
    private static List<EventType> ALLOW_COLLECTION_TYPES;

    /**
     * 允许收集的数据库
     */
    private static List<String> ALLOW_COLLECTION_SCHEMAS;

    /**
     * 允许收集的数据库表
     */
    private static List<String> ALLOW_COLLECTION_TABLES;

    private CanalDB canalDB = null;

    /**
     * 表名
     */
    private String tableName = null;

    /**
     * 数据库名
     */
    private String dbName = null;

    /**
     * 数据库名
     */
    private Boolean isBinlogChanged = false;


    @PostConstruct
    public void init() throws IOException {
        String schema = redisUtil.get("binLog_etl_schema", String.class);
        String table = redisUtil.get("binLog_etl_table", String.class);
        log.error("加载redis中schema={},table={}", schema, table);
        SQL_SCHEMA = "select table_schema, table_name, column_name, ordinal_position from information_schema.columns where table_schema = '%s' and table_name = '%s'";
        SQL_PRIMARY_SCHEMA = "select column_name, column_key from information_schema.columns where table_schema = '%s' and table_name = '%s' and column_key = 'PRI'";
        ALLOW_COLLECTION_TYPES = Lists.newArrayList(EventType.EXT_UPDATE_ROWS, EventType.EXT_WRITE_ROWS, EventType.EXT_DELETE_ROWS);
        ALLOW_COLLECTION_SCHEMAS = Lists.newArrayList(schema.split(","));
        ALLOW_COLLECTION_TABLES = Lists.newArrayList(table.split(","));
        NORMAL_TYPE = Lists.newArrayList(byte.class, Byte.class,
                short.class, Short.class,
                int.class, Integer.class,
                long.class, Long.class,
                float.class, Float.class,
                double.class, Double.class,
                char.class, Character.class,
                String.class,
                BigDecimal.class);
    }

    @Override
    public void collectionIncrementalData(Event event) {

        // 获取到事件类型
        EventType type = event.getHeader().getEventType();

        // 切换了 binlog 文件
        if (handleBinlogFileChange(type, event)) {
            return;
        }

        // 设置表信息
        optionTableInfo(event, type);

        // 判断是否可以收集
        if (!canCollection(type)) {
            return;
        }

        // 执行收集逻辑
        doCollection(event, type);
    }

    /**
     * 处理 binlog 文件切换事件
     *
     * @param type  事件类型
     * @param event binlog事件
     * @return boolean
     */
    private boolean handleBinlogFileChange(EventType type, Event event) {
        if (EventType.ROTATE.equals(type)) {
            // 更新 binlog 文件相关记录配置
            String originalFile = sysDictionaryService.getValByKey(SysDictionaryEnum.BIN_LOG_FILE_NAME);
            String binlogFilename = ((RotateEventData) event.getData()).getBinlogFilename();
            // 如果文件未变化忽略即可
            if (StringUtils.equals(binlogFilename, originalFile)) {
                return true;
            }

            isBinlogChanged = true;
            sysDictionaryService.updateByKey(SysDictionaryEnum.BIN_LOG_FILE_NAME, binlogFilename);
            return true;
        }
        if (EventType.FORMAT_DESCRIPTION.equals(type) && isBinlogChanged) {
            // 更新 binlog 开始位置记录配置
            long nextPosition = ((EventHeaderV4) event.getHeader()).getNextPosition();
            sysDictionaryService.updateByKey(SysDictionaryEnum.BIN_LOG_NEXT_POSITION, String.valueOf(nextPosition));
            isBinlogChanged = false;
            return true;
        }
        return false;
    }

    /**
     * 执行收集逻辑
     *
     * @param event binlog 事件
     * @param type  事件类型
     * @throws {@link Exception} 收集失败抛出
     */
    private void doCollection(Event event, EventType type) {
        try {
            // 查询表映射信息
            Map<Integer, String> dbPosMap = getDbPosMap(dbName, tableName);
            // 构造 BinlogRowData 对象
            BinlogRowDataDTO rowData = buildRowData(event.getData(), type, dbPosMap);
            rowData.setNextPosition(((EventHeaderV4) event.getHeader()).getNextPosition());
            rowData.setCurPosition(((EventHeaderV4) event.getHeader()).getPosition());

            log.error("binlog收集完成: {}", rowData);
            canalDB = new CanalDB();
            canalDB.setTableName(tableName);
            canalDB.setTableSchema(dbName);
            // 将数据变动同步到备份表
            String topic;
            switch (type) {
                case EXT_WRITE_ROWS:
                    topic = ETL_CANAL_INSTALL;
                    break;
                case EXT_UPDATE_ROWS:
                    topic = ETL_CANAL_UPDATE;
                    break;
                case EXT_DELETE_ROWS:
                    topic = ETL_CANAL_DELETE;
                    break;
                default:
                    throw new Exception("非法的数据行类型: " + type);
            }
            rowData.getSql(canalDB, producerService, topic);
            log.info("更新配置表={},偏移量={}", SysDictionaryEnum.BIN_LOG_NEXT_POSITION,rowData.getNextPosition());
            sysDictionaryService.updateByKey(SysDictionaryEnum.BIN_LOG_NEXT_POSITION, String.valueOf(rowData.getNextPosition()));
        } catch (Exception ex) {
            log.error("收集增量数据发送异常, 异常信息: ", ex);
        } finally {
            // 重置库名和表名
            this.dbName = null;
            this.tableName = null;
            this.canalDB = null;
        }
    }

    /**
     * 将数据变动同步到备份表
     *
     * @param rowData 源数据
     */
//    private void doBackup(BinlogRowDataDTO rowData, CanalDB canalDB, IKafkaProducerService producerService, String topic) {
//        String tableName = rowData.getTableName();
//        log.info("监控到影响table: {}", tableName);
//        rowData.getSql(canalDB, producerService, topic);
//        log.info("更新配置表={}", SysDictionaryEnum.BIN_LOG_NEXT_POSITION);
//        sysDictionaryService.updateByKey(SysDictionaryEnum.BIN_LOG_NEXT_POSITION, String.valueOf(rowData.getNextPosition()));
//    }

    /**
     * 校验是否可以收集
     *
     * @param type 事件类型
     * @return boolean true 可以 false 不可以
     */
    private boolean canCollection(EventType type) {
        // 如果不是更新、插入、删除事件, 直接忽略即可
        if (!ALLOW_COLLECTION_TYPES.contains(type)) {
            return false;
        }

        // 表名和库名是否已经完成填充
        if (StringUtils.isEmpty(dbName) || StringUtils.isEmpty(tableName)) {
            log.error("no meta data event");
            return false;
        }

        // 是否在数据库白名单中
        if (!ALLOW_COLLECTION_SCHEMAS.contains(dbName)) {
            return false;
        }

        // 是否在数据库表白名单中
        return ALLOW_COLLECTION_TABLES.contains(tableName);
    }

    /**
     * 构建行数据
     *
     * @param data 事件
     * @return {@link BinlogRowDataDTO} 行数据
     */
    private BinlogRowDataDTO buildRowData(EventData data, EventType eventType, Map<Integer, String> dbPosMap) throws Exception {

        BinlogRowDataDTO binlogRowDataDTO = new BinlogRowDataDTO();
        List<String> primaryKeys = getPrimaryKeys(dbName, tableName);
        List<Map<String, String>> after = Lists.newArrayList();
        List<Map<String, String>> before = Lists.newArrayList();

        switch (eventType) {
            case EXT_WRITE_ROWS:
                processWriteRows((WriteRowsEventData) data, dbPosMap, after);
                break;
            case EXT_UPDATE_ROWS:
                processUpdateRows((UpdateRowsEventData) data, dbPosMap, after, before);
                break;
            case EXT_DELETE_ROWS:
                processDeleteRows((DeleteRowsEventData) data, dbPosMap, after);
                break;
            default:
                throw new Exception("非法的数据行类型: " + eventType.name());
        }

        binlogRowDataDTO.setPrimaryKeys(primaryKeys)
                .setSchemaName(dbName)
                .setTableName(tableName)
                .setAfter(after)
                .setBefore(before)
                .setEventType(eventType);

        return binlogRowDataDTO;
    }

    /**
     * 处理删除行操作
     *
     * @param data     binlog 源数据
     * @param dbPosMap 表映射
     * @param after    变更后的数据
     */
    private void processDeleteRows(DeleteRowsEventData data, Map<Integer, String> dbPosMap, List<Map<String, String>> after) {
        BitSet columns = data.getIncludedColumns();
        List<Serializable[]> rows = data.getRows();
        addRowData(dbPosMap, after, columns, rows);
    }

    /**
     * 处理插入数据
     *
     * @param data     binlog 数据
     * @param dbPosMap 数据库映射
     * @param after    变更后的数据
     */
    private void processWriteRows(WriteRowsEventData data, Map<Integer, String> dbPosMap, List<Map<String, String>> after) {
        BitSet columns = data.getIncludedColumns();
        List<Serializable[]> rows = data.getRows();
        addRowData(dbPosMap, after, columns, rows);
    }

    /**
     * 添加行数据
     *
     * @param dbPosMap 表映射
     * @param rowList  行数据列表
     * @param columns  行信息
     * @param rows     需要转换的binlog源数据
     */
    private void addRowData(Map<Integer, String> dbPosMap, List<Map<String, String>> rowList, BitSet columns, List<Serializable[]> rows) {
        for (Serializable[] row : rows) {
            Map<String, String> afterRow = Maps.newHashMap();
            for (int i = 0; i < row.length; i++) {
                Object item = row[i];

                // todo：这里需要做数据类型转换
                afterRow.put(dbPosMap.get(columns.nextSetBit(i)), convert2SqlStr(item));
            }
            rowList.add(afterRow);
        }
    }

    /**
     * 转换Sql字符串
     *
     * @param item 参数
     * @return {@link String}
     */
    private String convert2SqlStr(Object item) {
        if (item == null) {
            return null;
        }
        if (NORMAL_TYPE.contains(item.getClass())) {
            return String.valueOf(item);
        }
        if (item instanceof Boolean) {
            return (boolean) item ? String.valueOf(1) : String.valueOf(0);
        }
        if (item instanceof Date) {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(item);
        }
        return null;
    }

    /**
     * 处理更新行
     *
     * @param data     binlog 数据
     * @param dbPosMap 数据库字段映射
     * @param after    变更前
     * @param before   变更后
     */
    private void processUpdateRows(UpdateRowsEventData data, Map<Integer, String> dbPosMap, List<Map<String, String>> after, List<Map<String, String>> before) {
        BitSet columns = data.getIncludedColumns();
        List<Map.Entry<Serializable[], Serializable[]>> rows = data.getRows();
        for (Map.Entry<Serializable[], Serializable[]> entry : rows) {

            // 添加变动前的数据列表
            addRowData(dbPosMap, before, columns, Collections.singletonList(entry.getKey()));

            // 添加变动后的数据列表
            addRowData(dbPosMap, after, columns, Collections.singletonList(entry.getValue()));
        }
    }

    /**
     * 设置表信息
     *
     * @param event 事件
     * @param type  类型
     */
    private void optionTableInfo(Event event, EventType type) {
        // 如果是 TABLE_MAP 事件，可以从中获取到操作的库名和表名
        if (type == EventType.TABLE_MAP) {
            TableMapEventData data = event.getData();
            tableName = data.getTable();
            dbName = data.getDatabase();
        }
    }

    @Override
    public Map<Integer, String> getDbPosMap(String schema, String tableName) {

        Map<Integer, String> posMap = Maps.newHashMap();
        String sql = String.format(SQL_SCHEMA, new String[]{schema, tableName});
        List<Entity> execute = execute(sql, new EntityListHandler(), schema);
        List<LinkedHashMap<String, Object>> convert = Convert.convert(new TypeReference<List<LinkedHashMap<String, Object>>>() {
        }, execute);
        for (LinkedHashMap<String, Object> stringObjectLinkedHashMap : convert) {
            int pos = Integer.parseInt(stringObjectLinkedHashMap.get("ORDINAL_POSITION").toString());
            posMap.put(pos - 1, stringObjectLinkedHashMap.get("COLUMN_NAME").toString());
        }
        return posMap;
    }

    @Override
    public List<String> getPrimaryKeys(String schema, String tableName) {

        List<String> primaryKeys = Lists.newArrayList();
        String sql = String.format(SQL_PRIMARY_SCHEMA, new String[]{schema, tableName});
        String execute = execute(sql, new StringHandler(), schema);
        primaryKeys.add(execute);
        return primaryKeys;
    }

    public <T> T execute(String sql, RsHandler<T> rsh, String schema) {
        String driverClassName = "com.mysql.cj.jdbc.Driver";
        String url = "jdbc:mysql://" + ip + ":" + port + "/" + schema + "?serverTimezone=Asia/Shanghai&useUnicode=true&characterEncoding=utf-8&zeroDateTimeBehavior=convertToNull&useSSL=false";
        try (Connection con = DriverManager.getConnection(url, username, password)) {
            assert con != null;
            Class.forName(driverClassName);
            return SqlExecutor.query(con, sql, rsh);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }
}
