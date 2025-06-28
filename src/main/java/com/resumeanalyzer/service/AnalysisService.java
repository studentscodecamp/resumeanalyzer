// src/main/java/com/resumeanalyzer/service/AnalysisService.java
package com.resumeanalyzer.service;

import com.resumeanalyzer.dto.AnalysisResponse;
import com.resumeanalyzer.model.AnalysisResult;
import com.resumeanalyzer.model.JobDescription;
import com.resumeanalyzer.model.Resume;
import com.resumeanalyzer.repo.AnalysisResultRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.logging.Logger;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

/**
 * Service for performing the analysis of a resume against a job description,
 * and saving the results.
 */
@Service
public class AnalysisService {

    private static final Logger logger = Logger.getLogger(AnalysisService.class.getName());

    private final ResumeService resumeService;
    private final JobDescriptionService jobDescriptionService;
    private final SkillExtractionService skillExtractionService;
    private final ScoreCalculationService scoreCalculationService;
    private final AnalysisResultRepository analysisResultRepository;

    @Autowired
    public AnalysisService(
            ResumeService resumeService,
            JobDescriptionService jobDescriptionService,
            SkillExtractionService skillExtractionService,
            ScoreCalculationService scoreCalculationService,
            AnalysisResultRepository analysisResultRepository) {
        this.resumeService = resumeService;
        this.jobDescriptionService = jobDescriptionService;
        this.skillExtractionService = skillExtractionService;
        this.scoreCalculationService = scoreCalculationService;
        this.analysisResultRepository = analysisResultRepository;
    }

    /**
     * Analyzes a resume against a job description, saves the result, and returns the analysis response.
     * @param resumeId The ID of the resume to analyze.
     * @param jobId The ID of the job description to analyze against.
     * @return AnalysisResponse containing the match score and details.
     * @throws Exception if resume or job description not found, or analysis fails.
     */
    public AnalysisResponse analyzeResumeAndSaveResult(String resumeId, String jobId) throws Exception {
        Optional<Resume> resumeOptional = resumeService.getResume(resumeId);
        Optional<JobDescription> jobOptional = jobDescriptionService.getJobDescription(jobId);

        if (resumeOptional.isEmpty()) {
            throw new IllegalArgumentException("Resume with ID " + resumeId + " not found.");
        }
        if (jobOptional.isEmpty()) {
            throw new IllegalArgumentException("Job Description with ID " + jobId + " not found.");
        }

        Resume resume = resumeOptional.get();
        JobDescription job = jobOptional.get();

        // 1. Extract skills from resume and job description
        List<String> resumeSkills = skillExtractionService.extractSkills(resume.getContent());
        System.out.println("resume.getContent(): " + resume.getContent());
        List<String> jobSkills = skillExtractionService.extractSkills(job.getDescription());

        // Log extracted skills for debugging
        logger.info("Extracted Resume Skills: " + resumeSkills);
        logger.info("Extracted Job Description Skills: " + jobSkills);


        // 2. Calculate match score
        double matchScore = scoreCalculationService.calculateScore(resumeSkills, jobSkills);
        System.out.println("matchScore: " + matchScore);
        // 3. Generate detailed feedback (strengths, weaknesses, recommendations) - Placeholder for LLM
        String strengths = "Your resume strongly matches the required skills. Skills like " +
                getTopMatchingSkills(resumeSkills, jobSkills, 3) + " are well-highlighted.";
        String weaknesses = "Consider adding more details on " +
                getMissingJobSkills(resumeSkills, jobSkills, 2) + " to better align with the job description.";
        String recommendations = "Tailor your resume further by emphasizing projects related to " +
                getRecommendations(jobSkills, 2) + ".";


        // Create the AnalysisResponse DTO for the frontend
        AnalysisResponse analysisResponse = new AnalysisResponse(
                matchScore / 100.0, // Convert percentage back to 0-1 range for frontend display
                "Analysis complete.", // Explicit message for the frontend
                strengths,
                weaknesses,
                recommendations,
                resumeSkills,
                jobSkills
        );
        System.out.println("analysisResponse: " + analysisResponse);
        // 4. Save the analysis result
        AnalysisResult analysisResult = new AnalysisResult();
        analysisResult.setId(UUID.randomUUID().toString());
        analysisResult.setResumeId(resumeId);
        analysisResult.setJobId(jobId);
        analysisResult.setJobTitle(job.getTitle()); // Denormalize job title for easy retrieval
        analysisResult.setCompany("N/A"); // Placeholder for company, if not available in JobDescription model
        analysisResult.setMatchScore(matchScore); // Store as percentage
        analysisResult.setStrengths(strengths);
        analysisResult.setWeaknesses(weaknesses);
        analysisResult.setRecommendations(recommendations);
        // Corrected: Pass Instant object directly, @ServerTimestamp handles Firestore conversion
        analysisResult.setAnalysisDate(Instant.now().toString());

        analysisResultRepository.save(analysisResult);

        return analysisResponse;
    }

    public List<AnalysisResult> getAllAnalysisResults() {
        try {
            return analysisResultRepository.findAll();
        } catch (Exception e) {
            throw new RuntimeException("Error fetching all analysis results", e);
        }
    }


    public Optional<AnalysisResult> getAnalysisResult(String id) {
        try {
            return analysisResultRepository.findById(id);
        } catch (Exception e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Error fetching analysis result with ID: " + id, e);
        }
    }

    // Corrected: Changed return type to void to match AnalysisResultRepository's deleteById
    public boolean deleteAnalysisResult(String id) {
        return analysisResultRepository.deleteById(id);
    }

    // Helper methods for generating mock feedback based on skills
    private String getTopMatchingSkills(List<String> resumeSkills, List<String> jobSkills, int count) {
        List<String> matched = resumeSkills.stream()
                .filter(s -> jobSkills.stream().anyMatch(js -> js.equalsIgnoreCase(s)))
                .limit(count)
                .toList();
        return matched.isEmpty() ? "key skills" : String.join(", ", matched);
    }

    private String getMissingJobSkills(List<String> resumeSkills, List<String> jobSkills, int count) {
        List<String> missing = jobSkills.stream()
                .filter(js -> resumeSkills.stream().noneMatch(rs -> rs.equalsIgnoreCase(js)))
                .limit(count)
                .toList();
        return missing.isEmpty() ? "no specific skills" : String.join(", ", missing);
    }

    private String getRecommendations(List<String> jobSkills, int count) {
        List<String> uniqueJobSkills = jobSkills.stream().limit(count).toList();
        return uniqueJobSkills.isEmpty() ? "relevant technologies" : String.join(" and ", uniqueJobSkills);
    }
}
