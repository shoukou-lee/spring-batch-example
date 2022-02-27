package com.shoukou.springbatchexample.repository;

import com.shoukou.springbatchexample.model.Teacher;
import com.shoukou.springbatchexample.repository.custom.TeacherRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeacherRepository extends JpaRepository<Teacher, Long>, TeacherRepositoryCustom {
}
