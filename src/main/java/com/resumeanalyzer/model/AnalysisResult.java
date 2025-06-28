package com.resumeanalyzer.model;

import java.time.Instant;

/**
 * Model representing a saved analysis result. This entity will be stored in Firestore.
 * It links a resume to a job description and stores the comprehensive analysis output
 * (score, strengths, weaknesses, recommendations).
 */
public class AnalysisResult {
    private String id; // Unique ID for the analysis result (Firestore document ID)
    private String resumeId; // ID of the resume analyzed
    private String jobDescriptionId; // ID of the job description used for analysis
    private String jobTitle; // Denormalized job title for easier dashboard display
    private String company; // Denormalized company name for easier dashboard display
    private double matchScore; // Calculated match percentage
    private String strengths; // AI-generated strengths
    private String weaknesses; // AI-generated weaknesses/areas for improvement
    private String recommendations; // AI-generated recommendations
    private String analysisDate; // Timestamp of when the analysis was performed
    private String jobId;

    // Constructors
    public AnalysisResult() {
        // Default constructor needed for Firestore deserialization
    }

    public AnalysisResult(String id, String resumeId, String jobDescriptionId, String jobTitle, String company, double matchScore, String strengths, String weaknesses, String recommendations, String analysisDate, String jobId) {
        this.id = id;
        this.resumeId = resumeId;
        this.jobDescriptionId = jobDescriptionId;
        this.jobTitle = jobTitle;
        this.company = company;
        this.matchScore = matchScore;
        this.strengths = strengths;
        this.weaknesses = weaknesses;
        this.recommendations = recommendations;
        this.analysisDate = analysisDate;
        this.jobId = jobId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

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

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public double getMatchScore() {
        return matchScore;
    }

    public void setMatchScore(double matchScore) {
        this.matchScore = matchScore;
    }

    public String getStrengths() {
        return strengths;
    }

    public void setStrengths(String strengths) {
        this.strengths = strengths;
    }

    public String getWeaknesses() {
        return weaknesses;
    }

    public void setWeaknesses(String weaknesses) {
        this.weaknesses = weaknesses;
    }

    public String getRecommendations() {
        return recommendations;
    }

    public void setRecommendations(String recommendations) {
        this.recommendations = recommendations;
    }

    public String getAnalysisDate() {
        return analysisDate;
    }

    public void setAnalysisDate(String analysisDate) {
        this.analysisDate = analysisDate;
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }
}