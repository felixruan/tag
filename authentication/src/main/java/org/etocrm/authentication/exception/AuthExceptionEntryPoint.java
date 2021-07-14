package org.etocrm.authentication.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author chengrong.yang
 * @date 2020/8/19 19:27
 */
public class AuthExceptionEntryPoint implements AuthenticationEntryPoint {

    //自定义Token异常信息,用于tokan校验失败返回信息,比如token过期/验证错误
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,AuthenticationException authException) throws ServletException {
        Map map = new HashMap();
        map.put("code", "401");
        map.put("message", authException.getMessage());
        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.writeValue(response.getOutputStream(), map);
        } catch (Exception e) {
            throw new ServletException();
        }
    }
}
