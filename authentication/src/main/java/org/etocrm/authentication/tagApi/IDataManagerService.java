package org.etocrm.authentication.tagApi;

import org.etocrm.authentication.entity.VO.tagBrands.SysTagBrandsInfoVO;
import org.etocrm.authentication.tagApi.fallback.DataManagerServiceFallBackFactory;
import org.etocrm.core.util.ResponseVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @Author chengrong.yang
 * @Date 2020/9/15 20:34
 */
@FeignClient(name = "tag-etocrm-dataManager-server", fallbackFactory = DataManagerServiceFallBackFactory.class)
public interface IDataManagerService {

    /**
     * 根据品牌信息查询是存在数据源
     *
     * @param sysTagBrandsInfoVO
     * @return
     */
    @PostMapping("/dataSource/existsDatasource")
    ResponseVO<Boolean> existsDatasourceByBrandsInfo(@RequestBody SysTagBrandsInfoVO sysTagBrandsInfoVO);
}
