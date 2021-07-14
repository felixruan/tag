package org.etocrm.authentication.tagApi.fallback;

import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.etocrm.authentication.entity.VO.tagBrands.SysTagBrandsInfoVO;
import org.etocrm.authentication.tagApi.IDataManagerService;
import org.etocrm.core.enums.ResponseEnum;
import org.etocrm.core.util.ResponseVO;
import org.springframework.stereotype.Component;

/**
 * @Author chengrong.yang
 * @Date 2020/9/15 20:36
 */
@Component
@Slf4j
public class DataManagerServiceFallBackFactory implements FallbackFactory<IDataManagerService> {


    @Override
    public IDataManagerService create(Throwable throwable) {
        return new IDataManagerService() {

            @Override
            public ResponseVO<Boolean> existsDatasourceByBrandsInfo(SysTagBrandsInfoVO sysTagBrandsInfoVO) {
                log.error("existsDatasourceByBrandsInfo:{}", ResponseEnum.TIMEOUT);
                return ResponseVO.error(ResponseEnum.TIMEOUT);
            }
        };
    }
}
