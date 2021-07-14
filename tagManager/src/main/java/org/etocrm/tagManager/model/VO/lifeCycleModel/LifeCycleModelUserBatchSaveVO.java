package org.etocrm.tagManager.model.VO.lifeCycleModel;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LifeCycleModelUserBatchSaveVO implements Serializable {
    private static final long serialVersionUID = -7127838951253342341L;

    /**
     * 用户id list
     */
    private List<Long> userIdList;

    /**
     * 模型id
     */
    private Long modelId;

    /**
     * 模型规则id
     */
    private Long modelRuleId;
}
