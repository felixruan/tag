package org.etocrm.tagManager.model.VO.ModelTable;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.etocrm.tagManager.util.DataRange;
import org.etocrm.tagManager.util.LogicalOperations;

import java.io.Serializable;
import java.util.List;

@ApiModel(value = "系统模型字段实体类")
@Data
public class SysModelTableColumnVO implements Serializable {

    private static final long serialVersionUID = -2410903057343167590L;

    @ApiModelProperty(value = "id" )
    private Long id;

    @ApiModelProperty(value = "模板表ID" )
    private Long modelTableId;

    @ApiModelProperty(value = "字段名称" )
    private String columnName;

    @ApiModelProperty(value = "显示名称" )
    private String displayName;

    @ApiModelProperty(value = "是否可为空" )
    private Long notNull;

    @ApiModelProperty(value = "数据类型" )
    private Long dataType;

    @ApiModelProperty(value = "数据类型名称" )
    private String dataTypeName;

    @ApiModelProperty(value = "字段长度" )
    private Long length;

    @ApiModelProperty(value = "标签类型" )
    private Long valueType;

    @ApiModelProperty(value = "标签类型名称" )
    private String valueTypeName;

    @ApiModelProperty(value = "标签类型名称" )
    private String valueTypeDictCode;

    @ApiModelProperty(value = "是否可多选" )
    private Long isMutiSelect;

//    @ApiModelProperty(value = "关联表名称" )
//    private String relationTableName;

    @ApiModelProperty(value = "关联表id" )
    private Long relationTableId;

    @ApiModelProperty(value = "关联表名称" )
    private String relationTableName;

    @ApiModelProperty(value = "关联表显示字段" )
    private Long displayColumnId;

    @ApiModelProperty(value = "关联表显示字段名称" )
    private String displayColumnName;

    @ApiModelProperty(value = "关联主键" )
    private Long relationPk;

    @ApiModelProperty(value = "关联主键名称" )
    private String relationPkName;

    @ApiModelProperty(value = "逻辑运算类型" )
    private List<LogicalOperations> logicalOperations;

    @ApiModelProperty(value = "值域" )
    private List<DataRange> dataRange;

    @ApiModelProperty(value = "字段是否隐藏" )
    private Long delFlag;

}
