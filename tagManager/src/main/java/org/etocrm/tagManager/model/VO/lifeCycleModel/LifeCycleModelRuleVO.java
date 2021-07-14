package org.etocrm.tagManager.model.VO.lifeCycleModel;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class LifeCycleModelRuleVO {
    @ApiModelProperty(value = "阶段id，修改时需要")
    private Long id;

    @NotBlank(message = "阶段名称不能为空")
    @ApiModelProperty(value = "阶段名称")
    private String stepName;

    @NotBlank(message = "阶段code不能为空")
    @ApiModelProperty(value = "阶段code,register/pureNew/preNew/growth/maturity/decline/lose")
    private String stepCode;

    @Valid
    @NotNull(message = "阶段规则值不能为空")
    @ApiModelProperty(value = "阶段规则值")
    private LifeCycleModelRuleValueVO ruleValue;
}
