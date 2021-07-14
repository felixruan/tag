package org.etocrm.tagManager.util;

import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.etocrm.core.util.ResponseVO;
import org.etocrm.tagManager.api.IDataManagerService;
import org.etocrm.tagManager.config.XxlJobProps;
import org.etocrm.tagManager.model.VO.AddSysJobVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.HttpCookie;
import java.util.List;

/**
 * @author shengjie.ding
 * @create 2020/9/2 14:13
 */
@Component
public class JobUtils {

    @Value("${xxl.base.job_user_name}")
    private String JOB_USER_NAME;

    @Value("${xxl.base.job_pass_word}")
    private String JOB_PASS_WORD;

    @Value("${xxl.base.uri}")
    private String BASE_URI;

    // 任务执行CRON表达式
    @Value("${data-source-params.dataSourceCorn}")
    private String jobCron;

    // 任务负责人
    @Value("${xxl-job-params.author}")
    private String author;

    // 报警邮件
    @Value("${xxl-job-params.alarmEmail}")
    private String alarmEmail;

    // 任务执行超时时间，单位秒
    @Value("${xxl-job-params.executorTimeout}")
    private int executorTimeout;

    // 失败重试次数
    @Value("${xxl-job-params.executorFailRetryCount}")
    private int executorFailRetryCount;

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
    @Value("${xxl-job-params.executorRouteStrategy}")
    private String executorRouteStrategy;

    @Autowired
    IDataManagerService iDataManagerService;

    @Autowired
    private XxlJobProps xxlJobProps;

    public  String getCookie() throws Exception{
        String loginUrl=BASE_URI+"/login?userName="+JOB_USER_NAME+"&password="+JOB_PASS_WORD;
        HttpResponse execute= HttpUtil.createPost(loginUrl).execute();
        StringBuilder tmpCookies = new StringBuilder();
        if(execute.getStatus()==200){
            List<HttpCookie> cookies=execute.getCookies();
            for (HttpCookie cookie:cookies){
                tmpCookies.append(cookie.toString()+";");
            }
        }
        if(tmpCookies.length()>0){
            return tmpCookies.toString();
        }else{
            return null;
        }
    }
    /**
     * executorParam:任务所需参数
     * jobDesc：任务描述
     * executorHandler:执行器handler
     * triggerStatus调度状态：0-停止，1-运行
     *
     * */
    public AddSysJobVO getAddSysJobVO(String executorParam, String jobDesc, String executorHandler, int triggerStatus){
        AddSysJobVO addSysJobVO=new AddSysJobVO();
        addSysJobVO.setAlarmEmail(alarmEmail);
        addSysJobVO.setAuthor(author);
        addSysJobVO.setExecutorHandler(executorHandler);
        addSysJobVO.setExecutorParam(executorParam);
        addSysJobVO.setExecutorTimeout(executorTimeout);
        addSysJobVO.setJobCron(jobCron);
        addSysJobVO.setJobDesc(jobDesc);
        addSysJobVO.setTriggerStatus(triggerStatus);
        addSysJobVO.setExecutorFailRetryCount(executorFailRetryCount);
        addSysJobVO.setExecutorRouteStrategy(executorRouteStrategy);
        if(getJObGroupId()!=null){
            addSysJobVO.setJobGroup(Long.decode(getJObGroupId().toString()));
        }else{
            return null;
        }
        return addSysJobVO;
    }
    /**
     * 通过配置文件xxljob 的appName获取执行器id
     * 上线后需要创建执行器
     *
     * */

    private Integer  getJObGroupId(){
        ResponseVO responseVO=this.iDataManagerService.getGroup();
        if(responseVO.getCode()==0){
            Object obj =responseVO.getData();
            if(obj!=null){
                JSONObject data=JSONObject.parseObject(obj.toString());
                if(data!=null){
                    JSONArray dataArr=data.getJSONArray("data");
                    for (int i=0;i<dataArr.size();i++) {
                        JSONObject dataobj=dataArr.getJSONObject(i);
                        if(this.xxlJobProps.getExecutor().getAppName().equals(dataobj.getString("appname"))){
                            return dataobj.getInteger("id");
                        }
                    }
                }

            }
        }
        return null;
    }
}
