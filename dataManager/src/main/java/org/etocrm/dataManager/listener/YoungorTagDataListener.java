package org.etocrm.dataManager.listener;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.etocrm.dataManager.model.VO.YoungorTagDataVO;
import org.etocrm.dataManager.service.DBWriterService;
import org.etocrm.dataManager.util.TableData;
import org.etocrm.dynamicDataSource.service.ISourceDBService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

@Component
@Slf4j
public class YoungorTagDataListener {

    @Autowired
    private ISourceDBService iSourceDBService;

    @Autowired
    private DBWriterService dbWriterService;

    String destTableName = "youngor_member_preference_tag_table";


    @KafkaListener(topics = "${CUSTOM.KAFKA.TOPIC.YOUNGOR_TAG_TOPIC}", groupId = "${CUSTOM.KAFKA.GROUP.YOUNGOR_GROUP}", containerFactory = "myKafkaBatchFactory"
    )
    public void saveTagDataListener(String dataStr, Acknowledgment ack) {
        if (StrUtil.isNotBlank(dataStr)) {
            log.error("saveTagDataListener 进入分批保存" + dataStr);
            YoungorTagDataVO youngorTagDataVO = JSONUtil.toBean(dataStr, YoungorTagDataVO.class);
            long s = System.currentTimeMillis();
            this.doSave(youngorTagDataVO);
            ack.acknowledge();
            log.error("=====YOUNGOR_TAG_TOPIC 所需时间:{}s", (System.currentTimeMillis() - s) / 1000);
        }

    }


    private void doSave(YoungorTagDataVO dataVO) {
        long begin = System.currentTimeMillis();
        List<String> column = getColumn();

        List<LinkedHashMap<String, Object>> originData = iSourceDBService.selectYoungorListId(dataVO.getTableNames(), column, dataVO.getStart(), dataVO.getSize());
        if (CollUtil.isNotEmpty(originData)) {
            TableData tableDatum = new TableData();
            tableDatum.setDestinationTableName(destTableName);
            log.error("YoungorService doSave writer 查到数据量：{}，查询耗时：{}", originData.size(), (System.currentTimeMillis() - begin));
            tableDatum.setOriginData(originData);
            dbWriterService.dbWriter(tableDatum);
            long w = System.currentTimeMillis();
            log.error("YoungorService doSave writer 入库耗时：" + (w - begin));
        }
    }


    private static List<String> getColumn() {
        String[] columnArr = {"IMEMBERID as   member_id ", "CPHONE as phone", "DREGISTERTIME  as   register_time", "CCARDNO   as cardno", "CREALNAME as   realname", "ISEX  as  sex", "AGE   as age", "BIRTHDATE as   birth_date", "BIRTHMONTH as birth_month", "CHINESEZODIAC  as   chinese_zodiac", "CONSTELLATION  as   constellation", "PROVINCECITY  as  province_city ", "STORECODE as   store_code", "STORENAME as   store_name", "IPIANQUCODE   as pianqu_code", "IPIANQUNAME   as pianqu_name", "IAREACODE as   area_code ", "IAREANAME as   area_name ", "IYXGSCODE as   yxgs_code ", "IYXGSNAME as   yxgs_name ", "INITIATIONTIME as initiation_time", "MEMBERLEVEL   as member_level", "MEMBERLIFECYCLE   as  member_life_cycle", "CURRENTPOINT  as  current_point ", "CONSUMSUM as   consum_sum", "CONSUMPTIONNUMYTD as   consumption_num_ytd", "CONSUMPTIONNUM as consumption_num", "CONSUMPTIONLASTMONTH  as  consumption_last_month", "CONSUMPTIONLASTQUARTER as consumption_last_quarter", "CONSUMPTIONFIRSTHALFYEAR  as  consumption_first_half_year", "CONSUMPTIONLASTYEAR   as consumption_last_year", "CONSUMBRAND   as consum_brand", "BRAND_PREFER_NAME as   brand_prefer_name", "DALEI_PREFER_NAME as   dalei_prefer_name", "PROD_STYLE_PREFER_NAME as prod_style_prefer_name", "SEASON_PREFER_NAME as season_prefer_name", "COLOR_PREFER_NAME as   color_prefer_name", "SERIES_PREFER_NAME as series_prefer_name", "BANXING_PREFER_NAME   as banxing_prefer_name", "CONSUMRANGE_PREFER as consum_range_prefer", "DISCOUNTRANGE_PREFER  as  discount_range_prefer", "CONSUMDAY_CLASS_PREFER_LM as   consum_day_class_prefer_lm", "CONSUMDAY_CLASS_PREFER_LQ as   consum_day_class_prefer_lq", "CONSUMTIMERANGE_PREFER as consumtime_range_prefer", "CONSUM_CHANNEL_PREFER_NAME as consum_channel_prefer_name", "PAY_MODE_PREFER_NAME_OFFLINE  as  pay_mode_prefer_name_offline"};
        return Arrays.asList(columnArr);
    }
}