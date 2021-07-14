package org.etocrm.tagManager.listener.tagGroup;

import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.etocrm.tagManager.batch.impl.common.BatchTagGroupCommonService;
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
public class TagGroupUserListener {

    @Autowired
    BatchTagGroupCommonService batchTagGroupCommonService;


    @KafkaListener(topics = "${CUSTOM.KAFKA.TOPIC.TAG_GROUP_USER_TOPIC}", groupId = "${CUSTOM.KAFKA.GROUP.TAG_GROUP_TOPIC_GROUP}")
    public void groupUserListener(String groupIdStr, Acknowledgment ack) {
        log.info("groupUserListener kafka 收到数据：" + groupIdStr);
        if (StrUtil.isNotBlank(groupIdStr)) {
            batchTagGroupCommonService.getUserAndSave(Long.parseLong(groupIdStr));
        }
        ack.acknowledge();
    }
}
