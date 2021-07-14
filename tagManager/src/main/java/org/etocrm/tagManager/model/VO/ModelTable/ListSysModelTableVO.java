package org.etocrm.tagManager.model.VO.ModelTable;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;


@ApiModel(value = "系统模型实体类")
@Data
public class ListSysModelTableVO implements Serializable {
    private static final long serialVersionUID = -7736440648541369635L;


    @ApiModelProperty(value = "主键id" )
    private Long id;


    @ApiModelProperty(value = "模型表名称" )
    private String modelTable;

    @ApiModelProperty(value = "显示名称" )
    private String modelTableName;

    @ApiModelProperty(value = "字段数量" )
    private Long modelCount;

    @ApiModelProperty(value = "关联模型数量" )
    private Long modelRuleCount;

    @ApiModelProperty(value = "启用状态" )
    private Long modelStatus;

    @ApiModelProperty(value = "模型名称类型 0会员库1粉丝库" )
    private Integer dataFlag;

    @ApiModelProperty(value = "创建人" )
    private String createdBy;


    private String updatedTime;


    private String createdTime;
}
