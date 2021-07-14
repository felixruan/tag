package org.etocrm.databinlog;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.kafka.annotation.EnableKafka;

@SpringBootApplication
@ComponentScan({"org.etocrm.core","org.etocrm.kafkaServer","org.etocrm.dynamicDataSource","org.etocrm.databinlog"})
@MapperScan(basePackages = {"org.etocrm.dynamicDataSource.mapper", "org.etocrm.dataManager.mapper"})
@EnableKafka
public class DataBinlogApplication {

    public static void main(String[] args) {
        SpringApplication.run(DataBinlogApplication.class, args);
    }

}
