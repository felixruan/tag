package org.etocrm.dataManager.model.VO;

import lombok.Data;

/**
 * @author lingshuang.pang
 * @Date 2020/9/4 10:01
 */
@Data
public class DBProcessorColumnVO {

    private String tableName;//表名称

    private String tableSchema;//库名称

    private String columnComment;//字段说明

    private String columnType;//字段类型 varchar(32)

    private String dataType;

    private String columnKey;

    private String columnName;

}
