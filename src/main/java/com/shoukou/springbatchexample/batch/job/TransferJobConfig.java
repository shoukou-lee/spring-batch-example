package com.shoukou.springbatchexample.batch.job;

import com.shoukou.springbatchexample.batch.tasklet.DormantInsertItemWriter;
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
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.builder.JpaItemWriterBuilder;
import org.springframework.batch.item.support.CompositeItemWriter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManagerFactory;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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
    public TransferJobParameter jobParameter(@Value("#{jobParameters[currentDate]}") String currentDate) {
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
                .<User, User>chunk(chunkSize)
                .reader(transferReader())
                .processor(transferProcessor())
                .writer(composite())
                .build();
    }

    /**
     * item reader: lastAccess가 1년 전보다 오래된 User들을 Page 단위로 찾는다.
     * reader에서 LocalDate.now()를 호출하는 것보다 job parameter로 현재 시각을 넘겨받자
     * @jojoldu@tistory.com/451
     */

    @Bean
    public QueryDslPagingItemReader<User> transferReader() {
        // 마지막 로그인이 335일 전인 유저들 리스트를 조회 (이관 전 notice mail 수신 대상)
        return new QueryDslPagingItemReader<>(entityManagerFactory, chunkSize,
                jpaQueryFactory -> jpaQueryFactory
                        .selectFrom(user)
                        .where(user.lastAccess.before(LocalDate.from(jobParameter.getCurrentDate().minusYears(1))),
                                user.isDormant.eq(false))
        );
    }

    // item processor: User 필드를 복사한 Dormant user 생성
    private ItemProcessor<User, User> transferProcessor() {

        return (User u) -> {
            u.setIsDormant(true);
            log.info("u.getIsDormant() : {}", u.getIsDormant());
            return u;
        };
    }

    /**
     * User update query를 위한 JpaItemWriter와
     * Dormant insert query를 위한 Custom ItemWriter를 composite하는 ItemWriter를 만든다.
     *
     * User.isDormant=true와, 이 유저들에 대한 새로운 DormantUser 객체를 DB에 저장하고 싶은데,
     * JpaItemWriter의 EntityManagerFactory가 private이라서 상속-확장이 어렵다 ..
     * 우선은 JpaItemWriter를 Copy-and-paste 하고, doWrite() 메서드 일부만 커스터마이즈 해서 해결해봤다 ..
     */
    @Bean
    public CompositeItemWriter composite() {
        CompositeItemWriter compositeJpaItemWriter = new CompositeItemWriter();

        List<ItemWriter> writers = new ArrayList<>();
        writers.add(dormantWriter());
        writers.add(userWriter());

        compositeJpaItemWriter.setDelegates(writers);

        return compositeJpaItemWriter;
    }

    @Bean
    public JpaItemWriter<User> userWriter() {
        return new JpaItemWriterBuilder<User>()
                .entityManagerFactory(entityManagerFactory)
                .build();
    }

    @Bean
    public DormantInsertItemWriter dormantWriter() {
        DormantInsertItemWriter writer = new DormantInsertItemWriter();
        writer.setEntityManagerFactory(entityManagerFactory);

        return writer;
    }
}
