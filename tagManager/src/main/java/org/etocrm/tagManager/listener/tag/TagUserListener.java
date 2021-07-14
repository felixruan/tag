package org.etocrm.tagManager.listener.tag;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.etocrm.core.enums.BusinessEnum;
import org.etocrm.kafkaServer.service.IKafkaProducerService;
import org.etocrm.tagManager.batch.impl.common.BatchTagCommonService;
import org.etocrm.tagManager.model.DO.SysTagUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

/**
 * @Author: dkx
 * @Date: 9:46 2020/11/11
 * @Desc:
 */
@Component
@Slf4j
public class TagUserListener {

    @Autowired
    BatchTagCommonService batchTagCommonService;

    @Autowired
    IKafkaProducerService producerService;


    @KafkaListener(topics = "${CUSTOM.KAFKA.TOPIC.TAG_USER_TOPIC}", groupId = "${CUSTOM.KAFKA.GROUP.TAG_TOPIC_GROUP}")
    public void tagUserListener(String obj, Acknowledgment ack) {
        SysTagUser sysTagUser = JSONObject.parseObject(obj, SysTagUser.class);
        log.info("kafka delete tagId:{} ,tagType :{}", sysTagUser.getTagId(), sysTagUser.getTagType());
        if (sysTagUser.getTagType().equals(BusinessEnum.MEMBERS.getCode().toString())) {
            if (CollectionUtil.isNotEmpty(sysTagUser.getSysTagPropertyUserDOS())) {
                log.info("=============== begin save tagId:{}", sysTagUser.getTagId());
                batchTagCommonService.asyncInstallTagData(sysTagUser.getTagId(), sysTagUser.getSysTagPropertyUserDOS());
                log.info("=============== end save tagId:{}", sysTagUser.getTagId());
            }
        } else {
            if (CollectionUtil.isNotEmpty(sysTagUser.getSysTagPropertyWeChatUserPOS())) {
                log.info("=============== begin save tagId:{}", sysTagUser.getTagId());
                batchTagCommonService.asyncInstallTagWeChatData(sysTagUser.getTagId(), sysTagUser.getSysTagPropertyWeChatUserPOS());
                log.info("=============== end save tagId:{}", sysTagUser.getTagId());
            }
        }
        ack.acknowledge();
    }
}
