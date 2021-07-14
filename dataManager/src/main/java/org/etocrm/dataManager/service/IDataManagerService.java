package org.etocrm.dataManager.service;

import org.etocrm.core.util.ResponseVO;
import org.etocrm.dataManager.model.VO.*;
import org.etocrm.dataManager.model.VO.dataSource.*;
import org.etocrm.dataManager.model.VO.tagProperty.SysTagBrandsInfoVO;

/**
 * @Author chengrong.yang
 * @date 2020/8/31 0:56
 */
public interface IDataManagerService {

    ResponseVO getDataSourceListAllByPage(GetDataSourceForPageVO getDataSourceForPageVO);

    ResponseVO getDataSourceList(Long brandsId);

    ResponseVO getDataSourceById(Long id);

    ResponseVO getDataSourceListByParam(GetDataSourceVO getDataSourceVO);

    ResponseVO addDataSource(AddDataSourceVO addDataSourceVO);

    ResponseVO updateDataSource(DataSourceVO dataSourceVO);

    ResponseVO deleteDataSource(Long id);

    ResponseVO updateDataStatus(UpdateDataStatusVO updateDataStatusVO);

    ResponseVO testConnection(AddDataSourceVO addDataSourceVO);

    ResponseVO<DataAndDbSourceReturnVo> getDataSourceInfo(Long id);

    ResponseVO<Boolean> existsDatasourceByBrandsInfo(SysTagBrandsInfoVO sysTagBrandsInfoVO);

}
