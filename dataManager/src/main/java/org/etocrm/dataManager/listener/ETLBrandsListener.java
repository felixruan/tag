package org.etocrm.dataManager.listener;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.etocrm.core.enums.BusinessEnum;
import org.etocrm.dataManager.model.DO.SysSynchronizationConfigDO;
import org.etocrm.dataManager.model.VO.SynchronizationConfig.SysSynchronizationConfigVO;
import org.etocrm.dataManager.service.ISysSynchronizationConfigService;
import org.etocrm.dataManager.util.ColumnData;
import org.etocrm.dataManager.util.TableData;
import org.etocrm.dynamicDataSource.model.DO.SysDataSourceDO;
import org.etocrm.dynamicDataSource.model.DO.SysDbSourceDO;
import org.etocrm.dynamicDataSource.service.IDataSourceService;
import org.etocrm.dynamicDataSource.service.IDbSourceService;
import org.etocrm.dynamicDataSource.util.RedisUtil;
import org.etocrm.kafkaServer.service.IKafkaProducerService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.TopicPartition;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Author chengrong.yang
 * @Date 2020/11/9 10:24
 */
@Component
@Slf4j
public class ETLBrandsListener {


    public static final String REDIS_ETL_DATA_SOURCE_IDS = "redisEtlDataSourceIds";

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    IDbSourceService dbSourceService;

    @Autowired
    private IDataSourceService dataSourceService;

    @Autowired
    private ISysSynchronizationConfigService sysSynchronizationConfigService;

    @Autowired
    private IKafkaProducerService producerService;

    @Value("${CUSTOM.KAFKA.TOPIC.ETL_TABLE_TOPIC}")
    private String ETL_TABLE_TOPIC;

    @Value("${CUSTOM.KAFKA.TOPIC.ETL_BRANDS_TOPIC}")
    private String ETL_TEST_BRANDS;

    @Value("${CUSTOM.KAFKA.TOPIC.ETL_DATA_TOPIC}")
    private String ETL_DATA_TOPIC;


    @KafkaListener(topics = "${CUSTOM.KAFKA.TOPIC.ETL_BRANDS_TOPIC}", groupId = "${CUSTOM.KAFKA.GROUP.ETL_GROUP}",containerFactory = "myKafkaBatchFactory"
            //,topicPartitions = {@TopicPartition(partitions = {"${CUSTOM.KAFKA.PARTITION}"},topic = "${CUSTOM.KAFKA.TOPIC.ETL_BRANDS_TOPIC}")}
    )
    public void brandsListener(String obj, Acknowledgment ack) {
        //log.info(ETL_TEST_BRANDS + "brandsListener : \n" + "data : " + obj);
        if (null != obj) {
            Long start = System.currentTimeMillis();
            String dataBaseId = String.valueOf(obj);
            Long originDatabaseId = Long.parseLong(dataBaseId);
            getTableData(originDatabaseId);
            ack.acknowledge();
            Long end = System.currentTimeMillis();
            log.error("=====etl ETL_BRANDS_TOPIC   所需时间={}s", (end - start) / 1000);
        }

    }

    private void getTableData(Long originDatabaseId) {
        //1.通过源表数据源ID确认数据源存在，并且状态未启用，否则直接结束
        if (checkOriginDatabase(originDatabaseId)) {
            return;
        }
        //2.判断源库是否存在
        String originDBName = getOriginDBName(originDatabaseId);
        if (StringUtils.isEmpty(originDBName)) {
            return;
        }
        log.info("2.通过源表数据源ID与目标表数据源ID获取同步规则");
        List<SysSynchronizationConfigDO> sysSynchronizationConfigDOS = getSynchronizationConfig(originDatabaseId);
        if (CollUtil.isEmpty(sysSynchronizationConfigDOS)) {
            return;
        }
        log.info("3.将规则JSON转换为对象，传入处理节点");
        process(sysSynchronizationConfigDOS, originDBName);
    }

    private boolean checkOriginDatabase(Long originDatabaseId) {
        SysDataSourceDO sysDataSourceDO = new SysDataSourceDO();
        sysDataSourceDO.setId(originDatabaseId);
        List<SysDataSourceDO> dataSource = dataSourceService.getDataSource(sysDataSourceDO);
        return CollUtil.isEmpty(dataSource);
    }

    private String getOriginDBName(Long originDatabaseId) {
        SysDbSourceDO dbSource = dbSourceService.selectSysDbSourceById(originDatabaseId);
        return dbSource.getDbName();
    }

    private List<SysSynchronizationConfigDO> getSynchronizationConfig(Long originDatabaseId) {
        SysSynchronizationConfigVO sysSynchronizationConfigVO = new SysSynchronizationConfigVO();
        if (null != originDatabaseId) {
            sysSynchronizationConfigVO.setOriginDatabaseId(originDatabaseId);
        }
        List<SysSynchronizationConfigDO> response = sysSynchronizationConfigService.getListEtl(sysSynchronizationConfigVO);
        return response;
    }

    private void process(List<SysSynchronizationConfigDO> sysSynchronizationConfigDOS, String originDBName) {
        int index = 0;
        for (SysSynchronizationConfigDO sysSynchronizationConfigDO : sysSynchronizationConfigDOS) {
            if (BusinessEnum.END_EXECUTION.getCode().longValue() != sysSynchronizationConfigDO.getProcessStatus()) {
                redisUtil.sSet(REDIS_ETL_DATA_SOURCE_IDS, sysSynchronizationConfigDO.getOriginDatabaseId());
                TableData tableData = new TableData();
                tableData.setSynchronizationConfigId(sysSynchronizationConfigDO.getId());
                tableData.setOriginDBName(originDBName);
                tableData.setBrandsId(sysSynchronizationConfigDO.getBrandId());
                tableData.setOrgId(sysSynchronizationConfigDO.getOrgId());
                BeanUtils.copyProperties(sysSynchronizationConfigDO, tableData);
                tableData.setColumnData(JSON.parseArray(sysSynchronizationConfigDO.getColumnData(), ColumnData.class));
                //redisUtil.leftPush(ETL_TABLE_TOPIC, JSONObject.toJSONString(tableData));
                producerService.sendMessage(ETL_TABLE_TOPIC, JSONObject.toJSONString(tableData), index);
                index++;
            }
        }
    }

}
