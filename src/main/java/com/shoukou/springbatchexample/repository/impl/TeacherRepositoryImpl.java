package com.shoukou.springbatchexample.repository.impl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.shoukou.springbatchexample.model.QStudent;
import com.shoukou.springbatchexample.model.QTeacher;
import com.shoukou.springbatchexample.model.Student;
import com.shoukou.springbatchexample.model.Teacher;
import com.shoukou.springbatchexample.repository.custom.TeacherRepositoryCustom;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;

import static com.shoukou.springbatchexample.model.QStudent.*;
import static com.shoukou.springbatchexample.model.QTeacher.*;

@RequiredArgsConstructor
public class TeacherRepositoryImpl implements TeacherRepositoryCustom {
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<Teacher> findAllTeachers() {
        return jpaQueryFactory.selectFrom(teacher)
                .fetch();
    }

    @Override
    public List<Teacher> findTeachersByName(String name) {
        return jpaQueryFactory.selectFrom(teacher)
                        .where(teacher.name.eq(name))
                        .fetch();
    }

    @Override
    public List<Teacher> findTeachersBySubject(String subject) {
        return jpaQueryFactory.selectFrom(teacher)
                .where(teacher.subject.eq(subject))
                .fetch();
    }

    @Override
    public Optional<Teacher> findTeacherByStudentId(Long studentId) {
        return Optional.ofNullable(
                jpaQueryFactory.selectFrom(teacher)
                        .join(teacher.students, student)
                        .fetchJoin()
                        .where(student.id.eq(studentId))
                        .fetch()
                        .get(0)
        );
    }
}
