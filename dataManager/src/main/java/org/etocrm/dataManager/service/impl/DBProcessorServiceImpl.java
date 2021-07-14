package org.etocrm.dataManager.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.etocrm.core.util.JsonUtil;
import org.etocrm.core.util.ResponseVO;
import org.etocrm.dataManager.model.VO.DBProcessorColumnVO;
import org.etocrm.dataManager.model.VO.DBProcessorVO;
import org.etocrm.dataManager.service.IDBProcessorService;
import org.etocrm.dynamicDataSource.service.IDynamicService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;


@Slf4j
@Service
public class DBProcessorServiceImpl implements IDBProcessorService {

    @Resource
    private IDynamicService dynamicService;

    /**
     * @param dbProcessorVO
     * @Description: 判断表是否存在
     **/
    @Override
    public Boolean verifyTableExists(DBProcessorVO dbProcessorVO) {
        try {

            String whereClause = String.format("table_schema='%s' AND table_name = '%s'", dbProcessorVO.getTableSchema(), dbProcessorVO.getTableName());
            List<String> columns = new ArrayList<String>();
            columns.add("table_name");
            List list = dynamicService.getAllTable(columns, whereClause);
            if (list.size() < 1) {
                return false;
            }
            return true;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return false;
    }

    /**
     * @Description: 判断字段是否存在
     * @param: dbProcessorVO
     **/
    @Override
    public List<HashMap<String, DBProcessorColumnVO>> verifyColumnsExists(DBProcessorVO dbProcessorVO) {

        String whereClause = String.format("table_schema='%s' AND table_name = '%s'", dbProcessorVO.getTableSchema(), dbProcessorVO.getTableName());

        List<String> columns = new ArrayList<String>();
        columns.add("table_name as tableName");
        columns.add("table_schema as tableSchema");
        columns.add("column_comment as columnComment");
        columns.add("column_name as columnName");
        columns.add("data_type as dataType");
        columns.add("column_type as columnType");
        columns.add("column_key as columnKey");
        List<HashMap> list = dynamicService.getAllColumn(columns, whereClause);

        List<HashMap<String, DBProcessorColumnVO>> resultList = new ArrayList<>();
        List<String> requestColumn = dbProcessorVO.getColumn();
        for (HashMap column : list) {
            if (resultList.size() > list.size()) {
                break;
            }
            String columnName = String.valueOf(column.getOrDefault("columnName", ""));
            if (requestColumn.contains(columnName)) {
                DBProcessorColumnVO dbProcessorColumnVO = JsonUtil.readJson2Bean(JsonUtil.toJson(column), DBProcessorColumnVO.class);
                HashMap columnMap = new HashMap<>();
                columnMap.put(dbProcessorColumnVO.getColumnName(), dbProcessorColumnVO);
                resultList.add(columnMap);
            }

        }
        return resultList;
    }

    /**
     * @param dbProcessorVO
     * @Description: 创建表
     **/
    @Override
    public int createTable(DBProcessorVO dbProcessorVO) {
        List<String> columns = initColumns();
        int result = dynamicService.createTable(dbProcessorVO.getTableName(), columns);
        return result;
    }

    /**
     * @param dbProcessorVO
     * @Description: 新增字段
     * @return: int
     * @author: lingshuang.pang
     * @Date: 2020/9/4 9:46
     **/
    @Override
    public Boolean addColumns(DBProcessorVO dbProcessorVO) {

        List<String> columnList = dbProcessorVO.getColumn();

//        StringBuilder columnSb = new StringBuilder("( ");
//        for (String columnInfo : columnList) {
//            columnSb.append(columnInfo);
//            columnSb.append(",");
//        }
//        columnSb.deleteCharAt(columnSb.length() - 1);
//        columnSb.append(" )");
        //alert 成功 是0 重复  不成功会有异常
        Boolean result = true;
        try {
            dynamicService.addColumn(dbProcessorVO.getTableName(), columnList);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result = false;
        }
        return result;
    }


    /**
     * @param dbProcessorVO
     * @Description: 获取数据
     * @return: java.util.List
     * @author: lingshuang.pang
     * @Date: 2020/9/4 19:47
     **/
    public List<TreeMap> selectList(DBProcessorVO dbProcessorVO) {
//        choseDataSource(dbProcessorVO.getDatabaseId());

        List<String> tableNames = new ArrayList<String>();
        tableNames.add(dbProcessorVO.getTableName());

        String whereClause = "1=1";
        String orderStr = "";
        List<TreeMap> resultMap = dynamicService.selectList(tableNames, dbProcessorVO.getColumn(), whereClause, orderStr);
        return resultMap;
    }

    public List<TreeMap> selectList(DBProcessorVO dbProcessorVO, int start, int end) {
//        choseDataSource(dbProcessorVO.getDatabaseId());

        List<String> tableNames = new ArrayList<String>();
        tableNames.add(dbProcessorVO.getTableName());

        String whereClause = "1=1";
        String orderStr = "";
        List<TreeMap> resultMap = dynamicService.selectList(tableNames, dbProcessorVO.getColumn(), whereClause, orderStr, start, end);
        return resultMap;
    }


    @Override
    public ResponseVO verifyTableExistsService(DBProcessorVO dbProcessorVO) {
        return ResponseVO.success(this.verifyTableExists(dbProcessorVO));
    }

    @Override
    public ResponseVO verifyColumnsExistsService(DBProcessorVO dbProcessorVO) {
        return ResponseVO.success(this.verifyColumnsExists(dbProcessorVO));
    }

    @Override
    public ResponseVO createTableService(DBProcessorVO dbProcessorVO) {
        return ResponseVO.success(this.createTable(dbProcessorVO));
    }

    @Override
    public ResponseVO addColumnsService(DBProcessorVO dbProcessorVO) {
        return ResponseVO.success(this.addColumns(dbProcessorVO));
    }

    @Override
    public ResponseVO selectListService(DBProcessorVO dbProcessorVO) {
        return ResponseVO.success(this.selectList(dbProcessorVO));
    }

    @Override
    public ResponseVO truncateTable(DBProcessorVO dbProcessorVO) {
        dynamicService.truncateTable(dbProcessorVO.getTableName());
        return ResponseVO.success();
    }

    @Override
    public ResponseVO selectListByLimit(DBProcessorVO dbProcessorVO, int start, int end) {
        return ResponseVO.success(this.selectList(dbProcessorVO, start, end));
    }

    @Override
    public ResponseVO selectListById(DBProcessorVO dbProcessorVO, int start, int end, String primaryKey) {
        List<TreeMap> resultMap = dynamicService.selectListId(dbProcessorVO.getTableName(), dbProcessorVO.getColumn(), start, end, primaryKey);
        return ResponseVO.success(resultMap);
    }

    /**
     * @Description: 建表的默认值初始化
     * @return: java.util.List<java.lang.String>
     * @author: lingshuang.pang
     * @Date: 2020/9/4 11:03
     **/
    private List<String> initColumns() {
        List<String> columns = new ArrayList<String>();
        columns.add("`id` int NOT NULL AUTO_INCREMENT COMMENT '主键' PRIMARY KEY");
        columns.add("`revision` int DEFAULT NULL COMMENT '乐观锁'");
        columns.add("`created_by` int DEFAULT NULL COMMENT '创建人'");
        columns.add("`created_time` datetime NULL DEFAULT NULL COMMENT '创建时间'");
        columns.add("`updated_by` int DEFAULT NULL COMMENT '更新人'");
        columns.add("`updated_time` datetime NULL DEFAULT NULL COMMENT '更新时间'");
        columns.add("`is_delete` int DEFAULT '0' COMMENT '删除标志'");
        columns.add("`delete_by` int DEFAULT NULL COMMENT '删除人'");
        columns.add("`delete_time` datetime NULL DEFAULT NULL COMMENT '删除时间'");
        return columns;
    }
}
