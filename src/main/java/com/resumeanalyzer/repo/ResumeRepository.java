package com.resumeanalyzer.repo;

import com.resumeanalyzer.model.Resume;
import com.resumeanalyzer.service.FirebaseService;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Simulated Repository for Resume entities.
 * In a real application, this would interact with a database like Firestore.
 * For demonstration, it uses an in-memory map.
 */
@Repository
public class ResumeRepository {
    // In-memory store for Resume objects
    private final ConcurrentHashMap<String, Resume> inMemoryStore = new ConcurrentHashMap<>();

    // Simulated Firestore collection name
    private static final String COLLECTION_NAME = "resumes";

    private final FirebaseService firebaseService;

    public ResumeRepository(FirebaseService firebaseService) {
        this.firebaseService = firebaseService;
    }

    /**
     * Saves a new Resume or updates an existing one.
     * Assigns a new ID if the entity does not have one.
     * @param resume The Resume to save.
     * @return The saved Resume with its ID.
     */
    public Resume save(Resume resume) {
        if (resume.getId() == null || resume.getId().isEmpty()) {
            resume.setId(UUID.randomUUID().toString()); // Generate a unique ID
        }
        // Simulate saving to Firestore via FirebaseService (in-memory for now)
        firebaseService.saveDocument(COLLECTION_NAME, resume.getId(), resume);
        inMemoryStore.put(resume.getId(), resume); // Keep in in-memory store
        return resume;
    }

    /**
     * Retrieves a Resume by its ID.
     * @param id The ID of the Resume.
     * @return An Optional containing the Resume if found, empty otherwise.
     */
    public Optional<Resume> findById(String id) {
        // Simulate retrieving from Firestore via FirebaseService
        return Optional.ofNullable(firebaseService.getDocument(COLLECTION_NAME, id, Resume.class));
    }

    /**
     * Retrieves all Resumes.
     * @return A list of all Resumes.
     */
    public List<Resume> findAll() {
        // Simulate retrieving all from Firestore via FirebaseService
        return firebaseService.getCollection(COLLECTION_NAME, Resume.class);
    }

    /**
     * Deletes a Resume by its ID.
     * @param id The ID of the Resume to delete.
     * @return True if the Resume was deleted, false otherwise.
     */
    public boolean deleteById(String id) {
        // Simulate deleting from Firestore via FirebaseService
        firebaseService.deleteDocument(COLLECTION_NAME, id);
        return inMemoryStore.remove(id) != null;
    }
}