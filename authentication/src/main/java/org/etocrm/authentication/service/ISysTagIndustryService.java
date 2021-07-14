package org.etocrm.authentication.service;

import org.etocrm.authentication.entity.DO.SysTagIndustryDO;
import org.etocrm.authentication.entity.VO.tagBrands.SysTagClassesTreeVO;
import org.etocrm.authentication.entity.VO.tagIndustry.SysTagIndustryModifyVO;
import org.etocrm.authentication.entity.VO.tagIndustry.SysTagIndustryRequestVO;
import org.etocrm.core.util.ResponseVO;

import java.util.List;

public interface ISysTagIndustryService {

    ResponseVO modifyTagIndustry(SysTagIndustryModifyVO sysTagIndustryModifyVO);

    ResponseVO<List<SysTagClassesTreeVO>> getTagIndustryList(SysTagIndustryRequestVO tagIndustryRequestVO);

    ResponseVO<List<SysTagClassesTreeVO>> getTagTreeWithIndustry(SysTagIndustryRequestVO tagIndustryRequestVO);

    List<SysTagIndustryDO> getTagIndustryListById(Long industryId);

    ResponseVO deleteByTagId(Long tagId);
}
