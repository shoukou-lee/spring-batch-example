package com.shoukou.springbatchexample.simplesample.batch.job;

import com.shoukou.springbatchexample.simplesample.batch.reader.QueryDslPagingItemReader;
import com.shoukou.springbatchexample.model.DormantUser;
import com.shoukou.springbatchexample.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.builder.JpaItemWriterBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManagerFactory;

import static com.shoukou.springbatchexample.model.QUser.*;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class QueryDslJobConfig {
    public static final String JOB_NAME = "queryDslBatchJob";

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final EntityManagerFactory entityManagerFactory;
    private final QueryDslPagingItemReaderJobParameter jobParameter;

    private int chunkSize;

    @Value("${chunkSize:1000}")
    public void setChunkSize(int chunkSize) {
        this.chunkSize = chunkSize;
    }

    @Bean
    @JobScope
    public QueryDslPagingItemReaderJobParameter jobParameter() {
        return new QueryDslPagingItemReaderJobParameter();
    }

    @Bean
    public Job queryDslBatchjob() {
        return jobBuilderFactory.get(JOB_NAME)
                .start(queryDslBatchStep())
                .build();
    }

    @Bean
    public Step queryDslBatchStep() {
        return stepBuilderFactory.get("queryDslBatchStep")
                .<User, DormantUser>chunk(chunkSize)
                .reader(queryDslBatchReader())
                .processor(queryDslBatchProcessor())
                .writer(queryDslBatchWriter())
                .build();
    }

    @Bean
    public QueryDslPagingItemReader<User> queryDslBatchReader() {
        return new QueryDslPagingItemReader<>(entityManagerFactory, chunkSize,
                jpaQueryFactory -> jpaQueryFactory.selectFrom(user)
                        .where(user.lastAccess.eq(jobParameter.getLastAccess()))
                );
    }

    private ItemProcessor<User, DormantUser> queryDslBatchProcessor() {
        return DormantUser::new;
    }

    @Bean
    public JpaItemWriter<DormantUser> queryDslBatchWriter() {
        return new JpaItemWriterBuilder<DormantUser>()
                .entityManagerFactory(entityManagerFactory)
                .build();
    }
}
