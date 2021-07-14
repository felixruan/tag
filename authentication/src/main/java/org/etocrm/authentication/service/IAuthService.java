package org.etocrm.authentication.service;

import org.etocrm.authentication.entity.VO.auth.SysUserLoginVO;
import org.etocrm.core.util.ResponseVO;

import javax.servlet.http.HttpServletRequest;

/**
 * @Author chengrong.yang
 * @date 2020/8/19 19:31
 */
public interface IAuthService {

    ResponseVO permission(String token, String url);

    ResponseVO login(SysUserLoginVO user);

    Object revokeToken(String token);

    ResponseVO crmAuth(String woaap_token, HttpServletRequest request);

    void updateLastLoginTime(String userAccount);


}
