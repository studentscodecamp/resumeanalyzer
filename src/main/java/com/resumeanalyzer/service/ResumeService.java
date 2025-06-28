// src/main/java/com/resumeanalyzer/service/ResumeService.java
package com.resumeanalyzer.service;

import com.resumeanalyzer.model.Resume;
import com.resumeanalyzer.repo.ResumeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID; // For generating unique IDs
import java.util.logging.Logger; // Import Logger

/**
 * Service for handling Resume-related business logic.
 */
@Service
public class ResumeService {

    private static final Logger logger = Logger.getLogger(ResumeService.class.getName()); // Initialize Logger

    private final ResumeRepository resumeRepository;
    private final TextExtractionService textExtractionService; // Inject TextExtractionService

    @Autowired
    public ResumeService(ResumeRepository resumeRepository, TextExtractionService textExtractionService) {
        this.resumeRepository = resumeRepository;
        this.textExtractionService = textExtractionService;
    }

    public Resume saveResume(MultipartFile file) throws IOException, InterruptedException, java.util.concurrent.ExecutionException {
        // Extract text content using the injected TextExtractionService
        String extractedText = textExtractionService.extractText(file);
        System.out.println("Extracted Text:\n" + extractedText);
        // --- NEW LOGGING ADDED HERE TO DEBUG TEXT EXTRACTION ---
        // Log the extracted text to see if PDFBox (or DOCX extractor) is actually retrieving content.
        // We truncate the content for logging to avoid flooding logs with very large resumes.
        logger.info("Extracted text from resume file (length: " + extractedText.length() + "): " +
                (extractedText.length() > 500 ? extractedText.substring(0, 500) + "..." : extractedText));
        // --- END NEW LOGGING ---

        Resume resume = new Resume();
        resume.setId(UUID.randomUUID().toString()); // Generate a unique ID
        resume.setFileName(file.getOriginalFilename());
        resume.setContentType(file.getContentType());
        resume.setFileSize(file.getSize());
        System.out.println("file.getSize:\n" + file.getSize());
        resume.setContent(extractedText); // This line sets the content
        resume.setUploadDate(Instant.now().toString()); // Set current timestamp
        System.out.println("Instant.now().toString():\n" + Instant.now().toString());
        return resumeRepository.save(resume);
    }

    public Optional<Resume> getResume(String id) {
        try {
            return resumeRepository.findById(id);
        } catch (Exception e) {
            Thread.currentThread().interrupt(); // Restore interrupt status
            throw new RuntimeException("Error fetching resume with ID: " + id, e);
        }
    }

    public void deleteResume(String id) {
        resumeRepository.deleteById(id);
    }
}
