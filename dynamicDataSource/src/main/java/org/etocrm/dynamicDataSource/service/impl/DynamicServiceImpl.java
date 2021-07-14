package org.etocrm.dynamicDataSource.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.etocrm.dynamicDataSource.mapper.IDynamicMapper;
import org.etocrm.dynamicDataSource.model.DO.DynamicDO;
import org.etocrm.dynamicDataSource.model.DO.UpdateBatchDO;
import org.etocrm.dynamicDataSource.service.IDynamicService;
import org.etocrm.dynamicDataSource.service.ISourceDBService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @Author chengrong.yang
 * @date 2020/9/3 13:34
 */
@Slf4j
@Service
public class DynamicServiceImpl implements IDynamicService {

    private static final String ORDERBY_STR = "orderByStr";
    private static final String PAGE_STARTROW = "startRow";
    private static final String PAGE_PAGESIZE = "pageSize";
    private static final String WHERECLAUSE_OPER_TYPE = "whereClauseOpertypeMap";

    @Autowired
    private IDynamicMapper dynamicMapper;

    @Autowired
    AsyncService asyncService;

    @Autowired
    ISourceDBService iSourceDBService;

    @Override
    public Map selectById(List<String> tableNames, List<String> columns, String whereClause) {
        if (tableNames == null || columns == null || tableNames.size() < 1 || columns.size() < 1 || StringUtils.isEmpty(whereClause)) {
            return null;
        } else {
            DynamicDO record = new DynamicDO();
            record.setColumns(columns);
            record.setTableNames(tableNames);
            boolean flag = false;
            for (String col : columns) {
                if (col.contains("is_delete")) {
                    flag = true;
                    break;
                }
            }
            if (flag) {
                for (String str : tableNames) {
                    String[] tableName = str.split(" ");
                    if (tableName.length == 2) {
                        whereClause = whereClause + " AND " + tableName[1] + ".is_delete = 0";
                    } else {
                        whereClause = whereClause + " AND " + tableName[0] + ".is_delete = 0";
                    }
                }
            }
            record.setWhereClause(whereClause);
            return dynamicMapper.selectById(record);
        }
    }

    @Override
    public List<TreeMap> selectList(List<String> tableNames, List<String> columns, String whereClause, String orderByStr) {
        if (tableNames == null || columns == null || tableNames.size() < 1 || columns.size() < 1 || StringUtils.isEmpty(whereClause)) {
            return null;
        } else {
            DynamicDO record = new DynamicDO();
            record.setColumns(columns);
            record.setTableNames(tableNames);
            boolean flag = false;
            for (String col : columns) {
                if (col.contains("is_delete")) {
                    flag = true;
                    break;
                }
            }
            if (flag) {
                for (String str : tableNames) {
                    String[] tableName = str.split(" ");
                    if (tableName.length == 2) {
                        whereClause = whereClause + " AND " + tableName[1] + ".is_delete = 0";
                    } else {
                        whereClause = whereClause + " AND " + tableName[0] + ".is_delete = 0";
                    }
                }
            }
            record.setWhereClause(whereClause);
            record.setOrderStr(orderByStr);
            return dynamicMapper.selectList(record);
        }
    }


    @Override
    public List<TreeMap> selectListMat(List<String> tableNames, List<String> columns, String whereClause, String orderByStr, String tagGroupId, String memberId, String handleId,String ids) {
        if (tableNames == null || columns == null || tableNames.size() < 1 || columns.size() < 1 || StringUtils.isEmpty(whereClause)) {
            return null;
        } else {
            DynamicDO record = new DynamicDO();
            record.setColumns(columns);
            record.setTableNames(tableNames);
            boolean flag = false;
            for (String col : columns) {
                if (col.contains("is_delete")) {
                    flag = true;
                    break;
                }
            }
            if (flag) {
                for (String str : tableNames) {
                    String[] tableName = str.split(" ");
                    if (tableName.length == 2) {
                        whereClause = whereClause + " AND " + tableName[1] + ".is_delete = 0";
                    } else {
                        whereClause = whereClause + " AND " + tableName[0] + ".is_delete = 0";
                    }
                }
            }
            record.setOperateTableName(ids);
            record.setWhereClause(whereClause);
            record.setOrderStr(orderByStr);
            record.setPrimaryKey(handleId);
            record.setColumn(tagGroupId);
            record.setId(memberId);
            return dynamicMapper.selectListMat(record);
        }
    }

    @Override
    public List<TreeMap> selectList(List<String> tableNames, List<String> columns, String whereClause, String orderByStr, int start, int end) {
        if (tableNames == null || columns == null || tableNames.size() < 1 || columns.size() < 1 || StringUtils.isEmpty(whereClause)) {
            return null;
        } else {
            DynamicDO record = new DynamicDO();
            record.setColumns(columns);
            record.setTableNames(tableNames);
            boolean flag = false;
            for (String col : columns) {
                if (col.contains("is_delete")) {
                    flag = true;
                    break;
                }
            }
            if (flag) {
                for (String str : tableNames) {
                    String[] tableName = str.split(" ");
                    if (tableName.length == 2) {
                        whereClause = whereClause + " AND " + tableName[1] + ".is_delete = 0";
                    } else {
                        whereClause = whereClause + " AND " + tableName[0] + ".is_delete = 0";
                    }
                }
            }
            record.setWhereClause(whereClause);
            record.setOrderStr(orderByStr);
            record.setLimitStart(start);
            record.setLimitEnd(end);
            return dynamicMapper.selectList(record);
        }
    }

    @Override
    public List<TreeMap> selectListId(String tableNames, List<String> columns, int start, int end, String primaryKey) {
        if (StringUtils.isEmpty(tableNames) || columns == null || columns.size() < 1) {
            return null;
        } else {
            DynamicDO record = new DynamicDO();
            record.setColumns(columns);
            record.setOperateTableName(tableNames);
            record.setPrimaryKey(primaryKey);
            record.setLimitStart(start);
            record.setLimitEnd(end);
            return dynamicMapper.selectListId(record);
        }
    }


    @Override
    public List<TreeMap> selectEtlListId(Long id, String tableNames, List<String> columns, int start, int end, String primaryKey) {
        if (StringUtils.isEmpty(tableNames) || columns == null || columns.size() < 1) {
            return null;
        } else {
            DynamicDO record = new DynamicDO();
            record.setColumns(columns);
            record.setOperateTableName(tableNames);
            record.setPrimaryKey(primaryKey);
            record.setLimitStart(start);
            record.setLimitEnd(end);
            //DynamicDataSource.setDataSource(id);
            iSourceDBService.getAllTable(id);
            return dynamicMapper.selectListId(record);
        }
    }

    @Override
    public Integer deleteRecordByLimit(String tableName, String tag_id, Integer deleteTotal, Long tagId) {
        DynamicDO record = new DynamicDO();
        record.setOperateTableName(tableName);
        record.setLimitStart(deleteTotal);
        Map map = new HashMap();
        map.put(tag_id, tagId);
        record.setOperrateColumnsAndValue(map);
        Integer integer = dynamicMapper.deleteRecordByLimit(record);
        return integer;
    }

    @Override
    public IPage selectListByPage(long pageSize, long current, List<String> tableNames, List<String> columns, String whereClause, String orderByStr) {
        if (tableNames == null || columns == null || tableNames.size() < 1 || columns.size() < 1 || StringUtils.isEmpty(whereClause)) {
            return null;
        } else {
            DynamicDO record = new DynamicDO();
            record.setColumns(columns);
            record.setTableNames(tableNames);
            for (String str : tableNames) {
                String[] tableName = str.split(" ");
                if (tableName.length == 2) {
                    whereClause = whereClause + " AND " + tableName[1] + ".is_delete = 0";
                } else {
                    whereClause = whereClause + " AND " + tableName[0] + ".is_delete = 0";
                }
            }
            record.setWhereClause(whereClause);
            record.setOrderStr(orderByStr);
            Page page = new Page();
            page.setSize(pageSize);
            page.setCurrent(current);
            IPage<List<HashMap>> resultPage = dynamicMapper.selectListByPage(page, record.getColumns(), record.getTableNames(), record.getWhereClause(), record.getOrderStr());
            return resultPage;
        }
    }

    @Override
    public String insertRecord(String tableName, Map columnAndValues, String whereClause) {
        if (tableName == null || columnAndValues == null || columnAndValues.size() < 1) {
            return null;
        } else {
            DynamicDO record = new DynamicDO();
            record.setOperateTableName(tableName);
            record.setOperrateColumnsAndValue(columnAndValues);
            dynamicMapper.insertRecord(record);
            return record.getId();
        }
    }

    @Override
//    @Async//@Async("asyncServiceExecutor")
    public String insertPlusRecord(String tableName, List columns, List<HashMap<String, Object>> value, CountDownLatch countDownLatch) {
        if (tableName == null || columns == null || columns.size() < 1 || value == null || value.size() < 1) {
            return null;
        } else {
            log.info("开始异步落地到数据库了");
            Map record = new HashMap();
            record.put("tableName", tableName);
            record.put("columnList", columns);
            record.put("valueList", value);
            //      DynamicDataSource.setDataSource(dataSourceId);
            int i = dynamicMapper.insertPlusRecord(record);
            if (null != countDownLatch) {
                countDownLatch.countDown();
            }
            return String.valueOf(i);
        }
    }

    @Override
    //@Async//@Async("asyncServiceExecutor")
    public String insertRecordBatch(String tableName, String column, List<String> values) {
        if (tableName == null || column == null || values.size() < 1) {
            return null;
        } else {
            try {
                if (values.size() >= 10000) {
                    List<List<String>> aaa = pageList(values);
                    CountDownLatch latch = new CountDownLatch(aaa.size());
                    for (List<String> list : aaa) {
                        asyncService.batchAsync(tableName,column,list, latch);
                    }
                    latch.await();
                } else {
                    DynamicDO record = new DynamicDO();
                    record.setOperateTableName(tableName);
                    record.setColumn(column);
                    record.setColumns(values);
                    dynamicMapper.insertRecordBatch(record);
                }

            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
            return "";
        }
    }



    public List<List<String>> pageList(List<String> values) {
        int limit = countStep(values.size());
        List<List<String>> mglist = new ArrayList<>();
        Integer saveMaxNum = 2000;
        Stream.iterate(0, n -> n + 1).limit(limit).forEach(i -> {
            mglist.add(values.stream().skip(i * saveMaxNum).limit(saveMaxNum).collect(Collectors.toList()));
        });
        return mglist;
    }

    /**
     * 计算切分次数
     */
    public Integer countStep(Integer size) {
        return (size + 2000 - 1) / 2000;
    }

    @Override
    public int updateRecord(String tableName, Map columnAndValues, String whereClause) {
        if (tableName == null || columnAndValues == null || columnAndValues.size() < 1 || StringUtils.isEmpty(whereClause)) {
            return 0;
        } else {
            DynamicDO record = new DynamicDO();
            record.setOperateTableName(tableName);
            record.setOperrateColumnsAndValue(columnAndValues);
            record.setWhereClause(whereClause);
            return dynamicMapper.updateRecord(record);
        }
    }

    @Override
    public int deleteRecord(String tableName, String whereClause) {
        if (tableName == null || StringUtils.isEmpty(whereClause)) {
            return 0;
        } else {
            DynamicDO record = new DynamicDO();
            record.setOperateTableName(tableName);
            record.setWhereClause(whereClause);
            return dynamicMapper.deleteRecord(record);
        }
    }

    @Override
    public List<HashMap> selectListBySelective(List<String> tableNames, List<String> columns, Map whereClause, String orderByStr, HashMap whereClauseOperMap) {
        if (tableNames == null || columns == null || tableNames.size() < 1 || columns.size() < 1) {
            return null;
        } else {
            if (whereClauseOperMap == null) {
                whereClauseOperMap = new HashMap(0);
            }
            DynamicDO record = new DynamicDO();
            record.setColumns(columns);
            record.setTableNames(tableNames);
            record.setWhereClauseMap(removeExtraConditionFromWhereclause(whereClause));
            record.setOrderStr(orderByStr);
            record.setWhereClauseOpertypeMap(whereClauseOperMap);
            return dynamicMapper.selectListBySelective(record);
        }
    }

    @Override
    public List<HashMap> getAllTable(List<String> columns, String whereClause) {
        DynamicDO record = new DynamicDO();
        record.setColumns(columns);
        record.setWhereClause(whereClause);
        return dynamicMapper.getAllTable(record);
    }

    @Override
    public List<HashMap> getAllColumn(List<String> columns, String whereClause) {
        DynamicDO record = new DynamicDO();
        record.setColumns(columns);
        record.setWhereClause(whereClause);
        return dynamicMapper.getAllColumn(record);
    }

    @Override
    public int createTable(String tableName, List<String> columns) {
        DynamicDO record = new DynamicDO();
        record.setOperateTableName(tableName);
        record.setColumns(columns);
        dynamicMapper.createTable(record);
        return 1;
    }

    @Override
    public int addColumn(String tableName, List<String> columnList) {
//        DynamicDO record = new DynamicDO();
//        record.setOperateTableName(tableName);
//        record.setColumns(columnList);
//        dynamicMapper.addColumn(record);
//        return 0;
        DynamicDO record;
        for (String s : columnList) {
            record = new DynamicDO();
            record.setOperateTableName(tableName);
            record.setColumn(s);
            dynamicMapper.addColumnSingle(record);
        }
        return 0;
    }

    @Override
    public void truncateTable(String tableName) {
        dynamicMapper.truncateTable(tableName);
    }

    @Override
    public int count(String tableName) {
        return dynamicMapper.count(tableName);
    }

    @Override
    public int count(List<String> tableNames, String whereClause) {
        if (CollectionUtil.isEmpty(tableNames)) {
            return 0;
        } else {
            DynamicDO record = new DynamicDO();
            record.setTableNames(tableNames);
            record.setWhereClause(whereClause);
            return dynamicMapper.countByWhere(record);
        }
    }

    private Map removeExtraConditionFromWhereclause(Map whereClauseMap) {
        if (whereClauseMap != null && whereClauseMap.size() > 0) {
            whereClauseMap.remove(ORDERBY_STR);
            whereClauseMap.remove(PAGE_STARTROW);
            whereClauseMap.remove(PAGE_PAGESIZE);
            whereClauseMap.remove(WHERECLAUSE_OPER_TYPE);
        }
        return whereClauseMap;
    }

    @Override
    public Integer selectMaxId(String tableName, String primaryKey) {
        Map<String, String> map = new HashMap<>();
        map.put("tableName", tableName);
        map.put("key", primaryKey);
        return dynamicMapper.selectMaxId(map);
    }

    @Override
    public List<Long> getIdsList(List<String> tableNames, List<String> columns, String whereClause, String orderByStr) {
        if (tableNames == null || columns == null || tableNames.size() < 1 || columns.size() < 1 || StringUtils.isEmpty(whereClause)) {
            return null;
        } else {
            DynamicDO record = new DynamicDO();
            record.setColumns(columns);
            record.setTableNames(tableNames);
            boolean flag = false;
            for (String col : columns) {
                if (col.contains("is_delete")) {
                    flag = true;
                    break;
                }
            }
            if (flag) {
                for (String str : tableNames) {
                    String[] tableName = str.split(" ");
                    if (tableName.length == 2) {
                        whereClause = whereClause + " AND " + tableName[1] + ".is_delete = 0";
                    } else {
                        whereClause = whereClause + " AND " + tableName[0] + ".is_delete = 0";
                    }
                }
            }
            record.setWhereClause(whereClause);
            record.setOrderStr(orderByStr);
            return dynamicMapper.getIdsList(record);
        }
    }

    @Override
    public String getString(List<String> tableNames, String column, String whereClause) {
        if (StringUtils.isEmpty(whereClause) || StringUtils.isBlank(column) || CollectionUtil.isEmpty(tableNames)) {
            return null;
        } else {
            DynamicDO record = new DynamicDO();
            record.setColumn(column);
            record.setTableNames(tableNames);

            record.setWhereClause(whereClause);
            return dynamicMapper.getString(record);
        }
    }

    @Override
    public List<TreeMap> selectListBySql(String sql) {
        if (StringUtils.isBlank(sql)) {
            return null;
        } else {
            return dynamicMapper.selectListBySql(sql);
        }
    }

    @Override
    public Integer batchUpdate(List<String> sqlList) {
        if (CollectionUtil.isEmpty(sqlList)) {
            return null;
        } else {
            Long beginTime = System.currentTimeMillis();
            Integer integer = dynamicMapper.batchUpdate(sqlList);
            log.info("============ batchUpdate end,cost:{}ms,updateSize:{},updateCount:{}", System.currentTimeMillis() - beginTime, sqlList.size(), integer);
            return integer;
        }
    }

    @Override
    public void updateBatch(UpdateBatchDO batchDO) {
        Object list = batchDO.getDataList();
        if (null == list) {
            return;
        }
        Long beginTime = System.currentTimeMillis();
        int i = dynamicMapper.updateBatch(batchDO);
        log.info("============ batchUpdate end,cost:{}ms,,updateCount:{}", System.currentTimeMillis() - beginTime, i);

    }


    @Override
    public String saveOrUpdateRecordBatch(String tableName, List columns, List<HashMap<String, Object>> value) {
        if (tableName == null || CollectionUtil.isEmpty(columns) || CollectionUtil.isEmpty(value)) {
            return null;
        } else {
            Map record = new HashMap();
            record.put("tableName", tableName);
            record.put("columnList", columns);
            record.put("valueList", value);
            int i = dynamicMapper.saveOrUpdateRecordBatch(record);
            return String.valueOf(i);
        }
    }
}
