package org.etocrm.tagManager.batch.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateTime;
import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.etocrm.core.enums.BusinessEnum;
import org.etocrm.kafkaServer.service.IKafkaProducerService;
import org.etocrm.tagManager.batch.IBatchLifeCycleService;
import org.etocrm.tagManager.mapper.ISysLifeCycleModelMapper;
import org.etocrm.tagManager.model.DO.SysLifeCycleModelDO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class BatchLifeCycleServiceImpl implements IBatchLifeCycleService {

    @Autowired
    private ISysLifeCycleModelMapper sysLifeCycleModelMapper;

    @Autowired
    private IKafkaProducerService producerService;

    @Value("${CUSTOM.KAFKA.TOPIC.LIFE_CYCLE}")
    private String LIFE_CYCLE_TOPIC;

    @Override
    public void run() {
        log.info("batch life cycle run begin");
        this.doBatchLifeCycle();
        log.info("batch life cycle run end");
    }

    private void doBatchLifeCycle() {
        List<SysLifeCycleModelDO> sysLifeCycleModelDOS = sysLifeCycleModelMapper.selectList(new LambdaQueryWrapper<SysLifeCycleModelDO>()
                .le(SysLifeCycleModelDO::getDataNextUpdateDate, DateTime.now()));
        if (CollectionUtil.isNotEmpty(sysLifeCycleModelDOS)) {
            int count = 0;
            for (SysLifeCycleModelDO modelDO : sysLifeCycleModelDOS) {
                //设置执行状态:执行中，sendMessage
                modelDO.setDataUpdateStatus(BusinessEnum.IN_EXECUTION.getCode());
                sysLifeCycleModelMapper.updateById(modelDO);
                producerService.sendMessage(LIFE_CYCLE_TOPIC, JSONArray.toJSONString(modelDO), count);
                count++;
            }
        }
    }
}
