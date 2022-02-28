package com.shoukou.springbatchexample.repository;

import com.shoukou.springbatchexample.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
