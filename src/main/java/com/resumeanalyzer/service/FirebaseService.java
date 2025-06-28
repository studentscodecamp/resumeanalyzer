package com.resumeanalyzer.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * **SIMULATED FIREBASE SERVICE FOR DEMONSTRATION PURPOSES.**
 *
 * In a real Spring Boot application interacting with Google Cloud Firestore,
 * this service would utilize the Firebase Admin SDK.
 *
 * **To set up actual Firebase Admin SDK:**
 * 1. Add `firebase-admin` dependency to your `pom.xml`.
 * 2. Initialize the SDK:
 * `FirebaseOptions options = FirebaseOptions.builder()`
 * `.setCredentials(GoogleCredentials.fromStream(new FileInputStream("path/to/your/serviceAccountKey.json")))`
 * `.setDatabaseUrl("https://<YOUR_PROJECT_ID>.firebaseio.com")` // Or other services you use
 * `.build();`
 * `FirebaseApp.initializeApp(options);`
 * 3. Get Firestore instance: `Firestore db = FirestoreClient.getFirestore();`
 * 4. Replace the in-memory map operations with actual Firestore API calls
 * (e.g., `db.collection("your_collection").document("doc_id").set(data)`).
 *
 * This simulated service uses in-memory maps to store data, allowing the application
 * to run and demonstrate the logic without requiring a live Firestore connection and Admin SDK setup.
 *
 * **User Authentication/Authorization for Firestore Paths:**
 * The Canvas environment provides `__app_id` and `__initial_auth_token` for client-side
 * authentication. For a Spring Boot backend, you would typically authenticate with
 * Firestore using a service account (as mentioned above) and manage user-specific
 * data access by extracting the `userId` from your own authentication mechanism
 * (e.g., JWT from Spring Security) and including it in the Firestore document paths.
 *
 * For this simulation, data is stored in a simple global map, not specific to a user.
 * In a real app, you would prepend paths with `/artifacts/{__app_id}/users/{userId}/`
 * as per Canvas Firestore rules for private data.
 */
@Service
public class FirebaseService {

    // Simulating Firestore collections as ConcurrentHashMaps
    // Outer map: collectionName -> (Inner map: documentId -> documentData)
    private final ConcurrentHashMap<String, ConcurrentHashMap<String, Map<String, Object>>> dataStore = new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper; // For converting objects to/from Map<String, Object>

    public FirebaseService() {
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Simulates saving a document to a Firestore collection.
     * In a real setup, this would use `db.collection(collectionName).document(documentId).set(data)`.
     *
     * @param collectionName The name of the Firestore collection.
     * @param documentId The ID of the document.
     * @param data The object to save.
     */
    public void saveDocument(String collectionName, String documentId, Object data) {
        // Convert object to a Map<String, Object> for generic storage
        Map<String, Object> documentData = objectMapper.convertValue(data, Map.class);
        dataStore.computeIfAbsent(collectionName, k -> new ConcurrentHashMap<>())
                .put(documentId, documentData);
        System.out.println("Simulated Firestore: Saved document '" + documentId + "' in collection '" + collectionName + "'");
    }

    /**
     * Simulates retrieving a document from a Firestore collection.
     * In a real setup, this would use `db.collection(collectionName).document(documentId).get().toObject(valueType)`.
     *
     * @param collectionName The name of the Firestore collection.
     * @param documentId The ID of the document.
     * @param valueType The Class type to convert the document data into.
     * @return The retrieved object, or null if not found.
     */
    public <T> T getDocument(String collectionName, String documentId, Class<T> valueType) {
        ConcurrentHashMap<String, Map<String, Object>> collection = dataStore.get(collectionName);
        if (collection != null) {
            Map<String, Object> documentData = collection.get(documentId);
            if (documentData != null) {
                System.out.println("Simulated Firestore: Retrieved document '" + documentId + "' from collection '" + collectionName + "'");
                // Convert Map<String, Object> back to the desired object type
                return objectMapper.convertValue(documentData, valueType);
            }
        }
        System.out.println("Simulated Firestore: Document '" + documentId + "' not found in collection '" + collectionName + "'");
        return null;
    }

    /**
     * Simulates retrieving all documents from a Firestore collection.
     * In a real setup, this would use `db.collection(collectionName).get().toObjects(valueType)`.
     *
     * @param collectionName The name of the Firestore collection.
     * @param valueType The Class type to convert each document data into.
     * @return A list of all documents in the collection.
     */
    public <T> List<T> getCollection(String collectionName, Class<T> valueType) {
        ConcurrentHashMap<String, Map<String, Object>> collection = dataStore.get(collectionName);
        if (collection != null) {
            System.out.println("Simulated Firestore: Retrieved all documents from collection '" + collectionName + "'");
            return collection.values().stream()
                    .map(docData -> objectMapper.convertValue(docData, valueType))
                    .collect(Collectors.toList());
        }
        System.out.println("Simulated Firestore: Collection '" + collectionName + "' is empty or does not exist.");
        return List.of(); // Return empty list if collection doesn't exist
    }

    /**
     * Simulates deleting a document from a Firestore collection.
     * In a real setup, this would use `db.collection(collectionName).document(documentId).delete()`.
     *
     * @param collectionName The name of the Firestore collection.
     * @param documentId The ID of the document to delete.
     */
    public void deleteDocument(String collectionName, String documentId) {
        ConcurrentHashMap<String, Map<String, Object>> collection = dataStore.get(collectionName);
        if (collection != null) {
            if (collection.remove(documentId) != null) {
                System.out.println("Simulated Firestore: Deleted document '" + documentId + "' from collection '" + collectionName + "'");
            } else {
                System.out.println("Simulated Firestore: Document '" + documentId + "' not found for deletion in collection '" + collectionName + "'");
            }
        }
    }
}