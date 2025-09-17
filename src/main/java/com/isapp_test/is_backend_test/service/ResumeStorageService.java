package com.isapp_test.is_backend_test.service;

import com.isapp_test.is_backend_test.entity.Resume;
import com.isapp_test.is_backend_test.repo.ResumeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;

import java.net.URI;
import java.time.Duration;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ResumeStorageService {
  private final S3Client s3;
  private final S3Presigner presigner;
  private final ResumeRepository repo;

  @Value("${app.s3.bucket}") private String bucket;
  @Value("${app.s3.resumePrefix}") private String prefix;
  @Value("${presign.uploadExpireSeconds:600}") private int upExp;
  @Value("${presign.downloadExpireSeconds:600}") private int dlExp;

  public record UploadInit(UUID resumeId, String s3Key, URI uploadUrl) {}

  // 追加（一覧返却用の1行を表す）
  public record ResumeRow(
      UUID resumeId,
      String s3Key,
      String originalName,
      String status,
      Long fileSizeBytes,
      String etag,
      java.time.Instant updatedAt
  ) {}
  @Transactional
  public UploadInit initUpload(UUID candidateId, String originalName, String contentType, String uploadedBy) {
    String ext = Optional.ofNullable(originalName)
        .filter(n -> n.contains("."))
        .map(n -> n.substring(n.lastIndexOf('.')+1))
        .orElse("bin");
    String key = "%s/%s.%s".formatted(prefix, UUID.randomUUID(), ext);

    // プリサインPUT
    PutObjectRequest put = PutObjectRequest.builder()
        .bucket(bucket).key(key)
        .contentType(Optional.ofNullable(contentType).orElse("application/octet-stream"))
        .build();
    PresignedPutObjectRequest pre = presigner.presignPutObject(b -> b
        .signatureDuration(Duration.ofSeconds(upExp))
        .putObjectRequest(put));
    
    // DBにPENDINGで保存
    Resume r = new Resume();
    r.setCandidateId(candidateId);
    r.setS3Key(key);
    r.setOriginalName(originalName);
    r.setContentType(contentType);
    r.setUploadedBy(uploadedBy);
    r.setStatus("PENDING");
    repo.save(r);
    URI presignedUri = URI.create(pre.url().toString());
    return new UploadInit(r.getResumeId(), key, presignedUri);
    // return new UploadInit(r.getResumeId(), key, pre.url().toString());
  }

  @Transactional
  public void completeUpload(UUID resumeId) {
    Resume r = repo.findById(resumeId).orElseThrow();
    HeadObjectResponse head = s3.headObject(b -> b.bucket(bucket).key(r.getS3Key()));
    r.setFileSizeBytes(head.contentLength());
    r.setEtag(head.eTag());
    r.setStatus("COMPLETED");
    repo.save(r);
  }

  public URI presignDownload(UUID resumeId) {
    Resume r = repo.findById(resumeId).orElseThrow();
    GetObjectRequest get = GetObjectRequest.builder().bucket(bucket).key(r.getS3Key()).build();
    PresignedGetObjectRequest pre = presigner.presignGetObject(b -> b
        .signatureDuration(Duration.ofSeconds(dlExp))
        .getObjectRequest(get));
    return URI.create(pre.url().toString());
  }

  @Transactional
  public void delete(UUID resumeId) {
    Resume r = repo.findById(resumeId).orElseThrow();
    s3.deleteObject(b -> b.bucket(bucket).key(r.getS3Key()));
    r.setStatus("DELETED");
    repo.save(r);
  }

  // 一覧取得メソッド
  @Transactional(readOnly = true)
  public List<ResumeRow> listByCandidate(UUID candidateId, boolean withS3Head) {
    // DBからDELETED以外を新しい順で取得（Repositoryの追加メソッドを使用）
    var list = repo.findByCandidateIdAndStatusNotOrderByCreatedAtDesc(candidateId, "DELETED");

    // まずはDBの値で行DTOを作る
    List<ResumeRow> rows = list.stream()
        .map(r -> new ResumeRow(
            r.getResumeId(),
            r.getS3Key(),
            r.getOriginalName(),
            r.getStatus(),
            r.getFileSizeBytes(),
            r.getEtag(),
            r.getUpdatedAt()
        ))
        .toList();

    // 必要ならS3の最新メタ情報（サイズ/ETag）で上書き（COMPLETEDのみ）
    if (withS3Head) {
      rows = rows.stream().map(row -> {
        if ("COMPLETED".equalsIgnoreCase(row.status())) {
          var head = s3.headObject(b -> b.bucket(bucket).key(row.s3Key()));
          return new ResumeRow(
              row.resumeId(),
              row.s3Key(),
              row.originalName(),
              row.status(),
              head.contentLength(),
              head.eTag(),
              row.updatedAt()
          );
        }
        return row;
      }).toList();
    }

    return rows;
  }
}
