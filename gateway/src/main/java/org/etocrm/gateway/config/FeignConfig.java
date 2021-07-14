package org.etocrm.gateway.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import feign.Target;
import org.etocrm.core.util.SnowflakeIdWorker;
import org.etocrm.gateway.util.RedisUtil;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import reactor.core.publisher.Mono;

import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @Author chengrong.yang
 * @date 2020/8/19 10:28
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

    @Bean
    KeyResolver userKeyResolver() {
        return exchange -> Mono.just(Objects.requireNonNull(exchange.getRequest().getRemoteAddress()).getAddress().getHostAddress());
    }

    @Bean
    @ConditionalOnMissingBean
    public HttpMessageConverters messageConverters(ObjectProvider<HttpMessageConverter<?>> converters) {
        return new HttpMessageConverters(converters.orderedStream().collect(Collectors.toList()));
    }

    @Override
    public void apply(RequestTemplate requestTemplate) {
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
