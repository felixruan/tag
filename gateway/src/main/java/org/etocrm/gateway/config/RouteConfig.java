package org.etocrm.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author chengrong.yang
 * @Date 2020/11/10 9:37
 */
@Configuration
public class RouteConfig {

    @Bean
    public MyRouteDefinitionRepository mysqlRouteDefinitionRepository() {
        return new MyRouteDefinitionRepository();
    }
}
