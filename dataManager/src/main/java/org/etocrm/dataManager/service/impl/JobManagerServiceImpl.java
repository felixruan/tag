package org.etocrm.dataManager.service.impl;

import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;

import com.alibaba.druid.util.StringUtils;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;

import com.xxl.job.core.enums.ExecutorBlockStrategyEnum;
import com.xxl.job.core.glue.GlueTypeEnum;
import lombok.extern.slf4j.Slf4j;

import org.etocrm.core.util.BeanUtils;
import org.etocrm.core.util.ResponseVO;
import org.etocrm.dataManager.model.VO.job.AddSysJobVO;
import org.etocrm.dataManager.model.VO.job.EditSysJobVO;
import org.etocrm.dataManager.model.VO.job.SysJobVo;
import org.etocrm.dataManager.model.VO.TriggerJobVO;
import org.etocrm.dataManager.service.IJobManagerService;
import org.etocrm.dataManager.util.JobUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author chengrong.yang
 * @date 2020/9/1 13:59
 */
@Service
@Slf4j
public class JobManagerServiceImpl implements IJobManagerService {

    @Value("${xxl.base.uri}")
    private String BASE_URI;

    @Value("${xxl.base.job_info_uri}")
    private String JOB_INFO_URI;

    @Value("${xxl.base.job_group_uri}")
    private String JOB_GROUP_URI;

    @Autowired
    private JobUtils jobUtils;

    @Override
    public ResponseVO getGroup() {
        try {
            String cookie=this.jobUtils.getCookie();
            if(StringUtils.isEmpty(cookie)){
                return ResponseVO.error(4001,"获取cookie失败");
            }
            HttpResponse execute = HttpUtil.createGet(BASE_URI + JOB_GROUP_URI + "/pageList").addHeaders(getCookie(cookie)).execute();
            JSONObject result=JSONObject.parseObject(execute.body());
            Integer code = result.getInteger("code");
            if(code==null){
                return ResponseVO.success(result);
            }else{
                return ResponseVO.error(4002,result.getString("msg"));
            }
        } catch (Exception e) {
            // TODO 定义错误的返回
            log.error(e.getMessage(), e);
            return ResponseVO.error(4003,"获取失败");
        }
    }

    @Override
    public ResponseVO getJobByPage(SysJobVo sysJobVo) {
        try {
            Map<String, Object> jobInfo = Maps.newHashMap();
            jobInfo.put("start", sysJobVo.getCurrent());
            jobInfo.put("length", sysJobVo.getPageSize());
            jobInfo.put("jobGroup", sysJobVo.getJobGroup());
            jobInfo.put("triggerStatus", -1);
            String cookie=this.jobUtils.getCookie();
            if(StringUtils.isEmpty(cookie)){
                return ResponseVO.error(4001,"获取失败,获取cookie失败");
            }
            HttpResponse execute = getExecute(BASE_URI + JOB_INFO_URI + "/pageList",getCookie(cookie),jobInfo);
            JSONObject result=JSONObject.parseObject(execute.body());
            Integer code = result.getInteger("code");
            if(code==null){
                return ResponseVO.success(result);
            }else{
                return ResponseVO.error(4002,result.getString("msg"));
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return  ResponseVO.error(4003,"获取任务列表失败");
        }
    }

    @Override
    public ResponseVO addJob(AddSysJobVO addSysJobVo) {
        try {
            EditSysJobVO editSysJobVo=new EditSysJobVO();
            BeanUtils.copyPropertiesIgnoreNull(addSysJobVo,editSysJobVo);
            Map<String, Object> jobInfo=getXxlJobInfo(editSysJobVo);
            String cookie=this.jobUtils.getCookie();
            if(StringUtils.isEmpty(cookie)){
                return ResponseVO.error(4001,"获取cookie失败");
            }
            HttpResponse execute = getExecute(BASE_URI + JOB_INFO_URI + "/add",getCookie(cookie),jobInfo);
            JSONObject resultJson=JSONObject.parseObject(execute.body());
            int code=resultJson.getInteger("code");
            if(code==200){
                JSONObject result=new JSONObject();
                result.put("jobId",resultJson.get("content"));
                return ResponseVO.success(result);
            }else{
                return ResponseVO.error(4001,resultJson.get("msg").toString());
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ResponseVO.error(4003,"添加失败！");
        }
    }

    @Override
    public ResponseVO triggerJob(TriggerJobVO triggerJobVO) {
        try {
            Map<String, Object> jobInfo = Maps.newHashMap();
            jobInfo.put("id", triggerJobVO.getId());
            jobInfo.put("executorParam", triggerJobVO.getExecutorParam());
            String cookie=this.jobUtils.getCookie();
            if(StringUtils.isEmpty(cookie)){
                return ResponseVO.error(4001,"获取cookie失败");
            }
            HttpResponse execute = getExecute(BASE_URI + JOB_INFO_URI + "/trigger",getCookie(cookie),jobInfo);
            JSONObject result=JSONObject.parseObject(execute.body());
            int code=result.getInteger("code");
            return code==200?ResponseVO.success(0,"执行一次任务成功"):ResponseVO.error(4002,result.get("msg").toString());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ResponseVO.error(4003,"执行一次任务失败");
        }
    }

    @Override
    public ResponseVO removeJob(Integer id) {
        try {
            Map<String, Object> jobInfo = Maps.newHashMap();
            jobInfo.put("id", id);
            String cookie=this.jobUtils.getCookie();
            if(StringUtils.isEmpty(cookie)){
                return ResponseVO.error(4001,"获取cookie失败");
            }
            HttpResponse execute = getExecute(BASE_URI + JOB_INFO_URI + "/remove",getCookie(cookie),jobInfo);
            JSONObject result=JSONObject.parseObject(execute.body());
            int code=result.getInteger("code");
            return code==200?ResponseVO.success(0,"删除任务成功"):ResponseVO.error(4002,result.get("msg").toString());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ResponseVO.error(4003,"删除任务失败");
        }
    }

    @Override
    public ResponseVO stopJob(Integer id) {
        try {
            Map<String, Object> jobInfo = Maps.newHashMap();
            jobInfo.put("id", id);
            String cookie=this.jobUtils.getCookie();
            if(StringUtils.isEmpty(cookie)){
                return ResponseVO.error(4001,"获取cookie失败");
            }
            HttpResponse execute = getExecute(BASE_URI + JOB_INFO_URI + "/stop",getCookie(cookie),jobInfo);
            JSONObject result=JSONObject.parseObject(execute.body());
            int code=result.getInteger("code");
            return code==200?ResponseVO.success(0,"停止任务成功"):ResponseVO.error(4002,result.get("msg").toString());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ResponseVO.error(4003,"停止任务失败");
        }
    }

    @Override
    public ResponseVO startJob(Integer id) {
        try {
            Map<String, Object> jobInfo = Maps.newHashMap();
            jobInfo.put("id", id);
            String cookie=this.jobUtils.getCookie();
            if(StringUtils.isEmpty(cookie)){
                return ResponseVO.error(4001,"获取cookie失败");
            }
            HttpResponse execute =getExecute(BASE_URI + JOB_INFO_URI + "/start",getCookie(cookie),jobInfo);
            JSONObject result=JSONObject.parseObject(execute.body());
            int code=result.getInteger("code");
            return code==200?ResponseVO.success(0,"开始任务成功"):ResponseVO.error(4002,result.get("msg").toString());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ResponseVO.error(4003,"开始任务失败");
        }
    }

    @Override
    public ResponseVO editJob(EditSysJobVO editSysJobVO) {
        try {
            Map<String, Object> jobInfo=getXxlJobInfo(editSysJobVO);
            String cookie=this.jobUtils.getCookie();
            if(StringUtils.isEmpty(cookie)){
                return ResponseVO.error(4001,"获取cookie失败");
            }
            HttpResponse execute = getExecute(BASE_URI + JOB_INFO_URI + "/update",getCookie(cookie),jobInfo);
            JSONObject resultJson=JSONObject.parseObject(execute.body());
            int code=resultJson.getInteger("code");
            return code==200?ResponseVO.success(0,"修改成功"):ResponseVO.error(4003,"添加失败！");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ResponseVO.error(4003,"添加失败！");
        }
    }

    private Map<String,String> getCookie(String cookie){
        log.info("------->获取到的cookie" + cookie);
        Map<String, String> map = new HashMap<>();
        map.put("Cookie", cookie);
        return map;
    }
    private HttpResponse getExecute(String url,Map<String,String>cookie,Map<String, Object>jobInfo){
        HttpResponse execute = HttpUtil.createGet(url).addHeaders(cookie).form(jobInfo).execute();
        return execute;
    }
    private Map<String, Object> getXxlJobInfo(EditSysJobVO editSysJobVO){
        Map<String, Object> jobInfo = Maps.newHashMap();
        jobInfo.put("id", editSysJobVO.getId());
        jobInfo.put("jobGroup", editSysJobVO.getJobGroup());
        jobInfo.put("jobCron", editSysJobVO.getJobCron());
        jobInfo.put("jobDesc", editSysJobVO.getJobDesc());
        jobInfo.put("author", editSysJobVO.getAuthor());
        jobInfo.put("childJobId", editSysJobVO.getChildJobId());
        jobInfo.put("executorRouteStrategy",editSysJobVO.getExecutorRouteStrategy());
        jobInfo.put("executorHandler", editSysJobVO.getExecutorHandler());
        jobInfo.put("executorParam", editSysJobVO.getExecutorParam());
        jobInfo.put("alarmEmail", editSysJobVO.getAlarmEmail());
        jobInfo.put("executorBlockStrategy", ExecutorBlockStrategyEnum.SERIAL_EXECUTION);
        jobInfo.put("glueType", GlueTypeEnum.BEAN);
        return jobInfo;
    }
}
