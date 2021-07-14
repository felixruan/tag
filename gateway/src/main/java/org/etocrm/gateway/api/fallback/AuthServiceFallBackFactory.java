package org.etocrm.gateway.api.fallback;

import com.alibaba.fastjson.JSON;
import feign.hystrix.FallbackFactory;
import org.etocrm.core.enums.ResponseEnum;
import org.etocrm.core.util.ResponseVO;
import org.etocrm.gateway.api.IAuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * @Author chengrong.yang
 * @date 2020/8/18 14:29
 */
@Component
public class AuthServiceFallBackFactory implements FallbackFactory<IAuthService> {

    private static final Logger log = LoggerFactory.getLogger(AuthServiceFallBackFactory.class);

    @Override
    public IAuthService create(Throwable throwable) {

        return  new IAuthService() {
            @Override
            public String permission(String authentication, String url){
                log.error(String.valueOf(ResponseEnum.TIMEOUT));
                return JSON.toJSONString(ResponseVO.error(ResponseEnum.TIMEOUT));
            }
        };
    }
}
