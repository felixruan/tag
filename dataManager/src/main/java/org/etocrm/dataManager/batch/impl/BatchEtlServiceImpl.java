package org.etocrm.dataManager.batch.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.etocrm.core.enums.BusinessEnum;
import org.etocrm.dataManager.batch.IBatchEtlService;
import org.etocrm.dataManager.mapper.SysSynchronizationConfigMapper;
import org.etocrm.dataManager.model.DO.SysSynchronizationConfigDO;
import org.etocrm.dataManager.model.VO.SynchronizationConfig.SysSynchronizationConfigVO;
import org.etocrm.dataManager.service.ISysSynchronizationConfigService;
import org.etocrm.dynamicDataSource.util.RedisUtil;
import org.etocrm.kafkaServer.service.IKafkaProducerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @Author chengrong.yang
 * @Date 2020/11/4 17:56
 */
@Service
@Slf4j
public class BatchEtlServiceImpl implements IBatchEtlService {

    //定时任务标签解析使用
    public static final String REDIS_ETL_DATA_SOURCE_IDS = "redisEtlDataSourceIds";

//    public static final String KAFKA_ETL_BRANDS = "ETL_BRANDS";

    //    public static final String KAFKA_ETL_BRANDS = "ETL_TEST_BRANDS";
    @Value("${CUSTOM.KAFKA.TOPIC.ETL_BRANDS_TOPIC}")
    private String ETL_TEST_BRANDS;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    IKafkaProducerService producerService;

    @Autowired
    private ISysSynchronizationConfigService sysSynchronizationConfigService;

    @Autowired
    private SysSynchronizationConfigMapper sysSynchronizationConfigMapper;


    @Override
    public void run() {
        log.info("clear redis");
        redisUtil.deleteCache(REDIS_ETL_DATA_SOURCE_IDS);
        log.info("update status");
        this.selectSynchronizationConfigProcessStatus();
        log.info("read all brands");
        this.syncAllBrands(Objects.requireNonNull(this.read()));
    }


    private void syncAllBrands(Set<Object> brands) {
        int index = 0;
        for (Object object : brands) {
            //redisUtil.leftPush(ETL_TEST_BRANDS, object.toString());
            producerService.sendMessage(ETL_TEST_BRANDS, object,index);
            index++;
        }
    }

    private Set<Object> read() {
        if (redisUtil.sGetSetSize(REDIS_ETL_DATA_SOURCE_IDS) == 0L) {
            log.info("如果长度为空 结束循环");
            return Collections.emptySet();
        }
        return redisUtil.sGet(REDIS_ETL_DATA_SOURCE_IDS);
    }

    private void selectSynchronizationConfigProcessStatus() {
        //todo 查所有的同步规则获取origId
        SysSynchronizationConfigDO sysSynchronizationConfigDO = new SysSynchronizationConfigDO();
        SysSynchronizationConfigVO sysSynchronizationConfigVO = new SysSynchronizationConfigVO();
        List<SysSynchronizationConfigDO> sysSynchronizationConfigDOList = sysSynchronizationConfigMapper.selectList(
                new LambdaQueryWrapper<>(sysSynchronizationConfigDO)
                        .eq(SysSynchronizationConfigDO::getDeleted, BusinessEnum.NOTDELETED.getCode())
                        .eq(SysSynchronizationConfigDO::getSyncStatus, BusinessEnum.USING.getCode())
                        .likeRight(SysSynchronizationConfigDO::getUpdatedTime, cn.hutool.core.date.DateUtil.formatDate(cn.hutool.core.date.DateUtil.offsetDay(cn.hutool.core.date.DateUtil.beginOfDay(new Date()), -1))));
        for (SysSynchronizationConfigDO synchronizationConfigDO : sysSynchronizationConfigDOList) {
            redisUtil.sSet(REDIS_ETL_DATA_SOURCE_IDS, synchronizationConfigDO.getOriginDatabaseId().toString());
            //将所有ETL规则置为未执行状态
            sysSynchronizationConfigVO.setProcessStatus(BusinessEnum.RULE_UNEXECUTED.getCode().longValue());
            sysSynchronizationConfigVO.setId(synchronizationConfigDO.getId());
            sysSynchronizationConfigService.etlUpdateById(sysSynchronizationConfigVO);

        }
    }

}
