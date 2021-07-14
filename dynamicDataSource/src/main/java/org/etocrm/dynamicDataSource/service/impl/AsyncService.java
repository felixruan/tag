package org.etocrm.dynamicDataSource.service.impl;

import org.etocrm.dynamicDataSource.mapper.IDynamicMapper;
import org.etocrm.dynamicDataSource.model.DO.DynamicDO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * @Author: dkx
 * @Date: 16:52 2021/2/8
 * @Desc:
 */
@Component
public class AsyncService {

    @Autowired
    private IDynamicMapper dynamicMapper;

    @Async
    public void batchAsync(String tableName, String column, List<String> list, CountDownLatch latch) {
        DynamicDO record = new DynamicDO();
        record.setOperateTableName(tableName);
        record.setColumn(column);
        record.setColumns(list);
        dynamicMapper.insertRecordBatch(record);
        latch.countDown();
    }
}
