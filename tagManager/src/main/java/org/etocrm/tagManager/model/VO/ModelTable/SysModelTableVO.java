package org.etocrm.tagManager.model.VO.ModelTable;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.etocrm.tagManager.model.VO.RelationsVO;

import java.io.Serializable;
import java.util.LinkedList;


@ApiModel(value = "系统模型实体类")
@Data
public class SysModelTableVO implements Serializable {
    private static final long serialVersionUID = -7736440648541369635L;
    @ApiModelProperty(value = "主键" )
    private Long id;

    @ApiModelProperty(value = "模型表名称" )
    private String modelTable;

    @ApiModelProperty(value = "显示名称" )
    private String modelTableName;

    @ApiModelProperty(value = "关联关系" )
    private LinkedList<RelationsVO> relationRule;

    @ApiModelProperty(value = "启用状态" )
    private Long modelStatus;

    @ApiModelProperty(value = "模型名称类型 0会员库1粉丝库" )
    private Integer dataFlag;


    private Integer createdBy;



    private String updatedTime;

    private String createdTime;
}
