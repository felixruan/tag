package org.etocrm.tagManager.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.etocrm.core.enums.BusinessEnum;
import org.etocrm.core.enums.ResponseEnum;
import org.etocrm.core.util.BeanUtils;
import org.etocrm.core.util.ParamDeal;
import org.etocrm.core.util.ResponseVO;
import org.etocrm.core.util.VoParameterUtils;
import org.etocrm.dynamicDataSource.util.BasePage;
import org.etocrm.dynamicDataSource.util.RandomUtil;
import org.etocrm.dynamicDataSource.util.RedisUtil;
import org.etocrm.kafkaServer.service.IKafkaProducerService;
import org.etocrm.tagManager.api.IAuthenticationService;
import org.etocrm.tagManager.api.IDataManagerService;
import org.etocrm.tagManager.constant.TagConstant;
import org.etocrm.tagManager.enums.TagDictEnum;
import org.etocrm.tagManager.enums.TagErrorMsgEnum;
import org.etocrm.tagManager.mapper.*;
import org.etocrm.tagManager.model.DO.*;
import org.etocrm.tagManager.model.VO.DictFindAllVO;
import org.etocrm.tagManager.model.VO.SysDictVO;
import org.etocrm.tagManager.model.VO.SysUserAllVO;
import org.etocrm.tagManager.model.VO.SysUserOutVO;
import org.etocrm.tagManager.model.VO.tag.TagBrandsInfoVO;
import org.etocrm.tagManager.model.VO.tagGroup.*;
import org.etocrm.tagManager.service.ITagGroupRuleService;
import org.etocrm.tagManager.service.ITagGroupUserService;
import org.etocrm.tagManager.util.ExcelUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class TagGroupServiceTransactionImpl extends ServiceImpl<ISysTagGroupMapper, SysTagGroupDO> {

    @Autowired
    private ISysTagGroupMapper sysTagGroupMapper;

    @Autowired
    private ISysTagGroupRuleMapper sysTagGroupRuleMapper;

    @Autowired
    private ITagGroupRuleService tagGroupRuleService;

    @Autowired
    private ISysTagMapper sysTagMapper;

    @Autowired
    private ISysTagPropertyMapper sysTagPropertyMapper;

    @Autowired
    private IDataManagerService dataManagerService;

    @Autowired
    private ITagGroupUserService tagGroupUserService;

    @Autowired
    private ISysTagPropertyUserMapper sysTagPropertyUserMapper;

    @Autowired
    private ISysTagGroupUserMapper sysTagGroupUserMapper;

    @Autowired
    private IAuthenticationService authenticationService;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private IKafkaProducerService producerService;

    @Autowired
    private RandomUtil randomUtil;

    @Value("${CUSTOM.KAFKA.TOPIC.TAG_GROUP_USER_TOPIC}")
    private String tagGroupUserTopic;


    /**
     * 添加群组
     *
     * @param tagGroupAddVO
     * @return
     */
    @Transactional(rollbackFor = {Exception.class})
    public ResponseVO addTagGroup(SysTagGroupAddVO tagGroupAddVO, TagBrandsInfoVO brandsInfo) {
        // 1.检查参数
        if (BusinessEnum.TAG_GROUP_START_ASSIGN_DATE.getCode().equals(tagGroupAddVO.getTagGroupStartType()) && null == tagGroupAddVO.getTagGroupStartTime()) {
            return ResponseVO.errorParams("请指定启动日期");
        }

        //动态群组指定静止日期需要大于当前时间或者自定义启动时间
        if (null != tagGroupAddVO.getTagGroupRestDate()) {
            if (BusinessEnum.TAG_GROUP_START_ASSIGN_DATE.getCode().equals(tagGroupAddVO.getTagGroupStartType()) && DateUtil.between(tagGroupAddVO.getTagGroupStartTime(), tagGroupAddVO.getTagGroupRestDate(), DateUnit.DAY, false) < 0) {
                return ResponseVO.errorParams("启动日期应在静止时间之前");
            }
            if (BusinessEnum.TAG_GROUP_START_IMMEDIATELY.getCode().equals(tagGroupAddVO.getTagGroupStartType()) && DateUtil.between(new Date(), tagGroupAddVO.getTagGroupRestDate(), DateUnit.DAY, false) < 0) {
                return ResponseVO.errorParams("静止时间应大于当前时间");
            }
        }

        //去除名称首尾空格
        tagGroupAddVO.setTagGroupName(tagGroupAddVO.getTagGroupName().trim());

        // 2.检查名称是否重复
        if (getByTagGroupNameCount(tagGroupAddVO.getTagGroupName(), null, brandsInfo) > 0) {
            return ResponseVO.errorParams(TagErrorMsgEnum.TAG_GROUP_NAME_EXISTS.getMessage());
        }

        // 3.add group 表
        SysTagGroupDO sysTagGroupDO = new SysTagGroupDO();
        BeanUtils.copyPropertiesIgnoreNull(tagGroupAddVO, sysTagGroupDO);
        // 新建的群组默认启动
        sysTagGroupDO.setTagGroupStatus(BusinessEnum.USING.getCode());
        sysTagGroupDO.setBrandsId(brandsInfo.getBrandsId());
        sysTagGroupDO.setOrgId(brandsInfo.getOrgId());
        sysTagGroupMapper.insert(sysTagGroupDO);

        return ResponseVO.success(sysTagGroupDO.getId());
    }

    /**
     * 修改群组
     *
     * @param tagGroupModifyVO
     * @return
     */
    @Transactional(rollbackFor = {Exception.class})
    public ResponseVO modifyTagGroup(SysTagGroupModifyVO tagGroupModifyVO, TagBrandsInfoVO brandsInfo) {
        // 检查入参
        if (BusinessEnum.TAG_GROUP_START_ASSIGN_DATE.getCode().equals(tagGroupModifyVO.getTagGroupStartType()) && null == tagGroupModifyVO.getTagGroupStartTime()) {
            return ResponseVO.errorParams("请指定启动日期");
        }

        //动态群组指定静止日期需要大于当前时间或者自定义启动时间
        if (null != tagGroupModifyVO.getTagGroupRestDate()) {
            if (BusinessEnum.TAG_GROUP_START_ASSIGN_DATE.getCode().equals(tagGroupModifyVO.getTagGroupStartType()) && DateUtil.between(tagGroupModifyVO.getTagGroupStartTime(), tagGroupModifyVO.getTagGroupRestDate(), DateUnit.DAY, false) < 0) {
                return ResponseVO.errorParams("启动日期应在静止时间之前");
            }
            if (BusinessEnum.TAG_GROUP_START_IMMEDIATELY.getCode().equals(tagGroupModifyVO.getTagGroupStartType()) && DateUtil.between(new Date(), tagGroupModifyVO.getTagGroupRestDate(), DateUnit.DAY, false) < 0) {
                return ResponseVO.errorParams("静止时间应大于当前时间");
            }
        }

        //检查群组是否存在
        SysTagGroupDO tagGroupDOFind = getTagGroup(tagGroupModifyVO.getId(), brandsInfo);
        if (null == tagGroupDOFind) {
            return ResponseVO.errorParams(TagErrorMsgEnum.TAG_GROUP_NOT_EXISTS.getMessage());
        }

        //检查依赖，是否可以修改
        if (checkModifyDepending(tagGroupModifyVO.getId())) {
            return ResponseVO.errorParams(TagErrorMsgEnum.TAG_GROUP_USING_UPDATE_FAILED.getMessage());
        }

        //去除名称首尾空格
        tagGroupModifyVO.setTagGroupName(tagGroupModifyVO.getTagGroupName().trim());

        //检查名称是否重复
        if (getByTagGroupNameCount(tagGroupModifyVO.getTagGroupName(), tagGroupModifyVO.getId(), brandsInfo) > 0) {
            return ResponseVO.errorParams(TagErrorMsgEnum.TAG_GROUP_NAME_EXISTS.getMessage());
        }

        //update group 表
        LambdaUpdateWrapper<SysTagGroupDO> updateWrapper = new LambdaUpdateWrapper<SysTagGroupDO>();
        updateWrapper.eq(SysTagGroupDO::getId, tagGroupModifyVO.getId());

        SysTagGroupDO sysTagGroupDO = new SysTagGroupDO();
        BeanUtils.copyPropertiesIgnoreNull(tagGroupModifyVO, sysTagGroupDO);
        sysTagGroupDO.setBrandsId(brandsInfo.getBrandsId());
        sysTagGroupDO.setOrgId(brandsInfo.getOrgId());
        //立即启动的把启动时间置空
        if (BusinessEnum.TAG_GROUP_START_IMMEDIATELY.getCode().equals(tagGroupModifyVO.getTagGroupStartType())) {
            updateWrapper.set(SysTagGroupDO::getTagGroupStartTime, null);
        }

        boolean calculate = false;
        // TODO: 2020/9/25  修改群组基础信息的时候重新跑数据
        //  (修改了 启动类型 ) & 启用  & 立即启动  &  有规则  重新跑数据
        if (!tagGroupDOFind.getTagGroupStartType().equals(sysTagGroupDO.getTagGroupStartType())
                && BusinessEnum.USING.getCode().equals(tagGroupDOFind.getTagGroupStatus())
//                && BusinessEnum.TAG_GROUP_TYPE_STATIC.getCode().equals(sysTagGroupDO.getTagGroupType())
                && BusinessEnum.TAG_GROUP_START_IMMEDIATELY.getCode().equals(sysTagGroupDO.getTagGroupStartType())
                && getRuleCount(tagGroupModifyVO.getId()) > 0) {

            // 群组set 成计算中
            SysTagGroupCountUserInfo countUserInfo = new SysTagGroupCountUserInfo();
            countUserInfo.dealCountUserInfo();
            sysTagGroupDO.setCountUserInfo(JSON.toJSONString(countUserInfo));

            calculate = true;
        }

        sysTagGroupMapper.update(sysTagGroupDO, updateWrapper);
        if (calculate) {
            //异步计算
            producerService.sendMessage(tagGroupUserTopic, String.valueOf(sysTagGroupDO.getId()), randomUtil.getRandomIndex());
        }
        return ResponseVO.success();

    }

    /**
     * 查询群组规则
     *
     * @param tagGroupId
     * @return
     */
    private Integer getRuleCount(Long tagGroupId) {
        return sysTagGroupRuleMapper.selectCount(new LambdaQueryWrapper<SysTagGroupRuleDO>()
                .eq(SysTagGroupRuleDO::getTagGroupId, tagGroupId)
                .eq(SysTagGroupRuleDO::getDeleted, BusinessEnum.NOTDELETED.getCode()));
    }

    /**
     * 复制群组
     *
     * @param tagGroupId
     * @return
     */
    @Transactional(rollbackFor = {Exception.class})
    public ResponseVO copyTagGroup(Long tagGroupId, TagBrandsInfoVO brandsInfo) {
        SysTagGroupDO srcSysTagGroupDO = getTagGroup(tagGroupId, brandsInfo);
        if (null == srcSysTagGroupDO) {
            return ResponseVO.errorParams(TagErrorMsgEnum.TAG_GROUP_NOT_EXISTS.getMessage());
        }
        String newTagGroupName = srcSysTagGroupDO.getTagGroupName() + "-副本";
        if (newTagGroupName.length() > 20) {
            return ResponseVO.errorParams("即将自动生成的 " + newTagGroupName + " 超过长度限制，请先修改名字，再进行保存！");
        }
        List<SysTagGroupDO> sysTagGroupDOS = sysTagGroupMapper.selectList(new LambdaQueryWrapper<SysTagGroupDO>()
                .eq(SysTagGroupDO::getTagGroupName, newTagGroupName)
                .eq(SysTagGroupDO::getOrgId, brandsInfo.getOrgId())
                .eq(SysTagGroupDO::getBrandsId, brandsInfo.getBrandsId())
        );

        if (CollUtil.isNotEmpty(sysTagGroupDOS)) {
            return ResponseVO.errorParams("即将自动生成的 " + newTagGroupName + " 已经存在，请先修改名字，再进行保存！");
        }
        //2.copy 群组name 后面加 -副本
        SysTagGroupDO targetSysTagGroupDO = new SysTagGroupDO();
        BeanUtils.copyPropertiesIgnoreNull(srcSysTagGroupDO, targetSysTagGroupDO);
        targetSysTagGroupDO.setId(null);
        targetSysTagGroupDO.setTagGroupName(targetSysTagGroupDO.getTagGroupName() + "-副本");

        //人群信息复制中
        if (StringUtils.isNotBlank(targetSysTagGroupDO.getCountUserInfo())) {
            SysTagGroupCountUserInfo countUserInfo = new SysTagGroupCountUserInfo();
            countUserInfo.copyCountUserInfo();
            targetSysTagGroupDO.setCountUserInfo(JSON.toJSONString(countUserInfo));
        }

        //复制的群组 只复制分组规则，不复制 分组数量
        targetSysTagGroupDO.setTagGroupSplitCount(0L);
        targetSysTagGroupDO.setSonCountInfo(null);
        sysTagGroupMapper.insert(targetSysTagGroupDO);

        //3.获取源群组规则
        List<SysTagGroupRuleRequestVO> srcRule = tagGroupRuleService.getCopyRule(tagGroupId);

        //4.srcRule to targetRule and save rule
        this.saveGroupRule(targetSysTagGroupDO, srcRule, false);
        Long targetGroupId = targetSysTagGroupDO.getId();
        //5 保存群组人员数据   todo 改成分批保存
        List<SysTagGroupUserDO> groupUserDOList = sysTagGroupUserMapper.selectList(new LambdaQueryWrapper<SysTagGroupUserDO>()
                .select(SysTagGroupUserDO::getUserId)
                .eq(SysTagGroupUserDO::getTagGroupId, tagGroupId));
        List<SysTagGroupUserPO> groupUserPOList = groupUserDOList.stream().map(item -> {
            SysTagGroupUserPO userPO = new SysTagGroupUserPO();
            userPO.setUserId(item.getUserId());
            userPO.setTagGroupId(targetGroupId);
            return userPO;
        }).collect(Collectors.toList());

        if (CollUtil.isNotEmpty(groupUserPOList)) {
            tagGroupUserService.asyncSaveBatchGroupUser(targetGroupId, groupUserPOList);
        }
        return ResponseVO.success();
    }

    @Transactional(rollbackFor = {Exception.class})
    public ResponseVO updateStatus(SysTagGroupUpdateStatusVO updateStatusVO, TagBrandsInfoVO brandsInfo) {

        //1.检查群组是否存在
        SysTagGroupDO sysTagGroupDOFind = getTagGroup(updateStatusVO.getTagGroupId(), brandsInfo);
        if (null == sysTagGroupDOFind) {
            return ResponseVO.errorParams(TagErrorMsgEnum.TAG_GROUP_NOT_EXISTS.getMessage());
        }
        if (sysTagGroupDOFind.getTagGroupStatus().equals(BusinessEnum.USING.getCode()) && BusinessEnum.NOTUSE.getCode().equals(updateStatusVO.getTagGroupStatus())) {
            //2.检查依赖
            if (checkModifyDepending(updateStatusVO.getTagGroupId())) {
                return ResponseVO.errorParams(TagErrorMsgEnum.TAG_GROUP_USING_UPDATE_FAILED.getMessage());
            }
        }

        SysTagGroupDO sysTagGroupDO = new SysTagGroupDO();
        sysTagGroupDO.setId(updateStatusVO.getTagGroupId());

        boolean calculate = false;
        // TODO: 2020/9/25 改成启用状态  立即执行的  有规则的 重跑数据
        if (!sysTagGroupDOFind.getTagGroupStatus().equals(updateStatusVO.getTagGroupStatus())
                && BusinessEnum.USING.getCode().equals(updateStatusVO.getTagGroupStatus())
                && BusinessEnum.TAG_GROUP_START_IMMEDIATELY.getCode().equals(sysTagGroupDOFind.getTagGroupStartType())
                && getRuleCount(updateStatusVO.getTagGroupId()) > 0
        ) {
            //todo 2021/1/8 如果群组改成启用  判断所引用标签的状态
            List<SysTagGroupRuleDO> sysTagGroupRuleDOS = sysTagGroupRuleMapper.selectList(new LambdaQueryWrapper<SysTagGroupRuleDO>()
                    .eq(SysTagGroupRuleDO::getTagGroupId, sysTagGroupDOFind.getId())
                    .eq(SysTagGroupRuleDO::getDeleted, BusinessEnum.NOTDELETED.getCode())
            );
            if (CollectionUtil.isNotEmpty(sysTagGroupRuleDOS)) {
                Set<Long> collect = sysTagGroupRuleDOS.stream().map(SysTagGroupRuleDO::getTagId).collect(Collectors.toSet());
                if (!collect.isEmpty()) {
                    //如果标签是中间状态，则提示不能操作。
                    List<SysTagDO> sysTagDOS = sysTagMapper.selectBatchIds(collect);
                    for (SysTagDO sysTagDO : sysTagDOS) {
                        if (sysTagDO.getTagPropertyChangeExecuteStatus().equals(BusinessEnum.RULE_UNEXECUTED.getCode())) {
                            sysTagGroupDO.setTagGroupRuleChangeExecuteStatus(BusinessEnum.UNEXECUTED.getCode());
                            sysTagGroupMapper.updateById(sysTagGroupDO);
                            return ResponseVO.errorParams("此" + sysTagDO.getTagName() + "标签已被修改，不能更新状态！");
                        }
                    }
                }
            }
            // 群组set 成计算中
            SysTagGroupCountUserInfo countUserInfo = new SysTagGroupCountUserInfo();
            countUserInfo.dealCountUserInfo();
            sysTagGroupDO.setCountUserInfo(JSON.toJSONString(countUserInfo));
            calculate = true;
        }

        sysTagGroupDO.setTagGroupStatus(updateStatusVO.getTagGroupStatus());
        sysTagGroupMapper.updateById(sysTagGroupDO);

        if (calculate) {
            producerService.sendMessage(tagGroupUserTopic, String.valueOf(updateStatusVO.getTagGroupId()), randomUtil.getRandomIndex());
        }
        return ResponseVO.success();
    }

    /**
     * 分页查询标签群组列表
     *
     * @param queryRequestVO
     * @return
     */
    public ResponseVO<BasePage<ListPageSysTagGroupQueryResponseVO>> getListByPage(SysTagGroupQueryRequestVO queryRequestVO, TagBrandsInfoVO brandsInfo) throws IllegalAccessException {
        log.info("=============enter,beginTime:{}", System.currentTimeMillis());
        ParamDeal.setStringNullValue(queryRequestVO);
        IPage<SysTagGroupDO> page = new Page<>(VoParameterUtils.getCurrent(queryRequestVO.getCurrent()), VoParameterUtils.getSize(queryRequestVO.getSize()));

        LambdaQueryWrapper<SysTagGroupDO> objectLambdaQueryWrapper = new LambdaQueryWrapper<>();
        objectLambdaQueryWrapper.eq(SysTagGroupDO::getOrgId, brandsInfo.getOrgId())
                .eq(SysTagGroupDO::getBrandsId, brandsInfo.getBrandsId());

        if (StringUtils.isNotBlank(queryRequestVO.getTagGroupName())) {
            objectLambdaQueryWrapper.like(SysTagGroupDO::getTagGroupName, queryRequestVO.getTagGroupName());
        }
        if (StringUtils.isNotBlank(queryRequestVO.getCreatedByName())) {
            List createUserIdList = null;
            SysUserAllVO sysUserAllVO = new SysUserAllVO();
            sysUserAllVO.setUserName(queryRequestVO.getCreatedByName());
            ResponseVO<List<SysUserOutVO>> userAll = authenticationService.findUserAll(sysUserAllVO);
            if (userAll.getCode() != 0) {
                return ResponseVO.errorParams(TagErrorMsgEnum.SELECT_ERROR.getMessage());
            }
            if (CollUtil.isEmpty(userAll.getData())) {
                //do 没有数据
                return ResponseVO.success(new BasePage<>(new ArrayList<>()));
            }
            createUserIdList = userAll.getData().stream().map(user -> user.getId()).collect(Collectors.toList());
            objectLambdaQueryWrapper.in(SysTagGroupDO::getCreatedBy, createUserIdList);
        }
        if (null != queryRequestVO.getStartTime()) {
            objectLambdaQueryWrapper.ge(SysTagGroupDO::getCreatedTime, queryRequestVO.getStartTime());
        }
        // 如果传了结束日期，日期+1 .目前前端传的到 到日期
        if (null != queryRequestVO.getEndTime()) {
            //日期加1
            Date endTime = DateUtil.offsetDay(queryRequestVO.getEndTime(), 1);
            objectLambdaQueryWrapper.lt(SysTagGroupDO::getCreatedTime, endTime);
        }
        if (null != queryRequestVO.getTagGroupType()) {
            objectLambdaQueryWrapper.eq(SysTagGroupDO::getTagGroupType, queryRequestVO.getTagGroupType());
        }
        if (null != queryRequestVO.getTagGroupStatus()) {
            objectLambdaQueryWrapper.eq(SysTagGroupDO::getTagGroupStatus, queryRequestVO.getTagGroupStatus());
        }
        objectLambdaQueryWrapper.eq(SysTagGroupDO::getDeleted, BusinessEnum.NOTDELETED.getCode())
                .orderByDesc(SysTagGroupDO::getId);
        log.info("=============enter,before select time:{}", System.currentTimeMillis());
        IPage<SysTagGroupDO> sysTagClassesDOIPage = sysTagGroupMapper.selectPage(page, objectLambdaQueryWrapper);
        log.info("=============enter,after select time:{}", System.currentTimeMillis());
        List<ListPageSysTagGroupQueryResponseVO> list = new ArrayList<>();
        List<SysTagGroupDO> records = sysTagClassesDOIPage.getRecords();

        ListPageSysTagGroupQueryResponseVO tagGroupQueryRequestVO;
        for (SysTagGroupDO record : records) {
            tagGroupQueryRequestVO = this.dealTagGroupInfo(record);

            list.add(tagGroupQueryRequestVO);
        }
        log.info("=============enter,for end select time:{}", System.currentTimeMillis());

        BasePage<ListPageSysTagGroupQueryResponseVO> objectBasePage = new BasePage<>(sysTagClassesDOIPage);
        objectBasePage.setRecords(list);
        return ResponseVO.success(objectBasePage);
    }

    private ListPageSysTagGroupQueryResponseVO dealTagGroupInfo(SysTagGroupDO record) {
        ListPageSysTagGroupQueryResponseVO tagGroupQueryRequestVO = new ListPageSysTagGroupQueryResponseVO();
        BeanUtils.copyPropertiesIgnoreNull(record, tagGroupQueryRequestVO);

        //覆盖人数
        String countUserInfoStr = record.getCountUserInfo();
        if (StringUtils.isNotBlank(countUserInfoStr)) {
            SysTagGroupCountUserInfo countUserInfoObj = JSON.parseObject(countUserInfoStr, SysTagGroupCountUserInfo.class);
            if (null != countUserInfoObj) {
                tagGroupQueryRequestVO.setCountUser(countUserInfoObj.getCountUser());
                tagGroupQueryRequestVO.setCountMemberId(countUserInfoObj.getCountMemberId());
                tagGroupQueryRequestVO.setCountMobileId(countUserInfoObj.getCountMobileId());
                tagGroupQueryRequestVO.setCountUnionID(countUserInfoObj.getCountUnionID());
            }
        }

        //子群组数量
        tagGroupQueryRequestVO.setSonCountUser(record.getTagGroupSplitCount());

        // 子群组信息
        String sonCountInfoStr = record.getSonCountInfo();
        if (StringUtils.isNotBlank(sonCountInfoStr)) {
            tagGroupQueryRequestVO.setSonCountUserInfo(JSON.parseArray(sonCountInfoStr, SysTagGroupSonUserInfo.class));
        }

        //创建信息
        tagGroupQueryRequestVO.setCreatedByName(getUserName(record.getCreatedBy()));
        tagGroupQueryRequestVO.setCreateTime(DateUtil.format(record.getCreatedTime(), DatePattern.NORM_DATETIME_PATTERN));
        tagGroupQueryRequestVO.setUpdatedTime(DateUtil.format(record.getUpdatedTime(), DatePattern.NORM_DATETIME_PATTERN));

        return tagGroupQueryRequestVO;
    }

    private String getUserName(Integer userId) {
        if (null != userId) {
            ResponseVO<SysUserOutVO> userById = authenticationService.getUserById(userId);
            if (null != userById && null != userById.getData()) {
                return (userById.getData()).getUserName();
            }
        }
        return "";
    }

    /**
     * 预估人数
     *
     * @param predictVO
     * @return
     */
    public ResponseVO predict(SysTagGroupPredictVO predictVO) {
        PredictResponseVO resultData = new PredictResponseVO();

        //根据运算id 获取运算code
        HashMap<Long, String> relationshipMap = getDictCodeMap(TagDictEnum.TAG_RELATIONAL_OPERATION.getCode());
        HashMap<Long, String> logicalOperationMap = getDictCodeMap(TagDictEnum.GROUP_OPERATORS.getCode());
        if (relationshipMap.keySet().size() < 1 || logicalOperationMap.keySet().size() < 1) {
            return ResponseVO.errorParams("预估失败，请重试");
        }
        //群组间的 且/或
        String groupRelationCode = relationshipMap.get(predictVO.getTagGroupRuleRelationshipId());
        //总的人群list
        Set<Long> userIdList = new HashSet<>();
        Set<Long> groupInnerUserIdList;
        //每个群组内的人群list
        List<SysTagGroupRuleRequestVO> ruleRequestVOS = predictVO.getRule();
        int groupCount = 0;
        for (SysTagGroupRuleRequestVO ruleVO : ruleRequestVOS) {
            //已经执行过 且 是且关系的 的，没有拿到人群 就不用继续执行。
            if (groupCount > 1 && StringUtils.equals(TagDictEnum.TAG_RELATIONAL_OPERATION_AND.getCode(), groupRelationCode) && CollUtil.isEmpty(userIdList)) {
                resultData.setCount(0);
                return ResponseVO.success(resultData);
            }

            //群组内的且/或code
            String groupInnerRelationshipCode = relationshipMap.get(ruleVO.getTagGroupRuleRelationshipId());
            if (StrUtil.isBlank(groupRelationCode)) {
                return ResponseVO.errorParams("预估失败，请重试");
            }
            groupInnerUserIdList = this.getUserIdListByRuleVOList(ruleVO.getTagGroupRule(), groupInnerRelationshipCode, logicalOperationMap);

            //群组间的数据且/或
            if (0 == groupCount) {
                userIdList = groupInnerUserIdList;
            } else {
                dealListByRelationCode(userIdList, groupInnerUserIdList, groupRelationCode);
            }

            groupCount++;
        }
        resultData.setCount(userIdList.size());
        return ResponseVO.success(resultData);
    }

    /**
     * 删除群组
     *
     * @param tagGroupId
     * @return
     */
    @Transactional(rollbackFor = {Exception.class})
    public ResponseVO deleteTagGroup(Long tagGroupId, TagBrandsInfoVO brandsInfo) {
        //检查群组是否存在
        SysTagGroupDO tagGroupDOFind = this.getTagGroup(tagGroupId, brandsInfo);
        if (null == tagGroupDOFind) {
            return ResponseVO.errorParams(TagErrorMsgEnum.TAG_GROUP_NOT_EXISTS.getMessage());
        }

        //检查依赖
        if (checkDeleteDepending(tagGroupId)) {
            return ResponseVO.errorParams(TagErrorMsgEnum.TAG_GROUP_USING_DELETE_FAILED.getMessage());
        }

        //删除群组
        SysTagGroupDO sysTagGroupDO = new SysTagGroupDO();
        sysTagGroupDO.setId(tagGroupId);
        sysTagGroupDO.setDeleted(BusinessEnum.DELETED.getCode());
        sysTagGroupMapper.updateById(sysTagGroupDO);

        //删除群组规则
        SysTagGroupRuleDO ruleDO = new SysTagGroupRuleDO();
        ruleDO.setDeleted(BusinessEnum.DELETED.getCode());
        sysTagGroupRuleMapper.update(ruleDO, new LambdaUpdateWrapper<SysTagGroupRuleDO>()
                .eq(SysTagGroupRuleDO::getTagGroupId, tagGroupId)
        );
        return ResponseVO.success();
    }


    /**
     * 更新群组名称
     *
     * @param updateNameVO
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ResponseVO updateTagGroupName(SysTagGroupUpdateNameVO updateNameVO, TagBrandsInfoVO brandsInfo) {
        //去除名称首尾空格
        updateNameVO.setTagGroupName(updateNameVO.getTagGroupName().trim());

        //检查名称是否重复
        if (getByTagGroupNameCount(updateNameVO.getTagGroupName(), updateNameVO.getTagGroupId(), brandsInfo) > 0) {
            return ResponseVO.errorParams(TagErrorMsgEnum.TAG_GROUP_NAME_EXISTS.getMessage());
        }

        SysTagGroupDO sysTagGroupDO = new SysTagGroupDO();
        sysTagGroupDO.setTagGroupName(updateNameVO.getTagGroupName());
        sysTagGroupDO.setId(updateNameVO.getTagGroupId());
        sysTagGroupDO.setOrgId(brandsInfo.getOrgId());
        sysTagGroupDO.setBrandsId(brandsInfo.getBrandsId());
        int updateCount = sysTagGroupMapper.updateById(sysTagGroupDO);
        if (updateCount > 0) {
            return ResponseVO.success();
        }
        return ResponseVO.errorParams("更新失败");
    }

    public ResponseVO downLoadMemberPackage(String json, TagBrandsInfoVO brandsInfoVO) {
        try {

            //  获取模板
            //    SysDictVO sysDictVOParam = new SysDictVO();
            //    sysDictVOParam.setDictParentId(excelModel);
            //   ResponseVO<List<SysDictVO>> dictResponse = dataManagerService.findAll(sysDictVOParam);
            //这是测试方法
           /* ExcelUtil.noModelWrite();
            if(0 == dictResponse.getCode()){
                List<SysDictVO> list = dictResponse.getData();
                List<SysDictVO> newList = list.stream().sorted(Comparator.comparing(SysDictVO::getOrderNumber)).collect(Collectors.toList());
                List<String> columnList = new ArrayList<String>();
                List<List<String>> headList = new ArrayList<List<String>>();
                for(SysDictVO sysDictVO : newList){
                    List<String> head = new ArrayList<String>();
                    head.add(sysDictVO.getDictName());
                    headList.add(head);
                    columnList.add(sysDictVO.getDictValue());
                }*/
            JSONObject excelObj = JSON.parseObject(json);
            Object tagGroupIdObj = excelObj.get("tagGroupId");
            if (null == tagGroupIdObj) {
                return ResponseVO.errorParams("群组id不能为空");
            }
            //todo 检查群组是否存在 ?? 是否需要
            Long tagGroupId = Long.parseLong(tagGroupIdObj.toString());

            //获取生成excel的数据
            Map<String, List> map = tagGroupUserService.getUsersDetailToExecl(excelObj, BusinessEnum.TAG_GROUP_EXCEL_EXPORT.getCode(), brandsInfoVO);

            //表标题数据
            List<List<String>> headList = (List<List<String>>) map.get("head");

            //表格数据
            List<List<Object>> dataList = (List<List<Object>>) map.get("data");

            //根据获取的数据生成excel文件
            ExcelUtil.noModelWrite(headList, dataList);
            /*}else{
                return ResponseVO.error(ResponseEnum.FALL_BACK_INFO.getCode(), "获取模板失败");
            }*/
            return ResponseVO.success();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ResponseVO.error(ResponseEnum.FALL_BACK_INFO);
        }
    }


    /**
     * 修改标签群组
     *
     * @param tagGroupName
     * @return
     */
    protected Integer getByTagGroupNameCount(String tagGroupName, Long id, TagBrandsInfoVO brandsInfo) {
        SysTagGroupDO sysTagGroupDO = new SysTagGroupDO();
        sysTagGroupDO.setTagGroupName(tagGroupName);
        sysTagGroupDO.setBrandsId(brandsInfo.getBrandsId());
        sysTagGroupDO.setOrgId(brandsInfo.getOrgId());
        sysTagGroupDO.setDeleted(BusinessEnum.NOTDELETED.getCode());

        if (null == id) {
            return sysTagGroupMapper.selectCount(new LambdaQueryWrapper<>(sysTagGroupDO));
        } else {
            //查询不等于自己  是否还存在同样的名字
            return sysTagGroupMapper.selectCount(new LambdaQueryWrapper<>(sysTagGroupDO).ne(SysTagGroupDO::getId, id));
        }
    }

    /**
     * 检查修改时的群组依赖
     * true 有依赖
     * false 无依赖
     *
     * @param groupId
     * @return
     */
    protected boolean checkModifyDepending(Long groupId) {
        // TODO: 2020/9/16 检查群组依赖  什么情况算是有依赖  
        // TODO: 2020/9/24 暂无依赖判断 
        return false;
    }

    /**
     * 检查删除时的群组依赖
     * true 有依赖
     * false 无依赖
     *
     * @param tagGroupId
     * @return
     */
    protected boolean checkDeleteDepending(Long tagGroupId) {
        //  2020/9/24 有数据的不能删除
        Integer count = sysTagGroupUserMapper.selectCount(new LambdaQueryWrapper<SysTagGroupUserDO>()
                .eq(SysTagGroupUserDO::getTagGroupId, tagGroupId)
                .eq(SysTagGroupUserDO::getDeleted, BusinessEnum.NOTDELETED.getCode()));

        return count > 0;
    }

    /**
     * @Description: 根据群组id删除群组规则
     **/
    public void deleteByPropertyId(Long groupId) {
        SysTagGroupRuleDO sysTagGroupRuleDO = new SysTagGroupRuleDO();
        sysTagGroupRuleDO.setDeleted(BusinessEnum.DELETED.getCode());
        sysTagGroupRuleMapper.update(sysTagGroupRuleDO,
                new LambdaUpdateWrapper<SysTagGroupRuleDO>()
                        .eq(SysTagGroupRuleDO::getTagGroupId, groupId)
        );
    }

    /**
     * 根据群组id 查询群组以及群组规则信息
     *
     * @param tagGroupId
     * @return
     */
    public ResponseVO<SysTagGroupResponseVO> getGroupById(Long tagGroupId, TagBrandsInfoVO brandsInfo) {
        SysTagGroupDO sysTagGroupDO = getTagGroup(tagGroupId, brandsInfo);
        if (null == sysTagGroupDO) {
            return ResponseVO.errorParams(TagErrorMsgEnum.TAG_GROUP_NOT_EXISTS.getMessage());
        }
        SysTagGroupResponseVO data = new SysTagGroupResponseVO();
        BeanUtils.copyPropertiesIgnoreNull(sysTagGroupDO, data);
        return ResponseVO.success(data);
    }

    /**
     * 查询标签群组
     *
     * @param tagGroupId
     * @return
     */
    protected SysTagGroupDO getTagGroup(Long tagGroupId, TagBrandsInfoVO brandsInfo) {
        return sysTagGroupMapper.selectOne(new LambdaQueryWrapper<SysTagGroupDO>()
                .eq(SysTagGroupDO::getId, tagGroupId)
                .eq(SysTagGroupDO::getBrandsId, brandsInfo.getBrandsId())
                .eq(SysTagGroupDO::getOrgId, brandsInfo.getOrgId())
        );
    }


    /**
     * 根据群组id 获取群组规则信息
     *
     * @param tagGroupId
     * @return
     */
    public ResponseVO<SysTagGroupRuleGetResponseVO> getGroupRuleById(Long tagGroupId, TagBrandsInfoVO brandsInfo) {

        SysTagGroupDO sysTagGroupDO = getTagGroup(tagGroupId, brandsInfo);
        if (null == sysTagGroupDO) {
            return ResponseVO.errorParams(TagErrorMsgEnum.TAG_GROUP_NOT_EXISTS.getMessage());
        }

        SysTagGroupRuleGetResponseVO data = new SysTagGroupRuleGetResponseVO();
//        //群组信息
        BeanUtils.copyPropertiesIgnoreNull(sysTagGroupDO, data);
        //群组规则
        data.setRule(tagGroupRuleService.getRule(tagGroupId));
        return ResponseVO.success(data);
    }

    /**
     * 根据父字典code得到子字典code
     *
     * @param dictParentCode
     * @return
     */
    private HashMap<Long, String> getDictCodeMap(String dictParentCode) {
        HashMap<Long, String> resultMap = new HashMap<>();

        DictFindAllVO dictFindAllVO = new DictFindAllVO();
        dictFindAllVO.setDictParentCode(dictParentCode);
        ResponseVO<List<SysDictVO>> dictResponseVO = dataManagerService.findAll(dictFindAllVO);
        if (CollUtil.isNotEmpty(dictResponseVO.getData())) {
            for (SysDictVO dictVO : dictResponseVO.getData()) {
                resultMap.put(dictVO.getId(), dictVO.getDictCode());
            }
        }
        return resultMap;
    }

    private List<SysTagGroupRuleDO> getRuleList(Long groupId, Long parentId) {
        LambdaQueryWrapper<SysTagGroupRuleDO> query = new LambdaQueryWrapper<>();
        query.eq(SysTagGroupRuleDO::getTagGroupId, groupId)
                .eq(SysTagGroupRuleDO::getTagGroupRuleParentId, parentId)
                .eq(SysTagGroupRuleDO::getDeleted, BusinessEnum.NOTDELETED.getCode());
        return sysTagGroupRuleMapper.selectList(query);
    }

    /**
     * 根据标签获取用户
     *
     * @param tag
     * @param logicalOperationDictCode
     * @return
     */
    private Set<Long> getTagUser(SysTagGroupRuleInfoRequestVO tag, String logicalOperationDictCode) {

        List<LogicalOperationValueRequestVO> tagPropertyList = tag.getLogicalOperationValue();
        List<Long> propertyIds = tagPropertyList.stream().map(LogicalOperationValueRequestVO::getTagPropertyId).collect(Collectors.toList());

        QueryWrapper<SysTagPropertyUserDO> query = new QueryWrapper();
        query.eq("tag_id", tag.getTagId());

        if (StringUtils.equals(TagDictEnum.GROUP_OPERATORS_IN.getCode(), logicalOperationDictCode)) {
            query.in("property_id", propertyIds);
        } else if (StringUtils.equals(TagDictEnum.GROUP_OPERATORS_NOT_IN.getCode(), logicalOperationDictCode)) {
            query.notIn("property_id", propertyIds);
        } else {
            //其他类型 todo
        }
        List<SysTagPropertyUserDO> list = sysTagPropertyUserMapper.selectList(query);
        return list.stream().map(userDO -> userDO.getUserId()).filter(x -> x != null).collect(Collectors.toSet());
    }


    /**
     * 逻辑关系处理
     *
     * @param first
     * @param second
     * @param relationCode
     * @return
     */
    private Set<Long> dealListByRelationCode(Set<Long> first, Set<Long> second, String relationCode) {
        if (StringUtils.equals(TagDictEnum.TAG_RELATIONAL_OPERATION_AND.getCode(), relationCode)) {
            //交集
            first.retainAll(second);
        } else if (StringUtils.equals(TagDictEnum.TAG_RELATIONAL_OPERATION_OR.getCode(), relationCode)) {
            //并集
            first.removeAll(second);
            first.addAll(second);
        }
        return first;
    }

    /**
     * 人群控制处理逻辑
     *
     * @param tagGroupDO
     * @param userIdSet
     * @return
     */
    private Set<Long> dealUserList(SysTagGroupDO tagGroupDO, Set<Long> userIdSet) {
        List<Long> userIdList = new ArrayList<>();
        userIdList.addAll(userIdSet);

        //移除null值。
        userIdList.remove(null);

        Set<Long> userListResult = new HashSet<>();

        //总人数
        if (userIdList.size() > 0) {
            // 人群控制的处理逻辑
            //剔除群组id的用户
            if (StringUtils.isNotBlank(tagGroupDO.getExcludeUserGroupId())) {
                //获取剔除人群的userList
                List<Long> excludeUserList = getExcludeUserList(tagGroupDO.getExcludeUserGroupId());
                if (CollUtil.isNotEmpty(excludeUserList)) {
                    userIdList.removeAll(excludeUserList);
                }
            }

            //人群限制
            Integer limit = getLimit(userIdList.size(), tagGroupDO);
            if (null == limit) {
                //不限制
                userListResult.addAll(userIdList);
            } else if (limit < 1) {
            } else {

                //随机取
                while (userIdList.size() > 0 && userListResult.size() < limit) {
                    int index = new Random().nextInt(userIdList.size());

                    userListResult.add(userIdList.get(index));
                    userIdList.remove(index);
                }
            }

        }

        return userListResult;
    }


    private List<Long> getExcludeUserList(String excludeUserGroupId) {
        List<Long> excludeGroupList = Arrays.asList(excludeUserGroupId.split(",")).stream().map(s -> Long.parseLong(s)).collect(Collectors.toList());
        List<SysTagGroupUserDO> excludeUserDOList = sysTagGroupUserMapper.selectList(new LambdaQueryWrapper<SysTagGroupUserDO>()
                .in(SysTagGroupUserDO::getTagGroupId, excludeGroupList)
                .eq(SysTagGroupUserDO::getDeleted, BusinessEnum.NOTDELETED.getCode())
        );
        if (CollUtil.isNotEmpty(excludeGroupList)) {
            return excludeUserDOList.stream().map(SysTagGroupUserDO::getUserId).collect(Collectors.toList());
        }
        return null;
    }

    /**
     * 获取人群限制人数
     *
     * @param total
     * @param tagGroupDO
     * @return null 表示不限制，其他数字表示实际限制人数
     */
    private Integer getLimit(Integer total, SysTagGroupDO tagGroupDO) {
        Integer limit = null;
        if (total > 0) {
            //限制百分比
            if (null != tagGroupDO.getTagGroupCountLimitPercent()) {
                limit = Integer.valueOf((new BigDecimal(total).multiply(new BigDecimal(tagGroupDO.getTagGroupCountLimitPercent())).divide(new BigDecimal(100)).setScale(0, BigDecimal.ROUND_UP)).toString());
            }
            //限制总人数
            if (null != tagGroupDO.getTagGroupCountLimitNum()) {
                limit = tagGroupDO.getTagGroupCountLimitNum();
            }
        }
        return limit;
    }

    /**
     * @param sysTagGroupRuleVO
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ResponseVO editGroupRule(SysTagGroupRuleVO sysTagGroupRuleVO, TagBrandsInfoVO brandsInfo) {
        // TODO: 2020/9/24 检查依赖，什么情况下可以编辑标签属性

        //入参检查  控制人数和百分比不能同时有值
        if (null != sysTagGroupRuleVO.getTagGroupCountLimitNum() && null != sysTagGroupRuleVO.getTagGroupCountLimitPercent()) {
            return ResponseVO.errorParams(TagErrorMsgEnum.TAG_GROUP_USER_CONTROL_TOTAL_ERROR.getMessage());
        }

        Long tagGroupId = sysTagGroupRuleVO.getId();
        //检查群组是否存在
        SysTagGroupDO tagGroupDOFind = getTagGroup(tagGroupId, brandsInfo);
        if (null == tagGroupDOFind) {
            return ResponseVO.errorParams(TagErrorMsgEnum.TAG_GROUP_NOT_EXISTS.getMessage());
        }

        //  检查标签是否是启用状态
        if (checkTagStatus(sysTagGroupRuleVO.getRule())) {
            return ResponseVO.errorParams(TagErrorMsgEnum.TAG_GROUP_TAG_NOT_USE.getMessage());
        }

        //检查标签属性
        if (checkTagProperty(sysTagGroupRuleVO.getRule())) {
            return ResponseVO.errorParams(TagErrorMsgEnum.TAG_GROUP_TAG_NOT_USE.getMessage());
        }

        //更新人群限制信息
        //规则 人数控制相关没有传值，就把历史的值置空，所以这里使用updateWrapper 进行更新
        this.update(new SysTagGroupDO(),new LambdaUpdateWrapper<SysTagGroupDO>()
                .eq(SysTagGroupDO::getId, tagGroupId)
                .set(SysTagGroupDO::getTagGroupRuleRelationshipId, sysTagGroupRuleVO.getTagGroupRuleRelationshipId())
                .set(SysTagGroupDO::getTagGroupCountLimitNum, sysTagGroupRuleVO.getTagGroupCountLimitNum())
                .set(SysTagGroupDO::getTagGroupCountLimitPercent, sysTagGroupRuleVO.getTagGroupCountLimitPercent())
                .set(SysTagGroupDO::getExcludeUserGroupId, sysTagGroupRuleVO.getExcludeUserGroupId())
                .set(SysTagGroupDO::getTagGroupRuleChangeExecuteStatus, BusinessEnum.UNEXECUTED.getCode())
        );


        // 群组历史规则数据清除
        if (getRuleCount(tagGroupId) > 0) {
            deleteByPropertyId(tagGroupId);
        }

//        tagGroupDOFind.setTagGroupCountLimitPercent(sysTagGroupRuleVO.getTagGroupCountLimitPercent());
//        tagGroupDOFind.setTagGroupCountLimitNum(sysTagGroupRuleVO.getTagGroupCountLimitNum());
//        tagGroupDOFind.setExcludeUserGroupId(sysTagGroupRuleVO.getExcludeUserGroupId());
//        tagGroupDOFind.setTagGroupRuleRelationshipId(sysTagGroupRuleVO.getTagGroupRuleRelationshipId());
        //保存规则
        return saveGroupRule(tagGroupDOFind, sysTagGroupRuleVO.getRule(), true);

    }

    /**
     * 检查标签状态
     *
     * @param ruleList
     * @return
     */
    private boolean checkTagStatus(List<SysTagGroupRuleRequestVO> ruleList) {
        HashSet<Long> tagSet = new HashSet<>();
        for (SysTagGroupRuleRequestVO group : ruleList) {
            tagSet.addAll(group.getTagGroupRule().stream()
                    .map(SysTagGroupRuleInfoRequestVO::getTagId).collect(Collectors.toList()));
        }

        Integer tagCount = sysTagMapper.selectCount(new LambdaQueryWrapper<SysTagDO>()
                .in(SysTagDO::getId, tagSet)
                .eq(SysTagDO::getDeleted, BusinessEnum.NOTDELETED.getCode())
                .eq(SysTagDO::getTagStatus, BusinessEnum.USING.getCode())
        );
        return tagSet.size() != tagCount;
    }

    /**
     * 检查标签属性状态
     *
     * @param ruleList
     * @return
     */
    private boolean checkTagProperty(List<SysTagGroupRuleRequestVO> ruleList) {
        Set<Long> tagPropertySet;
        List<SysTagGroupRuleInfoRequestVO> tagList;
        for (SysTagGroupRuleRequestVO group : ruleList) {
            tagList = group.getTagGroupRule();

            for (SysTagGroupRuleInfoRequestVO tag : tagList) {
                tagPropertySet = tag.getLogicalOperationValue().stream().map(LogicalOperationValueRequestVO::getTagPropertyId).collect(Collectors.toSet());
                Integer propertyCount = sysTagPropertyMapper.selectCount(new LambdaQueryWrapper<SysTagPropertyDO>()
                        .in(SysTagPropertyDO::getId, tagPropertySet)
                        .eq(SysTagPropertyDO::getTagId, tag.getTagId())
                        .eq(SysTagPropertyDO::getDeleted, BusinessEnum.NOTDELETED.getCode())
                );
                return propertyCount != tagPropertySet.size();
            }
        }

        return false;
    }


    /**
     * 保存群组规则
     *
     * @param sysTagGroupDO 群组信息
     * @param tagGroupRule  群组规则
     * @param calculate     是否计算
     * @return
     */
    protected ResponseVO saveGroupRule(SysTagGroupDO sysTagGroupDO, List<SysTagGroupRuleRequestVO> tagGroupRule, boolean calculate) {
        SysTagGroupRuleDO parentRuleDO;
        SysTagGroupRuleDO ruleDO;
        for (SysTagGroupRuleRequestVO ruleAddVO : tagGroupRule) {
            // 群组
            parentRuleDO = new SysTagGroupRuleDO();
            parentRuleDO.setTagGroupId(sysTagGroupDO.getId());
            parentRuleDO.setTagGroupRuleParentId(0L);
            parentRuleDO.setTagGroupRuleRelationshipId(ruleAddVO.getTagGroupRuleRelationshipId());
            sysTagGroupRuleMapper.insert(parentRuleDO);
            //群组标签
            List<SysTagGroupRuleDO> ruleDOList = new ArrayList<>();
            for (SysTagGroupRuleInfoRequestVO ruleInfoAddVO : ruleAddVO.getTagGroupRule()) {
                ruleDO = new SysTagGroupRuleDO();
                ruleDO.setTagGroupId(sysTagGroupDO.getId());
                ruleDO.setTagGroupRuleParentId(parentRuleDO.getId());
                ruleDO.setPropertyIds(this.getPropIds(ruleInfoAddVO));
                ruleDO.setTagGroupRule(JSON.toJSONString(ruleInfoAddVO));
                ruleDO.setTagId(ruleInfoAddVO.getTagId());
                ruleDOList.add(ruleDO);
            }
            tagGroupRuleService.saveBatch(ruleDOList);
        }

        // TODO: 2020/9/17 启用状态    立即执行，需要触发计算人群
        //动态的立即执行 也执行
        if (calculate
                && BusinessEnum.USING.getCode().equals(sysTagGroupDO.getTagGroupStatus())
//                && BusinessEnum.TAG_GROUP_TYPE_STATIC.getCode().equals(sysTagGroupDO.getTagGroupStartType())
                && BusinessEnum.TAG_GROUP_START_IMMEDIATELY.getCode().equals(sysTagGroupDO.getTagGroupStartType())
        ) {
            //群组覆盖人数set 成计算中
            SysTagGroupCountUserInfo countUserInfo = new SysTagGroupCountUserInfo();
            countUserInfo.dealCountUserInfo();
            SysTagGroupDO groupDO = new SysTagGroupDO();
            groupDO.setId(sysTagGroupDO.getId());
            groupDO.setCountUserInfo(JSON.toJSONString(countUserInfo));
            sysTagGroupMapper.updateById(groupDO);
            producerService.sendMessage(tagGroupUserTopic, String.valueOf(sysTagGroupDO.getId()), randomUtil.getRandomIndex());
        }
        return ResponseVO.success(sysTagGroupDO.getId());
    }

    /**
     * 获取群组propIds
     *
     * @param vo
     * @return
     */
    private String getPropIds(SysTagGroupRuleInfoRequestVO vo) {
        List<LogicalOperationValueRequestVO> logicalOperationValue = vo.getLogicalOperationValue();
        String ids = "";
        for (LogicalOperationValueRequestVO logicalOperationValueRequestVO : logicalOperationValue) {
            ids = ids + logicalOperationValueRequestVO.getTagPropertyId() + ",";
        }
        ids = ids.substring(0, ids.lastIndexOf(","));
        return ids;
    }


    /**
     * 查询需要定时执行的群组数据
     *
     * @return
     */
    public List<SysTagGroupDO> getTagGroupListByDataManager(String brandAndOrgId) {
        if (StringUtils.isBlank(brandAndOrgId)) {
            return new ArrayList<>();
        }

        String[] ids = brandAndOrgId.split(",");
        Long brandsId = Long.valueOf(ids[0]);
        Long orgId = Long.valueOf(ids[1]);
        //启动的指定日期的
        List<SysTagGroupDO> groupDOList = sysTagGroupMapper.selectList(new LambdaQueryWrapper<SysTagGroupDO>()
                .eq(SysTagGroupDO::getDeleted, BusinessEnum.NOTDELETED.getCode())
                .eq(SysTagGroupDO::getTagGroupStatus, BusinessEnum.USING.getCode())
                .eq(SysTagGroupDO::getBrandsId, brandsId)
                .eq(SysTagGroupDO::getOrgId, orgId)
                .eq(SysTagGroupDO::getTagGroupStartType, BusinessEnum.TAG_GROUP_START_ASSIGN_DATE.getCode())
                .le(SysTagGroupDO::getTagGroupStartTime, new Date())
        );

        //启动的立即执行动态的
        groupDOList.addAll(sysTagGroupMapper.selectList(new LambdaQueryWrapper<SysTagGroupDO>()
                .eq(SysTagGroupDO::getDeleted, BusinessEnum.NOTDELETED.getCode())
                .eq(SysTagGroupDO::getTagGroupStatus, BusinessEnum.USING.getCode())
                .eq(SysTagGroupDO::getBrandsId, brandsId)
                .eq(SysTagGroupDO::getOrgId, orgId)
                .eq(SysTagGroupDO::getTagGroupStartType, BusinessEnum.TAG_GROUP_START_IMMEDIATELY.getCode())
                .eq(SysTagGroupDO::getTagGroupType, BusinessEnum.TAG_GROUP_TYPE_DYNAMIC.getCode())
        ));

        //去除没规则的  静态redis 中有的 databaseId_tagGroup_static_ids
        Iterator<SysTagGroupDO> it = groupDOList.iterator();
        SysTagGroupDO sysTagGroupDO;
        while (it.hasNext()) {
            sysTagGroupDO = it.next();
            //移除没有规则的
            if (getRuleCount(sysTagGroupDO.getId()) < 1) {
                it.remove();
                continue;
            }

            //移除静态的 已经执行过的数据
            if (BusinessEnum.TAG_GROUP_TYPE_STATIC.getCode().equals(sysTagGroupDO.getTagGroupType())) {
                if (redisUtil.sHasKey(TagConstant.GROUP_STATIC_EXECUTED_REDIS_KEY_PREFIX + brandsId, sysTagGroupDO.getId())) {
                    it.remove();
                }
            }
        }
        return groupDOList;
    }

    /**
     * @param tagGroupDO
     * @return null 获取失败
     */
    public Set<Long> getUserListByGroupId(SysTagGroupDO tagGroupDO) {
        //总的人群list
        Set<Long> userIdList = new HashSet<>();
        Long tagGroupId = tagGroupDO.getId();

        //根据运算id 获取运算code
        HashMap<Long, String> relationshipMap = getDictCodeMap(TagDictEnum.TAG_RELATIONAL_OPERATION.getCode());
        HashMap<Long, String> logicalOperationMap = getDictCodeMap(TagDictEnum.GROUP_OPERATORS.getCode());
        if (relationshipMap.keySet().size() < 1 || logicalOperationMap.keySet().size() < 1) {
            return null;
        }
        //群组间的 且/或
        String groupRelationCode = relationshipMap.get(tagGroupDO.getTagGroupRuleRelationshipId());
        if (StrUtil.isBlank(groupRelationCode)) {
            return null;
        }
        //父群组
        List<SysTagGroupRuleDO> parentRuleList = getRuleList(tagGroupId, 0L);
        //子群组
        List<SysTagGroupRuleDO> childRuleDOList;
        //每个群组内的人群list
        Set<Long> groupInnerUserIdList;

        int groupCount = 0;
        for (SysTagGroupRuleDO parentRuleDO : parentRuleList) {
            //已经执行过 且 是且关系的 的，没有拿到人群 就不用继续执行。
            if (groupCount > 1 && StringUtils.equals(TagDictEnum.TAG_RELATIONAL_OPERATION_AND.getCode(), groupRelationCode) && CollUtil.isEmpty(userIdList)) {
                break;
            }

            //群组内的且/或code
            String groupInnerRelationshipCode = relationshipMap.get(parentRuleDO.getTagGroupRuleRelationshipId());
            if (StrUtil.isBlank(groupRelationCode)) {
                return null;
            }

            //子群组规则
            childRuleDOList = getRuleList(tagGroupId, parentRuleDO.getId());

            groupInnerUserIdList = this.getUserIdListByRuleDOList(childRuleDOList, groupInnerRelationshipCode, logicalOperationMap);

            //群组间的数据且/或
            if (groupCount == 0) {
                userIdList = groupInnerUserIdList;
            } else {
                dealListByRelationCode(userIdList, groupInnerUserIdList, groupRelationCode);
            }
            groupCount++;
        }

        //人群控制处理逻辑
        return dealUserList(tagGroupDO, userIdList);
    }

    private Set<Long> getUserIdListByRuleDOList(List<SysTagGroupRuleDO> childRuleDOList, String relationCode, HashMap<Long, String> logicalOperationMap) {
        List<SysTagGroupRuleInfoRequestVO> ruleList = childRuleDOList.stream().map(rule -> JSON.parseObject(rule.getTagGroupRule(), SysTagGroupRuleInfoRequestVO.class)).collect(Collectors.toList());
        return this.getUserIdListByRuleVOList(ruleList, relationCode, logicalOperationMap);
    }

    private Set<Long> getUserIdListByRuleVOList(List<SysTagGroupRuleInfoRequestVO> ruleList, String relationCode, HashMap<Long, String> logicalOperationMap) {
        Set<Long> groupInnerUserIdList = null;

        int count = 0;
        for (SysTagGroupRuleInfoRequestVO tag : ruleList) {
            if (count > 1 && StringUtils.equals(TagDictEnum.TAG_RELATIONAL_OPERATION_AND.getCode(), relationCode) && CollUtil.isEmpty(groupInnerUserIdList)) {
                break;
            }

            Set<Long> tagUserList = getTagUser(tag, logicalOperationMap.get(tag.getLogicalOperationId()));
            //群组内的数据且/或
            if (0 == count) {
                groupInnerUserIdList = tagUserList;
            } else {
                dealListByRelationCode(groupInnerUserIdList, tagUserList, relationCode);
            }

            count++;
        }
        if (null == groupInnerUserIdList) {
            return new HashSet<>();
        }
        return groupInnerUserIdList;
    }
}
