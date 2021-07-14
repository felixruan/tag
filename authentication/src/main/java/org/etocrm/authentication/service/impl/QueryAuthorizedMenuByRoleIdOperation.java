package org.etocrm.authentication.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.etocrm.authentication.entity.DO.SysMenuDO;
import org.etocrm.authentication.entity.DO.SysPermissionDO;
import org.etocrm.authentication.entity.DO.SysRolePermissionDO;
import org.etocrm.authentication.mapper.ISysMenuMapper;
import org.etocrm.authentication.mapper.ISysRolePermissionMapper;
import org.etocrm.authentication.service.ISysPermissionService;
import org.etocrm.authentication.util.CacheManager;
import org.etocrm.core.enums.BusinessEnum;
import org.etocrm.core.util.BeanUtils;
import org.etocrm.dynamicDataSource.service.IDynamicService;
import org.etocrm.dynamicDataSource.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Create By peter.li
 */
@Component
@Scope("prototype")
public class QueryAuthorizedMenuByRoleIdOperation implements MenuOperation<Boolean> {

    private Integer roleId;

    @Autowired
    IDynamicService dynamicService;

    @Autowired
    private ISysMenuMapper iSysMenuMapper;

    @Override
    public Boolean doExecute(SysMenu sysMenu) throws Exception {

        List<Integer> menuIds = (List<Integer>)CacheManager.getInstance().getCacheMap("menuIdsShow_"+roleId);
        List<SysMenu> targetChildren = new ArrayList<SysMenu>();

        List<SysMenu> rootMenus = getChildMenus(sysMenu.getId());

        for(SysMenu rootDO : rootMenus){
            rootDO.execute(this);
            if(menuIds == null){
                rootDO.setIsSelect(0);
            }else{
                if(menuIds.contains(rootDO.getId())){
                    rootDO.setIsSelect(1);
                }else{
                    rootDO.setIsSelect(0);
                }
            }
            targetChildren.add(rootDO);
        }
        Collections.sort(targetChildren);
        sysMenu.setChildren(targetChildren);
        return true;
    }

    private List<SysMenu> getChildMenus(Integer parentId){

        Set<SysMenu> sysMenuSets = new HashSet<SysMenu>();

        QueryWrapper<SysMenuDO> query = new QueryWrapper<>();
        query.eq("menu_parent_id",parentId);
        query.eq("is_delete",0);
        query.eq("is_button",0);
        List<SysMenuDO> sysMenuDOS = iSysMenuMapper.selectList(query);

        if (sysMenuDOS != null && sysMenuDOS.size() > 0) {
            for(SysMenuDO sysMenuDO : sysMenuDOS){
                SysMenu sysMenu = new SysMenu();
                BeanUtils.copyPropertiesIgnoreNull(sysMenuDO,sysMenu);
                sysMenuSets.add(sysMenu);
            }
        }
        return new ArrayList<SysMenu>(sysMenuSets);
    }

    public Integer getRoleId(){
        return roleId;
    }

    public void setRoleId(Integer roleId){
        this.roleId = roleId;
    }
}
