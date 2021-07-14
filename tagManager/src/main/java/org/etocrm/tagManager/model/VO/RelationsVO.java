package org.etocrm.tagManager.model.VO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel(value = "模型关联关系规则表对应字段实体" )
@Data
public class RelationsVO {

    @ApiModelProperty(value = "当前模型字段id")
    private Long currentId;

    @ApiModelProperty(value = "当前模型字段名称")
    private String currentName;

    @ApiModelProperty(value = "表名称id")
    private Long tableId;

    @ApiModelProperty(value = "关联表关联字段名称id")
    private Long relationId;

    @ApiModelProperty(value = "关联表字段名称id")
    private Long fk;

    @ApiModelProperty(value = "表名称")
    private String tableName;

    @ApiModelProperty(value = "关联表关联字段名称")
    private String relationName;

    @ApiModelProperty(value = "关联表字段名称")
    private String fkName;
}
