package org.etocrm.tagManager;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * @author lingshuang.pang
 * @Date 2020/8/31 13:26
 */
@SpringBootApplication
@ComponentScan({"org.etocrm.core","org.etocrm.kafkaServer","org.etocrm.core","org.etocrm.dynamicDataSource","org.etocrm.tagManager"})
@MapperScan(basePackages = {"org.etocrm.dynamicDataSource.mapper","org.etocrm.tagManager.mapper"})
@EnableDiscoveryClient
@EnableFeignClients
@EnableHystrix
@EnableAsync
@EnableKafka
@EnableAspectJAutoProxy(exposeProxy = true)
public class TagManagerApplication {
    public static void main(String[] args) {
        SpringApplication.run(TagManagerApplication.class, args);
    }
}
