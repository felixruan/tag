package org.etocrm.dataManager.model.VO.dataSource;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @Date 2020/9/14 17:05
 */
@Api(value = "系统数据源 ")
@Data
public class DataSourceReturnVO implements Serializable {
    private static final long serialVersionUID = -4616744204466390081L;
    @ApiModelProperty(value = "主键")
    private Long id;

//    @ApiModelProperty(value = "数据源类型 数据字典")
//    private Long dataType;

    @ApiModelProperty(value = "所属品牌")
    private Long brandsId;

    @ApiModelProperty(value = "机构id")
    private Long orgId;

    @ApiModelProperty(value = "机构名称")
    private String orgName;

    @ApiModelProperty(value = "数据源状态")
    private Long dataStatus;

    @ApiModelProperty(value = "数据源名称")
    private String dataName;

    @ApiModelProperty(value = "数据源描述")
    private String dataMemo;

    @ApiModelProperty(value = "同步频次 corn表达式")
    private String dataCorn;

    @ApiModelProperty(value = "排序字段")
    private Integer orderNumber;

    @ApiModelProperty(value = "品牌名称")
    private String brandName;

    @ApiModelProperty(value = "创建人")
    private String createBy;

    @ApiModelProperty(value = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
    private Date createdTime;

    @ApiModelProperty(value = "修改时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
    private Date updatedTime;

    @ApiModelProperty(value = "数据源类型字典dictValue")
    private Integer dataFlag;

    @ApiModelProperty(value = "数据源类型名称")
    private String dataFlagName;
}
