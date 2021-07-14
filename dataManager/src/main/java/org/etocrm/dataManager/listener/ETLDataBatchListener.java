package org.etocrm.dataManager.listener;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.etocrm.dataManager.service.DBWriterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.PartitionOffset;
import org.springframework.kafka.annotation.TopicPartition;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

/**
 * @Author chengrong.yang
 * @Date 2020/11/9 15:14
 */
@Component
@Slf4j
public class ETLDataBatchListener {


    @Autowired
    TableListenerAsync async;

    @Autowired
    DBWriterService dbWriterService;

    @KafkaListener(topics = "${CUSTOM.KAFKA.TOPIC.ETL_DATA_BATCH_TOPIC}", groupId = "${CUSTOM.KAFKA.GROUP.ETL_GROUP}",containerFactory = "myKafkaBatchFactory"
            //,topicPartitions = {@TopicPartition(partitions = {"${CUSTOM.KAFKA.PARTITION}"},topic = "${CUSTOM.KAFKA.TOPIC.ETL_DATA_BATCH_TOPIC}")}
    )
    public void tableListener(String tableDataStr, Acknowledgment ack) {
        if (null != tableDataStr) {
            log.error("进入分批保存"+tableDataStr);
            BatchVO vo = JSONObject.parseObject(tableDataStr, BatchVO.class);
            log.error("ETLDataBatchListener table:{},start:{},end:{}", vo.getTableName(), vo.getLimitStart(), vo.getEnd());
            long s = System.currentTimeMillis();
            async.asyncSave(vo);
            ack.acknowledge();
            log.error("=====ETL_DATA_BATCH_TOPIC 所需时间:{}s", (System.currentTimeMillis() - s) / 1000);
        }

    }
}
