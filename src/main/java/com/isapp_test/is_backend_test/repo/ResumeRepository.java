package com.isapp_test.is_backend_test.repo;

import com.isapp_test.is_backend_test.entity.Resume;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

// public interface ResumeRepository extends JpaRepository<Resume, UUID> {}

public interface ResumeRepository extends JpaRepository<Resume, UUID> {
    List<Resume> findByCandidateIdAndStatusNotOrderByCreatedAtDesc(UUID candidateId, String status);
}