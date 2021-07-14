package org.etocrm.tagManager.model.VO.lifeCycleModel;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class LifeCycelModelGetRuleResponseVO implements Serializable {
    private static final long serialVersionUID = 3983310779190866401L;

    @ApiModelProperty(value = "主键")
    private Long id;

    @ApiModelProperty(value = "品牌名称")
    private String brandsName;

    @ApiModelProperty(value = "品牌id")
    private Long brandsId;

    @ApiModelProperty(value = "机构id")
    private Long orgId;
//
//    @ApiModelProperty(value = "机构名称")
//    private String orgName;

    @ApiModelProperty(value = "模型名称")
    private String name;

    @ApiModelProperty(value = "更新频率,0-不更新，1-每天一次，2-每周一次，3-每月一次")
    private Integer updateType;

    @ApiModelProperty(value = "每周几/每月哪天")
    private Integer updateValue;

    @ApiModelProperty(value = "更新状态 1 异常  其他值 正常")
    private Integer dataUpdateStatus;

    @ApiModelProperty(value = "规则修改时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date ruleUpdateTime;


//    @ApiModelProperty(value = "更新频率,0-不更新，1-每天一次，2-每周一次，3-每月一次")
//    private String updateTypeName;

    @ApiModelProperty(value = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date createdTime;

    @ApiModelProperty(value = "数据更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date dataUpdateTime;

    @ApiModelProperty(value = "总人数")
    private Long sumRuleCount;

    private List<LifeCycleModelRuleResponseVO> lifeCycleModelRuleResponseVO;
}
