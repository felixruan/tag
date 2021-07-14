package org.etocrm.dynamicDataSource.service;

import org.etocrm.dynamicDataSource.model.DO.WoaapManageBrandsDO;
import org.etocrm.dynamicDataSource.model.DO.WoaapOrgDO;

import java.util.List;

/**
 * @Author: dkx
 * @Date: 16:12 2020/12/15
 * @Desc:
 */
public interface IWoaapService {

    /**
     * 获取全部机构
     * @return
     */
    List<WoaapOrgDO> getListWoaapOrg();

    /**
     * 获取单独的机构
     * @param orgId
     * @return
     */
    WoaapOrgDO getWoaapOrg(String orgId);


    /**
     * 获取品牌管理appids
     * @param orgId
     * @return
     */
    List<WoaapManageBrandsDO> getListWoaapManageBrands(String orgId);

    /**
     * 获取品牌
     * @param woaapId
     * @return
     */
    WoaapManageBrandsDO getWoaapManageBrands(String woaapId);


    WoaapManageBrandsDO getAppNameByAppId(String appId);

}
