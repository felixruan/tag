package org.etocrm.tagManager.config;

import com.github.xiaoymin.knife4j.spring.annotations.EnableKnife4j;
import io.swagger.annotations.ApiOperation;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.Order;
import springfox.bean.validators.configuration.BeanValidatorPluginsConfiguration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Parameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @Author chengrong.yang
 * @date 2020/9/9 17:16
 */
@Configuration
@EnableSwagger2
@EnableKnife4j
@Import(BeanValidatorPluginsConfiguration.class)
public class SwaggerConfig {

    @Bean(value = "tagManagerApi")
    @Order(value = 1)
    public Docket groupRestApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(groupApiInfo())
                .globalOperationParameters(getGlobalOperationParameters())
                .select()
                .apis(RequestHandlerSelectors.withMethodAnnotation(ApiOperation.class))
                .paths(PathSelectors.any())
                .build()
                .directModelSubstitute(Timestamp.class, Date.class);
    }

    private ApiInfo groupApiInfo() {
        return new ApiInfoBuilder()
                .title("标签管理系统API文档")
                .description("<div style='font-size:14px;color:red;'> 标签管理模块 API</div>")
                .version("1.0")
                .build();
    }

    private List<Parameter> getGlobalOperationParameters() {
        List<Parameter> pars = new ArrayList<>();
        ParameterBuilder authParameterBuilder = new ParameterBuilder();
        authParameterBuilder.name("Authorization").description("登录用户填写").modelRef(new ModelRef("string")).parameterType("header").required(false);
        ParameterBuilder accessParameterBuilder = new ParameterBuilder();
        accessParameterBuilder.name("AccessKey").description("接口调用系统填写").modelRef(new ModelRef("string")).parameterType("header").required(false);
        ParameterBuilder signParameterBuilder = new ParameterBuilder();
        signParameterBuilder.name("Sign").description("接口调用系统填写").modelRef(new ModelRef("string")).parameterType("header").required(false);
        ParameterBuilder dataTimeParameterBuilder = new ParameterBuilder();
        dataTimeParameterBuilder.name("DataTime").description("接口调用系统填写").modelRef(new ModelRef("string")).parameterType("header").required(false);
        pars.add(authParameterBuilder.build());
        pars.add(accessParameterBuilder.build());
        pars.add(signParameterBuilder.build());
        pars.add(dataTimeParameterBuilder.build());
        return pars;
    }
}

