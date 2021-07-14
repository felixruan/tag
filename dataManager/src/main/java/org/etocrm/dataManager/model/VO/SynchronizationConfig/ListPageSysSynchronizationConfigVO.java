package org.etocrm.dataManager.model.VO.SynchronizationConfig;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;


/**
 * @author lingshuang.pang
 * @Date 2020-09-03
 */

@Data
public class ListPageSysSynchronizationConfigVO implements Serializable {


    private static final long serialVersionUID = -5443379270667575258L;
    @ApiModelProperty(value = "主键")
    private Long id;

    @ApiModelProperty(value = "品牌名称")
    private String brandName;

    @ApiModelProperty(value = "机构名称")
    private String orgName;

    @ApiModelProperty(value = "源数据源名称")
    private String originDatabaseName;

    @ApiModelProperty(value = "目标数据源名称")
    private String destinationDatabaseName;

    @ApiModelProperty(value = "源表名称")
    private String originTableName;

    @ApiModelProperty(value = "目标表名称")
    private String destinationTableName;

    @ApiModelProperty(value = "启用状态")
    private Integer syncStatus;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
    private Date updatedTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
    private Date createdTime;

}