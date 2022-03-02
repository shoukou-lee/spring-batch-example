package com.shoukou.springbatchexample.simplesample.repository.custom;

import com.shoukou.springbatchexample.simplesample.model.Teacher;

import java.util.List;
import java.util.Optional;

public interface TeacherRepositoryCustom {
    List<Teacher> findAllTeachersWithStudents();
    List<Teacher> findTeachersByName(String name);
    List<Teacher> findTeachersBySubject(String subject);
    Optional<Teacher> findTeacherByStudentId(Long studentId);
}
