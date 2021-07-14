package org.etocrm.dataManager.model.VO.dataSource;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.etocrm.core.util.DateUtil;
import org.etocrm.dynamicDataSource.util.TagPageInfo;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * @Date 2020/9/9 21:11
 */
@Api(value = "系统数据源 ")
@Data
public class GetDataSourceForPageVO extends TagPageInfo implements Serializable {
    private static final long serialVersionUID = -745332207381731273L;

    @ApiModelProperty(value = "主键")
    private Long id;

    @ApiModelProperty(value = "所属品牌")
    private Long brandsId;

    @ApiModelProperty(value = "所属机构")
    private Long orgId;

    @ApiModelProperty(value = "数据源名称")
    private String dataName;

    @DateTimeFormat(pattern= DateUtil.default_dateformat)
    @ApiModelProperty(value = "开始时间")
    private Date begTime;

    @DateTimeFormat(pattern=DateUtil.default_dateformat)
    @ApiModelProperty(value = "结束时间")
    private Date  endTime;

    @ApiModelProperty(value = "数据源状态")
    private Integer dataStatus;


    }
