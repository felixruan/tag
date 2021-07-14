package org.etocrm.tagManager.service.mat;

import org.etocrm.core.util.ResponseVO;
import org.etocrm.tagManager.model.VO.mat.*;

import java.util.List;


public interface IMatProvideOfAutomationMarketingService {

    ResponseVO receiveMarketingRule(MatWorkProcessVO matWorkProcessVO);

    ResponseVO receiveMarketingRecords(List<MatWorkProcessSendRecordVO> sendRecordVOs);

    ResponseVO getEventsOrProperties(Integer modularId, Integer eventId);

    ResponseVO receiveReportingData(MatReportingParameter reportingParameter);

    ResponseVO getTagGroups(Long originalId);

    ResponseVO getPropertyScreenList(Integer modularId,Integer modelId);

    ResponseVO getPropertyScreenGroupUsers(MatQueryMarketingVO queryVO);

    ResponseVO getModelColumnRelationListById(Long orgId,Long columnId);

    void matchingEventMarketingProcess(MatReportingParameter reportingParameter) throws Exception;

    ResponseVO getHandleIdByUserInfo(MatUserInfoVO userInfoVO);


    ResponseVO batchReceivePassiveWorkReportingData(MatBatchReportingParameter batchReportingParameter);

    void matchingUserByRule(MatBatchReportingParameter batchReportingParameter);


}
