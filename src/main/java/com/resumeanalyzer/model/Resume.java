package com.resumeanalyzer.model;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.Instant;

/**
 * Model representing an uploaded resume. This entity will be stored in Firestore.
 * It holds the extracted text content of the resume and metadata like upload date.
 */
public class Resume {
    private String id; // Unique ID for the resume (Firestore document ID)
    private String content; // Extracted text content of the resume
    private String fileName; // Original file name of the uploaded resume
    private String contentType; // MIME type of the uploaded file
    private long fileSize; // Size of the uploaded file in bytes
    private String uploadDate; // Timestamp of when the resume was uploaded

    // Constructors
    public Resume() {
        // Default constructor needed for Firestore deserialization
    }

    public Resume(String id, String content, String fileName, String contentType, long fileSize, String uploadDate) {
        this.id = id;
        this.content = content;
        this.fileName = fileName;
        this.contentType = contentType;
        this.fileSize = fileSize;
        this.uploadDate = uploadDate;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public String getUploadDate() {
        return uploadDate;
    }

    public void setUploadDate(String uploadDate) {
        this.uploadDate = uploadDate;
    }
}