package org.etocrm.dynamicDataSource.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class RandomUtil {

    @Autowired
    private RedisUtil redisUtil;
    private static final String KAFKA_PARTITION_COUNT = "kafkaPartitionCount";

    /**
     * 根据kafka分片数量随机获取发送分区
     * @return
     */
    public  int getRandomIndex() {
        Integer kafkaPartitionCount = Integer.valueOf(redisUtil.getValueByKey(KAFKA_PARTITION_COUNT).toString());
        return new Random().nextInt(kafkaPartitionCount);
    }
}
