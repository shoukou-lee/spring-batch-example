package com.shoukou.springbatchexample.repository;

import com.shoukou.springbatchexample.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentRepository extends JpaRepository<Student, Long> {
}
