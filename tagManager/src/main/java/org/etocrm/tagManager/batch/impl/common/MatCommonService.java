package org.etocrm.tagManager.batch.impl.common;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.etocrm.core.enums.BusinessEnum;
import org.etocrm.core.util.ResponseVO;
import org.etocrm.dynamicDataSource.service.IDynamicService;
import org.etocrm.dynamicDataSource.util.RedisUtil;
import org.etocrm.tagManager.api.IAuthenticationService;
import org.etocrm.tagManager.api.IDataManagerService;
import org.etocrm.tagManager.constant.TagConstant;
import org.etocrm.tagManager.model.DO.*;
import org.etocrm.tagManager.model.VO.DBProcessorVO;
import org.etocrm.tagManager.model.VO.SysDictVO;
import org.etocrm.tagManager.model.VO.batch.SysBrandsListAllResponseVO;
import org.etocrm.tagManager.model.VO.mat.MatProcessConditionVO;
import org.etocrm.tagManager.model.VO.mat.MatWorkProcessVO;
import org.etocrm.tagManager.model.VO.tag.SysTagBrandsInfoVO;
import org.etocrm.tagManager.service.*;
import org.etocrm.tagManager.util.Relations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @Author: peter.li
 * @Date: 10:48 2020/12/15
 * @Desc:
 */

@Service
@Slf4j
public class MatCommonService {

    @Resource
    IDataManagerService dataManagerService;

    @Resource
    IDynamicService dynamicService;

    @Resource
    ISysModelTableService sysModelTableService;

    @Autowired
    ISysModelTableColumnService sysModelTableColumnService;

    private static final String[] NUMBER_DICT_CODE = new String[]{
            "greater_than", "less_than", "greater_equal", "less_equal",
            "sum_greater_than", "sum_less_than", "sum_greater_equal", "sum_less_equal",
            "count_greater_than", "count_less_than", "count_greater_equal", "count_less_equal",
            "distinct_count_greater_than", "distinct_count_less_than", "distinct_count_greater_equal", "distinct_count_less_equal",
            "avg_greater_than", "avg_less_than", "avg_greater_equal", "avg_less_equal",
    };


    /**
     * 单个属性多个规则的逻辑运算（同且/同或）
     *
     * @param matWorkProcessVO
     * @return
     * @throws Exception
     */
    public List<TreeMap> changeRuleToSQLWithResult(MatWorkProcessVO matWorkProcessVO) {
        List<TreeMap> processScreenData = new ArrayList<>();
        try{
            Long handleId = matWorkProcessVO.getHandleId();
            Long tagGroupId = matWorkProcessVO.getUserGroupId();
            if(handleId != null && handleId == 0){
                handleId = null;
            }
            if(tagGroupId != null && tagGroupId == 0){
                tagGroupId = null;
            }


            List<TreeMap<String, Object>> list1 = new ArrayList<>();
            Integer ruleRelationId;

            JSONObject searchCondition = matWorkProcessVO.getSearchCondition();
            String logicOperator = searchCondition.get("logic_operator").toString();
            if (logicOperator.equals("and")) {
                ruleRelationId = 9;
            } else if (logicOperator.equals("or")) {
                ruleRelationId = 10;
            } else {
                ruleRelationId = 0;
            }

            //      List<MatProcessConditionVO> conditions = JSONArray.parseArray(searchCondition.get("conditions").toString(), MatProcessConditionVO.class);
            //    String conditionsStr = String.valueOf(searchCondition.get("conditions"));
            JSONArray jsonArray = JSONArray.parseArray(searchCondition.get("conditions") == null ? "" : JSON.toJSONString(searchCondition.get("conditions")));

            if(jsonArray == null){
                return processScreenData;
            }
            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject jsonObject = JSONObject.parseObject(jsonArray.get(i).toString());
                //    log.info("获取到规则" + columns);
                //   JSONObject jsonObject = JSONObject.parseObject(columns);
                //获取规则中模型表 规则中模型列字段
                Long modelTableId = jsonObject.getLong("modelTableId");
                Long columnId = jsonObject.getLong("columnId");
                if (null == modelTableId || null == columnId) {
                    return processScreenData;
                }
                //查询主库
                SysModelTableDO sysModelTableDO = sysModelTableService.selectSysModelTableById(modelTableId);
                SysModelTableColumnDO sysModelTableColumnDO = sysModelTableColumnService.selectSysModelColumnTableById(columnId);
                if (sysModelTableDO == null || sysModelTableColumnDO == null) {
                    return processScreenData;
                }
                //获取规则中运算逻辑
                JSONObject logicalOperation = jsonObject.getJSONObject("logicalOperation");
                if (null == logicalOperation) {
                    return processScreenData;
                }
                //sql获取结果集
                TreeMap<String, Object> treeMap = this.appendRelationSqlStr(sysModelTableDO, logicalOperation, sysModelTableColumnDO);
                log.info("封装返回的sql数据" + treeMap.size());
                list1.add(treeMap);
            }
            //封装返回数据
            processScreenData = getResultBySql(list1, ruleRelationId, tagGroupId, handleId, matWorkProcessVO.getMemberId(),matWorkProcessVO.getMemberIds());
        }catch (Exception e){
            log.error("changeRuleToSQLWithResult()出错！！！");
            log.error(e.getMessage(),e);
        }

        return processScreenData;
    }


    /**
     * 整合sql 并查询结果
     *
     * @param list1
     * @param ruleRelationId
     * @return
     * @throws Exception
     */
    private List<TreeMap> getResultBySql(List<TreeMap<String, Object>> list1, Integer ruleRelationId, Long tagGroupId, Long handleId, String memberId,String ids) {
        List<TreeMap> processScreenData = new ArrayList<>();
        try{
            StringBuilder whereStr = new StringBuilder();
            StringBuilder tablesStr = new StringBuilder();
            StringBuilder group = new StringBuilder();
            StringBuilder modelStr = new StringBuilder();
            String id = " DISTINCT members.id,members.member_id,members.unionid,members.email,members.mobile,members.is_delete";
            //根据ruleRelationId去字典表中查对应的dict_value，拼接或且sql
            String relationKey = this.getDictValue(ruleRelationId);
            Set<String> tables = new TreeSet<>();
            Set<String> columns = new TreeSet<>();
            columns.add(id);
            for (int i = 0; i < list1.size(); i++) {
                TreeMap<String, Object> treeMap = list1.get(i);
                //模型间规则
                if (treeMap.get("model") != null) {
                    modelStr.append(treeMap.get("model")).append(" and ");
                }
                //属性间条件
                if (treeMap.get("where") != null) {
                    whereStr.append(treeMap.get("where")).append(" " + relationKey + " ");
                }
                //属性间分组
                if (treeMap.get("groupBy") != null) {
                    group.append(treeMap.get("groupBy")).append(" " + relationKey + " ");
                }
                Set<String> setTable = (Set<String>) treeMap.get("from");
                tables.addAll(setTable);
            }
            for (String table : tables) {
                tablesStr.append(table + ",");
            }
            log.info("table：" + tablesStr);

            //判断是否存在group By
            String groupBy = "";
            String whereCase = "";
            if (group.length() > 0) {
                groupBy = group.substring(0, group.lastIndexOf(relationKey));
                groupBy = " GROUP BY members.id HAVING " + groupBy;
            }
            //    whereCase = " members.brands_id = " + brandsInfoVO.getBrandsId() + " and members.org_id = " + brandsInfoVO.getOrgId() + " and ";
            log.info("groupBy：" + groupBy);
            if (whereStr.length() > 0) {
                log.info("====wherestr===" + whereStr);
                whereCase = whereCase + whereStr.substring(0, whereStr.lastIndexOf(relationKey));
            }
            log.info("====whereCase=====" + whereCase);

            String model = "";
            if (modelStr.length() > 0) {
                model = modelStr.substring(0, modelStr.lastIndexOf("and"));
            }
            log.info("model：" + model);

            // 封装sql远程切换数据源

            DBProcessorVO dbProcessorV = new DBProcessorVO();
            List<String> b = new ArrayList<>(columns);
            dbProcessorV.setColumn(b);
            if (StringUtils.isNotBlank(tablesStr)) {
                dbProcessorV.setTableName(tablesStr.substring(0, tablesStr.lastIndexOf(",")));
                if (StringUtils.isBlank(model)) {
                    if(StringUtils.isBlank(memberId)){
                        dbProcessorV.setWhereCase(whereCase + groupBy);
                    }else{
                        if(StringUtils.isBlank(whereCase)){
                            dbProcessorV.setWhereCase("( members.member_id = " +memberId+ " ) " + groupBy);
                        }else{
                            dbProcessorV.setWhereCase(whereCase + " and ( members.member_id = " +memberId+ " ) " + groupBy);
                        }
                    }
                } else {
                    if (StringUtils.isBlank(whereCase)) {
                        if(StringUtils.isBlank(memberId)){
                            dbProcessorV.setWhereCase("( " + model + ") " + groupBy);
                        }else{
                            dbProcessorV.setWhereCase("( " + model + ") and ( members.member_id =" + memberId + " ) " + groupBy);
                        }
                    } else {
                        if(StringUtils.isBlank(memberId)){
                            dbProcessorV.setWhereCase("( " + model + ") and ( " + whereCase + " ) " + groupBy);
                        }else{
                            dbProcessorV.setWhereCase("( " + model + ") and ( " + whereCase + " ) and ( members.member_id =" + memberId + " ) " + groupBy);
                        }
                    }
                }

                //and members.member_id = ${id}
                log.info("最终的sql->>>>>>>>>>" + dbProcessorV.toString());
                List<String> tableNames = new ArrayList<>();
                tableNames.add(dbProcessorV.getTableName());
                String whereClause = dbProcessorV.getWhereCase();
            /*if (StringUtils.isEmpty(whereClause)) {
                whereClause = "1=1";
            }*/
                processScreenData = dynamicService.selectListMat(
                        tableNames, dbProcessorV.getColumn(), whereClause, "", tagGroupId == null ? null : tagGroupId.toString(), memberId, handleId == null ? null : handleId.toString(),ids);
                //    map = this.selectUserDo(listResponseVO, map);
            }
        }catch (Exception e){
            log.error("getResultBySql()出错！！！");
            log.error(e.getMessage(),e);
        }

        return processScreenData;
    }

    /**
     * @param map
     * @return
     */
    private Map<String, Object> selectUserDo(List<TreeMap> listResponseVO, Map<String, Object> map) throws Exception{

        SysTagPropertyUserPO userDO;
        SysTagPropertyWeChatUserPO weChatUserPO;
        List<SysTagPropertyUserPO> userPOS = new ArrayList<>();
        for (int i = 0; i < listResponseVO.size(); i++) {
            userDO = new SysTagPropertyUserPO();
            Object o = listResponseVO.get(i).get("id");
            if (null == o) {
                continue;
            }
            userDO.setUserId(Long.parseLong(o.toString()));
            userPOS.add(userDO);
            map.put("flag", true);
            map.put("data", userPOS);
        }
        return map;
    }


    /**
     * 解析sql
     *
     * @param modelTableDO
     * @param logicalOperation
     * @param modelTableColumnDO
     * @return
     * @throws Exception
     */
    private TreeMap<String, Object> appendRelationSqlStr(SysModelTableDO modelTableDO, JSONObject logicalOperation,
                                                         SysModelTableColumnDO modelTableColumnDO) throws Exception{
        Integer id;
        Object startValue;
        Object endValue;
        String controlTypeDictCode;
        String dictCode;
        Integer childId = null;
        log.info("获取运算规则 : " + logicalOperation);
        JSONObject jsonChild = (JSONObject) logicalOperation.get("child");
        if (null != jsonChild) {
            childId = logicalOperation.getInteger("id");
            id = jsonChild.getInteger("id");
            startValue = jsonChild.get("startValue");
            endValue = jsonChild.get("endValue");
            controlTypeDictCode = jsonChild.getString("controlTypeDictCode");
            dictCode = jsonChild.getString("dictCode");
        } else {
            id = logicalOperation.getInteger("id");
            startValue = logicalOperation.get("startValue");
            endValue = logicalOperation.get("endValue");
            controlTypeDictCode = logicalOperation.getString("controlTypeDictCode");
            dictCode = logicalOperation.getString("dictCode");
        }
        log.info("拿到的规则值 childId:" + childId + " id:" + id + " startValue:" + startValue + " endValue:" + endValue + " controlTypeDictCode:" + controlTypeDictCode);
        switch (controlTypeDictCode) {
            case "input_box":
                //输入框
                if (!Arrays.asList(NUMBER_DICT_CODE).contains(dictCode)) {
                    startValue = "'" + startValue + "'";
                }
                break;
            case "number_input_box":
                //暂不处理  数值输入框
                break;
            case "number_input_box_group":
                //暂不处理 数值输入框组
                break;
            case "date_select_box":
                //日期选择框
                startValue = "STR_TO_DATE('" + startValue + "','%Y-%m-%d %T')";
                break;
            case "date_select_box_group":
                //日期选择框组
                startValue = "STR_TO_DATE('" + startValue + "','%Y-%m-%d %T')";
                endValue = "STR_TO_DATE('" + endValue + "','%Y-%m-%d %T')";
                break;
            case "select_table":
                startValue = this.select_table(startValue);
                break;
            default:
                break;
        }
        TreeMap<String, Object> map = new TreeMap<>();
        //条件
        StringBuilder whereBuffer = new StringBuilder();
        //拼接模型关联关系
        StringBuilder modelBuffer = new StringBuilder();
        String tableName = modelTableDO.getModelTable();
        Set<String> tables = new TreeSet<>();
        log.info("考虑多表映射关系");
        map = this.tableRelationRule(modelTableDO, tables, modelBuffer, tableName, map);
        String columnName = modelTableColumnDO.getColumnName();
        tables.add(tableName + " as " + tableName);
        //判断是否存在逻辑组2
        if (childId != null) {
            columnName = this.getOperationSymbolBySelect(this.getDictValue(childId), columnName, tableName);
            String groupBy = this.getOperationSymbolByWhere(this.getDictValue(id), columnName, startValue, endValue);
            map.put("groupBy", groupBy);
        } else {
            columnName = tableName + "." + columnName;
            String whereClause = this.getOperationSymbolByWhere(this.getDictValue(id), columnName, startValue, endValue);
            whereBuffer.append(whereClause);
            map.put("where", whereBuffer.toString());
        }
        map.put("from", tables);
        String sql = "select " + columnName + " from " + tables.toString() + " where " + whereBuffer + " model " + modelBuffer;
        log.info("我就是看看sql长啥样>>>>" + sql);
        return map;
    }

    private Object select_table(Object startValue) throws Exception{
        String select_table = "";
        log.info("未处理下拉框:" + startValue);
        String[] split = startValue.toString().split(",");
        for (String s : split) {
            select_table = select_table + "'" + s + "',";
        }
        startValue = select_table.substring(0, select_table.lastIndexOf(","));
        log.info("已处理下拉框:" + startValue);
        return startValue;
    }

    /**
     * 对表关系进行处理
     *
     * @param modelTableDO
     * @param tables
     * @param modelBuffer
     * @param tableName
     * @param map
     * @return
     */
    private TreeMap<String, Object> tableRelationRule(SysModelTableDO modelTableDO, Set<String> tables, StringBuilder modelBuffer,
                                                      String tableName, TreeMap<String, Object> map) throws Exception{
        if (null != modelTableDO.getRelationRule() && modelTableDO.getRelationRule().length() > 2) {
            log.info("拿到的modelTableDO:" + modelTableDO.toString());
            //判断规则最终查询哪张表,什么where条件？
            ObjectMapper mapper = new ObjectMapper();
            LinkedList<Relations> pp3 = null;
            try {
                pp3 = mapper.readValue(modelTableDO.getRelationRule(), new TypeReference<LinkedList<Relations>>() {
                });
            } catch (IOException e) {
                log.error(e.getMessage(), e);
            }
            String fk = "";
            String fkTable = "";
            for (Relations relations : pp3) {
                log.info("拿到的Relations:" + relations.toString());
                String relationTable;
                String relationColumn;
                String currentColumn;
                //进行判断为空不参与逻辑，查询主库
                SysModelTableDO tableVo = sysModelTableService.selectSysModelTableById(relations.getTableId());
                SysModelTableColumnDO relationIdVo = sysModelTableColumnService.selectSysModelColumnTableById(relations.getRelationId());
                SysModelTableColumnDO currentId = sysModelTableColumnService.selectSysModelColumnTableById(relations.getCurrentId());
                if (tableVo == null || relationIdVo == null || currentId == null) {
                    break;
                }
                relationTable = tableVo.getModelTable();
                relationColumn = relationIdVo.getColumnName();
                currentColumn = currentId.getColumnName();
                tables.add(relationTable + " as " + relationTable);
                if (StringUtils.isBlank(fk)) {
                    if (StringUtils.isNotBlank(relationColumn) && StringUtils.isNotBlank(currentColumn)) {
                        //拼接模型关联关系
                        modelBuffer.append(tableName + "." + currentColumn + " = " + relationTable + "." + relationColumn + " and ");
                        log.info("拼接模型关联关系" + modelBuffer);
                    }
                } else {
                    if (StringUtils.isNotBlank(relationColumn) && StringUtils.isNotBlank(currentColumn)) {
                        //拼接模型关联关系
                        modelBuffer.append(fkTable + "." + fk + " = " + relationTable + "." + relationColumn + " and ");
                        log.info("拼接模型关联关系" + modelBuffer);
                    }
                }
                if (relations.getFk() != null) {
                    fkTable = relationTable;
                    SysModelTableColumnDO fkVo = sysModelTableColumnService.selectSysModelColumnTableById(relations.getFk());
                    if (fkVo == null) {
                        break;
                    }
                    fk = fkVo.getColumnName();
                }
            }
            if (modelBuffer.lastIndexOf("and") == -1) {
                map.put("model", modelBuffer);
            } else {
                map.put("model", modelBuffer.substring(0, modelBuffer.lastIndexOf("and")));
            }
            log.info("拿到模型table..." + tables.toString() + "...拿到模型关联..." + modelBuffer);

        }
        return map;
    }

    /**
     * 运算规则 where
     *
     * @return
     */
    private String getOperationSymbolByWhere(String value, String columnName, Object startValue, Object endValue) throws Exception{
        switch (value) {
            case "IS NOT NULL":
                return columnName + " IS NOT NULL ";
            case "IS NULL":
                return columnName + " IS NULL ";
            case ">= ? AND ? <=":
                String[] strs = value.split("\\?");
                return columnName + strs[0] + startValue + strs[1] + columnName + strs[2] + endValue;
            case "NOW()":
                String str = " DATEDIFF(NOW()," + columnName + ")>=" + startValue;
                if (StringUtils.isNotBlank(endValue.toString())) {
                    str += " and DATEDIFF(NOW()," + columnName + ")<=" + endValue;
                }
                return str;
            default:
                return columnName + " " + value.replace("?", String.valueOf(startValue));
        }
    }

    /**
     * 获取字典
     *
     * @param id
     * @return
     */
    private String getDictValue(Integer id) throws Exception{
        //查询主库
        ResponseVO<SysDictVO> detail = dataManagerService.detail(id.longValue());
        if (detail.getCode() != 0) {
            log.error("这里查询字典貌似出错了");
            return "";
        }
        return detail.getData().getDictValue();
    }

    /**
     * * 25 求和 41 计数 98 平均 45 去重计数
     *
     * @param columnName
     * @return
     */
    private String getOperationSymbolBySelect(String value, String columnName, String tableName) throws Exception{
        return value.replace("?", tableName + "." + columnName);
    }

}
