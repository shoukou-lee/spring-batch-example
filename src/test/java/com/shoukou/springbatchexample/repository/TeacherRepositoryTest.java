package com.shoukou.springbatchexample.repository;

import com.shoukou.springbatchexample.model.Student;
import com.shoukou.springbatchexample.model.Teacher;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

/**
 * 일단 MYSQL로 테이블에 저장된 값으로 해보자
 */
// @ExtendWith(MockitoExtension.class)
// @ActiveProfiles("test-h2")
@SpringBootTest
public class TeacherRepositoryTest {
    @Autowired
    private TeacherRepository teacherRepository;
    
    @BeforeEach
    void init() {
        
    }
    
    @AfterEach
    void tearDown() {
        
    }
    
    @Test
    void findAllTeacher_일단_저장된값으로 () {
        // given

        // when
        List<Teacher> teachers = teacherRepository.findAllTeachers();

        // then
        // 1~15 출력되야 댐
        for (Teacher teacher : teachers) {
            System.out.println("teacher.getId() = " + teacher.getId());
        }
    }

    @Test
    void findTeacherByStudentId_일단_저장된값으로() {
        // given

        // when
        Teacher teacher = teacherRepository.findTeacherByStudentId(2L)
                .orElseThrow(() -> new RuntimeException("student ID로 못찾는 경우"));

        // then
        System.out.println("teacher.getId() = " + teacher.getId());
        System.out.println("teacher.getName() = " + teacher.getName());

        // fetchjoin 까먹으면 여기 실행안댐
        List<Student> hisStudents = teacher.getStudents();
        for (Student hisStudent : hisStudents) {
            System.out.println("hisStudent.getName() = " + hisStudent.getName());
        }
    }
    
}
