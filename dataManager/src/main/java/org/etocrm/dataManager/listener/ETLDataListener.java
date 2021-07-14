package org.etocrm.dataManager.listener;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.etocrm.core.enums.BusinessEnum;
import org.etocrm.dataManager.model.VO.DBProcessorVO;
import org.etocrm.dataManager.model.VO.SynchronizationConfig.SysSynchronizationConfigVO;
import org.etocrm.dataManager.service.DBWriterService;
import org.etocrm.dataManager.service.IDBProcessorService;
import org.etocrm.dataManager.service.ISysSynchronizationConfigService;
import org.etocrm.dataManager.util.TableData;
import org.etocrm.dynamicDataSource.service.IDynamicService;
import org.etocrm.dynamicDataSource.service.ISourceDBService;
import org.etocrm.dynamicDataSource.util.RedisUtil;
import org.etocrm.kafkaServer.service.IKafkaProducerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author chengrong.yang
 * @Date 2020/11/9 15:14
 */
@Component
@Slf4j
public class ETLDataListener {

    private static final String MAX_NUMBER = "query_max_number";

    @Autowired
    DBWriterService dbWriterService;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    IDynamicService dynamicService;

    @Autowired
    IDBProcessorService dbProcessorService;

    @Autowired
    ISourceDBService iSourceDBService;

    @Autowired
    private IKafkaProducerService producerService;

    @Autowired
    private ISysSynchronizationConfigService sysSynchronizationConfigService;

    @Value("${CUSTOM.KAFKA.TOPIC.ETL_DATA_TOPIC}")
    private String ETL_DATA_TOPIC;

    @Value("${CUSTOM.KAFKA.TOPIC.ETL_DATA_BATCH_TOPIC}")
    private String ETL_DATA_BATCH_TOPIC;

    @KafkaListener(topics = "${CUSTOM.KAFKA.TOPIC.ETL_DATA_TOPIC}", groupId = "${CUSTOM.KAFKA.GROUP.ETL_GROUP}")
//            topicPartitions = {@TopicPartition(partitions = {"${CUSTOM.KAFKA.PARTITION}"},topic = "${CUSTOM.KAFKA.TOPIC.ETL_DATA_TOPIC}")})
    public void tableListener(String tableDataStr,Acknowledgment ack) {
        //log.info(ETL_DATA_TOPIC + " : \n" + "data : " + tableDataStr);
        long beginTime = System.currentTimeMillis();
        TableData tableData = JSONObject.parseObject(tableDataStr, TableData.class);
        DBProcessorVO dbProcessorVO = tableData.getDbProcessorVO();
        log.info("writer 拿到的查询语句是：{}", dbProcessorVO.toString());

        log.info("writer插入数据之前先清除数据表:{}", tableData.getDestinationTableName());
        long s = System.currentTimeMillis();
        deleteTableData(tableData);
        long d = System.currentTimeMillis();
        log.info("deleteTableData 删除上次品牌数据 耗时：{}", (d - s));

        log.info("writer开始保存数据");
        getOriginDataAndSave(dbProcessorVO, tableData);
        //更新状态
        updateSynchronizationById(tableData);

        ack.acknowledge();
        log.error("=====ETL_DATA_TOPIC  所需时间={}s", (System.currentTimeMillis() - beginTime) / 1000);
    }

    private void deleteTableData(TableData destinationTableName) {
        dbWriterService.deleteTableData(destinationTableName);
    }

    private void getOriginDataAndSave(DBProcessorVO dbProcessorVO, TableData tableDatum) {
        long s = System.currentTimeMillis();
        Map<String, Object> map = dbWriterService.getAllIdsByTableName(tableDatum.getOriginDatabaseId(), dbProcessorVO.getTableName(), tableDatum.getOriginTablePrimaryKey());
        Integer maxId = (Integer) map.get("id");
        Integer countByTableName = iSourceDBService.count(tableDatum.getOriginDatabaseId(), dbProcessorVO.getTableName());
        if (countByTableName < Integer.parseInt(redisUtil.getValueByKey(MAX_NUMBER).toString())) {
            List<String> tableName = new ArrayList<>();
            tableName.add(dbProcessorVO.getTableName());
            List<LinkedHashMap<String, Object>> originData = iSourceDBService.selectList(tableDatum.getOriginDatabaseId(), tableName, dbProcessorVO.getColumn(), dbProcessorVO.getWhereCase(), "");
            if (CollUtil.isNotEmpty(originData)) {
                log.info("writer 查到数据量：{}", originData.size());
                tableDatum.setOriginData(originData);
                dbWriterService.dbWriter(tableDatum);
            }
        } else {
            Integer limit;
            if (maxId.equals(countByTableName)) {
                limit = this.countStep(countByTableName);
            } else {
                limit = this.countStep(maxId);
            }
            int limitStart = 0;
//            int limitEnd = 0;
            for (int i = 0; i <= limit; i++) {
//                int start = limitEnd + limitStart;
                int start = limitStart;
                int end = limitStart + Integer.parseInt(redisUtil.getValueByKey(MAX_NUMBER).toString());
                log.info("writer 共 {} 第 {} 次 limit start {}", limit, i, start);
                String key = String.valueOf(map.get("key"));
                if (end > maxId) {
                    end = 0;
                    saveById(dbProcessorVO, tableDatum, start, end, key, i);
                    break;
                }
                limitStart = end;
                saveById(dbProcessorVO, tableDatum, start, end, key, i);
            }
        }
        long l = System.currentTimeMillis();
        log.error("====同步数据时间：{}s", (l - s) / 1000);
    }

    public Integer countStep(Integer size) {
        int quotient = (size + Integer.parseInt(redisUtil.getValueByKey(MAX_NUMBER).toString()) - 1) / Integer.parseInt(redisUtil.getValueByKey(MAX_NUMBER).toString());
        int remain = (size + Integer.parseInt(redisUtil.getValueByKey(MAX_NUMBER).toString()) - 1) % Integer.parseInt(redisUtil.getValueByKey(MAX_NUMBER).toString());
        if (remain > 0) {
            quotient = quotient + 1;
        }
        return quotient;
    }

    private void saveById(DBProcessorVO dbProcessorVO, TableData tableDatum, int limitStart, int end, String columns, int index) {
        BatchVO vo = new BatchVO();
        vo.setColumn(dbProcessorVO.getColumn());
        vo.setColumns(columns);
        vo.setDestinationTableName(tableDatum.getDestinationTableName());
        vo.setLimitStart(limitStart);
        vo.setEnd(end);
        vo.setTableName(dbProcessorVO.getTableName());
        vo.setOriginDatabaseId(tableDatum.getOriginDatabaseId());
        //redisUtil.leftPush(ETL_DATA_BATCH_TOPIC, JSONObject.toJSONString(vo));
        producerService.sendMessage(ETL_DATA_BATCH_TOPIC, JSONObject.toJSONString(vo), index);
    }

    public void updateSynchronizationById(TableData tableData) {
        log.info("writer开始更新规则状态为 执行完毕 1");
        SysSynchronizationConfigVO sysSynchronizationConfigVO = new SysSynchronizationConfigVO();
        sysSynchronizationConfigVO.setOriginDatabaseId(tableData.getOriginDatabaseId());
        sysSynchronizationConfigVO.setDestinationDatabaseId(tableData.getDestinationDatabaseId());
        sysSynchronizationConfigVO.setId(tableData.getSynchronizationConfigId());
        sysSynchronizationConfigVO.setProcessStatus(BusinessEnum.END_EXECUTION.getCode().longValue());
        sysSynchronizationConfigService.etlUpdateById(sysSynchronizationConfigVO);
        log.info("writer开始更新规则状态为 执行完毕 2");
    }
}
