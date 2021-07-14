package org.etocrm.tagManager.model.VO.tagProperty;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;


@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@JsonInclude(value = Include.NON_NULL)
public class SysTagPropertyRuleResponseVO implements Serializable {

    private static final long serialVersionUID = -5393738031546881657L;

    @ApiModelProperty(value = "属性规则ID")
    private Long id;

    @ApiModelProperty(value = "业务模型ID")
    private Long modelTableId;

    @ApiModelProperty(value = "业务模型名称")
    private String modelTableName;

    @ApiModelProperty(value = "业务模型字段ID")
    private Long columnId;

    @ApiModelProperty(value = "业务模型字段名称")
    private String columnDisplayName;

    @ApiModelProperty(value = "字段数据类型dictCode",required = true)
    private String columnDataTypeDictCode;

    @ApiModelProperty(value = "字段数据类型ID")
    private Long columnDataTypeId;

    @ApiModelProperty(value = "条件运算信息")
    private SysTagPropertyRuleLogicalOperationResponseVO logicalOperation;//


}