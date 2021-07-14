package org.etocrm.tagManager.service;

import org.etocrm.core.util.ResponseVO;
import org.etocrm.tagManager.model.VO.ModelTable.TagSysModelTableColumnVO;
import org.etocrm.tagManager.model.VO.tagGroup.SysTagGroupUserSplitDetailVO;

import java.util.List;

/**
 * Create By peter.li
 */
public interface AsyncServiceManager {

    void asyncSplitTagGroupUsersDetail(SysTagGroupUserSplitDetailVO splitVO/*,Long dataSourceId*/);

    void asyncBatchSaveLifeCycleUser(Long modelId, Long modelRuleId, List<Long> userIdList);

    void asyncSetModelColumnInfoRedis(Long modelTableId);

    ResponseVO<List<TagSysModelTableColumnVO>> getModelColumnByTableIdFromDB(Long modelTableId);

    void asyncDeleteTag(Long tagId, String tagType);
}
