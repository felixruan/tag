package org.etocrm.tagManager.model.VO.ModelTable;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.etocrm.core.util.DateUtil;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;


@ApiModel(value = "系统模型实体类")
@Data
public class ListPageSysModelTableVO implements Serializable {
    private static final long serialVersionUID = -7736440648541369635L;

    @ApiModelProperty(value = "模型表名称" )
    private String modelTable;

    @DateTimeFormat(pattern=DateUtil.default_datetimeformat)
    @ApiModelProperty(value = "开始时间" )
    private Date startTime;

    @DateTimeFormat(pattern=DateUtil.default_datetimeformat)
    @ApiModelProperty(value = "结束时间" )
    private Date endTime;


    @ApiModelProperty(value = "启用状态" )
    private Long modelStatus;

    /** 当前页 */
    @ApiModelProperty(value = "当前页")
    private Long current;

    /** 每页的数量 */
    @ApiModelProperty(value = "每页的数量")
    private Long size;
}
