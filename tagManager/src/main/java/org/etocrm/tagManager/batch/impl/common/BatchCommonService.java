package org.etocrm.tagManager.batch.impl.common;

import cn.hutool.core.collection.CollectionUtil;
import lombok.extern.slf4j.Slf4j;
import org.etocrm.core.util.ResponseVO;
import org.etocrm.dynamicDataSource.util.RedisUtil;
import org.etocrm.tagManager.api.IAuthenticationService;
import org.etocrm.tagManager.constant.TagConstant;
import org.etocrm.tagManager.model.VO.batch.SysBrandsListAllResponseVO;
import org.etocrm.tagManager.service.ISysTagPropertyUserService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Slf4j
@Service
public class BatchCommonService {

    @Resource
    private IAuthenticationService authenticationService;

    @Resource
    private ISysTagPropertyUserService sysTagPropertyUserService;

    @Resource
    private RedisUtil redisUtil;

    public void setBrandToRedis(String redis) {
        //查询所有的品牌和机构
        ResponseVO<List<SysBrandsListAllResponseVO>> brandsResponseVO = authenticationService.getListAll();
        if (null != brandsResponseVO && 0 == brandsResponseVO.getCode() && CollectionUtil.isNotEmpty(brandsResponseVO.getData())) {
            List<SysBrandsListAllResponseVO> brandsData = brandsResponseVO.getData();
            for (SysBrandsListAllResponseVO brands : brandsData) {
                redisUtil.sSet(redis, brands.getId() + "," + brands.getOrgId());
            }
        }
//        redisUtil.sSet(redis, "3,2");
    }

    public void deleteTag(Long tagId, String tagType) {
        Boolean flag = false;
        Integer deleteNum = Integer.valueOf(redisUtil.getValueByKey(TagConstant.DELETE_NUMBER).toString());
        while (!flag) {
            int a = sysTagPropertyUserService.deleteByTagId(tagId, tagType, deleteNum);
            log.info("===删除影响条数===" + a);
            if (a == 0) {
                flag = true;
            }
        }
    }


}
