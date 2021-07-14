package org.etocrm.authentication.entity.VO.tagIndustry;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.models.auth.In;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 查询标签行业入参
 */
@ApiModel(value = "查询标签行业入参 " )
@Data
public class SysTagIndustryRequestVO {

    private static final Integer TYPE_SELECTED = 0;
    private static final Integer TYPE_ALL = 1;


    @NotNull(message = "行业ID不能为空")
    @ApiModelProperty(value = "行业ID",required = true)
    private Long industryId;
}


