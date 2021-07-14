package org.etocrm.authentication.entity.VO.tagIndustry;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 修改标签行业入参
 */
@ApiModel(value = "修改标签行业入参 " )
@Data
public class SysTagIndustryModifyVO {

    @NotNull(message = "行业ID不能为空")
    @ApiModelProperty(value = "行业ID" ,required = true)
    private Long industryId;

    @NotNull(message = "标签ID不能为空")
    @ApiModelProperty(value = "标签ID,多个之间用英文逗号分隔" ,required = true)
    private String tagIds;
}
