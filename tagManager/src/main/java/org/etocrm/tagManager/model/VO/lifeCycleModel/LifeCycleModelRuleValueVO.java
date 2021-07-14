package org.etocrm.tagManager.model.VO.lifeCycleModel;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class LifeCycleModelRuleValueVO {
    @ApiModelProperty(value = "单个天数")
    private Integer day;

    @ApiModelProperty(value = "多个天数")
    private List<Integer> dayList;

    @ApiModelProperty(value = "运算关系code ,大于：life_cycle_greater_than/介于：life_cycle_situated_between ")
    private String logicDictCode;

    @ApiModelProperty(value = "运算值")
    private Integer startValue;

    @ApiModelProperty(value = "运算值，介于有该值")
    private Integer endValue;

}
