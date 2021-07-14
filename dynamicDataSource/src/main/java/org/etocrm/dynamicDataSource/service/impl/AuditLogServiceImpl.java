package org.etocrm.dynamicDataSource.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.etocrm.dynamicDataSource.mapper.IAuditLogMapper;
import org.etocrm.dynamicDataSource.model.DO.SysAuditLogDO;
import org.etocrm.dynamicDataSource.model.VO.UniteUserAuthOutVO;
import org.etocrm.dynamicDataSource.service.IAuditLogService;
import org.etocrm.dynamicDataSource.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * @Author chengrong.yang
 * @Date 2020/9/18 10:25
 */
@Service
@Slf4j
public class AuditLogServiceImpl implements IAuditLogService {

    @Autowired
    RedisUtil redisUtil;

    @Autowired
    IAuditLogMapper auditLogMapper;

    @Value("${sysAuditLog.MAX_SIZE}")
    private int MAX_NUMBER;

    @Override
    @Async//@Async("asyncServiceExecutor")
    public void saveAuditLog(SysAuditLogDO sysAuditLogDO) {
        String token = sysAuditLogDO.getToken();
        if (!StringUtils.isEmpty(token)) {
//            UniteUserAuthOutVO tokenVO = redisUtil.getOauthTokenRefresh(token, UniteUserAuthOutVO.class);
            UniteUserAuthOutVO tokenVO = redisUtil.getOauthToken(token, UniteUserAuthOutVO.class);
            if (null != tokenVO) {
                sysAuditLogDO.setUserId(tokenVO.getId());
            }
        }
        try {
            if (!StringUtils.isEmpty(sysAuditLogDO.getRequestContent())) {
                int length = sysAuditLogDO.getRequestContent().length();
                log.info("request请求参数长度为：" + length);
                if (length > MAX_NUMBER) {
                    String substring = sysAuditLogDO.getRequestContent().substring(0, MAX_NUMBER);
                    //log.info("request请求参数" + substring);
                    sysAuditLogDO.setRequestContent(substring);
                }
            }
            if (!StringUtils.isEmpty(sysAuditLogDO.getResponseContent())) {
                int length = sysAuditLogDO.getResponseContent().length();
                log.info("Response请求参数长度为：" + length);
                if (length > MAX_NUMBER) {
                    String substring = sysAuditLogDO.getResponseContent().substring(0, MAX_NUMBER);
                    //log.info("Response请求参数" + substring);
                    sysAuditLogDO.setResponseContent(substring);
                }
            }

            auditLogMapper.insert(sysAuditLogDO);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
}
