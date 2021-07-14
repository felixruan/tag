//package org.etocrm.dataManager.config;
//
//import org.etocrm.dataManager.batch.*;
//import org.springframework.batch.core.Job;
//import org.springframework.batch.core.JobExecutionListener;
//import org.springframework.batch.core.Step;
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
//    @Qualifier("tagJob")
//    public Job tagJob(JobBuilderFactory jobBuilderFactory,
//                      StepBuilderFactory stepBuilderFactory
//                      //,TagItemReader tagItemReader,
//                      //TagItemProcessor tagItemProcessor,
//                      //TagItemWriter tagItemWriter
//                      ) {
//        Step step = stepBuilderFactory.get("Tag-file-load")
//                .chunk(1)//读取多少次
//                //.reader(tagItemReader)
//                //.processor(tagItemProcessor)
//                //.writer(tagItemWriter)
//                .build();
//
//        return jobBuilderFactory.get("Tag-file-load")
//                .incrementer(new RunIdIncrementer())
//                .start(step)
//                .listener(jobListener())
//                .build();
//
//    }
//
//    @Bean
//    @Qualifier("tagGroupJob")
//    public Job tagGroupJob(JobBuilderFactory jobBuilderFactory,
//                      StepBuilderFactory stepBuilderFactory
//                      //,TagGroupItemReader tagGroupItemReader,
//                      //TagGroupItemProcessor tagGroupItemProcessor,
//                      //TagGroupItemWriter tagGroupItemWriter
//                           ) {
//        Step step = stepBuilderFactory.get("Tag-group-file-load")
//                .chunk(1)//读取多少次
//                //.reader(tagGroupItemReader)
//                //.processor(tagGroupItemProcessor)
//                //.writer(tagGroupItemWriter)
//                .build();
//
//        return jobBuilderFactory.get("Tag-group-Load")
//                .incrementer(new RunIdIncrementer())
//                .start(step)
//                .listener(jobListener())
//                .build();
//
//    }
//
//    @Bean
//    public JobExecutionListener jobListener(){
//        return new JobListener();
//    }
//}
