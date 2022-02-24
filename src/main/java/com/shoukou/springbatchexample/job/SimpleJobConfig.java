package com.shoukou.springbatchexample.job;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class SimpleJobConfig {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job simpleJob() {
        return jobBuilderFactory.get("simpleJob") // job 이름 설정
                .start(simpleStep1(null))
                .next(simpleStep2(null))
                .build();
    }

    /*
    동일한 job에 대해 다른 job parameter가 있으면, BATCH_JOB_INSTANCE에 job 인스턴스 데이터가 저장됨
    또한 동일한 job parameter로 job을 실행할 수 없음
    (A job instance already exists and is complete for parameters)
     */
    @Bean
    @JobScope
    public Step simpleStep1(@Value("#{jobParameters[requestDate]}") String requestDate) {
        return stepBuilderFactory.get("simpleStep1") // step 이름 설정
                .tasklet(
                        (contribution, chunkContext) -> {
                            /*
                            * BATCH_JOB_EXECUTION 테이블은 JOB_INSTANCE의 자식이며,
                            * JOB_INSTANCE의 성공/실패 내역을 포함
                            * 아래 throw가 실행되면 실패된 내역을 테이블에서 확인 가능능                            * */
                            // throw new IllegalArgumentException("step1 실패");

                            log.info(">>>>> This is Step 1");
                            log.info(">>>>> requestDate = {}", requestDate);
                            return RepeatStatus.FINISHED;
                        }
                ).build();
    }

    @Bean
    @JobScope
    public Step simpleStep2(@Value("#{jobParameters[requestDate]}") String requestDate) {
        return stepBuilderFactory.get("simpleStep2") // step 이름 설정
                .tasklet(
                        (contribution, chunkContext) -> {
                            log.info(">>>>> This is Step 2");
                            log.info(">>>>> requestDate = {}", requestDate);
                            return RepeatStatus.FINISHED;
                        } // step 내부에서 수행될 기능(tasklet) 명시
                ).build();
    }
}
