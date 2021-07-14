package org.etocrm.tagManager.model.VO.mat;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@ApiModel(value = "元数据根据事物code获取属性VO ")
@Data
public class MetadataGetPropertyOutVO implements Serializable {

    private static final long serialVersionUID = -2684902431503677113L;
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
     * 显示状态;可见或隐藏
     */
    @ApiModelProperty(value = "显示状态")
    private Integer status;

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
     * 上报数据;有无上报数据
     */
    @ApiModelProperty(value = "上报数据")
    private Integer isReport;


}
