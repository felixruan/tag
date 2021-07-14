//package org.etocrm.tagManager.config;
//
//import org.etocrm.tagManager.batch.*;
//import org.springframework.batch.core.*;
//import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
//import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
//import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
//import org.springframework.batch.core.launch.support.RunIdIncrementer;
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
///**
// * @Author chengrong.yang
// * @date 2020/9/2 9:50
// */
//@Configuration
//@EnableBatchProcessing
//public class DBBatchConfig {
//
//    @Bean
//    @Qualifier("etlJob")
//    public Job etlJob(JobBuilderFactory jobBuilderFactory,
//                      StepBuilderFactory stepBuilderFactory,
//                      EtlItemReader itemReader,
//                      EtlItemProcessor itemProcessor,
//                      EtlItemWriter itemWriter) {
//        Step step = stepBuilderFactory.get("ETL-file-load")
//                .chunk(1)//读取多少次
//                .reader(itemReader)
//                .processor(itemProcessor)
//                .writer(itemWriter)
//                .build();
//
//        return jobBuilderFactory.get("ETL-Load")
//                .incrementer(new RunIdIncrementer())
//                .start(step)
//                .listener(jobListener())
//                .build();
//
//    }
//
//    @Bean
//    @Qualifier("modelTableJob")
//    public Job modelTableJob(JobBuilderFactory jobBuilderFactory,
//                             StepBuilderFactory stepBuilderFactory,
//                             ModelTableItemReader itemReader,
//                             ModelTableItemProcessor itemProcessor,
//                             ModelTableItemWriter itemWriter) {
//        Step step = stepBuilderFactory.get("ModelTable-file-load")
//                .chunk(1)//读取多少次
//                .reader(itemReader)
//                .processor(itemProcessor)
//                .writer(itemWriter)
//                .build();
//
//        return jobBuilderFactory.get("ModelTable-Load")
//                .incrementer(new RunIdIncrementer())
//                .start(step)
//                .listener(jobListener())
//                .build();
//
//    }
//
//    @Bean
//    public JobExecutionListener jobListener() {
//        return new JobListener();
//    }
//
//}
