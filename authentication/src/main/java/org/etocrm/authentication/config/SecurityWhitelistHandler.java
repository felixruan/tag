package org.etocrm.authentication.config;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.stereotype.Component;

/**
 * @Author chengrong.yang
 * @Date 2020/9/14 17:59
 */
@Component
public class SecurityWhitelistHandler {

    public HttpSecurity handle(HttpSecurity http) throws Exception {
        return http
                .authorizeRequests()
                .regexMatchers(AuthConfig.WHITE_LIST)
                .permitAll()
                .and();
    }
}
