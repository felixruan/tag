package org.etocrm.dataManager.batch.impl;


import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.etocrm.core.util.ResponseVO;
import org.etocrm.dataManager.api.IAuthenticationService;
import org.etocrm.dataManager.batch.IEtlProcessingService;
import org.etocrm.dataManager.model.VO.SysBrands.SysBrandsListAllResponseVO;
import org.etocrm.dynamicDataSource.util.RedisUtil;
import org.etocrm.kafkaServer.service.IKafkaProducerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * etl 加工
 *
 * @author lingshuang.pang
 */
@Slf4j
@Service
public class EtlProcessingServiceImpl implements IEtlProcessingService {

    @Autowired
    private IAuthenticationService authenticationService;

    @Autowired
    private IKafkaProducerService producerService;

    @Value("${CUSTOM.KAFKA.TOPIC.ETL_PROCESSING}")
    private String etlProcessing;

    @Autowired
    RedisUtil redisUtil;

    public static final String ETL_PROCESSING_BRANDS_INFO = "etl_process_brands_info";

    @Override
    public void run() {
        List<SysBrandsListAllResponseVO> brandsInfoList = this.getBrandsInfo();
        if (CollectionUtil.isNotEmpty(brandsInfoList)){
            int index = 0;
            for (SysBrandsListAllResponseVO brands : brandsInfoList) {
                producerService.sendMessage(etlProcessing, JSONUtil.toJsonStr(brands), index);
                index++;
            }
        }

    }

    private List<SysBrandsListAllResponseVO> getBrandsInfo(){
        List<SysBrandsListAllResponseVO> sysBrandsListAllResponseVOS = null;
        //先查redis 查到以redis 配置的品牌为准
        Object brandsInfoObj = redisUtil.getValueByKey(ETL_PROCESSING_BRANDS_INFO);
        if (null!=brandsInfoObj){
            sysBrandsListAllResponseVOS  = JSON.parseArray(brandsInfoObj.toString(), SysBrandsListAllResponseVO.class);
        }
        if (null == sysBrandsListAllResponseVOS){
            //查询所有的品牌和机构
            ResponseVO<List<SysBrandsListAllResponseVO>> brandsResponseVO = authenticationService.getListAll();
            if (null != brandsResponseVO && 0 == brandsResponseVO.getCode() && CollectionUtil.isNotEmpty(brandsResponseVO.getData())) {
                sysBrandsListAllResponseVOS = brandsResponseVO.getData();
            }
        }
        return sysBrandsListAllResponseVOS;
    }

}


