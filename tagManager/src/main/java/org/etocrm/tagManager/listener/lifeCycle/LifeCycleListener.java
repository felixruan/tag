package org.etocrm.tagManager.listener.lifeCycle;

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.etocrm.dynamicDataSource.service.IDynamicService;
import org.etocrm.kafkaServer.service.IKafkaProducerService;
import org.etocrm.tagManager.batch.impl.common.BatchLifeCycleCommonService;
import org.etocrm.tagManager.constant.LifeCycleConstant;
import org.etocrm.tagManager.enums.TagDictEnum;
import org.etocrm.tagManager.model.DO.SysLifeCycleModelDO;
import org.etocrm.tagManager.model.DO.SysLifeCycleModelRuleDO;
import org.etocrm.tagManager.model.VO.lifeCycleModel.LifeCycleModelRuleValueVO;
import org.etocrm.tagManager.model.VO.lifeCycleModel.LifeCycleModelUserBatchSaveVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
@Slf4j
public class LifeCycleListener {

    @Autowired
    BatchLifeCycleCommonService batchLifeCycleCommonService;

    @Autowired
    IKafkaProducerService producerService;

    @Autowired
    IDynamicService dynamicService;

    @Value("${CUSTOM.KAFKA.TOPIC.LIFE_CYCLE_USER}")
    private String LIFE_CYCLE_USER_TOPIC;


    @KafkaListener(topics = "${CUSTOM.KAFKA.TOPIC.LIFE_CYCLE}", groupId = "${CUSTOM.KAFKA.GROUP.LIFE_CYCLE_GROUP}")
    public void lifeCycleListener(String lifeCycleModelStr, Acknowledgment ack) {
        log.info("lifeCycleListener kafka 收到数据：{}", lifeCycleModelStr);
        if (StringUtils.isNotBlank(lifeCycleModelStr)) {
            SysLifeCycleModelDO modelDO = JSON.parseObject(lifeCycleModelStr, SysLifeCycleModelDO.class);
            this.process(modelDO);

            batchLifeCycleCommonService.updateExecutionInfo(modelDO);
        }
        log.info("lifeCycleListener end：");
        ack.acknowledge();
    }

    private void process(SysLifeCycleModelDO modelDO) {
        //获取到规则
        List<SysLifeCycleModelRuleDO> ruleList = batchLifeCycleCommonService.getRuleListByModelId(modelDO.getId());
        int count = 0;
        for (SysLifeCycleModelRuleDO ruleDO : ruleList) {
            LifeCycleModelUserBatchSaveVO saveVO = new LifeCycleModelUserBatchSaveVO();
            saveVO.setModelId(modelDO.getId());
            saveVO.setModelRuleId(ruleDO.getId());
            saveVO.setUserIdList(this.getModelRuleUser(modelDO, ruleDO, ruleList));
            producerService.sendMessage(LIFE_CYCLE_USER_TOPIC, JSON.toJSONString(saveVO), count);
            count++;
        }
    }

    private List<Long> getModelRuleUser(SysLifeCycleModelDO modelDO, SysLifeCycleModelRuleDO ruleDO, List<SysLifeCycleModelRuleDO> ruleList) {
        List<String> tableNames = new ArrayList<String>();
        tableNames.add("members");
        List<String> columns = new ArrayList<>();
        columns.add("id");
        String whereClause = "";

        LifeCycleModelRuleValueVO ruleValueVO = JSON.parseObject(ruleDO.getStepRuleValue(), LifeCycleModelRuleValueVO.class);

        int stepOrder = ruleDO.getStepOrder();
        switch (stepOrder) {
            case 0:
                // 注册180天内 1单
                String registerDay = DateUtil.formatDateTime(DateUtil.offsetDay(DateUtil.beginOfDay(new Date()), -ruleValueVO.getDay()));
                whereClause = LifeCycleConstant.STEP_RULE_WHERE_SQL[0].replace("#{registeredTime}", registerDay);
                break;
            case 1:
                // 180天内1单
                String payTime = DateUtil.formatDateTime(DateUtil.offsetDay(DateUtil.beginOfDay(new Date()), -ruleValueVO.getDay()));
                whereClause = LifeCycleConstant.STEP_RULE_WHERE_SQL[1].replace("#{payTime}", payTime);
                break;
            case 2:
                // 180天内1单，历史订单>1
                payTime = DateUtil.formatDateTime(DateUtil.offsetDay(DateUtil.beginOfDay(new Date()), -ruleValueVO.getDay()));
                whereClause = LifeCycleConstant.STEP_RULE_WHERE_SQL[2].replace("#{payTime}", payTime);
                break;
            case 3:
            case 4:
                //
                String payTime1 = DateUtil.formatDateTime(DateUtil.offsetDay(DateUtil.beginOfDay(new Date()), -ruleValueVO.getDayList().get(0)));
                String payTime2 = DateUtil.formatDateTime(DateUtil.offsetDay(DateUtil.beginOfDay(new Date()), -ruleValueVO.getDayList().get(1)));

                if (TagDictEnum.LIFE_CYCLE_SITUATED_BETWEEN.getCode().equals(ruleValueVO.getLogicDictCode())) {
                    // 介于
                    whereClause = LifeCycleConstant.STEP_RULE_WHERE_SQL[3]
                            .replace("#{payTime1}", payTime1)
                            .replace("#{payTime2}", payTime2)
                            .replace("#{startValue}", String.valueOf(ruleValueVO.getStartValue()))
                            .replace("#{endValue}", String.valueOf(ruleValueVO.getEndValue()))
                    ;
                } else if (TagDictEnum.LIFE_CYCLE_GREATER_THAN.getCode().equals(ruleValueVO.getLogicDictCode())) {
                    // 大于
                    whereClause = LifeCycleConstant.STEP_RULE_WHERE_SQL[4]
                            .replace("#{payTime1}", payTime1)
                            .replace("#{payTime2}", payTime2)
                            .replace("#{startValue}", String.valueOf(ruleValueVO.getStartValue()))
                    ;
                }

                break;
            case 5:
                // 180天内订单数=0，且历史总订单数>0
                payTime = DateUtil.formatDateTime(DateUtil.offsetDay(DateUtil.beginOfDay(new Date()), -ruleValueVO.getDay()));

                LifeCycleModelRuleValueVO loseDayRuleValueVO = JSON.parseObject(ruleList.get(6).getStepRuleValue(), LifeCycleModelRuleValueVO.class);
                String losePayTime = DateUtil.formatDateTime(DateUtil.offsetDay(DateUtil.beginOfDay(new Date()), -loseDayRuleValueVO.getDay()));
                whereClause = LifeCycleConstant.STEP_RULE_WHERE_SQL[5].replace("#{payTime}", payTime).replace("#{lose.payTime}", losePayTime);
                break;
            case 6:
                // 365天内订单数=0
                payTime = DateUtil.formatDateTime(DateUtil.offsetDay(DateUtil.beginOfDay(new Date()), -ruleValueVO.getDay()));

                LifeCycleModelRuleValueVO registerDayRuleValueVO = JSON.parseObject(ruleList.get(0).getStepRuleValue(), LifeCycleModelRuleValueVO.class);
                registerDay = DateUtil.formatDateTime(DateUtil.offsetDay(DateUtil.beginOfDay(new Date()), -registerDayRuleValueVO.getDay()));

                whereClause = LifeCycleConstant.STEP_RULE_WHERE_SQL[6].replace("#{payTime}", payTime).replace("#{registeredTime}", registerDay);
                break;
            default:
                break;
        }
        whereClause = whereClause
                .replace("#{orgId}", String.valueOf(modelDO.getOrgId()))
                .replace("#{brandsId}", String.valueOf(modelDO.getBrandsId()));

        if (StringUtils.isBlank(whereClause)) {
            log.error("getModelRuleUser error, whereClause null,modelDO:{}", modelDO);
            return new ArrayList<>();
        }
        log.info("getModelRuleUser ======== whereClause:{}", whereClause);
        return dynamicService.getIdsList(tableNames, columns, whereClause, null);
    }


}
