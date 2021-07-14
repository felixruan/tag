package org.etocrm.tagManager.model.DO;


import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.etocrm.dynamicDataSource.util.BasePojo;

import java.io.Serializable;


@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@JsonInclude(value = JsonInclude.Include.NON_NULL)
@TableName("sys_life_cycle_model_rule")
public class SysLifeCycleModelRuleDO extends BasePojo implements Serializable {
    private static final long serialVersionUID = -5204546974087527500L;

    @ApiModelProperty(value = "主键")
    private Long id;

    @ApiModelProperty(value = "生命周期模型id")
    private Long modelId;

    @ApiModelProperty(value = "阶段名称")
    private String stepName;

    @ApiModelProperty(value = "阶段code")
    private String stepCode;

    @ApiModelProperty(value = "阶段顺序,越小越靠前")
    private Integer stepOrder;

    @ApiModelProperty(value = "阶段规则描述")
    private String stepMemo;

    @ApiModelProperty(value = "结算规则值")
    private String stepRuleValue;

    @ApiModelProperty(value = "覆盖人数")
    private Integer coveredCount;

}
