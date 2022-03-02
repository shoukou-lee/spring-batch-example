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
import org.springframework.batch.item.support.CompositeItemProcessor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManagerFactory;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class ProcessorCompositeJobConfig {
    public static final String JOB_NAME = "processorCompositeBatch";
    public static final String BEAN_PREFIX = JOB_NAME + "_";

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final EntityManagerFactory entityManagerFactory;

    @Value("${chunkSize:1000}")
    private int chunkSize;

    @Bean(JOB_NAME)
    public Job processorCompositeBatchJob() {
        return jobBuilderFactory.get(JOB_NAME)
                .preventRestart()
                .start(processorCompositeBatchStep())
                .build();
    }

    @Bean(BEAN_PREFIX + "step")
    @JobScope
    public Step processorCompositeBatchStep() {
        return stepBuilderFactory.get(BEAN_PREFIX + "step")
                .<Teacher, String>chunk(chunkSize)
                .reader(processorCompositeBatchReader())
                .processor(compositeProcessor())
                .writer(processorCompositeBatchWriter())
                .build();
    }

    @Bean
    public JpaPagingItemReader<Teacher> processorCompositeBatchReader() {
        return new JpaPagingItemReaderBuilder<Teacher>()
                .name(BEAN_PREFIX + "reader")
                .entityManagerFactory(entityManagerFactory)
                .pageSize(chunkSize)
                .queryString("SELECT t FROM Teacher t")
                .build();
    }

    /**
     * @CompositeItemProcessor: ItemProcessor간의 체이닝을 지원하는 Processor
     */
    @Bean
    public CompositeItemProcessor compositeProcessor() {
        List<ItemProcessor> delegates = new ArrayList<>(2);
        delegates.add(proc1());
        delegates.add(proc2());

        CompositeItemProcessor processor = new CompositeItemProcessor<>();
        processor.setDelegates(delegates);

        return processor;
    }

    public ItemProcessor<Teacher, String> proc1() {
        return Teacher::getName;
    }

    public ItemProcessor<String, String> proc2() {
        return name -> "안녕하새요. " + name + "입니다 .";
    }

    private ItemWriter<String> processorCompositeBatchWriter() {
        return items -> {
            for (String item : items) {
                log.info("Teacher name = {}", item);
            }  
        };
    }
}
