package org.etocrm.tagManager.service;

import org.etocrm.core.util.ResponseVO;
import org.etocrm.tagManager.model.DO.SysTagPropertyUserPO;
import org.etocrm.tagManager.model.DO.SysTagPropertyWeChatUserPO;
import org.etocrm.tagManager.model.VO.tagProperty.FileTagVo;
import org.etocrm.tagManager.model.VO.tagProperty.SysTagPropertyUserDetailPageVO;

import java.util.List;
import java.util.Set;

/**
 * <p>
 * 系统标签属性用户映射表  服务类
 * </p>
 */
public interface ISysTagPropertyUserService {


    void batchInsert(List<SysTagPropertyUserPO> sysTagPropertyUserDOS);

    void batchInsertWeChat(List<SysTagPropertyWeChatUserPO> sysTagPropertyWeChatUserPOS);

    ResponseVO getUsersDetailByPage(SysTagPropertyUserDetailPageVO pageVO);


    /**
     * 查询人群数量  根据标签id
     */
    Integer getPeopleCountByTagId(Long tagId);

    /**
     * 查询人群数量  根据标签属性id
     */
    Integer getCountByPropertyId(Long propertyId);

    Integer deleteByTagId(Long tagId, String tagType, Integer deleteTotal);

    ResponseVO exportUser(String excelJson);

    ResponseVO fileUpload(FileTagVo fileTagVo);
}