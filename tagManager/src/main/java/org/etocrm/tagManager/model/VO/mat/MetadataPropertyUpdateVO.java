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
@ApiModel(value = "元数据属性更新VO")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class MetadataPropertyUpdateVO implements Serializable {

    private static final long serialVersionUID = 8802978926940798436L;

    /**
     * 主键id
     */
    @ApiModelProperty(value = "主键id")
    private Long id;

    /**
     * 属性名
     */
    private String propertyCode;
    /**
     * 属性显示名
     */
    @ApiModelProperty(value = "属性名")
    private String propertyName;

    /**
     * 数据类型;必填单选 NUMBER、BOOL、STRING、DATETIME、LIST
     */
    @ApiModelProperty(value = "数据类型")
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
     * 是否为公共属性;公共属性是每个事件都有的固定属性
     */
    @ApiModelProperty(value = "是否为公共属性")
    private Integer isPublic;

    /**
     * 显示状态;可见或隐藏
     */
    @ApiModelProperty(value = "显示状态")
    private Integer status;

}
