package org.etocrm.dataManager.model.VO.SynchronizationConfig;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.etocrm.core.util.DateUtil;

import java.io.Serializable;
import java.sql.Timestamp;


@Data
public class ConditionSysSynchronizationConfigVO implements Serializable {


    private static final long serialVersionUID = 1659834960064594431L;
    @ApiModelProperty(value = "是否查询源数据库")
    private Integer isOriginDatabase;

    @ApiModelProperty(value = "是否查询目标数据源")
    private Integer isDestinationDatabase;

    @ApiModelProperty(value = "是否查询品牌")
    private Integer isBrandName;

    @ApiModelProperty(value = "是否查询源表")
    private Integer isOriginTable;

    @ApiModelProperty(value = "是否查询目标表")
    private Integer isDestinationTable;



}