package org.etocrm.dataManager.listener;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.etocrm.dataManager.service.DBWriterService;
import org.etocrm.dataManager.util.TableData;
import org.etocrm.dynamicDataSource.service.ISourceDBService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.TreeMap;

/**
 * @Author: dkx
 * @Date: 11:38 2021/2/4
 * @Desc:
 */
@Service
@Slf4j
public class TableListenerAsync {


    @Autowired
    ISourceDBService iSourceDBService;

    @Autowired
    DBWriterService dbWriterService;

    //@Async
    public void asyncSave(BatchVO vo){
        long s = System.currentTimeMillis();
        log.error("======TableListenerAsync table:{},start:{},end:{}", vo.getTableName(), vo.getLimitStart(), vo.getEnd());
        List<LinkedHashMap<String, Object>> originData = iSourceDBService.selectEtlListId(vo.getOriginDatabaseId(), vo.getTableName(), vo.getColumn(), vo.getLimitStart(), vo.getEnd(), vo.getColumns());
        if (CollUtil.isNotEmpty(originData)) {
            TableData tableDatum = new TableData();
            tableDatum.setDestinationTableName(vo.getDestinationTableName());
            log.error("asyncServiceExecutorTag writer 查到数据量：{}", originData.size());
            long d = System.currentTimeMillis();
            log.error("asyncServiceExecutorTag writer 查询耗时：{}", (d - s));
            tableDatum.setOriginData(originData);
            dbWriterService.dbWriter(tableDatum);
            long w = System.currentTimeMillis();
            log.error("asyncServiceExecutorTag writer 入库耗时：{}", (w - d));
        }
        log.error("=====TableListenerAsync 所需时间:{}s", (System.currentTimeMillis() - s) / 1000);
    }

}
