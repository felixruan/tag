package org.etocrm.tagManager.batch.impl.common;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.etocrm.core.enums.BusinessEnum;
import org.etocrm.core.util.ResponseVO;
import org.etocrm.dynamicDataSource.service.IDynamicService;
import org.etocrm.dynamicDataSource.util.RedisUtil;
import org.etocrm.tagManager.api.IDataManagerService;
import org.etocrm.tagManager.constant.TagConstant;
import org.etocrm.tagManager.model.DO.*;
import org.etocrm.tagManager.model.VO.DBProcessorVO;
import org.etocrm.tagManager.model.VO.SysDictVO;
import org.etocrm.tagManager.model.VO.tag.SysTagBrandsInfoVO;
import org.etocrm.tagManager.service.*;
import org.etocrm.tagManager.util.Relations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.*;

/**
 * @Author: dkx
 * @Date: 18:48 2020/11/10
 * @Desc:
 */

@Service
@Slf4j
public class BatchTagCommonService {

    @Resource
    private RedisUtil redisUtil;

    @Resource
    ISysTagService sysTagService;

    @Resource
    private ISysTagPropertyService sysTagPropertyService;

    @Resource
    ISysTagPropertyRuleService iSysTagPropertyRuleService;

    @Resource
    IDataManagerService dataManagerService;

    @Resource
    IDynamicService dynamicService;

    @Resource
    ISysModelTableService sysModelTableService;

    @Autowired
    ISysModelTableColumnService sysModelTableColumnService;

    @Resource
    private ISysTagPropertyUserService sysTagPropertyUserService;

    private static final String[] NUMBER_DICT_CODE = new String[]{
            "greater_than", "less_than", "greater_equal", "less_equal",
            "sum_greater_than", "sum_less_than", "sum_greater_equal", "sum_less_equal",
            "count_greater_than", "count_less_than", "count_greater_equal", "count_less_equal",
            "distinct_count_greater_than", "distinct_count_less_than", "distinct_count_greater_equal", "distinct_count_less_equal",
            "avg_greater_than", "avg_less_than", "avg_greater_equal", "avg_less_equal",
    };



    /**
     * 获取品牌下需要执行定时任务的标签
     *
     * @param brandsInfoVO
     * @return
     */
    public List<SysTagDO> getSysTagList(SysTagBrandsInfoVO brandsInfoVO) {
        return sysTagService.getSysTagListByDataManager(brandsInfoVO);
    }

    public List<SysTagPropertyUserPO> userSet(Long tagId, SysTagBrandsInfoVO brandsInfoVO, String tagType, String appId) {
        try {
            List<SysTagPropertyUserPO> userSet = new ArrayList<>();
            List<SysTagProperty> sysTagProperties = this.getSysTagPropertyList(tagId);
            if (CollectionUtil.isEmpty(sysTagProperties)) {
                return null;
            }
            for (SysTagProperty sysTagProperty : sysTagProperties) {
                log.info("获取逻辑属性,tagId:" + tagId + ",propertyId:" + sysTagProperty.getId());
                Integer ruleRelationshipId = sysTagProperty.getRuleRelationshipId();
                List<SysTagPropertyRuleDO> rule = sysTagProperty.getRule();
                Map<String, Object> bb = this.changeRuleToSQLWithResult(rule, ruleRelationshipId, tagId, sysTagProperty.getId(), brandsInfoVO, tagType, appId);
                log.info("tagId:" + tagId + ",propertyId:" + sysTagProperty.getId());
                if (null == bb.get("flag")) {
                    continue;
                }
                Boolean flag = (Boolean) bb.get("flag");
                if (null != flag) {
                    if (flag) {
                        List<SysTagPropertyUserPO> data = (List<SysTagPropertyUserPO>) bb.get("data");
                        log.info("user size:" + data.size());
                        userSet.addAll(data);
                    }
                }
            }
            return userSet;
        } catch (Exception e) {
            log.error("tagListener userSet error,e:{}", e.getMessage(), e);
        }
        return null;
    }

    public List<SysTagPropertyWeChatUserPO> weChatUserSet(Long tagId, SysTagBrandsInfoVO brandsInfoVO, String tagType, String appId) {
        try {
            List<SysTagPropertyWeChatUserPO> userSet = new ArrayList<>();
            List<SysTagProperty> sysTagProperties = this.getSysTagPropertyList(tagId);
            if (CollectionUtil.isEmpty(sysTagProperties)) {
                return null;
            }
            for (SysTagProperty property : sysTagProperties) {
                log.info("获取逻辑 属性,tagId:" + tagId + ",propertyId:" + property.getId());
                Integer ruleRelationshipId = property.getRuleRelationshipId();
                List<SysTagPropertyRuleDO> rule = property.getRule();
                Map<String, Object> bb = this.changeRuleToSQLWithResult(rule, ruleRelationshipId, tagId, property.getId(), brandsInfoVO, tagType, appId);
                log.info("tagId :" + tagId + ",propertyId :" + property.getId());
                if (null == bb.get("flag")) {
                    continue;
                }
                Boolean flag = (Boolean) bb.get("flag");
                if (null != flag) {
                    if (flag) {
                        List<SysTagPropertyWeChatUserPO> data = (List<SysTagPropertyWeChatUserPO>) bb.get("data");
                        log.info("user size:" + data.size());
                        userSet.addAll(data);
                    }
                }
            }
            return userSet;
        } catch (Exception e) {
            log.error("tagListener userSet error,e:{}", e.getMessage(), e);
        }
        return null;
    }

    /**
     * 获取标签属性   对应的规则
     *
     * @param tagId
     * @return
     */
    private List<SysTagProperty> getSysTagPropertyList(Long tagId) {
        //查询标签属性
        List<SysTagProperty> sysTagProperties = new ArrayList<>();
        List<SysTagPropertyDO> sysTagPropertyDOs = sysTagPropertyService.getTagPropertyByTagId(tagId);
        if (CollectionUtil.isNotEmpty(sysTagPropertyDOs)) {
            SysTagProperty sysTagProperty;
            for (SysTagPropertyDO sysTagPropertyDO : sysTagPropertyDOs) {
                sysTagProperty = new SysTagProperty();
                sysTagProperty.setRuleRelationshipId(sysTagPropertyDO.getRuleRelationshipId());
                //查询规则
                List<SysTagPropertyRuleDO> sysTagPropertyRuleDOs = iSysTagPropertyRuleService.getTagPropertyRuleByTagId(sysTagPropertyDO.getId());
                if (CollectionUtil.isNotEmpty(sysTagPropertyRuleDOs)) {
                    sysTagProperty.setTagId(tagId);
                    sysTagProperty.setId(sysTagPropertyDO.getId());
                    sysTagProperty.setRule(sysTagPropertyRuleDOs);
                    sysTagProperties.add(sysTagProperty);
                }
            }
        }
        return sysTagProperties;
    }


    /**
     * 单个属性多个规则的逻辑运算（同且/同或）
     *
     * @param ruleArr
     * @param ruleRelationId
     * @param tagId
     * @param propertyId
     * @return
     * @throws Exception
     */
    private Map<String, Object> changeRuleToSQLWithResult(List<SysTagPropertyRuleDO> ruleArr, Integer ruleRelationId,
                                                          Long tagId, Long propertyId, SysTagBrandsInfoVO brandsInfoVO, String tagType, String appId) {
        Map<String, Object> map = new HashMap<>();
        List<TreeMap<String, Object>> list1 = new ArrayList<>();
        for (SysTagPropertyRuleDO sysTagPropertyRuleDO : ruleArr) {
            String columns = sysTagPropertyRuleDO.getColumns();
            log.info("获取到规则" + columns);
            JSONObject jsonObject = JSONObject.parseObject(columns);
            //获取规则中模型表 规则中模型列字段
            Long modelTableId = jsonObject.getLong("modelTableId");
            Long columnId = jsonObject.getLong("columnId");
            if (null == modelTableId || null == columnId) {
                return map;
            }
            //查询主库
            SysModelTableDO sysModelTableDO = sysModelTableService.selectSysModelTableById(modelTableId);
            SysModelTableColumnDO sysModelTableColumnDO = sysModelTableColumnService.selectSysModelColumnTableById(columnId);
            if (sysModelTableDO == null || sysModelTableColumnDO == null) {
                return map;
            }
            //获取规则中运算逻辑
            JSONObject logicalOperation = jsonObject.getJSONObject("logicalOperation");
            if (null == logicalOperation) {
                return map;
            }
            //sql获取结果集
            TreeMap<String, Object> treeMap = this.appendRelationSqlStr(sysModelTableDO, logicalOperation, sysModelTableColumnDO, tagType);
            log.info("封装返回的sql数据" + treeMap.size());
            list1.add(treeMap);
        }
        //封装返回数据
        map = getResultBySql(list1, ruleRelationId, tagId, propertyId, brandsInfoVO, tagType, appId);
        return map;
    }

    /**
     * 整合sql 并查询结果
     *
     * @param list1
     * @param ruleRelationId
     * @param tagId
     * @param propertyId
     * @return
     * @throws Exception
     */
    private Map<String, Object> getResultBySql(List<TreeMap<String, Object>> list1, Integer ruleRelationId,
                                               Long tagId, Long propertyId, SysTagBrandsInfoVO brandsInfoVO, String tagType, String appId) {
        StringBuilder whereStr = new StringBuilder();
        StringBuilder tablesStr = new StringBuilder();
        StringBuilder group = new StringBuilder();
        StringBuilder modelStr = new StringBuilder();
        String id;
        if (tagType.equals(BusinessEnum.MEMBERS.getCode().toString())) {
            id = " DISTINCT members.id, members.is_delete";
        } else {
            id = " DISTINCT wechat_user.id, wechat_user.wechat_openid, wechat_user.is_delete";
        }
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
        String whereCase;
        if (tagType.equals(BusinessEnum.MEMBERS.getCode().toString())) {
            if (group.length() > 0) {
                groupBy = group.substring(0, group.lastIndexOf(relationKey));
                groupBy = " GROUP BY members.id HAVING " + groupBy;
            }
            whereCase = " members.brands_id = " + brandsInfoVO.getBrandsId() + " and members.org_id = " + brandsInfoVO.getOrgId() + " and ";
            if (whereStr.length() > 0) {
                log.info("====wherestr===" + whereStr);
                whereCase = whereCase + whereStr.substring(0, whereStr.lastIndexOf(relationKey));
            }
        } else {
            if (group.length() > 0) {
                groupBy = group.substring(0, group.lastIndexOf(relationKey));
                groupBy = " GROUP BY wechat_user.id HAVING " + groupBy;
            }
            whereCase = " ( wechat_user.wechat_appid = '" + appId + "') and (";
            if (whereStr.length() > 0) {
                log.info("====wherestr===" + whereStr);
                whereCase = whereCase + whereStr.substring(0, whereStr.lastIndexOf(relationKey)) + " ) ";
            }
        }
        log.info("groupBy====：" + groupBy);
        log.info("====whereCase========" + whereCase);

        String model = "";
        if (modelStr.length() > 0) {
            model = modelStr.substring(0, modelStr.lastIndexOf("and"));
        }
        log.info("model=====：" + model);

        // 封装sql远程切换数据源
        Map<String, Object> map = new HashMap<>();
        DBProcessorVO dbProcessorV = new DBProcessorVO();
        List<String> b = new ArrayList<>(columns);
        dbProcessorV.setColumn(b);
        if (StringUtils.isNotBlank(tablesStr)) {
            dbProcessorV.setTableName(tablesStr.substring(0, tablesStr.lastIndexOf(",")));
            if (StringUtils.isBlank(model)) {
                dbProcessorV.setWhereCase(whereCase + groupBy);
            } else {
                if (StringUtils.isBlank(whereCase)) {
                    dbProcessorV.setWhereCase("( " + model + ") " + groupBy);
                } else {
                    dbProcessorV.setWhereCase("( " + model + ") and ( " + whereCase + " ) " + groupBy);
                }
            }
            log.info("最终的sql-==========:" + dbProcessorV.toString());
            List<String> tableNames = new ArrayList<>();
            tableNames.add(dbProcessorV.getTableName());
            String whereClause = dbProcessorV.getWhereCase();
            List<TreeMap> listResponseVO = dynamicService.selectList(tableNames, dbProcessorV.getColumn(), whereClause, "");
            map = this.selectUserDo(listResponseVO, map, tagId, propertyId, tagType);
        }
        return map;
    }

    /**
     * @param map
     * @param tagId
     * @param propertyId
     * @return
     */
    private Map<String, Object> selectUserDo(List<TreeMap> listResponseVO, Map<String, Object> map, Long tagId, Long propertyId, String tagType) {

        if (CollectionUtil.isEmpty(listResponseVO)) {
            log.info("查不到结果要删除之前标签相关数据 tagId :" + tagId);
            map.put("flag", false);
            map.put("data", tagId);
        } else {
            SysTagPropertyUserPO userDO;
            SysTagPropertyWeChatUserPO weChatUserPO;
            if (tagType.equals(BusinessEnum.MEMBERS.getCode().toString())) {
                log.info("开始保存标签的人员数据tagId:" + tagId + " 属性id：" + propertyId);
                List<SysTagPropertyUserPO> userPOS = new ArrayList<>();
                for (int i = 0; i < listResponseVO.size(); i++) {
                    userDO = new SysTagPropertyUserPO();
                    Object o = listResponseVO.get(i).get("id");
                    if (null == o) {
                        continue;
                    }
                    userDO.setUserId(Long.parseLong(o.toString()));
                    userDO.setPropertyId(propertyId);
                    userDO.setTagId(tagId);
                    userPOS.add(userDO);
                    map.put("flag", true);
                    map.put("data", userPOS);
                }
            } else {
                log.info("开始保存标签的人员数据tagId:" + tagId + " 属性id：" + propertyId);
                List<SysTagPropertyWeChatUserPO> userPOS = new ArrayList<>();
                for (int i = 0; i < listResponseVO.size(); i++) {
                    weChatUserPO = new SysTagPropertyWeChatUserPO();
                    Object id = listResponseVO.get(i).get("id");
                    Object wechatOpenid = listResponseVO.get(i).get("wechat_openid");
                    if (null == id) {
                        continue;
                    }
                    weChatUserPO.setOpenId(wechatOpenid + "");
                    weChatUserPO.setUserId(Long.parseLong(id.toString()));
                    weChatUserPO.setPropertyId(propertyId);
                    weChatUserPO.setTagId(tagId);
                    userPOS.add(weChatUserPO);
                    map.put("flag", true);
                    map.put("data", userPOS);
                }
            }

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
                                                         SysModelTableColumnDO modelTableColumnDO, String tagType) {
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
        if (tagType.equals(BusinessEnum.MEMBERS.getCode().toString())) {
            map = this.tableRelationRule(modelTableDO, tables, modelBuffer, tableName, map);
        }
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

    private Object select_table(Object startValue) {
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
                                                      String tableName, TreeMap<String, Object> map) {
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
    private String getOperationSymbolByWhere(String value, String columnName, Object startValue, Object endValue) {
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
    private String getDictValue(Integer id) {
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
    private String getOperationSymbolBySelect(String value, String columnName, String tableName) {
        return value.replace("?", tableName + "." + columnName);
    }

    /**
     * @param tagId
     * @param sysTagPropertyUserDOS
     */
    public void asyncInstallTagData(Long tagId, List<SysTagPropertyUserPO> sysTagPropertyUserDOS) {
        log.info("write拿到tagId: " + tagId);
        log.info("write拿到tagId 的对应查到的人数: " + sysTagPropertyUserDOS.size());
//        int limit = countStep(sysTagPropertyUserDOS.size());
//        List<List<SysTagPropertyUserPO>> mglist = new ArrayList<>();
//        Integer saveMaxNum = Integer.valueOf(redisUtil.getValueByKey(TagConstant.SAVE_MAX_NUMBER).toString());
//        Stream.iterate(0, n -> n + 1).limit(limit).forEach(i -> {
//            mglist.add(sysTagPropertyUserDOS.stream().skip(i * saveMaxNum).limit(saveMaxNum).collect(Collectors.toList()));
//        });
//        log.info("分割成几组数据:" + mglist.size());
       // for (List<SysTagPropertyUserPO> list : mglist) {
            sysTagPropertyUserService.batchInsert(sysTagPropertyUserDOS);

      //  }
    }

    /**
     * @param tagId
     * @param sysTagPropertyUserDOS
     */
    public void asyncInstallTagWeChatData(Long tagId, List<SysTagPropertyWeChatUserPO> sysTagPropertyUserDOS) {
        log.info("write拿到tagId: " + tagId);
//        log.info("write拿到tagId 的对应查到的人数: " + sysTagPropertyUserDOS.size());
//        int limit = countStep(sysTagPropertyUserDOS.size());
//        List<List<SysTagPropertyWeChatUserPO>> mglist = new ArrayList<>();
//        Integer saveMaxNum = Integer.valueOf(redisUtil.getValueByKey(TagConstant.SAVE_MAX_NUMBER).toString());
//        Stream.iterate(0, n -> n + 1).limit(limit).forEach(i -> {
//            mglist.add(sysTagPropertyUserDOS.stream().skip(i * saveMaxNum).limit(saveMaxNum).collect(Collectors.toList()));
//        });
//        log.info("分割成几组数据:" + mglist.size());
       // for (List<SysTagPropertyWeChatUserPO> list : mglist) {
            sysTagPropertyUserService.batchInsertWeChat(sysTagPropertyUserDOS);

       // }
    }

    /**
     * 计算切分次数
     */
    private Integer countStep(Integer size) {
        Integer saveMaxNum = Integer.valueOf(redisUtil.getValueByKey(TagConstant.SAVE_MAX_NUMBER).toString());
        return (size + saveMaxNum - 1) / saveMaxNum;
    }

    /**
     * 修改：覆盖人数，下次执行时间，执行状态
     *
     * @param tagId
     */
    public void updateTagInfo(Long tagId, int coveredPeopleNum) {
        SysTagDO sysTagDO = sysTagService.getTagById(tagId);
        if (null != sysTagDO) {
            sysTagDO.setTagLastUpdateDate(DateUtil.beginOfDay(new Date()));
            Date nextUpdateTime = sysTagService.getNextUpdateTime(sysTagDO.getTagLastUpdateDate(), null, sysTagDO.getTagUpdateFrequency());
            if (null != nextUpdateTime) {
                sysTagDO.setTagNextUpdateDate(nextUpdateTime);
            }

            //set 覆盖人数
            sysTagDO.setCoveredPeopleNum(coveredPeopleNum);
            sysTagDO.setTagPropertyChangeExecuteStatus(BusinessEnum.EXECUTED.getCode());
            sysTagService.update(sysTagDO);
        }
    }


    public Integer getCountByPropertyId(Long propertyId) {
        return sysTagPropertyUserService.getCountByPropertyId(propertyId);
    }

    public void updateCoveredPeopleNum(Long tagId, Integer dataType, int value) {
        sysTagService.updateCoveredPeopleNum(tagId, dataType, value);
    }
}
