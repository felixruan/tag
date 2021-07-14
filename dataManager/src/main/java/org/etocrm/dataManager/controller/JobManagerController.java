package org.etocrm.dataManager.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import jdk.nashorn.internal.ir.annotations.Reference;
import org.etocrm.core.util.ResponseVO;
import org.etocrm.dataManager.model.VO.job.AddSysJobVO;
import org.etocrm.dataManager.model.VO.job.EditSysJobVO;
import org.etocrm.dataManager.model.VO.job.SysJobVo;
import org.etocrm.dataManager.model.VO.TriggerJobVO;
import org.etocrm.dataManager.service.IJobManagerService;
import org.etocrm.dataManager.util.JobUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * @Author chengrong.yang
 * @date 2020/9/1 13:39
 */
@RestController
@Reference
@RequestMapping("/jobManager/")
@Api(value = "定时任务API ",tags = "定时任务API")
public class JobManagerController {

    @Autowired
    IJobManagerService jobManagerService;

    @Autowired
    JobUtils jobUtils;


    /**
     * @Author chengrong.yang
     * @Description //获取任务组列表，触发器
     * @Date 2020/9/1 13:41
     * @Param []
     * @return org.etocrm.core.util.ResponseVO
     **/
    @GetMapping("getGroup")
    @ApiOperation(value = "获取执行器", notes = "获取执行器" )
    public ResponseVO getGroup() {
        return jobManagerService.getGroup();
    }

    /**
     * @Author chengrong.yang
     * @Description //分页获取任务列表
     * @Date 2020/9/1 13:50
     * @Param [sysJobVo]
     * @return org.etocrm.core.util.ResponseVO
     **/
    @GetMapping("getJobByPage")
    @ApiOperation(value = "获取job分页列表", notes = "获取job分页列表" )
    public ResponseVO getJobByPage(@RequestBody @Valid SysJobVo sysJobVo){
        return jobManagerService.getJobByPage(sysJobVo);
    }

    /**
     * @Author chengrong.yang
     * @Description //添加任务
     * @Date 2020/9/1 13:51
     * @Param [sysJobVo]
     * @return org.etocrm.core.util.ResponseVO
     **/
    @PostMapping("addJob")
    @ApiOperation(value = "添加任务", notes = "添加任务" )
    public ResponseVO addJob(@RequestBody @Valid AddSysJobVO addSysJobVO){
        return jobManagerService.addJob(addSysJobVO);
    }

    @PostMapping("editJob")
    @ApiOperation(value = "编辑任务", notes = "编辑任务" )
    public ResponseVO editJob(@RequestBody @Valid EditSysJobVO editSysJobVO){
        return jobManagerService.editJob(editSysJobVO);
    }


    /**
     * @Author chengrong.yang
     * @Description //手动触发一次定时任务
     * @Date 2020/9/1 13:55
     * @Param [sysJobVo]
     * @return org.etocrm.core.util.ResponseVO
     **/
    @PostMapping("triggerJob")
    @ApiOperation(value = "手动触发一次定时任务", notes = "手动触发一次定时任务" )
    public ResponseVO triggerJob(@RequestBody @Valid TriggerJobVO triggerJobVO){
        return jobManagerService.triggerJob(triggerJobVO);
    }

    /**
     * @Author chengrong.yang
     * @Description //移除任务
     * @Date 2020/9/1 13:56
     * @Param [sysJobVo]
     * @return org.etocrm.core.util.ResponseVO
     **/
    @PostMapping("removeJob")
    @ApiOperation(value = "删除任务", notes = "删除任务" )
    public ResponseVO removeJob(@RequestParam @NotNull(message = "任务id不能为空") Integer id){
        return jobManagerService.removeJob(id);
    }

    /**
     * @Author chengrong.yang
     * @Description //停止任务
     * @Date 2020/9/1 13:57
     * @Param [sysJobVo]
     * @return org.etocrm.core.util.ResponseVO
     **/
    @PostMapping("stopJob")
    @ApiOperation(value = "停止任务", notes = "停止任务" )
    public ResponseVO stopJob(@RequestParam @NotNull(message = "任务id不能为空") Integer id){
        return jobManagerService.stopJob(id);
    }

    /**
     * @Author chengrong.yang
     * @Description //启动任务
     * @Date 2020/9/1 13:58
     * @Param [sysJobVo]
     * @return org.etocrm.core.util.ResponseVO
     **/
    @PostMapping("startJob")
    @ApiOperation(value = "开始任务", notes = "开始任务" )
    public ResponseVO startJob(@RequestParam @NotNull(message = "任务id不能为空") Integer id){
        return jobManagerService.startJob(id);
    }


}
