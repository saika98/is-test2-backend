package com.isapp_test.is_backend_test.controller;


import com.isapp_test.is_backend_test.service.ResumeStorageService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/resumes")
@RequiredArgsConstructor
public class ResumeController {
  private final ResumeStorageService storage;

  @Data public static class FileSpec { String name; String contentType; }
  @Data public static class InitRequest {
    private UUID candidateId;
    private String uploadedBy;
    private List<FileSpec> files;
  }
  @Data public static class CompleteRequest { private List<UUID> resumeIds; }

  @PostMapping("/init-uploads")
  public List<Map<String, Object>> initUploads(@RequestBody InitRequest req) {
    List<Map<String, Object>> out = new ArrayList<>();
    for (FileSpec f : req.getFiles()) {
      var u = storage.initUpload(req.getCandidateId(), f.getName(), f.getContentType(), req.getUploadedBy());
      out.add(Map.of("resumeId", u.resumeId(), "s3Key", u.s3Key(), "uploadUrl", u.uploadUrl().toString()));
    }

    return out;
  }

  @PostMapping("/complete")
  public void complete(@RequestBody CompleteRequest req) {
    for (UUID id : req.getResumeIds()) storage.completeUpload(id);
  }

  @GetMapping("/{resumeId}/download-url")
  public Map<String, String> download(@PathVariable UUID resumeId) {
    return Map.of("url", storage.presignDownload(resumeId).toString());
  }

  @DeleteMapping("/{resumeId}")
  public void delete(@PathVariable UUID resumeId) {
    storage.delete(resumeId);
  }
}
