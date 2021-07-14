package org.etocrm.dataManager.model.VO;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @Date 2020/9/9 20:31
 */
@Data
@Api(value = "执行一次任务实体类")
public class TriggerJobVO {
    // 主键ID
    @ApiModelProperty(value = "主键ID" )
    @NotNull(message = "任务id不能为空！")
    private Long id;

    // 执行器，任务参数
    @ApiModelProperty(value = "执行器，任务参数" )
    private String executorParam;
}
