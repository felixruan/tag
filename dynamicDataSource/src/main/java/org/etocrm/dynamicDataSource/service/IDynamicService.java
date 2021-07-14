package org.etocrm.dynamicDataSource.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.etocrm.dynamicDataSource.model.DO.UpdateBatchDO;
import org.springframework.scheduling.annotation.Async;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.CountDownLatch;

/**
 * @Author chengrong.yang
 * @date 2020/9/3 13:34
 */
public interface IDynamicService {

    Map selectById(List<String> tableNames, List<String> columns, String whereClause);

    List<TreeMap> selectList(List<String> tableNames, List<String> columns, String whereClause, String orderStr);

    List<TreeMap> selectListMat(List<String> tableNames, List<String> columns, String whereClause, String orderStr, String tagGroupId, String memberId, String handleId,String ids);

    IPage selectListByPage(long pageSize, long current, List<String> tableNames, List<String> columns, String whereClause, String orderByStr);

    String insertRecord(String tableName, Map columnAndValues, String whereClause);

    String insertPlusRecord(String tableName, List columns, List<HashMap<String, Object>> value, CountDownLatch countDownLatch);

    String insertRecordBatch(String tableName, String column, List<String> values);

    int updateRecord(String tableName, Map columnAndValues, String whereClause);

    int deleteRecord(String tableName, String whereClause);

    List<HashMap> selectListBySelective(List<String> tableNames, List<String> columns, Map whereClause, String orderStr, HashMap whereClauseOperMap);

    List<HashMap> getAllTable(List<String> columns, String whereClause);

    List<HashMap> getAllColumn(List<String> columns, String whereClause);

    int createTable(String tableName, List<String> columns);

    int addColumn(String tableName, List<String> columnList);

    void truncateTable(String tableName);

    int count(String tableName);

    int count(List<String> tableNames, String whereClause);

    List<TreeMap> selectList(List<String> tableNames, List<String> columns, String whereClause, String orderStr, int start, int end);

    List<TreeMap> selectListId(String tableNames, List<String> columns, int start, int end, String primaryKey);

    List<TreeMap> selectEtlListId(Long id, String tableNames, List<String> columns, int start, int end, String primaryKey);

    Integer deleteRecordByLimit(String sys_tag_property_user, String tag_id, Integer deleteTotal, Long tagId);

    Integer selectMaxId(String tableName, String primaryKey);

    List<Long> getIdsList(List<String> tableNames, List<String> columns, String whereClause, String orderStr);

    String getString(List<String> tableNames, String column, String whereClause);

    List<TreeMap> selectListBySql(String sql);

    @Async
    Integer batchUpdate(List<String> sql);

    @Async
    void updateBatch(UpdateBatchDO batchDO);

    String saveOrUpdateRecordBatch(String tableName, List columns, List<HashMap<String, Object>> value);

}
