package org.etocrm.gateway.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Author chengrong.yang
 * @date 2020/8/19 11:02
 */
@Data
@Component
@ConfigurationProperties(prefix = "gateway")
public class GatewayPropertiesConfig {

    private List<String> ignoreUrl;
}
