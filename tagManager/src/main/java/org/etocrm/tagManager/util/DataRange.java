package org.etocrm.tagManager.util;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel(value = "值域对应字段实体" )
@Data
public class DataRange {
    @ApiModelProperty(value = "值域ID")
    private String value;

    @ApiModelProperty(value = "值域名称")
    private String displayName;
}
