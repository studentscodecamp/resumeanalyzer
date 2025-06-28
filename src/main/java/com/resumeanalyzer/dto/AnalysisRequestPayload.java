package com.resumeanalyzer.dto;

/**
 * DTO for the request payload when analyzing a resume against a job description.
 */
public class AnalysisRequestPayload {
    private String resumeId;
    private String jobDescriptionId;

    public AnalysisRequestPayload() {
    }

    public AnalysisRequestPayload(String resumeId, String jobDescriptionId) {
        this.resumeId = resumeId;
        this.jobDescriptionId = jobDescriptionId;
    }

    // Getters and Setters
    public String getResumeId() {
        return resumeId;
    }

    public void setResumeId(String resumeId) {
        this.resumeId = resumeId;
    }

    public String getJobDescriptionId() {
        return jobDescriptionId;
    }

    public void setJobDescriptionId(String jobDescriptionId) {
        this.jobDescriptionId = jobDescriptionId;
    }
}