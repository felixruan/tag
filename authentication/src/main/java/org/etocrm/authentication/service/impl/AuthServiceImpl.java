package org.etocrm.authentication.service.impl;

import cn.hutool.http.HttpRequest;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.etocrm.authentication.config.AuthConfig;
import org.etocrm.authentication.config.PermissionService;
import org.etocrm.authentication.config.ServerConfig;
import org.etocrm.authentication.entity.DO.SysUserDO;
import org.etocrm.authentication.entity.VO.auth.SysSSODataVO;
import org.etocrm.authentication.entity.VO.auth.SysSSORequestDataVO;
import org.etocrm.authentication.entity.VO.auth.SysSSOUserInfoVO;
import org.etocrm.authentication.entity.VO.auth.SysUserLoginVO;
import org.etocrm.authentication.service.*;
import org.etocrm.authentication.util.AuthUtils;
import org.etocrm.core.enums.BusinessEnum;
import org.etocrm.core.enums.ResponseEnum;
import org.etocrm.core.util.BeanUtils;
import org.etocrm.core.util.Constant;
import org.etocrm.core.util.ResponseVO;
import org.etocrm.dynamicDataSource.model.VO.UniteUserAuthOutVO;
import org.etocrm.dynamicDataSource.util.RedisConfig;
import org.etocrm.dynamicDataSource.util.RedisUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.provider.token.ConsumerTokenServices;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * @Author chengrong.yang
 * @date 2020/8/19 19:31
 */
@Service
@Slf4j
public class AuthServiceImpl implements IAuthService {

    public static final String LOGIN_URL = "/oauth/token";

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ISysUserService sysUserService;

    @Autowired
    private ISysMenuService sysMenuService;

    @Autowired
    private ConsumerTokenServices consumerTokenServices;

    @Autowired
    private PermissionService permissionService;

    @Autowired
    ServerConfig serverConfig;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private AuthUtils authUtils;

    @Autowired
    private AuthAsyncServiceManager authAsyncServiceManager;


    @Override
    public ResponseVO permission(String token, String url) {
        try {
            UniteUserAuthOutVO tokenVO;
            if (null != token && !StringUtils.isEmpty(token) && token.length() > 7) {
//                tokenVO  = redisUtil.getOauthTokenRefresh(token, UniteUserAuthOutVO.class);
                tokenVO = redisUtil.getOauthToken(token, UniteUserAuthOutVO.class);
                //    return ResponseVO.success(tokenVO);
            } else {
                return ResponseVO.error(ResponseEnum.ACCESS_TOKEN_INVALID);
            }


            //按钮名称
            String buttonName = url.trim();

            //校验用户是否有按钮权限
            Boolean hasAuth = permissionService.hasPermi(tokenVO, buttonName);
            if (hasAuth) {
                return ResponseVO.success(tokenVO);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        // return ResponseVO.error(ResponseEnum.ACCESS_TOKEN_INVALID);
        return ResponseVO.error(ResponseEnum.INSUFFICIENT_PERMISSIONS);
    }

    @Override
    public ResponseVO login(SysUserLoginVO user) {
        try {
            UniteUserAuthOutVO tokenVO;
            RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
            ServletRequestAttributes attributes = (ServletRequestAttributes) requestAttributes;
            HttpServletRequest request = attributes.getRequest();
            String token = request.getHeader("Authorization");
            QueryWrapper<SysUserDO> query = new QueryWrapper();
            query.eq("user_account", user.getUserAccount());
            query.eq("status", BusinessEnum.USING.getCode());
            SysUserDO sysUserDO = sysUserService.getOne(query);
            if (null != token && !StringUtils.isEmpty(token) && token.length() > 7) {
//                tokenVO = redisUtil.getOauthTokenRefresh(token, UniteUserAuthOutVO.class);
                tokenVO = redisUtil.getOauthToken(token, UniteUserAuthOutVO.class);
                if (null != tokenVO && null != tokenVO.getId()) {
                    if (token.indexOf("Bearer") == 0) {
                        token = token.substring(7, token.length());
                    }
                    BeanUtils.copyPropertiesIgnoreNull(sysUserDO, tokenVO);
                    tokenVO.setOrgId(sysUserDO.getOrganization());
                    sysUserService.updateLastLoginTime(tokenVO.getUserAccount());
                    return ResponseVO.success(chmodAuth(tokenVO, token));
                }
            }

            if (null != sysUserDO) {
                tokenVO = new UniteUserAuthOutVO();
                BeanUtils.copyPropertiesIgnoreNull(sysUserDO, tokenVO);
                tokenVO.setOrgId(sysUserDO.getOrganization());
                String client_secret = AuthConfig.CLIENT_SECRET;
                if (!client_secret.equals(user.getPassword())) {
                    return ResponseVO.error(ResponseEnum.PASSWORD_ERROR);
                }
                DefaultOAuth2AccessToken accesstoken = createTokenByPwd(user);
                token = accesstoken.getValue();
                redisUtil.set(token, tokenVO, RedisConfig.expire);
                sysUserService.updateLastLoginTime(sysUserDO.getUserAccount());
                return ResponseVO.success(chmodAuth(tokenVO, token));
            } else {
                return ResponseVO.error(ResponseEnum.USER_NOT_EXIST);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            if (e.getMessage().contains("密码错误")) {
                return ResponseVO.error(ResponseEnum.PASSWORD_ERROR);
            }
            return ResponseVO.error(ResponseEnum.UNAUTHORIZED);
        }
    }

    private JSONObject chmodAuth(UniteUserAuthOutVO tokenVO, String token) {
        JSONObject json = new JSONObject();
        String menuTree = "";
        JSONArray menuTreeArr = sysMenuService.getAuthorizedTree(tokenVO.getId());
        menuTree = JSON.toJSONString(menuTreeArr);
        redisUtil.set(tokenVO.getId().toString(), menuTree, RedisConfig.expire);

        authAsyncServiceManager.asyncLoadUserAuths(tokenVO);

        json.put("userSession", tokenVO);
        json.put("authentication", token);
        json.put("menuTree", menuTreeArr);
        return json;
    }

    @Override
    public Object revokeToken(String token) {
        try {
            consumerTokenServices.revokeToken(token);
            return ResponseVO.success();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ResponseVO.error(ResponseEnum.ACCESS_TOKEN_INVALID);
        }
    }

    @Override
    public ResponseVO crmAuth(String woaap_token, HttpServletRequest request) {

        //    SysUserTokenVO sysUserTokenVO = new SysUserTokenVO();
        JSONObject obj = new JSONObject();
        try {
            String requestUrl = request.getRequestURL().toString();
            log.info("请求路径>>>>>>>>>" + requestUrl);

            if (requestUrl.contains(authUtils.getHost())) {
                String resData = requestGetUserInfo(woaap_token);
                SysSSORequestDataVO requestDataVO = JSONObject.parseObject(resData, SysSSORequestDataVO.class);
                //         JSONObject jsonObject = JSONObject.parseObject(resData);
                log.info("requestData.errcode>>>>>>" + requestDataVO.getErrcode());
                if (requestDataVO.getErrcode() == 0) {//表示请求成功
                    SysSSODataVO dataVO = requestDataVO.getData();
                    if (null == dataVO) {
                        return ResponseVO.error(ResponseEnum.DATA_GET_ERROR);
                    }
                    log.info("dataInfo>>>>>>>" + dataVO.getUserInfo().toString());
                    SysSSOUserInfoVO userInfo = dataVO.getUserInfo();
                    if (null == userInfo) {
                        return ResponseVO.error(ResponseEnum.DATA_GET_ERROR);
                    }
                    Long ssoId = userInfo.getUserId();

                    log.info("ssoId>>>>>>>" + ssoId);

                    QueryWrapper<SysUserDO> query = new QueryWrapper();
                    query.eq("sso_id", ssoId);
                    query.eq("is_delete", BusinessEnum.NOTDELETED.getCode());
                    query.eq("status", BusinessEnum.USING.getCode());
                    SysUserDO sysUserDO = sysUserService.getOne(query);
                    if (null == sysUserDO) {
                        return ResponseVO.error(ResponseEnum.USER_NOT_EXIST);
                    }
                    UniteUserAuthOutVO uniteUserAuthOutVO = new UniteUserAuthOutVO();
                    BeanUtils.copyPropertiesIgnoreNull(sysUserDO, uniteUserAuthOutVO);
                    uniteUserAuthOutVO.setOrgId(sysUserDO.getOrganization());

                    log.info("authUser>>>>>>>" + sysUserDO.toString());
                    DefaultOAuth2AccessToken token = createTokenByAccount(uniteUserAuthOutVO);
                    JSONArray menuTreeArr = sysMenuService.getAuthorizedTree(uniteUserAuthOutVO.getId());
                    obj.put("authorization", token.getValue());
                    obj.put("userSession", uniteUserAuthOutVO);
                    obj.put("menuTree", menuTreeArr);
                    //          sysUserTokenVO.setAuthorization(String.valueOf(token.getValue()));
                    log.info("responseData>>>>>>" + obj.toJSONString());
                    sysUserService.updateLastLoginTime(sysUserDO.getUserAccount());

                } else if (requestDataVO.getErrcode() == 10000) {
                    log.info("requestData>>>>>>" + requestDataVO.toString());
                    return ResponseVO.error(ResponseEnum.ACCESS_TOKEN_INVALID);
                } else {
                    return ResponseVO.error(ResponseEnum.ACCESS_CODE_INVALID);
                }
            } else {
                return ResponseVO.error(ResponseEnum.INCORRECT_PARAMS);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ResponseVO.error(ResponseEnum.UNAUTHORIZED);
        }
        return ResponseVO.success(obj);
    }

    /**
     * 更新最后登录时间
     *
     * @param userAccount
     */
    @Override
    public void updateLastLoginTime(String userAccount) {
        try {
            sysUserService.updateLastLoginTime(userAccount);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

    }

    public String requestGetUserInfo(String woaap_token) {
        String url = authUtils.getRequestGetUserUrl() + "?woaap_token=" + woaap_token;
        return HttpRequest.get(url).execute().body();
    }

    private DefaultOAuth2AccessToken createTokenByPwd(SysUserLoginVO user) {
        MultiValueMap<String, Object> paramMap = new LinkedMultiValueMap<>();
        paramMap.add("client_id", AuthConfig.CLIENT_ID);
        paramMap.add("client_secret", AuthConfig.CLIENT_SECRET);
        paramMap.add("username", user.getUserAccount());
        paramMap.add("password", user.getPassword());
        paramMap.add("grant_type", AuthConfig.GRANT_TYPE[0]);

        DefaultOAuth2AccessToken token = restTemplate.postForObject(
                serverConfig.getUrl() + LOGIN_URL, paramMap, DefaultOAuth2AccessToken.class);
        return token;
    }

    private DefaultOAuth2AccessToken createTokenByAccount(UniteUserAuthOutVO user) {
        MultiValueMap<String, Object> paramMap = new LinkedMultiValueMap<>();
        paramMap.add("client_id", AuthConfig.CLIENT_ID);
        paramMap.add("client_secret", AuthConfig.CLIENT_SECRET);
        paramMap.add("username", user.getUserAccount());
        paramMap.add("grant_type", AuthConfig.GRANT_TYPE[2]);

        DefaultOAuth2AccessToken token = restTemplate.postForObject(
                serverConfig.getUrl() + LOGIN_URL, paramMap, DefaultOAuth2AccessToken.class);
        return token;
    }

}
