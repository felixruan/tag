package org.etocrm.authentication.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.etocrm.authentication.entity.DO.SysPermissionDO;
import org.etocrm.authentication.mapper.ISysPermissionMapper;
import org.etocrm.authentication.service.ISysPermissionService;
import org.etocrm.core.enums.BusinessEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author peter.li
 * @date 2020/8/17 14:20
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class SysPermissionServiceImpl implements ISysPermissionService {

    private static final Logger log = LoggerFactory.getLogger(AuthServiceImpl.class);

    @Autowired
    private ISysPermissionMapper iSysPermissionMapper;


    @Override
    public Boolean saveSysPermission(SysPermissionDO sysPermissionDO) {
        try{
            iSysPermissionMapper.insert(sysPermissionDO);
            return true;
        }catch (Exception e){
            log.error(e.getMessage(), e);
            return false;
        }
    }

    @Override
    public Integer getCountByRoleId(Integer roleId) {
        try{
            QueryWrapper query = new QueryWrapper();
            query.eq("role_id",roleId);
            query.eq("is_delete", BusinessEnum.NOTDELETED.getCode());
            Integer count = iSysPermissionMapper.selectCount(query);
            return count;
        }catch (Exception e){
            log.error(e.getMessage(), e);
            return null;
        }
    }

    @Override
    public List<SysPermissionDO> getSysPermissionsByUserId(Integer userId) {
        try{
            QueryWrapper<SysPermissionDO> query = new QueryWrapper<>();
            query.eq("user_id",userId);
            query.eq("is_delete", BusinessEnum.NOTDELETED.getCode());
            List<SysPermissionDO> sysPermissionDOs = iSysPermissionMapper.selectList(query);
            return sysPermissionDOs;
        }catch (Exception e){
            log.error(e.getMessage(), e);
            return new ArrayList<SysPermissionDO>();
        }
    }

    @Override
    public List<Integer> getUserIdsByRoleId(Integer roleId) {
        List<Integer> userIdList = null;
        try{
            QueryWrapper<SysPermissionDO> query = new QueryWrapper<>();
            query.eq("role_id",roleId);
            query.eq("is_delete", BusinessEnum.NOTDELETED.getCode());
            List<SysPermissionDO> sysPermissionDOs = iSysPermissionMapper.selectList(query);
            if(sysPermissionDOs == null){
                return userIdList;
            }
            userIdList = new ArrayList<>(sysPermissionDOs.size());
            for(SysPermissionDO sysPermissionDO : sysPermissionDOs){
                userIdList.add(sysPermissionDO.getUserId());
            }
            return userIdList;
        }catch (Exception e){
            log.error(e.getMessage(), e);
            return userIdList;
        }
    }

    @Override
    public Boolean removeByUserId(Integer userId) {
        try{
            QueryWrapper<SysPermissionDO> query = new QueryWrapper<>();
            query.eq("user_id",userId);
            query.eq("is_delete", BusinessEnum.NOTDELETED.getCode());
            List<SysPermissionDO> sysPermissionDOs = iSysPermissionMapper.selectList(query);
            for(SysPermissionDO sysPermissionDO : sysPermissionDOs){
                sysPermissionDO.setDeleted(BusinessEnum.DELETED.getCode());
                iSysPermissionMapper.updateById(sysPermissionDO);
          //      iSysPermissionMapper.deleteById(sysPermissionDO.getId());
            }
            return true;
        }catch (Exception e){
            log.error(e.getMessage(), e);
            return false;
        }
    }

    @Override
    public Boolean removeByRoleId(Integer roleId,Integer userId) {
        try{
            QueryWrapper<SysPermissionDO> query = new QueryWrapper<>();
            query.eq("role_id",roleId);
            query.eq("user_id",userId);
            query.eq("is_delete", BusinessEnum.NOTDELETED.getCode());
            List<SysPermissionDO> sysPermissionDOs = iSysPermissionMapper.selectList(query);
            if(sysPermissionDOs != null && sysPermissionDOs.size() > 0){
                for(SysPermissionDO sysPermissionDO : sysPermissionDOs){
                    sysPermissionDO.setDeleted(BusinessEnum.DELETED.getCode());
                    iSysPermissionMapper.updateById(sysPermissionDO);
                }
            }

            return true;
        }catch (Exception e){
            log.error(e.getMessage(), e);
            return false;
        }
    }
}
