package org.etocrm.tagManager.model.VO.lifeCycleModel;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class LifeCycleModelRuleResponseVO {
    @ApiModelProperty(value = "阶段id，修改时需要")
    private Long id;


    @ApiModelProperty(value = "阶段名称")
    private String stepName;


    @ApiModelProperty(value = "阶段code,register/pureNew/preNew/growth/maturity/decline/lose")
    private String stepCode;

    @ApiModelProperty(value = "阶段规则值")
    private LifeCycleModelRuleValueVO ruleValue;

    @ApiModelProperty(value = "阶段规则描述")
    private String stepMemo;


    @ApiModelProperty(value = "覆盖人数")
    private Integer coveredCount;

}
