package org.etocrm.tagManager.api;

import org.etocrm.core.util.ResponseVO;
import org.etocrm.dynamicDataSource.model.DO.WoaapBrandsDO;
import org.etocrm.tagManager.api.fallback.IAuthenticationServiceFallBackFactory;
import org.etocrm.tagManager.model.VO.SysBrandsGetResponseVO;
import org.etocrm.tagManager.model.VO.SysUserAllVO;
import org.etocrm.tagManager.model.VO.SysUserOutVO;
import org.etocrm.tagManager.model.VO.batch.SysBrandsListAllResponseVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "tag-etocrm-authentication-server", fallbackFactory = IAuthenticationServiceFallBackFactory.class)
public interface IAuthenticationService {


    @GetMapping("/auth/user/get/{id}")
    ResponseVO<SysUserOutVO> getUserById(@PathVariable("id") Integer id);

    /**
     * 根据用户名模糊查询用户列表
     *
     * @return
     */
    @PostMapping("/auth/user/findUserAll")
    ResponseVO<List<SysUserOutVO>> findUserAll(@RequestBody SysUserAllVO userVO);

    /**
     * 根据标签id删除标签行业
     *
     * @param tagId
     * @return
     */
    @GetMapping("/sysTagIndustry/deleteByTagId/{tagId}")
    ResponseVO deleteByTagId(@PathVariable Long tagId);


    @GetMapping("/feign/getListAll")
    ResponseVO<List<SysBrandsListAllResponseVO>> getListAll();

    /**
     * 根据Id获取品牌
     */
    @GetMapping("/sysBrands/getById/{id}")
    ResponseVO<SysBrandsGetResponseVO> getById(@PathVariable("id") Long id);

    @GetMapping("/sysBrands/getWoaapBrands")
    List<WoaapBrandsDO> getWoaapBrands(@RequestParam Long brandsId);

}
