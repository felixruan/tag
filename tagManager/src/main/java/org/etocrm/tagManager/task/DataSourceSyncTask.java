package org.etocrm.tagManager.task;

import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.etocrm.tagManager.batch.IAvgRepurchaseCycleService;
import org.etocrm.tagManager.batch.IBatchLifeCycleService;
import org.etocrm.tagManager.batch.IBatchTagGroupService;
import org.etocrm.tagManager.batch.IBatchTagService;
import org.etocrm.tagManager.service.ILifeCycleModelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @Author chengrong.yang
 * @date 2020/8/31 20:35
 */
@Slf4j
@Component
public class DataSourceSyncTask {

    @Autowired
    IBatchTagService tagService;

    @Autowired
    IBatchTagGroupService tagGroupService;

    @Autowired
    IAvgRepurchaseCycleService avgRepurchaseCycleService;

    @Autowired
    IBatchLifeCycleService lifeCycleService;

    @XxlJob("tagRuleHandle")
    public ReturnT<String> tagRuleHandle(@RequestParam String str) throws Exception {
        try {
            log.info("tagRuleHandle Job start");
            tagService.run();
            log.info("tagRuleHandle Job end");
            return ReturnT.SUCCESS;
        } catch (Exception e) {
            //TODO send mail
            log.error(e.getMessage(), e);
            return ReturnT.FAIL;
        }
    }

    @XxlJob("tagGroupRuleHandle")
    public ReturnT<String> tagGroupRuleHandle(@RequestParam String str) throws Exception {
        try {
            log.info("tagGroupRuleHandle Job start");
            tagGroupService.run();
            log.info("tagGroupRuleHandle Job end");
            return ReturnT.SUCCESS;
        } catch (Exception e) {
            //TODO send mail
            log.error(e.getMessage(), e);
            return ReturnT.FAIL;
        }
    }

    @XxlJob("avgRepurchaseCycle")
    public ReturnT<String> avgRepurchaseCycle(@RequestParam String str) throws Exception {
        try {
            log.info("avgRepurchaseCycle Job start");
            avgRepurchaseCycleService.run();
            log.info("avgRepurchaseCycle Job end");
            return ReturnT.SUCCESS;
        } catch (Exception e) {
            //TODO send mail
            log.error(e.getMessage(), e);
            return ReturnT.FAIL;
        }
    }

    @XxlJob("lifeCycleHandle")
    public ReturnT<String> lifeCycleHandle(@RequestParam String str) throws Exception {
        try {
            log.info("lifeCycleHandle Job start");
            lifeCycleService.run();
            log.info("lifeCycleHandle Job end");
            return ReturnT.SUCCESS;
        } catch (Exception e) {
            //TODO send mail
            log.error(e.getMessage(), e);
            return ReturnT.FAIL;
        }
    }
}


