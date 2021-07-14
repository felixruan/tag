package org.etocrm.dataManager.listener;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.etocrm.dataManager.model.DO.EtlProcessingRuleDO;
import org.etocrm.dataManager.model.VO.EtlTableVO;
import org.etocrm.dataManager.model.VO.EtlUpdateSqlVO;
import org.etocrm.dataManager.model.VO.SysBrands.SysBrandsListAllResponseVO;
import org.etocrm.dynamicDataSource.service.IDynamicService;
import org.etocrm.kafkaServer.service.IKafkaProducerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author lingshuang.pang
 */
@Slf4j
@Component
public class EtlProcessingTableListener {

    @Autowired
    private IDynamicService dynamicService;

    @Autowired
    private IKafkaProducerService producerService;

    private static final int NO_LIMIT_SIZE = 10;

    @Value("${CUSTOM.KAFKA.TOPIC.ETL_PROCESSING_UPDATE}")
    private String updateTopic;

    @KafkaListener(topics = "${CUSTOM.KAFKA.TOPIC.ETL_PROCESSING_TABLE}", groupId = "${CUSTOM.KAFKA.GROUP.ETL_PROCESSING_GROUP}")
    public void etlProcessingTableListener(String tableStr, Acknowledgment ack) {
        log.info("etlProcessingTableListener info:{} ", tableStr);
        long beginTime = System.currentTimeMillis();
        EtlTableVO tableVO = JSONObject.parseObject(tableStr, EtlTableVO.class);
        this.doTable(tableVO.getTable(), tableVO.getRuleList(), tableVO.getBrandsInfo());
        log.error("========= etlProcessingTableListener data:{},cost:{}", tableStr, System.currentTimeMillis() - beginTime);

        ack.acknowledge();

    }

    private void doTable(String table, List<EtlProcessingRuleDO> ruleList, SysBrandsListAllResponseVO brandsInfo) {
        Set<String> queryColumns = ruleList.stream().map(rule -> rule.getParamColumn()).collect(Collectors.toSet());
        List<String> tableNameList = new ArrayList<>();
        tableNameList.add(table);
        List<String> columns = new ArrayList<>();
        columns.addAll(queryColumns);
        String whereClause = " org_id=#{orgId} and brands_id=#{brandsId}"
                .replace("#{orgId}", brandsInfo.getOrgId() + "")
                .replace("#{brandsId}", brandsInfo.getId() + "");

        int dataSize = dynamicService.count(tableNameList, whereClause);
        log.info("======== table:{} size :{}", table, dataSize);
        if (dataSize < 1) {
            return;
        }
        int querySize = ruleList.get(0).getQuerySize();
        int limit = countStep(dataSize, querySize);

        if (dataSize <= querySize || querySize < NO_LIMIT_SIZE) {
            this.doNoLimitData(tableNameList, columns, whereClause, limit, querySize, ruleList, brandsInfo);
        } else {
            this.doLimitData(tableNameList, columns, whereClause, limit, querySize, ruleList, brandsInfo);
        }
    }

    private void doNoLimitData(List<String> tableNameList, List<String> columns, String whereClause, int limit, int querySize, List<EtlProcessingRuleDO> ruleList, SysBrandsListAllResponseVO brandsInfo) {
        List<TreeMap> paramDataList = dynamicService.selectList(tableNameList, columns, whereClause, "");
        List<List<TreeMap>> paramDataLimit = new ArrayList<>();
        Stream.iterate(0, n -> n + 1).limit(limit).forEach(i -> {
            paramDataLimit.add(paramDataList.stream().skip(i * querySize).limit(querySize).collect(Collectors.toList()));
        });
        EtlUpdateSqlVO sqlVO = new EtlUpdateSqlVO();
        sqlVO.setBrandsInfo(brandsInfo);
        sqlVO.setRuleList(ruleList);
        int index = 0;
        for (List<TreeMap> paramData : paramDataLimit) {
            sqlVO.setParamDataList(paramData);
            producerService.sendMessage(updateTopic, JSON.toJSONString(sqlVO), index);
            index++;
        }
    }

    private void doLimitData(List<String> tableNameList, List<String> columns, String whereClause, int limit, int querySize, List<EtlProcessingRuleDO> ruleList, SysBrandsListAllResponseVO brandsInfo) {
        EtlUpdateSqlVO sqlVO = new EtlUpdateSqlVO();
        sqlVO.setBrandsInfo(brandsInfo);
        sqlVO.setRuleList(ruleList);

        Integer limitStart = 0;
        for (int i = 0; i < limit; i++) {
            log.info("======= writer 共:{},第{}次 limit start:{},brandsInfo:{}", limit, i, limitStart, brandsInfo);
            List<TreeMap> paramDataList = dynamicService.selectList(tableNameList, columns, whereClause, "id", limitStart, querySize);
            sqlVO.setParamDataList(paramDataList);
            producerService.sendMessage(updateTopic, JSON.toJSONString(sqlVO), i);
            log.info("======= send update topic end");
            limitStart += querySize;
        }
    }

    private int countStep(Integer size, Integer batchSize) {
        return (size + batchSize - 1) / batchSize;
    }

}
