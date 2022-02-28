package com.shoukou.springbatchexample.batch.job;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/*
앞선 step에서 오류가 났을 때, 이후 step이 멈추지 말고 조건에 따른 분기를 수행하도록 구현
 */
@Slf4j
@RequiredArgsConstructor
@Configuration
public class StepNextConditionalJobConfig {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    // Program arguments: --job.name=stepNextConditionalJob
    @Bean
    public Job stepNextConditionalJob() {
        return jobBuilderFactory.get("stepNextConditionalJob")
                .start(conditionalJobStep1())
                    .on("FAILED") // Catch할 ExitStatus를 FAILED로 지정
                    .to(conditionalJobStep3()) // FAILED가 캐치되면 다음 이동할 Step을 Step3으로 지정
                    .on("*") // Step3의 결과와 관계 없이
                    .end() // flow 종료 - flowbuilder를 반환하며, 반환 성공하면 이후 from을 이어갈 수 있음
                .from(conditionalJobStep1()) // 이벤트 리스너: 상태값을 보고 일치한다면 to의 step 호출
                    .on("*") // FAILED 아닌 모든 경우를 캐치하면
                    .to(conditionalJobStep2()) // Step2로 이동
                    .next(conditionalJobStep3()) // Step2가 정상 종료되면 Step3으로
                    .on("*") // Step3의 결과와 관계 없이
                    .end() //flow 종료
                .end() // Job 종료
                .build();
    }

    @Bean
    public Step conditionalJobStep1() {
        return stepBuilderFactory.get("step1")
                .tasklet(
                        (contribution, chunkContext) -> {
                            log.info(">>>>> This is stepNextConditionalJob Step1");

                            /**
                             * ExitStatus : Step의 종료 후 상태값
                             * FAILED로 지정했으므로, Job의 flow는 Step1 -> Step3을 기대함
                             */
                            // Step이 가질 역할 이외의 책임을 떠앉는 코드 - 왜 ExitStatus를 Step이 조작해야 하는가?
                            contribution.setExitStatus(ExitStatus.FAILED);
                            return RepeatStatus.FINISHED;
                        }
                ).build();
    }

    @Bean
    public Step conditionalJobStep2() {
        return stepBuilderFactory.get("step2")
                .tasklet(
                        (contribution, chunkContext) -> {
                            log.info(">>>>> This is stepNextConditionalJob Step2");
                            return RepeatStatus.FINISHED;
                        }
                ).build();
    }

    @Bean
    public Step conditionalJobStep3() {
        return stepBuilderFactory.get("step3")
                .tasklet(
                        (contribution, chunkContext) -> {
                            log.info(">>>>> This is stepNextConditionalJob Step3");
                            return RepeatStatus.FINISHED;
                        }
                ).build();
    }
}
