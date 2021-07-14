package org.etocrm.tagManager.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.etocrm.core.enums.ResponseEnum;
import org.etocrm.core.util.JsonUtil;
import org.etocrm.core.util.ResponseVO;
import org.etocrm.dynamicDataSource.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @Author chengrong.yang
 * @Date 2020/9/30 17:26
 */
@Slf4j
public class SessionManagerInterceptor implements HandlerInterceptor {

    private static final String REQ_HEAD = "/tag";

    @Value("${spring.profiles.active}")
    private String env;

    @Autowired
    RedisUtil redisUtil;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object arg2) throws Exception {
        log.debug("=================== Interceptor in!!!==============");
        boolean isValid = check(request);
        if (isValid) {
            return true;
        } else {
            returnJson(response);
            return false;
        }
    }

    private boolean check(HttpServletRequest request) {
        if("dev".equals(env) || "local".equals(env)){
            return true;
        }
        String key = request.getHeader("X-REQUEST-KEY");
        String path = REQ_HEAD + request.getRequestURI();
        String value = (String) redisUtil.getValueByKey(key);
        if(!StringUtils.isEmpty(value) && value.equals(path)){
            return true;
        }
        return false;
    }

    private void returnJson(HttpServletResponse response) throws Exception {
        PrintWriter writer = null;
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=utf-8");
        try {
            writer = response.getWriter();
            writer.print(JsonUtil.toJson(ResponseVO.error(ResponseEnum.UNAUTHORIZED)));

        } catch (IOException e) {
            log.error("response error", e);
        } finally {
            if (writer != null)
                writer.close();
        }
    }
}
