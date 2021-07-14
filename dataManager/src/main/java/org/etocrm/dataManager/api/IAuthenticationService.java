package org.etocrm.dataManager.api;


import org.etocrm.core.util.ResponseVO;
import org.etocrm.dataManager.api.fallback.IAuthenticationServiceFallBackFactory;
import org.etocrm.dataManager.model.VO.SysBrands.SysBrandsGetResponseVO;
import org.etocrm.dataManager.model.VO.SysBrands.SysBrandsListAllResponseVO;
import org.etocrm.dataManager.model.VO.SysBrands.SysBrandsOrgGetResponseVO;
import org.etocrm.dataManager.model.VO.dataSource.SySUserGetByIdResponseVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "tag-etocrm-authentication-server", fallbackFactory = IAuthenticationServiceFallBackFactory.class)
public interface IAuthenticationService {

    /**
     * 根据Id获取品牌
     */
    @GetMapping("/sysBrands/getById/{id}")
    ResponseVO<SysBrandsGetResponseVO> getById(@PathVariable("id") Long id);

    /**
     * 根据获取用户信息
     */
    @GetMapping("/auth/user/get/{id}")
    ResponseVO<SySUserGetByIdResponseVO> getUserById(@PathVariable("id") Integer id);


//    @GetMapping("/sysBrandsOrg/getById/{id}")
//    ResponseVO<SysBrandsOrgGetResponseVO> getOrgById(@PathVariable("id") Long id);
//
//    @GetMapping("/feign/sysBrands/getById/{id}")
//    ResponseVO<SysBrandsGetResponseVO> getBrandOrgById(@PathVariable("id") Long id);

    @GetMapping("/feign/getListAll")
    ResponseVO<List<SysBrandsListAllResponseVO>> getListAll();
}
