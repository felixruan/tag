package org.etocrm.kafkaServer.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.etocrm.dynamicDataSource.util.RedisUtil;
import org.etocrm.kafkaServer.service.IKafkaProducerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

/**
 * @Author chengrong.yang
 * @Date 2020/11/4 14:35
 */
@Service
@Slf4j
public class KafkaProducerServiceImpl implements IKafkaProducerService {

    @Autowired
    private KafkaTemplate kafkaTemplate;

    @Autowired
    private RedisUtil redisUtil;

    private static final String KAFKA_PARTITION_COUNT = "kafkaPartitionCount";

    @Override
    public void sendMessage(String topic, Object obj, int listIndex) {

        try {
            // 目前是null 后期有需要，入参记得改
            String key = null;
            Integer kafkaPartitionCount = Integer.valueOf(redisUtil.getValueByKey(KAFKA_PARTITION_COUNT).toString());
            //获取分片
            int partitionId = listIndex % kafkaPartitionCount;
            ListenableFuture<SendResult<String, String>> future = kafkaTemplate.send(topic, partitionId, key, obj);
            future.addCallback(new ListenableFutureCallback<SendResult<String, String>>() {
                @Override
                public void onSuccess(SendResult<String, String> result) {
                    log.info(topic + " success");
                }

                @Override
                public void onFailure(Throwable ex) {
                    log.error(topic + " failure");
                }
            });
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @Override
    public void sendMessage(String topic, Object obj) {
        try {
            ListenableFuture<SendResult<String, String>> future = kafkaTemplate.send(topic, obj);
            future.addCallback(new ListenableFutureCallback<SendResult<String, String>>() {
                @Override
                public void onSuccess(SendResult<String, String> result) {
                    log.info(topic + " success");
                }

                @Override
                public void onFailure(Throwable ex) {
                    log.error(topic + " failure");
                }
            });
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

}
