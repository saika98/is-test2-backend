package com.isapp_test.is_backend_test.repo;

import com.isapp_test.is_backend_test.entity.Resume;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ResumeRepository extends JpaRepository<Resume, UUID> {}
