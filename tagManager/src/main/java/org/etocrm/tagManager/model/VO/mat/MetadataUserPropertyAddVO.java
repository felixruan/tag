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
@ApiModel(value = "元数据属性新增VO")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class MetadataUserPropertyAddVO implements Serializable {


    private static final long serialVersionUID = 6515749435198545489L;
    /**
     * 属性名
     */
    @ApiModelProperty(value = "属性名")
    private String propertyCode;
    /**
     * 属性显示名
     */
    @ApiModelProperty(value = "属性显示名")
    private String propertyName;

    /**
     * 数据类型;必填单选 NUMBER、BOOL、STRING、DATETIME、LIST
     */
    @ApiModelProperty(value = "数据类型,必填单选 NUMBER、BOOL、STRING、DATETIME、LIST")
    private String dataType;

    /**
     * 单位/格式;统计值的单位，设置后会在分析详情和概览中显示。
     */
    @ApiModelProperty(value = "单位/格式")
    private String unit;

    /**
     * 属性值说明或示例
     */
    @ApiModelProperty(value = "属性值说明或示例")
    private String valueExplain;


    /**
     * 显示状态;可见或隐藏
     */
    @ApiModelProperty(value = "显示状态: 0 可见、1 隐藏")
    private Integer status;

    /**
     * 是否预置
     */
   /* @ApiModelProperty(value = "是否预置: 0 否、1 是")
    private Integer isPreset;*/


}
