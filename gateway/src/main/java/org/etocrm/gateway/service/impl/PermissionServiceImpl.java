package org.etocrm.gateway.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.etocrm.gateway.api.IAuthService;
import org.etocrm.gateway.entity.Authentication;
import org.etocrm.gateway.service.IPermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author chengrong.yang
 * @date 2020/8/18 14:25
 */
@Service
public class PermissionServiceImpl implements IPermissionService {

    @Autowired
    private IAuthService authService;

    @Override
    public Authentication permission(String token, String url) {
        String result = "";
        try{
            result = authService.permission(token, url);
        }catch(Exception e){
            e.printStackTrace();
        }
        if (StringUtils.isEmpty(result)) return null;
        JSONObject resultJson = JSONObject.parseObject(result);
        Integer code = (Integer) resultJson.get("code");
        if (0 == code) {
            Authentication authentication = JSON.parseObject(String.valueOf(resultJson.getString("data")), Authentication.class);
            if (null != authentication) {
                authentication.setAccessToken(token);
                return authentication;
            }
        }
        return null;
    }
}
