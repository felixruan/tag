package org.etocrm.tagManager.model.VO.mat;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.etocrm.dynamicDataSource.util.TagPageInfo;

import java.io.Serializable;

@ApiModel(value = "元数据事件条件分页VO ")
@Data
public class MetadataEventInPageVO extends TagPageInfo implements Serializable {

    private static final long serialVersionUID = -8794743025019477090L;

    @ApiModelProperty(value = "事件名")
    private String eventCode;

    @ApiModelProperty(value = "事件显示名")
    private String eventName;

    @ApiModelProperty(value = "应埋点平台")
    private String buryingPointPlatform;

    @ApiModelProperty(value = "显示状态")
    private Integer status;

}
