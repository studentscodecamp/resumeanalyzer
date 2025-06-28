package com.resumeanalyzer.service;

import java.util.List;

/**
 * Interface for the skill extraction service.
 * Defines the contract for extracting skills from a given text.
 * This service will typically interact with an AI/NLP model (e.g., Gemini API)
 * to identify and return a list of relevant skills.
 */
public interface SkillExtractionService {
    /**
     * Extracts a list of skills from the provided text.
     * The implementation will use an underlying AI model to perform the extraction.
     *
     * @param text The input text (e.g., resume content, job description).
     * @return A list of extracted skills (strings). Returns an empty list if no skills are found or an error occurs.
     */
    List<String> extractSkills(String text);
}