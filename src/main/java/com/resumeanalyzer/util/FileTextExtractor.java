package com.resumeanalyzer.util;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.io.InputStream;

/**
 * Utility class for extracting text content from various file types.
 * Currently supports PDF. Placeholder comments for DOCX and DOC.
 *
 * **IMPORTANT**: For production use, you must uncomment and implement the
 * parsing logic for DOCX and DOC files using Apache POI.
 * Ensure you add the corresponding Apache POI dependencies to your `pom.xml`.
 */
@Component
public class FileTextExtractor {

    /**
     * Extracts text from a given MultipartFile based on its content type.
     *
     * @param file The MultipartFile to extract text from.
     * @return The extracted text content as a String.
     * @throws IOException If an I/O error occurs during file reading.
     * @throws IllegalArgumentException If the file type is not supported.
     */
    public String extractText(MultipartFile file) throws IOException, IllegalArgumentException {
        String contentType = file.getContentType();
        if (contentType == null) {
            throw new IllegalArgumentException("File content type is unknown.");
        }

        try (InputStream inputStream = file.getInputStream()) {
            switch (contentType) {
                case "application/pdf":
                    return extractPdfText(inputStream);
                case "application/vnd.openxmlformats-officedocument.wordprocessingml.document": // .docx
                    // return extractDocxText(inputStream); // Uncomment and implement
                    System.out.println("DOCX parsing not fully implemented. Returning placeholder.");
                    return simulateDocxTextExtraction(file.getOriginalFilename());
                case "application/msword": // .doc
                    // return extractDocText(inputStream); // Uncomment and implement
                    System.out.println("DOC parsing not fully implemented. Returning placeholder.");
                    return simulateDocTextExtraction(file.getOriginalFilename());
                case "text/plain":
                    return new String(file.getBytes());
                default:
                    throw new IllegalArgumentException("Unsupported file type: " + contentType + ". Supported types are PDF, DOC, DOCX, TXT.");
            }
        }
    }

    /**
     * Extracts text from a PDF InputStream using Apache PDFBox.
     * @param inputStream InputStream of the PDF file.
     * @return Extracted text.
     * @throws IOException If PDF parsing fails.
     */
    private String extractPdfText(InputStream inputStream) throws IOException {
        try (PDDocument document = PDDocument.load(inputStream)) {
            PDFTextStripper pdfStripper = new PDFTextStripper();
            return pdfStripper.getText(document);
        }
    }

    /**
     * Placeholder method for DOCX text extraction.
     * In a real implementation, you would use Apache POI:
     * `XWPFDocument document = new XWPFDocument(inputStream);`
     * `XWPFWordExtractor extractor = new XWPFWordExtractor(document);`
     * `return extractor.getText();`
     */
    private String simulateDocxTextExtraction(String fileName) {
        System.out.println("Simulating DOCX text extraction for: " + fileName);
        return "Simulated DOCX Content: Experience with Java, Spring Boot, Microservices, REST APIs, SQL, Agile. Designed and developed scalable backend systems.";
    }

    /**
     * Placeholder method for DOC text extraction.
     * In a real implementation, you would use Apache POI:
     * `HWPFDocument document = new HWPFDocument(inputStream);`
     * `WordExtractor extractor = new WordExtractor(document);`
     * `return extractor.getText();`
     */
    private String simulateDocTextExtraction(String fileName) {
        System.out.println("Simulating DOC text extraction for: " + fileName);
        return "Simulated DOC Content: Skilled in Python, Django, Frontend frameworks like Angular/React. Database design and optimization.";
    }
}