package org.etocrm.dataManager.service;

import org.etocrm.core.util.ResponseVO;
import org.etocrm.dataManager.model.VO.DBProcessorColumnVO;
import org.etocrm.dataManager.model.VO.DBProcessorVO;

import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

/**
 * @author lingshuang.pang
 * @Date 2020/9/4 9:41
 */
public interface IDBProcessorService {
    /**
     * @param dbProcessorVO
     * @Description: 判断表是否存在
     **/
    Boolean verifyTableExists(DBProcessorVO dbProcessorVO);

    /**
     * @param dbProcessorVO
     * @Description: 判断字段是否存在
     **/
    List<HashMap<String, DBProcessorColumnVO>> verifyColumnsExists(DBProcessorVO dbProcessorVO);

    /**
     * @param dbProcessorVO
     * @Description: 创建表
     * @return: int
     **/
    int createTable(DBProcessorVO dbProcessorVO);

    /**
     * @param dbProcessorVO
     * @Description: 新增字段
     * @return: Boolean
     **/
    Boolean addColumns(DBProcessorVO dbProcessorVO);


    /**
     * @param dbProcessorVO
     * @Description: 查询数据
     **/
    List<TreeMap> selectList(DBProcessorVO dbProcessorVO);



    /**================== return ResponseVO ==================*/

    /**
     * @param dbProcessorVO
     * @Description: 判断表是否存在
     **/
    ResponseVO verifyTableExistsService(DBProcessorVO dbProcessorVO);

    /**
     * @param dbProcessorVO
     * @Description: 判断字段是否存在
     **/
    ResponseVO verifyColumnsExistsService(DBProcessorVO dbProcessorVO);

    /**
     * @param dbProcessorVO
     * @Description: 创建表
     * @return: int
     **/
    ResponseVO createTableService(DBProcessorVO dbProcessorVO);

    /**
     * @param dbProcessorVO
     * @Description: 新增字段
     * @return: Boolean
     **/
    ResponseVO addColumnsService(DBProcessorVO dbProcessorVO);


    /**
     * @param dbProcessorVO
     * @Description: 查询数据
     **/
    ResponseVO selectListService(DBProcessorVO dbProcessorVO);

    ResponseVO truncateTable(DBProcessorVO dbProcessorVO);

    ResponseVO selectListByLimit(DBProcessorVO dbProcessorVO, int start, int end);

    ResponseVO selectListById(DBProcessorVO dbProcessorVO, int start, int end, String primaryKey);
}
