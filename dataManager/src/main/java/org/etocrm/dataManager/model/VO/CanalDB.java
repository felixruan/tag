package org.etocrm.dataManager.model.VO;

import lombok.Data;
import org.etocrm.dataManager.model.DO.SysSynchronizationConfigDO;

import java.io.Serializable;
import java.util.TreeMap;

@Data
public class CanalDB implements Serializable {

    private static final long serialVersionUID = -1318902980047358308L;

    private String tableName;//表名称

    private TreeMap<String, Object> columnAndData;//列数据

    private String tableSchema;//库名称

    private String whereCase;

    private SysSynchronizationConfigDO sysSynchronizationConfig;

    private String type;//crud
}

