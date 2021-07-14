package org.etocrm.authentication.service.impl;

import org.etocrm.dynamicDataSource.service.IDynamicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Create By peter.li
 */
@Component
@Scope("prototype")
public class QueryAuthorizedMenuByUserIdOperation implements MenuOperation<Boolean> {

    private Integer userId;

    @Autowired
    IDynamicService dynamicService;

    @Override
    public Boolean doExecute(SysMenu sysMenu) throws Exception {
        List<SysMenu> targetChildren = new ArrayList<SysMenu>();

        List<SysMenu> rootMenus = getChildMenus(userId,sysMenu.getId());

        for(SysMenu rootDO : rootMenus){
            rootDO.execute(this);
            targetChildren.add(rootDO);
        }
        Collections.sort(targetChildren);
        sysMenu.setChildren(targetChildren);
        return true;
    }

    private List<SysMenu> getChildMenus(Integer userId,Integer parentId){
        Set<SysMenu> sysMenuSets = new HashSet<SysMenu>();

        List<String> tableNames = new ArrayList<String>();
        tableNames.add("sys_permission sp");
        tableNames.add("sys_role_permission rp");
        tableNames.add("sys_menu sm");

        List<String> columns = new ArrayList<>();
        columns.add("sm.id id");
        columns.add("sm.menu_name menuName");
        columns.add("sm.menu_parent_id menuParentId");
        columns.add("sm.menu_route menuRoute");
        columns.add("sm.menu_status menuStatus");
        columns.add("sm.menu_order menuOrder");
        columns.add("sm.icon icon");

        String whereClause = "";
        whereClause = " sp.role_id=rp.role_id and rp.menu_id = sm.id and sp.user_id = " + userId
                +" and rp.is_delete=0 and sp.is_delete=0 "
                +" and sm.menu_parent_id="+parentId+" and sm.menu_status=1 and sm.is_delete=0 ";
        List<TreeMap> treeMaps = dynamicService.selectList(tableNames, columns, whereClause,null);

        if (treeMaps != null && treeMaps.size() > 0) {
            SysMenu  sysMenu;
            for(TreeMap map : treeMaps){
                sysMenu = new SysMenu();
                sysMenu.setId(Integer.valueOf(map.get("id").toString()));
                sysMenu.setMenuName(String.valueOf(map.get("menuName")));
                sysMenu.setMenuParentId(Integer.valueOf(map.get("menuParentId").toString()));
                sysMenu.setMenuRoute(String.valueOf(map.get("menuRoute")));
                sysMenu.setMenuStatus(Integer.valueOf(map.get("menuStatus").toString()));
                sysMenu.setMenuOrder(Integer.valueOf(map.get("menuOrder").toString()));
                sysMenu.setIcon(String.valueOf(map.get("icon")));

                sysMenuSets.add(sysMenu);
            }
        }
        return new ArrayList<SysMenu>(sysMenuSets);
    }


    public Integer getUserId(){
        return userId;
    }

    public void setUserId(Integer userId){
        this.userId = userId;
    }
}
