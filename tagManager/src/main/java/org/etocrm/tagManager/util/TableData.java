package org.etocrm.tagManager.util;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.etocrm.tagManager.model.VO.DBProcessorVO;

import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

/**
 * @Author chengrong.yang
 * @date 2020/9/3 11:12
 */
@ApiModel(value = "数据库同步规则表实体" )
@Data
public class TableData {

    @ApiModelProperty(value = "源数据源ID")
    private Long originDatabaseId;

    @ApiModelProperty(value = "目前数据源ID")
    private Long destinationDatabaseId;

    @ApiModelProperty("规则Id")
    private Long synchronizationConfigId;


    @ApiModelProperty(value = "源表名称" )
    private String originTableName;

    @ApiModelProperty(value = "源表数据库连接名称" )
    private String originDBName;

    @ApiModelProperty(value = "目标表数据库连接名称" )
    private String destinationDBName;

    @ApiModelProperty(value = "目前表名称")
    private String destinationTableName;

    @ApiModelProperty(value = "关联主键")
    private String primaryKey;

    @ApiModelProperty(value = "字段集")
    List<ColumnData> columnData;

    @ApiModelProperty(value = "源数据")
    List<TreeMap> originData;

    private DBProcessorVO dbProcessorVO;

}
