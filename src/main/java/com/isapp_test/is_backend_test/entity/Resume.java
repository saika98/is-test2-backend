package com.isapp_test.is_backend_test.entity;

import jakarta.persistence.*;
import lombok.Getter; import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Entity @Table(name = "resumes")
@Getter @Setter
public class Resume {
  @Id @Column(name="resume_id")
  private UUID resumeId;

  @Column(nullable=false)
  private UUID candidateId;

  @Column(name = "s3_key", nullable=false)
  private String s3Key;

  @Column(name = "original_name", nullable=false)
  private String originalName;

  @Column(name = "content_type")
  private String contentType;

  @Column(name = "file_size_bytes")
  private Long fileSizeBytes;

  @Column(name = "etag")
  private String etag;

  @Column(name = "status", nullable=false)
  private String status; // PENDING/COMPLETED/DELETED
  @Column(name = "uploaded_by")
  private String uploadedBy;

  @Column(name = "created_at", nullable=false)
  private Instant createdAt;

  @Column(name = "updated_at", nullable=false)
  private Instant updatedAt;

  @PrePersist
  public void prePersist() {
    if (resumeId == null) resumeId = UUID.randomUUID();
    if (status == null) status = "PENDING";
    Instant now = Instant.now();
    createdAt = now; updatedAt = now;
  }
  @PreUpdate public void preUpdate() { updatedAt = Instant.now(); }
}
