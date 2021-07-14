package org.etocrm.dataManager.model.VO.job;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @Date 2020/9/9 20:14
 */
@Data
@Api(value = "添加任务实体类")
public class AddSysJobVO  implements Serializable {

    @ApiModelProperty(value = "执行器主键ID" )
    @NotNull(message = "执行器主键id不能为空")
    private Long jobGroup;

    // 任务执行CRON表达式
    @ApiModelProperty(value = "任务执行CRON表达式" )
    @NotBlank(message = "CRON表达式不能为空")
    private String jobCron;

    // 任务描述
    @ApiModelProperty(value = "任务描述" )
    @NotBlank(message = "任务描述不能为空")
    private String jobDesc;

    // 任务负责人
    @ApiModelProperty(value = "任务负责人" )
    @NotBlank(message = "任务负责人不能为空")
    private String author;

    // 报警邮件
    @ApiModelProperty(value = "报警邮件" )
    private String alarmEmail;

    // 子任务ID，多个逗号分隔
    @ApiModelProperty(value = "子任务ID，多个逗号分隔" )
    private String childJobId;

    /*
     * 执行器路由策略
     * FIRST 第一个
     * LAST 最后一个
     * ROUND 轮询
     * RANDOM 随机
     * CONSISTENT_HASH 一致性
     * LEAST_FREQUENTLY_USED 最不经常使用
     * LEAST_RECENTLY_USED 最近最久未使用
     * FAILOVER 故障转移
     * BUSYOVER 忙碌转移
     * SHARDING_BROADCAST 分片广播
     **/
    @ApiModelProperty(value = "执行器路由策略" +
            "     * FIRST 第一个" +
            "     * LAST 最后一个" +
            "     * ROUND 轮询" +
            "     * RANDOM 随机" +
            "     * CONSISTENT_HASH 一致性" +
            "     * LEAST_FREQUENTLY_USED 最不经常使用" +
            "     * LEAST_RECENTLY_USED 最近最久未使用" +
            "     * FAILOVER 故障转移" +
            "     * BUSYOVER 忙碌转移" +
            "     * SHARDING_BROADCAST 分片广播" )
    private String executorRouteStrategy;

    // 执行器，任务Handler名称
    @ApiModelProperty(value = "执行器，任务Handler名称" )
    private String executorHandler;

    // 执行器，任务参数
    @ApiModelProperty(value = "执行器，任务参数" )
    private String executorParam;

    // 阻塞处理策略
    @ApiModelProperty(value = "阻塞处理策略" )
    private String executorBlockStrategy;

    // 任务执行超时时间，单位秒
    @ApiModelProperty(value = "任务执行超时时间，单位秒" )
    private int executorTimeout;

    // 失败重试次数
    @ApiModelProperty(value = "失败重试次数" )
    private int executorFailRetryCount;

    /*
     * 运行模式
     * BEAN
     * GLUE_GROOVY GLUE(Java)
     * GLUE_SHELL GLUE(shell)
     * GLUE_PYTHON GLUE(python)
     * GLUE_PHP GLUE(php)
     * GLUE_NODEJS GLUE(NodeJs)
     * GLUE_POWERSHELL GLUE(PowerShell)
     **/
    @ApiModelProperty(value = "运行模式" +
            "      BEAN" +
            "      GLUE_GROOVY GLUE(Java)" +
            "      GLUE_SHELL GLUE(shell)" +
            "      GLUE_PYTHON GLUE(python)" +
            "      GLUE_PHP GLUE(php)" +
            "      GLUE_NODEJS GLUE(NodeJs)" +
            "      GLUE_POWERSHELL GLUE(PowerShell)" )
    private String glueType;

    // GLUE备注
    @ApiModelProperty(value = "GLUE备注" )
    private String glueRemark;

    // 调度状态：0-停止，1-运行
    @ApiModelProperty(value = "调度状态：0-停止，1-运行" )
    private int triggerStatus;

}
