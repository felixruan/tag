package org.etocrm.tagManager.service;

import org.etocrm.core.util.ResponseVO;
import org.etocrm.tagManager.model.DO.SysModelTableColumnDO;
import org.etocrm.tagManager.model.VO.ModelTable.AddSysModelTableColumnVO;
import org.etocrm.tagManager.model.VO.ModelTable.ListPageSysModelTableColumnVO;
import org.etocrm.tagManager.model.VO.ModelTable.SysModelTableColumnVO;
import org.etocrm.tagManager.model.VO.ModelTable.UpdateSysModelTableColumnVO;

public interface ISysModelTableColumnService {
    ResponseVO saveSysModelColumnTable(AddSysModelTableColumnVO sysModelTableColumnVO);

    ResponseVO updateSysModelColumnTableById(UpdateSysModelTableColumnVO sysModelTableColumnVO);

    ResponseVO getSysModelColumnTableById(ListPageSysModelTableColumnVO id);

    ResponseVO getSysModelColumnTableListAll();

    ResponseVO getSysModelColumnTableListAllByPage(ListPageSysModelTableColumnVO sysModelTableColumnVO);

    ResponseVO getSysModelColumnTableListByParam(SysModelTableColumnVO sysModelTableColumnVO);

    ResponseVO getSysModelColumnTableListByParamByPage(SysModelTableColumnVO sysModelTableColumnVO);

    ResponseVO deleteById(Long id);

    SysModelTableColumnDO selectSysModelColumnTableById(Long id);

    ResponseVO getTagSysModelColumnTableById(Long id);

    ResponseVO getSysModelColumnTableDynamicListById(Long id);

    ResponseVO getSysModelColumnTableRelationListById(Long id);

    ResponseVO getSysModelColumnTableListAllById(Long id);

}
