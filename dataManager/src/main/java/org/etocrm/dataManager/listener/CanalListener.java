package org.etocrm.dataManager.listener;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.etocrm.dataManager.api.IAuthenticationService;
import org.etocrm.dataManager.model.DO.SysSynchronizationConfigDO;
import org.etocrm.dataManager.model.VO.CanalDB;
import org.etocrm.dataManager.service.ISysSynchronizationConfigService;
import org.etocrm.dataManager.util.ColumnData;
import org.etocrm.dynamicDataSource.service.IDynamicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * @Author: dkx
 * @Date: 15:22 2020/11/16
 * @Desc:
 */
@Component
@Slf4j
public class CanalListener {

    @Autowired
    private ISysSynchronizationConfigService sysSynchronizationConfigService;

    @Autowired
    IAuthenticationService iAuthenticationService;

    @Autowired
    IDynamicService dynamicService;

    @Autowired
    ETLTableListener etlTableListener;

    //这边canal复用了 etl 组
    @KafkaListener(topics = "${CUSTOM.KAFKA.TOPIC.ETL_CANAL_INSTALL}", groupId = "${CUSTOM.KAFKA.GROUP.ETL_GROUP}")
    public void install(String obj, Acknowledgment ack) {
        log.error("kafka canal rec install--->>>>>>" + obj);
        CanalDB canalDB = JSONObject.parseObject(obj, CanalDB.class);
        SysSynchronizationConfigDO configDO = sysSynchronizationConfigService.synchronizationConfig(canalDB);
        canalDB.setSysSynchronizationConfig(configDO);
        if (configDO != null) {
            //转换字段
            List<ColumnData> columnData = JSON.parseArray(configDO.getColumnData(), ColumnData.class);
            TreeMap<String, Object> transformation = transformation(canalDB, columnData);
            //执行操作
            transformation.put("is_delete", 0);
            transformation.put("created_time", new Date());
            transformation.put("brands_id", configDO.getBrandId());
            transformation.put("org_id", configDO.getOrgId());
            /**
             * 需要过滤一下，如果已经存在的id相同的数据，需要跳过，防止与同步规则冲突
             * 防止两条一样的数*/
            String whereClause;
            String key = transformationWhereKey(columnData, configDO.getOriginTablePrimaryKey());
            Object keyValue = transformation.get(key);
            if (configDO.getAppId() != null) {
                transformation.put("wechat_appid", configDO.getAppId());
                whereClause = " brands_id = " + configDO.getBrandId() + " and org_id = "
                        + configDO.getOrgId() + " and wechat_appid = '" + configDO.getAppId() + "'  and " + key + " = " + keyValue;
            } else {
                whereClause = " brands_id = " + configDO.getBrandId() + " and org_id = "
                        + configDO.getOrgId() + " and " + key + " = " + keyValue;
            }
            List<String> list = new ArrayList<>();
            list.add(configDO.getDestinationTableName());
            int count = dynamicService.count(list, whereClause);
            log.info("新增前查询是否存在数据：{},条数：{}",whereClause, count);
            if (count <= 0) {
                dynamicService.insertRecord(configDO.getDestinationTableName(), transformation, "1=1");
            }
        }
        ack.acknowledge();
    }

    @KafkaListener(topics = "${CUSTOM.KAFKA.TOPIC.ETL_CANAL_UPDATE}", groupId = "${CUSTOM.KAFKA.GROUP.ETL_GROUP}")
    public void update(String obj, Acknowledgment ack) {
        log.error("kafka canal rec update--->>>>>>" + obj);
        CanalDB canalDB = JSONObject.parseObject(obj, CanalDB.class);
        SysSynchronizationConfigDO config = sysSynchronizationConfigService.synchronizationConfig(canalDB);
        canalDB.setSysSynchronizationConfig(config);
        if (config != null) {
            //转换字段
            List<ColumnData> columnData = JSON.parseArray(config.getColumnData(), ColumnData.class);
            String whereClause = this.transformationWhere(columnData, canalDB);
            TreeMap<String, Object> transformation = transformation(canalDB, columnData);
            //执行操
            transformation.put("updated_time", new Date());
            dynamicService.updateRecord(config.getDestinationTableName(), transformation, whereClause);
        }
        ack.acknowledge();
    }

    @KafkaListener(topics = "${CUSTOM.KAFKA.TOPIC.ETL_CANAL_DELETE}", groupId = "${CUSTOM.KAFKA.GROUP.ETL_GROUP}")
    public void delete(String obj, Acknowledgment ack) {
        log.error("kafka canal rec delete--->>>>>>" + obj);
        CanalDB canalDB = JSONObject.parseObject(obj, CanalDB.class);
        SysSynchronizationConfigDO config = sysSynchronizationConfigService.synchronizationConfig(canalDB);
        canalDB.setSysSynchronizationConfig(config);
        if (config != null) {
            List<ColumnData> columnData = JSON.parseArray(config.getColumnData(), ColumnData.class);
            String whereClause = this.transformationWhere(columnData, canalDB);
            dynamicService.deleteRecord(config.getDestinationTableName(), whereClause);
        }
        ack.acknowledge();
    }

    //查询对应字段，并转换 transformation
    private String transformationWhere(List<ColumnData> columnData, CanalDB canalDB) {
        if (StringUtils.isBlank(canalDB.getWhereCase())) {
            return "";
        }
        String[] split = canalDB.getWhereCase().split("\\=");
        if (split.length <= 0) {
            return "";
        }
        SysSynchronizationConfigDO config = canalDB.getSysSynchronizationConfig();
        if (config != null) {
            String column = split[0];
            for (ColumnData columnDatum : columnData) {
                if (columnDatum.getOriginColumnName().equalsIgnoreCase(column)) {
                    if (config.getAppId() != null) {
                        return " brands_id = " + config.getBrandId() + " and org_id = "
                                + config.getOrgId() + " and wechat_appid = '" + config.getAppId() + "' and " + columnDatum.getDestinationColumnName() + " = " + split[1];
                    } else {
                        return " brands_id = " + config.getBrandId() + " and org_id = "
                                + config.getOrgId() + " and " + columnDatum.getDestinationColumnName() + " = " + split[1];
                    }
                }
            }
        }
        return "";
    }

    private String transformationWhereKey(List<ColumnData> columnData, String key) {
        if (key != null) {
            for (ColumnData columnDatum : columnData) {
                if (columnDatum.getOriginColumnName().equalsIgnoreCase(key)) {
                    return columnDatum.getDestinationColumnName();
                }
            }
        }
        return "";
    }

    //查询对应字段，并转换 transformation
    private TreeMap<String, Object> transformation(CanalDB canalDB, List<ColumnData> columnData) {
        TreeMap<String, Object> treeMap = new TreeMap<>();
        TreeMap<String, Object> orgColumnAndData = canalDB.getColumnAndData();
        for (ColumnData columnDatum : columnData) {
            for (Map.Entry<String, Object> stringObjectEntry : orgColumnAndData.entrySet()) {
                if (columnDatum.getOriginColumnName().equalsIgnoreCase(stringObjectEntry.getKey())) {
                    treeMap.put(columnDatum.getDestinationColumnName(), stringObjectEntry.getValue());
                    break;
                }
            }
        }
        return treeMap;
    }
}
