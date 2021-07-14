package org.etocrm.dataManager.service;

import org.etocrm.core.util.ResponseVO;
import org.etocrm.dataManager.model.VO.job.AddSysJobVO;
import org.etocrm.dataManager.model.VO.job.EditSysJobVO;
import org.etocrm.dataManager.model.VO.job.SysJobVo;
import org.etocrm.dataManager.model.VO.TriggerJobVO;

/**
 * @Author chengrong.yang
 * @date 2020/9/1 13:58
 */
public interface IJobManagerService {
    ResponseVO getGroup();

    ResponseVO getJobByPage(SysJobVo sysJobVo);

    ResponseVO addJob(AddSysJobVO addSysJobVo);

    ResponseVO triggerJob(TriggerJobVO triggerJobVO);

    ResponseVO removeJob(Integer id);

    ResponseVO stopJob(Integer id);

    ResponseVO startJob(Integer id);

    ResponseVO editJob(EditSysJobVO editSysJobVO);
}
