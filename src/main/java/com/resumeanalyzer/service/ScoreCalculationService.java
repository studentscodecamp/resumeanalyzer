package com.resumeanalyzer.service;

import java.util.List;

/**
 * Interface for the score calculation service.
 * Defines the contract for calculating the match score between resume and job description skills.
 */
public interface ScoreCalculationService {
    /**
     * Calculates the matching score between resume skills and job description skills.
     * The score typically represents how many of the job description's required skills
     * are present in the resume.
     *
     * @param resumeSkills A list of skills extracted from the resume.
     * @param jobDescriptionSkills A list of skills extracted from the job description.
     * @return A double representing the matching score (e.g., 0.0 to 100.0).
     */
    double calculateScore(List<String> resumeSkills, List<String> jobDescriptionSkills);
}