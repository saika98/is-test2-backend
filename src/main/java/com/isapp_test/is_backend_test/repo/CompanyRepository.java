package com.isapp_test.is_backend_test.repo;

import com.isapp_test.is_backend_test.entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompanyRepository extends JpaRepository<Company, Integer> {}
