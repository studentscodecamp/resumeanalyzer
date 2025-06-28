package com.resumeanalyzer.dto;

import java.time.Instant;

/**
 * DTO for presenting historical analysis results to the dashboard.
 * This closely matches the `recentAnalyses` structure in your Angular `DashboardComponent`.
 */
public class AnalysisResultResponse {
    private String id;
    private String jobTitle;
    private String company;
    private double matchScore;
    private String date; // Using String for date consistency with Java 8+

    // Constructors
    public AnalysisResultResponse() {
    }

    public AnalysisResultResponse(String id, String jobTitle, String company, double matchScore, String date) {
        this.id = id;
        this.jobTitle = jobTitle;
        this.company = company;
        this.matchScore = matchScore;
        this.date = date;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}