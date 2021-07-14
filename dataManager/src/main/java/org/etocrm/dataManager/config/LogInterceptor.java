package org.etocrm.dataManager.config;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.etocrm.core.util.JsonUtil;
import org.etocrm.core.util.StringUtil;
import org.etocrm.dynamicDataSource.model.DO.SysAuditLogDO;
import org.etocrm.dynamicDataSource.service.IAuditLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.NamedThreadLocal;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * @Author chengrong.yang
 * @Date 2020/9/16 15:28
 */
//@Aspect
//@Component
@Slf4j
public class LogInterceptor {

    private static final String LOG_SEPERATOR = "||";

//    @Autowired
//    IAuditLogService auditLogService;
//
//    // 记录每个Controller方法的开始时间，记录在线程中，保证线程安全
//    private NamedThreadLocal<StringBuilder> logInfoThreadLocal = new NamedThreadLocal(
//            "logInfoInThreadLocal");
//
//    private NamedThreadLocal<SysAuditLogDO> logInfoVoThreadLocal = new NamedThreadLocal(
//            "logInfoVoInThreadLocal");
//
//    /**
//     * 指定切点
//     */
//    @Pointcut("execution(public * org.etocrm.dataManager.controller.*.*(..))")
//    public void webLog() {
//    }
//
//    /**
//     * 前置通知，方法调用前被调用
//     *
//     * @param joinPoint
//     */
//    @Before("webLog()")
//    public void doBefore(JoinPoint joinPoint) {
//        StringBuilder sb = new StringBuilder("");
//        SysAuditLogDO auditLogDO = new SysAuditLogDO();
//        auditLogDO.setStartTime(System.currentTimeMillis());
//        sb.append("【ENTER_LOG】").append(LOG_SEPERATOR);
//
//        // 接收到请求，记录请求内容
//        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
//        if(attributes!=null){
//            HttpServletRequest req = attributes.getRequest();
//            sb.append("request id:").append(req.getSession().getId()).append(LOG_SEPERATOR);
//            auditLogDO.setRequestId(req.getSession().getId());
//            String token = req.getHeader("Authorization");
//            auditLogDO.setToken(token);
//            //线程号
//            sb.append("thread id:").append(Thread.currentThread().getId()).append(LOG_SEPERATOR);
//            auditLogDO.setThreadId(Thread.currentThread().getId());
//            // 记录下请求IP
//            sb.append("IP:").append(getIpAddress(req)).append(LOG_SEPERATOR);
//            auditLogDO.setRequestIp(getIpAddress(req));
//        }
//        //获取目标方法的参数信息
//        Signature signature = joinPoint.getSignature();
//        //AOP代理类的名字
//        sb.append("pkg:").append(signature.getDeclaringTypeName()).append(LOG_SEPERATOR);
//        auditLogDO.setRequestPkg(signature.getDeclaringTypeName());
//        //代理的是哪一个方法
//        sb.append("method:").append(signature.getName()).append(LOG_SEPERATOR);
//        auditLogDO.setRequestMethod(signature.getName());
//        //AOP代理类的类（class）信息
//        signature.getDeclaringType();
//        MethodSignature methodSignature = (MethodSignature) signature;
//        String[] strings = methodSignature.getParameterNames();
//        sb.append("参数名:").append(JsonUtil.toJson(strings)).append(LOG_SEPERATOR).append("参数值ARGS:").append(getArgs(joinPoint)).append(LOG_SEPERATOR);
//        auditLogDO.setRequestContent(JsonUtil.toJson(getArgs(joinPoint)));
//        logInfoThreadLocal.set(sb);
//        logInfoVoThreadLocal.set(auditLogDO);
//    }
//
//    private String getIpAddress(HttpServletRequest req) {
//        String ip = String.valueOf(req.getHeader("X-REQUEST-TRUE-IP"));
//        if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip) || "null".equals(ip)){
//            ip = req.getRemoteAddr();
//        }
//        return ip;
//    }
//
//
//    /**
//     * 处理完请求返回内容
//     *
//     * @param ret
//     * @throws Throwable
//     */
//    @AfterReturning(returning = "ret", pointcut = "webLog()")
//    public void doAfterReturning(Object ret) throws Throwable {
//        // 处理完请求，返回内容
//        StringBuilder sb = logInfoThreadLocal.get();
//        SysAuditLogDO auditLogDO = logInfoVoThreadLocal.get();
//        sb.append("返回值：").append(JsonUtil.toJson(ret)).append(LOG_SEPERATOR);
//        auditLogDO.setResponseContent(JsonUtil.toJson(ret));
//        String response = sb.toString().replaceAll("【ENTER_LOG】", "【RESPONSE_LOG】");
//        auditLogDO.setProcesTime(System.currentTimeMillis()-auditLogDO.getStartTime());
//        auditLogService.saveAuditLog(auditLogDO);
//    }
//
//    /**
//     * 后置异常通知
//     *
//     * @param jp
//     */
////    @AfterThrowing(throwing = "ex", pointcut = "webLog()")
//    public void throwss(Throwable ex, JoinPoint jp) {
//        log.error("后置异常信息", ex);
//    }
//
//    /**
//     * 后置最终通知,final增强，不管是抛出异常或者正常退出都会执行
//     *
//     * @param jp
//     */
//    @After("webLog()")
//    public void after(JoinPoint jp) {
//
//    }
//
//    /**
//     * 环绕通知,环绕增强，相当于MethodInterceptor
//     *
//     * @param pjp
//     * @return
//     */
//    @Around("webLog()")
//    public Object arround(ProceedingJoinPoint pjp) throws Throwable {
//        try {
//            Object o = pjp.proceed();
//            return o;
//        } catch (Throwable e) {
//            log.error("异常信息", e);
//            throw e;
//        }
//    }
//
//    private String getArgs(JoinPoint point) {
//        Object[] args = point.getArgs();
//        if (args == null) {
//            return "";
//        } else {
//            StringBuilder sb = new StringBuilder("");
//            for (int i = 0, n = args.length; i < n; i++) {
//                String argTmp = StringUtil.valueOf(args[i]);
//                sb.append("【").append(argTmp).append("】");
//            }
//            return sb.toString();
//        }
//    }
}
