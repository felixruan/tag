package org.etocrm.tagManager.listener.mat;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.etocrm.core.enums.BusinessEnum;
import org.etocrm.dynamicDataSource.util.RedisUtil;
import org.etocrm.kafkaServer.service.IKafkaProducerService;
import org.etocrm.tagManager.mapper.mat.IMatMetadataModularMapper;
import org.etocrm.tagManager.mapper.mat.IMatMetadataReportingMapper;
import org.etocrm.tagManager.model.DO.mat.MatModularDO;
import org.etocrm.tagManager.model.DO.mat.MatReportingDO;
import org.etocrm.tagManager.model.VO.mat.MatBatchReportingParameter;
import org.etocrm.tagManager.model.VO.mat.MatReportingParameter;
import org.etocrm.tagManager.model.VO.mat.MetadataPropertyParameter;
import org.etocrm.tagManager.service.mat.IMatProvideOfAutomationMarketingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @Author: lht
 * @Date: 9:46 2021/01/07
 * @Desc:
 */
@Component
@Slf4j
public class MatReportDataListener {

    @Autowired
    IKafkaProducerService producerService;

    @Autowired
    private IMatMetadataModularMapper iMatMetadataModularMapper;

    @Autowired
    private IMatMetadataReportingMapper iMatMetadataReportingMapper;

    @Autowired
    private IMatProvideOfAutomationMarketingService iMatProvideOfAutomationMarketingService;

    @Autowired
    private RedisUtil redisUtil;

    @KafkaListener(topics = "${CUSTOM.KAFKA.TOPIC.MAT_REPORT_TOPIC}", groupId = "${CUSTOM.KAFKA.GROUP.MAT_REPORT_TOPIC_GROUP}")
    public void reportDataListener(String obj, Acknowledgment ack) throws InterruptedException {
        log.info("kafka 收到MAT上报数据:【" + obj + "】");
        if (null != obj) {
            MatReportingParameter reportingParameter = JSON.toJavaObject(JSON.parseObject(obj), MatReportingParameter.class);

            String consumeSign = redisUtil.getRefresh(reportingParameter.getConsumeSign(), String.class);
            if(consumeSign == null){
                log.info("consumeSign =【"+reportingParameter.getConsumeSign() + "】消息已被消费，直接返回");
                return;
            }

            log.info("kafka 收到MAT上报数据,开始处理 eventCode =【"+reportingParameter.getEventCode()+"】,consumeSign =【"+reportingParameter.getConsumeSign()+"】");
            matchingEvent(reportingParameter);//根据eventCode匹配流程

            saveReportData(reportingParameter);//上报数据入库

            redisUtil.deleteCache(reportingParameter.getConsumeSign());//处理完毕删除标识
        }
        log.info("kafka 收到MAT上报数据处理完毕");
        ack.acknowledge();
    }


    @KafkaListener(topics = "${CUSTOM.KAFKA.TOPIC.MAT_BATCH_REPORT_TOPIC}", groupId = "${CUSTOM.KAFKA.GROUP.MAT_REPORT_TOPIC_GROUP}")
    public void batchReportDataListener(String obj, Acknowledgment ack) throws InterruptedException {
        if (null != obj) {
            MatBatchReportingParameter batchReportingParameter = JSON.toJavaObject(JSON.parseObject(obj), MatBatchReportingParameter.class);

            String consumeSign = redisUtil.getRefresh(batchReportingParameter.getConsumeSign(), String.class);
            if(consumeSign == null){
                log.info("consumeSign =【"+batchReportingParameter.getConsumeSign() + "】消息已被消费，直接返回");
                return;
            }
            log.info("kafka 收到MAT批量上报数据: workId =【" + batchReportingParameter.getWorkId() + "】," + "handleId =【" + batchReportingParameter.getHandleId()
                    + "】,"+ "userSize =【" + batchReportingParameter.getUserParameters().size() + "】");
            log.info("kafka 收到MAT上报数据,开始处理 workId =【"+batchReportingParameter.getWorkId()+"】,consumeSign =【"+batchReportingParameter.getConsumeSign()+"】");

            matchingUserByRule(batchReportingParameter);//根据流程规则对上报用户进行筛选

            redisUtil.deleteCache(batchReportingParameter.getConsumeSign());//处理完毕删除标识
        }
        log.info("kafka 收到MAT上报数据处理完毕");
        ack.acknowledge();
    }


    //根据流程规则对上报用户进行筛选
    void matchingUserByRule(MatBatchReportingParameter batchReportingParameter){
        try{
            iMatProvideOfAutomationMarketingService.matchingUserByRule(batchReportingParameter);
        }catch (Exception e){
            log.info("根据流程规则对上报用户进行筛选出错，workId=【"+batchReportingParameter.getWorkId()+"】");
            log.error(e.getMessage(), e);
        }
    }

    //根据eventCode匹配流程
    void matchingEvent(MatReportingParameter reportingParameter){
        try {
            iMatProvideOfAutomationMarketingService.matchingEventMarketingProcess(reportingParameter);
        }catch (Exception e){
            log.info("根据eventCode匹配流程出错，eventCode=【"+reportingParameter.getEventCode()+"】");
            log.error(e.getMessage(), e);

        }
    }

    void saveReportData(MatReportingParameter reportingParameter){
        Date date = new Date();
        JSONArray eventProArr = new JSONArray();
        JSONArray userProArr = new JSONArray();
        try {
            for (MetadataPropertyParameter parameter : reportingParameter.getEventProperty()) {
                JSONObject jo = new JSONObject();
                jo.put("propertyCode", parameter.getPropertyCode());
                jo.put("propertyValue", parameter.getPropertyValue());
                eventProArr.add(jo);
            }
            for (MetadataPropertyParameter parameter : reportingParameter.getUserProperty()) {
                JSONObject jo = new JSONObject();
                jo.put("propertyCode", parameter.getPropertyCode());
                jo.put("propertyValue", parameter.getPropertyValue());
                userProArr.add(jo);
            }

            QueryWrapper<MatModularDO> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("mod_code", reportingParameter.getModCode());
            queryWrapper.eq("is_delete", BusinessEnum.NOTDELETED.getCode());
            MatModularDO modularDO = iMatMetadataModularMapper.selectOne(queryWrapper);

            MatReportingDO reportingDO = new MatReportingDO();
            reportingDO.setEventProperty(eventProArr.toJSONString());
            reportingDO.setUserProperty(userProArr.toJSONString());
            reportingDO.setEventCode(reportingParameter.getEventCode());
            reportingDO.setModCode(reportingParameter.getModCode());
            reportingDO.setModId(modularDO.getId());
            reportingDO.setReportingTime(date);
            iMatMetadataReportingMapper.insert(reportingDO);
        }catch (Exception e){
            log.info("上报数据入库出错，eventCode=【"+reportingParameter.getEventCode()+"】");
            log.error(e.getMessage(), e);
        }
    }

}
