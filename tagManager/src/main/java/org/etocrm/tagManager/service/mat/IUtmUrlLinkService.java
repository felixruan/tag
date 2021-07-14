package org.etocrm.tagManager.service.mat;

import org.etocrm.core.util.ResponseVO;
import org.etocrm.tagManager.model.VO.utm.UrlLinkAddVO;
import org.etocrm.tagManager.model.VO.utm.UrlLinkInPageVO;
import org.etocrm.tagManager.model.VO.utm.UrlLinkUpdateVO;

public interface IUtmUrlLinkService {


    ResponseVO addLink(UrlLinkAddVO addVO);

    ResponseVO deleteLinkById(Long id);

    ResponseVO updateLinkById(UrlLinkUpdateVO updateVO);

    ResponseVO getLinkById(Long id);

    ResponseVO getLinksByPage(UrlLinkInPageVO pageVO);


}
