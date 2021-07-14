package org.etocrm.dataManager.listener;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.etocrm.dataManager.model.DO.EtlProcessingRuleDO;
import org.etocrm.dataManager.model.VO.EtlUpdateSqlVO;
import org.etocrm.dataManager.model.VO.SysBrands.SysBrandsListAllResponseVO;
import org.etocrm.dynamicDataSource.model.DO.UpdateBatchDO;
import org.etocrm.dynamicDataSource.service.IDynamicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author lingshuang.pang
 */
@Slf4j
@Component
public class EtlProcessingUpdateListener {

    @Autowired
    private IDynamicService dynamicService;


    @KafkaListener(topics = "${CUSTOM.KAFKA.TOPIC.ETL_PROCESSING_UPDATE}", groupId = "${CUSTOM.KAFKA.GROUP.ETL_PROCESSING_GROUP}")
    public void etlProcessingUpdateListener(String updateStr, Acknowledgment ack) {
        long beginTime = System.currentTimeMillis();
        EtlUpdateSqlVO sqlVO = JSONObject.parseObject(updateStr, EtlUpdateSqlVO.class);
        log.info("=========== etl processing update updateStr:{}", sqlVO);
        this.doUpdate(sqlVO.getParamDataList(), sqlVO.getRuleList(), sqlVO.getBrandsInfo());
        log.error("========= etlProcessingTableListener data:{},cost:{}", sqlVO.getRuleList(), System.currentTimeMillis() - beginTime);

        ack.acknowledge();
    }

    public void doUpdate(List<TreeMap> paramDatas, List<EtlProcessingRuleDO> ruleList, SysBrandsListAllResponseVO brandsInfo) {
        int updateSize = ruleList.get(0).getUpdateSize();

        int limit = countStep(paramDatas.size(), updateSize);
        List<List<TreeMap>> paramDataList = new ArrayList<>();
        Stream.iterate(0, n -> n + 1).limit(limit).forEach(i -> {
            paramDataList.add(paramDatas.stream().skip(updateSize * i).limit(updateSize).collect(Collectors.toList()));
        });
        // case when 方式生成sql
        UpdateBatchDO updateBatchDO = null;
        for (List<TreeMap> paramData : paramDataList) {
            Long beginTime = System.currentTimeMillis();
            updateBatchDO = this.getUpdateSql(paramData, ruleList, brandsInfo);
            log.info("========= getUpdateSql end,cost:{}ms", System.currentTimeMillis() - beginTime);
            this.doUpdateSql(updateBatchDO);
        }

        // 每条数据生成一个sql
//        List<String> updateSql = null;
//        for (List<TreeMap> paramData : paramDataList) {
//            Long beginTime = System.currentTimeMillis();
//            updateSql = this.getUpdateSql(paramData, ruleList, brandsInfo);
//            log.info("========= getUpdateSql end,cost:{}ms", System.currentTimeMillis() - beginTime);
//            this.doUpdateSql(updateSql);
//        }
    }


    private UpdateBatchDO getUpdateSql(List<TreeMap> dataList, List<EtlProcessingRuleDO> ruleList, SysBrandsListAllResponseVO brandsInfo) {
        EtlProcessingRuleDO baseRuleDO = ruleList.get(0);
        UpdateBatchDO result = new UpdateBatchDO();
        result.setTableName(baseRuleDO.getTargetTable());
        result.setOrgId(brandsInfo.getOrgId());
        result.setBrandsId(brandsInfo.getId());
        result.setWhereCol(baseRuleDO.getTargetWhereColumn());

        Map<Long, Map<Object, List<TreeMap>>> queryDataMap = new HashMap<>(8);
        Set<String> updateColumnSet = new HashSet<>();

        for (EtlProcessingRuleDO rule : ruleList) {
            String querySql = this.getQuerySql(rule, brandsInfo, dataList);
            List<TreeMap> queryData = dynamicService.selectListBySql(querySql);
            queryDataMap.put(rule.getId(), queryData.stream().collect(Collectors.groupingBy(queryDataInfo -> queryDataInfo.get(rule.getParamColumn()).toString())));
            updateColumnSet.addAll(Arrays.asList(rule.getTargetColumns().split(",")));
        }
        List<String> updateColumnList = new ArrayList<>(updateColumnSet);
        result.setUpdateColNameList(updateColumnList);

        List<Map> updateSqlList = new ArrayList<>();
        for (TreeMap data : dataList) {
            Map sql = this.getUpdateSql(data, ruleList, brandsInfo, queryDataMap);
            if (null != sql) {
                updateSqlList.add(sql);
            }
        }
        result.setDataList(updateSqlList);

        return result;
    }

    private String getQuerySql(EtlProcessingRuleDO rule, SysBrandsListAllResponseVO brandsInfo, List<TreeMap> dataList) {
        String querySql = rule.getProcessingSql()
                .replace("#{orgId}", brandsInfo.getOrgId() + "")
                .replace("#{brandsId}", brandsInfo.getId() + "");

        String paramColumn = rule.getParamColumn();
        Set<Object> paramColumnDataSet = dataList.stream().map(data -> data.get(paramColumn)).collect(Collectors.toSet());

        if (paramColumnDataSet.size() > 1) {
            StringBuilder whereSb = new StringBuilder();
            String whereStr;
            for (Object paramColumnData : paramColumnDataSet) {
                whereSb.append("'");
                whereSb.append(paramColumnData);
                whereSb.append("',");
            }
            whereStr = whereSb.substring(0, whereSb.length() - 1);
            querySql = querySql.replace("#{whereParam}", "in (" + whereStr + ")");
        } else {
            //单条
            querySql = querySql.replace("#{whereParam}", "='" + paramColumnDataSet.iterator().next() + "'");
        }
        return querySql;
    }

    private Map getUpdateSql(TreeMap data, List<EtlProcessingRuleDO> ruleList, SysBrandsListAllResponseVO brandsInfo, Map<Long, Map<Object, List<TreeMap>>> queryDataMap) {
        Map result = new HashMap();

        int index = 0;
        try {
            for (EtlProcessingRuleDO rule : ruleList) {
                if (null == data.get(rule.getParamColumn())) {
                    continue;
                }
                String paramColumnValue = data.get(rule.getParamColumn()).toString();

                if (StringUtils.equals(rule.getParamColumn(), rule.getTargetWhereColumnValueSource())) {
                    result.put(rule.getTargetWhereColumn(), paramColumnValue);
                }

                result = this.getUpdateSqlMap(result, rule, paramColumnValue, queryDataMap.get(rule.getId()));
                index++;
            }
        } catch (Exception e) {
            log.error("============= ?? data：{},data.get(rule.getParamColumn()):{}", data, data.get(ruleList.get(index).getParamColumn()));
        }
        if (result.keySet().size() < 1) {
            result = null;
        }
        return result;
    }

    private Map getUpdateSqlMap(Map map, EtlProcessingRuleDO rule, String paramColumnValue, Map<Object, List<TreeMap>> queryDataMap) {

        String[] targetColumnList = rule.getTargetColumns().split(",");

        List<TreeMap> columnDataList = queryDataMap.get(paramColumnValue);

        TreeMap columnData = new TreeMap();
        if (CollectionUtil.isNotEmpty(columnDataList)) {
            columnData = columnDataList.get(0);
        }
        for (int i = 0; i < targetColumnList.length; i++) {
            String col = targetColumnList[i];
            Object value = columnData.get(col);
            if (null == value) {
                if ("mobile_available".equals(col)) {
                    map.put(col, "0");
                } else {
                    map.put(col, null);
                }
            } else {
                map.put(col, value);
            }
        }
        return map;
    }


    private int countStep(Integer size, Integer batchSize) {
        return (size + batchSize - 1) / batchSize;
    }


    private void doUpdateSql(UpdateBatchDO batchDO) {
        dynamicService.updateBatch(batchDO);
    }


    /**
     * ======   以下的方式是拼接成update sql 直接执行，每个一条，测试下来，没有上面的case when 效率高，所以暂未使用 ========
     */
    private void doUpdateSql(List<String> sqlList) {
        dynamicService.batchUpdate(sqlList);
    }

    /**
     * 处理规则 获取更新sql
     *
     * @param ruleList
     * @param brandsInfo
     */
    private List<String> getUpdateSqlStrList(List<TreeMap> dataList, List<EtlProcessingRuleDO> ruleList, SysBrandsListAllResponseVO brandsInfo) {

        Map<Long, Map<Object, List<TreeMap>>> queryDataMap = new HashMap<>(8);
        for (EtlProcessingRuleDO rule : ruleList) {
            String querySql = this.getQuerySql(rule, brandsInfo, dataList);
            List<TreeMap> queryData = dynamicService.selectListBySql(querySql);

            queryDataMap.put(rule.getId(), queryData.stream().collect(Collectors.groupingBy(queryDataInfo -> queryDataInfo.get(rule.getParamColumn()).toString())));
        }

        //整合查询的数据组成update
        List<String> updateSqlList = new ArrayList<>();
        for (TreeMap data : dataList) {
            String sql = this.getUpdateSqlStr(data, ruleList, brandsInfo, queryDataMap);
            if (StringUtils.isNotBlank(sql)) {
                updateSqlList.add(sql);
            }
        }
        return updateSqlList;
    }

    private String getUpdateSqlStr(TreeMap data, List<EtlProcessingRuleDO> ruleList, SysBrandsListAllResponseVO brandsInfo, Map<Long, Map<Object, List<TreeMap>>> queryDataMap) {
        EtlProcessingRuleDO baseRule = ruleList.get(0);

        String updateSql = "update #{updateTable} set #{set} where #{column}='#{value}' and org_id=#{orgId} and brands_id=#{brandsId} "
                .replace("#{updateTable}", baseRule.getTargetTable())
                .replace("#{column}", baseRule.getTargetWhereColumn())
                .replace("#{orgId}", brandsInfo.getOrgId() + "")
                .replace("#{brandsId}", brandsInfo.getId() + "");

        String updateSet = "";
        StringBuilder sb = new StringBuilder();
        int index = 0;
        try {
            for (EtlProcessingRuleDO rule : ruleList) {
                if (null == data.get(rule.getParamColumn())) {
                    continue;
                }
                String paramColumnValue = data.get(rule.getParamColumn()).toString();

                if (StringUtils.equals(rule.getParamColumn(), rule.getTargetWhereColumnValueSource())) {
                    updateSql = updateSql.replace("#{value}", paramColumnValue);
                }

                sb = this.getUpdateSqlSb(sb, rule, paramColumnValue, queryDataMap.get(rule.getId()));
                index++;
            }
        } catch (Exception e) {
            log.error("============= ?? data：{},data.get(rule.getParamColumn()):{}", data, data.get(ruleList.get(index).getParamColumn()));
        }

        if (sb.length() > 1) {
            updateSet = sb.substring(0, sb.length() - 1);
        }

        if (StringUtils.isNotBlank(updateSet)) {
            updateSql = updateSql.replace("#{set}", updateSet);
            return updateSql;
        }
        return null;
    }

    private StringBuilder getUpdateSqlSb(StringBuilder sb, EtlProcessingRuleDO rule, String paramColumnValue, Map<Object, List<TreeMap>> queryDataMap) {

        String[] targetColumnList = rule.getTargetColumns().split(",");

        List<TreeMap> columnDataList = queryDataMap.get(paramColumnValue);

        TreeMap columnData = new TreeMap();
        if (CollectionUtil.isNotEmpty(columnDataList)) {
            columnData = columnDataList.get(0);
        }
        for (int i = 0; i < targetColumnList.length; i++) {
            String col = targetColumnList[i];
            Object value = columnData.get(col);
            sb.append(col);
            if (null == value) {
                if ("mobile_available".equals(col)) {
                    sb.append("=0,");
                } else {
                    sb.append("=NULL,");
                }
            } else {
                sb.append("='");
                sb.append(value);
                sb.append("',");
            }
        }
        return sb;
    }

}
