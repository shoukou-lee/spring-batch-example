package com.shoukou.springbatchexample.repository;

import com.shoukou.springbatchexample.model.Student;
import com.shoukou.springbatchexample.model.Teacher;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
public class TeacherRepositoryTest {
    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private StudentRepository studentRepository;

    @BeforeEach
    void init() {

    }
    
    @AfterEach
    void tearDown() {
        studentRepository.deleteAll();
        teacherRepository.deleteAll();
    }

    @Test
    void findAllTeacher_성공 () {
        // given
        List<Teacher> teachers = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            Teacher t = Teacher.builder()
                    .name("Teacher" + Long.valueOf(i))
                    .subject("Subject" + Long.valueOf(i))
                    .build();
            teachers.add(t);
        }
        teacherRepository.saveAll(teachers);

        // when - 얘는 Student 정보도 바로 fetch join
        List<Teacher> result = teacherRepository.findAllTeachersWithStudents();

        // then
        for (int i = 0; i < result.size(); i++) {
            assertThat(result.get(i).getId()).isEqualTo(teachers.get(i).getId());
            assertThat(result.get(i).getName()).isEqualTo(teachers.get(i).getName());
            assertThat(result.get(i).getSubject()).isEqualTo(teachers.get(i).getSubject());
        }
    }

    @Test
    void findTeacherByStudentId_성공() {
        // given
        List<Teacher> teachers = new ArrayList<>();
        List<Student> students = new ArrayList<>();

        for (int i = 0; i < 4; i++) {
            Student s = Student.builder()
                    .name("Student" + Long.valueOf(i))
                    .build();
            students.add(s);
        }

        for (int i = 0; i < 2; i++) {
            Teacher t = Teacher.builder()
                    .name("Teacher" + Long.valueOf(i))
                    .subject("Subject" + Long.valueOf(i))
                    .build();
            teachers.add(t);
        }

        teachers.get(0).addStudent(students.get(0));
        teachers.get(0).addStudent(students.get(1));
        teachers.get(1).addStudent(students.get(2));
        teachers.get(1).addStudent(students.get(3));

        teacherRepository.saveAll(teachers);
        studentRepository.saveAll(students);

        List<Teacher> retTeachers = teacherRepository.findAllTeachersWithStudents();
        List<Student> retStudents = studentRepository.findAll();

        // when
        Teacher teacher0 = teacherRepository.findTeacherByStudentId(1L)
                .orElseThrow(() -> new RuntimeException("student ID로 못찾는 경우"));

        Teacher teacher1 = teacherRepository.findTeacherByStudentId(2L)
                .orElseThrow(() -> new RuntimeException("student ID로 못찾는 경우"));

        Teacher teacher2 = teacherRepository.findTeacherByStudentId(3L)
                .orElseThrow(() -> new RuntimeException("student ID로 못찾는 경우"));

        Teacher teacher3 = teacherRepository.findTeacherByStudentId(4L)
                .orElseThrow(() -> new RuntimeException("student ID로 못찾는 경우"));

        // then
        assertThat(teacher0.getId()).isEqualTo(teacher1.getId());
        assertThat(teacher2.getId()).isEqualTo(teacher3.getId());
    }

}
