package com.resumeanalyzer.controller;

import com.resumeanalyzer.dto.JobDescriptionRequest;
import com.resumeanalyzer.dto.JobDescriptionResponse;
import com.resumeanalyzer.model.JobDescription;
import com.resumeanalyzer.service.JobDescriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * REST Controller for managing job descriptions.
 * Handles endpoints related to `JobDescription` entities (CRUD operations).
 */
@RestController
@RequestMapping("/api/jobs")
@CrossOrigin(origins = "http://localhost:4200") // Allow requests from your Angular frontend
public class JobDescriptionController {

    private final JobDescriptionService jobDescriptionService;

    /**
     * Constructs the JobDescriptionController with necessary services injected by Spring.
     * @param jobDescriptionService The service handling job description-related business logic.
     */
    @Autowired
    public JobDescriptionController(JobDescriptionService jobDescriptionService) {
        this.jobDescriptionService = jobDescriptionService;
    }

    /**
     * Creates a new job description.
     * Expected endpoint: POST /api/jobs
     *
     * @param request The JobDescriptionRequest DTO containing job details.
     * @return ResponseEntity with the created JobDescriptionResponse.
     */
    @PostMapping
    public ResponseEntity<JobDescriptionResponse> createJobDescription(@RequestBody JobDescriptionRequest request) {
        // Convert DTO to Model
        JobDescription jobDescription = new JobDescription();
        jobDescription.setTitle(request.getTitle());
        jobDescription.setDescription(request.getDescription());
        jobDescription.setRequiredSkills(request.getRequiredSkills());
        jobDescription.setPreferredSkills(request.getPreferredSkills());
        jobDescription.setExperienceLevel(request.getExperienceLevel());

        // Save using the service
        JobDescription createdJob = jobDescriptionService.createJobDescription(jobDescription);

        // Convert Model to Response DTO
        JobDescriptionResponse response = new JobDescriptionResponse(
                createdJob.getId(),
                createdJob.getTitle(),
                createdJob.getDescription(),
                createdJob.getRequiredSkills(),
                createdJob.getPreferredSkills(),
                createdJob.getExperienceLevel()
        );
        return new ResponseEntity<>(response, HttpStatus.CREATED); // Return 201 Created
    }

    /**
     * Retrieves all job descriptions for the current user.
     * Expected endpoint: GET /api/jobs
     *
     * @return ResponseEntity with a list of JobDescriptionResponse DTOs.
     */
    @GetMapping
    public ResponseEntity<List<JobDescriptionResponse>> getJobDescriptions() {
        List<JobDescription> jobs = jobDescriptionService.getAllJobDescriptions();
        // Convert list of Models to list of Response DTOs
        List<JobDescriptionResponse> responses = jobs.stream()
                .map(job -> new JobDescriptionResponse(
                        job.getId(),
                        job.getTitle(),
                        job.getDescription(),
                        job.getRequiredSkills(),
                        job.getPreferredSkills(),
                        job.getExperienceLevel()
                ))
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    /**
     * Retrieves a single job description by its ID.
     * Expected endpoint: GET /api/jobs/{id}
     *
     * @param id The ID of the job description.
     * @return ResponseEntity with the JobDescriptionResponse or 404 if not found.
     */
    @GetMapping("/{id}")
    public ResponseEntity<JobDescriptionResponse> getJobDescription(@PathVariable String id) {
        return jobDescriptionService.getJobDescription(id)
                .map(job -> new JobDescriptionResponse(
                        job.getId(),
                        job.getTitle(),
                        job.getDescription(),
                        job.getRequiredSkills(),
                        job.getPreferredSkills(),
                        job.getExperienceLevel()
                ))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Updates an existing job description.
     * Expected endpoint: PUT /api/jobs/{id}
     *
     * @param id The ID of the job description to update.
     * @param request The JobDescriptionRequest DTO with updated details.
     * @return ResponseEntity with the updated JobDescriptionResponse or 404 if not found.
     */
    @PutMapping("/{id}")
    public ResponseEntity<JobDescriptionResponse> updateJobDescription(@PathVariable String id, @RequestBody JobDescriptionRequest request) {
        // Convert DTO to Model
        JobDescription jobDescription = new JobDescription();
        jobDescription.setId(id); // Ensure ID is set for update
        jobDescription.setTitle(request.getTitle());
        jobDescription.setDescription(request.getDescription());
        jobDescription.setRequiredSkills(request.getRequiredSkills());
        jobDescription.setPreferredSkills(request.getPreferredSkills());
        jobDescription.setExperienceLevel(request.getExperienceLevel());

        JobDescription updatedJob = jobDescriptionService.updateJobDescription(jobDescription);
        if (updatedJob != null) {
            JobDescriptionResponse response = new JobDescriptionResponse(
                    updatedJob.getId(),
                    updatedJob.getTitle(),
                    updatedJob.getDescription(),
                    updatedJob.getRequiredSkills(),
                    updatedJob.getPreferredSkills(),
                    updatedJob.getExperienceLevel()
            );
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Deletes a specific job description by its ID.
     * Expected endpoint: DELETE /api/jobs/{id}
     *
     * @param id The ID of the job description to delete.
     * @return ResponseEntity with 204 No Content on successful deletion or 404 if not found.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteJobDescription(@PathVariable String id) {
        boolean deleted = jobDescriptionService.deleteJobDescription(id);
        if (deleted) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}