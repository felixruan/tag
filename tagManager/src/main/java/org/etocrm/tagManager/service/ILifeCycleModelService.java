package org.etocrm.tagManager.service;

import org.etocrm.core.util.ResponseVO;
import org.etocrm.tagManager.model.VO.lifeCycleModel.*;

import java.util.List;

public interface ILifeCycleModelService {

    ResponseVO<List<QueryListResponseVO>> getList(QueryListRequestVO requestVO);

    ResponseVO save(LifeCycelModelSaveRequestVO saveRequestVO);

    ResponseVO modify(LifeCycelModelModifyRequestVO modifyRequestVO);

    ResponseVO<LifeCycelModelGetRuleResponseVO> getRule(Long id);

    ResponseVO delete(Long id);

    ResponseVO getLifeCycleModelCount(LifeCycelModelCountRequestVO countRequestVO);
}
