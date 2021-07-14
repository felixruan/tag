package org.etocrm.dynamicDataSource.model.DO;

import lombok.Data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: dkx
 * @Date: 11:12 2020/9/5
 * @Desc:
 */
@Data
public class DictionaryJsonDO {
    //select的字段列表
    private List<String> columns;

    //select的表名列表
    private String tableName;

    //where字段
    private String whereClause;

    //wehere  map  例如：id= 100;
    private HashMap whereClauseMap;

    //别名
    private String alias;

    //列名
    private String columnName;

    //列名值
    private String columnNameValue;

    //新的列名值
    private String newColumnNameValue;

}
