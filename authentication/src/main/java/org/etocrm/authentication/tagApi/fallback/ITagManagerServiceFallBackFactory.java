package org.etocrm.authentication.tagApi.fallback;

import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.etocrm.authentication.entity.DO.SysTagDO;
import org.etocrm.authentication.entity.DO.SysTagGroupRuleDO;
import org.etocrm.authentication.entity.DO.SysTagPropertyDO;
import org.etocrm.authentication.entity.DO.SysTagPropertyRuleDO;
import org.etocrm.authentication.entity.VO.GetTagNameRequestInfoVO;
import org.etocrm.authentication.entity.VO.brands.TagCountParam;
import org.etocrm.authentication.entity.VO.tagBrands.SysTagBrandsInfoVO;
import org.etocrm.authentication.entity.VO.tagBrands.SysTagClassesTreeVO;
import org.etocrm.authentication.tagApi.ITagManagerService;
import org.etocrm.authentication.tagApi.LifeCycelModelCountRequestVO;
import org.etocrm.core.enums.ResponseEnum;
import org.etocrm.core.util.ResponseVO;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

/**
 * @Author: dkx
 * @Date: 18:38 2020/9/25
 * @Desc:
 */
@Component
@Slf4j
public class ITagManagerServiceFallBackFactory implements FallbackFactory<ITagManagerService> {
    @Override
    public ITagManagerService create(Throwable throwable) {
        return new ITagManagerService() {
            @Override
            public ResponseVO<List<SysTagClassesTreeVO>> getTagClassesTree(Integer tagStatus, Integer status) {
                log.error("getTagClassesTree:{}", ResponseEnum.TIMEOUT);
                return ResponseVO.error(ResponseEnum.TIMEOUT);
            }

            @Override
            public ResponseVO<List<SysTagDO>> getSysTagList(SysTagBrandsInfoVO brandsInfoVO) {
                log.error("getSysTagList:{}", ResponseEnum.TIMEOUT);
                return ResponseVO.error(ResponseEnum.TIMEOUT);
            }

            @Override
            public ResponseVO<List<SysTagGroupRuleDO>> getGroupRuleByTagIds(SysTagBrandsInfoVO brandsInfoVO) {
                log.error("getGroupRuleByTagIds:{}", ResponseEnum.TIMEOUT);
                return ResponseVO.error(ResponseEnum.TIMEOUT);
            }

            @Override
            public ResponseVO<List<SysTagDO>> getTagByIds(Set<Long> ids) {
                log.error("getTagByIds:{}", ResponseEnum.TIMEOUT);
                return ResponseVO.error(ResponseEnum.TIMEOUT);
            }

            @Override
            public ResponseVO<List<SysTagPropertyDO>> getTagPropertyByTagIds(Set<Long> ids) {
                log.error("getTagPropertyByTagIds:{}", ResponseEnum.TIMEOUT);
                return ResponseVO.error(ResponseEnum.TIMEOUT);
            }

            @Override
            public ResponseVO<List<SysTagPropertyRuleDO>> getSysTagPropertyRuleIds(Set<Long> ids) {
                log.error("getSysTagPropertyRuleIds:{}", ResponseEnum.TIMEOUT);
                return ResponseVO.error(ResponseEnum.TIMEOUT);
            }

            @Override
            public ResponseVO saveSysTags(SysTagDO sysTagDO) {
                log.error("saveSysTags:{}", ResponseEnum.TIMEOUT);
                return ResponseVO.error(ResponseEnum.TIMEOUT);
            }

            @Override
            public ResponseVO saveSysTagPropertys(SysTagPropertyDO sysTagPropertyDO) {
                log.error("saveSysTagPropertys:{}", ResponseEnum.TIMEOUT);
                return ResponseVO.error(ResponseEnum.TIMEOUT);
            }

            @Override
            public ResponseVO saveSysTagPropertyRules(SysTagPropertyRuleDO sysTagPropertyRuleDO) {
                log.error("saveSysTagPropertyRules:{}", ResponseEnum.TIMEOUT);
                return ResponseVO.error(ResponseEnum.TIMEOUT);
            }

            @Override
            public ResponseVO updateTags(Set<Long> ids) {
                log.error("updateTags:{}", ResponseEnum.TIMEOUT);
                return ResponseVO.error(ResponseEnum.TIMEOUT);
            }

            @Override
            public ResponseVO updateSysTagPropertys(Set<Long> ids) {
                log.error("updateSysTagPropertys:{}", ResponseEnum.TIMEOUT);
                return ResponseVO.error(ResponseEnum.TIMEOUT);
            }

            @Override
            public ResponseVO updateSysTagPropertyRules(Set<Long> ids) {
                log.error("updateSysTagPropertyRules:{}", ResponseEnum.TIMEOUT);
                return ResponseVO.error(ResponseEnum.TIMEOUT);
            }

            @Override
            public ResponseVO<List<SysTagDO>> getTagByMasterIds(Set<Long> ids, SysTagBrandsInfoVO tagBrandsInfoVO) {
                log.error("getTagByMasterIds:{}", ResponseEnum.TIMEOUT);
                return ResponseVO.error(ResponseEnum.TIMEOUT);
            }

            @Override
            public ResponseVO<Integer> getTagCount(TagCountParam tagCountParam) {
                log.error("getTagCount:{}", ResponseEnum.TIMEOUT);
                return ResponseVO.error(ResponseEnum.TIMEOUT);
            }

            @Override
            public ResponseVO<Boolean> selectTagNameByTagId(GetTagNameRequestInfoVO requestInfoVO) {
                log.error("selectTagNameByTagId:{}", ResponseEnum.TIMEOUT);
                return ResponseVO.error(ResponseEnum.TIMEOUT);
            }

            @Override
            public ResponseVO getLifeCycleModelCount(LifeCycelModelCountRequestVO countRequestVO) {
                log.error("getLifeCycleModelCount:{}", ResponseEnum.TIMEOUT);
                return ResponseVO.error(ResponseEnum.TIMEOUT);
            }
        };
    }
}
