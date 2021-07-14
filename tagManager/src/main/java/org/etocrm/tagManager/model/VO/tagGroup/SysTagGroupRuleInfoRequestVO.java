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
public class SysTagGroupRuleInfoRequestVO implements Serializable {

    private static final long serialVersionUID = -2852509025645492240L;

    @NotNull(message = "标签ID不能为空")
    @ApiModelProperty(value = "标签id",required = true)
    private Long tagId;

    @NotNull(message = "标签间关系ID不能为空")
    @ApiModelProperty(value = "运算关系id",required = true)
    private Long logicalOperationId;

    @Valid
    @Size(min = 1,message = "标签运算关系不能为空")
    @NotNull(message = "标签运算关系不能为空")
    @ApiModelProperty(value = "运算关系数据",required = true)
    private List<LogicalOperationValueRequestVO> logicalOperationValue;

}
