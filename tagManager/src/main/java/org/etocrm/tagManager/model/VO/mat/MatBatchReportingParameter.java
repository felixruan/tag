package org.etocrm.tagManager.model.VO.mat;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class MatBatchReportingParameter {


    /** 被动流程id */
    @ApiModelProperty(value = "被动流程id")
    private Long workId;

    /** 父级流程分支id */
    @ApiModelProperty(value = "父级流程分支id")
    private Long handleId;

    /** 机构id */
    @ApiModelProperty(value = "机构id")
    private Long orgId;

    /** 批次id */
    @ApiModelProperty(value = "批次id")
    private Long batchId;

    /** 批次数量 */
    @ApiModelProperty(value = "批次数量")
    private Integer batchSize;

    /** 用户信息数组 */
    @ApiModelProperty(value = "用户信息数组")
    private List<MatBatchReportingUserParameter> userParameters;

    /**
     * 用于保证Kafka消费幂等性
     */
    @ApiModelProperty(value = "用于保证Kafka消费幂等性consumeSign",hidden = true)
    private String consumeSign;

}
