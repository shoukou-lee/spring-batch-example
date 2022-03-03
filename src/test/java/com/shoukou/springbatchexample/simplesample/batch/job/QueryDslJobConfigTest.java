package com.shoukou.springbatchexample.simplesample.batch.job;

import com.shoukou.springbatchexample.TestBatchConfig;
import com.shoukou.springbatchexample.simplesample.batch.job.QueryDslJobConfig;
import com.shoukou.springbatchexample.config.QueryDslConfig;
import com.shoukou.springbatchexample.model.DormantUser;
import com.shoukou.springbatchexample.model.User;
import com.shoukou.springbatchexample.repository.DormantUserRepository;
import com.shoukou.springbatchexample.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest(classes = {QueryDslJobConfig.class, TestBatchConfig.class, QueryDslConfig.class})
@SpringBatchTest
public class QueryDslJobConfigTest {
    public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DormantUserRepository dormantUserRepository;

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @AfterEach
    public void tearDown() {
        userRepository.deleteAllInBatch();
        dormantUserRepository.deleteAllInBatch();
    }

    @Test
    public void DormanUser_이관() throws  Exception {
        // given
        LocalDate lastAccess = LocalDate.of(2022, 06, 28);
        String name = "이름";
        User user = User.builder()
                .name(name)
                .lastAccess(lastAccess)
                .build();

        userRepository.save(user);

        JobParameters jobParameters = new JobParametersBuilder()
                .addString("lastAccess", lastAccess.format(FORMATTER))
                .toJobParameters();

        // when
        JobExecution jobExecution = jobLauncherTestUtils.launchJob(jobParameters);

        // then
        List<DormantUser> dormants = dormantUserRepository.findAll();
        assertThat(jobExecution.getStatus()).isEqualTo(BatchStatus.COMPLETED);
        assertThat(dormants.size()).isEqualTo(1);
    }
}
