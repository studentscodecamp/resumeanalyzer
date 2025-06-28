package com.resumeanalyzer.dto;

import java.util.List;

/**
 * Data Transfer Object (DTO) for resume analysis responses.
 * This object will be sent back to the Angular frontend containing the results
 * of the resume analysis, including the match score and extracted skills.
 */
public class AnalysisResponse {
    private double matchScore;             // The calculated compatibility score (e.g., 0.0-1.0)
    private String message;                // A descriptive message about the analysis result
    private String strengths;              // Strengths identified
    private String weaknesses;             // Weaknesses identified
    private String recommendations;        // Recommendations for improvement
    private List<String> resumeSkills;     // List of skills extracted from the resume
    private List<String> jobDescriptionSkills; // List of skills extracted from the job description

    // Default constructor
    public AnalysisResponse() {
    }

    // Parameterized constructor for analysis results (without detailed breakdown)
    // Note: The 'message' field is included here for basic communication.
    public AnalysisResponse(double matchScore, String message, List<String> resumeSkills, List<String> jobDescriptionSkills) {
        this.matchScore = matchScore;
        this.message = message;
        this.resumeSkills = resumeSkills;
        this.jobDescriptionSkills = jobDescriptionSkills;
    }

    // Full parameterized constructor for all fields
    public AnalysisResponse(double matchScore, String message, String strengths, String weaknesses, String recommendations, List<String> resumeSkills, List<String> jobDescriptionSkills) {
        this.matchScore = matchScore;
        this.message = message;
        this.strengths = strengths;
        this.weaknesses = weaknesses;
        this.recommendations = recommendations;
        this.resumeSkills = resumeSkills;
        this.jobDescriptionSkills = jobDescriptionSkills;
    }

    // Getters and Setters for all fields
    public double getMatchScore() {
        return matchScore;
    }

    public void setMatchScore(double matchScore) {
        this.matchScore = matchScore;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
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

    public List<String> getResumeSkills() {
        return resumeSkills;
    }

    public void setResumeSkills(List<String> resumeSkills) {
        this.resumeSkills = resumeSkills;
    }

    public List<String> getJobDescriptionSkills() {
        return jobDescriptionSkills;
    }

    public void setJobDescriptionSkills(List<String> jobDescriptionSkills) {
        this.jobDescriptionSkills = jobDescriptionSkills;
    }
}