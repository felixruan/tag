package org.etocrm.dataManager.model.VO.dict;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @Date 2020/9/17 17:14
 */
@Data
@Api(value ="修改状态VO")
public class UpdateStatusVO {
    
    @ApiModelProperty(value = "主键" )
    @NotNull(message = "id不能为空")
    private Long id;
    
    @ApiModelProperty(value = "启用状态" )
    @NotNull(message = "启用状态不能为空")
    private Integer dictStatus;

}
