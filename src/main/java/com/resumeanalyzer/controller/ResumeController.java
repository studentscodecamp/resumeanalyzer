// src/main/java/com/resumeanalyzer/controller/ResumeController.java
package com.resumeanalyzer.controller;

import com.resumeanalyzer.dto.ResumeUploadResponse;
import com.resumeanalyzer.model.Resume;
import com.resumeanalyzer.service.ResumeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Instant; // Ensure Instant is used for dates

/**
 * REST Controller for handling resume related requests (upload, get, delete).
 */
@RestController
@RequestMapping("/api/resumes")
@CrossOrigin(origins = "http://localhost:4200") // Allow Angular frontend access
public class ResumeController {

    private final ResumeService resumeService;

    @Autowired
    public ResumeController(ResumeService resumeService) {
        this.resumeService = resumeService;
    }

    /**
     * Handles resume file uploads.
     * @param file The resume file to upload.
     * @return ResponseEntity with ResumeUploadResponse.
     */
    @PostMapping("/upload")
    public ResponseEntity<ResumeUploadResponse> uploadResume(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(new ResumeUploadResponse(null, "No file provided", null));
        }
        try {
            // Corrected method call: Use 'saveResume' instead of 'uploadAndProcessResume'
            Resume uploadedResume = resumeService.saveResume(file);
            return ResponseEntity.ok(new ResumeUploadResponse(uploadedResume.getId(), "Resume uploaded successfully!", uploadedResume.getUploadDate()));
        } catch (IOException e) {
            return ResponseEntity.status(500).body(new ResumeUploadResponse(null, "Failed to upload resume: " + e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new ResumeUploadResponse(null, "An unexpected error occurred during upload: " + e.getMessage(), null));
        }
    }

    /**
     * Retrieves a resume by its ID.
     * @param id The ID of the resume.
     * @return ResponseEntity with Resume details.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Resume> getResume(@PathVariable String id) {
        return resumeService.getResume(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Deletes a resume by its ID.
     * @param id The ID of the resume.
     * @return ResponseEntity indicating success or failure.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteResume(@PathVariable String id) {
        resumeService.deleteResume(id);
        return ResponseEntity.noContent().build();
    }
}
