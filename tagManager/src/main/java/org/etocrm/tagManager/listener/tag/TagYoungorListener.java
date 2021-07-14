package org.etocrm.tagManager.listener.tag;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.etocrm.core.util.JsonUtil;
import org.etocrm.core.util.StringUtil;
import org.etocrm.dynamicDataSource.service.IDynamicService;
import org.etocrm.tagManager.constant.TagConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Set;


@Component
@Slf4j
public class TagYoungorListener {

    @Autowired
    private IDynamicService dynamicService;

    @KafkaListener(topics = "${CUSTOM.KAFKA.TOPIC.TAG_YOUNGOR_DATA_TOPIC}", groupId = "${CUSTOM.KAFKA.GROUP.TAG_YOUNGOR_DATA_GROUP}")
    public void saveTagYoungorDataListener(String dataListStr, Acknowledgment ack) {
        log.info("saveTagYoungorDataListener kafka 收到数据" );
        if (null != dataListStr) {
            List<HashMap<String, Object>> hashMaps = JsonUtil.JsonToMapList(dataListStr);
            Set<String> strings = hashMaps.get(0).keySet();
            String column = StringUtil.humpToLine2(StringUtils.join(strings, ","));
            List<String> columnList = Arrays.asList(column.split(","));
            String count = dynamicService.saveOrUpdateRecordBatch(TagConstant.YOUNGOR_TABLE_NAME, columnList, hashMaps);
            log.info("============ saveYoungorData count:{}",count);
        }
        ack.acknowledge();
    }
}
