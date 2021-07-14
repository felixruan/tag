package org.etocrm.tagManager.model.VO.tagGroup;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;


@ApiModel(value = "新增/修改标签群组规则入参 ")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class SysTagGroupRuleInfoResponseVO implements Serializable {

    private static final long serialVersionUID = -2852509025645492240L;

    @ApiModelProperty(value = "标签id")
    private Long tagId;

    @ApiModelProperty(value = "标签名称")
    private String tagName;

    @ApiModelProperty(value = "运算关系id")
    private Long logicalOperationId;
    
    @ApiModelProperty(value = "运算关系数据")
    private List<LogicalOperationValueResponseVO> logicalOperationValue;

}