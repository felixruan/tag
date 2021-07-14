package org.etocrm.tagManager.model.VO.tagProperty;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@JsonInclude(value = Include.NON_NULL)
public class SysTagPropertyQueryResponseVO  implements Serializable {

    private static final long serialVersionUID = 9013020084007328134L;

    @ApiModelProperty(value = "主键")
    @TableId(value = "id" , type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "属性名称")
    private String propertyName;

    @ApiModelProperty(value = "规则运算id")
    private Integer ruleRelationshipId;

    @ApiModelProperty(value = "规则运算名称")
    private String ruleRelationshipName;

    @ApiModelProperty(value = "规则信息")
    private List<SysTagPropertyRuleResponseVO> rule;
}