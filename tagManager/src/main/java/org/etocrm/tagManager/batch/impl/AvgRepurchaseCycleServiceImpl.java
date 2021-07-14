package org.etocrm.tagManager.batch.impl;

import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.etocrm.dynamicDataSource.util.RedisUtil;
import org.etocrm.kafkaServer.service.IKafkaProducerService;
import org.etocrm.tagManager.batch.IAvgRepurchaseCycleService;
import org.etocrm.tagManager.batch.impl.common.BatchCommonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Set;


@Service
@Slf4j
public class AvgRepurchaseCycleServiceImpl implements IAvgRepurchaseCycleService {

    //定时任务标签解析使用
    static final String AVG_REPURCHASE_CYCLE = "AVG_REPURCHASE_CYCLE";

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private BatchCommonService commonService;

    @Autowired
    IKafkaProducerService producerService;

    @Value("${CUSTOM.KAFKA.TOPIC.AVG_REPURCHASE_CYCLE}")
    String avgRepurchaseCycle;

    @Override
    public void run() {
        log.info("clear redis");
        redisUtil.deleteCache(AVG_REPURCHASE_CYCLE);
        log.info("set all brands");
        commonService.setBrandToRedis(AVG_REPURCHASE_CYCLE);
        log.info("send kafka");
        this.sendKafka();
    }

    private void sendKafka() {
        if (redisUtil.sGetSetSize(AVG_REPURCHASE_CYCLE) != 0) {
            Set<Object> objects = redisUtil.sGet(AVG_REPURCHASE_CYCLE);
            int count = 0;
            for (Object object : objects) {
                producerService.sendMessage(avgRepurchaseCycle, JSONUtil.toJsonStr(object), count);
                count++;
            }
        }
    }

}
