package org.etocrm.tagManager.listener.tagGroup;

import lombok.extern.slf4j.Slf4j;
import org.etocrm.kafkaServer.service.IKafkaProducerService;
import org.etocrm.tagManager.batch.impl.common.BatchTagGroupCommonService;
import org.etocrm.tagManager.model.DO.SysTagGroupDO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Author: dkx
 * @Date: 9:46 2020/11/11
 * @Desc:
 */
@Component
@Slf4j
public class TagGroupListener {

    @Autowired
    BatchTagGroupCommonService batchTagGroupCommonService;

    @Autowired
    IKafkaProducerService producerService;

    @Value("${CUSTOM.KAFKA.TOPIC.TAG_GROUP_USER_TOPIC}")
    private String tagGroupUserTopic;

    @KafkaListener(topics = "${CUSTOM.KAFKA.TOPIC.TAG_GROUP_TOPIC}", groupId = "${CUSTOM.KAFKA.GROUP.TAG_GROUP_TOPIC_GROUP}")
    public void groupListener(String brandAndOrgIdStr, Acknowledgment ack) {
        log.info("groupListener kafka 收到数据：" + brandAndOrgIdStr);
        if (null != brandAndOrgIdStr) {
            List<SysTagGroupDO> tagGroupList = batchTagGroupCommonService.getTagGroupList(brandAndOrgIdStr);
            this.process(tagGroupList);
        }
        ack.acknowledge();
    }


    public void process(List<SysTagGroupDO> tagGroupList) {
        int count = 0;
        for (SysTagGroupDO groupDO : tagGroupList) {
            //更新成计算中
            batchTagGroupCommonService.updateCalculate(groupDO.getId(), true);
            //sendMessage
            producerService.sendMessage(tagGroupUserTopic, String.valueOf(groupDO.getId()), count);
            count++;
        }
    }
}
