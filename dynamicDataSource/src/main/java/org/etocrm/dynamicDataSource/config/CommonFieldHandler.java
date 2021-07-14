package org.etocrm.dynamicDataSource.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.extension.plugins.OptimisticLockerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.etocrm.core.util.DateUtil;
import org.etocrm.core.util.StringUtil;
import org.etocrm.dynamicDataSource.model.VO.UniteUserAuthOutVO;
import org.etocrm.dynamicDataSource.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * @Author chengrong.yang
 * @date 2020/8/29 17:15
 */
@Slf4j
@Component
public class CommonFieldHandler implements MetaObjectHandler {

    @Autowired
    private RedisUtil redisUtil;

    UniteUserAuthOutVO userInVO;

    private void getUser() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes != null) {
            ServletRequestAttributes attributes = (ServletRequestAttributes) requestAttributes;
            HttpServletRequest request = attributes.getRequest();
            String token = request.getHeader("Authorization");
            if (StringUtils.hasText(token)) {
//                userInVO = redisUtil.getOauthTokenRefresh(token, UniteUserAuthOutVO.class);
                userInVO = redisUtil.getOauthToken(token, UniteUserAuthOutVO.class);
            }
        }
    }

    @Override
    public void insertFill(MetaObject metaObject) {
        log.info("start insert fill ....");
        getUser();
        if (userInVO != null) {
            this.setFieldValByName("createdBy", userInVO.getId(), metaObject);
            this.setFieldValByName("updatedBy", userInVO.getId(), metaObject);
        }
        this.setFieldValByName("createdTime", DateUtil.getTimestamp(), metaObject);
        this.setFieldValByName("updatedTime", DateUtil.getTimestamp(), metaObject);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        log.info("start update fill ....");
        getUser();
        if (userInVO != null) {
            this.setFieldValByName("updatedBy", userInVO.getId(), metaObject);
        }
        this.setFieldValByName("updatedTime", DateUtil.getTimestamp(), metaObject);
        String str = StringUtil.valueOf(this.getFieldValByName("deleted", metaObject));

        if (!StringUtils.isEmpty(str)) {
            Integer deleted = Integer.valueOf(str);
            this.setFieldValByName("deleted", deleted, metaObject);
            this.setFieldValByName("deleteTime", DateUtil.getTimestamp(), metaObject);
            if (userInVO != null) {
                this.setFieldValByName("deleteBy", userInVO.getId(), metaObject);
            }
        }else{
            this.setFieldValByName("deleted", 0, metaObject);
        }
    }

    @Bean
    public OptimisticLockerInterceptor optimisticLockerInterceptor() {
        return new OptimisticLockerInterceptor();
    }

    @Bean
    public PaginationInterceptor paginationInterceptor() {
        PaginationInterceptor paginationInterceptor = new PaginationInterceptor();
        return paginationInterceptor;
    }
}
