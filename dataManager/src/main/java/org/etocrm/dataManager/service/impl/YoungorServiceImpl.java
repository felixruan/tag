package org.etocrm.dataManager.service.impl;

import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.etocrm.dataManager.model.VO.YoungorTagDataVO;
import org.etocrm.dataManager.service.IYoungorService;
import org.etocrm.dynamicDataSource.service.IDynamicService;
import org.etocrm.kafkaServer.service.IKafkaProducerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class YoungorServiceImpl implements IYoungorService {

    @Autowired
    private IKafkaProducerService producerService;

    @Autowired
    private IDynamicService dynamicService;

    String originTableName = "member_preference_tag_table";

    String destTableName = "youngor_member_preference_tag_table";

    @Value("${CUSTOM.KAFKA.TOPIC.YOUNGOR_TAG_TOPIC}")
    private String YOUNGOR_TAG_TOPIC;

    @Override
    public void save(Integer count, Integer start, Integer size) {
        if (null == start) {
            start = 0;
        }
        if (start < 1) {
            truncateTable();
        }

        Integer countStep = countStep(count, size);

        YoungorTagDataVO dataVO = new YoungorTagDataVO();
        dataVO.setTableNames(originTableName);
        int lastIndex = countStep - 1;
        for (Integer i = 0; i < countStep; i++) {
            dataVO.setStart(start);

            //最后一次，看是size,还是count-size*最后一个index
            if (i == lastIndex && count < size * countStep) {
                size = count - size * lastIndex;
            }
            dataVO.setSize(size);
            producerService.sendMessage(YOUNGOR_TAG_TOPIC, JSONUtil.toJsonStr(dataVO), i);

            start += size;
        }
    }


    private Integer countStep(Integer count, Integer size) {
        return (count + size - 1) / size;
    }


    private void truncateTable() {
        dynamicService.truncateTable(destTableName);
    }

}
