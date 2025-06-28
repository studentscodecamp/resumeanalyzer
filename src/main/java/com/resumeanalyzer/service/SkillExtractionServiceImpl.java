package com.resumeanalyzer.service;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import java.util.*;
import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.core.JsonProcessingException;
import java.nio.charset.StandardCharsets;
import java.util.logging.Logger;

@Service
public class SkillExtractionServiceImpl implements SkillExtractionService {

    private static final Logger logger = Logger.getLogger(SkillExtractionServiceImpl.class.getName());

    @Value("${gemini.api.url}")
    private String geminiApiUrl;

    @Value("${gemini.api.key}")
    private String geminiApiKey;

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public SkillExtractionServiceImpl() {
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public List<String> extractSkills(String text) {
        if (text == null || text.trim().isEmpty()) {
            logger.warning("Input text for skill extraction is empty or null. Received: " + text);
            return new ArrayList<>();
        }

        String prompt = "Extract key technical and soft skills from the following text as a JSON array of strings under the key 'skills'. Do not include any other text besides the JSON.\n\n" + text;

        logger.info("Sending prompt to LLM for skill extraction. Text length: " + text.length());

        try {
            String requestBody = String.format("""
                {
                    "contents": [
                        {
                            "role": "user",
                            "parts": [
                                { "text": "%s" }
                            ]
                        }
                    ],
                    "generationConfig": {
                        "responseMimeType": "application/json",
                        "responseSchema": {
                            "type": "OBJECT",
                            "properties": {
                                "skills": {
                                    "type": "ARRAY",
                                    "items": { "type": "STRING" }
                                }
                            },
                            "propertyOrdering": ["skills"]
                        }
                    }
                }
                """, escapeJson(prompt));

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(geminiApiUrl + "?key=" + URLEncoder.encode(geminiApiKey, StandardCharsets.UTF_8.toString())))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            logger.info("LLM API Response Status: " + response.statusCode());
            logger.fine("LLM API Response Body: " + response.body());

            if (response.statusCode() == 200) {
                JsonNode rootNode = objectMapper.readTree(response.body());
                JsonNode candidatesNode = rootNode.path("candidates");

                if (candidatesNode.isArray() && !candidatesNode.isEmpty()) {
                    JsonNode contentNode = candidatesNode.get(0).path("content");
                    JsonNode partsNode = contentNode.path("parts");

                    if (partsNode.isArray() && !partsNode.isEmpty()) {
                        JsonNode textNode = partsNode.get(0).path("text");
                        if (textNode.isTextual()) {
                            JsonNode skillResultNode = objectMapper.readTree(textNode.asText());
                            JsonNode skillsArray = skillResultNode.path("skills");

                            if (skillsArray.isArray() && skillsArray.size() > 0) {
                                List<String> extractedSkills = new ArrayList<>();
                                for (JsonNode skillNode : skillsArray) {
                                    extractedSkills.add(skillNode.asText());
                                }
                                logger.info("Successfully extracted skills from LLM: " + extractedSkills);
                                return extractedSkills;
                            } else {
                                logger.warning("Empty 'skills' array from LLM, using fallback extraction.");
                                return extractSkillsFallback(text); // Fallback extraction
                            }
                        }
                    }
                }

                logger.warning("Unexpected LLM response format or missing skills. Using fallback extraction.");
                return extractSkillsFallback(text);
            } else {
                logger.severe("LLM API call failed with status: " + response.statusCode());
                return extractSkillsFallback(text);
            }

        } catch (JsonProcessingException e) {
            logger.severe("Error parsing JSON response from LLM: " + e.getMessage());
            return extractSkillsFallback(text);
        } catch (IOException | InterruptedException e) {
            logger.severe("Error communicating with LLM API: " + e.getMessage());
            Thread.currentThread().interrupt();
            return extractSkillsFallback(text);
        } catch (Exception e) {
            logger.severe("Unexpected error during skill extraction: " + e.getMessage());
            return extractSkillsFallback(text);
        }
    }

    /**
     * Escapes special characters for JSON string safety.
     */
    private String escapeJson(String text) {
        return text.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    /**
     * Fallback method to extract skills based on keyword matching.
     */
    private List<String> extractSkillsFallback(String text) {
        List<String> skillKeywords = List.of(
                "java", "spring", "spring boot", "scrum", "scrum master",
                "oracle certified", "node.js", "bootstrap", "php", "jquery",
                "jsf", "apache struts", "servlets", "jndi", "weblogic",
                "software engineering", "cisco", "problem-solving", "tutoring", "training"
        );

        List<String> extractedSkills = new ArrayList<>();
        String lowerText = text.toLowerCase();

        for (String skill : skillKeywords) {
            if (lowerText.contains(skill.toLowerCase())) {
                extractedSkills.add(skill);
            }
        }

        logger.info("Fallback skill extraction matched: " + extractedSkills);
        return extractedSkills;
    }
}
