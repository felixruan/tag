package org.etocrm.gateway.api;

import org.etocrm.gateway.api.fallback.AuthServiceFallBackFactory;
import org.etocrm.gateway.config.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @Author chengrong.yang
 * @date 2020/8/18 14:27
 */
@FeignClient(name = "tag-etocrm-authentication-server", configuration = FeignConfig.class, fallbackFactory = AuthServiceFallBackFactory.class)
public interface IAuthService {

    @GetMapping("/auth/permission")
    String permission(@RequestParam String authentication, @RequestParam(required = false) String url) throws Exception;
}
