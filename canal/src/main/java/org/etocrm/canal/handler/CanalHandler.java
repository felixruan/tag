package org.etocrm.canal.handler;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.otter.canal.protocol.CanalEntry;
import com.google.protobuf.InvalidProtocolBufferException;
import lombok.extern.slf4j.Slf4j;
import org.etocrm.canal.api.IDataManagerService;
import org.etocrm.canal.model.CanalDB;
import org.etocrm.canal.model.SysSynchronizationConfigDO;
import org.etocrm.core.util.ResponseVO;
import org.etocrm.kafkaServer.service.IKafkaProducerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.TreeMap;

/**
 * @Author: dkx
 * @Date: 18:08 2020/11/13
 * @Desc:
 */
@Component
@Slf4j
public class CanalHandler {

    @Autowired
    IKafkaProducerService producerService;

    @Value("${CUSTOM.KAFKA.TOPIC.ETL_CANAL_INSTALL}")
    String ETL_CANAL_INSTALL;

    @Value("${CUSTOM.KAFKA.TOPIC.ETL_CANAL_DELETE}")
    String ETL_CANAL_DELETE;

    @Value("${CUSTOM.KAFKA.TOPIC.ETL_CANAL_UPDATE}")
    String ETL_CANAL_UPDATE;

//    @Value("${brandInfo}")
//    String brandInfo;

    @Autowired
    IDataManagerService iDataManagerService;

    /**
     * 分析数据
     *
     * @param entries
     * @throws InvalidProtocolBufferException
     */
    public void analysis(List<CanalEntry.Entry> entries) throws InvalidProtocolBufferException {
        for (CanalEntry.Entry entry : entries) {
            if (entry.getEntryType() == CanalEntry.EntryType.TRANSACTIONBEGIN
                    || entry.getEntryType() == CanalEntry.EntryType.TRANSACTIONEND) {
                //如果是事务跳过
                continue;
            }
            if (CanalEntry.EntryType.ROWDATA == entry.getEntryType()) {
                CanalEntry.RowChange rowChange = CanalEntry.RowChange.parseFrom(entry.getStoreValue());
                CanalEntry.EventType eventType = rowChange.getEventType();
                //封装
                CanalDB canalDB = new CanalDB();
                canalDB.setTableName(entry.getHeader().getTableName());
                canalDB.setTableSchema(entry.getHeader().getSchemaName());
                log.info("查询SchemaName:" + canalDB.getTableSchema() + " 表名：" + canalDB.getTableName());
                //查询是否在模型列表中
                ResponseVO<SysSynchronizationConfigDO> responseVO = iDataManagerService.synchronizationConfig(canalDB);
                if (responseVO.getCode() != 0 || responseVO.getData() == null) {
                    continue;
                }
                canalDB.setSysSynchronizationConfig(responseVO.getData());
                //类型判断
                if (eventType == CanalEntry.EventType.DELETE) {
                    canalDB.setType("delete");
                    saveDeleteSql(entry, canalDB);
                } else if (eventType == CanalEntry.EventType.UPDATE) {
                    canalDB.setType("update");
                    saveUpdateSql(entry, canalDB);
                } else if (eventType == CanalEntry.EventType.INSERT) {
                    canalDB.setType("install");
                    saveInsertSql(entry, canalDB);
                }
            }
        }
    }

    /**
     * 更新语句
     *
     * @param entry
     */
    private void saveUpdateSql(CanalEntry.Entry entry, CanalDB canalDB) {
        try {
            CanalEntry.RowChange rowChange = CanalEntry.RowChange.parseFrom(entry.getStoreValue());
            List<CanalEntry.RowData> dataList = rowChange.getRowDatasList();
            int count = 0;
            for (CanalEntry.RowData rowData : dataList) {
                List<CanalEntry.Column> afterColumnsList = rowData.getAfterColumnsList();
                TreeMap<String, Object> hashMapHashMap = new TreeMap<>();
                for (CanalEntry.Column column : afterColumnsList) {
                    hashMapHashMap.put(column.getName(), column.getValue());
                    canalDB.setColumnAndData(hashMapHashMap);
                    if (column.getIsKey()) {
                        canalDB.setWhereCase(column.getName() + "=" + column.getValue());
                    }
                }
                String canal = JSONObject.toJSONString(canalDB);
                log.info("kafka canal send update---->>>" + canal);
                producerService.sendMessage(ETL_CANAL_UPDATE, canal, count);
                count++;
            }
        } catch (InvalidProtocolBufferException e) {
            log.error(e.getMessage(), e);
        }
    }

    /**
     * 删除语句
     *
     * @param entry
     */
    private void saveDeleteSql(CanalEntry.Entry entry, CanalDB canalDB) {
        try {
            CanalEntry.RowChange rowChange = CanalEntry.RowChange.parseFrom(entry.getStoreValue());
            List<CanalEntry.RowData> rowDataList = rowChange.getRowDatasList();
            int count = 0;
            for (CanalEntry.RowData rowData : rowDataList) {
                List<CanalEntry.Column> columnList = rowData.getBeforeColumnsList();
                for (CanalEntry.Column column : columnList) {
                    if (column.getIsKey()) {
                        canalDB.setWhereCase(column.getName() + "=" + column.getValue());
                        break;
                    }
                }
                String canal = JSONObject.toJSONString(canalDB);
                log.info("kafka canal send delete--->>>>" + canal);
                producerService.sendMessage(ETL_CANAL_DELETE, canal, count);
                count++;
            }
        } catch (InvalidProtocolBufferException e) {
            log.error(e.getMessage(), e);
        }
    }

    /**
     * 插入语句
     *
     * @param entry
     */
    private void saveInsertSql(CanalEntry.Entry entry, CanalDB canalDB) {
        try {
            CanalEntry.RowChange rowChange = CanalEntry.RowChange.parseFrom(entry.getStoreValue());
            List<CanalEntry.RowData> dataList = rowChange.getRowDatasList();
            int count = 0;
            for (CanalEntry.RowData rowData : dataList) {
                List<CanalEntry.Column> columnList = rowData.getAfterColumnsList();
                TreeMap<String, Object> hashMapHashMap = new TreeMap<>();
                for (int i = 0; i < columnList.size(); i++) {
                    hashMapHashMap.put(columnList.get(i).getName(), columnList.get(i).getValue());
                    canalDB.setColumnAndData(hashMapHashMap);
                }
                String canal = JSONObject.toJSONString(canalDB);
                log.info("kafka canal send install----->>" + canal);
                producerService.sendMessage(ETL_CANAL_INSTALL, canal, count);
                count++;
            }
        } catch (InvalidProtocolBufferException e) {
            log.error(e.getMessage(), e);
        }
    }
}
