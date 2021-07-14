package org.etocrm.dataManager.service;

import org.etocrm.core.util.ResponseVO;
import org.etocrm.dataManager.model.VO.dbSource.DbSourceGetInfoVO;
import org.etocrm.dataManager.model.VO.dbSource.SysDbSourceVO;

public interface SysDbSourceService {
    ResponseVO addSysDBSource(SysDbSourceVO sysDbSourceVO);

    ResponseVO getSysDBSourceListAll();

    ResponseVO getSysDBSourceById(Long id);

    ResponseVO getSysDBSourceListAllByPage(DbSourceGetInfoVO dbSourceGetInfoVO);

    ResponseVO updateSysDBSource(SysDbSourceVO sysDbSourceVO);

    ResponseVO deleteSysDBSource(Long id);

    ResponseVO getSysDBSourceByDataSourceId(Long id);

    ResponseVO getSysDBSourceByParam(DbSourceGetInfoVO dbSourceGetInfoVO);
}
