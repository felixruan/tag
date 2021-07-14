package org.etocrm.authentication.controller;

import org.etocrm.authentication.entity.VO.brands.SysBrandsListAllResponseVO;
import org.etocrm.authentication.service.ISysBrandsService;
import org.etocrm.core.util.ResponseVO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Author chengrong.yang
 * @Date 2020/11/13 15:14
 */
@RestController
@RequestMapping("/feign")
public class FeignController {

    @Resource
    private ISysBrandsService SysBrandsDOService;

    /**
     * 获取非系统品牌的品牌列表
     * @return
     */
    @GetMapping("/getListAll")
    public ResponseVO<List<SysBrandsListAllResponseVO>> getListAll() {
        return SysBrandsDOService.getListAll();
    }


//    @GetMapping("/sysBrands/getById/{id}")
//    public ResponseVO<SysBrandsGetResponseVO> getById(@PathVariable("id") Long id){
//        return SysBrandsDOService.getById(id);
//    }
}
