package org.etocrm.gateway.service;

import org.etocrm.gateway.entity.Authentication;

/**
 * @Author chengrong.yang
 * @date 2020/8/18 14:25
 */
public interface IPermissionService {
    Authentication permission(String token, String url);
}
