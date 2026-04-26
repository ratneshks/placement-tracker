package com.tracker.service;

import com.tracker.dto.ResumeAnalysisResult;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class ResumeAnalyzerServiceTest {

    private ResumeAnalyzerService resumeAnalyzerService;

    @BeforeEach
    void setUp() {
        resumeAnalyzerService = new ResumeAnalyzerService();
    }

    @Test
    void testAnalyzeResume() throws IOException {
        // Create a mock PDF file in memory
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage();
            document.addPage(page);
            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                contentStream.beginText();
                contentStream.setFont(PDType1Font.HELVETICA, 12);
                contentStream.newLineAtOffset(100, 700);
                contentStream.showText("Skills: Java, Python, SQL, REST API, Spring Boot");
                contentStream.endText();
            }
            document.save(out);
        }

        MockMultipartFile mockPdf = new MockMultipartFile(
                "resume", "resume.pdf", "application/pdf", out.toByteArray());

        String jobDescription = "We are looking for a backend developer with experience in Java, Spring Boot, Microservices, and SQL.";

        ResumeAnalysisResult result = resumeAnalyzerService.analyzeResume(mockPdf, jobDescription);

        assertNotNull(result);
        assertTrue(result.getMatchPercentage() > 0);
        assertTrue(result.getFoundSkills().contains("java"));
        assertTrue(result.getFoundSkills().contains("spring boot"));
        assertTrue(result.getMissingSkills().contains("microservices"));
    }
}
