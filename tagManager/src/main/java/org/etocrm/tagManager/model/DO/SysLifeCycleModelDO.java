package org.etocrm.tagManager.model.DO;


import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.etocrm.dynamicDataSource.util.BasePojo;

import java.io.Serializable;
import java.util.Date;


@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@JsonInclude(value = JsonInclude.Include.NON_NULL)
@TableName("sys_life_cycle_model")
public class SysLifeCycleModelDO extends BasePojo implements Serializable {
    private static final long serialVersionUID = -4317241983195452434L;

    @ApiModelProperty(value = "主键")
    private Long id;

    @ApiModelProperty(value = "模型名称")
    private String name;

    @ApiModelProperty(value = "机构id")
    private Long orgId;

    @ApiModelProperty(value = "品牌id")
    private Long brandsId;

    @ApiModelProperty(value = "更新频率，0-不更新，1-每天一次，2-每周一次，3-每月一次")
    private Integer updateType;

    @ApiModelProperty(value = "每周几/每月哪天")
    private Integer updateValue;

    @ApiModelProperty(value = "数据更新时间")
    private Date dataUpdateTime;

    @ApiModelProperty(value = "数据下次更新时间")
    private Date dataNextUpdateDate;

    @ApiModelProperty(value = "更新状态")
    private Integer dataUpdateStatus;

    @ApiModelProperty(value = "规则修改时间")
    private Date ruleUpdateTime;

}
