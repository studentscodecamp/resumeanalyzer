package com.resumeanalyzer.service;

import com.resumeanalyzer.model.JobDescription;
import com.resumeanalyzer.repo.JobDescriptionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Service for managing JobDescription entities.
 * Provides methods for creating, retrieving, updating, and deleting job descriptions.
 */
@Service
public class JobDescriptionService {

    private final JobDescriptionRepository jobDescriptionRepository;

    /**
     * Constructs the JobDescriptionService with the JobDescriptionRepository injected by Spring.
     */
    @Autowired
    public JobDescriptionService(JobDescriptionRepository jobDescriptionRepository) {
        this.jobDescriptionRepository = jobDescriptionRepository;
    }

    /**
     * Creates and saves a new job description.
     * @param jobDescription The JobDescription object to save.
     * @return The saved JobDescription, including its generated ID.
     */
    public JobDescription createJobDescription(JobDescription jobDescription) {
        return jobDescriptionRepository.save(jobDescription);
    }

    /**
     * Retrieves a job description by its ID.
     * @param id The ID of the job description.
     * @return An Optional containing the JobDescription if found, empty otherwise.
     */
    public Optional<JobDescription> getJobDescription(String id) {
        return jobDescriptionRepository.findById(id);
    }

    /**
     * Retrieves all job descriptions for the current user.
     * In a real multi-user Firestore setup, this would query based on `userId`.
     * @return A list of all JobDescription objects.
     */
    public List<JobDescription> getAllJobDescriptions() {
        return jobDescriptionRepository.findAll();
    }

    /**
     * Updates an existing job description.
     * @param jobDescription The JobDescription object with updated details (ID must be present).
     * @return The updated JobDescription if found and updated, null otherwise.
     */
    public JobDescription updateJobDescription(JobDescription jobDescription) {
        // Check if the job description exists before attempting to update
        if (jobDescription.getId() == null || jobDescriptionRepository.findById(jobDescription.getId()).isEmpty()) {
            return null; // Or throw a specific exception
        }
        return jobDescriptionRepository.save(jobDescription);
    }

    /**
     * Deletes a job description by its ID.
     * @param id The ID of the job description to delete.
     * @return True if the job description was deleted successfully, false otherwise.
     */
    public boolean deleteJobDescription(String id) {
        return jobDescriptionRepository.deleteById(id);
    }
}