package org.etocrm.tagManager.service;

import com.alibaba.fastjson.JSONObject;
import org.etocrm.core.util.ResponseVO;
import org.etocrm.tagManager.model.DO.SysTagGroupUserPO;
import org.etocrm.tagManager.model.VO.tag.TagBrandsInfoVO;
import org.etocrm.tagManager.model.VO.tagGroup.*;

import java.util.List;
import java.util.Map;

public interface ITagGroupUserService {


    ResponseVO getTagGroupUsersDetail(SysTagGroupUserDetailPageVO pageVO);

    Map<String, List> getUsersDetailToExecl(JSONObject excelObj, Integer exportType, TagBrandsInfoVO brandsInfoVO);

    ResponseVO splitTagGroupUsersDetail(SysTagGroupUserSplitDetailVO splitVO);

    void asyncSaveBatchGroupUser(GroupUserSaveBatchVO groupUserSaveBatchVO);

    ResponseVO getTagGroupUsersDetailCount(Long tagGroupId);

    ResponseVO asyncSaveBatchGroupUser(Long groupId, List<SysTagGroupUserPO> groupUserPOList);

    ResponseVO<TagGroupTableResponseVO> getTableInfo(List<Long> tagGroupIds);

    ResponseVO<TagGroupChartResponseVO> getChartInfo(TagGroupChartRequestVO requestVO);

    ResponseVO<List<TagClassesInfoVO>> getTagClassesList();

}
