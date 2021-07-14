package org.etocrm.canal.api;

import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.etocrm.canal.model.CanalDB;
import org.etocrm.core.enums.ResponseEnum;
import org.etocrm.core.util.ResponseVO;
import org.springframework.stereotype.Component;

/**
 * @Author: dkx
 * @Date: 9:28 2020/11/23
 * @Desc:
 */
@Slf4j
@Component
public class DataManagerServiceFallBackFactory implements FallbackFactory<IDataManagerService> {
    @Override
    public IDataManagerService create(Throwable throwable) {
        return new IDataManagerService() {
            @Override
            public ResponseVO synchronizationConfig(CanalDB canalDB) {
                log.error(String.valueOf(ResponseEnum.TIMEOUT));
                return ResponseVO.error(ResponseEnum.TIMEOUT);
            }
        };
    }
}
