package org.etocrm.tagManager.service.impl.mat;

import cn.hutool.http.Header;
import cn.hutool.http.HttpRequest;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.etocrm.core.enums.BusinessEnum;
import org.etocrm.core.util.BeanUtils;
import org.etocrm.core.util.JsonUtil;
import org.etocrm.core.util.ResponseVO;
import org.etocrm.dynamicDataSource.service.IDynamicService;
import org.etocrm.dynamicDataSource.util.RedisConfig;
import org.etocrm.dynamicDataSource.util.RedisUtil;
import org.etocrm.tagManager.batch.impl.common.MatCommonService;
import org.etocrm.tagManager.mapper.mat.IMatCalculationDataMapper;
import org.etocrm.tagManager.mapper.mat.IMatMetadataWorkProcessActionMapper;
import org.etocrm.tagManager.mapper.mat.IMatMetadataWorkProcessHandleMapper;
import org.etocrm.tagManager.mapper.mat.IMatMetadataWorkProcessMapper;
import org.etocrm.tagManager.model.DO.mat.*;
import org.etocrm.tagManager.model.VO.mat.MatWorkProcessActionVO;
import org.etocrm.tagManager.model.VO.mat.MatWorkProcessHandleVO;
import org.etocrm.tagManager.model.VO.mat.MatWorkProcessSendRecordVO;
import org.etocrm.tagManager.model.VO.mat.MatWorkProcessVO;
import org.etocrm.tagManager.service.mat.IMatAutomationMarketingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Service
@Slf4j
public class MatAutomationMarketingServiceImpl extends ServiceImpl<IMatCalculationDataMapper, MatCalculationDataDO> implements IMatAutomationMarketingService {


    @Autowired
    private IDynamicService iDynamicService;

    @Autowired
    private MatCommonService matCommonService;

    @Autowired
    private IMatMetadataWorkProcessMapper iMatMetadataWorkProcessMapper;

    @Autowired
    private IMatMetadataWorkProcessHandleMapper iMatMetadataWorkProcessHandleMapper;

    @Autowired
    private IMatMetadataWorkProcessActionMapper iMatMetadataWorkProcessActionMapper;

    @Autowired
    private RedisUtil redisUtil;

    //获取mat系统授权Token接口 URL
    @Value("${mat.url.auth}")
    private String matAuthUrl;

    //流程筛选人群完成通知mat接口 URL
    @Value("${mat.url.work}")
    private String matWorkNoticeUrl;

    @Value("${mat.url.grant_type}")
    private String grantType;

    @Value("${mat.url.client_id}")
    private String clientId;

    @Value("${mat.url.client_secret}")
    private String clientSecret;

    private static final String MAT_TOKEN_CACHE_KEY = "matTokenCacheKey";

    /**
     * 根据规则对群组人群进行拆分
     *
     * @param matWorkProcessVO
     * @return
     */
    @Override
    @Async
    public void calculationTagGroupByProcessRule(MatWorkProcessVO matWorkProcessVO) {

        /*if (matWorkProcessVO.getSearchCondition() == null || matWorkProcessVO.getSearchCondition().toJSONString().trim().equals("{}")) {
            return ResponseVO.error(4001, "属性筛选规则为空");
        }*/
        ResponseVO responseVO = new ResponseVO(1001, "初始化值");
        try {
            //先做覆盖删除操作
            String tableNames = "mat_map_calculation_data cd";
            String whereClause = " cd.mat_work_id =" + matWorkProcessVO.getId();
            iDynamicService.deleteRecord(tableNames, whereClause);

            if (matWorkProcessVO.getHandleType() == 1) {//表示不筛选
                responseVO = nothandleScreenOperator(matWorkProcessVO);//对流程选中人群进行不筛选属性的操作
                noticeMatSystem(responseVO, matWorkProcessVO.getOrgId(), matWorkProcessVO.getId());//人群处理完成，通知mat系统

            } else if (matWorkProcessVO.getHandleType() == 2) {//表示需要对选中人群进行属性筛选
                responseVO = commonTypeMarketingRuleOperater(matWorkProcessVO);
                noticeMatSystem(responseVO, matWorkProcessVO.getOrgId(), matWorkProcessVO.getId());//人群处理完成，通知mat系统

            } else {
                ResponseVO.error(4001, "人群处理类型为不确定类型");
            }
            log.info("人群拆分返回结果responseVO =【" + responseVO.toString() + "】");

            //处理子流程
        //    executeSubProcess(matWorkProcessVO.getSubProcessVO());

        } catch (Exception e) {
            if(matWorkProcessVO.getType() == null){
                log.info( "重新计算流程群组人群发生错误，tagGroupId=【"+matWorkProcessVO.getUserGroupId()+"】,matWorkId=【"+matWorkProcessVO.getId()+"】");
            }
            log.error(e.getMessage(), e);
        }
    }


    @Transactional(rollbackFor = Exception.class)
    public void saveMarketingRule(MatWorkProcessVO matWorkProcessVO){
        //第一步保存流程规则信息
        MatWorkProcessDO processDO = new MatWorkProcessDO();
        BeanUtils.copyPropertiesIgnoreNull(matWorkProcessVO, processDO);
        processDO.setMatId(matWorkProcessVO.getId());
        JSONObject searchCondition = matWorkProcessVO.getSearchCondition();
        JSONObject execConfig = matWorkProcessVO.getExecConfig();
        JSONObject sendLimitConfig = matWorkProcessVO.getSendLimitConfig();
        JSONObject triggerCondition = matWorkProcessVO.getTriggerCondition();

        processDO.setConditions(searchCondition == null ? null : searchCondition.toJSONString());
        processDO.setExecConfig(execConfig == null ? null : execConfig.toJSONString());
        processDO.setSendLimitConfig(sendLimitConfig == null ? null : sendLimitConfig.toJSONString());
        processDO.setTriggerCondition(triggerCondition == null ? null : triggerCondition.toJSONString());
        if (matWorkProcessVO.getBeginTime() == null || matWorkProcessVO.getBeginTime().trim().equals("")) {
            processDO.setBeginTime(null);
        }
        if (matWorkProcessVO.getEndTime() == null || matWorkProcessVO.getEndTime().trim().equals("")) {
            processDO.setEndTime(null);
        }
        if (matWorkProcessVO.getExecTime() == null || matWorkProcessVO.getExecTime().trim().equals("")) {
            processDO.setExecTime(null);
        }
        if (matWorkProcessVO.getLastExecTime() == null || matWorkProcessVO.getLastExecTime().trim().equals("")) {
            processDO.setLastExecTime(null);
        }

        matWorkProcessVO.getHandleId();

        iMatMetadataWorkProcessMapper.insert(processDO);
        matWorkProcessVO.setWorksId(processDO.getId());//把生成的主键id放入
        Map<Long, Long> handleIdMap = new HashMap<>();
        Map<Long, Long> idMap = new HashMap<>();

        List<MatWorkProcessHandleVO> handles = matWorkProcessVO.getHandles();
        for (MatWorkProcessHandleVO handleVO : handles) {
            MatWorkProcessHandleDO handleDO = new MatWorkProcessHandleDO();
            BeanUtils.copyPropertiesIgnoreNull(handleVO, handleDO);
            if (handleVO.getExecTime().trim().equals("")) {
                handleDO.setExecTime(null);
            }
            handleDO.setWorkId(processDO.getId());
            handleDO.setMatId(handleVO.getId());
            handleDO.setMatWorkId(handleVO.getWorkId());
            iMatMetadataWorkProcessHandleMapper.insert(handleDO);
            handleIdMap.put(handleVO.getId(), handleDO.getId());
            idMap.put(handleVO.getId(), handleDO.getId());
        }
        matWorkProcessVO.setHandleIdMap(handleIdMap);
        List<MatWorkProcessActionVO> actions = matWorkProcessVO.getActions();
        for (MatWorkProcessActionVO actionVO : actions) {
            MatWorkProcessActionDO actionDO = new MatWorkProcessActionDO();
            BeanUtils.copyPropertiesIgnoreNull(actionVO, actionDO);
            actionDO.setMatId(actionVO.getId());
            actionDO.setMatWorkId(actionVO.getWorksId());
            actionDO.setMatHandleId(actionVO.getHandleId());
            actionDO.setWorksId(processDO.getId());
            actionDO.setDetails(actionVO.getDetails() == null ? null : actionVO.getDetails().toJSONString());
            Set<Long> longs = idMap.keySet();
            for (Long id : longs) {
                if (actionVO.getHandleId().equals(id)) {
                    actionDO.setHandleId(idMap.get(id));
                }
            }
            iMatMetadataWorkProcessActionMapper.insert(actionDO);
        }
    }



    //人群处理完成，通知mat系统
    void noticeMatSystem(ResponseVO responseVO, Long orgId, Long workId) {
        if (responseVO.getCode() == 0) {//code=0表示人群数据处理完成，开始通知mat系统
            log.info("流程workId【" + workId + "】的筛选人群数据处理完成，开始通知mat系统，机构orgId=【" + orgId + "】");
            String token = this.getMatTokenByOrgId(String.valueOf(orgId));//根据机构id获取token

            String workResult = "";
            JSONObject obj = new JSONObject();
            obj.put("organization_id", orgId);
            obj.put("matWorkId", workId);
            if (!token.equals("")) {

                log.info("------------------------------分割线workResult--obj------------------------------");
                log.info("请求参数obj =【" + obj.toJSONString() + "】");

                workResult = HttpRequest.post(matWorkNoticeUrl)
                        .header(Header.CONTENT_TYPE, "application/json")
                        .header(Header.AUTHORIZATION, "Bearer " + token)
                        .body(obj.toJSONString())
                        .timeout(20000)//超时，毫秒
                        .execute().body();
                log.info("请求参数workResult =【" + workResult + "】");
            }
            //表示缓存的token无效
            if(workResult.contains("401") && workResult.contains("Unauthenticated")){
                redisUtil.deleteCache(MAT_TOKEN_CACHE_KEY + "_" + orgId);
                token = this.getMatTokenByOrgId(orgId.toString());
                workResult = HttpRequest.post(matWorkNoticeUrl)
                        .header(Header.CONTENT_TYPE, "application/json")
                        .header(Header.AUTHORIZATION, "Bearer " + token)
                        .body(obj.toJSONString())
                        .timeout(20000)//超时，毫秒
                        .execute().body();
                log.info("因token无效，noticeMatSystem第二次请求结果workResult =【" + workResult + "】");
            }
            log.info("------------------------------分割线workResult------------------------------");
        }
    }

    public String getMatTokenByOrgId(String orgId) {
        String token = redisUtil.getRefresh(MAT_TOKEN_CACHE_KEY + "_" + orgId, String.class);
        if (token == null || token.equals("")) {
            JSONObject obj = new JSONObject();
            obj.put("organization_id", orgId);
            obj.put("grant_type", grantType);
            obj.put("client_id", clientId);
            obj.put("client_secret", clientSecret);

            String result1 = HttpRequest.post(matAuthUrl)//请求获取mat请求授权token
                    .header(Header.CONTENT_TYPE, "application/json")
                    .body(obj.toJSONString())
                    .timeout(20000)//超时，毫秒
                    .execute().body();

            log.info("------------------------------分割线result1------------------------------");
            log.info("请求结果result1 =【" + result1 + "】");
            JSONObject tokenObj = JSON.parseObject(result1);
            token = tokenObj.get("access_token") == null ? "" : tokenObj.get("access_token").toString();
            if (!token.equals("")) {
                redisUtil.set(MAT_TOKEN_CACHE_KEY + "_" + orgId, token, RedisConfig.expire);
            }
        }else{
            log.info("获取到的token =【" + token + "】");
        }
        return token;
    }

    /**
     * 事件营销流程属性筛选
     *
     * @param matWorkProcessVO
     * @return
     */
    @Override
    public List<TreeMap> eventProcessPropertyScreen(MatWorkProcessVO matWorkProcessVO)  {
        List<TreeMap> treeMaps = matCommonService.changeRuleToSQLWithResult(matWorkProcessVO);
        return treeMaps;
    }

    /**
     * 异步批量保存自动化营销流程的执行结果记录
     *
     * @param sendRecordVOs
     * @return
     */
    @Override
    @Async
    public void asyncBatchSaveSendRecord(List<MatWorkProcessSendRecordVO> sendRecordVOs) {
        try {
            //CountDownLatch countDownLatch = new CountDownLatch(sendRecordVOs.size());
            /*List<HashMap<String, Object>> hashMaps = JsonUtil.JsonToMapList(JsonUtil.toJson(sendRecordVOs));
            Set<String> strings = hashMaps.get(0).keySet();
            String column = humpToLine2(StringUtils.join(strings, ","));
            List<String> columns = Arrays.asList(column.split(","));
            TableName table = MatWorkProcessSendRecordDO.class.getAnnotation(TableName.class);
            String tableName = "";
            if (table != null) {
                tableName = table.value();
            }
            iDynamicService.insertPlusRecord(tableName, columns, hashMaps, null);*/
            TableName table = MatWorkProcessSendRecordDO.class.getAnnotation(TableName.class);
            for (MatWorkProcessSendRecordVO recordVO : sendRecordVOs) {
                if (recordVO.getBeginTime().trim().equals("")) {
                    recordVO.setBeginTime(null);
                }
                if (recordVO.getEndTime().trim().equals("")) {
                    recordVO.setEndTime(null);
                }

            }
            batchSave(sendRecordVOs, table);
            //     countDownLatch.await();
        } catch (Exception e) {
            log.error("asyncBatchSaveSendRecord异步批量保存自动化营销流程执行结果记录出错！！！");
            log.error(e.getMessage(), e);
        }

    }

    public void batchSave(List<?> lists, TableName table) {
        List<HashMap<String, Object>> hashMaps = JsonUtil.JsonToMapList(JsonUtil.toJson(lists));
        Set<String> strings = hashMaps.get(0).keySet();
        String column = humpToLine2(StringUtils.join(strings, ","));
        String columnStr = column.replaceAll("deleted", "is_delete");
        List<String> columns = Arrays.asList(columnStr.split(","));

        String tableName = "";
        if (table != null) {
            tableName = table.value();
        }
        iDynamicService.insertPlusRecord(tableName, columns, hashMaps, null);
    }


    public static String humpToLine2(String str) {
        Pattern humpPattern = Pattern.compile("[A-Z]");
        Matcher matcher = humpPattern.matcher(str);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(sb, "_" + matcher.group(0).toLowerCase());
        }
        matcher.appendTail(sb);
        return sb.toString();
    }


    //处理子流程
    private void executeSubProcess(MatWorkProcessVO subProcessVO){
        if(subProcessVO.getId() != null && subProcessVO.getHandleId() != null){
            //处理subProcessVO部分信息
            saveMarketingRule(subProcessVO);

            calculationTagGroupByProcessRule(subProcessVO);
        }
    }


    //单次或周期性营销流程处理人群操作
    private ResponseVO commonTypeMarketingRuleOperater(MatWorkProcessVO matWorkProcessVO) {
        try {
            log.info("开始根据属性条件进行人群筛选");
            List<TreeMap> treeMaps = matCommonService.changeRuleToSQLWithResult(matWorkProcessVO);//表示以群组人群为基础进行筛选的结果
            List<Long> idsList = new ArrayList<>();
            for (TreeMap map : treeMaps) {
                idsList.add(Long.valueOf(map.get("member_id").toString()));
            }
            log.info("workId=【"+matWorkProcessVO.getId()+"】流程属性筛选【命中】的人群数量为【"+idsList.size()+"】");
            List<MatWorkProcessHandleVO> handles = matWorkProcessVO.getHandles();
            Long workId = matWorkProcessVO.getWorksId();
            Long tagGroupId = matWorkProcessVO.getUserGroupId();
            Long matWorkId = matWorkProcessVO.getId();
            Map<Long, Long> handleIdMap = matWorkProcessVO.getHandleIdMap();

            List<MatWorkProcessHandleVO> processTypes = new ArrayList<>();
            List<MatWorkProcessHandleVO> processTypesHit = new ArrayList<>();
            for (int i = 0; i < handles.size(); i++) {
                Integer processType = handles.get(i).getProcessType();
                if (processType == 1) {//表示命中属性筛选的handle分支
                    processTypesHit.add(handles.get(i));
                } else {//表示未命中属性筛选的handle分支
                    processTypes.add(handles.get(i));
                }
            }

            openAbtestOperator(processTypesHit, idsList, workId, tagGroupId, matWorkId, handleIdMap, BusinessEnum.HITED.getCode());
            log.info("workId=【"+matWorkProcessVO.getId()+"】流程属性筛选【命中】的人群保存成功");
            if (!processTypes.isEmpty()) {
                List<Long> memberIdsByGroupId = getMemberIdListByTagGroupId(tagGroupId);
                memberIdsByGroupId.removeAll(idsList);
                log.info("workId=【"+matWorkProcessVO.getId()+"】流程属性筛选【未命中】的人群数量为【"+memberIdsByGroupId.size()+"】");
                openAbtestOperator(processTypes, memberIdsByGroupId, workId, tagGroupId, matWorkId, handleIdMap, BusinessEnum.NOTHIT.getCode());
                log.info("workId=【"+matWorkProcessVO.getId()+"】流程属性筛选【未命中】的人群保存成功");
            }

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ResponseVO.error(4001, "流程人群数据处理出错");
        }
        return ResponseVO.success("属性筛选人群处理成功");
    }


    //对流程选中人群进行不筛选属性的操作
    private ResponseVO nothandleScreenOperator(MatWorkProcessVO matWorkProcessVO) {
        try {
            List<MatWorkProcessHandleVO> handles = matWorkProcessVO.getHandles();
            Long workId = matWorkProcessVO.getWorksId();
            Long tagGroupId = matWorkProcessVO.getUserGroupId();
            Long matWorkId = matWorkProcessVO.getId();
            Map<Long, Long> handleIdMap = matWorkProcessVO.getHandleIdMap();
            Long parentHandleId = matWorkProcessVO.getHandleId();//主流程分支id

            List<Long> idsList = new ArrayList<>();
            if(parentHandleId != null && parentHandleId != 0L){
                idsList = getMemberIdListByParentHandleId(parentHandleId);//根据主流程分支id获取人群会员id
            }else{
                idsList = getMemberIdListByTagGroupId(tagGroupId);//根据群组id获取人群会员id
            }
            //判断是否开启ABTest
            if (matWorkProcessVO.getIsOpenAbtest() == 0) {//表示关闭
                Long matHandleId = handles.get(0).getId();
                Long handleId = handleIdMap.get(matHandleId);

                saveBatchCalculationData(idsList, workId, tagGroupId, matWorkId, matHandleId, handleId, BusinessEnum.NOTHIT.getCode());

            } else if (matWorkProcessVO.getIsOpenAbtest() == 1) {//表示开启
                openAbtestOperator(handles, idsList, workId, tagGroupId, matWorkId, handleIdMap, BusinessEnum.NOTHIT.getCode());
            } else {
                return ResponseVO.error(4001, "ABTest值不符合格式0或1");
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ResponseVO.error(4001, "流程人群数据处理出错");
        }

        return ResponseVO.success("不筛选处理人群操作成功");
    }

    //对开启ABTets的handle分支，按照百分比进行拆分
    private void openAbtestOperator(List<MatWorkProcessHandleVO> handles,
                                    List<Long> idsList, Long workId, Long tagGroupId,
                                    Long matWorkId, Map<Long, Long> handleIdMap, Integer isHit) throws Exception {

        int sumSize = 0;
        int count = 0;
        List<Long> ids;
        for (int i = 0; i < handles.size(); i++) {
            Long matHandleId = handles.get(i).getId();
            Long handleId = handleIdMap.get(matHandleId);
            if (i == handles.size() - 1) {
                ids = idsList.subList(sumSize, idsList.size());
            } else {
                Double percent = handles.get(i).getPercent();
                count = (int) ((idsList.size() * percent) / 100);
                ids = idsList.subList(sumSize, sumSize + count);
            }
            saveBatchCalculationData(ids, workId, tagGroupId, matWorkId, matHandleId, handleId, isHit);
            sumSize += count;
        }
    }

    //批量保存营销流程数据
    public void saveBatchCalculationData(List<Long> idsList, Long workId, Long tagGroupId,
                                         Long matWorkId, Long matHandleId, Long handleId, Integer isHit) throws Exception {
        Long orgId = 0L;
        Long brandId = 0L;
        List<TreeMap> treeMaps = getWoappOrgIdAndBrandIdByTagGroupId(tagGroupId);
        if (!treeMaps.isEmpty()) {
            orgId = Long.valueOf(String.valueOf(treeMaps.get(0).get("orgId") == null ? 0 : treeMaps.get(0).get("orgId")));
            brandId = Long.valueOf(String.valueOf(treeMaps.get(0).get("brandId") == null ? 0 : treeMaps.get(0).get("brandId")));
        }

        List<TreeMap> memberInfos = new ArrayList<>();
        if (idsList != null && idsList.size() > 5000) {
            log.info("----------------idsList大小大于5000，开始切分-----------------");
            int n = idsList.size() / 5000;
            if (idsList.size() % 20000 != 0) {
                n = n + 1;
            }
            List<List<Long>> ids01 = averageAssign(idsList, n);
            for (int j = 0; j < ids01.size(); j++) {
                memberInfos = getMemberInfoListByTagGroupId(ids01.get(j));
                structureModel(memberInfos, workId, tagGroupId, matWorkId, matHandleId, handleId, isHit, orgId, brandId);
                log.info("----------------切分第" + j + "组大小为" + ids01.get(j).size() + "-------------------");
            }
        } else {
            if (idsList != null && idsList.size() > 0) {
                memberInfos = getMemberInfoListByTagGroupId(idsList);
                structureModel(memberInfos, workId, tagGroupId, matWorkId, matHandleId, handleId, isHit, orgId, brandId);
            }
        }

        //     List<TreeMap> memberInfos = getMemberInfoListByTagGroupId(idsList);

    }


    private void structureModel(List<TreeMap> memberInfos, Long workId, Long tagGroupId, Long matWorkId, Long matHandleId, Long handleId, Integer isHit, Long orgId, Long brandId) throws Exception {
        List<MatCalculationDataDO> dataDOS = new ArrayList<>();
        for (TreeMap map : memberInfos) {

            String memberInfo = this.getMemberInfo(map);

            MatCalculationDataDO dataDO = new MatCalculationDataDO();
            dataDO.setBrandId(brandId);
            dataDO.setMatHandleId(matHandleId);
            dataDO.setMatWorkId(matWorkId);
            dataDO.setMemberId(Long.valueOf(map.get("member_id").toString()));
            dataDO.setOriginalId(orgId);
            dataDO.setTagGroupId(tagGroupId);
            dataDO.setWorkId(workId);
            dataDO.setHandleId(handleId);
            dataDO.setIsHit(isHit);
            dataDO.setMemberInfo(memberInfo);
            dataDO.setDeleted(BusinessEnum.NOTDELETED.getCode());
            dataDOS.add(dataDO);
        }
        //   this.saveBatch(dataDOS);//批量保存
        TableName table = MatCalculationDataDO.class.getAnnotation(TableName.class);
        batchSave(dataDOS, table);
    }

    //按照份数把list平均切割
    private <T> List<List<T>> averageAssign(List<T> data, int count) throws Exception {
        List<List<T>> result = new LinkedList<>();
        int num = data.size() / count;//计算每组数量
        for (int i = 0; i < count; i++) {
            List<T> list = new ArrayList<>();
            if (i < count - 1) {
                list = data.subList(i * num, (i + 1) * num);
            } else {//表示最后一组，考虑有余数，把余数部分直接放在最后一组中
                list = data.subList(i * num, data.size());
            }
            result.add(list);
        }
        return result;
    }

    //封装会员相关信息
    public String getMemberInfo(TreeMap map) throws Exception {

        JSONArray jsonArr = new JSONArray();
        JSONObject mobileObj = new JSONObject();
        mobileObj.put("platform", "mobile");
        mobileObj.put("attribute", map.get("mobile") == null ? "" : map.get("mobile"));
        jsonArr.add(mobileObj);
        JSONObject emailObj = new JSONObject();
        emailObj.put("platform", "email");
        emailObj.put("attribute", map.get("email") == null ? "" : map.get("email"));
        jsonArr.add(emailObj);
        JSONObject qywxObj = new JSONObject();
        qywxObj.put("platform", "qywx");
        qywxObj.put("attribute", "");
        jsonArr.add(qywxObj);
        JSONObject memberObj = new JSONObject();
        memberObj.put("platform", "member");
        memberObj.put("attribute", map.get("member_id") == null ? "" : map.get("member_id"));
        jsonArr.add(memberObj);
        JSONObject wechatObj = new JSONObject();
        wechatObj.put("platform", "wechat");
        wechatObj.put("appid", map.get("wechat_appid") == null ? "" : map.get("wechat_appid"));
        wechatObj.put("attribute", map.get("wechat_openid") == null ? "" : map.get("wechat_openid"));
        jsonArr.add(wechatObj);
        JSONObject miniappObj = new JSONObject();
        miniappObj.put("platform", "miniapp");
        miniappObj.put("appid", "");
        miniappObj.put("attribute", "");
        jsonArr.add(miniappObj);

        return jsonArr.toJSONString();
    }

    //根据群组id获取人群会员id
    private List<TreeMap> getWoappOrgIdAndBrandIdByTagGroupId(Long tagGroupId) throws Exception {
        //select o.woaap_org_id orgId,w.woaap_id brandId from sys_tag_group t,sys_brands b,sys_brands_org o,sys_woaap_brands w where t.brands_id=b.id and b.org_id=o.id and b.id=w.brands_id and t.id=10;
        List<String> tableNames = new ArrayList<>();
        tableNames.add("sys_tag_group t");
        tableNames.add("sys_brands b");
        tableNames.add("sys_brands_org o");
        tableNames.add("sys_woaap_brands w");
        List<String> columns = new ArrayList<>();
        columns.add("o.woaap_org_id orgId");
        columns.add("w.woaap_id brandId");
        String whereClause = "t.brands_id=b.id and b.org_id=o.id and b.id=w.brands_id and t.id=" + tagGroupId;
        List<TreeMap> treeMaps = iDynamicService.selectList(tableNames, columns, whereClause, null);
        return treeMaps;
    }

    //根据群组id获取人群会员详情
    private List<TreeMap> getMemberInfoListByTagGroupId(List<Long> idsList) throws Exception {
        String idList = "";
        for (int i = 0; i < idsList.size(); i++) {
            if (i != (idsList.size() - 1)) {
                idList += "'" + idsList.get(i) + "'" + ",";
            } else {
                idList += "'" + idsList.get(i) + "'";
            }
        }
        //SELECT m.id,m.member_id,m.unionid,m.email,m.mobile,m.is_delete FROM members m WHERE m.member_id in ()
        List<String> tableNames = new ArrayList<>();
        tableNames.add("members m");
        List<String> columns = new ArrayList<>();
        columns.add("m.member_id");
        columns.add("m.unionid");
        columns.add("m.email");
        columns.add("m.mobile");
        //    columns.add("m.is_delete");
        String whereClause = " m.member_id in (" + idList + ") GROUP BY m.member_id";
        List<TreeMap> mapList = iDynamicService.selectListMat(
                tableNames, columns, whereClause, "", null, null, null,null);
        return mapList;
    }


    private List<Long> getMemberIdListByParentHandleId(Long parentHandleId){
        List<String> tableNames = new ArrayList<>();
        tableNames.add("mat_map_calculation_data s");
        List<String> columns = new ArrayList<>();
        columns.add("s.member_id");
        String whereClause = "s.mat_handle_id=" + parentHandleId + " group by member_id";
        List<Long> idsList = iDynamicService.getIdsList(tableNames, columns, whereClause, null);
        return idsList;
    }

    //根据群组id获取人群会员id
    private List<Long> getMemberIdListByTagGroupId(Long tagGroupId) throws Exception {
        //select m.member_id from members m,sys_tag_group_user s where s.user_id=m.id and s.tag_group_id=9;
        List<String> tableNames = new ArrayList<>();
        tableNames.add("members m");
        tableNames.add("sys_tag_group_user s");
        List<String> columns = new ArrayList<>();
        columns.add("m.member_id");
        String whereClause = "s.user_id=m.id and s.tag_group_id=" + tagGroupId;
        List<Long> idsList = iDynamicService.getIdsList(tableNames, columns, whereClause, null);
        return idsList;
    }


}








