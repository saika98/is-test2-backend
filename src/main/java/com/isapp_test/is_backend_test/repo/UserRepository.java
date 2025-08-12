package com.isapp_test.is_backend_test.repo;

import com.isapp_test.is_backend_test.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer> {}
