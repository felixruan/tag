package org.etocrm.tagManager.service.impl.mat;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.http.Header;
import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.etocrm.core.enums.BusinessEnum;
import org.etocrm.core.enums.ResponseEnum;
import org.etocrm.core.util.BeanUtils;
import org.etocrm.core.util.ResponseVO;
import org.etocrm.dynamicDataSource.service.IDynamicService;
import org.etocrm.dynamicDataSource.util.RandomUtil;
import org.etocrm.dynamicDataSource.util.RedisConfig;
import org.etocrm.dynamicDataSource.util.RedisUtil;
import org.etocrm.kafkaServer.service.IKafkaProducerService;
import org.etocrm.tagManager.api.IDataManagerService;
import org.etocrm.tagManager.batch.impl.common.MatCommonService;
import org.etocrm.tagManager.enums.TagDictEnum;
import org.etocrm.tagManager.mapper.ISysModelTableColumnMapper;
import org.etocrm.tagManager.mapper.ISysModelTableMapper;
import org.etocrm.tagManager.mapper.mat.*;
import org.etocrm.tagManager.model.DO.SysModelTableColumnDO;
import org.etocrm.tagManager.model.DO.SysModelTableDO;
import org.etocrm.tagManager.model.DO.mat.*;
import org.etocrm.tagManager.model.VO.SysDictVO;
import org.etocrm.tagManager.model.VO.mat.*;
import org.etocrm.tagManager.model.VO.tagGroup.SysTagGroupCountUserInfo;
import org.etocrm.tagManager.service.ISysModelTableColumnService;
import org.etocrm.tagManager.service.mat.IMatAutomationMarketingService;
import org.etocrm.tagManager.service.mat.IMatProvideOfAutomationMarketingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;


@Service
@Slf4j
public class MatProvideOfAutomationMarketingServiceImpl implements IMatProvideOfAutomationMarketingService {

    @Autowired
    private IMatMetadataEventModularRelationMapper iMatMetadataEventModularRelationMapper;

    @Autowired
    private MatCommonService matCommonService;

    @Autowired
    private IMatMetadataEventMapper iMatMetadataEventMapper;

    @Autowired
    private IMatMetadataUserPropertyMapper iMatMetadataUserPropertyMapper;

    @Autowired
    private IMatMetadataPropertyMapper iMatMetadataPropertyMapper;

    @Autowired
    private IMatMetadataEventPropertyRelationshipMapper iMatMetadataEventPropertyRelationshipMapper;

    @Autowired
    private IMatMetadataModularMapper iMatMetadataModularMapper;

    @Autowired
    private IMatMetadataWorkProcessMapper iMatMetadataWorkProcessMapper;

    @Autowired
    private IMatMetadataWorkProcessHandleMapper iMatMetadataWorkProcessHandleMapper;

    @Autowired
    private ISysModelTableMapper iSysModelTableMapper;

    @Autowired
    private ISysModelTableColumnMapper iSysModelTableColumnMapper;

    @Autowired
    private IMatMetadataModularModelRelationMapper iMatMetadataModularModelRelationMapper;

    @Autowired
    private IMatAutomationMarketingService iMatAutomationMarketingService;

    @Autowired
    private IMatCalculationDataMapper iMatCalculationDataMapper;

    @Autowired
    private IDataManagerService iDataManagerService;

    @Autowired
    ISysModelTableColumnService sysModelTableColumnService;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private IDynamicService iDynamicService;

    @Autowired
    IKafkaProducerService producerService;

    @Autowired
    private RandomUtil randomUtil;

    //事件营销流程缓存key
    private static final String ALL_EVENT_PROCESS_WORK = "allEventProcessWork";

    //批量上报标志位
    private static final String MAT_BATCH_OFFSET = "matBatchOffset";

    //事件营销触发通知MAT接口 URL
    @Value("${mat.url.notice}")
    private String matEventNoticeUrl;

    @Value("${CUSTOM.KAFKA.TOPIC.MAT_REPORT_TOPIC}")
    String matReportTopic;

    @Value("${CUSTOM.KAFKA.TOPIC.MAT_BATCH_REPORT_TOPIC}")
    String matBatchReportTopic;

    private static final String MAT_TOKEN_CACHE_KEY = "matTokenCacheKey";


    /**
     * 接收自动化营销流程的配置规则信息
     *
     * @param matWorkProcessVO
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseVO receiveMarketingRule(MatWorkProcessVO matWorkProcessVO) {
        try {

            log.info("receiveMarketingRule开始接收配置规则信息,matWorkId =【" + matWorkProcessVO.getId() + "】");
            log.info("流程规则信息matWorkProcessVO =【" + JSON.toJSONString(matWorkProcessVO) + "】");
            //如果是事件营销流程，则删除缓存中事件营销流程缓存
            if (matWorkProcessVO.getType() == 1) {
                redisUtil.deleteCache(ALL_EVENT_PROCESS_WORK + "_" + matWorkProcessVO.getOrgId());
            }

            //保存流程规则和分支信息
            iMatAutomationMarketingService.saveMarketingRule(matWorkProcessVO);

            //第二步根据规则对群组人群进行拆分
            if (matWorkProcessVO.getType() != 1 || matWorkProcessVO.getType() != 4) {//事件营销和被动流程不做人群拆分
                iMatAutomationMarketingService.calculationTagGroupByProcessRule(matWorkProcessVO);
            }

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ResponseVO.error(4001, "流程配置规则信息接收失败");
        }
        return ResponseVO.success();
    }


    /**
     * 接收自动化营销流程的执行结果记录
     *
     * @param sendRecordVOs
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseVO receiveMarketingRecords(List<MatWorkProcessSendRecordVO> sendRecordVOs) {
        log.info("receiveMarketingRecords开始接收执行结果记录,sendRecordVOs.size() =【" + sendRecordVOs.size() + "】");
        try {
            //    for (MatWorkProcessSendRecordVO recordVO : sendRecordVOs) {
            //根据原始的workid、handleid、actionid获取actionDO,拿到本地表的workid、handleid、actionid
                /*QueryWrapper<MatWorkProcessActionDO> actionQuery = new QueryWrapper();
                actionQuery.eq("mat_id", recordVO.getActionId());
                actionQuery.eq("mat_work_id", recordVO.getWorksId());
                actionQuery.eq("mat_handle_id", recordVO.getHandleId());
                actionQuery.eq("is_delete", BusinessEnum.NOTDELETED.getCode());
                MatWorkProcessActionDO actionDO = iMatMetadataWorkProcessActionMapper.selectOne(actionQuery);

                if (actionDO == null) {
                    ResponseVO.error(4001, "没有找到对应流程");
                }*/
            //    MatWorkProcessSendRecordDO recordDO = new MatWorkProcessSendRecordDO();
            //    BeanUtils.copyPropertiesIgnoreNull(recordVO, recordDO);
                /*recordDO.setActionId(actionDO.getId());
                recordDO.setWorksId(actionDO.getWorksId());
                recordDO.setHandleId(actionDO.getHandleId());*/
            //      iMatMetadataWorkProcessSendRecordMapper.insert(recordDO);
            //  }

            iMatAutomationMarketingService.asyncBatchSaveSendRecord(sendRecordVOs);

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ResponseVO.error(4001, "流程执行结果接收失败");
        }
        return ResponseVO.success();
    }


    /**
     * 根据模块id获取事件，或根据事件id获取属性
     *
     * @param modularId
     * @param eventId
     * @return
     */
    @Override
    public ResponseVO getEventsOrProperties(Integer modularId, Integer eventId) {
        JSONObject obj = new JSONObject();
        List<MatModularOutVO> modularOutVOS;
        List<MetadataMarketingEventsOutVO> marketingEventsOutVO = new ArrayList<>();
        List<MetadataMarketingPropertiesOutVO> marketingPropertiesOutVO = new ArrayList<>();
        List<MetadataMarketingPropertiesOutVO> userPropertiesOutVO = new ArrayList<>();
        try {

            if (modularId == null) {

                modularOutVOS = getModulars(modularId);

            } else if (modularId != null && eventId == null) {
                modularOutVOS = getModulars(modularId);
                marketingEventsOutVO = getEvents(modularId, eventId);

            } else {
                modularOutVOS = getModulars(modularId);
                marketingEventsOutVO = getEvents(modularId, eventId);
                marketingPropertiesOutVO = getProperties(eventId);
                userPropertiesOutVO = getUserProperties();
            }

            obj.put("modulars", modularOutVOS);
            obj.put("events", marketingEventsOutVO);
            obj.put("eventProperties", marketingPropertiesOutVO);
            obj.put("userProperties", userPropertiesOutVO);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ResponseVO.error(4001, "模块、事件、属性获取失败");
        }
        return ResponseVO.success(obj);
    }


    /**
     * 接收上报数据
     *
     * @param reportingParameter
     * @return
     */
    @Override
    //  @Transactional(rollbackFor = Exception.class)
    public ResponseVO receiveReportingData(MatReportingParameter reportingParameter) {
        log.info("receiveReportingData开始接收上报数据,eventCode =【" + reportingParameter.getEventCode() + "】");
        try {

            if (reportingParameter.getModCode() == null || reportingParameter.getModCode().equals("")
                    || reportingParameter.getEventCode() == null || reportingParameter.getEventCode().equals("")) {
                return ResponseVO.error(4001, "上报数据参数为空");
            }

            String consumeSign = String.valueOf(new Date().getTime()) + "_" + Math.random();
            reportingParameter.setConsumeSign(consumeSign);
            redisUtil.set(consumeSign, "kafkaSign", RedisConfig.expire);

            //上报数据先入kafka
            log.info("receiveReportingData()上报数据,放入Kafka，eventCode =【" + reportingParameter.getEventCode() + "】,consumeSign =【" + consumeSign + "】");
            producerService.sendMessage(matReportTopic, JSONUtil.toJsonStr(reportingParameter), randomUtil.getRandomIndex());


            /*Date date = new Date();

            JSONArray eventProArr = new JSONArray();
            JSONArray userProArr = new JSONArray();
            for (MetadataPropertyParameter parameter : reportingParameter.getEventProperty()) {
                JSONObject jo = new JSONObject();
                jo.put("propertyCode", parameter.getPropertyCode());
                jo.put("propertyValue", parameter.getPropertyValue());
                eventProArr.add(jo);
            }
            for (MetadataPropertyParameter parameter : reportingParameter.getUserProperty()) {
                JSONObject jo = new JSONObject();
                jo.put("propertyCode", parameter.getPropertyCode());
                jo.put("propertyValue", parameter.getPropertyValue());
                userProArr.add(jo);
            }

            QueryWrapper<MatModularDO> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("mod_code", reportingParameter.getModCode());
            queryWrapper.eq("is_delete", BusinessEnum.NOTDELETED.getCode());
            MatModularDO modularDO = iMatMetadataModularMapper.selectOne(queryWrapper);

            MatReportingDO reportingDO = new MatReportingDO();
            reportingDO.setEventProperty(eventProArr.toJSONString());
            reportingDO.setUserProperty(userProArr.toJSONString());
            reportingDO.setEventCode(reportingParameter.getEventCode());
            reportingDO.setModCode(reportingParameter.getModCode());
            reportingDO.setModId(modularDO.getId());
            reportingDO.setReportingTime(date);
            iMatMetadataReportingMapper.insert(reportingDO);

            this.matchingEventMarketingProcess(reportingParameter);//根据eventCode匹配流程*/

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ResponseVO.error(4001, "接收上报数据失败");
        }
        return ResponseVO.success();
    }


    /**
     * 批量接收被动流程上报数据
     *
     * @param batchReportingParameter
     * @return
     */
    public ResponseVO batchReceivePassiveWorkReportingData(MatBatchReportingParameter batchReportingParameter) {

        log.info("batchReceivePassiveWorkReportingData开始接收批量上报数据,workId =【" + batchReportingParameter.getWorkId() + "】");
        try {

            if (batchReportingParameter.getWorkId() != null && batchReportingParameter.getHandleId() != null
                    && batchReportingParameter.getUserParameters() != null && batchReportingParameter.getUserParameters().size() > 0) {

                String consumeSign = new Date().getTime() + "_" + Math.random();
                batchReportingParameter.setConsumeSign(consumeSign);
                redisUtil.set(consumeSign, "kafkaSign", RedisConfig.expire);

                //上报数据先入kafka
                log.info("receiveReportingData()上报数据,放入Kafka，workId =【" + batchReportingParameter.getWorkId() + "】,consumeSign =【" + consumeSign + "】");
                producerService.sendMessage(matBatchReportTopic, JSONUtil.toJsonStr(batchReportingParameter), randomUtil.getRandomIndex());
            } else {
                return ResponseVO.error(4001, "上报数据参数为空");
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ResponseVO.error(4001, "接收上报数据失败");
        }
        return ResponseVO.success();
    }

    /**
     * 根据流程规则对上报用户进行筛选
     *
     * @param batchReportingParameter
     */
    public void matchingUserByRule(MatBatchReportingParameter batchReportingParameter) {
        try {
            Long workId = batchReportingParameter.getWorkId();
            QueryWrapper<MatWorkProcessDO> queryWork = new QueryWrapper();
            queryWork.eq("mat_id", workId);
            queryWork.eq("is_delete", BusinessEnum.NOTDELETED.getCode());
            MatWorkProcessDO processDO = iMatMetadataWorkProcessMapper.selectOne(queryWork);

            QueryWrapper<MatWorkProcessHandleDO> queryHandle = new QueryWrapper();
            queryHandle.eq("mat_work_id", workId);
            queryHandle.eq("is_delete", BusinessEnum.NOTDELETED.getCode());
            List<MatWorkProcessHandleDO> handleDOS = iMatMetadataWorkProcessHandleMapper.selectList(queryHandle);

            Long batchId = batchReportingParameter.getBatchId();
            Integer batchSize = batchReportingParameter.getBatchSize();//该批次总数量
            String batchOffsetStr = redisUtil.getRefresh(MAT_BATCH_OFFSET + "_" + workId, String.class);
            Integer batchOffset = batchOffsetStr == null ? 0 : Integer.valueOf(batchOffsetStr);//该批次已上报数量
            List<MatBatchReportingUserParameter> userParameters = batchReportingParameter.getUserParameters();
            List<Long> memberIds = new ArrayList<>(userParameters.size());
            String idList = "";
            for (int i = 0; i < userParameters.size(); i++) {
                memberIds.add(userParameters.get(i).getMemberId());
                if (i != (userParameters.size() - 1)) {//拼接in条件
                    idList += "'" + userParameters.get(i).getMemberId() + "'" + ",";
                } else {
                    idList += "'" + userParameters.get(i).getMemberId() + "'";
                }
            }

            Integer usersSize = userParameters.size();//当前上报数量

            MatWorkProcessVO processVO = new MatWorkProcessVO();
            BeanUtils.copyPropertiesIgnoreNull(processDO, processVO);
            processVO.setSearchCondition(JSONObject.parseObject(processDO.getConditions()));
            processVO.setHandleId(null);
            processVO.setUserGroupId(null);

            List<JSONObject> processes = new ArrayList<>();//用于通知map系统 todo
            if (processDO.getHandleType() == 1) {//表示不需要筛选
                if (processDO.getIsOpenAbtest() == 0) {//表示不开启ABTest
                    MatWorkProcessHandleDO handleDO = handleDOS.get(0);
                    processes = dealReturnResultOneZero(userParameters, batchReportingParameter.getBatchId(), processDO, handleDO);
                } else {//表示开启ABTest
                    processes = finalGatherResult(batchSize, batchOffset, usersSize, batchId, processDO, processes, handleDOS, userParameters);
                }
            } else {//表示需要属性筛选
                processVO.setMemberIds(idList);
                List<TreeMap> treeMaps = matCommonService.changeRuleToSQLWithResult(processVO);
                List<Long> isHitMemberIds = new ArrayList<>(treeMaps.size());
                List<MatBatchReportingUserParameter> isHitUserParameters = new ArrayList<>();
                if (processDO.getIsOpenAbtest() == 0) {//表示不开启ABTest
                    for (TreeMap map : treeMaps) {
                        isHitMemberIds.add(Long.valueOf(String.valueOf(map.get("member_id"))));
                    }
                    for (MatBatchReportingUserParameter userParameter : userParameters) {
                        if (isHitMemberIds.contains(userParameter.getMemberId())) {
                            isHitUserParameters.add(userParameter);
                        }
                    }
                    for (MatWorkProcessHandleDO handleDO : handleDOS) {
                        if (handleDO.getProcessType() == 1) {//表示属性筛选分支
                            List<JSONObject> jsonObjects = dealReturnResultOneZero(isHitUserParameters, batchReportingParameter.getBatchId(), processDO, handleDO);
                            processes.addAll(jsonObjects);
                        } else {
                            userParameters.removeAll(isHitUserParameters);//去除属性筛选通过的，剩下就是筛选不通过的
                            List<JSONObject> jsonObjects = dealReturnResultOneZero(userParameters, batchReportingParameter.getBatchId(), processDO, handleDO);
                            processes.addAll(jsonObjects);
                        }
                    }
                } else {//表示开启ABTest
                    for (TreeMap map : treeMaps) {
                        isHitMemberIds.add(Long.valueOf(String.valueOf(map.get("member_id"))));
                    }
                    for (MatBatchReportingUserParameter userParameter : userParameters) {
                        if (isHitMemberIds.contains(userParameter.getMemberId())) {
                            isHitUserParameters.add(userParameter);
                        }
                    }
                    List<MatWorkProcessHandleDO> isHitHandleDOs = new ArrayList<>();
                    List<MatWorkProcessHandleDO> notHitHandleDOs = new ArrayList<>();
                    for (MatWorkProcessHandleDO handleDO : handleDOS) {
                        if (handleDO.getProcessType() == 1) {//表示属性筛选分支
                            isHitHandleDOs.add(handleDO);
                        } else {
                            notHitHandleDOs.add(handleDO);
                        }
                    }

                    //处理属性筛选通过的
                    processes = finalGatherResult(batchSize, batchOffset, usersSize, batchId, processDO, processes, isHitHandleDOs, isHitUserParameters);

                    //处理属性筛选不通过的
                    userParameters.removeAll(isHitUserParameters);//去除属性筛选通过的，剩下就是筛选不通过的
                    processes = finalGatherResult(batchSize, batchOffset, usersSize, batchId, processDO, processes, notHitHandleDOs, userParameters);
                }

                redisUtil.set(MAT_BATCH_OFFSET + "_" + workId,(batchOffset+usersSize)+"",RedisConfig.expire);

                log.info("最终处理结果为 processes = 【"+processes.size()+"】");
            }
        } catch (Exception e) {
            log.error("根据流程规则对上报用户进行筛选matchingUserByRule()报错！！！");
            log.error(e.getMessage(), e);
        }

    }

    private List<JSONObject> finalGatherResult(Integer batchSize, Integer batchOffset, Integer usersSize, Long batchId, MatWorkProcessDO processDO, List<JSONObject> processes,
                                               List<MatWorkProcessHandleDO> isHitHandleDOs, List<MatBatchReportingUserParameter> isHitUserParameters) {
        Map<Integer, Integer> map = defineWhichHandle(batchSize, batchOffset, usersSize, isHitHandleDOs);
        Integer beginSign = 0;
        for (Integer index : map.keySet()) {
            List<JSONObject> jsonObjects = dealReturnResultOneZero(isHitUserParameters.subList(beginSign, map.get(index) + beginSign), batchId, processDO, isHitHandleDOs.get(index));
            processes.addAll(jsonObjects);
            beginSign = map.get(index);
        }
        return processes;
    }

    private Map<Integer, Integer> defineWhichHandle(Integer batchSize, Integer batchOffset, Integer usersSize,
                                                    List<MatWorkProcessHandleDO> handleDOS) {
        Map<Integer, Integer> map = new HashMap<>();//以handleid为key，分布在分支的人数
        Integer handleSize = 0;
        for (int i = 0; i < handleDOS.size(); i++) {
            Integer percent = handleDOS.get(i).getPercent().intValue();
            if (i < handleDOS.size() - 1) {
                Integer nextPercent = handleDOS.get(i + 1).getPercent().intValue();
                handleSize += batchSize * percent / 100;
                if (handleSize >= batchOffset + usersSize) {//上报人数分布在当前分支内
                    map.put(i, usersSize);
                    break;
                } else if (handleSize < batchOffset + usersSize && batchOffset + usersSize < nextPercent + handleSize) {
                    //上报人数分布在当前分支和下个分支内
                    map.put(i, handleSize - batchOffset);
                    map.put(i + 1, batchOffset + usersSize - handleSize);
                    break;
                } else {
                    continue;
                }
            }
        }
        return map;
    }

    //表示不筛选且不开启ABTest的处理结果
    private List<JSONObject> dealReturnResultOneZero(List<MatBatchReportingUserParameter> userParameters, Long batchId, MatWorkProcessDO processDO, MatWorkProcessHandleDO handleDO) {
        List<JSONObject> processes = new ArrayList<>();
        for (MatBatchReportingUserParameter userParameter : userParameters) {
            JSONObject processe = new JSONObject();
            //       processe.put("isHit", BusinessEnum.NOTHIT.getCode());//没有命中
            processe.put("workId", processDO.getMatId());
            processe.put("handleId", handleDO.getMatId());
            processe.put("batchId", batchId);
            processe.put("userInfo", JSONObject.toJSONString(userParameter));
            processes.add(processe);
        }
        return processes;
    }


   /* private List<JSONObject> dealReturnResultZeroOne(MatBatchReportingParameter batchReportingParameter, MatWorkProcessVO processVO, MatWorkProcessDO processDO, MatWorkProcessHandleDO handleDO) {
        List<JSONObject> processes = new ArrayList<>();
        for (MatBatchReportingUserParameter userParameter : batchReportingParameter.getUserParameters()) {
            //这里做筛选条件匹配判断
            processVO.setMemberId(String.valueOf(userParameter.getMemberId()));

            //根据会员id(memberId)进行属性筛选，判断触发事件的会员是否符合流程的属性筛选条件
            List<TreeMap> treeMaps = iMatAutomationMarketingService.eventProcessPropertyScreen(processVO);
            JSONObject processe = new JSONObject();
            if (treeMaps.size() > 0) {
                //treeMaps.get(0).get("member_id") != null || treeMaps.get(0).get("unionid") != null || treeMaps.get(0).get("wechat_openid") != null
                if (treeMaps.get(0).get("member_id") != null) {
                    processe.put("isHit", BusinessEnum.HITED.getCode());//命中
                    //      processe.put("handleId", hitHandleId);
                } else {
                    processe.put("isHit", BusinessEnum.NOTHIT.getCode());//没有命中
                    //      processe.put("handleId", notHitHandleId);
                }
                processe.put("workId", processDO.getMatId());
                processes.add(processe);
            } else {
                processe.put("isHit", BusinessEnum.NOTHIT.getCode());//没有命中
                processe.put("workId", processDO.getMatId());
                //    processe.put("handleId", notHitHandleId);
                processes.add(processe);
            }
        }
        return processes;
    }*/

    /**
     * 查询群组列表
     *
     * @return
     */
    @Override
    public ResponseVO getTagGroups(Long originalId) {
        JSONArray groupArr = new JSONArray();
        try {

            /*//根据mat系统机构id查询标签系统机构id
            QueryWrapper<SysBrandsOrgDO> orgQuery = new QueryWrapper<>();
            orgQuery.eq("woaap_org_id", originalId);
            orgQuery.eq("is_delete", BusinessEnum.NOTDELETED.getCode());
            SysBrandsOrgDO sysBrandsOrgDO = iSysBrandsOrgMapper.selectOne(orgQuery);

            //根据机构id查询品牌id
            QueryWrapper<SysBrandsDO> brandsQuery = new QueryWrapper<>();
            brandsQuery.eq("org_id", sysBrandsOrgDO.getId());
            brandsQuery.eq("is_delete", BusinessEnum.NOTDELETED.getCode());
            List<SysBrandsDO> sysBrandsDOS = iSysBrandsMapper.selectList(brandsQuery);

            for (SysBrandsDO brandsDO : sysBrandsDOS) {
                //根据标签系统品牌id查询mat系统品牌id
                QueryWrapper<WoaapBrandsDO> woaapBrandsWrapper = new QueryWrapper<>();
                woaapBrandsWrapper.eq("brands_id", brandsDO.getId());
                woaapBrandsWrapper.eq("is_delete", BusinessEnum.NOTDELETED.getCode());
                WoaapBrandsDO woaapBrandsDO = iSysWoaapBrandsMapper.selectOne(woaapBrandsWrapper);

                //根据品牌id查询群组
                QueryWrapper<SysTagGroupDO> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("brands_id", brandsDO.getId());
                queryWrapper.eq("is_delete", BusinessEnum.NOTDELETED.getCode());
                List<SysTagGroupDO> sysTagGroupDOS = iSysTagGroupMapper.selectList(queryWrapper);
                for (SysTagGroupDO groupDO : sysTagGroupDOS) {
                    JSONObject obj = new JSONObject();
                    String countUserInfo = groupDO.getCountUserInfo();
                    if (countUserInfo == null || countUserInfo.equals("")) {
                        obj.put("count", 0);
                    } else {
                        SysTagGroupCountUserInfo sysTagGroupCountUserInfo = JSONObject.toJavaObject(JSON.parseObject(countUserInfo), SysTagGroupCountUserInfo.class);
                        if (sysTagGroupCountUserInfo.getCountUser().matches("^[0-9]*$")) {//判断该字段值是否是数值类型
                            obj.put("count", sysTagGroupCountUserInfo.getCountUser());
                        } else {
                            obj.put("count", 0);
                        }
                    }
                    obj.put("brandId", woaapBrandsDO.getWoaapId());
                    obj.put("brandName", brandsDO.getBrandsName());
                    obj.put("tagGroupName", groupDO.getTagGroupName());
                    obj.put("tagGroupId", groupDO.getId());
                    groupArr.add(obj);
                }
            }*/

            List<TreeMap> tagGroups = getTagGroupsByOrgId(originalId);

            for (TreeMap map : tagGroups) {
                JSONObject obj = new JSONObject();
                String countUserInfo = String.valueOf(map.get("userInfo")).trim();
                if (countUserInfo.equals("null") || countUserInfo.equals("")) {
                    obj.put("count", 0);
                } else {
                    SysTagGroupCountUserInfo sysTagGroupCountUserInfo = JSONObject.toJavaObject(JSON.parseObject(countUserInfo), SysTagGroupCountUserInfo.class);
                    if (sysTagGroupCountUserInfo.getCountUser().matches("^[0-9]*$")) {//判断该字段值是否是数值类型
                        obj.put("count", sysTagGroupCountUserInfo.getCountUser());
                    } else {
                        obj.put("count", 0);
                    }
                }
                obj.put("brandId", String.valueOf(map.get("brandId")).trim());
                obj.put("brandName", String.valueOf(map.get("brandName")).trim());
                obj.put("tagGroupName", String.valueOf(map.get("tagGroupName")).trim());
                obj.put("tagGroupId", String.valueOf(map.get("tagGroupId")).trim());
                groupArr.add(obj);
            }


        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ResponseVO.error(4001, "查询群组列表失败");
        }

        return ResponseVO.success(groupArr);
    }


    /**
     * 查询属性筛选列表
     *
     * @param modelId
     * @return
     */
    @Override
    public ResponseVO getPropertyScreenList(Integer modularId, Integer modelId) {

        JSONObject obj = new JSONObject();
        try {
            List<MatPropertyScreenOutVO> outVOS = new ArrayList<>();
            //   List<MatPropertyScreenColumnOutVO> outColumnVOS = new ArrayList<>();

            List<MatModularOutVO> modulars = getModulars(modularId);

            ResponseVO tagSysModelColumnTableById = new ResponseVO();
            if (modularId != null) {
                outVOS = getModels(modularId, modelId);
                if (modelId != null && outVOS.size() > 0) {
                    tagSysModelColumnTableById = sysModelTableColumnService.getTagSysModelColumnTableById(Long.valueOf(modelId));
                    //   outColumnVOS = getModelColumns(modelId);
                }
            }
            obj.put("modulars", modulars);
            obj.put("models", outVOS);
            obj.put("modelColumns", tagSysModelColumnTableById.getData());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ResponseVO.error(4001, "查询属性筛选列表出错");
        }
        return ResponseVO.success(obj);
    }


    /**
     * 查询属性筛选人群数据
     *
     * @return
     */
    @Override
    public ResponseVO getPropertyScreenGroupUsers(MatQueryMarketingVO queryVO) {
        JSONObject obj = new JSONObject();
        try {

            List<String> tableNamesStatus = new ArrayList<>();
            tableNamesStatus.add("mat_map_works m");
            tableNamesStatus.add("sys_tag_group t");
            List<String> columnsStatus = new ArrayList<>();
            columnsStatus.add("t.tag_group_rule_change_execute_status changeStatus");
            columnsStatus.add("t.tag_group_status status");
            String whereClauseStatus = "m.user_group_id = t.id and m.mat_id=" + queryVO.getWorkId();
            List<TreeMap> treeMapsStatus = iDynamicService.selectList(tableNamesStatus, columnsStatus, whereClauseStatus, null);
            String changeStatus = "";
            String status = "";
            if (treeMapsStatus != null && treeMapsStatus.size() > 0) {
                changeStatus = String.valueOf(treeMapsStatus.get(0).get("changeStatus"));
                status = String.valueOf(treeMapsStatus.get(0).get("status"));
            }

            if (!changeStatus.equals("1") || !status.equals("1")) {
                return ResponseVO.error(4001, "群组暂时不可用");
            }

            int pageSize = (int) queryVO.getSize().longValue();
            int pageCurrent = (int) queryVO.getCurrent().longValue();

            int startNum = pageSize * pageCurrent;

            List<String> tableNames = new ArrayList<>();
            tableNames.add("mat_map_calculation_data cd join ( SELECT id FROM mat_map_calculation_data ");

            List<String> tableNamesCount = new ArrayList<>();
            tableNamesCount.add("mat_map_calculation_data cd");

            List<String> columns = new ArrayList<>();
            //       columns.add("cd.id id");
            columns.add("cd.tag_group_id tagGroupId");
            columns.add("cd.member_id memberId");
            columns.add("cd.mat_work_id matWorkId");
            columns.add("cd.mat_handle_id matHandleId");
            columns.add("cd.brand_id brandId");
            columns.add("cd.original_id originalId");
            columns.add("cd.is_hit isHit");
            columns.add("cd.member_info memberInfo");

            String whereClauseCount = " cd.mat_work_id =" + queryVO.getWorkId();

            String whereClause = "mat_work_id =" + queryVO.getWorkId() + " ORDER BY id LIMIT " + startNum + "," + pageSize + " ) a on cd.id = a.id ";

            int count = iDynamicService.count(tableNamesCount, whereClauseCount);

            int pages = count / pageSize;
            if (count % pageSize != 0) {
                pages += 1;
            }

            List<TreeMap> treeMaps = iDynamicService.selectList(tableNames, columns, whereClause, null);

            obj.put("current", pageCurrent);
            obj.put("size", pageSize);
            obj.put("total", count);
            obj.put("pages", pages);
            obj.put("records", treeMaps);

            /*IPage<MatCalculationDataDO> iPage = new Page<>(
                    VoParameterUtils.getCurrent(queryVO.getCurrent()), VoParameterUtils.getSize(queryVO.getSize()));

            LambdaQueryWrapper<MatCalculationDataDO> lQuery = new LambdaQueryWrapper<>();
            lQuery.eq(MatCalculationDataDO::getMatWorkId, queryVO.getWorkId())
                 //   .eq(MatCalculationDataDO::getDeleted, BusinessEnum.NOTDELETED.getCode())
                    .orderByAsc(MatCalculationDataDO::getId);

            IPage<MatCalculationDataDO> matIPage = iMatCalculationDataMapper.selectPage(iPage, lQuery);
            List<MatCalculationDataOutVO> outVOS = new ArrayList<>();
            for (MatCalculationDataDO dataDO : matIPage.getRecords()) {
                MatCalculationDataOutVO outVO = new MatCalculationDataOutVO();
                BeanUtils.copyPropertiesIgnoreNull(dataDO, outVO);
                outVOS.add(outVO);
            }
            BasePage page = new BasePage(matIPage);
            page.setRecords(outVOS);*/

            return ResponseVO.success(obj);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ResponseVO.error(4001, "查询属性筛选人群数据出错");
        }
    }


    /**
     * 根据字段ID查询模型字段关联数据
     *
     * @return
     */
    public ResponseVO getModelColumnRelationListById(Long matOrgId, Long columnId) {


        try {
            SysModelTableColumnDO sysModelTableColumnDO = iSysModelTableColumnMapper.selectById(columnId);
            ResponseVO<SysDictVO> detail = iDataManagerService.detail(sysModelTableColumnDO.getValueType());
            if (detail.getCode() != 0 || null == detail.getData()) {
                return ResponseVO.errorParams("获取标签数据类型失败！！！");
            }
            String dictCode = detail.getData().getDictCode();
            if (dictCode.equals(TagDictEnum.TAG_DICT_LINK.getCode())) {
                //主表
                SysModelTableDO sysModelTableDO = iSysModelTableMapper.selectById(sysModelTableColumnDO.getModelTableId());
                //关联表
                SysModelTableDO sysModelTableDOs = iSysModelTableMapper.selectById(sysModelTableColumnDO.getRelationTableId());
                //关联字段
                SysModelTableColumnDO sysModelTableColumnDOs = iSysModelTableColumnMapper.selectById(sysModelTableColumnDO.getDisplayColumnId());
                //展示字段
                SysModelTableColumnDO sysModelTableColumnDO2 = iSysModelTableColumnMapper.selectById(sysModelTableColumnDO.getRelationPk());

                List<String> tableNames1 = new ArrayList<String>();
                tableNames1.add("sys_brands_org o");
                tableNames1.add("sys_brands b");
                List<String> columns1 = new ArrayList<>();
                columns1.add("b.id");
                columns1.add("b.org_id");
                String whereClause1 = "o.id=b.org_id and o.is_delete=0 and woaap_org_id=" + matOrgId;
                List<TreeMap> orgList = iDynamicService.selectList(tableNames1, columns1, whereClause1, null);
                String orgId = "";
                String brandId = "";
                if (orgList != null && orgList.size() > 0) {
                    orgId = orgList.get(0).get("org_id") == null ? "" : orgList.get(0).get("org_id").toString();
                    brandId = orgList.get(0).get("id") == null ? "" : orgList.get(0).get("id").toString();
                }
                log.info("根据模型字段ID查询模型字段关联数据，matOrgId=【" + matOrgId + "】，orgId=【" + orgId + "】,brandId=【" + brandId + "】");
                if (orgId.equals("") || brandId.equals("")) {
                    return ResponseVO.error(4001, "mat机构id没有对应的标签机构id");
                }
                //查动态数据
                List<String> tableNames2 = new ArrayList<String>();
                tableNames2.add(sysModelTableDO.getModelTable() + " as t1");
                tableNames2.add(sysModelTableDOs.getModelTable() + " as t2");
                List<String> columns2 = new ArrayList<>();
                columns2.add("distinct t2." + sysModelTableColumnDOs.getColumnName() + " as value");
                columns2.add("t2." + sysModelTableColumnDO2.getColumnName() + "  name");
                String whereClause2 = "t1." + sysModelTableColumnDO.getColumnName() + "=t2." + sysModelTableColumnDOs.getColumnName();
                //判断是否是主库用户
                /*TagBrandsInfoVO brandsInfo = brandsInfoUtil.getBrandsInfo();
                if (null != brandsInfo.getResponseVO()) {
                    return brandsInfo.getResponseVO();
                }
                if (brandsInfo.getSystemFlag()) {
                    return ResponseVO.success();
                }*/

                String whereClause3 = whereClause2 + " and  t1.brands_id = " + brandId + " and t1.org_id = " + orgId + " and  t2.brands_id = " + brandId + " and t2.org_id = " + orgId;
                List<TreeMap> mapList = iDynamicService.selectList(tableNames2, columns2, whereClause3, null);
                return ResponseVO.success(mapList);
            } else {
                return ResponseVO.errorParams("传参错误！！！");
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return ResponseVO.error(ResponseEnum.DATA_GET_ERROR);
    }


    public ResponseVO getHandleIdByUserInfo(MatUserInfoVO userInfoVO) {

        QueryWrapper<MatCalculationDataDO> calculationQuery = new QueryWrapper<>();
        log.info("-------------------getHandleIdByUserInfo开始查询-------------------");
        calculationQuery.eq("mat_work_id", userInfoVO.getWorkId());
        log.info("-------------------mat_work_id<" + userInfoVO.getWorkId() + ">");
        if (StringUtils.isNotBlank(userInfoVO.getOpenid())) {
            calculationQuery.eq("open_id", userInfoVO.getOpenid());
            log.info("-------------------open_id<" + userInfoVO.getOpenid() + ">");
        }
        if (StringUtils.isNotBlank(userInfoVO.getUnionid())) {
            calculationQuery.eq("union_id", userInfoVO.getUnionid());
            log.info("-------------------union_id<" + userInfoVO.getUnionid() + ">");
        }
        if (userInfoVO.getMemberId() != null) {
            calculationQuery.eq("member_id", userInfoVO.getMemberId());
            log.info("-------------------member_id<" + userInfoVO.getMemberId() + ">");
        }
        List<MatCalculationDataDO> dataDOS = iMatCalculationDataMapper.selectList(calculationQuery);
        if (CollectionUtil.isNotEmpty(dataDOS) && dataDOS.size() == 1) {
            log.info("-------------------getHandleIdByUserInfo查询结果为【" + dataDOS.get(0).toString() + "】");
            return ResponseVO.success(dataDOS.get(0).getMatHandleId());
        } else {
            if (CollectionUtil.isNotEmpty(dataDOS)) {
                log.info("-------------------getHandleIdByUserInfo查询结果为【" + dataDOS.toString() + "】");
                return ResponseVO.error(4001, "查询结果异常为多个");
            }
            return ResponseVO.error(4001, "查询结果为空");
        }
    }


    //根据eventCode匹配流程
    public void matchingEventMarketingProcess(MatReportingParameter reportingParameter) {

        String modCode = reportingParameter.getModCode().trim();
        String eventCode = reportingParameter.getEventCode().trim();

        String memberId = "";
        String orgId = "";
        String appId = "";
        String openId = "";
        for (MetadataPropertyParameter property : reportingParameter.getEventProperty()) {
            if (property.getPropertyCode().equals("appid")) {
                appId = String.valueOf(property.getPropertyValue()).equals("null") ? "" : String.valueOf(property.getPropertyValue());
            }
            break;
        }
        for (MetadataPropertyParameter property : reportingParameter.getUserProperty()) {
            if (property.getPropertyCode().equals("memberId")) {
                memberId = String.valueOf(property.getPropertyValue()).equals("null") ? "" : String.valueOf(property.getPropertyValue());
            } else if (property.getPropertyCode().equals("matOrgId")) {
                orgId = String.valueOf(property.getPropertyValue()).equals("null") ? "0" : String.valueOf(property.getPropertyValue());
            } else if (property.getPropertyCode().equals("openId")) {
                openId = String.valueOf(property.getPropertyValue()).equals("null") ? "0" : String.valueOf(property.getPropertyValue());
            }
        }
        log.info("开始匹配流程，eventCode=【" + reportingParameter.getEventCode() + "】" + ",orgId=【" + orgId + "】,memberId=【" + memberId + "】" + "】,openId=【" + openId + "】");
        try {
            List<MatWorkProcessDO> matWorkProcessDOS;
            String allEventProcessWork = redisUtil.getRefresh(ALL_EVENT_PROCESS_WORK + "_" + orgId, String.class);
            if (allEventProcessWork == null || allEventProcessWork.equals("") || allEventProcessWork.equals("[]")) {
                QueryWrapper<MatWorkProcessDO> processQuery = new QueryWrapper();
                processQuery.eq("type", 1);
                processQuery.eq("org_id", orgId);
                processQuery.eq("is_delete", BusinessEnum.NOTDELETED.getCode());
                matWorkProcessDOS = iMatMetadataWorkProcessMapper.selectList(processQuery);
                //缓存所有的事件营销
                if (CollectionUtil.isNotEmpty(matWorkProcessDOS)) {
                    redisUtil.set(ALL_EVENT_PROCESS_WORK + "_" + orgId, JSONArray.toJSONString(matWorkProcessDOS), RedisConfig.expire);
                }
            } else {
                matWorkProcessDOS = JSONArray.parseArray(allEventProcessWork, MatWorkProcessDO.class);
            }

            List<JSONObject> processes = assembleEventNoticeParam(matWorkProcessDOS, eventCode, memberId, appId, modCode, openId);

            if (processes.size() > 0) {
                for (MetadataPropertyParameter propertyParameter : reportingParameter.getUserProperty()) {
                    //这个地方调用MAT触发流程接口通知MAT触发流程执行
                    if (propertyParameter.getPropertyCode().equals("memberId")) {
                        String memberCode = propertyParameter.getPropertyCode();

                        String eventResult = triggerEventNoticeMat(orgId, memberId, memberCode, processes, openId, appId);//触发营销通知

                        if (eventResult.equals("") || !JSON.parseObject(eventResult).get("errcode").toString().equals("0")) {
                            log.error("会员【" + memberId + "】触发事件【" + eventCode + "】，事件营销反向通知MAT系统请求发送错误");
                        }
                        break;
                    }
                }
            }
        } catch (Exception e) {
            log.error("根据eventCode匹配流程出错!!!!!!!");
            log.error(e.getMessage(), e);
        }

    }


    //如果是公众号事件，同时还要匹配appid
    private List<JSONObject> assembleEventNoticeParam(List<MatWorkProcessDO> matWorkProcessDOS, String eventCode, String memberId, String appId, String modCode, String openId) throws Exception {
        List<JSONObject> processes = new ArrayList<>();
        log.info("根据eventCode=【" + eventCode + "】进行条件筛选匹配");
        for (MatWorkProcessDO processDO : matWorkProcessDOS) {
            log.info("matWorkId=【" + processDO.getMatId() + "】,tagGroupId= 【" + processDO.getUserGroupId() + "】");
            if (modCode.equals("official")) {
                if (!appId.equals("") && processDO.getTriggerCondition().contains(appId)) {
                    processes = getProcessesByAppid(processes, processDO, eventCode, memberId, openId, appId);
                }
            } else {
                processes = getProcessesByAppid(processes, processDO, eventCode, memberId, openId, appId);
            }
        }
        return processes;
    }


    private List<JSONObject> getProcessesByAppid(List<JSONObject> processes, MatWorkProcessDO processDO, String eventCode, String memberId, String openId, String appId) {

        JSONObject processe = new JSONObject();
        try {
            if (processDO.getTriggerCondition().contains(eventCode)) {
                QueryWrapper<MatWorkProcessHandleDO> queryHandle = new QueryWrapper();
                queryHandle.eq("mat_work_id", processDO.getMatId());
                List<MatWorkProcessHandleDO> handleDOS = iMatMetadataWorkProcessHandleMapper.selectList(queryHandle);
                //这里需要去匹配属性筛选条件，匹配通过才加入matWorkIds
                if (processDO.getHandleType() == 1) {//表示不筛选
                    processe.put("isHit", BusinessEnum.HITED.getCode());//命中
                    processe.put("workId", processDO.getMatId());
                    processe.put("handleId", handleDOS.get(0).getMatId());
                    processes.add(processe);
                } else {

                    Long notHitHandleId = 0L;
                    Long hitHandleId = 0L;
                    for (MatWorkProcessHandleDO handleDO : handleDOS) {
                        if (handleDO.getProcessType().equals(1)) {
                            hitHandleId = handleDO.getMatId();
                        } else {
                            notHitHandleId = handleDO.getMatId();
                        }
                    }

                    //在进行属性筛选之前，根据openid和appid获取memberId todo
                    if (memberId.equals("")) {
                        memberId = getMemberIdByOpenIdAndAppId(openId, appId);//公众号事件根据openid和appid获取memberid
                        if (memberId.equals("")) {
                            processe.put("isHit", BusinessEnum.NOTHIT.getCode());//没有命中
                            processe.put("workId", processDO.getMatId());
                            processe.put("handleId", notHitHandleId);
                            processes.add(processe);
                            return processes;
                        }
                    }

                    //这里做筛选条件匹配判断
                    MatWorkProcessVO processVO = new MatWorkProcessVO();
                    BeanUtils.copyPropertiesIgnoreNull(processDO, processVO);
                    processVO.setMemberId(memberId);
                    processVO.setSearchCondition(JSONObject.parseObject(processDO.getConditions()));

                    //根据会员id(memberId)进行属性筛选，判断触发事件的会员是否符合流程的属性筛选条件
                    List<TreeMap> treeMaps = iMatAutomationMarketingService.eventProcessPropertyScreen(processVO);

                    if (treeMaps.size() > 0) {
                        //treeMaps.get(0).get("member_id") != null || treeMaps.get(0).get("unionid") != null || treeMaps.get(0).get("wechat_openid") != null
                        if (treeMaps.get(0).get("member_id") != null) {
                            processe.put("isHit", BusinessEnum.HITED.getCode());//命中
                            processe.put("handleId", hitHandleId);
                        } else {
                            processe.put("isHit", BusinessEnum.NOTHIT.getCode());//没有命中
                            processe.put("handleId", notHitHandleId);
                        }
                        processe.put("workId", processDO.getMatId());
                        processes.add(processe);
                    } else {
                        processe.put("isHit", BusinessEnum.NOTHIT.getCode());//没有命中
                        processe.put("workId", processDO.getMatId());
                        processe.put("handleId", notHitHandleId);
                        processes.add(processe);
                    }
                }
            }
        } catch (Exception e) {
            log.error("方法getProcessesByAppid()出错!!!!!!!");
            log.error(e.getMessage(), e);
        }
        return processes;
    }

    private String getMemberIdByOpenIdAndAppId(String openId, String appId) {

        //SELECT b.member_id from wechat_user a,members b where a.wechat_appid='1abb27b36b0e928d4c45b0a4f55791f2' and a.wechat_openid='o-7tPt0-txSUB1ciyTMzbWuyVQEw' and a.wechat_unionid=b.unionid
        List<String> tableNames = new ArrayList<>();
        tableNames.add(" wechat_user a ");
        tableNames.add(" members b ");
        List<String> columns = new ArrayList<>();
        columns.add(" b.member_id ");

        String whereClause = "a.wechat_appid=" + appId + " and a.wechat_openid=" + openId + " and a.wechat_unionid=b.unionid";
        List<TreeMap> treeMaps = iDynamicService.selectList(tableNames, columns, whereClause, null);

        String memberId = "";
        if (treeMaps != null && treeMaps.size() > 0) {
            memberId = treeMaps.get(0).get("member_id") == null ? "" : String.valueOf(treeMaps.get(0).get("member_id"));
        }
        return memberId;
    }

    //获取授权token
    private String triggerEventNoticeMat(String orgId, String memberId, String memberCode, List<JSONObject> processes, String openId, String appId) throws Exception {
        log.info("条件筛选匹配成功，开始请求mat发送通知，orgId=【" + orgId + "】,memberId=【" + memberId + "】");
        String eventResult = "";
        String token = iMatAutomationMarketingService.getMatTokenByOrgId(orgId);
        if (!token.equals("")) {
            eventResult = triggerEventNoticeMat2(orgId, memberId, token, processes, openId, appId);//正式发起营销通知
        }
        log.info("------------------------------分割线eventResult------------------------------");
        //    log.info("请求结果eventResult =【" + eventResult + "】");

        return eventResult;
    }

    //正式发起营销通知
    private String triggerEventNoticeMat2(String orgId, String memberId, String token, List<JSONObject> processes, String openId, String appId) {
        String eventResult = "";
        try {
            List<String> tableNames = new ArrayList<>();
            tableNames.add("members m LEFT JOIN wechat_user wu on m.unionid = wu.wechat_unionid");
            List<String> columns = new ArrayList<>();
            columns.add("m.member_id,m.unionid,m.email,m.mobile,wu.wechat_openid,wu.wechat_appid");

            String whereClause = "";
            if (StringUtils.isEmpty(memberId)) {
                if (StringUtils.isEmpty(openId)) {
                    openId = "null";
                }
                whereClause = "wu.wechat_openid ='" + openId + "' group by wu.wechat_openid";
            } else {
                whereClause = "m.member_id =" + memberId + " group by m.member_id";
            }
            List<TreeMap> treeMaps = iDynamicService.selectList(tableNames, columns, whereClause, null);

            TreeMap treeMap = new TreeMap();
            if (treeMaps != null && treeMaps.size() > 0) {
                treeMap = treeMaps.get(0);
            } else {
                treeMap.put("wechat_appid", appId);
                treeMap.put("wechat_openid", openId);
            }
            String memberInfo = iMatAutomationMarketingService.getMemberInfo(treeMap);

            if (StringUtils.isEmpty(memberId)) {
                memberId = treeMap.get("member_id") == null ? "" : treeMap.get("member_id").toString();
            }
            JSONObject obj2 = new JSONObject();
            obj2.put("organization_id", orgId);
            obj2.put("memberId", memberId);
            obj2.put("unionId", treeMap.get("unionid") == null ? "" : treeMap.get("unionid"));
            obj2.put("memberInfo", memberInfo);
            obj2.put("matWorkId", processes);

            log.info("------------------------------分割线eventResult--obj2------------------------------");
            log.info("请求参数obj2 =【" + obj2.toJSONString() + "】");

            eventResult = HttpRequest.post(matEventNoticeUrl)//请求获取mat请求授权token
                    .header(Header.CONTENT_TYPE, "application/json")
                    .header(Header.AUTHORIZATION, "Bearer " + token)
                    .body(obj2.toJSONString())
                    .timeout(20000)//超时，毫秒
                    .execute().body();
            log.info("请求结果eventResult =【" + eventResult + "】");
            //表示缓存的token无效
            if (eventResult.contains("401") && eventResult.contains("Unauthenticated")) {
                redisUtil.deleteCache(MAT_TOKEN_CACHE_KEY + "_" + orgId);
                token = iMatAutomationMarketingService.getMatTokenByOrgId(orgId);
                eventResult = HttpRequest.post(matEventNoticeUrl)//请求获取mat请求授权token
                        .header(Header.CONTENT_TYPE, "application/json")
                        .header(Header.AUTHORIZATION, "Bearer " + token)
                        .body(obj2.toJSONString())
                        .timeout(20000)//超时，毫秒
                        .execute().body();
                log.info("因token无效，triggerEventNoticeMat2第二次请求结果eventResult =【" + eventResult + "】");
            }
        } catch (Exception e) {
            log.info("triggerEventNoticeMat2方法报错");
            log.error(e.getMessage(), e);
        }

        return eventResult;
    }


    //查询模型列表
    private List<MatPropertyScreenOutVO> getModels(Integer modularId, Integer modelId) throws Exception {

        List<Long> ids = new ArrayList<>();
        List<MatPropertyScreenOutVO> outVOS = new ArrayList<>();

        QueryWrapper<MatModularModelRelationDO> mmQuery = new QueryWrapper();
        mmQuery.eq("modular_id", modularId);
        mmQuery.eq("is_delete", BusinessEnum.NOTDELETED.getCode());
        List<MatModularModelRelationDO> relationDOS = iMatMetadataModularModelRelationMapper.selectList(mmQuery);

        if (relationDOS.size() > 0) {
            for (MatModularModelRelationDO relationDO : relationDOS) {
                ids.add(relationDO.getModelId());
            }
            QueryWrapper<SysModelTableDO> modelQuery = new QueryWrapper();
            if (modelId != null) {
                modelQuery.eq("id", modelId);
            } else {
                modelQuery.in("id", ids);
            }
            modelQuery.eq("is_delete", BusinessEnum.NOTDELETED.getCode());
            //     modelQuery.eq("model_status",BusinessEnum.NOTUSE.getCode());
            List<SysModelTableDO> sysModelTableDOS = iSysModelTableMapper.selectList(modelQuery);

            for (SysModelTableDO modelTableDO : sysModelTableDOS) {
                MatPropertyScreenOutVO outVO = new MatPropertyScreenOutVO();
                outVO.setId(modelTableDO.getId());
                outVO.setModelName(modelTableDO.getModelTableName());
                outVOS.add(outVO);
            }
        }

        return outVOS;
    }

    private List<MatPropertyScreenColumnOutVO> getModelColumns(Integer modelId) throws Exception {
        List<MatPropertyScreenColumnOutVO> outColumnVOS = new ArrayList<>();
        QueryWrapper<SysModelTableColumnDO> columnQuery = new QueryWrapper();
        columnQuery.eq("model_table_id", modelId);
        columnQuery.eq("is_delete", BusinessEnum.NOTDELETED.getCode());
        List<SysModelTableColumnDO> sysModelTableColumnDOS = iSysModelTableColumnMapper.selectList(columnQuery);
        for (SysModelTableColumnDO columnDO : sysModelTableColumnDOS) {
            MatPropertyScreenColumnOutVO outVO = new MatPropertyScreenColumnOutVO();
            outVO.setId(columnDO.getId());
            outVO.setModelProperty(columnDO.getDisplayName());
            String logicalOperations = columnDO.getLogicalOperations();
            if (logicalOperations != null && !logicalOperations.equals("")) {
                JSONArray jsonArray = JSONArray.parseArray(logicalOperations);
                outVO.setLogicalOperations(jsonArray);
            }
            outColumnVOS.add(outVO);
        }

        return outColumnVOS;
    }


    //获取所有的模块
    private List<MatModularOutVO> getModulars(Integer modularId) throws Exception {
        List<MatModularOutVO> modularOutVOS = new ArrayList<>();
        QueryWrapper<MatModularDO> queryWrapper = new QueryWrapper<>();
        if (modularId != null) {
            queryWrapper.eq("id", modularId);
        }
        queryWrapper.eq("is_delete", BusinessEnum.NOTDELETED.getCode());
        List<MatModularDO> matModularDOS = iMatMetadataModularMapper.selectList(queryWrapper);
        for (MatModularDO modularDO : matModularDOS) {
            MatModularOutVO outVO = new MatModularOutVO();
            BeanUtils.copyPropertiesIgnoreNull(modularDO, outVO);
            modularOutVOS.add(outVO);
        }
        return modularOutVOS;
    }

    //根据模块id获取对应的事件集合
    private List<MetadataMarketingEventsOutVO> getEvents(Integer modularId, Integer eventId) throws Exception {

        List<MetadataMarketingEventsOutVO> marketingEventsOutVO = new ArrayList<>();
        QueryWrapper<MetadataEventDO> queryEvent = new QueryWrapper();
        if (eventId != null) {
            queryEvent.eq("id", eventId);
        } else {
            QueryWrapper<MatModularDO> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("id", modularId);
            MatModularDO modularDO = iMatMetadataModularMapper.selectOne(queryWrapper);

            QueryWrapper<MetadataEventModularRelationDO> queryMod = new QueryWrapper();
            queryMod.eq("mod_code", modularDO.getModCode());
            queryMod.eq("is_delete", BusinessEnum.NOTDELETED.getCode());

            List<MetadataEventModularRelationDO> eventRelationDOS = iMatMetadataEventModularRelationMapper.selectList(queryMod);
            List<String> eventCodeList = new ArrayList<>(eventRelationDOS.size());
            for (MetadataEventModularRelationDO memr : eventRelationDOS) {
                eventCodeList.add(memr.getEventCode());
            }
            if (eventCodeList.size() > 0) {
                queryEvent.in("event_code", eventCodeList);
            }
        }
        queryEvent.eq("is_delete", BusinessEnum.NOTDELETED.getCode());
        List<MetadataEventDO> metadataEventDOS = iMatMetadataEventMapper.selectList(queryEvent);
        for (MetadataEventDO eventDO : metadataEventDOS) {
            MetadataMarketingEventsOutVO outVO = new MetadataMarketingEventsOutVO();
            BeanUtils.copyPropertiesIgnoreNull(eventDO, outVO);
            marketingEventsOutVO.add(outVO);
        }


        return marketingEventsOutVO;
    }

    //根据事件id获取对应的属性集合
    private List<MetadataMarketingPropertiesOutVO> getProperties(Integer eventId) throws Exception {
        List<MetadataMarketingPropertiesOutVO> marketingPropertiesOutVO = new ArrayList<>();
        QueryWrapper<MetadataEventDO> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", eventId);
        MetadataEventDO eventDO = iMatMetadataEventMapper.selectOne(queryWrapper);
        QueryWrapper<MetadataEventPropertyRelationshipDO> queryEvent = new QueryWrapper();
        queryEvent.eq("event_code", eventDO.getEventCode());
        queryEvent.eq("is_delete", BusinessEnum.NOTDELETED.getCode());
        List<MetadataEventPropertyRelationshipDO> propertyRelationDOS = iMatMetadataEventPropertyRelationshipMapper.selectList(queryEvent);
        List<String> propertyCodeList = new ArrayList<>(propertyRelationDOS.size());
        for (MetadataEventPropertyRelationshipDO mepr : propertyRelationDOS) {
            propertyCodeList.add(mepr.getPropertyCode());
        }

        if (propertyCodeList.size() > 0) {
            QueryWrapper<MetadataPropertyDO> propertyEvent = new QueryWrapper();
            propertyEvent.in("property_code", propertyCodeList);
            propertyEvent.eq("is_delete", BusinessEnum.NOTDELETED.getCode());
            List<MetadataPropertyDO> metadataPropertyDOS = iMatMetadataPropertyMapper.selectList(propertyEvent);
            for (MetadataPropertyDO propertyDO : metadataPropertyDOS) {
                MetadataMarketingPropertiesOutVO outVO = new MetadataMarketingPropertiesOutVO();
                BeanUtils.copyPropertiesIgnoreNull(propertyDO, outVO);
                marketingPropertiesOutVO.add(outVO);
            }
        }

        return marketingPropertiesOutVO;
    }


    private List<MetadataMarketingPropertiesOutVO> getUserProperties() throws Exception {
        List<MetadataMarketingPropertiesOutVO> userPropertiesOutVO = new ArrayList<>();

        QueryWrapper<MetadataUserPropertyDO> userQuery = new QueryWrapper();
        userQuery.eq("is_delete", BusinessEnum.NOTDELETED.getCode());
        List<MetadataUserPropertyDO> metadataUserPropertyDOS = iMatMetadataUserPropertyMapper.selectList(userQuery);
        for (MetadataUserPropertyDO propertyDO : metadataUserPropertyDOS) {
            MetadataMarketingPropertiesOutVO outVO = new MetadataMarketingPropertiesOutVO();
            BeanUtils.copyPropertiesIgnoreNull(propertyDO, outVO);
            userPropertiesOutVO.add(outVO);
        }
        return userPropertiesOutVO;
    }


    private List<TreeMap> getTagGroupsByOrgId(Long originalId) throws Exception {

        /*List<String> tableNames = new ArrayList<>();
        tableNames.add("members m");
        tableNames.add("sys_brands_org o");
        List<String> columns = new ArrayList<>();
        columns.add("o.woaap_org_id orgId");
        String whereClause = "m.org_id=o.woaap_org_id and m.member_id=" + memberId;
        List<TreeMap> treeMaps = iDynamicService.selectList(tableNames, columns, whereClause, null);*/

        List<String> tableNames = new ArrayList<>();
        tableNames.add("sys_brands_org o");
        tableNames.add("sys_brands b");
        tableNames.add("sys_woaap_brands w");
        tableNames.add("sys_tag_group g");
        List<String> columns = new ArrayList<>();
        columns.add("w.woaap_id brandId");
        columns.add("b.brands_name brandName");
        columns.add("g.tag_group_name tagGroupName");
        columns.add("g.id tagGroupId");
        columns.add("g.count_user_info userInfo");

        String whereClause = "o.id=b.org_id and b.id=w.brands_id and b.id=g.brands_id and g.is_delete = 0 and g.tag_group_status=1 and g.tag_group_rule_change_execute_status=1 and o.woaap_org_id=" + originalId;

        List<TreeMap> treeMaps = iDynamicService.selectList(tableNames, columns, whereClause, null);
        return treeMaps;
    }

}








