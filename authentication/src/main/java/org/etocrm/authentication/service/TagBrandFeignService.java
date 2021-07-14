package org.etocrm.authentication.service;

import org.etocrm.authentication.entity.VO.tagBrands.SysTagBrandsInfoVO;
import org.etocrm.core.util.ResponseVO;

import java.util.Set;

/**
 * @Author: dkx
 * @Date: 18:45 2020/9/25
 * @Desc:
 */
public interface TagBrandFeignService {

    ResponseVO<Set<Long>> getSysTagList(SysTagBrandsInfoVO brandsInfoVO);

    ResponseVO<Set<Long>> getGroupRuleByTagIds(SysTagBrandsInfoVO brandsInfoVO);

    ResponseVO<Set<Long>> getTagByIds(Set<Long> salse);
}
