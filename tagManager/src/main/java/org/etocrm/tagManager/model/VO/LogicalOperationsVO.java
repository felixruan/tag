package org.etocrm.tagManager.model.VO;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class LogicalOperationsVO {

    @ApiModelProperty(value = "逻辑运算类型ID")
    private Long id;


    @ApiModelProperty(value = "逻辑运算类型名称")
    private String name;

    @ApiModelProperty(value = "逻辑运算类型名称code")
    private String dictCode;

    @ApiModelProperty(value = "子类集合")
    private List<LogicalOperationsChildVO> child;
}
