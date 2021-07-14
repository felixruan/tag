package org.etocrm.dataManager.listener;

import lombok.Data;

import java.util.List;

/**
 * @Author: dkx
 * @Date: 10:50 2021/2/4
 * @Desc:
 */
@Data
public class BatchVO {
    Integer limitStart;
    Integer end;
    Long originDatabaseId;
    String tableName;
    List<String> column;
    String columns;
    String destinationTableName;
}
