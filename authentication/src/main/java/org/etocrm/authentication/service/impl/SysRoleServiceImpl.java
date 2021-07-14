package org.etocrm.authentication.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.etocrm.authentication.entity.DO.SysPermissionDO;
import org.etocrm.authentication.entity.DO.SysRoleDO;
import org.etocrm.authentication.entity.DO.SysRolePermissionDO;
import org.etocrm.authentication.entity.DO.SysUserDO;
import org.etocrm.authentication.entity.VO.auth.*;
import org.etocrm.authentication.mapper.ISysRoleMapper;
import org.etocrm.authentication.mapper.ISysUserMapper;
import org.etocrm.authentication.service.ISysMenuService;
import org.etocrm.authentication.service.ISysPermissionService;
import org.etocrm.authentication.service.ISysRolePermissionService;
import org.etocrm.authentication.service.ISysRoleService;
import org.etocrm.authentication.util.CacheManager;
import org.etocrm.core.enums.BusinessEnum;
import org.etocrm.core.enums.ResponseEnum;
import org.etocrm.core.util.*;
import org.etocrm.dynamicDataSource.util.BasePage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @Author peter.li
 * @date 2020/8/17 14:20
 */
@Service
public class SysRoleServiceImpl implements ISysRoleService {

    private static final Logger log = LoggerFactory.getLogger(AuthServiceImpl.class);

    @Autowired
    private ISysRoleMapper iSysRoleMapper;

    @Autowired
    private ISysRolePermissionService iSysRolePermissionService;

    @Autowired
    private ISysPermissionService iSysPermissionService;

    @Autowired
    private SysMenuCacheManager sysMenuCacheManager;

    @Autowired
    private ISysMenuService iSysMenuService;

    @Autowired
    private ISysUserMapper iSysUserMapper;


    /**
     * 分页查询角色
     *
     * @return
     */
    @Override
    public ResponseVO getListByPage(SysRoleInPageVO sysRole) {
        SysRoleDO sysRoleDO = new SysRoleDO();
        List<SysRoleOutVO> list = new ArrayList<>();
        try {
            ParamDeal.setStringNullValue(sysRole);

            IPage<SysRoleDO> iPage = new Page<>(VoParameterUtils.getCurrent(sysRole.getCurrent()), VoParameterUtils.getSize(sysRole.getSize()));
            BeanUtils.copyPropertiesIgnoreNull(sysRole, sysRoleDO);
            LambdaQueryWrapper<SysRoleDO> lQuery = conditionDecide(sysRoleDO);
            IPage<SysRoleDO> sysRoleDOIPage = iSysRoleMapper.selectPage(iPage, lQuery);
            BasePage page = new BasePage(sysRoleDOIPage);
            SysRoleOutVO sysRoleOutVO;
            for (SysRoleDO sysRoleDO1 : sysRoleDOIPage.getRecords()) {
                sysRoleOutVO = new SysRoleOutVO();
                BeanUtils.copyPropertiesIgnoreNull(sysRoleDO1, sysRoleOutVO);
                SysUserDO userDO1 = new SysUserDO();
                userDO1.setId(sysRoleDO1.getCreatedBy()==null?1:sysRoleDO1.getCreatedBy());
                SysUserDO userDO = iSysUserMapper.selectById(userDO1);

                sysRoleOutVO.setCreatedByName(userDO.getUserName());
                sysRoleOutVO.setCreatedTime(DateUtil.formatDateTimeByFormat(sysRoleDO1.getCreatedTime(),DateUtil.default_datetimeformat));

                list.add(sysRoleOutVO);
            }
            page.setRecords(list);
            return ResponseVO.success(page);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ResponseVO.error(ResponseEnum.DATA_SOURCE_GET_ERROR);
        }
    }

    /**
     * 获取全部角色
     *
     * @return
     */
    @Override
    public ResponseVO findAll() {
        List<SysRoleOutVO> sysRoleOutVOs = new ArrayList<>();
        try {
            SysRoleDO sysRoleDO = new SysRoleDO();
            LambdaQueryWrapper<SysRoleDO> queryWrapper = new LambdaQueryWrapper<>(sysRoleDO)
                    .eq(SysRoleDO::getDeleted, BusinessEnum.NOTDELETED.getCode())
                    .orderByDesc(SysRoleDO::getCreatedTime);
            List<SysRoleDO> sysRoleDOs = iSysRoleMapper.selectList(queryWrapper);

            if(sysRoleDOs!=null&&sysRoleDOs.size() > 0){
                for (SysRoleDO sysRoleDO1 : sysRoleDOs) {
                    SysRoleOutVO sysRoleOutVO = new SysRoleOutVO();
                    BeanUtils.copyPropertiesIgnoreNull(sysRoleDO1, sysRoleOutVO);
                    sysRoleOutVOs.add(sysRoleOutVO);
                }
            }
            return ResponseVO.success(sysRoleOutVOs);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ResponseVO.error(ResponseEnum.DATA_SOURCE_GET_ERROR);
        }
    }

    /**
     * 根据id查询角色
     *
     * @param id
     * @return
     */
    @Override
    public ResponseVO getRoleById(Integer id) {
        try {
            SysRoleDO sysRoleDO = iSysRoleMapper.selectById(id);
            SysRoleOutVO sysRoleOutVO = new SysRoleOutVO();
            BeanUtils.copyPropertiesIgnoreNull(sysRoleDO, sysRoleOutVO);

            return ResponseVO.success(sysRoleOutVO);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ResponseVO.error(ResponseEnum.DATA_SOURCE_GET_ERROR);
        }
    }

    /**
     * 根据用户id查询角色
     *
     * @param userId
     * @return
     */
    @Override
    public ResponseVO getRoleByUserId(Integer userId) {
        try {
            List<SysPermissionDO> sysPermissionDOs = iSysPermissionService
                    .getSysPermissionsByUserId(userId);
            List<SysRoleOutVO> sysRoleOutVOs = new ArrayList<>();
            if (sysPermissionDOs!=null&&sysPermissionDOs.size() > 0) {
                for (SysPermissionDO sysPermissionDO : sysPermissionDOs) {
                    SysRoleDO sysRoleDO = iSysRoleMapper.selectById(sysPermissionDO.getRoleId());
                    SysRoleOutVO sysRoleOutVO = new SysRoleOutVO();
                    BeanUtils.copyPropertiesIgnoreNull(sysRoleDO, sysRoleOutVO);
                    sysRoleOutVOs.add(sysRoleOutVO);
                }
            }
            return ResponseVO.success(sysRoleOutVOs);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ResponseVO.error(ResponseEnum.DATA_SOURCE_GET_ERROR);
        }
    }

    /**
     * 新增角色
     *
     * @param roleVO
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseVO saveRole(SysRoleAddVO roleVO) {
        try {
            if(roleVO.getRoleName()!=null && roleVO.getRoleName().length() > 25){
                return ResponseVO.error(4001,"角色名过长，长度不能大于25");
            }
            SysRoleDO sysRoleDO = new SysRoleDO();

            QueryWrapper<SysRoleDO> query = new QueryWrapper<>();
            query.eq("role_name", roleVO.getRoleName().trim());
            SysRoleDO sysRoleDO1 = iSysRoleMapper.selectOne(query);
            if (sysRoleDO1 != null) {
                return ResponseVO.error(4001,"角色名'"+roleVO.getRoleName()+"'已存在，无法重复添加");
            }
            BeanUtils.copyPropertiesIgnoreNull(roleVO, sysRoleDO);
            sysRoleDO.setRoleName(roleVO.getRoleName().trim());
            iSysRoleMapper.insert(sysRoleDO);

            //保存角色菜单关联关系
            if(roleVO.getMenuIds()!=null&&roleVO.getMenuIds().size() > 0){
                saveUpdateRelations(roleVO.getMenuIds(),sysRoleDO.getId());
            }

            return ResponseVO.success(sysRoleDO.getId());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ResponseVO.error(ResponseEnum.DATA_SOURCE_ADD_ERROR);
        }
    }

    /**
     * 删除角色
     *
     * @param id
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseVO removeSysRole(Integer id) {
        try {

            //检查是否为管理员角色
            /*Boolean isSupperRole = checkRoleAllowed(new SysRoleDO(id));
            if (isSupperRole) {
                return ResponseVO.error(4001,"管理员角色禁止操作");
            }*/
            if(String.valueOf(id).equals("1")){
                return ResponseVO.error(4001,"管理员角色禁止操作");
            }

            Integer count = iSysPermissionService.getCountByRoleId(id);
            if (count > 0) {
                return ResponseVO.error(4001,"角色被使用，无法删除");
            }
            SysRoleDO sysRoleDO = new SysRoleDO();
            sysRoleDO.setId(id);
            sysRoleDO.setDeleted(BusinessEnum.DELETED.getCode());
            iSysRoleMapper.updateById(sysRoleDO);
 //           iSysRoleMapper.deleteById(id);

            iSysRolePermissionService.removeByRoleId(id);
            CacheManager.getInstance().removeCacheMap("allMenusTree_" + id);

            return ResponseVO.success();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ResponseVO.error(ResponseEnum.DATA_SOURCE_UPDATE_ERROR);
        }
    }

    /**
     * 更新角色
     *
     * @param roleVO
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseVO updateSysRole(SysRoleUpdateVO roleVO) {
        try {

            //检查是否为管理员角色
           /* Boolean isSupperUser = checkRoleAllowed(new SysRoleDO(roleVO.getId()));
            if (isSupperUser) {
                return ResponseVO.error(4001,"管理员角色禁止操作");
            }*/
            if(String.valueOf(roleVO.getId()).equals("1")){
                return ResponseVO.error(4001,"管理员角色禁止操作");
            }
            if(roleVO.getRoleName()!=null && roleVO.getRoleName().length() > 25){
                return ResponseVO.error(4001,"角色名过长，长度不能大于25");
            }

            if(roleVO.getRoleName() != null && !roleVO.getRoleName().equals("")){
                QueryWrapper<SysRoleDO> query = new QueryWrapper();
                query.eq("role_name",roleVO.getRoleName().trim());
                query.eq("is_delete", BusinessEnum.NOTDELETED.getCode());
                List<SysRoleDO> sysRoleDOs = iSysRoleMapper.selectList(query);
                if(sysRoleDOs!=null&&sysRoleDOs.size() > 0){
                    if(sysRoleDOs.get(0).getId() != roleVO.getId()){
                        return ResponseVO.error(4001,"更新角色名'"+roleVO.getRoleName()+"'已存在，请重写");
                    }
                }
                SysRoleDO sysRoleDO = new SysRoleDO();
                BeanUtils.copyPropertiesIgnoreNull(roleVO, sysRoleDO);
                sysRoleDO.setRoleName(roleVO.getRoleName().trim());
                iSysRoleMapper.updateById(sysRoleDO);
            }
            updateSysRoleAuth(roleVO);

            return ResponseVO.success();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ResponseVO.error(ResponseEnum.DATA_SOURCE_UPDATE_ERROR);
        }
    }


    /**
     * 根据id更新角色权限
     *
     * @param roleVO
     * @return
     */
    private void updateSysRoleAuth(SysRoleUpdateVO roleVO) {
        try {

            //根据角色id获取全部的角色菜单关联关系
            List<SysRolePermissionDO> listByRoleId = iSysRolePermissionService.getListByRoleId(roleVO.getId());
            List<Integer> menuIdsOld = new ArrayList<>();
            if(listByRoleId!=null&&listByRoleId.size() > 0) {
                for (SysRolePermissionDO sysRolePermissionDO : listByRoleId) {
                    menuIdsOld.add(sysRolePermissionDO.getMenuId());
                }
            }
            List<Integer> menuIdsNew = new ArrayList<>(roleVO.getMenuIds());
            //取差集后，menuIds剩下的元素就是新增的关联关系
            menuIdsNew.removeAll(menuIdsOld);
            if( menuIdsNew.size() > 0){
                //新增角色权限关联关系
                saveUpdateRelations(menuIdsNew,roleVO.getId());
            }
            //取差集后，menuIdsold剩下的元素就是需要删除的关联关系
            menuIdsOld.removeAll(roleVO.getMenuIds());
            if(menuIdsOld.size()>0){
                //删除角色权限关联关系
                iSysRolePermissionService.removeByList(menuIdsOld,roleVO.getId());
            }
            CacheManager.getInstance().removeCacheMap("allMenusTree_"+roleVO.getId());
            //删除角色关联所有的用户的权限缓存
            List<Integer> userIdsByRoleId = iSysPermissionService.getUserIdsByRoleId(roleVO.getId());
            if(userIdsByRoleId!=null && userIdsByRoleId.size()>0){
                for(Integer userId : userIdsByRoleId){
                    sysMenuCacheManager.removeAuthorizedMenuTree(userId);
                }
            }
            /*String cacheMap = CacheManager.getInstance().getCacheMapAll().toString();
            log.info("本地缓存CacheManager全部内容为："+cacheMap);*/

        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }


    /**
     * 保存角色菜单关联关系
     * @param menuIds
     * @param roleId
     * @throws Exception
     */
    private void saveUpdateRelations(List<Integer> menuIds,Integer roleId) throws Exception {
        for (Integer menuId : menuIds) {
            SysRolePermissionDO sysRolePermissionDO = new SysRolePermissionDO();
 //           BeanUtils.copyPropertiesIgnoreNull(roleVO, sysRolePermissionDO);
            sysRolePermissionDO.setRoleId(roleId);
            sysRolePermissionDO.setMenuId(menuId);

            iSysRolePermissionService.save(sysRolePermissionDO);
        }
    }

    private LambdaQueryWrapper<SysRoleDO> conditionDecide(SysRoleDO roleDO) throws Exception {

        LambdaQueryWrapper<SysRoleDO> lQuery = new LambdaQueryWrapper<>();
        if (roleDO.getRoleName() != null) {
            lQuery.like(SysRoleDO::getRoleName, roleDO.getRoleName())
                    .eq(SysRoleDO::getDeleted, BusinessEnum.NOTDELETED.getCode())
                    .orderByDesc(SysRoleDO::getCreatedTime);
        } else {
            lQuery.eq(SysRoleDO::getDeleted, BusinessEnum.NOTDELETED.getCode())
                    .orderByDesc(SysRoleDO::getCreatedTime);
        }

        return lQuery;
    }

    public Boolean checkRoleAllowed(SysRoleDO roleDO) throws Exception {
        if (roleDO.isAdmin()) {
            return true;
        }
        return false;
    }
}
