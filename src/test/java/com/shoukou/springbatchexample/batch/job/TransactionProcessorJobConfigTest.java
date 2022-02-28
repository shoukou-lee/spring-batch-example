package com.shoukou.springbatchexample.batch.job;

import com.shoukou.springbatchexample.TestBatchConfig;
import com.shoukou.springbatchexample.model.Student;
import com.shoukou.springbatchexample.model.Teacher;
import com.shoukou.springbatchexample.repository.TeacherRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBatchTest
@SpringBootTest(classes = {TransactionProcessorJobConfig.class, TestBatchConfig.class})
@ActiveProfiles("test-h2")
public class TransactionProcessorJobConfigTest {

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Autowired
    private TeacherRepository teacherRepository;

    @BeforeEach
    void init() {

    }

    @AfterEach
    void tearDown() {
        teacherRepository.deleteAllInBatch();
    }

    @Test
    void transactionProcessorBatchJob_성공() throws Exception {
        // given
        for(long i = 0; i < 10; i++) {
            String teacherName = i + "선생님";
            Teacher teacher = Teacher.builder()
                    .name(teacherName)
                    .subject("수학")
                    .build();
            Student student = Student.builder()
                    .name(teacherName + "의 제자")
                    .build();
            teacher.addStudent(student);
            teacherRepository.save(teacher);
        }

        JobParametersBuilder builder = new JobParametersBuilder();
        builder.addString("version", "1");

        //when
        JobExecution jobExecution = jobLauncherTestUtils.launchJob(builder.toJobParameters());

        //then
        assertThat(jobExecution.getStatus()).isEqualTo(BatchStatus.COMPLETED);
    }

}
