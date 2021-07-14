package org.etocrm.databinlog.binlog.domain.dto;

import com.alibaba.fastjson.JSONObject;
import com.github.shyiko.mysql.binlog.event.EventType;
import com.google.common.collect.Lists;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.etocrm.kafkaServer.service.IKafkaProducerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.text.MessageFormat;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Binlog 数据
 */
@Data
@Slf4j
@Accessors(chain = true)
public class BinlogRowDataDTO {


    /**
     * 插入SQL模板
     */
    //private static String INSTAR_SQL_TEMPLATE = "INSERT INTO {0} ({1}) VALUES ({2})";

    /**
     * 编辑SQL模板
     */
    //private static String UPDATE_SQL_TEMPLATE = "UPDATE {0} SET {1} WHERE {2}";

    /**
     * 删除SQL模板
     */
    //private static String DELETE_SQL_TEMPLATE = "DELETE FROM {0} WHERE {1}";

    /**
     * 逗号
     */
    private static String COMMA = ",";

    /**
     * 条件连接符
     */
    private static String AND = "AND";

    /**
     * 引号
     */
    private static String SINGLE_QUOTE_TEMPLATE = "''{0}''";

    /**
     * 反引号
     */
    private static String BACK_QUOTE_TEMPLATE = "{0}";

    /**
     * 相等模板
     */
    private static String EQUAL_TEMPLATE = "{0}={1}";

    /**
     * 数据库名
     */
    private String schemaName;

    /**
     * 表名
     */
    private String tableName;

    /**
     * 当前记录的位置
     */
    private Long curPosition;

    /**
     * 下一个记录的位置
     */
    private Long nextPosition;

    /**
     * 主键
     */
    private List<String> primaryKeys;

    /**
     * 事件类型
     */
    private EventType eventType;

    /**
     * 更新之后的数据，对于删除类型来说，即为空
     */
    private List<Map<String, String>> after;

    /**
     * 更新之前的数据，对于插入类型来说，即为空
     */
    private List<Map<String, String>> before;

    /**
     * 获取SQL
     *
     * @return {@link String}
     * @author nza
     * @createTime 2020/12/23 11:27
     */
    public void getSql(CanalDB canalDB, IKafkaProducerService producerService, String topic) {
        switch (eventType) {
            case EXT_WRITE_ROWS:
                handleWriteRows(canalDB, producerService, topic);
                break;
            case EXT_UPDATE_ROWS:
                handleUpdateRows(canalDB, producerService, topic);
                break;
            case EXT_DELETE_ROWS:
                handleDeleteRows(canalDB, producerService, topic);
                break;
        }
    }

    /**
     * 处理删除行
     *
     * @author nza
     * @createTime 2020/12/23 11:53
     */
    private void handleDeleteRows(CanalDB canalDB, IKafkaProducerService producerService, String topic) {
        int i = 0;
        for (Map<String, String> map : after) {
            List<String> wheres = Lists.newArrayList();
            map.forEach((column, value) -> {
                if (primaryKeys.contains(column)) {
                    wheres.add(MessageFormat.format(EQUAL_TEMPLATE, column, value));
                }
            });

            String where = String.join(AND, wheres);
            canalDB.setWhereCase(where);
            String canal = JSONObject.toJSONString(canalDB);
            log.error("kafka canal send delete---->>>" + canal);
            producerService.sendMessage(topic, canal, i);
            i++;
        }
    }

    /**
     * 处理更新行
     *
     * @author nza
     * @createTime 2020/12/23 11:53
     */
    private void handleUpdateRows(CanalDB canalDB, IKafkaProducerService producerService, String topic) {
        for (int i = 0; i < after.size(); i++) {
            //List<String> updates = Lists.newArrayList();
            List<String> wheres = Lists.newArrayList();
            TreeMap<String, Object> columnAndData = new TreeMap<>();
            for (Map.Entry<String, String> map : after.get(i).entrySet()) {
                String column = map.getKey();
                String val = map.getValue();
                // 判断是否是主键
                if (!primaryKeys.contains(column)) {
                    // 如果值没有变化, 则跳过
                    String beforeVal = this.before.get(i).get(column);
                    if (StringUtils.equals(beforeVal, val)) {
                        continue;
                    }
                    columnAndData.put(column, val);
                    //updates.add(MessageFormat.format(EQUAL_TEMPLATE, column, val));
                } else {
                    columnAndData.put(column, val);
                    wheres.add(MessageFormat.format(EQUAL_TEMPLATE, column, val));
                }
            }
            String where = String.join(AND, wheres);
            canalDB.setWhereCase(where);
            canalDB.setColumnAndData(columnAndData);
            String canal = JSONObject.toJSONString(canalDB);
            log.error("kafka canal send update---->>>" + canal);
            producerService.sendMessage(topic, canal, i);
        }
    }

    /**
     * 处理插入行
     *
     * @author nza
     * @createTime 2020/12/23 11:53
     */
    private void handleWriteRows(CanalDB canalDB, IKafkaProducerService producerService, String topic) {
        int i = 0;
        TreeMap<String, Object> columnAndData = new TreeMap<>();
        for (Map<String, String> map : after) {
            map.forEach((column, value) -> {
                columnAndData.put(column,value);
            });
            canalDB.setColumnAndData(columnAndData);
            String canal = JSONObject.toJSONString(canalDB);
            log.error("kafka canal send install---->>>" + canal);
            producerService.sendMessage(topic, canal, i);
            i++;
        }
    }
}
