package org.etocrm.dataManager;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * @Author chengrong.yang
 * @date 2020/8/29 18:49
 */
@SpringBootApplication
@ComponentScan({"org.etocrm.core","org.etocrm.kafkaServer","org.etocrm.dynamicDataSource","org.etocrm.dataManager"})
@MapperScan(basePackages = {"org.etocrm.dynamicDataSource.mapper", "org.etocrm.dataManager.mapper"})
@EnableDiscoveryClient
@EnableFeignClients
@EnableHystrix
@EnableAsync
@EnableKafka
public class DataManagerApplication {

    public static void main(String[] args) {
        SpringApplication.run(DataManagerApplication.class, args);
    }
}
