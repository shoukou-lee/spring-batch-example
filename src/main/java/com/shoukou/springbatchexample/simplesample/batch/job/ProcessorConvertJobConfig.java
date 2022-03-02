package com.shoukou.springbatchexample.simplesample.batch.job;

import com.shoukou.springbatchexample.simplesample.model.Teacher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManagerFactory;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class ProcessorConvertJobConfig {
    public static final String JOB_NAME = "ProcessorConvertBatch";
    public static final String BEAN_PREFIX = JOB_NAME + "_";

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final EntityManagerFactory entityManagerFactory;

    // lombok 아닌 org.springframework.beans.factory.annotation.Value
    @Value("${chunkSize:1000}")
    private int chunkSize;

    @Bean(JOB_NAME)
    public Job job() {
        return jobBuilderFactory.get(JOB_NAME)
                .preventRestart()
                .start(step())
                .build();
    }

    @Bean(BEAN_PREFIX + "step")
    @JobScope
    public Step step() {
        return stepBuilderFactory.get(BEAN_PREFIX + "step")
                .<Teacher, Teacher>chunk(chunkSize) // Reader에서 읽어올 타입, Writer의 입력타입
                .reader(reader())
                .processor(processor())
                .writer(writer())
                .build();
    }

    // 모든 teacher를 조회
    @Bean
    public JpaPagingItemReader<Teacher> reader() {
        return new JpaPagingItemReaderBuilder<Teacher>()
                .name(BEAN_PREFIX + "reader")
                .entityManagerFactory(entityManagerFactory)
                .pageSize(chunkSize)
                .queryString("SELECT t FROM Teacher t")
                .build();
    }

    @Bean
    public ItemProcessor<Teacher, Teacher> processor() {
        return teacher -> {
            // 짝수 아이디이면 NULL 리턴
            boolean isIgnoreTarget = teacher.getId() % 2 == 0L;
            if (isIgnoreTarget) {
                log.info(">>>>> Teacher name = {}, isIgnoreTarget = {}",
                        teacher.getName(), isIgnoreTarget);
                return null;
            }
            
            return teacher;
        };
    }

    // 출력
    private ItemWriter<Teacher> writer() {
        return items -> {
            for (Teacher item: items) {
                log.info("Teacher Name = {}", item.getName());
            }
        };
    }
}
