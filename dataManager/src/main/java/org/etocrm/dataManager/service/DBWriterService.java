package org.etocrm.dataManager.service;

import org.etocrm.core.util.ResponseVO;
import org.etocrm.dataManager.util.TableData;

import java.util.Map;

public interface DBWriterService {

    ResponseVO dbWriter(TableData tableData);

    ResponseVO deleteTableData(TableData tableDatum);

    ResponseVO getCountByTableName(String tableName);

    Map<String, Object> getAllIdsByTableName(Long id,String tableName,String primaryKey);
}
