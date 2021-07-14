package org.etocrm.tagManager.batch.impl.common;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.etocrm.core.enums.BusinessEnum;
import org.etocrm.core.util.ResponseVO;
import org.etocrm.dynamicDataSource.service.IDynamicService;
import org.etocrm.dynamicDataSource.util.RedisUtil;
import org.etocrm.tagManager.constant.LifeCycleConstant;
import org.etocrm.tagManager.constant.TagConstant;
import org.etocrm.tagManager.mapper.ISysLifeCycleModelMapper;
import org.etocrm.tagManager.mapper.ISysLifeCycleModelRuleMapper;
import org.etocrm.tagManager.model.DO.SysLifeCycleModelDO;
import org.etocrm.tagManager.model.DO.SysLifeCycleModelRuleDO;
import org.etocrm.tagManager.model.VO.lifeCycleModel.LifeCycleModelUserBatchSaveVO;
import org.etocrm.tagManager.model.VO.tag.TagBrandsInfoVO;
import org.etocrm.tagManager.service.AsyncServiceManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
public class BatchLifeCycleCommonService {

    @Autowired
    private ISysLifeCycleModelRuleMapper sysLifeCycleModelRuleMapper;

    @Autowired
    private ISysLifeCycleModelMapper sysLifeCycleModelMapper;

    @Autowired
    private IDynamicService dynamicService;

    @Autowired
    private AsyncServiceManager asyncServiceManager;

    @Autowired
    private RedisUtil redisUtil;

//    @Autowired
//    private IDataManagerService dataManagerService;

//    @Value("${ETL.DELETE.NUMBER}")
//    private int DELETETOTAL;
//
//    @Value("${ETL.WRITER.MAX_NUMBER}")
//    private int WRITER_MAX_NUMBER;


    public List<SysLifeCycleModelRuleDO> getRuleListByModelId(Long modelId) {
        return sysLifeCycleModelRuleMapper.selectList(new LambdaQueryWrapper<SysLifeCycleModelRuleDO>()
                .eq(SysLifeCycleModelRuleDO::getModelId, modelId)
                .orderByAsc(SysLifeCycleModelRuleDO::getStepOrder)
        );
    }

    public SysLifeCycleModelDO getModelByBrandInfo(TagBrandsInfoVO brandsInfo) {
        List<SysLifeCycleModelDO> modelDOList = sysLifeCycleModelMapper.selectList(new LambdaQueryWrapper<SysLifeCycleModelDO>()
                .eq(SysLifeCycleModelDO::getOrgId, brandsInfo.getOrgId())
                .eq(SysLifeCycleModelDO::getBrandsId, brandsInfo.getBrandsId())
        );
        if (CollectionUtil.isNotEmpty(modelDOList)) {
            return modelDOList.get(0);
        }
        return null;
    }

    public void updateExecutionInfo(SysLifeCycleModelDO modelDO) {
        // set dataUpdateTime;
        // 计算下次执行时间
        // 修改成执行结束   todo 确定要在这里执行吗？
        LambdaUpdateWrapper<SysLifeCycleModelDO> updateWrapper = Wrappers.lambdaUpdate(SysLifeCycleModelDO.class);
        updateWrapper.eq(SysLifeCycleModelDO::getId,modelDO.getId());

        updateWrapper.set(SysLifeCycleModelDO::getDataUpdateTime,DateTime.now());
        updateWrapper.set(SysLifeCycleModelDO::getDataUpdateStatus,BusinessEnum.END_EXECUTION.getCode());
        updateWrapper.set(SysLifeCycleModelDO::getDataNextUpdateDate,this.getDataNextUpdateDate(modelDO.getUpdateType(), modelDO.getUpdateValue(), modelDO.getDataUpdateTime()));
        sysLifeCycleModelMapper.update(null,updateWrapper);

    }

    public Date getDataNextUpdateDate(Integer updateType, Integer updateValue, Date dataUpdateTime) {
        //计算下次执行时间
        switch (updateType) {
            case LifeCycleConstant.LIFE_CYCLE_UPDATE_TYPE_ONCE:
                //不更新
                if (null == dataUpdateTime) {
                    return DateUtil.beginOfDay(new Date());
                }
                return null;
            case LifeCycleConstant.LIFE_CYCLE_UPDATE_TYPE_DAILY:
                //每天
                if (null == dataUpdateTime) {
                    return DateUtil.beginOfDay(new Date());
                }
                return DateUtil.offsetDay(DateUtil.beginOfDay(dataUpdateTime), 1);
            case LifeCycleConstant.LIFE_CYCLE_UPDATE_TYPE_WEEKLY:
                //每周
                return this.getWeeklyNextTime(updateValue, dataUpdateTime);
            case LifeCycleConstant.LIFE_CYCLE_UPDATE_TYPE_MONTHLY:
                //每月
                return this.getMonthlyNextTime(updateValue, dataUpdateTime);
            default:
                return null;
        }
    }

    private DateTime getWeeklyNextTime(Integer updateValue, Date dataUpdateTime) {
        DateTime today = DateUtil.beginOfDay(new Date());

        if (null == dataUpdateTime) {
            return today;
        }

        dataUpdateTime = DateUtil.beginOfDay(dataUpdateTime);

        DateTime beginOfWeek = DateUtil.beginOfWeek(new Date());
        if (null == dataUpdateTime || dataUpdateTime.before(beginOfWeek)) {
            dataUpdateTime = today;
        }

        // 上次执行时间 可能不是周几 每月1号 改成每周二
        int lastIndex = DateUtil.dayOfWeek(dataUpdateTime) - 2;
        if (lastIndex < 0) {
            lastIndex = 6;
        }
        int newIndex = Arrays.asList(LifeCycleConstant.WEEK).indexOf(updateValue);

        DateTime nextUpdateTime;
        if (newIndex <= lastIndex) {
            //不相等
            nextUpdateTime = DateUtil.offsetWeek(DateUtil.offsetDay(dataUpdateTime, (newIndex - lastIndex)), 1);
        } else {
            nextUpdateTime = DateUtil.offsetDay(dataUpdateTime, (newIndex - lastIndex));
        }

        return nextUpdateTime;
    }

    private DateTime getMonthlyNextTime(Integer updateValue, Date dataUpdateTime) {
        DateTime today = DateUtil.beginOfDay(new Date());

        if (null == dataUpdateTime) {
            return today;
        }
        DateTime endOfMonth = DateUtil.beginOfDay(DateUtil.endOfMonth(new Date()));
        DateTime beginOfMonth = DateUtil.beginOfMonth(new Date());

        int lastIndex = DateUtil.dayOfMonth(today) - 1;
        int newIndex = Arrays.asList(LifeCycleConstant.MONTH).indexOf(updateValue);

        if (newIndex <= lastIndex) {
            beginOfMonth = DateUtil.offsetMonth(beginOfMonth, 1);
            endOfMonth = DateUtil.beginOfDay(DateUtil.endOfMonth(beginOfMonth));
        }

        //所在月的最后一天
        int endOfMonthIndex = DateUtil.dayOfMonth(endOfMonth) - 1;
        DateTime nextUpdateTime;
        if (newIndex >= endOfMonthIndex) {
            nextUpdateTime = endOfMonth;
        } else {
            nextUpdateTime = DateUtil.offsetDay(beginOfMonth, updateValue);
        }
        return nextUpdateTime;
    }

    /**
     * 根据阶段规则id 删除对应的人群
     *
     * @param lifeCycleModelRuleId
     */
    public void deleteLifeCycleUserByRuleId(Long lifeCycleModelRuleId) {
        String tableName = "sys_life_cycle_model_user";
        String whereClause = " model_rule_id = " + lifeCycleModelRuleId + " limit " + Integer.valueOf(redisUtil.getValueByKey(TagConstant.DELETE_NUMBER).toString());
        boolean deleteFlag = true;
        while (deleteFlag) {
            int deleteCount = dynamicService.deleteRecord(tableName, whereClause);
            deleteFlag = deleteCount > 0;
        }
    }


    public ResponseVO batchSaveUser(LifeCycleModelUserBatchSaveVO batchSaveVO) {
        List<Long> userIdList = batchSaveVO.getUserIdList();
        if (CollectionUtil.isNotEmpty(userIdList)) {
            Collections.sort(userIdList);

            //拆分批次保存
            int limit = countStep(userIdList.size());
            List<List<Long>> userIdSpiltList = new ArrayList<>();
            Integer writerMaxNum = Integer.valueOf(redisUtil.getValueByKey(TagConstant.WRITER_MAX_NUMBER).toString());
            Stream.iterate(0, n -> n + 1).limit(limit).forEach(i -> {
                userIdSpiltList.add(userIdList.stream().skip(i * writerMaxNum).limit(writerMaxNum).collect(Collectors.toList()));
            });
            log.info("======= batchSaveUser,modelRuleId:{},size:{}", batchSaveVO.getModelRuleId(), userIdSpiltList.size());
            for (List<Long> userIdSpilt : userIdSpiltList) {
                //调用批量保存
                log.info("============= enter batchSaveUser,modelRuleId:{},    size:{}", batchSaveVO.getModelRuleId(), userIdSpilt.size());
                asyncServiceManager.asyncBatchSaveLifeCycleUser(batchSaveVO.getModelId(), batchSaveVO.getModelRuleId(), userIdSpilt);
            }
        }

        return ResponseVO.success();
    }

    /**
     * 计算切分次数
     */
    private Integer countStep(Integer size) {
        Integer writerMaxNum = Integer.valueOf(redisUtil.getValueByKey(TagConstant.WRITER_MAX_NUMBER).toString());
        return (size + writerMaxNum - 1) / writerMaxNum;
    }

    public void setModelRuleCoveredCount(Long modelRuleId, int coveredCount) {
        SysLifeCycleModelRuleDO ruleDO = new SysLifeCycleModelRuleDO();
        ruleDO.setId(modelRuleId);
        ruleDO.setCoveredCount(coveredCount);
        sysLifeCycleModelRuleMapper.updateById(ruleDO);
    }

//    public SysDictVO getDictByDictId(Long logicId){
//        ResponseVO<SysDictVO> detail = dataManagerService.detail(logicId);
//        if (null!=detail && ResponseEnum.SUCCESS.getCode().equals(detail.getCode())){
//            return detail.getData();
//        }
//        log.error("getDictByDictId error,response:{}",detail);
//        return null;
//    }
}
