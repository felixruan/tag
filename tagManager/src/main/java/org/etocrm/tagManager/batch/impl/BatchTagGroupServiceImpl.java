package org.etocrm.tagManager.batch.impl;

import lombok.extern.slf4j.Slf4j;
import org.etocrm.dynamicDataSource.util.RedisUtil;
import org.etocrm.kafkaServer.service.IKafkaProducerService;
import org.etocrm.tagManager.batch.IBatchTagGroupService;
import org.etocrm.tagManager.batch.impl.common.BatchCommonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Set;


@Service
@Slf4j
public class BatchTagGroupServiceImpl implements IBatchTagGroupService {

    //定时任务标签群组解析使用
    static final String REDIS_TAG_GROUP_DATASOURCE_IDS = "tagGroupDataBaseIds";

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    BatchCommonService commonService;

    @Autowired
    IKafkaProducerService producerService;

    @Value("${CUSTOM.KAFKA.TOPIC.TAG_GROUP_TOPIC}")
    private String tagGroupTopic;

    @Override
    public void run() {
        log.info("clear redis");
        redisUtil.deleteCache(REDIS_TAG_GROUP_DATASOURCE_IDS);
        log.info("set all brands");
        commonService.setBrandToRedis(REDIS_TAG_GROUP_DATASOURCE_IDS);
        log.info("send kafka");
        this.sendKafka();
    }

    private void sendKafka() {
        if (redisUtil.sGetSetSize(REDIS_TAG_GROUP_DATASOURCE_IDS) != 0) {
            Set<Object> objects = redisUtil.sGet(REDIS_TAG_GROUP_DATASOURCE_IDS);
            int count = 0;
            for (Object object : objects) {
                producerService.sendMessage(tagGroupTopic, object, count);
                count++;
            }
        }
    }
}
