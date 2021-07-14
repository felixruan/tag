package org.etocrm.tagManager.model.VO.mat;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class MatReportingParameter {


    /** 事件code */
    @ApiModelProperty(value = "事件code")
    private String eventCode;

    /** 模块code */
    @ApiModelProperty(value = "模块code")
    private String modCode;

    /** 事件属性组 */
    @ApiModelProperty(value = "事件属性组")
    private List<MetadataPropertyParameter> eventProperty ;

    /** 用户属性组 */
    @ApiModelProperty(value = "用户属性组")
    private List<MetadataPropertyParameter> userProperty ;

    /**
     * 用于保证Kafka消费幂等性
     */
    @ApiModelProperty(value = "用于保证Kafka消费幂等性consumeSign",hidden = true)
    private String consumeSign;



}
