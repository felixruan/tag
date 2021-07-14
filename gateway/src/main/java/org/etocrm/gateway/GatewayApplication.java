package org.etocrm.gateway;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @Author chengrong.yang
 * @date 2020/8/16 21:27
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
@EnableHystrix
@MapperScan(basePackages = {"org.etocrm.dynamicDataSource.mapper","org.etocrm.gateway.mapper"})
public class GatewayApplication {

    public static void main(String[] args) {
        try{
            SpringApplication.run(GatewayApplication.class, args);
        }catch(Exception e){
            e.printStackTrace();

        }

    }
}
