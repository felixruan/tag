package org.etocrm.tagManager.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.etocrm.core.util.ResponseVO;
import org.etocrm.tagManager.model.DO.SysTagDO;
import org.etocrm.tagManager.model.DO.SysTagPropertyDO;
import org.etocrm.tagManager.model.VO.tagProperty.SysTagPropertyEditRequestVO;
import org.etocrm.tagManager.model.VO.tagProperty.SysTagPropertyQueryResponseVO;
import org.etocrm.tagManager.model.VO.tagProperty.SysTagPropertyResponseVO;

import java.util.List;
import java.util.Set;

/**
 * <p>
 * 系统标签属性表  服务类
 * </p>
 */
public interface ISysTagPropertyService extends IService<SysTagPropertyDO> {

    ResponseVO editSysTagProperty(SysTagPropertyEditRequestVO editVO);

    ResponseVO<List<SysTagPropertyQueryResponseVO>> getSysTagProperty(Long tagId);

    List<SysTagPropertyDO> getTagPropertyByTagId(Long tagId);

    ResponseVO<List<SysTagPropertyResponseVO>> getListByTagId(Long tagId);

    ResponseVO updateTagPropertyByTagId(SysTagPropertyDO sysTagPropertyDO);

    ResponseVO<List<SysTagPropertyDO>> getTagPropertyByTagIds(Set<Long> ids);

    ResponseVO insert(SysTagPropertyDO sysTagPropertyDO);

    ResponseVO updateSysTagPropertysById(Set<Long> ids);
}