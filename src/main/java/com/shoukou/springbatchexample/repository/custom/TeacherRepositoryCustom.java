package com.shoukou.springbatchexample.repository.custom;

import com.shoukou.springbatchexample.model.Student;
import com.shoukou.springbatchexample.model.Teacher;

import java.util.List;
import java.util.Optional;

public interface TeacherRepositoryCustom {
    List<Teacher> findAllTeachersWithStudents();
    List<Teacher> findTeachersByName(String name);
    List<Teacher> findTeachersBySubject(String subject);
    Optional<Teacher> findTeacherByStudentId(Long studentId);
}
