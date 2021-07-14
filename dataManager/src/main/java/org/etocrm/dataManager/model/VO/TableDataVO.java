package org.etocrm.dataManager.model.VO;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
public class TableDataVO implements Serializable {
    private static final long serialVersionUID = -2007785814440041169L;

    @ApiModelProperty(value = "数据源类型")
//    @NotNull(message = "数据源类型不可为空")
    private Long id;

    @ApiModelProperty(value = "数据源品牌id")
    @NotNull(message = "数据源品牌不可为空")
    private Long brandId;

    @ApiModelProperty(value = "数据源机构id")
    @NotNull(message = "数据源机构id不可为空")
    private Long orgId;

    @ApiModelProperty(value = "数据源名称")
    private String name;

    @ApiModelProperty(value = "数据源状态")
    private Long dataStatus;

    @ApiModelProperty(value = "数据源类型 0会员库1粉丝库")
    private Integer dataFlag;
}
