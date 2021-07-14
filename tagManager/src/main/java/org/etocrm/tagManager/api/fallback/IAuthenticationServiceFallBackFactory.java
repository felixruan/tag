package org.etocrm.tagManager.api.fallback;

import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.etocrm.core.enums.ResponseEnum;
import org.etocrm.core.util.ResponseVO;
import org.etocrm.dynamicDataSource.model.DO.WoaapBrandsDO;
import org.etocrm.tagManager.api.IAuthenticationService;
import org.etocrm.tagManager.model.VO.SysBrandsGetResponseVO;
import org.etocrm.tagManager.model.VO.SysUserAllVO;
import org.etocrm.tagManager.model.VO.SysUserOutVO;
import org.etocrm.tagManager.model.VO.batch.SysBrandsListAllResponseVO;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: dkx
 * @Date: 16:17 2020/9/27
 * @Desc:
 */
@Slf4j
@Component
public class IAuthenticationServiceFallBackFactory implements FallbackFactory<IAuthenticationService> {
    @Override
    public IAuthenticationService create(Throwable throwable) {
        return new IAuthenticationService() {
            @Override
            public ResponseVO<SysUserOutVO> getUserById(Integer id) {
                log.error(String.valueOf(ResponseEnum.TIMEOUT));
                return ResponseVO.error(ResponseEnum.TIMEOUT);
            }

            @Override
            public ResponseVO<List<SysUserOutVO>> findUserAll(SysUserAllVO userVO) {
                log.error(String.valueOf(ResponseEnum.TIMEOUT));
                return ResponseVO.error(ResponseEnum.TIMEOUT);
            }

            @Override
            public ResponseVO deleteByTagId(Long tagId) {
                log.error(String.valueOf(ResponseEnum.TIMEOUT));
                return ResponseVO.error(ResponseEnum.TIMEOUT);
            }

            @Override
            public ResponseVO<List<SysBrandsListAllResponseVO>> getListAll() {
                log.error(String.valueOf(ResponseEnum.TIMEOUT));
                return ResponseVO.error(ResponseEnum.TIMEOUT);
            }

            @Override
            public ResponseVO<SysBrandsGetResponseVO> getById(Long id) {
                log.error(String.valueOf(ResponseEnum.TIMEOUT));
                return ResponseVO.error(ResponseEnum.TIMEOUT);
            }

            @Override
            public List<WoaapBrandsDO> getWoaapBrands(Long brandsId) {
                return new ArrayList<>();
            }
        };
    }
}
