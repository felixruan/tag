package org.etocrm.tagManager.service.mat;

import org.etocrm.core.util.ResponseVO;
import org.etocrm.tagManager.model.VO.mat.*;

public interface IMatMetadataUserPropertyService {


    ResponseVO addProperty(MetadataUserPropertyAddVO addVO);

    ResponseVO deletePropertyById(Long id);

    ResponseVO updatePropertyById(MetadataUserPropertyUpdateVO updateVO);

    ResponseVO getPropertyById(Long id);

    ResponseVO getPropertiesByPage(MetadataUserPropertyInPageVO pageVO);

}
