package org.etocrm.tagManager.util;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@ApiModel(value = "逻辑运算类型对应字段实体" )
@Data
public class LogicalOperations {
    @NotNull(message = "不能为空")
    @ApiModelProperty(value = "逻辑运算类型ID")
    private Long id;

    @NotBlank(message = "不能为空")
    @ApiModelProperty(value = "逻辑运算类型名称")
    private String name;


}
