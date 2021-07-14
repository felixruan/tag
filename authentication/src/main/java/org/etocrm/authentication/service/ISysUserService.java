package org.etocrm.authentication.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.etocrm.authentication.entity.DO.SysUserDO;
import org.etocrm.authentication.entity.VO.SysUserAllVO;
import org.etocrm.authentication.entity.VO.auth.*;
import org.etocrm.core.util.ResponseVO;

/**
 * @Author peter.li
 * @date 2020/8/17 14:20
 */
public interface ISysUserService  {

    ResponseVO getUsersByPage(SysUserInPageVO userVO);

    ResponseVO findAll();

    ResponseVO getSysUserById(Integer id);

    ResponseVO saveSysUser(SysUserAddVO userVO);

    ResponseVO updateSysUser(SysUserUpdateInfoVO userVO);

    ResponseVO updateUserAuth(SysUserUpdateAuthVO sysUserUpdateAuthVO);

    ResponseVO updatePassword(SysUserPwdVO userVO);

    ResponseVO removeSysUser(Integer id);

    SysUserDO getOne(QueryWrapper<SysUserDO> query);

    ResponseVO changeStatus(SysUserChangeStatusVO sysUserChangeStatusVO);

    void updateLastLoginTime(String userAccount);

    ResponseVO findUserAll(SysUserAllVO userVO);

    ResponseVO refreshMenuIdsByUserId(Integer userId);
}
