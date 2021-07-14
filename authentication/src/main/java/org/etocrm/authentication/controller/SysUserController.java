package org.etocrm.authentication.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.etocrm.authentication.entity.VO.SysUserAllVO;
import org.etocrm.authentication.entity.VO.auth.*;
import org.etocrm.authentication.service.ISysUserService;
import org.etocrm.core.util.ResponseVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.MessageSource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * @Author peter.li
 * @date 2020/8/19 19:27
 */
@Api(value = "用户相关控制类", tags = "系统用户相关API")
@RefreshScope
@RestController
@RequestMapping("/auth/user")
public class SysUserController {

    @Autowired
    MessageSource messageSource;

    @Autowired
    private ISysUserService sysUserService;

    /**
     * 新增系统用户
     *
     * @param userVO
     * @return
     */
    @ApiOperation(value = "新增一个系统用户", notes = "")
 //   @PreAuthorize("@ss.hasPermi('user:add')")
    @ApiImplicitParam(paramType = "insert", name = "SysUserAddVO", value = "用户对象", required = true)
    @PostMapping("/add")
    public ResponseVO saveSysUser(@RequestBody @Valid SysUserAddVO userVO) {
        return sysUserService.saveSysUser(userVO);
    }

    /**
     * 根据id更新系统用户
     *
     * @param userVO
     * @return
     */
    @ApiOperation(value = "根据用户id修改系统用户 ", notes = "")
 //   @PreAuthorize("@ss.hasPermi('user:update')")
    @ApiImplicitParam(paramType = "update", name = "SysUserUpdateInfoVO", value = "用户对象", required = true)
    @PostMapping("/updateUserInfo")
    public ResponseVO updateSysUser(@RequestBody @Valid SysUserUpdateInfoVO userVO) {
        return sysUserService.updateSysUser(userVO);
    }



    /**
     * 根据id更新用户权限
     *
     * @param userVO
     * @return
     */
    @ApiOperation(value = "根据用户id更新用户权限 ", notes = "")
    @PreAuthorize("@ss.hasPermi('user:updateauth')")
    @ApiImplicitParam(paramType = "update", name = "SysUserUpdateAuthVO", value = "用户对象", required = true)
    @PostMapping("/updateAuth")
    public ResponseVO updateUserAuth(@RequestBody @Valid SysUserUpdateAuthVO userVO) {
        return sysUserService.updateUserAuth(userVO);
    }



    /**
     * 更新用户密码
     *
     * @param userVO
     * @return
     */
    @ApiOperation(value = "根据用户id修改系统用户密码 ", notes = "")
 //   @PreAuthorize("@ss.hasPermi('user:pwd')")
    @ApiImplicitParam(paramType = "update", name = "SysUserPwdVO", value = "用户对象", required = true)
 //   @PostMapping("/pwd")
    private ResponseVO updatePassword(@RequestBody @Valid SysUserPwdVO userVO) {
        return sysUserService.updatePassword(userVO);
    }

    /**
     * 根据id查询系统用户
     *
     * @param id
     * @return
     */
    @ApiOperation(value = "根据用户id查询用户", notes = "")
//    @PreAuthorize("@ss.hasPermi('user:get')")
    @ApiImplicitParam(paramType = "path", name = "id", value = "用户id", dataType = "Integer", required = true)
    @GetMapping("/get/{id}")
    public ResponseVO getUserById(@PathVariable("id") Integer id) {
        return sysUserService.getSysUserById(id);
    }

    /**
     * 根据id删除系统用户
     *
     * @param id
     * @return
     */
    @ApiOperation(value = "根据用户id删除系统用户", notes = "")
 //   @PreAuthorize("@ss.hasPermi('user:remove')")
    @ApiImplicitParam(paramType = "path", name = "id", value = "用户id", required = true, dataType = "Integer")
 //   @DeleteMapping("/remove/{id}")
    private ResponseVO removeSysUser(@PathVariable("id") Integer id) {
        return sysUserService.removeSysUser(id);
    }

    /**
     * 分页查询用户
     *
     * @return
     */
    @ApiOperation(value = "分页查询系统用户列表", notes = "")
//    @PreAuthorize("@ss.hasPermi('user:page')")
    @PostMapping("/page")
    public ResponseVO getUsersByPage(@RequestBody @Valid SysUserInPageVO userVO) {
        return sysUserService.getUsersByPage(userVO);
    }

    /**
     * 查询全部用户
     *
     * @return
     */
    @ApiOperation(value = "查询全部用户列表", notes = "")
    //   @PreAuthorize("@ss.hasPermi('user:all')")
 //   @PostMapping("/all")
    private ResponseVO findAll() {
        return sysUserService.findAll();
    }


    /**
     * 根据字段模糊查询用户列表（tag模块调用户模块）
     *
     * @return
     */
    @ApiOperation(value = "查询全部用户列表", notes = "")
    @PostMapping("/findUserAll")
    public ResponseVO findUserAll(@RequestBody SysUserAllVO userVO) {
        return sysUserService.findUserAll(userVO);
    }



    /**
     * 设置用户启用/停用
     *
     * @param sysUserChangeStatusVO
     * @return
     */
    @ApiOperation(value = "启用/停用用户", notes = "")
 //   @PreAuthorize("@ss.hasPermi('user:status')")
    @ApiImplicitParam(paramType = "update", name = "SysUserChangeStatusVO", value = "用户对象", required = true)
    @PostMapping("/updateStatus")
    public ResponseVO changeStatus(@RequestBody @Valid SysUserChangeStatusVO sysUserChangeStatusVO) {
        return sysUserService.changeStatus(sysUserChangeStatusVO);
    }

    /**
     * 刷新用户菜单ids缓存
     *
     * @param userId
     * @return
     */
    @ApiOperation(value = "刷新用户菜单ids缓存", notes = "")
    //   @PreAuthorize("@ss.hasPermi('user:status')")
    @ApiImplicitParam(paramType = "update", name = "SysUserChangeStatusVO", value = "用户对象", required = true)
    @PostMapping("/refreshMenuIdsByUserId/{userId}")
    public ResponseVO refreshMenuIdsByUserId(@PathVariable("userId") Integer userId) {
        return sysUserService.refreshMenuIdsByUserId(userId);
    }


}

