package org.etocrm.tagManager.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.etocrm.core.enums.BusinessEnum;
import org.etocrm.core.enums.ResponseEnum;
import org.etocrm.core.util.BeanUtils;
import org.etocrm.core.util.ParamDeal;
import org.etocrm.core.util.ResponseVO;
import org.etocrm.core.util.VoParameterUtils;
import org.etocrm.dynamicDataSource.util.BasePage;
import org.etocrm.tagManager.api.IAuthenticationService;
import org.etocrm.tagManager.batch.impl.common.BatchLifeCycleCommonService;
import org.etocrm.tagManager.constant.LifeCycleConstant;
import org.etocrm.tagManager.enums.TagDictEnum;
import org.etocrm.tagManager.mapper.ISysLifeCycleModelMapper;
import org.etocrm.tagManager.model.DO.SysLifeCycleModelDO;
import org.etocrm.tagManager.model.DO.SysLifeCycleModelRuleDO;
import org.etocrm.tagManager.model.VO.SysBrandsGetResponseVO;
import org.etocrm.tagManager.model.VO.lifeCycleModel.*;
import org.etocrm.tagManager.service.ILifeCycleModelRuleService;
import org.etocrm.tagManager.service.ILifeCycleModelService;
import org.etocrm.tagManager.service.LifeCycelModelCountRequestVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class LifeCycleModelServiceImpl implements ILifeCycleModelService {

    @Autowired
    private ISysLifeCycleModelMapper sysLifeCycleModelMapper;

    @Autowired
    private ILifeCycleModelRuleService lifeCycleModelRuleService;

    @Autowired
    private BatchLifeCycleCommonService batchLifeCycleCommonService;

    @Autowired
    IAuthenticationService iAuthenticationService;

    /**
     * 查询list
     *
     * @param requestVO
     * @return
     */
    @Override
    public ResponseVO getList(QueryListRequestVO requestVO) {
        try {
            ParamDeal.setStringNullValue(requestVO);

            LambdaQueryWrapper<SysLifeCycleModelDO> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            if (StringUtils.isNotBlank(requestVO.getName())) {
                lambdaQueryWrapper.like(SysLifeCycleModelDO::getName, requestVO.getName());
            }
            Page<SysLifeCycleModelDO> page = new Page<>(VoParameterUtils.getCurrent(requestVO.getCurrent()), VoParameterUtils.getSize(requestVO.getSize()));
            IPage<SysLifeCycleModelDO> sysLifeCycleModelDOIPage = sysLifeCycleModelMapper.selectPage(page, lambdaQueryWrapper);
            BasePage basePage = new BasePage(sysLifeCycleModelDOIPage);
            List<QueryListResponseVO> transformation = this.transformation(basePage.getRecords());
            basePage.setRecords(transformation);
            return ResponseVO.success(basePage);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return ResponseVO.errorParams("查询失败");
    }

    private List<QueryListResponseVO> transformation(List<SysLifeCycleModelDO> records) {
        List list = new ArrayList();
        QueryListResponseVO queryListResponseVO;
        for (SysLifeCycleModelDO record : records) {
            queryListResponseVO = new QueryListResponseVO();
            BeanUtils.copyPropertiesIgnoreNull(record, queryListResponseVO);
            ResponseVO<SysBrandsGetResponseVO> byId = iAuthenticationService.getById(record.getBrandsId());
            if (byId.getCode() == 0 && byId.getData() != null) {
                queryListResponseVO.setBrandsName(byId.getData().getBrandsName());
                queryListResponseVO.setOrgName(byId.getData().getOrgName());
            }
            queryListResponseVO.setUpdateTypeName(updateTypeName(record.getUpdateType()));
            list.add(queryListResponseVO);
        }
        return list;
    }


    private String updateTypeName(Integer aa) {
        switch (aa) {
            case LifeCycleConstant.LIFE_CYCLE_UPDATE_TYPE_ONCE:
                return BusinessEnum.LIFE_CYCLE_UPDATE_TYPE_ONCE.getMessage();

            case LifeCycleConstant.LIFE_CYCLE_UPDATE_TYPE_DAILY:
                return BusinessEnum.LIFE_CYCLE_UPDATE_TYPE_DAILY.getMessage();

            case LifeCycleConstant.LIFE_CYCLE_UPDATE_TYPE_WEEKLY:
                return BusinessEnum.LIFE_CYCLE_UPDATE_TYPE_WEEKLY.getMessage();

            case LifeCycleConstant.LIFE_CYCLE_UPDATE_TYPE_MONTHLY:
                return BusinessEnum.LIFE_CYCLE_UPDATE_TYPE_MONTHLY.getMessage();
            default:
                break;
        }
        return "";
    }

    /**
     * 保存模型
     *
     * @param saveRequestVO
     * @return
     */
    @Override
    @Transactional
    public ResponseVO save(LifeCycelModelSaveRequestVO saveRequestVO) {

        /**
         * 1。check 名称是否重复
         * 2. check 更新频率value
         * 3. save model
         * 4. save rule
         */
        SysLifeCycleModelDO modelDO = new SysLifeCycleModelDO();
        BeanUtils.copyPropertiesIgnoreNull(saveRequestVO, modelDO);

        //验证品牌是否存在
        if (this.checkBrandsNotExists(saveRequestVO.getOrgId(), saveRequestVO.getBrandsId())) {
            return ResponseVO.errorParams("品牌信息不正确");
        }

        //2期限制只能有一个生命周期
        if (this.checkExists(modelDO)) {
            return ResponseVO.errorParams("只能添加一个生命周期模型哦");
        }
        // 2期只有一个模型，先注释名称校验
//            if (this.checkName(modelDO)) {
//                return ResponseVO.errorParams("名称已存在，请重新输入");
//            }

        if (this.checkUpdateValue(modelDO.getUpdateType(), modelDO.getUpdateValue())) {
            return ResponseVO.errorParams("请输入正确的更新频率值");
        }

        Long id = this.saveModel(modelDO);
        if (null == id) {
            return ResponseVO.errorParams("保存失败");
        }

        this.saveOrUpdateRule(id, saveRequestVO.getRule());

        return ResponseVO.success(id);
    }

    /**
     * 验证品牌是否不存在
     *
     * @param orgId
     * @param brandsId
     * @return true 不存在 ，false 存在
     */
    private boolean checkBrandsNotExists(Long orgId, Long brandsId) {
        ResponseVO<SysBrandsGetResponseVO> brandsInfo = iAuthenticationService.getById(brandsId);
        if (ResponseEnum.SUCCESS.getCode().equals(brandsInfo.getCode()) && null != brandsInfo.getData()) {
            return !orgId.equals(brandsInfo.getData().getOrgId());
        }
        return true;
    }

    private boolean checkExists(SysLifeCycleModelDO modelDO) {
        return sysLifeCycleModelMapper.selectCount(new LambdaQueryWrapper<SysLifeCycleModelDO>()
                .eq(SysLifeCycleModelDO::getOrgId, modelDO.getOrgId())
                .eq(SysLifeCycleModelDO::getBrandsId, modelDO.getBrandsId())
        ) > 0;
    }


    /**
     * 检查名称是否重复
     *
     * @param modelDO
     * @return true 重复  false 不重复
     */
    private boolean checkName(SysLifeCycleModelDO modelDO) {
        LambdaQueryWrapper<SysLifeCycleModelDO> eq = new LambdaQueryWrapper<SysLifeCycleModelDO>()
                .eq(SysLifeCycleModelDO::getOrgId, modelDO.getOrgId())
                .eq(SysLifeCycleModelDO::getBrandsId, modelDO.getBrandsId())
                .eq(SysLifeCycleModelDO::getName, modelDO.getName());
        if (null != modelDO.getId()) {
            eq.ne(SysLifeCycleModelDO::getId, modelDO.getId());
        }
        return sysLifeCycleModelMapper.selectCount(eq) > 0;
    }

    /**
     * 检查更新频率value
     */
    private boolean checkUpdateValue(Integer updateType, Integer updateValue) {
        if (BusinessEnum.LIFE_CYCLE_UPDATE_TYPE_WEEKLY.getCode().equals(updateType)) {

            if (null == updateValue || !Arrays.asList(LifeCycleConstant.WEEK).contains(updateValue)) {
                return true;
            }
        } else if (BusinessEnum.LIFE_CYCLE_UPDATE_TYPE_MONTHLY.getCode().equals(updateType)) {
            if (null == updateValue || !Arrays.asList(LifeCycleConstant.MONTH).contains(updateValue)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 保存模型
     *
     * @param modelDO
     * @return
     */
    private Long saveModel(SysLifeCycleModelDO modelDO) {
        modelDO.setRuleUpdateTime(DateTime.now());
        modelDO.setDataNextUpdateDate(DateUtil.beginOfDay(new Date()));

        sysLifeCycleModelMapper.insert(modelDO);
        return modelDO.getId();
    }


    /**
     * 保存模型规则
     *
     * @param modelId
     * @param
     */
    private void saveOrUpdateRule(Long modelId, List<LifeCycleModelRuleVO> ruleList) {
        List<SysLifeCycleModelRuleDO> ruleDOList = new ArrayList<>();
        List<String> modelRuleCode = Arrays.asList(LifeCycleConstant.MODEL_RULE_CODE);
        SysLifeCycleModelRuleDO modelRuleDO;
        for (LifeCycleModelRuleVO ruleVO : ruleList) {
            modelRuleDO = new SysLifeCycleModelRuleDO();
            BeanUtils.copyPropertiesIgnoreNull(ruleVO, modelRuleDO);

            modelRuleDO.setModelId(modelId);
            modelRuleDO.setStepOrder(modelRuleCode.indexOf(ruleVO.getStepCode()));
            modelRuleDO.setStepMemo(getRuleStepMemo(modelRuleDO.getStepOrder(), ruleVO.getRuleValue()));
            modelRuleDO.setStepRuleValue(JSON.toJSONString(ruleVO.getRuleValue()));

            ruleDOList.add(modelRuleDO);
        }

        lifeCycleModelRuleService.saveOrUpdateBatch(ruleDOList);
    }

    private String getRuleStepMemo(int stepOrder, LifeCycleModelRuleValueVO ruleValueVO) {
        switch (stepOrder) {
            case 0:
                return LifeCycleConstant.STEP_RULE_MEMO[0]
                        .replace("#{registerDay}", String.valueOf(ruleValueVO.getDay()));
            case 1:
                return LifeCycleConstant.STEP_RULE_MEMO[1]
                        .replace("#{orderDay}", String.valueOf(ruleValueVO.getDay()));
            case 2:
                return LifeCycleConstant.STEP_RULE_MEMO[2]
                        .replace("#{orderDay}", String.valueOf(ruleValueVO.getDay()));
            case 3:
            case 4:
                //根据logicId 获取名称
                if (TagDictEnum.LIFE_CYCLE_SITUATED_BETWEEN.getCode().equals(ruleValueVO.getLogicDictCode())) {
                    return LifeCycleConstant.STEP_RULE_MEMO[3]
                            .replace("#{orderDay1}", String.valueOf(ruleValueVO.getDayList().get(0)))
                            .replace("#{logicName}", TagDictEnum.LIFE_CYCLE_SITUATED_BETWEEN.getMessage())
                            .replace("#{startValue}", String.valueOf(ruleValueVO.getStartValue()))
                            .replace("#{endValue}", String.valueOf(ruleValueVO.getEndValue()))
                            .replace("#{orderDay2}", String.valueOf(ruleValueVO.getDayList().get(1)))
                            ;
                } else if (TagDictEnum.LIFE_CYCLE_GREATER_THAN.getCode().equals(ruleValueVO.getLogicDictCode())) {
                    return LifeCycleConstant.STEP_RULE_MEMO[4]
                            .replace("#{orderDay1}", String.valueOf(ruleValueVO.getDayList().get(0)))
                            .replace("#{logicName}", TagDictEnum.LIFE_CYCLE_GREATER_THAN.getMessage())
                            .replace("#{startValue}", String.valueOf(ruleValueVO.getStartValue()))
                            .replace("#{orderDay2}", String.valueOf(ruleValueVO.getDayList().get(1)))
                            ;
                }
                return "";
            case 5:
                return LifeCycleConstant.STEP_RULE_MEMO[5]
                        .replace("#{orderDay}", String.valueOf(ruleValueVO.getDay()));
            case 6:
                return LifeCycleConstant.STEP_RULE_MEMO[6]
                        .replace("#{orderDay}", String.valueOf(ruleValueVO.getDay()));
            default:
                return "";
        }
    }


    /**
     * 修改模型
     *
     * @param modifyRequestVO
     * @return
     */
    @Override
    public ResponseVO modify(LifeCycelModelModifyRequestVO modifyRequestVO) {
        try {
            /**
             * 1.检查模型是否存在
             * 2.检查品牌是否可以修改   *  已产生数据的模型，不能更改品牌
             * 3.检查名称是否重复
             * 4.检查updateValue
             * 5.修改
             */
            SysLifeCycleModelDO modelDO = this.getById(modifyRequestVO.getId());
            if (null == modelDO) {
                return ResponseVO.errorParams("编辑失败,模型不存在");
            }

            //*  已产生数据的模型，不能更改品牌
            if (!modelDO.getBrandsId().equals(modifyRequestVO.getBrandsId())) {
                if (null != modelDO.getDataUpdateTime()) {
                    //查看dataUpdateTime 有的话，不能修改
                    return ResponseVO.errorParams("编辑失败,已产生数据的模型，不能更改品牌");
                }

                //修改的品牌已经存在模型，也不能修改成此品牌
                SysLifeCycleModelDO byBrandsId = this.getByBrandsId(modifyRequestVO.getOrgId(), modifyRequestVO.getBrandsId());
                if (null != byBrandsId) {
                    return ResponseVO.errorParams("编辑失败，该品牌已存在生命周期模型");
                }
                //验证品牌是否存在
                if (this.checkBrandsNotExists(modifyRequestVO.getOrgId(), modifyRequestVO.getBrandsId())) {
                    return ResponseVO.errorParams("品牌信息不正确");
                }
            }

            // 2期只有一个模型，先注释名称校验
//            if (this.checkName(modelDO)) {
//                return ResponseVO.errorParams("名称已存在，请重新输入");
//            }

            if (this.checkUpdateValue(modifyRequestVO.getUpdateType(), modifyRequestVO.getUpdateValue())) {
                return ResponseVO.errorParams("请输入正确的更新频率值");
            }

            //ruleId 必传
            Set<Long> ruleIdSet = modifyRequestVO.getRule().stream().map(rule -> rule.getId()).collect(Collectors.toSet());
            if (ruleIdSet.size() != LifeCycleConstant.MODEL_RULE_CODE.length) {
                return ResponseVO.errorParams("参数错误");
            }
            boolean ruleChanged = this.getRuleChange(modifyRequestVO.getRule(),modelDO.getId());
            this.update(modifyRequestVO, modelDO,ruleChanged);

            if (ruleChanged){
                this.saveOrUpdateRule(modifyRequestVO.getId(), modifyRequestVO.getRule());
            }

            return ResponseVO.success();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return ResponseVO.errorParams("编辑失败");
    }

    private boolean getRuleChange(List<LifeCycleModelRuleVO> ruleList, Long id) {
        List<LifeCycleModelRuleVO> ruleListFind = this.getRuleList(id);
        return !ruleList.equals(ruleListFind);
    }

    private List<LifeCycleModelRuleVO> getRuleList(Long modelId){
        List<LifeCycleModelRuleVO> ruleListFind = new ArrayList<>();

        List<SysLifeCycleModelRuleDO> ruleDOListFind = batchLifeCycleCommonService.getRuleListByModelId(modelId);
        LifeCycleModelRuleVO ruleVO;
        LifeCycleModelRuleValueVO lifeCycleModelRuleValueVO;
        for (SysLifeCycleModelRuleDO ruleDO : ruleDOListFind) {
            ruleVO = new LifeCycleModelRuleVO();
            BeanUtils.copyPropertiesIgnoreNull(ruleDO, ruleVO);

            lifeCycleModelRuleValueVO = JSONObject.parseObject(ruleDO.getStepRuleValue(), LifeCycleModelRuleValueVO.class);
            ruleVO.setRuleValue(lifeCycleModelRuleValueVO);
            ruleListFind.add(ruleVO);
        }
        return ruleListFind;
    }

    private SysLifeCycleModelDO getByBrandsId(Long orgId, Long brandsId) {
        List<SysLifeCycleModelDO> modelDOList = sysLifeCycleModelMapper.selectList(Wrappers.lambdaQuery(SysLifeCycleModelDO.class)
                .eq(SysLifeCycleModelDO::getOrgId, orgId)
                .eq(SysLifeCycleModelDO::getBrandsId, brandsId)
        );
        if (CollUtil.isNotEmpty(modelDOList)) {
            return modelDOList.get(0);
        }
        return null;
    }

    private void update(LifeCycelModelModifyRequestVO modifyRequestVO, SysLifeCycleModelDO modelDO,boolean ruleChanged) {
        SysLifeCycleModelDO updateModelDO = new SysLifeCycleModelDO();
        BeanUtils.copyPropertiesIgnoreNull(modifyRequestVO, updateModelDO);

        if (ruleChanged){
            updateModelDO.setRuleUpdateTime(DateTime.now());
            updateModelDO.setDataNextUpdateDate(DateUtil.beginOfDay(new Date()));
        }

        LambdaUpdateWrapper<SysLifeCycleModelDO> updateWrapper = new LambdaUpdateWrapper();
        updateWrapper.eq(SysLifeCycleModelDO::getId, modifyRequestVO.getId());

        if (!updateModelDO.getUpdateType().equals(modelDO.getUpdateType())) {
            //如果更新频率发生了变化，需要更新下次执行时间
            if (!ruleChanged){
                updateWrapper.set(SysLifeCycleModelDO::getDataNextUpdateDate, batchLifeCycleCommonService.getDataNextUpdateDate(modifyRequestVO.getUpdateType(), modifyRequestVO.getUpdateValue(), modelDO.getDataUpdateTime()));
            }

            if (LifeCycleConstant.LIFE_CYCLE_UPDATE_TYPE_ONCE == updateModelDO.getUpdateType() || LifeCycleConstant.LIFE_CYCLE_UPDATE_TYPE_DAILY == updateModelDO.getUpdateType()) {
                updateWrapper.set(SysLifeCycleModelDO::getUpdateValue, null);
            }
        }

        sysLifeCycleModelMapper.update(updateModelDO, updateWrapper);
    }

    private SysLifeCycleModelDO getById(Long id) {
        return sysLifeCycleModelMapper.selectById(id);
    }

    @Override
    public ResponseVO<LifeCycelModelGetRuleResponseVO> getRule(Long id) {
        try {
            List<LifeCycleModelRuleResponseVO> lifeCycleModelRuleResponseVO = new ArrayList<>();
            LifeCycelModelGetRuleResponseVO lifeCycelModelGetRuleResponseVO = new LifeCycelModelGetRuleResponseVO();
            SysLifeCycleModelDO sysLifeCycleModelDO = sysLifeCycleModelMapper.selectById(id);
            BeanUtils.copyPropertiesIgnoreNull(sysLifeCycleModelDO, lifeCycelModelGetRuleResponseVO);
            ResponseVO<SysBrandsGetResponseVO> byId = iAuthenticationService.getById(sysLifeCycleModelDO.getBrandsId());
            if (byId.getCode() == 0 && byId.getData() != null) {
                lifeCycelModelGetRuleResponseVO.setBrandsName(byId.getData().getBrandsName());
                lifeCycelModelGetRuleResponseVO.setBrandsId(byId.getData().getId());
            }
            List<SysLifeCycleModelRuleDO> ruleListByModelId = batchLifeCycleCommonService.getRuleListByModelId(id);
            Long sumCount = 0L;
            for (SysLifeCycleModelRuleDO sysLifeCycleModelRuleDO : ruleListByModelId) {
                LifeCycleModelRuleResponseVO lifeCycleModelRule = new LifeCycleModelRuleResponseVO();
                BeanUtils.copyPropertiesIgnoreNull(sysLifeCycleModelRuleDO, lifeCycleModelRule);
                LifeCycleModelRuleValueVO lifeCycleModelRuleValueVO = JSONObject.parseObject(sysLifeCycleModelRuleDO.getStepRuleValue(), LifeCycleModelRuleValueVO.class);
                lifeCycleModelRule.setRuleValue(lifeCycleModelRuleValueVO);
                lifeCycleModelRuleResponseVO.add(lifeCycleModelRule);
                if (null != sysLifeCycleModelRuleDO.getCoveredCount()) {
                    sumCount = sumCount + sysLifeCycleModelRuleDO.getCoveredCount();
                }
            }
            lifeCycelModelGetRuleResponseVO.setLifeCycleModelRuleResponseVO(lifeCycleModelRuleResponseVO);
            lifeCycelModelGetRuleResponseVO.setSumRuleCount(sumCount);
            return ResponseVO.success(lifeCycelModelGetRuleResponseVO);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return ResponseVO.errorParams("查询失败");
    }

    @Override
    public ResponseVO delete(Long id) {
        try {
            if (null == id || id == 0) return ResponseVO.errorParams("删除主键必传！");
            SysLifeCycleModelDO sysLifeCycleModelDO = sysLifeCycleModelMapper.selectById(id);
            if (sysLifeCycleModelDO != null) {
                sysLifeCycleModelDO.setDeleted(BusinessEnum.DELETED.getCode());
                sysLifeCycleModelDO.setDeleteTime(new Date());
                sysLifeCycleModelMapper.updateById(sysLifeCycleModelDO);

                SysLifeCycleModelRuleDO sysLifeCycleModelRuleDO = new SysLifeCycleModelRuleDO();
                sysLifeCycleModelRuleDO.setDeleted(BusinessEnum.DELETED.getCode());
                lifeCycleModelRuleService.update(sysLifeCycleModelRuleDO, new LambdaQueryWrapper<SysLifeCycleModelRuleDO>()
                        .eq(SysLifeCycleModelRuleDO::getModelId, sysLifeCycleModelDO.getId()));
                return ResponseVO.success();
            } else {
                return ResponseVO.errorParams("数据不存在！");
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return ResponseVO.errorParams("删除失败");
    }

    @Override
    public ResponseVO getLifeCycleModelCount(LifeCycelModelCountRequestVO countRequestVO) {
        Integer count = sysLifeCycleModelMapper.selectCount(new LambdaQueryWrapper<SysLifeCycleModelDO>()
                .eq(SysLifeCycleModelDO::getOrgId, countRequestVO.getOrgId())
                .eq(SysLifeCycleModelDO::getBrandsId, countRequestVO.getBrandsId())
        );
        return ResponseVO.success(count);
    }

}
