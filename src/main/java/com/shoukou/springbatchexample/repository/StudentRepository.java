package com.shoukou.springbatchexample.repository;

import com.shoukou.springbatchexample.model.Student;
import com.shoukou.springbatchexample.repository.custom.StudentRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentRepository extends JpaRepository<Student, Long>, StudentRepositoryCustom {
}
