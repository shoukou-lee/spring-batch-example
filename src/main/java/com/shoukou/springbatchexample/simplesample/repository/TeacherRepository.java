package com.shoukou.springbatchexample.simplesample.repository;

import com.shoukou.springbatchexample.simplesample.model.Teacher;
import com.shoukou.springbatchexample.simplesample.repository.custom.TeacherRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeacherRepository extends JpaRepository<Teacher, Long>, TeacherRepositoryCustom {
}
