package org.etocrm.tagManager.batch.impl;

import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.etocrm.dynamicDataSource.util.RedisUtil;
import org.etocrm.kafkaServer.service.IKafkaProducerService;
import org.etocrm.tagManager.batch.IBatchTagService;
import org.etocrm.tagManager.batch.impl.common.BatchCommonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Set;


@Service
@Slf4j
public class BatchTagServiceImpl implements IBatchTagService {

    //定时任务标签解析使用
    static final String REDIS_TAG_BRANDS_INFO = "redisTagBrandsInfo";

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    BatchCommonService commonService;

    @Autowired
    IKafkaProducerService producerService;

    @Value("${CUSTOM.KAFKA.TOPIC.TAG_TOPIC}")
    String TAG_TOPIC;

    @Override
    public void run() {
        log.info("clear redis");
        redisUtil.deleteCache(REDIS_TAG_BRANDS_INFO);
        log.info("set all brands");
        commonService.setBrandToRedis(REDIS_TAG_BRANDS_INFO);
        log.info("send kafka");
        this.sendKafka();
    }

    private void sendKafka() {
        if (redisUtil.sGetSetSize(REDIS_TAG_BRANDS_INFO) != 0) {
            Set<Object> objects = redisUtil.sGet(REDIS_TAG_BRANDS_INFO);
            int count = 0;
            for (Object object : objects) {
                producerService.sendMessage(TAG_TOPIC, JSONUtil.toJsonStr(object), count);
                count++;
            }
        }
    }

}
