package org.etocrm.authentication.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import feign.Target;
import org.etocrm.core.util.SnowflakeIdWorker;
import org.etocrm.dynamicDataSource.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * @Author chengrong.yang
 * @Date 2020/9/15 15:20
 */
@Configuration
public class FeignConfig implements RequestInterceptor {

    private static final String REQUEST_KEY = "X-REQUEST-KEY";

    @Autowired
    RedisUtil redisUtil;

    @Bean
    public RequestAttributeHystrixConcurrencyStrategy feignHystrixConcurrencyStrategy() {
        return new RequestAttributeHystrixConcurrencyStrategy();
    }

    @Override
    public void apply(RequestTemplate requestTemplate) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if(null!=attributes){
            HttpServletRequest request = attributes.getRequest();
            //添加token
            requestTemplate.header("Authorization", request.getHeader("Authorization"));
        }
        Target target = requestTemplate.feignTarget();
        String requestKey = String.valueOf(SnowflakeIdWorker.getId());
        switch (target.name()) {
            case "tag-etocrm-authentication-server":
                redisUtil.set(requestKey, "/auth" + requestTemplate.path());
                break;
            case "tag-etocrm-dataManager-server":
                redisUtil.set(requestKey, "/data" + requestTemplate.path());
                break;
            case "tag-etocrm-tagManager-server":
                redisUtil.set(requestKey, "/tag" + requestTemplate.path());
                break;
            default:
                break;
        }
        requestTemplate.header(REQUEST_KEY, requestKey);
    }
}
