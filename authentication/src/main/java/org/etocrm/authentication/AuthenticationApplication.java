package org.etocrm.authentication;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * @Author chengrong.yang
 * @date 2020/8/17 14:20
 */
@SpringBootApplication
@EnableFeignClients
@EnableDiscoveryClient
@ComponentScan({"org.etocrm.core","org.etocrm.dynamicDataSource","org.etocrm.authentication"})
@MapperScan(basePackages = {"org.etocrm.dynamicDataSource.mapper","org.etocrm.authentication.mapper"})
@EnableAsync
public class AuthenticationApplication {

    public static void main(String[] args) {
        try{
            SpringApplication.run(AuthenticationApplication.class, args);
        }catch(Exception e){
            e.printStackTrace();
        }

    }
}
