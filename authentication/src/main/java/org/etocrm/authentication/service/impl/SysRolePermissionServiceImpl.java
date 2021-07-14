package org.etocrm.authentication.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.etocrm.authentication.entity.DO.SysRolePermissionDO;
import org.etocrm.authentication.mapper.ISysRolePermissionMapper;
import org.etocrm.authentication.service.ISysRolePermissionService;
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
public class SysRolePermissionServiceImpl implements ISysRolePermissionService {

    private static final Logger log = LoggerFactory.getLogger(AuthServiceImpl.class);

    @Autowired
    private ISysRolePermissionMapper iSysRolePermissionMapper;


    /**
     * 新增角色和菜单关联关系
     * @param sysRolePermissionDO
     */
    @Override
    public void save(SysRolePermissionDO sysRolePermissionDO) {
        try {
            iSysRolePermissionMapper.insert(sysRolePermissionDO);
        }catch (Exception e){
            log.error(e.getMessage(), e);
        }
    }

    /**
     * 根据菜单id查询记录数
     * @param menuId
     * @return
     */
    @Override
    public Integer getCountByMenuId(Integer menuId) {
        try {
            QueryWrapper<SysRolePermissionDO> query = new QueryWrapper<>();
            query.eq("menu_id",menuId);
            query.eq("is_delete", BusinessEnum.NOTDELETED.getCode());
            Integer count = iSysRolePermissionMapper.selectCount(query);
            return count;
        }catch (Exception e){
            log.error(e.getMessage(), e);
            return null;
        }
    }

    /**
     * 根据角色id查询角色和菜单关联关系
     * @param roleId
     * @return
     */
    @Override
    public List<SysRolePermissionDO> getListByRoleId(Integer roleId) {
        try {
            QueryWrapper<SysRolePermissionDO> query = new QueryWrapper<>();
            query.eq("role_id",roleId);
            query.eq("is_delete", BusinessEnum.NOTDELETED.getCode());
            List<SysRolePermissionDO> sysRolePermissionDOs = iSysRolePermissionMapper
                    .selectList(query);
            return sysRolePermissionDOs;
        }catch (Exception e){
            log.error(e.getMessage(), e);
            return new ArrayList<SysRolePermissionDO>();
        }
    }

    /**
     * 根据角色id删除角色和菜单关联关系
     * @param roleId
     */
    @Override
    public Boolean removeByRoleId(Integer roleId) {
        try {
            QueryWrapper<SysRolePermissionDO> query = new QueryWrapper<>();
            query.eq("role_id",roleId);
            query.eq("is_delete", BusinessEnum.NOTDELETED.getCode());
            List<SysRolePermissionDO> sysRolePermissionDOs =
                    iSysRolePermissionMapper.selectList(query);
            if(sysRolePermissionDOs!=null&&sysRolePermissionDOs.size() > 0){
                for(SysRolePermissionDO sysRolePermissionDO : sysRolePermissionDOs){
                    sysRolePermissionDO.setDeleted(BusinessEnum.DELETED.getCode());
                    iSysRolePermissionMapper.updateById(sysRolePermissionDO);
      //              iSysRolePermissionMapper.deleteById(sysRolePermissionDO.getId());
                }
            }
            return true;
        }catch (Exception e){
            log.error(e.getMessage(), e);
            return false;
        }
    }


    /**
     * 删除角色和菜单关联关系
     * @param menuIds
     */
    @Override
    public Boolean removeByList(List<Integer> menuIds,Integer roleId) {
        try {
            for(Integer menuId : menuIds){
                QueryWrapper<SysRolePermissionDO> query = new QueryWrapper<>();
                query.eq("menu_id",menuId);
                query.eq("role_id",roleId);
                query.eq("is_delete", BusinessEnum.NOTDELETED.getCode());
                SysRolePermissionDO sysRolePermissionDO = new SysRolePermissionDO();
                sysRolePermissionDO.setDeleted(BusinessEnum.DELETED.getCode());
                iSysRolePermissionMapper.update(sysRolePermissionDO,query);
            }
            return true;
        }catch (Exception e){
            log.error(e.getMessage(), e);
            return false;
        }
    }
}
