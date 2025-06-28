// src/main/java/com/resumeanalyzer/service/TextExtractionService.java
package com.resumeanalyzer.service;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.logging.Logger; // Import Logger

/**
 * Service for extracting text content from various document types (PDF, DOCX).
 */
@Service
public class TextExtractionService {

    private static final Logger logger = Logger.getLogger(TextExtractionService.class.getName()); // Initialize Logger

    /**
     * Extracts text from a given MultipartFile based on its content type.
     * Supports PDF and DOCX formats.
     * @param file The MultipartFile to extract text from.
     * @return The extracted text content.
     * @throws IOException If there's an error reading the file.
     * @throws IllegalArgumentException If the file type is not supported.
     */
    public String extractText(MultipartFile file) throws IOException {
        String contentType = file.getContentType();
        if (contentType == null) {
            logger.warning("MultipartFile content type is null. Cannot determine file type.");
            throw new IllegalArgumentException("Could not determine file type.");
        }
        logger.info("Attempting to extract text from file: " + file.getOriginalFilename() + " with Content-Type: " + contentType);

        switch (contentType) {
            case "application/pdf":
                return extractPdfText(file);
            case "application/vnd.openxmlformats-officedocument.wordprocessingml.document": // .docx
                return extractDocxText(file);
            case "text/plain":
                return new String(file.getBytes());
            default:
                logger.warning("Unsupported file type encountered: " + contentType);
                throw new IllegalArgumentException("Unsupported file type: " + contentType);
        }
    }

    private String extractPdfText(MultipartFile file) throws IOException {
        try (PDDocument document = PDDocument.load(file.getInputStream())) {
            PDFTextStripper pdfStripper = new PDFTextStripper();
            String text = pdfStripper.getText(document);
            logger.info("PDF Text Extraction successful. Extracted length: " + text.length());
            logger.info("Extracted PDF Text:\n" + text);
            return text;
        } catch (IOException e) {
            logger.severe("Error during PDF text extraction: " + e.getMessage());
            throw e; // Re-throw to propagate the exception
        }
    }

    private String extractDocxText(MultipartFile file) throws IOException {
        try (XWPFDocument document = new XWPFDocument(file.getInputStream())) {
            XWPFWordExtractor extractor = new XWPFWordExtractor(document);
            String text = extractor.getText();
            logger.info("DOCX Text Extraction successful. Extracted length: " + text.length());
            return text;
        } catch (IOException e) {
            logger.severe("Error during DOCX text extraction: " + e.getMessage());
            throw e; // Re-throw to propagate the exception
        }
    }
}
