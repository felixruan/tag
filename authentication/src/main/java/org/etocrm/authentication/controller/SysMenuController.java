package org.etocrm.authentication.controller;

import com.alibaba.fastjson.JSONArray;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.etocrm.authentication.entity.VO.auth.*;
import org.etocrm.authentication.service.ISysMenuService;
import org.etocrm.core.enums.ResponseEnum;
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
@Api(value = "菜单控制类",tags = "菜单相关API")
@RefreshScope
@RestController
@RequestMapping("/auth/menu")
public class SysMenuController {
    @Autowired
    private ISysMenuService sysMenuService;

    /**
     * 新增菜单
     * @param menuVO
     * @return
     */
    @ApiOperation(value = "新增一个菜单", notes = "" )
//    @PreAuthorize("@ss.hasPermi('menu:add')")
    @ApiImplicitParam(paramType = "insert",name = "SysMenuAddVO",value = "菜单对象",required = true)
    @PostMapping("/add")
    public ResponseVO saveSysMenu(@RequestBody @Valid SysMenuAddVO menuVO){
        return sysMenuService.saveSysMenu(menuVO);
    }

    /**
     * 根据id更新菜单
     * @param menuVO
     * @return
     */
    @ApiOperation(value = "根据菜单id修改菜单 ", notes = "" )
 //   @PreAuthorize("@ss.hasPermi('menu:update')")
    @ApiImplicitParam(paramType = "update",name = "SysMenuUpdateVO",value = "菜单对象",required = true)
    @PostMapping("/update")
    public ResponseVO updateSysMenu(@RequestBody @Valid SysMenuUpdateVO menuVO){
        return sysMenuService.updateSysMenu(menuVO);
    }

    /**
     * 拖拽子菜单功能（既更换父菜单id）
     * @param sysMenuChangePIdVO
     * @return
     */
    @ApiOperation(value = "拖拽子菜单功能", notes = "" )
//    @PreAuthorize("@ss.hasPermi('menu:changePId')")
    @ApiImplicitParam(paramType = "update",name = "SysMenuChangePIdVO",value = "菜单对象",required = true)
    @PostMapping("/changePId")
    public ResponseVO changeMenuParnetId(@RequestBody @Valid SysMenuChangePIdVO sysMenuChangePIdVO){
        return sysMenuService.changeMenuParnetId(sysMenuChangePIdVO);
    }

    @ApiOperation(value = "启用/停用菜单", notes = "" )
    @ApiImplicitParam(paramType = "update",name = "SysMenuChangeStatusVO",value = "菜单对象",required = true)
 //   @PreAuthorize("@ss.hasPermi('menu:status')")
  //  @PostMapping("/status")
    private ResponseVO changeStatus(@RequestBody @Valid SysMenuChangeStatusVO sysMenuChangeStatusVO){
        return sysMenuService.changeStatus(sysMenuChangeStatusVO);
    }

    /**
     * 根据id查询菜单
     * @param id
     * @return
     */
    @ApiOperation(value = "根据菜单id查询菜单", notes = "" )
 //   @PreAuthorize("@ss.hasPermi('menu:get')")
    @ApiImplicitParam(paramType = "path",name = "id", value = "菜单id", dataType = "Integer", required = true)
    @GetMapping("/get/{id}")
    public ResponseVO getSysMenuById(@PathVariable("id") Integer id){
        return sysMenuService.getSysMenuById(id);
    }


    @ApiOperation(value = "根据菜单id删除菜单", notes = "" )
//    @PreAuthorize("@ss.hasPermi('menu:remove')")
    @ApiImplicitParam(paramType = "path",name = "id",value = "菜单id",required = true,dataType = "Integer")
    @GetMapping("/remove/{id}")
    public ResponseVO removeSysMenu(@PathVariable("id") Integer id){
        return sysMenuService.removeSysMenu(id);
    }

    /**
     * 查询根菜单
     * @return
     */
    @ApiOperation(value = "根据父id查询子菜单列表", notes = "根据父id查询子菜单" )
 //   @PreAuthorize("@ss.hasPermi('menu:kidmenu')")
    @ApiImplicitParam(paramType = "path",name = "id",value = "上级菜单id",required = true,dataType = "Integer")
    @GetMapping("/getChildrenMenusByParentId/{id}")
    public ResponseVO getSysMenuListByParentId(@PathVariable("id") Integer id){
        return sysMenuService.getSysMenuListByParentId(id);
    }

    /**
     * 根据菜单id查询子菜单
     * @param id
     * @return
     */
    @ApiOperation(value = "查询子菜单列表", notes = "" )
  //  @PreAuthorize("@ss.hasPermi('menu:child')")
    @ApiImplicitParam(paramType = "path",name = "id",value = "上级菜单id",required = true,dataType = "Integer")
   // @GetMapping("/child/{id}")
    private ResponseVO getChildSysMenuList(@PathVariable("id") Integer id){
        return sysMenuService.getChildSysMenuList(id);
    }


    /**
     * 根据用户id查询用户被授权的菜单树
     * @param userId
     * @return
     */
    @ApiOperation(value = "根据用户id查询用户权限菜单树", notes = "查询权限菜单树" )
 //   @PreAuthorize("@ss.hasPermi('menu:tree')")
    @ApiImplicitParam(paramType = "path",name = "userId",value = "用户id",required = true,dataType = "Integer")
    @GetMapping("/geMenuTreeByUserId/{userId}")
    public ResponseVO getAuthorizedTree(@PathVariable("userId") Integer userId){
        JSONArray jsonArr= sysMenuService.getAuthorizedTree(userId);
        if(jsonArr.size()==0){
            ResponseVO.error(ResponseEnum.DATA_SOURCE_GET_ERROR);
        }
        return ResponseVO.success(jsonArr);
    }

    /**
     * 根据角色id查询菜单
     * @param roleId
     * @return
     */
    @ApiOperation(value = "根据角色id查询菜单", notes = "查询菜单" )
    //   @PreAuthorize("@ss.hasPermi('menu:tree')")
    @ApiImplicitParam(paramType = "path",name = "roleId",value = "角色id",required = true,dataType = "Integer")
    @GetMapping("/getMenusByRoleId/{roleId}")
    public ResponseVO getMenusByRoleId(@PathVariable("roleId") Integer roleId){
        return sysMenuService.getMenusByRoleId(roleId);
    }

    /**
     * 自动新增按钮菜单
     * @return
     */
    /*@ApiOperation(value = "自动新增按钮菜单", notes = "自动新增按钮菜单" )
    //   @PreAuthorize("@ss.hasPermi('menu:tree')")
    @GetMapping("/autoCreateButtonMenu")
    public ResponseVO autoCreateButtonMenu(){
        return sysMenuService.autoCreateButtonMenu();
    }*/






}

