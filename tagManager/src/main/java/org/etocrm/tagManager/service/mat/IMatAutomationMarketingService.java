package org.etocrm.tagManager.service.mat;

import org.etocrm.core.util.ResponseVO;
import org.etocrm.tagManager.model.VO.mat.MatWorkProcessSendRecordVO;
import org.etocrm.tagManager.model.VO.mat.MatWorkProcessVO;

import java.util.List;
import java.util.TreeMap;

public interface IMatAutomationMarketingService {

    void calculationTagGroupByProcessRule(MatWorkProcessVO matWorkProcessVO);

    void saveMarketingRule(MatWorkProcessVO matWorkProcessVO);

    List<TreeMap> eventProcessPropertyScreen(MatWorkProcessVO matWorkProcessVO) ;

    void asyncBatchSaveSendRecord(List<MatWorkProcessSendRecordVO> sendRecordVOs);

    String getMemberInfo(TreeMap map) throws Exception;

    String getMatTokenByOrgId(String orgId);

}
