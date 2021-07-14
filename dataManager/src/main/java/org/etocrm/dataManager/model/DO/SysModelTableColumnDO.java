package org.etocrm.dataManager.model.DO;


import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.etocrm.dynamicDataSource.util.BasePojo;

import java.io.Serializable;


@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@JsonInclude(value = JsonInclude.Include.NON_NULL)
@TableName("sys_model_table_column" )
public class SysModelTableColumnDO extends BasePojo implements Serializable {

    private static final long serialVersionUID = -1318900980047358309L;

    @ApiModelProperty(value = "主键" )
    @TableId
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

    @ApiModelProperty(value = "字段长度" )
    private Long length;

    @ApiModelProperty(value = "显示类型" )
    private Long valueType;

    @ApiModelProperty(value = "是否可多选" )
    private Long isMutiSelect;

//    @ApiModelProperty(value = "关联表名称" )
//    private String relationTableName;

    @ApiModelProperty(value = "关联表id" )
    private Long relationTableId;

    @ApiModelProperty(value = "关联表显示字段" )
    private Long displayColumnId;

    @ApiModelProperty(value = "关联主键" )
    private Long relationPk;

    @ApiModelProperty(value = "逻辑运算类型" )
    private String logicalOperations;

    @ApiModelProperty(value = "值域" )
    private String dataRange;

    @ApiModelProperty(value = "字段是否隐藏" )
    private Long delFlag;
}
