package org.etocrm.tagManager.model.VO.tagGroup;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.List;

@ApiModel(value = "新增/修改标签群组规则入参 ")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class SysTagGroupRuleRequestVO implements Serializable {

    private static final long serialVersionUID = -2852509025645492240L;


    @ApiModelProperty(value = "群组规则运算关系id",required = true)
    @NotNull(message = "群组内的运算关系不能为空")
    private Long tagGroupRuleRelationshipId;

    @ApiModelProperty(value = "标签群组规则",required = true)
    @Valid
    @NotNull(message = "群组运算规则不能为空")
    @Size(min = 1,message = "群组运算规则不能为空")
    private List<SysTagGroupRuleInfoRequestVO> tagGroupRule;

}
