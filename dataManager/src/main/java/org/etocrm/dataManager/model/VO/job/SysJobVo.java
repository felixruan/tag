package org.etocrm.dataManager.model.VO.job;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.etocrm.dataManager.model.VO.job.PageInfoVO;

import java.io.Serializable;

/**
 * @Author chengrong.yang
 * @date 2020/9/1 11:40
 */
@Data
@Api(value = "获取任务实体类")
public class SysJobVo extends PageInfoVO implements Serializable {

   // 执行器主键ID
    @ApiModelProperty(value = "执行器主键ID" )
    private Long jobGroup;

    // 任务描述
    @ApiModelProperty(value = "任务描述" )
    private String jobDesc;

    // 任务负责人
    @ApiModelProperty(value = "任务负责人" )
    private String author;

    // 执行器，任务Handler名称
    @ApiModelProperty(value = "执行器，任务Handler名称" )
    private String executorHandler;

    // 调度状态：0-停止，1-运行
    @ApiModelProperty(value = "调度状态：0-停止，1-运行" )
    private int triggerStatus;

}
