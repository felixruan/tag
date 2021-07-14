package org.etocrm.authentication.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.apache.commons.lang3.StringUtils;
import org.etocrm.authentication.entity.DO.SysMenuDO;
import org.etocrm.authentication.entity.DO.SysPermissionDO;
import org.etocrm.authentication.entity.DO.SysRolePermissionDO;
import org.etocrm.authentication.entity.VO.auth.*;
import org.etocrm.authentication.mapper.ISysMenuMapper;
import org.etocrm.authentication.mapper.ISysPermissionMapper;
import org.etocrm.authentication.mapper.ISysRolePermissionMapper;
import org.etocrm.authentication.service.ISysMenuService;
import org.etocrm.authentication.service.ISysPermissionService;
import org.etocrm.authentication.util.CacheManager;
import org.etocrm.core.enums.BusinessEnum;
import org.etocrm.core.enums.ResponseEnum;
import org.etocrm.core.util.ApplicationContextUtil;
import org.etocrm.core.util.BeanUtils;
import org.etocrm.core.util.ResponseVO;
import org.etocrm.dynamicDataSource.service.IDynamicService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * @Author peter.li
 * @date 2020/8/17 14:20
 */
@Service
public class SysMenuServiceImpl implements ISysMenuService {

    private static final Logger log = LoggerFactory.getLogger(AuthServiceImpl.class);

    @Autowired
    private ISysMenuMapper iSysMenuMapper;

    @Autowired
    IDynamicService dynamicService;

    @Autowired
    private ISysPermissionMapper iSysPermissionMapper;

    @Autowired
    private ISysRolePermissionMapper iSysRolePermissionMapper;

    @Autowired
    private SysMenuCacheManager sysMenuCacheManager;

    @Autowired
    private ApplicationContextUtil context;


    /**
     * 新增一个菜单
     *
     * @param menuVO
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseVO saveSysMenu(SysMenuAddVO menuVO) {
        SysMenuDO menuDO = new SysMenuDO();
        try {
            //todo
            if (menuVO.getMenuParentId() != 0) {
                if (null == menuVO.getMenuRoute() && menuVO.getMenuRoute().equals("")) {
                    return ResponseVO.error(ResponseEnum.DATA_SOURCE_SONMENU_ERROR);
                }
            }
            if(menuVO.getMenuRoute().length()>256){
                return ResponseVO.error(4001,"页面地址长度超限");
            }
            QueryWrapper<SysMenuDO> query = new QueryWrapper<>();
            query.eq("menu_name", menuVO.getMenuName().trim());
            query.eq("is_delete", BusinessEnum.NOTDELETED.getCode());
            SysMenuDO sysMenuDO1 = iSysMenuMapper.selectOne(query);
            if (sysMenuDO1 != null) {
                return ResponseVO.error(4001, "菜单名'" + menuVO.getMenuName().trim() + "'重复，无法重复添加");
            }
            BeanUtils.copyPropertiesIgnoreNull(menuVO, menuDO);
            menuDO.setMenuName(menuVO.getMenuName().trim());
            if ("".equals(menuVO.getIcon())) {
                menuDO.setIcon("folder-o");//空值给个默认值
            }
            iSysMenuMapper.insert(menuDO);
            CacheManager.getInstance().removeCacheMap("allMenusTree");
            //给超级管理员角色赋权
            SysRolePermissionDO sysRolePermissionDO = new SysRolePermissionDO();
            sysRolePermissionDO.setMenuId(menuDO.getId());
            sysRolePermissionDO.setRoleId(1);
            iSysRolePermissionMapper.insert(sysRolePermissionDO);
            //根据menuId删除菜单树缓存
            removeMneuTreeCacheByMenuId(menuDO.getId());
            return ResponseVO.success();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ResponseVO.error(ResponseEnum.DATA_SOURCE_MENU_ADD_ERROR);
        }
    }

    /**
     * 根据id获取菜单
     *
     * @param id
     * @return
     */
    @Override
    public ResponseVO getSysMenuById(Integer id) {
        try {
            SysMenuDO menuDO = iSysMenuMapper.selectById(id);
            SysMenuOutVO menuVO = new SysMenuOutVO();
            BeanUtils.copyPropertiesIgnoreNull(menuDO, menuVO);
            return ResponseVO.success(menuVO);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ResponseVO.error(ResponseEnum.DATA_SOURCE_MENU_GET_ERROR);
        }

    }

    /**
     * 根据id删除菜单
     *
     * @param id
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseVO removeSysMenu(Integer id) {
        try {
            SysMenuDO sysMenuDO = iSysMenuMapper.selectById(id);
            SysMenu sysMenu = new SysMenu();
            BeanUtils.copyPropertiesIgnoreNull(sysMenuDO, sysMenu);

            /*//递归检查是否有关联关系
            RelatedCheckMenuOperation relatedCheckMenuOperation = context
                    .getBean(RelatedCheckMenuOperation.class);
            Boolean relateCheckResult = sysMenu.execute(relatedCheckMenuOperation);

            //返回true代表有关联关系，不能删除
            if (relateCheckResult) {
                return ResponseVO.error(4001, "菜单'" + sysMenuDO.getMenuName() + "'存在子菜单，禁止删除");
            }*/

            //根据menuId删除菜单树缓存  先删除缓存，再删除数据，因为删除缓存依赖删除之前的查询
            removeMneuTreeCacheByMenuId(id);

            //没有关联关系执行递归删除
            RemoveMenuOperation removeMenuOperation = context
                    .getBean(RemoveMenuOperation.class);
            sysMenu.execute(removeMenuOperation);


            CacheManager.getInstance().removeCacheMap("allMenusTree");
            //      CacheManager.getInstance().removeCacheMap("allMenusTree");

            return ResponseVO.success();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ResponseVO.error(ResponseEnum.DATA_SOURCE_CONNECTION_ERROR);
        }


    }

    /**
     * 更新菜单
     *
     * @param menuVO
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseVO updateSysMenu(SysMenuUpdateVO menuVO) {
        try {
            if(menuVO.getMenuRoute().length()>256){
                return ResponseVO.error(4001,"页面地址长度超限");
            }
            SysMenuDO menuDO = new SysMenuDO();

            QueryWrapper<SysMenuDO> query = new QueryWrapper();
            query.eq("menu_name", menuVO.getMenuName().trim());
            query.eq("is_delete", BusinessEnum.NOTDELETED.getCode());
            List<SysMenuDO> sysMenuDOS = iSysMenuMapper.selectList(query);
            if (sysMenuDOS != null && sysMenuDOS.size() > 0) {
                if (!String.valueOf(sysMenuDOS.get(0).getId()).equals(String.valueOf(menuVO.getId()))) {
                    return ResponseVO.error(4001, "更新菜单名'" + sysMenuDOS.get(0).getMenuName() + "'已存在，请重写");
                }
            }

            BeanUtils.copyPropertiesIgnoreNull(menuVO, menuDO);
            menuDO.setMenuName(menuVO.getMenuName().trim());
            iSysMenuMapper.updateById(menuDO);

            //根据menuId删除菜单树缓存
            removeMneuTreeCacheByMenuId(menuVO.getId());
            CacheManager.getInstance().removeCacheMap("allMenusTree");

            return ResponseVO.success();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ResponseVO.error(ResponseEnum.DATA_SOURCE_UPDATE_ERROR);
        }
    }

    /**
     * 查询根菜单
     *
     * @return
     */
    @Override
    public ResponseVO getSysMenuListByParentId(Integer id) {
        try {
            QueryWrapper<SysMenuDO> query = new QueryWrapper<>();
            query.eq("menu_parent_id", id);
            //       query.eq("menu_status", BusinessEnum.USING.getCode());
            query.eq("is_delete", BusinessEnum.NOTDELETED.getCode());
            List<SysMenuDO> menuDOs = iSysMenuMapper.selectList(query);
            if (menuDOs.size() == 0) {
                return ResponseVO.success();
            }
            List<SysMenuOutVO> menuVOs = new ArrayList<SysMenuOutVO>(menuDOs.size());
            for (SysMenuDO menuDO : menuDOs) {
                SysMenuOutVO menuVO = new SysMenuOutVO();
                BeanUtils.copyPropertiesIgnoreNull(menuDO, menuVO);
                menuVOs.add(menuVO);
            }
            Collections.sort(menuVOs);
            return ResponseVO.success(menuVOs);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ResponseVO.error(ResponseEnum.DATA_SOURCE_GET_ERROR);
        }
    }

    /**
     * 根据根菜单id查询子菜单
     *
     * @param parentId
     * @return
     */
    @Override
    public ResponseVO getChildSysMenuList(Integer parentId) {
        try {
            LambdaQueryWrapper<SysMenuDO> query = new LambdaQueryWrapper<>();
            query.like(SysMenuDO::getMenuStatus, BusinessEnum.USING.getCode())
                    .eq(SysMenuDO::getDeleted, BusinessEnum.NOTDELETED.getCode())
                    .orderByDesc(SysMenuDO::getMenuOrder);
            List<SysMenuDO> menuDOs = iSysMenuMapper.selectList(query);
            List<SysMenuOutVO> menuVOs = new LinkedList<SysMenuOutVO>();
            if (menuDOs.size() == 0) {
                return ResponseVO.success(menuVOs);
            }
            for (SysMenuDO menuDO : menuDOs) {
                SysMenuOutVO menuVO = new SysMenuOutVO();
                BeanUtils.copyPropertiesIgnoreNull(menuDO, menuVO);
                menuVOs.add(menuVO);
            }
            return ResponseVO.success(menuVOs);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ResponseVO.error(ResponseEnum.DATA_SOURCE_GET_ERROR);
        }
    }


    /**
     * 菜单功能启用/停用,1表示已启用,0表示已停用
     *
     * @param sysMenuChangeStatusVO
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ResponseVO changeStatus(SysMenuChangeStatusVO sysMenuChangeStatusVO) {
        try {
            SysMenuDO sysMenuDO = new SysMenuDO();
            sysMenuDO.setId(sysMenuChangeStatusVO.getId());
            sysMenuDO.setMenuStatus(sysMenuChangeStatusVO.getStatus());
            iSysMenuMapper.updateById(sysMenuDO);
            CacheManager.getInstance().removeCacheMap("allMenusTree");
            return ResponseVO.success();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ResponseVO.error(ResponseEnum.DATA_SOURCE_UPDATE_ERROR);
        }
    }

    /**
     * 拖拽子菜单功能（既更换父菜单id）
     *
     * @param sysMenuChangePIdVO
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ResponseVO changeMenuParnetId(SysMenuChangePIdVO sysMenuChangePIdVO) {
        try {
            SysMenuDO sysMenuDO = new SysMenuDO();
            sysMenuDO.setId(sysMenuChangePIdVO.getId());
            sysMenuDO.setMenuParentId(sysMenuChangePIdVO.getNewParentId());
            iSysMenuMapper.updateById(sysMenuDO);

            //根据menuId删除菜单树缓存
            removeMneuTreeCacheByMenuId(sysMenuChangePIdVO.getId());
            CacheManager.getInstance().removeCacheMap("allMenusTree");

            return ResponseVO.success();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ResponseVO.error(ResponseEnum.DATA_SOURCE_UPDATE_ERROR);
        }
    }

    /**
     * 根据用户id查询用户被授权的菜单树
     *
     * @param userId
     * @return
     */
    @Override
    public JSONArray getAuthorizedTree(Integer userId) {
        JSONArray menuTreeArr = new JSONArray();
        try {
            //从缓存中获取用户的权限菜单树
            String authorizedTreeJson = sysMenuCacheManager.getAuthorizedMenuTree(userId);
            if (StringUtils.isNotBlank(authorizedTreeJson) && !authorizedTreeJson.equals("[]")) {
                return JSONArray.parseArray(authorizedTreeJson);
            }
            //查询权限并组织成权限树
            QueryAuthorizedMenuByUserIdOperation operation = context.getBean(QueryAuthorizedMenuByUserIdOperation.class);
            operation.setUserId(userId);
            List<SysMenu> authorizedTree = new ArrayList<>();
            List<SysMenu> rootMenuDOs = getRootMenusByUserId(userId);
            if (CollectionUtil.isEmpty(rootMenuDOs)) {
                ResponseVO.success();
            }
            for (SysMenu rootDO : rootMenuDOs) {
          //      SysMenu targetRoot = new SysMenu();
          //      BeanUtils.copyPropertiesIgnoreNull(rootDO, targetRoot);
                rootDO.execute(operation);
                authorizedTree.add(rootDO);
            }
            Collections.sort(authorizedTree);
            authorizedTreeJson = JSON.toJSONString(authorizedTree);
            //缓存用户权限菜单树
            sysMenuCacheManager.cacheAuthorizedMenuTree(userId, authorizedTreeJson);
            menuTreeArr = JSONArray.parseArray(authorizedTreeJson);
            return menuTreeArr;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return menuTreeArr;
        }
    }


    /**
     * 根据角色id查询菜单
     *
     * @param roleId
     * @return
     */
    public ResponseVO getMenusByRoleId(Integer roleId) {
        try {
            List<Integer> menuIds = getMneuIdsByRoleId(roleId);

            List<SysMenu> authorizedTree = (List<SysMenu>) CacheManager.getInstance().getCacheMap("allMenusTree");
            if (authorizedTree == null) {
                authorizedTree = getAllMneuTree();
            }

            //缓存一下角色权限菜单id
            CacheManager.getInstance().setCacheMap("menuIdsShow_" + roleId, menuIds);

            //查询权限并组织成权限树
            QueryAuthorizedMenuByRoleIdOperation operation = context
                    .getBean(QueryAuthorizedMenuByRoleIdOperation.class);
            operation.setRoleId(roleId);

            for (SysMenu root : authorizedTree) {
                root.execute(operation);

                if (CollectionUtil.isEmpty(menuIds)) {
                    root.setIsSelect(0);
                } else {
                    if (menuIds.contains(root.getId())) {
                        root.setIsSelect(1);
                    } else {
                        root.setIsSelect(0);
                    }
                }
            }

            Collections.sort(authorizedTree);

            CacheManager.getInstance().setCacheMap("allMenusTree", authorizedTree);

            JSONArray authorizedTreeCache = JSONArray.parseArray(JSON.toJSONString(authorizedTree));

            CacheManager.getInstance().removeCacheMap("menuIdsShow_" + roleId);

            return ResponseVO.success(authorizedTreeCache);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ResponseVO.error(ResponseEnum.DATA_SOURCE_GET_ERROR);
        }
    }

    private void circleSetSign(SysMenu sysMenu, List<Integer> menuIds) throws Exception {
        if (sysMenu.getChildren() != null && sysMenu.getChildren().size() > 0) {
            for (SysMenu children : sysMenu.getChildren()) {
                if (menuIds.size() > 0) {
                    if (menuIds.contains(children.getId())) {
                        children.setIsSelect(1);
                    } else {
                        children.setIsSelect(0);
                    }
                } else {
                    children.setIsSelect(0);
                }
            }
        }
    }

    public List<SysMenu> getAllMneuTree() {
        Set<SysMenu> sysMenuSets = new HashSet<SysMenu>();
        try {
            QueryWrapper<SysMenuDO> query = new QueryWrapper();
            query.eq("menu_parent_id",0);
            query.eq("is_delete",0);

            List<SysMenuDO> sysMenuDOS = iSysMenuMapper.selectList(query);

            if (sysMenuDOS != null && sysMenuDOS.size() > 0) {
                for(SysMenuDO sysMenuDO : sysMenuDOS){
                    SysMenu sysMenu = new SysMenu();
                    BeanUtils.copyPropertiesIgnoreNull(sysMenuDO,sysMenu);
                    sysMenuSets.add(sysMenu);
                }
            }
        }catch (Exception e){
            log.error(e.getMessage(), e);
        }
        return new ArrayList<SysMenu>(sysMenuSets);
    }

    public List<String> getMenusByUserId(Integer userId) {
        Set<String> menuIdSets = new HashSet<String>();
        try {
            /*List<SysPermissionDO> sysPermissionDOs = iSysPermissionService
                    .getSysPermissionsByUserId(userId);

            if (sysPermissionDOs != null && sysPermissionDOs.size() > 0) {
                for (SysPermissionDO sysPermissionDO : sysPermissionDOs) {
                    QueryWrapper<SysRolePermissionDO> query = new QueryWrapper<>();
                    query.eq("role_id", sysPermissionDO.getRoleId());
                    query.eq("is_delete", BusinessEnum.NOTDELETED.getCode());
                    List<SysRolePermissionDO> sysRolePermissionDOs = iSysRolePermissionMapper.selectList(query);
                    if (sysRolePermissionDOs != null && sysRolePermissionDOs.size() > 0) {
                        for (SysRolePermissionDO sysRolePermissionDO : sysRolePermissionDOs) {
                            QueryWrapper<SysMenuDO> query1 = new QueryWrapper<>();
                            query1.eq("id", sysRolePermissionDO.getMenuId());
                            //         query1.eq("menu_parent_id", 0);
                            query1.eq("is_button", 0);
                            query1.eq("menu_status", BusinessEnum.USING.getCode());//表示可见
                            query1.eq("is_delete", BusinessEnum.NOTDELETED.getCode());
                            SysMenuDO sysMenuDO = iSysMenuMapper.selectOne(query1);
                            if (sysMenuDO != null) {
                                menuIdSets.add(String.valueOf(sysMenuDO.getId()));
                            }
                        }
                    }
                }
            }*/

            List<String> tableNames = new ArrayList<String>();
            tableNames.add("sys_permission sp");
            tableNames.add("sys_role_permission rp");
            tableNames.add("sys_menu sm");

            List<String> columns = new ArrayList<>();
            columns.add("sm.id id");
          //  columns.add("sm.button_name buttonName");

            String whereClause = "";
            whereClause = "sp.role_id=rp.role_id and rp.menu_id = sm.id and sp.user_id = " + userId
                    +" and rp.is_delete=0 and sp.is_delete=0 "
                    +" and sm.is_button=0 and sm.menu_status=1 and sm.is_delete=0 ";
            List<TreeMap> treeMaps = dynamicService.selectList(tableNames, columns, whereClause,null);
            for(TreeMap map : treeMaps){
                menuIdSets.add(String.valueOf(map.get("id")));
            }

            if (menuIdSets.size() > 0) {
                sysMenuCacheManager.cacheMenuIds(userId, new ArrayList<String>(menuIdSets));
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return new ArrayList<String>(menuIdSets);

    }

    private List<Integer> getMneuIdsByRoleId(Integer roleId) throws Exception {
        QueryWrapper<SysRolePermissionDO> query = new QueryWrapper<>();
        query.eq("role_id", roleId);
        query.eq("is_delete", BusinessEnum.NOTDELETED.getCode());
        List<SysRolePermissionDO> sysRolePermissionDOs = iSysRolePermissionMapper.selectList(query);
        List<Integer> menuIds = new ArrayList<>();
        if (sysRolePermissionDOs != null && sysRolePermissionDOs.size() > 0) {
            for (SysRolePermissionDO sysRolePermissionDO : sysRolePermissionDOs) {
                menuIds.add(sysRolePermissionDO.getMenuId());
            }
        }
        return menuIds;
    }


    /**
     * 获取用户所有的按钮权限
     *
     * @param userId
     * @return
     */
    public List<String> getUrlPermission(Integer userId) {
        List<String> buttonPerms = new ArrayList<>();
        try {
            /*List<SysPermissionDO> sysPermissionDOs = iSysPermissionService
                    .getSysPermissionsByUserId(userId);

            if (sysPermissionDOs != null && sysPermissionDOs.size() > 0) {
                for (SysPermissionDO sysPermissionDO : sysPermissionDOs) {
                    QueryWrapper<SysRolePermissionDO> query = new QueryWrapper<>();
                    query.eq("role_id", sysPermissionDO.getRoleId());
                    query.eq("is_delete", BusinessEnum.NOTDELETED.getCode());
                    List<SysRolePermissionDO> sysRolePermissionDOs = iSysRolePermissionMapper.selectList(query);
                    if (sysRolePermissionDOs != null && sysRolePermissionDOs.size() > 0) {
                        for (SysRolePermissionDO sysRolePermissionDO : sysRolePermissionDOs) {
                            QueryWrapper<SysMenuDO> query1 = new QueryWrapper<>();
                            query1.eq("id", sysRolePermissionDO.getMenuId());
                            query1.eq("is_button", 1);
                            query1.eq("menu_status", BusinessEnum.USING.getCode());//表示可见
                            query1.eq("is_delete", BusinessEnum.NOTDELETED.getCode());
                            SysMenuDO sysMenuDO = iSysMenuMapper.selectOne(query1);
                            if (sysMenuDO != null) {
                                buttonPerms.add(sysMenuDO.getButtonName());
                            }
                        }
                    }
                }
            }*/
            List<String> tableNames = new ArrayList<String>();
            tableNames.add("sys_permission sp");
            tableNames.add("sys_role_permission rp");
            tableNames.add("sys_menu sm");

            List<String> columns = new ArrayList<>();
            columns.add("sm.id id");
            columns.add("sm.button_name buttonName");

            String whereClause = "";
            whereClause = "sp.role_id=rp.role_id and rp.menu_id = sm.id and sp.user_id = " + userId
                    +" and rp.is_delete=0 and sp.is_delete=0 "
                    +" and sm.is_button=1 and sm.menu_status=1 and sm.is_delete=0 ";
            List<TreeMap> treeMaps = dynamicService.selectList(tableNames, columns, whereClause,null);

            if (treeMaps != null && treeMaps.size() > 0) {
                for(TreeMap map : treeMaps){
                    buttonPerms.add(String.valueOf(map.get("buttonName")));
                }
            }

            /*if(buttonPerms.size() > 0){
                sysMenuCacheManager.cacheButtonPerms(userId,buttonPerms);
            }*/
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
        return buttonPerms;
    }

    /**
     * 获取用户所有的菜单权限(第一版权限只到菜单)
     * @param userId
     * @return
     *//*
    public List<String> getUrlPermission(Integer userId){
        List<String> menuPerms = new ArrayList<>();
        try {
            List<SysPermissionDO> sysPermissionDOs = iSysPermissionService
                    .getSysPermissionsByUserId(userId);
            if(sysPermissionDOs.size() > 0){
                for(SysPermissionDO sysPermissionDO : sysPermissionDOs){
                    QueryWrapper<SysRolePermissionDO> query = new QueryWrapper<>();
                    query.eq("role_id",sysPermissionDO.getRoleId());
                    List<SysRolePermissionDO> sysRolePermissionDOs = iSysRolePermissionMapper.selectList(query);
                    if(sysRolePermissionDOs.size() > 0){
                        for(SysRolePermissionDO sysRolePermissionDO : sysRolePermissionDOs){
                            QueryWrapper<SysMenuDO> query1 = new QueryWrapper<>();
                            query1.eq("id",sysRolePermissionDO.getMenuId());
                            query1.eq("menu_status", BusinessEnum.USING.getCode());
                            query1.eq("is_delete", BusinessEnum.NOTDELETED.getCode());
                            SysMenuDO sysMenuDO = iSysMenuMapper.selectOne(query1);
                            if(sysMenuDO != null){
                                if(sysMenuDO.getMenuParentId() == null){
                                    menuPerms.add(sysMenuDO.getMenuName());
                                }
                            }
                        }
                    }
                }
            }
        }catch (Exception e){
            log.error(e.getMessage(), e);
            return null;
        }
        return menuPerms;
    }*/

    /**
     * 根据用户id获取根菜单
     *
     * @param userId
     * @return
     */
    private List<SysMenu> getRootMenusByUserId(Integer userId) throws Exception {
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
        whereClause = "sp.role_id=rp.role_id and rp.menu_id = sm.id and sp.user_id = " + userId
                      +" and rp.is_delete=0 and sp.is_delete=0 "
                      +" and sm.menu_parent_id=0 and sm.menu_status=1 and sm.is_delete=0 ";
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

    private List<SysMenu> getRootMenusByRoleId(Integer roleId) throws Exception {

        Set<SysMenu> sysMenuSets = new HashSet<SysMenu>();

        List<String> tableNames = new ArrayList<String>();
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
        whereClause = " rp.menu_id = sm.id and rp.role_id = " + roleId
                +" and rp.is_delete=0 "
                +" and sm.menu_parent_id = 0 and sm.menu_status=1 and sm.is_delete=0 ";
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


    private void removeMneuTreeCacheByMenuId(Integer menuId) throws Exception {
        QueryWrapper<SysRolePermissionDO> query = new QueryWrapper<>();
        query.eq("menu_id", menuId);
        query.eq("is_delete", BusinessEnum.NOTDELETED.getCode());
        List<SysRolePermissionDO> sysRolePermissionDOs = iSysRolePermissionMapper.selectList(query);
        if (sysRolePermissionDOs != null && sysRolePermissionDOs.size() > 0) {
            for (SysRolePermissionDO sysRolePermissionDO : sysRolePermissionDOs) {
                QueryWrapper<SysPermissionDO> query1 = new QueryWrapper<>();
                query1.eq("role_id", sysRolePermissionDO.getRoleId());
                query1.eq("is_delete", BusinessEnum.NOTDELETED.getCode());
                List<SysPermissionDO> sysPermissionDOs = iSysPermissionMapper.selectList(query1);
                if (sysPermissionDOs != null && sysPermissionDOs.size() > 0) {
                    for (SysPermissionDO sysPermissionDO : sysPermissionDOs) {
                        sysMenuCacheManager.removeAuthorizedMenuTree(sysPermissionDO.getUserId());
                    }
                }
                CacheManager.getInstance().removeCacheMap("allMenusTree_" + sysRolePermissionDO.getRoleId());
            }
        }
    }


    /**
     * 根据菜单按钮名称获取父菜单id，并判断用户是否有操作父菜单的权限
     *
     * @param buttonName
     * @return
     */
    public Boolean hasParentMenuAuth(Integer userId, String buttonName) {
        try {
            //获取非按钮菜单ids缓存
            List<String> menuIds = sysMenuCacheManager.getMenuIds(userId);
            if (menuIds == null || menuIds.size() == 0) {
                menuIds = this.getMenusByUserId(userId);
            }
            //     List<String> menuIds = this.getMenusByUserId(userId);

            QueryWrapper query = new QueryWrapper();
            query.eq("button_name", buttonName);
            query.eq("is_delete", BusinessEnum.NOTDELETED.getCode());
            query.eq("menu_status", BusinessEnum.USING.getCode());
            List<SysMenuDO> list = iSysMenuMapper.selectList(query);
            if (list != null && list.size() > 0) {
                Boolean contains;
                for (SysMenuDO sysMenuDO : list) {
                    contains = menuIds.contains(String.valueOf(sysMenuDO.getMenuParentId()));
                    if (contains) {
                        return menuIds.contains(String.valueOf(sysMenuDO.getMenuParentId()));
                    }
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return false;
    }


    /*public  ResponseVO autoCreateButtonMenu(){
        try {
            Resource resource = new ClassPathResource("apipath.json");
            InputStream inputStream = resource.getInputStream();
            StringBuilder out = new StringBuilder();
            byte[] b = new byte[4096];
            // 读取流
            for (int n; (n = inputStream.read(b)) != -1; ) {
                out.append(new String(b, 0, n));
            }
      //      System.out.println(out.toString());
            JSONObject obj = JSON.parseObject(out.toString());

            AutoCreateMenuDataVO autoCreateMenuVO = JSONObject.toJavaObject(obj,AutoCreateMenuDataVO.class);
            JSONObject obj1 =JSON.parseObject(autoCreateMenuVO.getPaths());
            Set<String> pathSet = obj1.keySet();
            for(String path : pathSet){
                if(path.contains("{")){
                    int i = path.indexOf("{");
                    path = path.substring(0,i);
                }
                //{
                //	"icon": "",
                //	"menuMemo": "",
                //	"menuName": "",
                //	"menuOrder": 0,
                //	"menuParentId": 0,
                //	"menuRoute": "",
                //	"menuStatus": 0
                //}
                SysMenuDO sysMenuDO = new SysMenuDO();
                sysMenuDO.setIcon("icon图标");
                sysMenuDO.setMenuMemo("这是按钮");
                sysMenuDO.setMenuName(path);
                sysMenuDO.setMenuOrder(1);
                sysMenuDO.setMenuParentId(1);
                sysMenuDO.setMenuRoute("/data");
                sysMenuDO.setMenuStatus(1);
                sysMenuDO.setIsButton(1);
                sysMenuDO.setButtonName("/data"+path.trim());
                iSysMenuMapper.insert(sysMenuDO);
            }
        }catch (Exception e){
            log.error(e.getMessage(), e);
            return ResponseVO.error(4001,"报错");
        }
        return ResponseVO.success();
    }*/


}
