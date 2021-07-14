package org.etocrm.dataManager.config;

import org.etocrm.dataManager.interceptor.SessionManagerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @Author chengrong.yang
 * @Date 2020/9/30 17:26
 */
@Configuration
public class SessionManagerAdaptor implements WebMvcConfigurer {

    @Bean
    public SessionManagerInterceptor sessionManagerInterceptor() {
        return new SessionManagerInterceptor();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 自定义拦截器，添加拦截路径和排除拦截路径
        registry.addInterceptor(sessionManagerInterceptor()).addPathPatterns("/**");
    }
}
