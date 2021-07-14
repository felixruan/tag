package org.etocrm.dynamicDataSource.service;

import org.etocrm.dynamicDataSource.model.DO.SysAuditLogDO;

/**
 * @Author chengrong.yang
 * @Date 2020/9/18 10:25
 */
public interface IAuditLogService {

    void saveAuditLog(SysAuditLogDO sysAuditLogDO);

}
