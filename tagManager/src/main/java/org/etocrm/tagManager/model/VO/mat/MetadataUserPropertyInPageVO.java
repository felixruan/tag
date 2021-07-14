package org.etocrm.tagManager.model.VO.mat;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.etocrm.dynamicDataSource.util.TagPageInfo;

import java.io.Serializable;

@ApiModel(value = "元数据属性条件分页VO ")
@Data
public class MetadataUserPropertyInPageVO extends TagPageInfo implements Serializable {


    private static final long serialVersionUID = -7739811942121630408L;
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
     * 显示状态;可见或隐藏
     */
    @ApiModelProperty(value = "显示状态")
    private Integer status;

    /**
     * 是否为公共属性;公共属性是每个事件都有的固定属性
     */
    /*@ApiModelProperty(value = "是否为公共属性")
    private Integer isPublic;*/

    /**
     * 数据类型;必填单选 NUMBER、BOOL、STRING、DATETIME、LIST
     */
    @ApiModelProperty(value = "数据类型")
    private String dataType;

}
