package org.etocrm.authentication.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.etocrm.authentication.entity.DO.SysBrandsDO;
import org.etocrm.authentication.entity.DO.SysBrandsOrgDO;
import org.etocrm.authentication.entity.DO.SysPermissionDO;
import org.etocrm.authentication.entity.DO.SysUserDO;
import org.etocrm.authentication.entity.VO.SysUserAllVO;
import org.etocrm.authentication.entity.VO.auth.*;
import org.etocrm.authentication.mapper.ISysBrandsMapper;
import org.etocrm.authentication.mapper.ISysBrandsOrgMapper;
import org.etocrm.authentication.mapper.ISysUserMapper;
import org.etocrm.authentication.service.ISysMenuService;
import org.etocrm.authentication.service.ISysPermissionService;
import org.etocrm.authentication.service.ISysUserService;
import org.etocrm.core.enums.BusinessEnum;
import org.etocrm.core.enums.ResponseEnum;
import org.etocrm.core.util.*;
import org.etocrm.dynamicDataSource.util.BasePage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @Author peter.li
 * @date 2020/8/17 14:20
 */
@Service
@Slf4j
public class SysUserServiceImpl implements ISysUserService {

    @Autowired
    private ISysUserMapper iSysUserMapper;

    @Autowired
    private SysMenuCacheManager sysMenuCacheManager;

    @Autowired
    private ISysPermissionService iSysPermissionService;

    @Autowired
    private ISysMenuService sysMenuService;
    @Autowired
    private ISysBrandsMapper iSysBrandsMapper;
    @Autowired
    private ISysBrandsOrgMapper iSysBrandsOrgMapper;

    /**
     * 分页查询系统用户
     *
     * @return
     */
    @Override
    public ResponseVO getUsersByPage(SysUserInPageVO userVO) {
        SysUserDO sysUserDO = new SysUserDO();
        List<SysUserOutVO> list = new ArrayList<>();
        try {
            ParamDeal.setStringNullValue(userVO);

            IPage<SysUserDO> iPage = new Page<>(VoParameterUtils.getCurrent(userVO.getCurrent()), VoParameterUtils.getSize(userVO.getSize()));
            BeanUtils.copyPropertiesIgnoreNull(userVO, sysUserDO);
            LambdaQueryWrapper<SysUserDO> lQuery = conditionDecide(sysUserDO);

            IPage<SysUserDO> sysRoleDOIPage = iSysUserMapper.selectPage(iPage, lQuery);
            BasePage page = new BasePage(sysRoleDOIPage);
            SysUserOutVO sysUserOutVO;
            for (SysUserDO sysUserDO1 : sysRoleDOIPage.getRecords()) {
                sysUserOutVO = new SysUserOutVO();
                BeanUtils.copyPropertiesIgnoreNull(sysUserDO1, sysUserOutVO);
                SysBrandsDO sysBrandsDO = iSysBrandsMapper.selectById(sysUserDO1.getBrandsId());
                SysBrandsOrgDO sysBrandsOrgDO = iSysBrandsOrgMapper.selectById(sysUserDO1.getOrganization());
                SysUserDO userDO1 = new SysUserDO();
                userDO1.setId(sysUserDO1.getCreatedBy()==null?1:sysUserDO1.getCreatedBy());
                SysUserDO userDO = iSysUserMapper.selectById(userDO1);
                if(sysBrandsDO!=null){
                    sysUserOutVO.setBrandsName(sysBrandsDO.getBrandsName()==null?"":sysBrandsDO.getBrandsName());
                }
                if(sysBrandsOrgDO!=null){
                    sysUserOutVO.setOrganizationName(sysBrandsOrgDO.getOrgName()==null?"":sysBrandsOrgDO.getOrgName());
                }
                sysUserOutVO.setCreatedByName(userDO.getUserName());

                sysUserOutVO.setCreatedTime(DateUtil.formatDateTimeByFormat(sysUserDO1.getCreatedTime(),DateUtil.default_datetimeformat));

                list.add(sysUserOutVO);
            }
            page.setRecords(list);
            return ResponseVO.success(page);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ResponseVO.error(ResponseEnum.DATA_SOURCE_GET_ERROR);
        }
    }

    /**
     * 获取全部用户
     *
     * @return
     */
    @Override
    public ResponseVO findAll() {
        SysUserDO sysUserDO = new SysUserDO();
        List<SysUserOutVO> sysUserOutVOs = new ArrayList<>();
        try {
            LambdaQueryWrapper<SysUserDO> queryWrapper = new LambdaQueryWrapper<>(sysUserDO)
                    .eq(SysUserDO::getDeleted, BusinessEnum.NOTDELETED.getCode())
                    .orderByDesc(SysUserDO::getCreatedTime);
            List<SysUserDO> sysUserDOs = iSysUserMapper.selectList(queryWrapper);
            for (SysUserDO sysUserDO1 : sysUserDOs) {
                SysUserOutVO sysUserOutVO = new SysUserOutVO();
                BeanUtils.copyPropertiesIgnoreNull(sysUserDO1, sysUserOutVO);
                SysBrandsDO sysBrandsDO = iSysBrandsMapper.selectById(sysUserDO1.getBrandsId());
                SysBrandsOrgDO sysBrandsOrgDO = iSysBrandsOrgMapper.selectById(sysUserDO1.getOrganization());
                if(sysBrandsDO!=null){
                    sysUserOutVO.setBrandsName(sysBrandsDO.getBrandsName()==null?"":sysBrandsDO.getBrandsName());
                }
                if(sysBrandsOrgDO!=null){
                    sysUserOutVO.setOrganizationName(sysBrandsOrgDO.getOrgName()==null?"":sysBrandsOrgDO.getOrgName());
                }
                sysUserOutVOs.add(sysUserOutVO);
            }

            return ResponseVO.success(sysUserOutVOs);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ResponseVO.error(ResponseEnum.DATA_SOURCE_GET_ERROR);
        }
    }


    /**
     * 根据id获取系统用户
     *
     * @param id
     * @return
     */
    @Override
    public ResponseVO getSysUserById(Integer id) {
        try {
            SysUserDO sysUserDO = iSysUserMapper.selectById(id);
            SysUserOutVO sysUserOutVO = new SysUserOutVO();
            BeanUtils.copyPropertiesIgnoreNull(sysUserDO, sysUserOutVO);
            SysBrandsDO sysBrandsDO = iSysBrandsMapper.selectById(sysUserDO.getBrandsId());
            SysBrandsOrgDO sysBrandsOrgDO = iSysBrandsOrgMapper.selectById(sysUserDO.getOrganization());
            if(sysBrandsDO!=null){
                sysUserOutVO.setBrandsName(sysBrandsDO.getBrandsName()==null?"":sysBrandsDO.getBrandsName());
            }
            if(sysBrandsOrgDO!=null){
                sysUserOutVO.setOrganizationName(sysBrandsOrgDO.getOrgName()==null?"":sysBrandsOrgDO.getOrgName());
            }
            SysUserDO userDO1 = new SysUserDO();
            userDO1.setId(sysUserDO.getCreatedBy()==null?1:sysUserDO.getCreatedBy());
            SysUserDO userDO = iSysUserMapper.selectById(userDO1);

            sysUserOutVO.setCreatedByName(userDO.getUserName());

            sysUserOutVO.setCreatedTime(DateUtil.formatDateTimeByFormat(sysUserDO.getCreatedTime(),DateUtil.default_datetimeformat));

            //       List<SysPermissionDO> sysPermissionDOs = iSysPermissionService.getSysPermissionsByUserId(id);
            //       sysUserOutVO.setSysPerms(sysPermissionDOs);
            return ResponseVO.success(sysUserOutVO);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ResponseVO.error(ResponseEnum.DATA_SOURCE_GET_ERROR);
        }
    }

    /**
     * 新增系统用户
     *
     * @param userVO
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseVO saveSysUser(SysUserAddVO userVO) {
        try {
            if(userVO.getUserName()!=null&&userVO.getUserName().length()>32){
                return ResponseVO.error(4001,"用户名过长，长度不能大于32");
            }
            //校验邮箱格式
            String eamil = userVO.getEmail();
            if (!eamil.matches("[A-Za-z0-9\\u4e00-\\u9fa5]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$")) {
                return ResponseVO.error(ResponseEnum.DATA_SOURCE_EMAIL_ERROR);
            }
            //校验手机号格式
            String phone = userVO.getPhone();
            if (!phone.matches("1\\d{10}$")) {
                return ResponseVO.error(ResponseEnum.DATA_SOURCE_PHONE_ERROR);
            }


            QueryWrapper<SysUserDO> query = new QueryWrapper();
            query.eq("is_delete", BusinessEnum.NOTDELETED.getCode());
            query.and(wrapper -> wrapper.eq("user_account",userVO.getUserAccount().trim())
                    .or().eq("sso_id", userVO.getSsoId()).or().eq("email",userVO.getEmail().trim()));
            List<SysUserDO> sysUserDOs = iSysUserMapper.selectList(query);
            if (sysUserDOs!=null&&sysUserDOs.size() > 0) {
                return ResponseVO.error(4001,"登录名或单点登陆用户ID或邮箱已存在，请重新输入");
            }
            SysUserDO sysUserDO = new SysUserDO();
            BeanUtils.copyPropertiesIgnoreNull(userVO, sysUserDO);
            sysUserDO.setUserAccount(userVO.getUserAccount().trim());
            //第一版没有密码，所有给个默认的密码
           /* if(sysUserDO.getUserAccount().equals("admin")){
                sysUserDO.setPassword(new BCryptPasswordEncoder().encode("etocrm@admin"));
            }*/
            sysUserDO.setPassword(new BCryptPasswordEncoder().encode("123456"));
     //       sysUserDO.setPassword(new BCryptPasswordEncoder().encode(sysUserDO.getPassword()));
            iSysUserMapper.insert(sysUserDO);

            return ResponseVO.success(sysUserDO.getId());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ResponseVO.error(ResponseEnum.DATA_SOURCE_ADD_ERROR);
        }

    }

    /**
     * 更新系统用户
     *
     * @param userVO
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseVO updateSysUser(SysUserUpdateInfoVO userVO) {
        try {

            //检查是否为管理员用户
            /*Boolean isSupperUser = checkUserAllowed(new SysUserDO(userVO.getId()));
            if (isSupperUser) {
                return ResponseVO.error(4001,"管理员用户禁止操作");
            }*/
            SysUserDO userDO = iSysUserMapper.selectById(userVO.getId());
            if(userDO.getUserAccount().equals("admin")){
                return ResponseVO.error(4001,"超级管理员用户禁止操作");
            }

            if(userVO.getUserName()!=null&&userVO.getUserName().length()>32){
                return ResponseVO.error(4001,"用户名过长，长度不能大于32");
            }

            //update时判断user_account、sso_id、email的唯一性
            String returnResult = checkUniqueness(userVO);
            if(!returnResult.equals("")){
                return ResponseVO.error(4001,returnResult+"已存在，请重新输入");
            }

            SysUserDO sysUserDO = new SysUserDO();
            BeanUtils.copyPropertiesIgnoreNull(userVO, sysUserDO);
            if(userVO.getUserAccount() != null){
                sysUserDO.setUserAccount(userVO.getUserAccount().trim());
            }
            iSysUserMapper.updateById(sysUserDO);

            return ResponseVO.success();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ResponseVO.error(ResponseEnum.DATA_SOURCE_UPDATE_ERROR);
        }
    }

    //校验字段的唯一性
    private String checkUniqueness(SysUserUpdateInfoVO userVO){
        String returnResult ="";
        QueryWrapper<SysUserDO> query = new QueryWrapper();

        if(StringUtils.isNotBlank(userVO.getUserAccount())){
            query.eq("user_account",userVO.getUserAccount().trim());
            query.eq("is_delete", BusinessEnum.NOTDELETED.getCode());
            List<SysUserDO> sysUserDOs = iSysUserMapper.selectList(query);
            if(sysUserDOs.size() > 0){
                if(!sysUserDOs.get(0).getId().equals(userVO.getId())){
                    returnResult = "登录名";
                }
            }
        }

        if(userVO.getSsoId()!=null){
            query = new QueryWrapper();
            query.eq("sso_id",userVO.getSsoId());
            query.eq("is_delete", BusinessEnum.NOTDELETED.getCode());
            List<SysUserDO> sysUserDOs = iSysUserMapper.selectList(query);
            if(sysUserDOs.size() > 0){
                if(!sysUserDOs.get(0).getId().equals(userVO.getId())){
                    if(!returnResult.equals("")){
                        returnResult = returnResult + "、单点登陆用户ID";
                    }else{
                        returnResult = "单点登陆用户ID";
                    }
                }
            }
        }

        if(StringUtils.isNotBlank(userVO.getEmail())){
            query = new QueryWrapper();
            query.eq("email",userVO.getEmail());
            query.eq("is_delete", BusinessEnum.NOTDELETED.getCode());
            List<SysUserDO> sysUserDOs = iSysUserMapper.selectList(query);
            if(sysUserDOs.size() > 0){
                if(!sysUserDOs.get(0).getId().equals(userVO.getId())){
                    if(!returnResult.equals("")){
                        returnResult = returnResult + "、邮箱";
                    }else{
                        returnResult = "邮箱";
                    }
                }
            }
        }
        return returnResult;
    }

    /**
     * 根据id更新用户权限
     *
     * @param userVO
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseVO updateUserAuth(SysUserUpdateAuthVO userVO) {
        try {

            SysUserDO userDO = iSysUserMapper.selectById(userVO.getId());
            if(userDO.getUserAccount().equals("admin")){
                return ResponseVO.error(4001,"超级管理员用户禁止操作");
            }
            //根据用户id获取关联关系
            List<SysPermissionDO> sysPermissionsDOs = iSysPermissionService.getSysPermissionsByUserId(userVO.getId());
            List<Integer> roleIds = new ArrayList<>();
            if(sysPermissionsDOs!=null&&sysPermissionsDOs.size()>0){
                for(SysPermissionDO sysPermissionDO : sysPermissionsDOs){
                    roleIds.add(sysPermissionDO.getRoleId());
                }
            }
            List<Integer> roleIdsNew = new ArrayList<>(userVO.getRoleIds());
            //用户需要更新的角色
            roleIdsNew.removeAll(roleIds);
            if(roleIdsNew.size() > 0){
                //保存新的用户角色关联关系
                saveUpdateRelations(roleIdsNew,userVO.getId());
            }
            //用户需要删除的角色
            roleIds.removeAll(userVO.getRoleIds());
            if(roleIds.size() > 0){
                //删除用户角色关联关系
                for(Integer roleId : roleIds){
                    iSysPermissionService.removeByRoleId(roleId,userVO.getId());
                }
            }

            //删除用户相关的权限缓存
            sysMenuCacheManager.removeAuthorizedMenuTree(userVO.getId());

         //   sysMenuCacheManager.removeButtonPerms(userVO.getId());
            //删除用户相关缓存
            sysMenuCacheManager.removeMenuIds(userVO.getId());

            //刷新用户按钮权限缓存
            List<String> buttonPerms = sysMenuService.getUrlPermission(userVO.getId());
            sysMenuCacheManager.cacheButtonPerms(userVO.getId(), buttonPerms);

            return ResponseVO.success();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ResponseVO.error(ResponseEnum.DATA_SOURCE_UPDATE_ERROR);
        }
    }

    /**
     * 更新系统用户密码
     *
     * @param userVO
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseVO updatePassword(SysUserPwdVO userVO) {
        try {

            if(userVO.getUserAccount().equals("admin")){
                return ResponseVO.error(4001,"超级管理员用户禁止操作");
            }

            //检查是否为管理员用户
            /*Boolean isSupperUser = checkUserAllowed(new SysUserDO(userVO.getId()));
            if (isSupperUser) {
                return ResponseVO.error(4001,"管理员用户禁止操作");
            }*/

            SysUserDO sysUserDO = new SysUserDO();
            BeanUtils.copyPropertiesIgnoreNull(userVO, sysUserDO);
            //            sysUserDO.setPassword(bCryptPasswordEncoder.encode(userVO.getPassword()));
            iSysUserMapper.updateById(sysUserDO);
            return ResponseVO.success();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ResponseVO.error(ResponseEnum.DATA_SOURCE_UPDATE_ERROR);
        }
    }

    /**
     * 根据id删除系统用户
     *
     * @param id
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseVO removeSysUser(Integer id) {
        try {
            //检查是否为管理员用户
            /*Boolean isSupperUser = checkUserAllowed(new SysUserDO(id));
            if (isSupperUser) {
                return ResponseVO.error(4001,"管理员用户禁止操作");
            }*/

            SysUserDO sysUserDO = new SysUserDO();
            sysUserDO.setId(id);
            sysUserDO.setDeleted(BusinessEnum.DELETED.getCode());
            iSysUserMapper.updateById(sysUserDO);

            iSysPermissionService.removeByUserId(id);

            sysMenuCacheManager.removeAuthorizedMenuTree(id);
            sysMenuCacheManager.removeButtonPerms(id);
            sysMenuCacheManager.removeMenuIds(id);
            return ResponseVO.success();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ResponseVO.error(ResponseEnum.DATA_SOURCE_REMOVE_ERROR);
        }
    }

    /**
     * 用户启用/停用,1表示已启用,0表示已停用
     *
     * @param sysUserChangeStatusVO
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseVO changeStatus(SysUserChangeStatusVO sysUserChangeStatusVO) {
        try {

            SysUserDO userDO = iSysUserMapper.selectById(sysUserChangeStatusVO.getId());
            if(userDO.getUserAccount().equals("admin")){
                return ResponseVO.error(4001,"超级管理员用户禁止操作");
            }

            List<SysPermissionDO> relationList = iSysPermissionService
                    .getSysPermissionsByUserId(sysUserChangeStatusVO.getId());
            if(relationList!=null&&relationList.size()>0){
                List<Integer> roleIds = new ArrayList<>();
                for(SysPermissionDO sysPermissionDO : relationList){
                    roleIds.add(sysPermissionDO.getRoleId());
                }
                if(roleIds.contains(1)){
                    return ResponseVO.error(4001,"管理员用户禁止操作");
                }
            }

            //检查是否为管理员用户
            /*Boolean isSupperUser = checkUserAllowed(new SysUserDO(sysUserChangeStatusVO.getId()));
            if (isSupperUser) {
                return ResponseVO.error(4001,"管理员用户禁止操作");
            }*/

            SysUserDO sysUserDO = new SysUserDO();
            sysUserDO.setId(sysUserChangeStatusVO.getId());
            sysUserDO.setStatus(sysUserChangeStatusVO.getStatus());
            iSysUserMapper.updateById(sysUserDO);

            //删除用户角色关联关系
       //     iSysPermissionService.removeByUserId(sysUserChangeStatusVO.getId());
            //删除用户按钮权限缓存
            sysMenuCacheManager.removeButtonPerms(sysUserChangeStatusVO.getId());
            sysMenuCacheManager.removeMenuIds(sysUserChangeStatusVO.getId());
            return ResponseVO.success();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ResponseVO.error(ResponseEnum.DATA_SOURCE_UPDATE_ERROR);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateLastLoginTime(String userAccount) {
        try {
            UpdateWrapper<SysUserDO> query = new UpdateWrapper<>();
            query.eq("user_account",userAccount);
            query.eq("is_delete",BusinessEnum.NOTDELETED.getCode());
            SysUserDO userDO = new SysUserDO();
            userDO.setLastLoginTime(new Date());
            iSysUserMapper.update(userDO,query);
        }catch (Exception e){
            log.error(e.getMessage(), e);
        }
    }

    /**
     * 模糊匹配用户
     *
     * @return
     */
    @Override
    public ResponseVO findUserAll(SysUserAllVO userVO) {
        List<SysUserOutVO> sysUserOutVOs = new ArrayList<>();
        try {
            LambdaQueryWrapper<SysUserDO> objectLambdaQueryWrapper = new LambdaQueryWrapper<>();
            if (null != userVO.getUserAccount()){
                objectLambdaQueryWrapper.like(SysUserDO::getUserAccount,userVO.getUserAccount());
            }
            if (null != userVO.getUserName()){
                objectLambdaQueryWrapper.like(SysUserDO::getUserName,userVO.getUserName());
            }
            objectLambdaQueryWrapper.eq(SysUserDO::getDeleted, BusinessEnum.NOTDELETED.getCode())
                    .orderByDesc(SysUserDO::getCreatedTime);
            List<SysUserDO> sysUserDOs = iSysUserMapper.selectList(objectLambdaQueryWrapper);
            for (SysUserDO sysUserDO1 : sysUserDOs) {
                SysUserOutVO sysUserOutVO = new SysUserOutVO();
                BeanUtils.copyPropertiesIgnoreNull(sysUserDO1, sysUserOutVO);
                SysBrandsDO sysBrandsDO = iSysBrandsMapper.selectById(sysUserDO1.getBrandsId());
                SysBrandsOrgDO sysBrandsOrgDO = iSysBrandsOrgMapper.selectById(sysUserDO1.getOrganization());
                SysUserDO userDO1 = new SysUserDO();
                userDO1.setId(sysUserDO1.getCreatedBy()==null?1:sysUserDO1.getCreatedBy());
                SysUserDO userDO = iSysUserMapper.selectById(userDO1);
                if(sysBrandsDO!=null){
                    sysUserOutVO.setBrandsName(sysBrandsDO.getBrandsName()==null?"":sysBrandsDO.getBrandsName());
                }
                if(sysBrandsOrgDO!=null){
                    sysUserOutVO.setOrganizationName(sysBrandsOrgDO.getOrgName()==null?"":sysBrandsOrgDO.getOrgName());
                }
                sysUserOutVO.setCreatedByName(userDO.getUserName());

                sysUserOutVO.setCreatedTime(DateUtil.formatDateTimeByFormat(sysUserDO1.getCreatedTime(),DateUtil.default_datetimeformat));

                sysUserOutVOs.add(sysUserOutVO);
            }
            return ResponseVO.success(sysUserOutVOs);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ResponseVO.error(ResponseEnum.DATA_SOURCE_GET_ERROR);
        }
    }

    @Override
    public SysUserDO getOne(QueryWrapper<SysUserDO> query) {
        try {
            SysUserDO sysUserDO = iSysUserMapper.selectOne(query);
            if (sysUserDO == null) {
                return null;
            }
            return sysUserDO;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    //    private void saveRelations(SysUserAddVO sysUserAddVO,Integer id) throws Exception{
    //        if(sysUserAddVO.getSysPerms().size() > 0){
    //            for(SysPermissionDO sysPermissionDO : sysUserAddVO.getSysPerms()){
    //                sysPermissionDO.setUserId(id);
    //                iSysPermissionService.saveSysPermission(sysPermissionDO);
    //            }
    //        }
    //    }

    private void saveUpdateRelations(List<Integer> roleIds,Integer userId) throws Exception {
        if (roleIds.size() > 0) {
            for (Integer roleId : roleIds) {
                SysPermissionDO sysPermissionDO = new SysPermissionDO();
                sysPermissionDO.setUserId(userId);
                sysPermissionDO.setRoleId(roleId);
                iSysPermissionService.saveSysPermission(sysPermissionDO);
            }
        }
    }

    private LambdaQueryWrapper<SysUserDO> conditionDecide(SysUserDO userDO) throws Exception {

        LambdaQueryWrapper<SysUserDO> lQuery = new LambdaQueryWrapper<>();
        if (userDO.getUserName() != null) {
            lQuery.like(SysUserDO::getUserName, userDO.getUserName());
        }
        lQuery
                .eq(SysUserDO::getDeleted, BusinessEnum.NOTDELETED.getCode())
                .orderByDesc(SysUserDO::getId);

        return lQuery;
    }

    public Boolean checkUserAllowed(SysUserDO userDO) throws Exception {
        if (userDO.isAdmin()) {
            return true;
        }
        return false;
    }

    public ResponseVO refreshMenuIdsByUserId(Integer userId){
        try {
            List<String> menuIds = sysMenuService.getMenusByUserId(userId);
            return ResponseVO.success(menuIds);
        }catch (Exception e){
            log.error(e.getMessage(), e);
            return ResponseVO.error(4001,"刷新缓存失败");
        }
    }
}
