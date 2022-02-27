package com.shoukou.springbatchexample.repository;

import com.shoukou.springbatchexample.model.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeacherRepository extends JpaRepository<Teacher, Long> {
}
