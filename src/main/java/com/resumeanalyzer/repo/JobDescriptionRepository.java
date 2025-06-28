package com.resumeanalyzer.repo;

import com.resumeanalyzer.model.JobDescription;
import com.resumeanalyzer.service.FirebaseService;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Simulated Repository for JobDescription entities.
 * In a real application, this would interact with a database like Firestore.
 * For demonstration, it uses an in-memory map.
 */
@Repository
public class JobDescriptionRepository {
    // In-memory store for JobDescription objects
    private final ConcurrentHashMap<String, JobDescription> inMemoryStore = new ConcurrentHashMap<>();

    // Simulated Firestore collection name
    private static final String COLLECTION_NAME = "job_descriptions";

    private final FirebaseService firebaseService;

    public JobDescriptionRepository(FirebaseService firebaseService) {
        this.firebaseService = firebaseService;
    }

    /**
     * Saves a new JobDescription or updates an existing one.
     * Assigns a new ID if the entity does not have one.
     * @param jobDescription The JobDescription to save.
     * @return The saved JobDescription with its ID.
     */
    public JobDescription save(JobDescription jobDescription) {
        if (jobDescription.getId() == null || jobDescription.getId().isEmpty()) {
            jobDescription.setId(UUID.randomUUID().toString()); // Generate a unique ID
        }
        // Simulate saving to Firestore via FirebaseService (in-memory for now)
        firebaseService.saveDocument(COLLECTION_NAME, jobDescription.getId(), jobDescription);
        inMemoryStore.put(jobDescription.getId(), jobDescription); // Keep in in-memory store
        return jobDescription;
    }

    /**
     * Retrieves a JobDescription by its ID.
     * @param id The ID of the JobDescription.
     * @return An Optional containing the JobDescription if found, empty otherwise.
     */
    public Optional<JobDescription> findById(String id) {
        // Simulate retrieving from Firestore via FirebaseService
        return Optional.ofNullable(firebaseService.getDocument(COLLECTION_NAME, id, JobDescription.class));
    }

    /**
     * Retrieves all JobDescriptions.
     * @return A list of all JobDescriptions.
     */
    public List<JobDescription> findAll() {
        // Simulate retrieving all from Firestore via FirebaseService
        return firebaseService.getCollection(COLLECTION_NAME, JobDescription.class);
    }

    /**
     * Deletes a JobDescription by its ID.
     * @param id The ID of the JobDescription to delete.
     * @return True if the JobDescription was deleted, false otherwise.
     */
    public boolean deleteById(String id) {
        // Simulate deleting from Firestore via FirebaseService
        firebaseService.deleteDocument(COLLECTION_NAME, id);
        return inMemoryStore.remove(id) != null;
    }
}