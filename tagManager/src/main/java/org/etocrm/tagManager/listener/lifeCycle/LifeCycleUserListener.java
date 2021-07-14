package org.etocrm.tagManager.listener.lifeCycle;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.etocrm.kafkaServer.service.IKafkaProducerService;
import org.etocrm.tagManager.batch.impl.common.BatchLifeCycleCommonService;
import org.etocrm.tagManager.model.VO.lifeCycleModel.LifeCycleModelUserBatchSaveVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

/**
 * @Author: dkx
 * @Date: 9:46 2020/11/11
 * @Desc:
 */
@Component
@Slf4j
public class LifeCycleUserListener {

    @Autowired
    BatchLifeCycleCommonService batchLifeCycleCommonService;

    @Autowired
    IKafkaProducerService producerService;


    @KafkaListener(topics = "${CUSTOM.KAFKA.TOPIC.LIFE_CYCLE_USER}", groupId = "${CUSTOM.KAFKA.GROUP.LIFE_CYCLE_GROUP}")
    public void lifeCycleUserListener(String str, Acknowledgment ack) {
        log.info("lifeCycleUserListener enter,str:{}", str);
        LifeCycleModelUserBatchSaveVO saveVO = JSON.parseObject(str, LifeCycleModelUserBatchSaveVO.class);

        //删除历史模型阶段规则用户
        batchLifeCycleCommonService.deleteLifeCycleUserByRuleId(saveVO.getModelRuleId());

        // set size
        batchLifeCycleCommonService.setModelRuleCoveredCount(saveVO.getModelRuleId(), saveVO.getUserIdList().size());

        //save获得的用户
        batchLifeCycleCommonService.batchSaveUser(saveVO);

        log.info("lifeCycleUserListener end");
        ack.acknowledge();
    }
}
