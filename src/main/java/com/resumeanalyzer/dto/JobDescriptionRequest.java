package com.resumeanalyzer.dto;

/**
 * DTO for creating and updating job descriptions.
 * Contains fields for job title, description, skills, and experience level.
 */
public class JobDescriptionRequest {
    private String title;
    private String description;
    private String requiredSkills;  // Comma-separated string of skills
    private String preferredSkills; // Comma-separated string of skills
    private String experienceLevel;

    // Constructors
    public JobDescriptionRequest() {
    }

    public JobDescriptionRequest(String title, String description, String requiredSkills, String preferredSkills, String experienceLevel) {
        this.title = title;
        this.description = description;
        this.requiredSkills = requiredSkills;
        this.preferredSkills = preferredSkills;
        this.experienceLevel = experienceLevel;
    }

    // Getters and Setters
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRequiredSkills() {
        return requiredSkills;
    }

    public void setRequiredSkills(String requiredSkills) {
        this.requiredSkills = requiredSkills;
    }

    public String getPreferredSkills() {
        return preferredSkills;
    }

    public void setPreferredSkills(String preferredSkills) {
        this.preferredSkills = preferredSkills;
    }

    public String getExperienceLevel() {
        return experienceLevel;
    }

    public void setExperienceLevel(String experienceLevel) {
        this.experienceLevel = experienceLevel;
    }
}