// src/main/java/com/resumeanalyzer/controller/AnalysisController.java
package com.resumeanalyzer.controller;

import com.resumeanalyzer.dto.AnalysisResponse;
import com.resumeanalyzer.dto.AnalysisResultResponse;
// Corrected: Using AnalysisRequestPayload consistent with your backend DTOs
import com.resumeanalyzer.dto.AnalysisRequestPayload;
import com.resumeanalyzer.model.AnalysisResult;
import com.resumeanalyzer.model.JobDescription;
import com.resumeanalyzer.model.Resume;
import com.resumeanalyzer.service.AnalysisService;
import com.resumeanalyzer.service.JobDescriptionService;
// Removed ResumeAnalyzerService import as it's now orchestrated by AnalysisService
// import com.resumeanalyzer.service.ResumeAnalyzerService;
import com.resumeanalyzer.service.ResumeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

/**
 * REST Controller for handling resume analysis and managing analysis results.
 * Exposes endpoints for initiating analysis and retrieving historical analysis results.
 */
@RestController
@RequestMapping("/api/analysis")
@CrossOrigin(origins = "http://localhost:4200") // Allow requests from your Angular frontend
public class AnalysisController {

    // Removed ResumeAnalyzerService as it's now internally used by AnalysisService
    // private final ResumeAnalyzerService resumeAnalyzerService;
    private final AnalysisService analysisService;
    private final ResumeService resumeService; // Still needed for fetching resume details if not fully abstracted
    private final JobDescriptionService jobDescriptionService; // Still needed for fetching job details if not fully abstracted


    /**
     * Constructs the AnalysisController with necessary services injected by Spring.
     * Note: ResumeAnalyzerService is removed from here as AnalysisService orchestrates it.
     */
    @Autowired
    public AnalysisController(
            AnalysisService analysisService,
            ResumeService resumeService,
            JobDescriptionService jobDescriptionService) {
        // this.resumeAnalyzerService = resumeAnalyzerService; // No longer needed directly
        this.analysisService = analysisService;
        this.resumeService = resumeService;
        this.jobDescriptionService = jobDescriptionService;
    }

    /**
     * Initiates a new resume analysis against a job description.
     * Expected endpoint: POST /api/analysis
     *
     * @param request The AnalysisRequestPayload DTO containing resumeId and jobDescriptionId.
     * @return ResponseEntity with the AnalysisResponse containing the analysis results.
     */
    @PostMapping
    public ResponseEntity<AnalysisResponse> analyzeResume(@RequestBody AnalysisRequestPayload request) {
        if (request.getResumeId() == null || request.getJobDescriptionId() == null) {
            return ResponseEntity.badRequest().body(new AnalysisResponse(0.0, "Resume ID and Job Description ID are required.", null, null, null, null, null));
        }
        try {
            // Call the AnalysisService method that orchestrates analysis AND saving the result.
            // This single call replaces the manual fetching, analysis, and saving logic.
            AnalysisResponse response = analysisService.analyzeResumeAndSaveResult(request.getResumeId(), request.getJobDescriptionId());
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            // Catch specific exceptions for clearer error messages to the client
            return ResponseEntity.badRequest().body(new AnalysisResponse(0.0, e.getMessage(), null, null, null, null, null));
        } catch (Exception e) {
            System.err.println("Error during analysis: " + e.getMessage());
            // Generic error handling for unexpected issues
            return ResponseEntity.status(500).body(new AnalysisResponse(0.0, "Error performing analysis: " + e.getMessage(), null, null, null, null, null));
        }
    }

    /**
     * Retrieves all historical analysis results for the current user.
     * Expected endpoint: GET /api/analysis
     *
     * @return ResponseEntity with a list of AnalysisResultResponse DTOs.
     */
    @GetMapping
    public ResponseEntity<List<AnalysisResultResponse>> getAnalysisResults() {
        List<AnalysisResult> results = analysisService.getAllAnalysisResults();
        // Convert AnalysisResult models to AnalysisResultResponse DTOs for the frontend dashboard
        List<AnalysisResultResponse> responses = results.stream()
                .map(result -> new AnalysisResultResponse(
                        result.getId(),
                        result.getJobTitle(),
                        result.getCompany(), // Use the company from AnalysisResult
                        result.getMatchScore(),
                        result.getAnalysisDate() // AnalysisDate is Instant, directly mappable to JS Date in frontend
                ))
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    /**
     * Retrieves a single analysis result by its ID.
     * Expected endpoint: GET /api/analysis/{id}
     *
     * @param id The ID of the analysis result.
     * @return ResponseEntity with the AnalysisResultResponse or 404 if not found.
     */
    @GetMapping("/{id}")
    public ResponseEntity<AnalysisResultResponse> getAnalysisResult(@PathVariable String id) {
        return analysisService.getAnalysisResult(id)
                .map(result -> new AnalysisResultResponse(
                        result.getId(),
                        result.getJobTitle(),
                        result.getCompany(),
                        result.getMatchScore(),
                        result.getAnalysisDate()
                ))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Deletes a specific analysis result by its ID.
     * Expected endpoint: DELETE /api/analysis/{id}
     *
     * @param id The ID of the analysis result to delete.
     * @return ResponseEntity with 204 No Content on successful deletion or 500 on error.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAnalysisResult(@PathVariable String id) {
        try {
            // AnalysisService.deleteAnalysisResult now returns void, directly call it.
            analysisService.deleteAnalysisResult(id);
            return ResponseEntity.noContent().build(); // 204 No Content for successful deletion
        } catch (Exception e) {
            System.err.println("Error deleting analysis result with ID " + id + ": " + e.getMessage());
            return ResponseEntity.status(500).build(); // Return 500 Internal Server Error on failure
        }
    }
}
