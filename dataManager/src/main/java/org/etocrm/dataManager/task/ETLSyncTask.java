package org.etocrm.dataManager.task;

import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.etocrm.dataManager.batch.IBatchEtlService;
import org.etocrm.dataManager.batch.IEtlProcessingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @Author chengrong.yang
 * @Date 2020/11/9 10:00
 */
@Slf4j
@Component
public class ETLSyncTask {

    @Autowired
    IBatchEtlService etlService;

    @Autowired
    IEtlProcessingService etlProcessingService;

    @XxlJob("etlSyncHandle")
    public ReturnT<String> executeTest(String param) {

        try {
            log.info("ETL Job start");
            etlService.run();
            log.info("ETL Job end");
            return ReturnT.SUCCESS;
        } catch (Exception e) {
            // TODO send mail
            log.error(e.getMessage(), e);
            return ReturnT.FAIL;
        }
    }

    @XxlJob("etlProcessingSyncHandle")
    public ReturnT<String> executeEtlProcessing(String param) {
        try {
            log.info("ETL Processing Job start");
            etlProcessingService.run();
            log.info("ETL Processing Job end");
            return ReturnT.SUCCESS;
        } catch (Exception e) {
            // TODO send mail
            log.error(e.getMessage(), e);
            return ReturnT.FAIL;
        }
    }
}
