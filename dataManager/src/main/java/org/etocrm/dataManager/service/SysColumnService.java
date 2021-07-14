package org.etocrm.dataManager.service;

import org.etocrm.core.util.ResponseVO;
import org.etocrm.dataManager.model.VO.column.SysColumnAddVO;
import org.etocrm.dataManager.model.VO.column.SysColumnPageVO;
import org.etocrm.dataManager.model.VO.column.SysColumnUpdateVO;
import org.etocrm.dataManager.model.VO.column.SysColumnVO;

import java.util.List;

public interface SysColumnService {
    ResponseVO saveSysColumn(SysColumnAddVO sysColumnVO);

    ResponseVO updateById(SysColumnUpdateVO sysColumnUpdateVO);

    ResponseVO deleteById(Long id);

    ResponseVO<List<SysColumnVO>> list(SysColumnPageVO vo);

    ResponseVO<List<SysColumnVO>> listAll(SysColumnPageVO vo);

    ResponseVO cache(String name);
}
