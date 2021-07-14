package org.etocrm.dynamicDataSource.model.DO;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class UpdateBatchDO {
    /**
     * 表名
     */
    private String tableName;
    /**
     * 机构id
     */
    private Long orgId;
    /**
     * 品牌id
     */
    private Long brandsId;
    /**
     * 更新的列名称list
     */
    private List<String> updateColNameList;
    /**
     * 更新的数据list
     */
    private List<Map> dataList;

    /**
     * 根据那一行更新
     */
    private String whereCol;

}
