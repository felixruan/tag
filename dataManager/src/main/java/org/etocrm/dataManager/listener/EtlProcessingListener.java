package org.etocrm.dataManager.listener;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.etocrm.core.enums.BusinessEnum;
import org.etocrm.dataManager.mapper.SysEtlProcessingRuleMapper;
import org.etocrm.dataManager.model.DO.EtlProcessingRuleDO;
import org.etocrm.dataManager.model.VO.EtlTableVO;
import org.etocrm.dataManager.model.VO.SysBrands.SysBrandsListAllResponseVO;
import org.etocrm.kafkaServer.service.IKafkaProducerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.TopicPartition;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author lingshuang.pang
 */
@Slf4j
@Component
public class EtlProcessingListener {

    @Autowired
    private SysEtlProcessingRuleMapper etlProcessingRuleMapper;

    @Autowired
    private IKafkaProducerService producerService;

    @Value("${CUSTOM.KAFKA.TOPIC.ETL_PROCESSING_TABLE}")
    private String tableTopic;

    @KafkaListener(topics = "${CUSTOM.KAFKA.TOPIC.ETL_PROCESSING}", groupId = "${CUSTOM.KAFKA.GROUP.ETL_PROCESSING_GROUP}")
    public void etlProcessingListener(String brandsStr, Acknowledgment ack) {
        log.info("etlProcessingListener info:{} ", brandsStr);
        long beginTime = System.currentTimeMillis();
        SysBrandsListAllResponseVO brandsInfo = JSONObject.parseObject(brandsStr, SysBrandsListAllResponseVO.class);
        this.process(brandsInfo);

        log.error("========= etlProcessingListener brandsStr:{},cost:{}",brandsStr,System.currentTimeMillis()-beginTime);

        ack.acknowledge();
    }

    public void process(SysBrandsListAllResponseVO brandsInfo) {
        //查询etl 加工规则
        List<EtlProcessingRuleDO> ruleList = etlProcessingRuleMapper.selectList(new LambdaQueryWrapper<EtlProcessingRuleDO>()
                .eq(EtlProcessingRuleDO::getStatus, BusinessEnum.USING.getCode()));
        //根据遍历表分组
        Map<String, List<EtlProcessingRuleDO>> tableList = ruleList.stream().collect(Collectors.groupingBy(EtlProcessingRuleDO::getParamTable));
        Set<String> tableSet = tableList.keySet();
        int index = 0;
        for (String table : tableSet) {
            EtlTableVO tableVO = new EtlTableVO();
            tableVO.setTable(table);
            tableVO.setRuleList(tableList.get(table));
            tableVO.setBrandsInfo(brandsInfo);
            producerService.sendMessage(tableTopic, JSON.toJSONString(tableVO),index);
            index++;
        }
    }

}
