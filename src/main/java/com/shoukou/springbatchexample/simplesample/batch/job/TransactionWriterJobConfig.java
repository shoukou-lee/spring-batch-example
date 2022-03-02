package com.shoukou.springbatchexample.simplesample.batch.job;

import com.shoukou.springbatchexample.simplesample.model.Teacher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
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
public class TransactionWriterJobConfig {
    private static final String JOB_NAME = "transactionWriterBatch";
    private static final String BEAN_PREFIX = JOB_NAME + "_";

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final EntityManagerFactory entityManagerFactory;

    @Value("${chunkSize:1000}")
    private int chunkSize = 1000;

    @Bean
    public Job transactionWriterBatchJob() {
        return jobBuilderFactory.get(JOB_NAME)
                .preventRestart()
                .start(transactionWriterBatchStep())
                .build();
    }

    @Bean(BEAN_PREFIX + "step")
    @JobScope
    public Step transactionWriterBatchStep() {
        return stepBuilderFactory.get(BEAN_PREFIX + "step")
                .<Teacher, Teacher>chunk(chunkSize)
                .reader(transactionWriterBatchReader())
                .writer(transactionWriterBatchWriter())
                .build();
    }

    @Bean
    public JpaPagingItemReader<Teacher> transactionWriterBatchReader() {
        return new JpaPagingItemReaderBuilder<Teacher>()
                .name(BEAN_PREFIX + "reader")
                .entityManagerFactory(entityManagerFactory)
                .pageSize(chunkSize)
                .queryString("SELECT t FROM Teacher t")
                .build();
    }

    // writer 또한 Lazy loading 가능
    private ItemWriter<Teacher> transactionWriterBatchWriter() {
        return items -> {
            log.info(">>>>> Item write");
            for (Teacher item : items) {
                log.info("teacher = {}, student size = {}",
                        item.getName(), item.getStudents().size());
            }
        };
    }
}
