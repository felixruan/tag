package org.etocrm.tagManager.service;

import org.etocrm.core.util.ResponseVO;
import org.etocrm.dynamicDataSource.util.BasePage;
import org.etocrm.tagManager.enums.TagMethodEnum;
import org.etocrm.tagManager.model.DO.SysTagDO;
import org.etocrm.tagManager.model.VO.YoungorDataRequestVO;
import org.etocrm.tagManager.model.VO.tag.*;
import org.etocrm.tagManager.model.VO.tagManager.GetTagNameRequestInfoVO;

import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * @author lingshuang.pang
 * @Date 2020/8/31 16:27
 */
public interface ISysTagService {

    ResponseVO singleDataSourceMethod(TagMethodEnum methodEnum, Object requestVO);

    ResponseVO<BasePage<List<SysTagVO>>> getSysTagListByPage(SysTagListRequestVO requestVO);

    ResponseVO<List<SysTagTreeVO>> getSysTagTree(Integer tagStatus, Integer querySystem);

    Integer getTagCountByParam(SysTagVO sysTagVO);

    Boolean checkTagDepending(Long tagId);

    SysTagDO getTagById(Long id, TagBrandsInfoVO brandsInfo);

    SysTagDO getTagById(Long id);

    List<SysTagDO> getSysTagListByDataManager(SysTagBrandsInfoVO brandsInfoVO);

    ResponseVO insert(SysTagDO sysTagDO);

    ResponseVO update(Set<Long> ids);

    ResponseVO<List<SysTagDO>> selectListByIds(Set<Long> ids);

    ResponseVO<List<SysTagDO>> getSysTagListByAuth(SysTagBrandsInfoVO brandsInfoVO);

    ResponseVO<List<SysTagDO>> getTagByMasterIds(Set<Long> ids, SysTagBrandsInfoVO tagBrandsInfoVO);

    ResponseVO<Integer> getTagCount(TagCountParam tagCountParam);

    ResponseVO<SysTagDetaileVO> getSysTagById(Long id);

    ResponseVO<Boolean> selectTagNameByTagId(GetTagNameRequestInfoVO requestInfoVO);

    /**
     * 获取下次更新日期
     *
     * @param lastUpdateDate
     * @param nextUpdateDate
     * @param updateFrequencyDictId
     * @return
     */
    Date getNextUpdateTime(Date lastUpdateDate, Date nextUpdateDate, Long updateFrequencyDictId);

    int update(SysTagDO sysTagDO);

    boolean checkTagByTagName(String tagName, TagBrandsInfoVO brandsInfo, Long id);

    void updateCoveredPeopleNum(Long tagId,Integer dataType, int value);

    ResponseVO getBrandsAppIds();

    ResponseVO saveYoungorData(YoungorDataRequestVO requestVO);
}
