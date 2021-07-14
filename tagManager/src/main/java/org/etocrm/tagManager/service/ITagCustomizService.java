package org.etocrm.tagManager.service;

import org.etocrm.core.util.ResponseVO;
import org.etocrm.dynamicDataSource.util.BasePage;
import org.etocrm.tagManager.enums.TagMethodEnum;
import org.etocrm.tagManager.model.VO.tag.SysTagVO;
import org.etocrm.tagManager.model.VO.tagCustomiz.SysTagCustomizAddVO;
import org.etocrm.tagManager.model.VO.tagCustomiz.SysTagCustomizUpdateVO;
import org.etocrm.tagManager.model.VO.tagCustomiz.TagCustomizListRequestVO;
import org.etocrm.tagManager.model.VO.tagCustomiz.TagExcelReadResponseVO;

import java.util.List;

public interface ITagCustomizService {

    ResponseVO<TagExcelReadResponseVO> saveTagCustomiz(SysTagCustomizAddVO addVO) throws Exception;

    ResponseVO<BasePage<List<SysTagVO>>> getSysTagListByPage(TagCustomizListRequestVO requestVO);

    ResponseVO deleteSysTagById(TagMethodEnum delete, Long id);

    ResponseVO downloadTemplate(String header);

    ResponseVO<TagExcelReadResponseVO> updateTagCustomiz(SysTagCustomizUpdateVO updateVO);
}
