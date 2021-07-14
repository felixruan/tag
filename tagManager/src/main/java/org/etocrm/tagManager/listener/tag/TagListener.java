package org.etocrm.tagManager.listener.tag;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.etocrm.core.enums.BusinessEnum;
import org.etocrm.dynamicDataSource.util.RedisUtil;
import org.etocrm.kafkaServer.service.IKafkaProducerService;
import org.etocrm.tagManager.batch.impl.common.BatchCommonService;
import org.etocrm.tagManager.batch.impl.common.BatchTagCommonService;
import org.etocrm.tagManager.constant.TagConstant;
import org.etocrm.tagManager.model.DO.SysTagDO;
import org.etocrm.tagManager.model.DO.SysTagPropertyUserPO;
import org.etocrm.tagManager.model.DO.SysTagPropertyWeChatUserPO;
import org.etocrm.tagManager.model.DO.SysTagUser;
import org.etocrm.tagManager.model.VO.tag.SysTagBrandsInfoVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @Author: dkx
 * @Date: 9:46 2020/11/11
 * @Desc:
 */
@Component
@Slf4j
public class TagListener {

    @Autowired
    BatchTagCommonService batchTagCommonService;

    @Autowired
    BatchCommonService batchCommonService;

    @Autowired
    IKafkaProducerService producerService;

    @Value("${CUSTOM.KAFKA.TOPIC.TAG_USER_TOPIC}")
    String TAG_USER_TOPIC;

    @Resource
    private RedisUtil redisUtil;


    @KafkaListener(topics = "${CUSTOM.KAFKA.TOPIC.TAG_TOPIC}", groupId = "${CUSTOM.KAFKA.GROUP.TAG_TOPIC_GROUP}")
    public void tagListener2(String obj, Acknowledgment ack) throws InterruptedException {
        log.info("kafka 收到数据:" + obj);
        if (null != obj) {
            SysTagBrandsInfoVO brandsInfoVO = new SysTagBrandsInfoVO();
            String[] split = obj.split(",");
            brandsInfoVO.setBrandsId(Long.valueOf(split[0]));
            brandsInfoVO.setOrgId(Long.valueOf(split[1]));
            List<SysTagDO> sysTagList = batchTagCommonService.getSysTagList(brandsInfoVO);
            this.process(sysTagList, brandsInfoVO);
        }
        ack.acknowledge();
    }

    public void process(List<SysTagDO> tagList, SysTagBrandsInfoVO brandsInfoVO) {
        log.info("开始解析process" + tagList.toString());
        SysTagUser sysTagUser;
        int count = 0;
        int coveredPeopleNum = 0;
        for (SysTagDO sysTagDO : tagList) {
            sysTagUser = new SysTagUser();
            sysTagUser.setTagId(sysTagDO.getId());
            if (sysTagDO.getTagType().equals(BusinessEnum.MEMBERS.getCode().toString())) {
                List<SysTagPropertyUserPO> sysTagPropertyUserPOS = batchTagCommonService.userSet(sysTagDO.getId(), brandsInfoVO, sysTagDO.getTagType(), sysTagDO.getAppId());
                if (null != sysTagPropertyUserPOS) {
                    sysTagUser.setTagType(sysTagDO.getTagType());
                    log.info("标签tagId" + sysTagDO.getId() + " 查询到对应人数：" + sysTagPropertyUserPOS.size());
                    batchCommonService.deleteTag(sysTagUser.getTagId(), sysTagUser.getTagType());
                    coveredPeopleNum = sysTagPropertyUserPOS.stream().map(user -> user.getUserId()).collect(Collectors.toSet()).size();
                    log.info("send tag kafka user");
                    int limit = countStep(sysTagPropertyUserPOS.size());
                    List<List<SysTagPropertyUserPO>> mglist = new ArrayList<>();
                    Integer saveMaxNum = Integer.valueOf(redisUtil.getValueByKey(TagConstant.SAVE_MAX_NUMBER).toString());
                    Stream.iterate(0, n -> n + 1).limit(limit).forEach(i -> {
                        mglist.add(sysTagPropertyUserPOS.stream().skip(i * saveMaxNum).limit(saveMaxNum).collect(Collectors.toList()));
                    });
                    log.info("分割成几组数据:" + mglist.size());
                    for (List<SysTagPropertyUserPO> tagPropertyUserPOS : mglist) {
                        sysTagUser.setSysTagPropertyUserDOS(tagPropertyUserPOS);
                        String s = JSONObject.toJSONString(sysTagUser);
                        producerService.sendMessage(TAG_USER_TOPIC, s, count);
                    }
                    log.info("更新频率 & 覆盖人数 & 执行状态:" + coveredPeopleNum);
                    batchTagCommonService.updateTagInfo(sysTagUser.getTagId(), coveredPeopleNum);
                }
            } else {
                List<SysTagPropertyWeChatUserPO> chatUserPOS = batchTagCommonService.weChatUserSet(sysTagDO.getId(), brandsInfoVO, sysTagDO.getTagType(), sysTagDO.getAppId());
                if (null != chatUserPOS) {
                    sysTagUser.setTagType(sysTagDO.getTagType());
                    log.info("粉丝标签 tagId" + sysTagDO.getId() + " 查询到对应人数： " + chatUserPOS.size());
                    batchCommonService.deleteTag(sysTagUser.getTagId(), sysTagUser.getTagType());
                    coveredPeopleNum = chatUserPOS.stream().map(user -> user.getUserId()).collect(Collectors.toSet()).size();
                    log.info("send tag kafka wechat");
                    int limit = countStep(chatUserPOS.size());
                    List<List<SysTagPropertyWeChatUserPO>> mglist = new ArrayList<>();
                    Integer saveMaxNum = Integer.valueOf(redisUtil.getValueByKey(TagConstant.SAVE_MAX_NUMBER).toString());
                    Stream.iterate(0, n -> n + 1).limit(limit).forEach(i -> {
                        mglist.add(chatUserPOS.stream().skip(i * saveMaxNum).limit(saveMaxNum).collect(Collectors.toList()));
                    });
                    log.info("分割成几组数据:" + mglist.size());
                    for (List<SysTagPropertyWeChatUserPO> sysTagPropertyWeChatUserPOS : mglist) {
                        sysTagUser.setSysTagPropertyWeChatUserPOS(sysTagPropertyWeChatUserPOS);
                        String s = JSONObject.toJSONString(sysTagUser);
                        producerService.sendMessage(TAG_USER_TOPIC, s, count);
                    }
                    log.info("更新频率 & 覆盖人数 & 执行状态:" + coveredPeopleNum);
                    batchTagCommonService.updateTagInfo(sysTagUser.getTagId(), coveredPeopleNum);
                }
            }
            count++;
        }
    }

    /**
     * 计算切分次数
     */
    private Integer countStep(Integer size) {
        Integer saveMaxNum = Integer.valueOf(redisUtil.getValueByKey(TagConstant.SAVE_MAX_NUMBER).toString());
        return (size + saveMaxNum - 1) / saveMaxNum;
    }
}
