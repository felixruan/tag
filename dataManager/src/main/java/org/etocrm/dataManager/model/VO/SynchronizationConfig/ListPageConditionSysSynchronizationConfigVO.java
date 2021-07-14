package org.etocrm.dataManager.model.VO.SynchronizationConfig;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.etocrm.core.util.DateUtil;

import java.io.Serializable;
import java.sql.Timestamp;



@Data
public class ListPageConditionSysSynchronizationConfigVO implements Serializable {


    private static final long serialVersionUID = 6717710030821662171L;
    @ApiModelProperty(value = "品牌id")
    private Long brandId;

    @ApiModelProperty(value = "机构id")
    private Long orgId;

    @ApiModelProperty(value = "源数据源名称")
    private Long originDatabaseName;


    @ApiModelProperty(value = "源表名称")
    private String originTableName;

    @ApiModelProperty(value = "目标表名称")
    private String destinationTableName;


    @ApiModelProperty(value = "当前页")
    private Long current;

    @ApiModelProperty(value = "每页的数量")
    private Long size;

}