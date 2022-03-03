package com.shoukou.springbatchexample.batch.job;

import com.shoukou.springbatchexample.model.DormantUser;
import com.shoukou.springbatchexample.model.User;
import com.shoukou.springbatchexample.simplesample.batch.reader.QueryDslPagingItemReader;
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

import static com.shoukou.springbatchexample.model.QUser.user;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class TransferJobConfig {
    public static final String JOB_NAME = "transferDormantUserJob";

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final EntityManagerFactory entityManagerFactory;
    private final TransferJobParameter jobParameter;

    private int chunkSize;

    @Value("${chunkSize:1000}")
    public void setChunkSize(int chunkSize) {
        this.chunkSize = chunkSize;
    }

    // job parameter로는 현재 시각을 넘겨받는다고 가정
    @Bean(JOB_NAME + "_parameter")
    @JobScope
    public TransferJobParameter jobParameter(@Value("#{jobParameters[currentDate]}")
                                             String currentDate) {
        return new TransferJobParameter(currentDate);
    }

    // job
    @Bean
    public Job transferJob() {
        return jobBuilderFactory.get(JOB_NAME)
                .start(transferStep())
                .build();
    }

    // step
    @Bean
    public Step transferStep() {
        return stepBuilderFactory.get("transferStep")
                .<User, DormantUser>chunk(chunkSize)
                .reader(transferReader())
                .processor(transferProcessor())
                .writer(transferWriter())
                .build();
    }

    /**
     * item reader: lastAccess가 1년 전보다 오래된 User들을 Page 단위로 찾는다.
     * reader에서 LocalDate.now()를 호출하는 것보다 job parameter로 현재 시각을 넘겨받자
     * @jojoldu@tistory.com/451
     */

    @Bean
    public QueryDslPagingItemReader<User> transferReader() {
        // 마지막 로그인이 1년보다 이전인 user 조회
        return new QueryDslPagingItemReader<>(entityManagerFactory, chunkSize,
                jpaQueryFactory -> jpaQueryFactory
                        .selectFrom(user)
                        .where(user.lastAccess.before(jobParameter.getCurrentDate().minusYears(1)))

                        // 아래처럼 쓰면 안되는가보다
                        // .where(user.lastAccess.before(jobParameter.getCurrentDate().minusYears(1)).eq(true))
        );
    }

    // item processor: User 필드를 복사한 Dormant user 생성
    private ItemProcessor<User, DormantUser> transferProcessor() {
        return DormantUser::new;
    }

    // item writer: EntityManager를 할당하고 dormant_user 테이블에 이 유저들을 insert
    // NOTE: JpaItemWriter는 EMF만 설정해주면, 엔티티를 알아서 entityManager.merge()로 테이블에 반영해줌
    @Bean
    public JpaItemWriter<DormantUser> transferWriter() {
        return new JpaItemWriterBuilder<DormantUser>()
                .entityManagerFactory(entityManagerFactory)
                .build();
    }
}
