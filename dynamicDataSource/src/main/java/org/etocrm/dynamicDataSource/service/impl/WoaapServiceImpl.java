package org.etocrm.dynamicDataSource.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.etocrm.dynamicDataSource.mapper.IWoaapManagerBrandsMapper;
import org.etocrm.dynamicDataSource.mapper.IWoaapOrgMapper;
import org.etocrm.dynamicDataSource.model.DO.WoaapManageBrandsDO;
import org.etocrm.dynamicDataSource.model.DO.WoaapOrgDO;
import org.etocrm.dynamicDataSource.service.IWoaapService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Author: dkx
 * @Date: 16:13 2020/12/15
 * @Desc:
 */

@Service
public class WoaapServiceImpl implements IWoaapService {

    @Resource
    IWoaapOrgMapper woaapOrgMapper;

    @Resource
    IWoaapManagerBrandsMapper woaapManagerBrandsMapper;

    @Override
    public List<WoaapOrgDO> getListWoaapOrg() {
        return woaapOrgMapper.selectList(new LambdaQueryWrapper<>());
    }

    @Override
    public WoaapOrgDO getWoaapOrg(String orgId) {
        return woaapOrgMapper.selectOne(new LambdaQueryWrapper<WoaapOrgDO>().eq(WoaapOrgDO::getWoaapId, orgId));
    }

    @Override
    public List<WoaapManageBrandsDO> getListWoaapManageBrands(String orgId) {
        return woaapManagerBrandsMapper.selectList(new LambdaQueryWrapper<WoaapManageBrandsDO>()
                .eq(WoaapManageBrandsDO::getOrganizationId, orgId)
                );
    }

    @Override
    public WoaapManageBrandsDO getWoaapManageBrands(String woaapId) {
        return woaapManagerBrandsMapper.selectOne(new LambdaQueryWrapper<WoaapManageBrandsDO>().eq(WoaapManageBrandsDO::getWoaapId, woaapId)
        );
    }

    @Override
    public WoaapManageBrandsDO getAppNameByAppId(String appId) {
        return woaapManagerBrandsMapper.selectOne(new LambdaQueryWrapper<WoaapManageBrandsDO>()
                .eq(WoaapManageBrandsDO::getWechatAppid,appId)
                );
    }
}
