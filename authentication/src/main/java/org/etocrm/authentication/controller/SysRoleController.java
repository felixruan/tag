package org.etocrm.authentication.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.etocrm.authentication.entity.VO.auth.*;
import org.etocrm.authentication.service.ISysRoleService;
import org.etocrm.core.util.ResponseVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * @Author peter.li
 * @date 2020/8/19 19:27
 */
@Api(value = "角色控制类", tags = "系统角色相关API")
@RefreshScope
@RestController
@RequestMapping("/auth/role")
public class SysRoleController {

    @Autowired
    private ISysRoleService sysRoleService;

    /**
     * 新增角色
     *
     * @param roleVO
     * @return
     */
    @ApiOperation(value = "新增一个系统角色", notes = "")
//    @PreAuthorize("@ss.hasPermi('role:add')")
    @ApiImplicitParam(paramType = "insert", name = "SysRole", value = "角色对象", required = true)
    @PostMapping("/add")
    public ResponseVO saveSysRole(@RequestBody @Valid SysRoleAddVO roleVO) {
        return sysRoleService.saveRole(roleVO);
    }

    /**
     * 根据id更新角色
     *
     * @param roleVO
     * @return
     */
    @ApiOperation(value = "根据角色id更新角色信息", notes = "")
//    @PreAuthorize("@ss.hasPermi('role:update')")
    @ApiImplicitParam(paramType = "update", name = "SysRoleInVO", value = "角色对象", required = true)
    @PostMapping("/update")
    public ResponseVO updateSysRole(@RequestBody @Valid SysRoleUpdateVO roleVO) {
        return sysRoleService.updateSysRole(roleVO);
    }

    /**
     * 根据id更新角色
     *
     * @param roleVO
     * @return
     */
    /*@ApiOperation(value = "根据角色id修改角色权限", notes = "")
    @PreAuthorize("@ss.hasPermi('role:updateauth')")
    @ApiImplicitParam(paramType = "update", name = "SysRoleInVO", value = "角色对象", required = true)
    @PostMapping("/updateauth")
    public ResponseVO updateSysRoleAuth(@RequestBody @Valid SysRoleUpdateAuthVO roleVO) {
        return sysRoleService.updateSysRoleAuth(roleVO);
    }*/

    /**
     * 根据id查询角色
     *
     * @param id
     * @return
     */
    @ApiOperation(value = "根据角色id查询角色", notes = "")
    //    @PreAuthorize("@ss.hasPermi('role:get')")
    @ApiImplicitParam(paramType = "path", name = "id", value = "角色id", dataType = "Integer", required = true)
    @GetMapping("/get/{id}")
    public ResponseVO getRoleById(@PathVariable("id") Integer id) {
        return sysRoleService.getRoleById(id);
    }

    /**
     * 根据用户id查询角色
     *
     * @param userId
     * @return
     */
    @ApiOperation(value = "根据用户id查询角色", notes = "")
    //    @PreAuthorize("@ss.hasPermi('role:get')")
    @ApiImplicitParam(paramType = "path", name = "userId", value = "用户id", dataType = "Integer", required = true)
    @GetMapping("/getRolesByUserId/{userId}")
    public ResponseVO getRoleByUserId(@PathVariable("userId") Integer userId) {
        return sysRoleService.getRoleByUserId(userId);
    }

    /**
     * 根据id删除角色
     *
     * @param id
     * @return
     */
    @ApiOperation(value = "根据角色id删除角色", notes = "")
//    @PreAuthorize("@ss.hasPermi('role:remove')")
    @ApiImplicitParam(paramType = "path", name = "id", value = "角色id", required = true, dataType = "Integer")
    @GetMapping("/remove/{id}")
    public ResponseVO removeSysRole(@PathVariable("id") Integer id) {
        return sysRoleService.removeSysRole(id);
    }

    /**
     * 分页查询角色
     *
     * @return
     */
    @ApiOperation(value = "分页查询角色列表", notes = "")
    //    @PreAuthorize("@ss.hasPermi('role:page')")
    @PostMapping("/page")
    public ResponseVO getRoleListByPage(@RequestBody @Valid SysRoleInPageVO sysRoleInPageVO) {
        return sysRoleService.getListByPage(sysRoleInPageVO);
    }

    /**
     * 查询全部角色
     *
     * @return
     */
    @ApiOperation(value = "查询全部角色列表", notes = "")
    //    @PreAuthorize("@ss.hasPermi('role:all')")
    @PostMapping("/findAll")
    public ResponseVO findAll() {
        return sysRoleService.findAll();
    }

}

