package org.etocrm.dataManager.service;

import org.etocrm.core.util.ResponseVO;
import org.etocrm.dataManager.model.VO.location.SysLocationPageVO;
import org.etocrm.dataManager.model.VO.location.SysLocationVO;

/**
 * <p>
 * 系统国家地区表  服务类
 * </p>
 * @author dkx
 * @Date 2020-09-02
 */
public interface SysLocationService {

    /**
     * 详情
     */
    ResponseVO detailByPk(Long pk);

    /**
     * 全查列表
     * @return
     */
    ResponseVO findAll(SysLocationVO sysLocation);

    /**
     * 分页查询
     */
    ResponseVO list(SysLocationPageVO sysLocation);
}