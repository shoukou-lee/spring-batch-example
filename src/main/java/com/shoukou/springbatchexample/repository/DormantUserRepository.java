package com.shoukou.springbatchexample.repository;

import com.shoukou.springbatchexample.model.DormantUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DormantUserRepository extends JpaRepository<DormantUser, Long> {
}
