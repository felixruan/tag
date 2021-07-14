package org.etocrm.tagManager.model.VO.lifeCycleModel;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class QueryListResponseVO implements Serializable {
    private static final long serialVersionUID = -3962777323746911057L;

    @ApiModelProperty(value = "主键")
    private Long id;

    @ApiModelProperty(value = "品牌名称")
    private String brandsName;

    @ApiModelProperty(value = "品牌id")
    private Long brandsId;

    @ApiModelProperty(value = "机构id")
    private Long orgId;

    @ApiModelProperty(value = "机构名称")
    private String orgName;

    @ApiModelProperty(value = "模型名称")
    private String name;

    @ApiModelProperty(value = "更新频率,0-不更新，1-每天一次，2-每周一次，3-每月一次")
    private Integer updateType;

    @ApiModelProperty(value = "更新频率,0-不更新，1-每天一次，2-每周一次，3-每月一次")
    private String updateTypeName;

    @ApiModelProperty(value = "规则修改时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date ruleUpdateTime;

    @ApiModelProperty(value = "数据更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date dataUpdateTime;
}
