package com.resumeanalyzer.dto;

import java.time.Instant; // Use Instant for date

/**
 * DTO for the response after a resume upload.
 */
public class ResumeUploadResponse {
    private String id;
    private String message;
    private String uploadDate; // Changed back to Instant

    public ResumeUploadResponse() {
    }

    public ResumeUploadResponse(String id, String message, String uploadDate) {
        this.id = id;
        this.message = message;
        this.uploadDate = uploadDate;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUploadDate() {
        return uploadDate;
    }

    public void setUploadDate(String uploadDate) {
        this.uploadDate = uploadDate;
    }
}