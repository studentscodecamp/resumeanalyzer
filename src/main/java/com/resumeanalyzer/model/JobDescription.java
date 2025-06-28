package com.resumeanalyzer.model;

/**
 * Model representing a job description. This entity will be stored in Firestore.
 * It contains all the details of a job posting against which resumes will be analyzed.
 */
public class JobDescription {
    private String id; // Unique ID for the job description (Firestore document ID)
    private String title;
    private String description;
    private String requiredSkills;  // Stored as a comma-separated string for simplicity
    private String preferredSkills; // Stored as a comma-separated string for simplicity
    private String experienceLevel;

    // Constructors
    public JobDescription() {
        // Default constructor needed for Firestore deserialization
    }

    public JobDescription(String id, String title, String description, String requiredSkills, String preferredSkills, String experienceLevel) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.requiredSkills = requiredSkills;
        this.preferredSkills = preferredSkills;
        this.experienceLevel = experienceLevel;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

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