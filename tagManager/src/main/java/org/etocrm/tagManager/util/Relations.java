package org.etocrm.tagManager.util;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel(value = "模型关联关系规则表对应字段实体" )
@Data
public class Relations {


    @ApiModelProperty(value = "当前模型字段id")
    private Long currentId;

    @ApiModelProperty(value = "关联模型id")
    private Long tableId;

    @ApiModelProperty(value = "关联表关联字段名称id")
    private Long relationId;

    @ApiModelProperty(value = "下层关联字段id")
    private Long fk;
}
