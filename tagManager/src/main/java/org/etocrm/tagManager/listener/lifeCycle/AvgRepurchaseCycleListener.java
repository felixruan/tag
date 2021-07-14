package org.etocrm.tagManager.listener.lifeCycle;

import lombok.extern.slf4j.Slf4j;
import org.etocrm.dynamicDataSource.service.IDynamicService;
import org.etocrm.kafkaServer.service.IKafkaProducerService;
import org.etocrm.tagManager.batch.impl.common.BatchTagCommonService;
import org.etocrm.tagManager.model.VO.tag.SysTagBrandsInfoVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: dkx
 * @Date: 9:46 2020/11/11
 * @Desc:
 */
@Component
@Slf4j
public class AvgRepurchaseCycleListener {

    @Autowired
    BatchTagCommonService batchTagCommonService;

    @Autowired
    IKafkaProducerService producerService;

    @Autowired
    IDynamicService dynamicService;


    @KafkaListener(topics = "${CUSTOM.KAFKA.TOPIC.AVG_REPURCHASE_CYCLE}", groupId = "${CUSTOM.KAFKA.GROUP.LIFE_CYCLE_GROUP}")
    public void brandsListener(String obj, Acknowledgment ack) {
        log.info("kafka 收到数据：" + obj);
        if (null != obj) {
            SysTagBrandsInfoVO brandsInfoVO = new SysTagBrandsInfoVO();
            String[] split = obj.split(",");
            brandsInfoVO.setBrandsId(Long.valueOf(split[0]));
            brandsInfoVO.setOrgId(Long.valueOf(split[1]));
            this.process(brandsInfoVO);
        }
        ack.acknowledge();
    }

    public void process(SysTagBrandsInfoVO brandsInfoVO) {
        //查询品牌机构下   订单数大于1的订单
        //获取时间差  / 总单数-1
        //所有人的平均复购时间
        List tables = new ArrayList();
        tables.add("(select member_id,datediff(max(pay_time),min(pay_time)) time,max(pay_time),min(pay_time),count(distinct orderno)-1 count " +
                "from t_orders where brands_id = " + brandsInfoVO.getBrandsId() + " and org_id = " + brandsInfoVO.getOrgId() + " and member_id is not null AND order_type=1 " +
                " group by member_id having (count(distinct orderno)>1)) a");
        List column = new ArrayList();
        column.add("ROUND(avg(a.time/a.count)) avgTime");
        Map map = dynamicService.selectById(tables, column, "1=1");
        if (null != map) {
            Object avgTime = map.get("avgTime");
            log.info("拿到平均复购周期：" + avgTime + " 更新品牌：" + brandsInfoVO.getBrandsId() + " 机构：" + brandsInfoVO.getOrgId());
            Map map1 = new HashMap();
            map1.put("avg_repurchase_cycle", avgTime);
            //修改 sys_brands  avg_repurchase_cycle 天
            dynamicService.updateRecord("sys_brands", map1, " id = " + brandsInfoVO.getBrandsId() + " and org_id = " + brandsInfoVO.getOrgId());
        }


    }
}
