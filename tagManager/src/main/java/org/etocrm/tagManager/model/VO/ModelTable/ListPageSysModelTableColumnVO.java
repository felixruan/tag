package org.etocrm.tagManager.model.VO.ModelTable;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@ApiModel(value = "系统模型字段实体类")
@Data
public class ListPageSysModelTableColumnVO implements Serializable {


    private static final long serialVersionUID = -6136113285670496167L;

    @NotNull(message = "主键不可为空")
    @ApiModelProperty(value = "主键" )
    private Long id;


    /** 当前页 */
    @ApiModelProperty(value = "当前页")
    private Long current;

    /** 每页的数量 */
    @ApiModelProperty(value = "每页的数量")
    private Long size;
}
