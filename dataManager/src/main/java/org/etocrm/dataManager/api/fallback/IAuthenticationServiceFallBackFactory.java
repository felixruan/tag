package org.etocrm.dataManager.api.fallback;

import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.etocrm.core.enums.ResponseEnum;
import org.etocrm.core.util.ResponseVO;
import org.etocrm.dataManager.api.IAuthenticationService;
import org.etocrm.dataManager.model.VO.SysBrands.SysBrandsGetResponseVO;
import org.etocrm.dataManager.model.VO.SysBrands.SysBrandsListAllResponseVO;
import org.etocrm.dataManager.model.VO.SysBrands.SysBrandsOrgGetResponseVO;
import org.etocrm.dataManager.model.VO.dataSource.SySUserGetByIdResponseVO;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Date 2020/9/15 17:29
 */
@Slf4j
@Component
public class IAuthenticationServiceFallBackFactory implements FallbackFactory<IAuthenticationService> {

    @Override
    public IAuthenticationService create(Throwable throwable) {
        return new IAuthenticationService() {
            @Override
            public ResponseVO<SysBrandsGetResponseVO> getById(Long id) {
                log.error(String.valueOf(ResponseEnum.TIMEOUT));
                return ResponseVO.error(ResponseEnum.TIMEOUT);
            }

            @Override
            public ResponseVO<SySUserGetByIdResponseVO> getUserById(Integer id) {
                log.error(String.valueOf(ResponseEnum.TIMEOUT));
                return ResponseVO.error(ResponseEnum.TIMEOUT);
            }

//            @Override
//            public ResponseVO<SysBrandsOrgGetResponseVO> getOrgById(Long id) {
//                log.error(String.valueOf(ResponseEnum.TIMEOUT));
//                return ResponseVO.error(ResponseEnum.TIMEOUT);
//            }

            @Override
            public ResponseVO<List<SysBrandsListAllResponseVO>> getListAll() {
                log.error("getListAll:"+ResponseEnum.TIMEOUT);
                return ResponseVO.error(ResponseEnum.TIMEOUT);
            }

//            @Override
//            public ResponseVO<SysBrandsGetResponseVO> getBrandOrgById(Long id) {
//                log.error(String.valueOf(ResponseEnum.TIMEOUT));
//                return ResponseVO.error(ResponseEnum.TIMEOUT);
//            }
        };

    }
}
