package org.etocrm.dataManager.service;

import org.etocrm.core.util.ResponseVO;
import org.etocrm.dataManager.model.DO.SysSynchronizationConfigDO;
import org.etocrm.dataManager.model.VO.*;
import org.etocrm.dataManager.model.VO.SynchronizationConfig.ListPageConditionSysSynchronizationConfigVO;
import org.etocrm.dataManager.model.VO.SynchronizationConfig.SaveSysSynchronizationConfigVO;
import org.etocrm.dataManager.model.VO.SynchronizationConfig.SysSynchronizationConfigVO;
import org.etocrm.dataManager.model.VO.SynchronizationConfig.UpdateSysSynchronizationConfigVO;
import org.etocrm.dynamicDataSource.util.TagPageInfo;

import java.util.List;


/**
 * <p>
 * 系统数据同步规则表  服务类
 * </p>
 *
 * @author lingshuang.pang
 * @Date 2020-09-03
 */
public interface ISysSynchronizationConfigService {


    /**
     * 添加
     */
    ResponseVO saveSysSynchronizationConfig(SaveSysSynchronizationConfigVO sysSynchronizationConfigVO);

    /**
     * 修改
     */
    ResponseVO updateById(UpdateSysSynchronizationConfigVO sysSynchronizationConfigVO);

    /**
     * 删除
     */
    ResponseVO deleteById(Long id);

    /**
     * 详情
     */
    ResponseVO getById(Long id);

    /**
     * 全查列表
     *
     * @return
     */
    ResponseVO getList(SysSynchronizationConfigVO sysSynchronizationConfigVO);

    /**
     * 分页查询
     */
    ResponseVO getListByPage(TagPageInfo sysSynchronizationConfigVO);

    ResponseVO getListPageByCondition(ListPageConditionSysSynchronizationConfigVO sysSynchronizationConfigVO);

    ResponseVO getAllDatabase();

    ResponseVO getByDataTableId(Long id);

    ResponseVO etlUpdateById(SysSynchronizationConfigVO vo);

    ResponseVO getByDataTableIdAndTableName(DataTableNameVO dataTableNameVO);

    ResponseVO updateStatus(UpdateTableStatusVO updateTableStatusVO);

    List<SysSynchronizationConfigDO> getListEtl(SysSynchronizationConfigVO vo);

    ResponseVO getMainDatabaseTableField();


    ResponseVO getMainDatabaseTableName(DataTableNameVO dataTableNameVO);

    SysSynchronizationConfigDO synchronizationConfig(CanalDB canalDB);

    ResponseVO getOrgDatabase(TableDataVO tableDataVO);
}