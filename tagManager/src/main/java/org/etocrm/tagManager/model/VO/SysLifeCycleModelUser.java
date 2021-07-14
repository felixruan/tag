package org.etocrm.tagManager.model.VO;

import lombok.Data;
import org.etocrm.tagManager.model.DO.SysLifeCycleModelUserPO;

import java.util.List;

@Data
public class SysLifeCycleModelUser {
    private Long lifeCycleModelId;
    private Long lifeCycleModelRuleId;
    private List<SysLifeCycleModelUserPO> userPOList;
}
