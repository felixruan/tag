package org.etocrm.tagManager.service;

import org.etocrm.core.util.ResponseVO;
import org.etocrm.dynamicDataSource.util.TagPageInfo;
import org.etocrm.tagManager.model.DO.SysModelTableDO;
import org.etocrm.tagManager.model.VO.*;
import org.etocrm.tagManager.model.VO.ModelTable.*;

import java.util.List;

public interface ISysModelTableService {
    ResponseVO saveSysModelTable(AddSysModelTableVO sysModelTableVO);

    ResponseVO updateSysModelTableById(UpdateSysModelTableVO sysModelTableVO);

    ResponseVO getSysModelTableById(Long id);

    ResponseVO getSysModelTableListPageAll(TagPageInfo tagPageInfo);

    ResponseVO getSysModelTableListAllByPage(SysModelTableVO sysModelTableVO);

    ResponseVO getSysModelTableListByParam(SysModelTableVO sysModelTableVO);

    ResponseVO getSysModelTableListByParamByPage(ListPageSysModelTableVO sysModelTableVO);

    ResponseVO deleteById(Long id);

    ResponseVO createModel(Long id);

    ResponseVO<List<SysModelTableListResponseVO>> getSysModelTableListAll(Integer dataFlag);

    ResponseVO getSysModelTableColumns(String tableName);

    SysModelTableDO selectSysModelTableById(Long id);

    ResponseVO getSysModelTableAllList();

    ResponseVO updateStatus(UpdateTableStatusVO updateTableStatusVO);
}
