package org.etocrm.dataManager.listener;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.etocrm.core.util.JsonUtil;
import org.etocrm.dataManager.model.VO.DBProcessorColumnVO;
import org.etocrm.dataManager.model.VO.DBProcessorVO;
import org.etocrm.dataManager.util.ColumnData;
import org.etocrm.dataManager.util.TableData;
import org.etocrm.dynamicDataSource.service.ISourceDBService;
import org.etocrm.dynamicDataSource.util.RandomUtil;
import org.etocrm.dynamicDataSource.util.RedisUtil;
import org.etocrm.kafkaServer.service.IKafkaProducerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.TopicPartition;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @Author chengrong.yang
 * @Date 2020/11/9 12:04
 */
@Component
@Slf4j
public class ETLTableListener {


    @Autowired
    private IKafkaProducerService producerService;

    @Autowired
    private ISourceDBService iSourceDBService;

    @Autowired
    private RandomUtil randomUtil;

    @Autowired
    RedisUtil redisUtil;

    @Value("${CUSTOM.KAFKA.TOPIC.ETL_TABLE_TOPIC}")
    private String ETL_TABLE_TOPIC;

    @Value("${CUSTOM.KAFKA.TOPIC.ETL_DATA_TOPIC}")
    private String ETL_DATA_TOPIC;


    private static final String DATETIME = "%Y-%m-%d %H:%i:%s";
    private static final String DATE = "%Y-%m-%d";
    private static final String TIME = "%H:%i:%s";


    @KafkaListener(topics = "${CUSTOM.KAFKA.TOPIC.ETL_TABLE_TOPIC}", groupId = "${CUSTOM.KAFKA.GROUP.ETL_GROUP}",containerFactory = "myKafkaBatchFactory"
            //,topicPartitions = {@TopicPartition(partitions = {"${CUSTOM.KAFKA.PARTITION}"},topic = "${CUSTOM.KAFKA.TOPIC.ETL_TABLE_TOPIC}")}
    )
    public void tableListener(String tableDataStr, Acknowledgment ack) {
        //log.info(ETL_TABLE_TOPIC + "tableListener : \n" + "data : " + tableDataStr);

        if (null != tableDataStr) {
            long beginTime = System.currentTimeMillis();

            TableData tableData = JSONObject.parseObject(tableDataStr, TableData.class);
            DBProcessorVO dbProcessorVO = new DBProcessorVO();
            dbProcessorVO.setTableSchema(tableData.getOriginDBName());
            dbProcessorVO.setTableName(tableData.getOriginTableName());
            dbProcessorVO.setDatabaseId(tableData.getOriginDatabaseId());
            //  1.判断源表是否存在
            List<HashMap> hashMaps = iSourceDBService.verifyTableExists(dbProcessorVO.getDatabaseId(), dbProcessorVO.getTableName());
            if (hashMaps.size() < 1) {
                ack.acknowledge();
                return;
            }
            dbProcessorVO.setColumn(new ArrayList<>());
            List<ColumnData> columnData = tableData.getColumnData();
            for (ColumnData columnDatum : columnData) {
                dbProcessorVO.getColumn().add(columnDatum.getOriginColumnName());
            }
            List<HashMap> hashMap = iSourceDBService.verifyColumnsExists(dbProcessorVO.getDatabaseId(), dbProcessorVO.getTableName());
            List<HashMap<String, DBProcessorColumnVO>> resultList = getHashMaps(dbProcessorVO, hashMap);
            //  2.判断源字段是否存在
            if (CollUtil.isEmpty(resultList)) {
                ack.acknowledge();
                return;
            }
            //  3.获取源表数据
            getSourceData(resultList, tableData, dbProcessorVO);
            ack.acknowledge();
            log.error("=====ETL_TABLE_TOPIC  所需时间=:{}", (System.currentTimeMillis() - beginTime) / 1000);
        }

    }

    private List<HashMap<String, DBProcessorColumnVO>> getHashMaps(DBProcessorVO dbProcessorVO, List<HashMap> hashMap) {
        List<HashMap<String, DBProcessorColumnVO>> resultList = new ArrayList<>();
        List<String> requestColumn = dbProcessorVO.getColumn();
        for (HashMap column : hashMap) {
            if (resultList.size() > hashMap.size()) {
                break;
            }
            String columnName = String.valueOf(column.getOrDefault("columnName", ""));
            if (requestColumn.contains(columnName)) {
                DBProcessorColumnVO dbProcessorColumnVO = JsonUtil.readJson2Bean(JsonUtil.toJson(column), DBProcessorColumnVO.class);
                HashMap columnMap = new HashMap<>();
                columnMap.put(dbProcessorColumnVO.getColumnName(), dbProcessorColumnVO);
                resultList.add(columnMap);
            }
        }
        return resultList;
    }

    private void getSourceData(List<HashMap<String, DBProcessorColumnVO>> originExistsColumnList, TableData tableData, DBProcessorVO dbProcessorVO) {
        dbProcessorVO.setTableSchema(tableData.getOriginDBName());
        dbProcessorVO.setTableName(tableData.getOriginTableName());
        dbProcessorVO.setColumn(this.getOriginDestinationColumnSql(tableData.getBrandsId(), tableData.getOrgId(), tableData.getAppId(), this.getColumnNameListDate(originExistsColumnList), this.getDestinationOriginColumnMap(tableData.getColumnData())));
        tableData.setDbProcessorVO(dbProcessorVO);
        //redisUtil.leftPush(ETL_DATA_TOPIC, JSONObject.toJSONString(tableData));
        producerService.sendMessage(ETL_DATA_TOPIC, JSONObject.toJSONString(tableData), randomUtil.getRandomIndex());
    }

    private HashMap<String, String> getDestinationOriginColumnMap(List<ColumnData> columnDataList) {
        HashMap<String, String> destinationOriginColumnMap = new HashMap<>();
        for (ColumnData columnData : columnDataList) {
            destinationOriginColumnMap.put(columnData.getDestinationColumnName(), columnData.getOriginColumnName());
        }
        return destinationOriginColumnMap;
    }

    private List<String> getColumnNameListDate(List<HashMap<String, DBProcessorColumnVO>> list) {
        List<String> result = new ArrayList<>();
        for (HashMap<String, DBProcessorColumnVO> columnVOHashMap : list) {
            columnVOHashMap.forEach((key, value) -> {
                if (value.getDataType().equalsIgnoreCase("datetime") ||
                        value.getDataType().equalsIgnoreCase("timestamp")) {
                    key = key + DATETIME;
                }
                if (value.getDataType().equalsIgnoreCase("date")) {
                    key = key + DATE;
                }
                if (value.getDataType().equalsIgnoreCase("time")) {
                    key = key + TIME;
                }
                result.add(key);
            });
        }
        return result;
    }

    private List<String> getOriginDestinationColumnSql(Long brandsId, Long orgId, String appId, List<String> originExistsColumnList, HashMap<String, String> destinationOriginColumnMap) {
        List<String> result = new ArrayList<>();
        destinationOriginColumnMap.forEach((destination, origin) -> {
            if (originExistsColumnList.contains(origin + DATETIME)) {
                origin = "date_format(" + origin + ", '" + DATETIME + "')";
                result.add(String.format("%s as %s", origin, destination));
            }

            if (originExistsColumnList.contains(origin + DATE)) {
                origin = "date_format(" + origin + ", '" + DATE + "')";
                result.add(String.format("%s as %s", origin, destination));
            }

            if (originExistsColumnList.contains(origin + TIME)) {
                origin = "date_format(" + origin + ", '" + TIME + "')";
                result.add(String.format("%s as %s", origin, destination));
            }

            if (originExistsColumnList.contains(origin)) {
                result.add(String.format("%s as %s", origin, destination));
            }
        });

        result.add(brandsId + " as brands_id");
        result.add(orgId + " as org_id");
        if (StringUtils.isNotEmpty(appId)) {
            result.add("'" + appId + "' as wechat_appid");
        }
        return result;
    }

}
