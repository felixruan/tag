package org.etocrm.tagManager.service.mat;

import org.etocrm.core.util.ResponseVO;
import org.etocrm.tagManager.model.VO.mat.MetadataEventAddVO;
import org.etocrm.tagManager.model.VO.mat.MetadataEventInPageVO;
import org.etocrm.tagManager.model.VO.mat.MetadataEventUpdateVO;

public interface IMatMetadataEventService {


    ResponseVO addEvent(MetadataEventAddVO addVO);

    ResponseVO deleteEventById(Long id);

    ResponseVO updateEventById(MetadataEventUpdateVO updateVO);

    ResponseVO getEventById(Long id);

    ResponseVO getEventsByPage(MetadataEventInPageVO pageVO);

    ResponseVO getEventsByPropertyCode(String propertyCode);


}
