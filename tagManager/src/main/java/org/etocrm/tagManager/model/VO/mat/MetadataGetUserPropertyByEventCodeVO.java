package org.etocrm.tagManager.model.VO.mat;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@ApiModel(value = "元数据根据事物code获取属性VO ")
@Data
public class MetadataGetUserPropertyByEventCodeVO implements Serializable {


    private static final long serialVersionUID = 6863207786608705720L;
    /**
     * 事物名
     */
    @ApiModelProperty(value = "事物名")
    private String eventCode;

    /*@ApiModelProperty(value = "是否为事物属性")
    private Integer isEventProperty;*/

    /*@ApiModelProperty(value = "是否为公共属性")
    private Integer isPublic;*/


}
