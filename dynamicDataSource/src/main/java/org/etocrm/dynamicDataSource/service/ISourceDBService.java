package org.etocrm.dynamicDataSource.service;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.TreeMap;

/**
 * @Author chengrong.yang
 * @Date 2020/11/13 16:18
 */
public interface ISourceDBService {

    List<HashMap> getAllTable(Long id);

    List<HashMap> getAllColumnByTable(Long id, String tableName);

    Integer getMaxId(Long id, String tableName, String primaryKey);

    String getTablePrimaryKey(Long id,String tableName);

    List<HashMap> verifyTableExists(Long id,String tableName);

    Integer count(Long id, String tableName);

    List<LinkedHashMap<String, Object>> selectList(Long id, List<String> tableNames, List<String> columns, String whereClause, String orderByStr);

    List<LinkedHashMap<String, Object>> selectEtlListId(Long id, String tableNames, List<String> columns, int start, int end, String primaryKey);

    List<HashMap> verifyColumnsExists(Long databaseId, String tableName);

    List<LinkedHashMap<String, Object>> selectYoungorListId(String tableNames, List<String> columns, int start, int end);

    void closeSession();

}
