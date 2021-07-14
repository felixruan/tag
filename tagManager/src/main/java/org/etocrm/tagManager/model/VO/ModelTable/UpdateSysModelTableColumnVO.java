package org.etocrm.tagManager.model.VO.ModelTable;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.etocrm.tagManager.util.DataRange;
import org.etocrm.tagManager.util.LogicalOperations;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.io.Serializable;
import java.util.List;

@ApiModel(value = "业务模型字段实体类")
@Data
public class UpdateSysModelTableColumnVO implements Serializable {

    private static final long serialVersionUID = -2410903057343167590L;

    @NotNull(message = "主键id不可为空")
    @ApiModelProperty(value = "主键" )
    private Long id;

    @NotNull(message = "模板表ID不可为空")
    @ApiModelProperty(value = "模板表ID" )
    private Long modelTableId;

    @NotBlank(message = "字段名称不能为空")
    @ApiModelProperty(value = "字段名称" )
    @Pattern(regexp = "^[a-zA-Z][a-zA-Z0-9_]{0,62}[a-zA-Z0-9]$",message = "名称不合法(第一位是大小写字母，中间为大小写字母，数字和下划线，结尾不可为下划线，长度为2~64)")
    private String columnName;

    @NotBlank(message = "中文显示名称不能为空")
    @ApiModelProperty(value = "中文显示名称" )
    private String displayName;

    @NotNull(message = "是否可为空不能为空")
    @ApiModelProperty(value = "是否可为空" )
    private Long notNull;

    @NotNull(message = "数据类型不能为空")
    @ApiModelProperty(value = "数据类型" )
    private Long dataType;

    @NotNull(message = "字段长度不能为空")
    @ApiModelProperty(value = "字段长度" )
    private Long length;

    @NotNull(message = "标签数据类型为空")
    @ApiModelProperty(value = "标签数据类型" )
    private Long valueType;

//    @NotNull(message = "是否可多选不能为空")
//    @ApiModelProperty(value = "是否可多选" )
//    private Long isMutiSelect;


    @ApiModelProperty(value = "关联表名称" )
    private String relationTableName;

    @ApiModelProperty(value = "关联表id" )
    private Long relationTableId;

    @ApiModelProperty(value = "关联表显示字段" )
    private Long displayColumnId;

    @ApiModelProperty(value = "关联主键" )
    private Long relationPk;

    @ApiModelProperty(value = "逻辑运算类型" )
    private List<LogicalOperations> logicalOperations;

    @ApiModelProperty(value = "值域" )
    private List<DataRange> dataRange;

    @NotNull(message = "逻辑运算类型不可为空")
    @ApiModelProperty(value = "字段是否隐藏" )
    private Long delFlag;

}
