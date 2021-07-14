package org.etocrm.tagManager.service.mat;

import org.etocrm.core.util.ResponseVO;
import org.etocrm.tagManager.model.VO.mat.MetadataGetPropertyByEventCodeVO;
import org.etocrm.tagManager.model.VO.mat.MetadataPropertyAddVO;
import org.etocrm.tagManager.model.VO.mat.MetadataPropertyInPageVO;
import org.etocrm.tagManager.model.VO.mat.MetadataPropertyUpdateVO;

public interface IMatMetadataPropertyService {


    ResponseVO addProperty(MetadataPropertyAddVO addVO);

    ResponseVO deletePropertyById(Long id);

    ResponseVO updatePropertyById(MetadataPropertyUpdateVO updateVO);

    ResponseVO getPropertyById(Long id);

    ResponseVO getPropertiesByPage(MetadataPropertyInPageVO pageVO);

    ResponseVO getPropertyByEventCode(MetadataGetPropertyByEventCodeVO getByVO);
}
