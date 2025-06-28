package com.resumeanalyzer.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.resumeanalyzer.dto.AnalysisResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.logging.Logger; // Import Logger

/**
 * Orchestration service for the resume analysis process.
 * This service coordinates skill extraction, score calculation, and
 * AI-powered generation of strengths, weaknesses, and recommendations.
 */
@Service
public class ResumeAnalyzerService {

    private static final Logger logger = Logger.getLogger(ResumeAnalyzerService.class.getName());

    private final SkillExtractionService skillExtractionService;
    private final ScoreCalculationService scoreCalculationService;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    @Value("${gemini.api.url}")
    private String geminiApiUrl;

    @Value("${gemini.api.key}")
    private String geminiApiKey;

    /**
     * Constructs the ResumeAnalyzerService with necessary dependencies injected by Spring.
     */
    @Autowired
    public ResumeAnalyzerService(SkillExtractionService skillExtractionService,
                                 ScoreCalculationService scoreCalculationService) {
        this.skillExtractionService = skillExtractionService;
        this.scoreCalculationService = scoreCalculationService;
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Analyzes the provided resume text against the job description text and its required skills.
     * This method performs skill extraction, score calculation, and generates qualitative analysis
     * (strengths, weaknesses, and recommendations) using the LLM.
     *
     * @param resumeText The extracted text content of the resume.
     * @param jobDescriptionText The full text content of the job description.
     * @param jobDescriptionRequiredSkills A comma-separated string of required skills from the job description (can be used for more targeted extraction).
     * @return An AnalysisResponse DTO containing the analysis results.
     */
    public AnalysisResponse analyze(String resumeText, String jobDescriptionText, String jobDescriptionRequiredSkills) {
        // Step 1: Extract skills from the resume
        List<String> resumeSkills = skillExtractionService.extractSkills(resumeText);
        logger.info("Extracted Resume Skills: " + resumeSkills);

        // Step 2: Extract skills from the job description (using the full description or just the required skills list)
        List<String> jobDescriptionSkills = new ArrayList<>();
        if (jobDescriptionRequiredSkills != null && !jobDescriptionRequiredSkills.trim().isEmpty()) {
            jobDescriptionSkills = Arrays.stream(jobDescriptionRequiredSkills.split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .collect(Collectors.toList());
        } else {
            // Fallback: If no explicit required skills, extract from full job description text
            jobDescriptionSkills = skillExtractionService.extractSkills(jobDescriptionText);
        }
        logger.info("Extracted Job Description Skills (for scoring): " + jobDescriptionSkills);

        // Step 3: Calculate the match score
        double score = scoreCalculationService.calculateScore(resumeSkills, jobDescriptionSkills);
        logger.info("Calculated Match Score: " + String.format("%.2f", score) + "%");

        // Step 4: Generate qualitative analysis (strengths, weaknesses, recommendations) using the LLM
        // This is a separate LLM call to get more detailed insights.
        AnalysisResponse qualitativeAnalysis = generateQualitativeAnalysis(resumeText, jobDescriptionText, score);

        // Combine all results into the final AnalysisResponse
        // Corrected: Added 'message' as the second argument.
        AnalysisResponse finalResponse = new AnalysisResponse(
                score / 100.0, // Assuming matchScore needs to be 0.0-1.0 for frontend
                qualitativeAnalysis.getMessage(), // Get the message from qualitative analysis
                qualitativeAnalysis.getStrengths(),
                qualitativeAnalysis.getWeaknesses(),
                qualitativeAnalysis.getRecommendations(),
                resumeSkills,
                jobDescriptionSkills
        );

        return finalResponse;
    }

    /**
     * Generates strengths, weaknesses, and recommendations using the Gemini LLM.
     * This method sends a prompt to the LLM asking for structured analysis based on
     * the resume content, job description, and a preliminary match score.
     *
     * @param resumeText The text content of the resume.
     * @param jobDescriptionText The text content of the job description.
     * @param matchScore The calculated match score.
     * @return An AnalysisResponse DTO containing the generated strengths, weaknesses, and recommendations.
     */
    private AnalysisResponse generateQualitativeAnalysis(String resumeText, String jobDescriptionText, double matchScore) {
        String prompt = String.format("""
            Based on the following resume and job description, provide a detailed analysis.
            The current calculated match score is %.2f%%.

            Resume:
            ---
            %s
            ---

            Job Description:
            ---
            %s
            ---

            Please provide the analysis in a JSON object with the following keys:
            "strengths": A string summarizing the resume's strengths relevant to the job.
            "weaknesses": A string summarizing areas for improvement or gaps in the resume concerning the job.
            "recommendations": A string offering actionable advice for improving the resume's match to this job or similar roles.
            "message": A short concluding message about the analysis (e.g., "Analysis complete." or "Detailed feedback provided.").
            """, matchScore, resumeText, jobDescriptionText);

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
                                "strengths": { "type": "STRING" },
                                "weaknesses": { "type": "STRING" },
                                "recommendations": { "type": "STRING" },
                                "message": { "type": "STRING" } // Added message field to schema
                            },
                            "propertyOrdering": ["strengths", "weaknesses", "recommendations", "message"] // Maintain order
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

            logger.info("LLM qualitative analysis: API Response Status: " + response.statusCode());
            logger.fine("LLM qualitative analysis: API Response Body: " + response.body());

            if (response.statusCode() == 200) {
                JsonNode rootNode = objectMapper.readTree(response.body());
                JsonNode candidatesNode = rootNode.path("candidates");

                if (candidatesNode.isArray() && !candidatesNode.isEmpty()) {
                    JsonNode contentNode = candidatesNode.get(0).path("content");
                    JsonNode partsNode = contentNode.path("parts");

                    if (partsNode.isArray() && !partsNode.isEmpty()) {
                        JsonNode textNode = partsNode.get(0).path("text");
                        if (textNode.isTextual()) {
                            JsonNode analysisNode = objectMapper.readTree(textNode.asText());
                            String strengths = analysisNode.path("strengths").asText("");
                            String weaknesses = analysisNode.path("weaknesses").asText("");
                            String recommendations = analysisNode.path("recommendations").asText("");
                            String message = analysisNode.path("message").asText("Qualitative analysis generated."); // Retrieve message

                            // Corrected constructor call: now includes 'message' as the second argument
                            return new AnalysisResponse(matchScore, message, strengths, weaknesses, recommendations, new ArrayList<>(), new ArrayList<>());
                        }
                    }
                }
                logger.warning("LLM qualitative analysis: Unexpected response format or missing fields: " + response.body());
            } else {
                logger.severe("LLM qualitative analysis: API call failed with status: " + response.statusCode() + ", Body: " + response.body());
            }
        } catch (IOException | InterruptedException e) {
            logger.severe("LLM qualitative analysis: Error communicating with LLM API: " + e.getMessage());
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            logger.severe("LLM qualitative analysis: An unexpected error occurred: " + e.getMessage());
        }

        // Return a default/empty response in case of any failure
        return new AnalysisResponse(matchScore, "Analysis failed.", "N/A", "N/A", "N/A", new ArrayList<>(), new ArrayList<>());
    }

    /**
     * Helper method to escape special characters in a string to make it a valid JSON string value.
     */
    private String escapeJson(String text) {
        return text.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}
