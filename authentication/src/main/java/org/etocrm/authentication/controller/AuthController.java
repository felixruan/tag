package org.etocrm.authentication.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.etocrm.authentication.entity.VO.auth.SysUserLoginVO;
import org.etocrm.authentication.service.IAuthService;
import org.etocrm.core.util.ResponseVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * @Author chengrong.yang
 * @date 2020/8/19 19:27
 */
@Api(value = "用户登录控制类",tags = "系统认证相关API")
@RefreshScope
@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    IAuthService authService;

    /**
     * @return org.etocrm.core.util.ResponseVO
     * @Author chengrong.yang
     * @Description //鉴权
     * @Date 2020/8/19 19:30
     * @Param [token]
     **/
    @GetMapping("/permission")
    public ResponseVO permission(@RequestParam String authentication, @RequestParam String url) {
        return authService.permission(authentication, url);
    }


    /**
     * @return org.etocrm.core.util.ResponseVO
     * @Author chengrong.yang
     * @Description //登录
     * @Date 2020/8/19 19:29
     * @Param [user]
     **/
    @ApiOperation(value = "登录系统", notes = "" )
    @PostMapping("/login")
    public ResponseVO login(@RequestBody SysUserLoginVO userVO) {
        return authService.login(userVO);
    }

    /**
     * @return org.etocrm.core.util.ResponseVO
     * @Author chengrong.yang
     * @Description //TODO
     * @Date 2020/8/20 1:09
     * @Param [token]
     **/
    @ApiOperation(value = "退出系统", notes = "" )
    @PostMapping("/logout")
    public ResponseVO logout(String token) {
        return ResponseVO.success(authService.revokeToken(token));
    }

    @ApiOperation(value = "crm登录认证", notes = "" )
    @PostMapping("/crmauth")
    public ResponseVO crmAuth(@RequestParam("woaap_token") String woaap_token, HttpServletRequest request){

        return authService.crmAuth(woaap_token,request);

    }

}
