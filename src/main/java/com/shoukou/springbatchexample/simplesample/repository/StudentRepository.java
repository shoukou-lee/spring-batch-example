package com.shoukou.springbatchexample.simplesample.repository;

import com.shoukou.springbatchexample.simplesample.model.Student;
import com.shoukou.springbatchexample.simplesample.repository.custom.StudentRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentRepository extends JpaRepository<Student, Long>, StudentRepositoryCustom {
}
