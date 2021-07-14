package org.etocrm.dynamicDataSource.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import org.etocrm.dynamicDataSource.model.DO.DictionaryJsonDO;
import org.etocrm.dynamicDataSource.model.DO.DynamicDO;
import org.etocrm.dynamicDataSource.model.DO.UpdateBatchDO;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * @Author chengrong.yang
 * @date 2020/9/3 13:16
 */

public interface IDynamicMapper {

    Map selectById(DynamicDO record);

    List<TreeMap> selectList(DynamicDO record);

    List<TreeMap> selectListMat(DynamicDO record);

    List<TreeMap> selectListId(DynamicDO record);

    int insertRecord(DynamicDO record);

    int insertRecordBatch(DynamicDO record);

    int insertPlusRecord(Map map);

    int updateRecord(DynamicDO record);

    int deleteRecord(DynamicDO record);

    List<HashMap> selectListBySelective(DynamicDO record);

    List<HashMap> getAllTable(DynamicDO record);

    List<HashMap> getAllColumn(DynamicDO record);

    void createTable(DynamicDO record);

    void addColumn(DynamicDO record);

    void truncateTable(String tableName);

    void updateJsonValueByWhere(DictionaryJsonDO dictionaryJsonDO);

    void deleteOriginColumnNameByPk(DictionaryJsonDO dictionaryJsonDO);

    void updateOriginColumnNameByPk(DictionaryJsonDO dictionaryJsonDO);

    List<Map<Object, Object>> findOriginColumnNameWherePk(DictionaryJsonDO dictionaryJsonDO);

    IPage<List<HashMap>> selectListByPage(Page page, @Param("columns") List<String> columns, @Param("tableNames") List<String> tableNames, @Param("whereClause") String whereClause, @Param("orderStr") String orderStr);

    int count(@Param("tableName") String tableName);

    int countByWhere(DynamicDO record);

    void addColumnSingle(DynamicDO record);

    Integer deleteRecordByLimit(DynamicDO record);

    String getTablePrimaryKey(Map<String, String> map);

    Integer selectMaxId(Map<String, String> map);

    List<Long> getIdsList(DynamicDO record);

    String getString(DynamicDO record);

    List<TreeMap> selectListBySql(@Param("sql") String sql);

    Integer batchUpdate(@Param("sqlList") List<String> sqlList);

    int updateBatch(UpdateBatchDO batchDO);

    int saveOrUpdateRecordBatch(Map record);
}
