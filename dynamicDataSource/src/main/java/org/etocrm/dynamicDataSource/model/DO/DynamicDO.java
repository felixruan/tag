package org.etocrm.dynamicDataSource.model.DO;

import lombok.Data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author chengrong.yang
 * @date 2020/9/3 13:14
 */
@Data
public class DynamicDO {

    //select的字段列表
    private List<String> columns;

    //insert、update的键值对Map
    private Map operrateColumnsAndValue;

    //select的表名列表
    private List<String> tableNames;

    //insert、update、delete的表名
    private String operateTableName;

    //where条件
    private String whereClause;

    private Map whereClauseMap;//用于前台查询

    private String id;

    private String orderStr;//排序字段，只允许一个排序，格式如 seqno desc

    private HashMap whereClauseOpertypeMap;

    private String column;

    private Integer limitStart;
    private Integer limitEnd;

    private String primaryKey;
}
