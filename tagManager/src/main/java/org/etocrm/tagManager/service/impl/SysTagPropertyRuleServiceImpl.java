package org.etocrm.tagManager.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.etocrm.core.enums.BusinessEnum;
import org.etocrm.core.enums.ResponseEnum;
import org.etocrm.core.util.BeanUtils;
import org.etocrm.core.util.DateUtil;
import org.etocrm.core.util.ResponseVO;
import org.etocrm.core.util.VoParameterUtils;
import org.etocrm.dynamicDataSource.util.BasePage;
import org.etocrm.tagManager.mapper.ISysTagPropertyRuleMapper;
import org.etocrm.tagManager.model.DO.SysTagPropertyRuleDO;
import org.etocrm.tagManager.model.VO.tagProperty.SysTagPropertyRuleVO;
import org.etocrm.tagManager.service.ISysTagPropertyRuleService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * <p>
 * 系统标签属性规则表  服务实现类
 * </p>
 */
@Service
@Slf4j
public class SysTagPropertyRuleServiceImpl extends ServiceImpl<ISysTagPropertyRuleMapper, SysTagPropertyRuleDO> implements ISysTagPropertyRuleService {

    @Resource
    private ISysTagPropertyRuleMapper sysTagPropertyRuleMapper;


    /**
     * @Description: 根据属性id删除
     **/
    @Override
    public ResponseVO deleteByPropertyId(Long propertyId) {
        try {
            SysTagPropertyRuleDO sysTagPropertyRuleDO = new SysTagPropertyRuleDO();
            sysTagPropertyRuleDO.setDeleted(BusinessEnum.DELETED.getCode());
            sysTagPropertyRuleMapper.update(sysTagPropertyRuleDO,
                    new LambdaUpdateWrapper<SysTagPropertyRuleDO>()
                            .eq(SysTagPropertyRuleDO::getPropertyId, propertyId)
            );
            return ResponseVO.success();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ResponseVO.error(ResponseEnum.DATA_REMOVE_ERROR);
        }
    }



    @Override
    public List<SysTagPropertyRuleDO> getTagPropertyRuleByTagId(Long propertyId) {
        QueryWrapper<SysTagPropertyRuleDO> query = new QueryWrapper<>();
        query.eq("property_id", propertyId);
        query.eq("is_delete", BusinessEnum.NOTDELETED);
        return sysTagPropertyRuleMapper.selectList(query);
    }

    @Override
    public ResponseVO<List<SysTagPropertyRuleDO>> getSysTagPropertyRuleIds(Set<Long> ids) {
        List<SysTagPropertyRuleDO> list = new ArrayList<>();
        for (Long id : ids) {
            List<SysTagPropertyRuleDO> sysTagPropertyRuleDOS = sysTagPropertyRuleMapper.selectList(
                    new LambdaQueryWrapper<SysTagPropertyRuleDO>()
                            .eq(SysTagPropertyRuleDO::getDeleted, BusinessEnum.NOTDELETED.getCode())
                            .eq(SysTagPropertyRuleDO::getPropertyId, id));
            list.addAll(sysTagPropertyRuleDOS);
        }
        return ResponseVO.success(list);
    }

    @Override
    public ResponseVO insert(SysTagPropertyRuleDO sysTagPropertyRuleDO) {
        sysTagPropertyRuleDO.setId(null);
        this.save(sysTagPropertyRuleDO);
        return ResponseVO.success(sysTagPropertyRuleDO.getId());
    }

    @Override
    public ResponseVO updateSysTagPropertyRulesById(Set<Long> ids) {
        SysTagPropertyRuleDO sysTagPropertyRuleDO = new SysTagPropertyRuleDO();
        sysTagPropertyRuleDO.setDeleted(BusinessEnum.DELETED.getCode());
        sysTagPropertyRuleMapper.update(sysTagPropertyRuleDO, new LambdaQueryWrapper<SysTagPropertyRuleDO>().in(SysTagPropertyRuleDO::getPropertyId, ids));
        return ResponseVO.success();
    }
}
