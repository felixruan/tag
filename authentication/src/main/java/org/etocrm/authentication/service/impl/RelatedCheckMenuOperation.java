package org.etocrm.authentication.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.etocrm.authentication.entity.DO.SysMenuDO;
import org.etocrm.authentication.mapper.ISysMenuMapper;
import org.etocrm.authentication.service.ISysRolePermissionService;
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
public class RelatedCheckMenuOperation implements MenuOperation<Boolean> {

    /**
     * 关联检查结果
     */
    private Boolean relateCheckResult = false;

    @Autowired
    private ISysMenuMapper iSysMenuMapper;

    @Autowired
    private ISysRolePermissionService iSysRolePermissionService;

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

        if(relateCheck(sysMenu)){
            this.relateCheckResult = true;
        }

        return this.relateCheckResult;
    }

    private Boolean relateCheck(SysMenu sysMenu) throws  Exception{

        Integer roleRelatedCount = iSysRolePermissionService.getCountByMenuId(sysMenu.getId());
        if(roleRelatedCount != null && roleRelatedCount > 0){
            return true;
        }

        return false;
    }

    public Boolean getRelateCheckResult(){
        return relateCheckResult;
    }
}
