package com.shoukou.springbatchexample.simplesample.batch.job;

import com.shoukou.springbatchexample.simplesample.model.ClassInformation;
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
public class TransactionProcessorJobConfig {

    public static final String JOB_NAME = "transactionProcessorBatch";
    public static final String BEAN_PREFIX = JOB_NAME + "_";

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final EntityManagerFactory entityManagerFactory;

    @Value("${chunkSize:1000}")
    private int chunkSize;

    @Bean(JOB_NAME)
    public Job transactionProcessorBatchJob() {
        return jobBuilderFactory.get(JOB_NAME)
                .preventRestart()
                .start(transactionProcessorBatchStep())
                .build();
    }

    @Bean(BEAN_PREFIX + "step")
    @JobScope
    public Step transactionProcessorBatchStep() {
        return stepBuilderFactory.get(BEAN_PREFIX + "step")
                /* processor의 트랜잭션 확인
                .<Teacher, ClassInformation>chunk(chunkSize)
                .reader(transactionReader())
                .processor(transactionProcessor())
                .writer(transactionWriter())
                .build();
                 */
                .<Teacher, Teacher>chunk(chunkSize)
                .reader(transactionProcessorBatchReader())
                .writer(transactionProcessorBatchWriter2())
                .build();
    }

    @Bean
    public JpaPagingItemReader<Teacher> transactionProcessorBatchReader() {
        return new JpaPagingItemReaderBuilder<Teacher>()
                .name(BEAN_PREFIX + "reader")
                .entityManagerFactory(entityManagerFactory)
                .pageSize(chunkSize)
                .queryString("SELECT t FROM Teacher t")
                .build();
    }

    // transaction 범위 안에서의 processor 처리
    public ItemProcessor<Teacher, ClassInformation> transactionProcessor() {
        return teacher -> ClassInformation.builder()
                .teacherName(teacher.getName())
                .studentSize(teacher.getStudents().size())
                .build();
    }

    private ItemWriter<ClassInformation> transactionWriter() {
        return items -> {
            log.info(">>>>> Item Write");
            for (ClassInformation item : items) {
                log.info("반 정보 = {}", item);
            }
        };
    }

    private ItemWriter<Teacher> transactionProcessorBatchWriter2() {
        return items -> {
            log.info(">>>>> [transactionProcessorBatchWriter2] Item Write");
            for (Teacher item : items) {
                log.info("teacher={}, studentSize={}",
                        item.getName(), item.getStudents().size());
            }
        };
    }
}
