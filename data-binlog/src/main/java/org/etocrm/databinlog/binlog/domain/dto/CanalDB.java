package org.etocrm.databinlog.binlog.domain.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.TreeMap;

@Data
public class CanalDB implements Serializable {

    private static final long serialVersionUID = -1318902980047358309L;

    private String tableName;//表名称

    private TreeMap<String, Object> columnAndData;//列数据

    private String tableSchema;//库名称

    private String whereCase;

    private String type;//crud
}

