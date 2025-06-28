package com.resumeanalyzer.repo;

import com.resumeanalyzer.model.AnalysisResult;
import com.resumeanalyzer.service.FirebaseService;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Simulated Repository for AnalysisResult entities.
 * In a real application, this would interact with a database like Firestore.
 * For demonstration, it uses an in-memory map.
 */
@Repository
public class AnalysisResultRepository {
    // In-memory store for AnalysisResult objects
    private final ConcurrentHashMap<String, AnalysisResult> inMemoryStore = new ConcurrentHashMap<>();

    // Simulated Firestore collection name
    private static final String COLLECTION_NAME = "analysis_results";

    // Although FirebaseService is injected, its methods will operate on inMemoryStore for simulation.
    private final FirebaseService firebaseService;

    public AnalysisResultRepository(FirebaseService firebaseService) {
        this.firebaseService = firebaseService;
    }

    /**
     * Saves a new AnalysisResult or updates an existing one.
     * Assigns a new ID if the entity does not have one.
     * @param result The AnalysisResult to save.
     * @return The saved AnalysisResult with its ID.
     */
    public AnalysisResult save(AnalysisResult result) {
        if (result.getId() == null || result.getId().isEmpty()) {
            result.setId(UUID.randomUUID().toString()); // Generate a unique ID
        }
        // Simulate saving to Firestore via FirebaseService (in-memory for now)
        firebaseService.saveDocument(COLLECTION_NAME, result.getId(), result);
        inMemoryStore.put(result.getId(), result); // Keep in in-memory store
        return result;
    }

    /**
     * Retrieves an AnalysisResult by its ID.
     * @param id The ID of the AnalysisResult.
     * @return An Optional containing the AnalysisResult if found, empty otherwise.
     */
    public Optional<AnalysisResult> findById(String id) {
        // Simulate retrieving from Firestore via FirebaseService
        return Optional.ofNullable(firebaseService.getDocument(COLLECTION_NAME, id, AnalysisResult.class));
    }

    /**
     * Retrieves all AnalysisResults.
     * @return A list of all AnalysisResults.
     */
    public List<AnalysisResult> findAll() {
        // Simulate retrieving all from Firestore via FirebaseService
        return firebaseService.getCollection(COLLECTION_NAME, AnalysisResult.class);
    }

    /**
     * Deletes an AnalysisResult by its ID.
     * @param id The ID of the AnalysisResult to delete.
     * @return True if the AnalysisResult was deleted, false otherwise.
     */
    public boolean deleteById(String id) {
        // Simulate deleting from Firestore via FirebaseService
        firebaseService.deleteDocument(COLLECTION_NAME, id);
        return inMemoryStore.remove(id) != null;
    }
}