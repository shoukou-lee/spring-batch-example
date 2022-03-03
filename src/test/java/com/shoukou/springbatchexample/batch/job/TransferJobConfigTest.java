package com.shoukou.springbatchexample.batch.job;

import com.shoukou.springbatchexample.TestBatchConfig;
import com.shoukou.springbatchexample.config.QueryDslConfig;
import com.shoukou.springbatchexample.repository.DormantUserRepository;
import com.shoukou.springbatchexample.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = {TransferJobConfig.class, TestBatchConfig.class, QueryDslConfig.class})
@SpringBatchTest
public class TransferJobConfigTest {
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
    void 테스트() throws Exception {
        // given

        // when

        // then
    }
}
