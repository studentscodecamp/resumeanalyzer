package com.resumeanalyzer.service;

import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Service implementation for calculating the compatibility score between a resume and a job description.
 * This service compares extracted skills from both the resume and job description to determine a match percentage.
 */
@Service
public class ScoreCalculationServiceImpl implements ScoreCalculationService {

    /**
     * Calculates the matching score between resume skills and job description skills.
     * The score is determined by the percentage of job description skills found in the resume skills.
     * Edge cases like empty job description skills or empty resume skills are handled gracefully.
     *
     * @param resumeSkills A list of skills extracted from the resume.
     * @param jobDescriptionSkills A list of skills extracted from the job description.
     * @return A double representing the matching score (0.0 to 100.0).
     */
    @Override
    public double calculateScore(List<String> resumeSkills, List<String> jobDescriptionSkills) {
        // If there are no required skills in the job description, it's considered a perfect match (100%)
        // or can be handled as a specific case (e.g., return 0.0 or throw an error based on business logic).
        // Here, 100% indicates all (zero) job skills are matched, which is technically true.
        if (jobDescriptionSkills == null || jobDescriptionSkills.isEmpty()) {
            return 100.0;
        }

        // If the resume has no skills, the score is 0, as no job description skills can be matched.
        if (resumeSkills == null || resumeSkills.isEmpty()) {
            return 0.0;
        }

        // Convert resume skills to a Set for efficient, case-insensitive lookups.
        // Convert all to lowercase for robust matching.
        Set<String> resumeSkillsSet = resumeSkills.stream()
                .map(String::toLowerCase)
                .collect(Collectors.toSet());
        int matchedSkillsCount = 0;

        // Iterate through each skill required by the job description.
        // If the resume contains that skill (case-insensitively), increment the matched count.
        for (String jobSkill : jobDescriptionSkills) {
            if (resumeSkillsSet.contains(jobSkill.toLowerCase())) { // Case-insensitive comparison
                matchedSkillsCount++;
            }
        }

        // Calculate the score as a percentage.
        // Ensure floating-point division by casting one of the operands to double.
        // Handle division by zero if somehow jobDescriptionSkills.size() becomes 0 after initial check.
        if (jobDescriptionSkills.size() == 0) {
            return 100.0; // Should not happen due to initial check, but for robustness
        }
        return ((double) matchedSkillsCount / jobDescriptionSkills.size()) * 100.0;
    }
}