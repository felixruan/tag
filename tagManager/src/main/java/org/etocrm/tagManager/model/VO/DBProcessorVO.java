package org.etocrm.tagManager.model.VO;

import lombok.Data;

import java.util.List;

/**
 * @author lingshuang.pang
 * @Date 2020/9/4 10:01
 */
@Data
public class DBProcessorVO {

    private String tableName;//表名称

    private List<String> column;//列数据

    private Long databaseId;  //数据源ID

    private String tableSchema;//库名称

    private String whereCase;
}

