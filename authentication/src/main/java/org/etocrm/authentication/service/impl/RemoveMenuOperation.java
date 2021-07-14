package org.etocrm.authentication.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import org.etocrm.authentication.entity.DO.SysMenuDO;
import org.etocrm.authentication.entity.DO.SysRolePermissionDO;
import org.etocrm.authentication.mapper.ISysMenuMapper;
import org.etocrm.authentication.mapper.ISysRolePermissionMapper;
import org.etocrm.core.enums.BusinessEnum;
import org.etocrm.core.util.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Create By peter.li
 */
@Component
@Scope("prototype")
public class RemoveMenuOperation implements MenuOperation<Boolean> {

    @Autowired
    private ISysMenuMapper iSysMenuMapper;

    @Autowired
    private ISysRolePermissionMapper iSysRolePermissionMapper;

    @Override
    public Boolean doExecute(SysMenu sysMenu) throws Exception {

        QueryWrapper<SysMenuDO> query = new QueryWrapper<>();
        query.eq("menu_parent_id",sysMenu.getId());
        query.eq("menu_status", BusinessEnum.USING.getCode());
        query.eq("is_delete", BusinessEnum.NOTDELETED.getCode());
        List<SysMenuDO> sysMenuDOs = iSysMenuMapper.selectList(query);

        if(sysMenuDOs != null && sysMenuDOs.size() > 0){
            for(SysMenuDO sysMenuDO : sysMenuDOs){
                SysMenu sysMenuNode = new SysMenu();
                BeanUtils.copyPropertiesIgnoreNull(sysMenuDO,sysMenuNode);
                sysMenuNode.execute(this);
            }
        }

        removeSysMenu(sysMenu);

        return true;
    }

    private void removeSysMenu(SysMenu node) throws  Exception{
        //删除菜单
        SysMenuDO sysMenuDO = new SysMenuDO();
        BeanUtils.copyPropertiesIgnoreNull(node,sysMenuDO);
        sysMenuDO.setDeleted(BusinessEnum.DELETED.getCode());
        iSysMenuMapper.updateById(sysMenuDO);

        //删除角色菜单关联关系
        QueryWrapper<SysRolePermissionDO> query = new QueryWrapper();
        query.eq("menu_id",node.getId());
        query.eq("is_delete",BusinessEnum.NOTDELETED.getCode());
        List<SysRolePermissionDO> list = iSysRolePermissionMapper.selectList(query);
        if(list!=null && list.size()>0){
           for(SysRolePermissionDO sysRolePermissionDO : list){
               sysRolePermissionDO.setDeleted(BusinessEnum.DELETED.getCode());
               UpdateWrapper update = new UpdateWrapper();
               update.eq("menu_id",node.getId());
               update.eq("is_delete",BusinessEnum.NOTDELETED.getCode());
               iSysRolePermissionMapper.update(sysRolePermissionDO,update);
           }
        }

        // iSysMenuMapper.deleteById(node.getId());
    }
}
