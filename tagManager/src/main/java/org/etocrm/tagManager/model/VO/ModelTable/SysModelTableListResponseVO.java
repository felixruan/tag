package org.etocrm.tagManager.model.VO.ModelTable;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;


@ApiModel(value = "模型列表出参")
@Data
public class SysModelTableListResponseVO implements Serializable {

    private static final long serialVersionUID = 2369813656588082544L;

    @ApiModelProperty(value = "主键")
    private Long id;


    @ApiModelProperty(value = "显示名称")
    private String modelTableName;

    @ApiModelProperty(value = "模型名称类型 0会员库1粉丝库" )
    private Integer dataFlag;


}
