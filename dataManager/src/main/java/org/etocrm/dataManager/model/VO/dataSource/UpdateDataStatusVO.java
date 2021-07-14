package org.etocrm.dataManager.model.VO.dataSource;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @Date 2020/9/10 14:41
 */
@Data
@Api(value = "修改数据源状态实体类")
public class UpdateDataStatusVO {

    @ApiModelProperty(value = "数据源id" )
    @NotNull(message = "数据源id不能为空")
    private Long dataSourceId;

    @ApiModelProperty(value = "数据源状态值" )
    @NotNull(message = "数据源状态值不能为空")
    private Long dataStatus;
}
