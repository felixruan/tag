package org.etocrm.tagManager.model.VO.mat;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


@Data
public class MetadataPropertyParameter {

    /** 属性code */
    @ApiModelProperty(value = "属性code")
    private String propertyCode;

    /** 属性值 */
    @ApiModelProperty(value = "属性值")
    private Object propertyValue;



}
