package org.etocrm.tagManager.model.VO.mat;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * Create By peter.li
 */
@ApiModel(value = "MAT流程群组拆分条件封装VO")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class MatProcessConditionVO implements Serializable {

    private static final long serialVersionUID = -4561373947806292417L;
    /**
     * 模型id
     */
    @ApiModelProperty(value = "模型id")
    private Long modelId;

    /**
     * 模型字段id
     */
    @ApiModelProperty(value = "模型字段id")
    private Long modelPropertyId;

    /**
     * 逻辑运算id，字典表id
     */
    @ApiModelProperty(value = "逻辑运算id，字典表id")
    private Integer logicId;

    /**
     * 单输入框值
     */
    @ApiModelProperty(value = "单输入框值")
    private String value;

    /**
     * 填值框值
     * 1输入框，2数值输入框，3数值输入框组，4日期选择框，5、日期选择框组，6、下拉选择框
     */
    @ApiModelProperty(value = "填值框值")
    private Integer valueType;

    /**
     * 双输入框前值
     */
    @ApiModelProperty(value = "逻辑运算id，字典表id")
    private String betweenBegin;

    /**
     * 双输入框后值
     */
    @ApiModelProperty(value = "逻辑运算id，字典表id")
    private String betweenEnd;

    private String columns;



}
