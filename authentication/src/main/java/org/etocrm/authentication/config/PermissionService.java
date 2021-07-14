package org.etocrm.authentication.config;

import lombok.extern.slf4j.Slf4j;
import org.etocrm.authentication.service.ISysMenuService;
import org.etocrm.dynamicDataSource.model.VO.UniteUserAuthOutVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Create By peter.li
 */
//@Service("ss")
@Component
@Slf4j
public class PermissionService {

    /** 所有权限标识 */
    private static final String ALL_PERMISSION = "*:*:*";


    /*@Autowired
    private RedisUtil redisUtil;*/
    @Autowired
    private ISysMenuService iSysMenuService;


    /**
     * 验证用户是否具备某权限
     *
     * @param permission 权限字符串
     * @return 用户是否具备某权限
     */
    public Boolean hasPermi(UniteUserAuthOutVO tokenVO, String permission)
    {
        Boolean isPass = false;
        try {
            if (permission == null &&permission.isEmpty()){
                return false;
            }
            //去掉数字
            permission = cutOutButtonName(permission);

            //第一期只判断用户是否有按钮的父菜单权限
            if(tokenVO != null && tokenVO.getId() != null){
                isPass = iSysMenuService.hasParentMenuAuth(tokenVO.getId(),permission);
            }

            return isPass;

            //保留到二期，前端页面直接控制到按钮级别
            /*List<String> buttonPerm;
            if(tokenVO !=null && tokenVO.getId() != null){
                buttonPerm = redisUtil.getRefresh("button_"+ tokenVO.getId(),List.class);
                if(buttonPerm == null || buttonPerm.size() == 0){
                    buttonPerm = iSysMenuService.getUrlPermission(tokenVO.getId());
                }
            }else{
                return false;
            }
            return hasPermissions(buttonPerm, permission);*/
        }catch (Exception e){
            log.error(e.getMessage(), e);
            return false;
        }

    }

    //截取路径中非数字部分，如果/auth/auth/user/get/21 -> /auth/auth/user/get/
    private String cutOutButtonName(String permission){
        Pattern pattern = Pattern.compile("\\D+");
        Matcher matcher = pattern.matcher(permission);
        while (matcher.find()) {
            permission = matcher.group(0);
        }
        return permission;
    }


    /**
     * 判断用户是否拥有某个角色
     *
     * @param role 角色字符串
     * @return 用户是否具备某角色
     */
    public Boolean hasRole(String role)
    {
        //todo
        return false;
    }

    /**
     * 验证用户是否不具备某角色，与 isRole逻辑相反。
     *
     * @param role 角色名称
     * @return 用户是否不具备某角色
     */
    public Boolean lacksRole(String role)
    {
        return hasRole(role) != true;
    }


    /**
     * 判断是否包含权限
     *
     * @param permissions 权限列表
     * @param permission 权限字符串
     * @return 用户是否具备某权限
     */
    private Boolean hasPermissions(List<String> permissions, String permission) throws Exception{
        Boolean result = false;
        for(int i=0;i<permissions.size();i++){
            result = permission.startsWith(permissions.get(i));
        }

        return result;
    }



    private String getToken(HttpServletRequest request)
    {
        String token = request.getHeader("token");
        if (token != null &&  token.startsWith("Bearer "))
        {
            token = token.replace("Bearer ", "");
        }
        return token;
    }


}
