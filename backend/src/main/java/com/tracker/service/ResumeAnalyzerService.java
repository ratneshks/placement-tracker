package com.tracker.service;

import com.tracker.dto.ResumeAnalysisResult;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ResumeAnalyzerService {

    // A comprehensive dictionary would be loaded from DB/Config in a real app
    private static final List<String> SKILL_DICTIONARY = Arrays.asList(
            "java", "python", "javascript", "react", "spring boot", "sql", "mysql", "postgresql",
            "microservices", "rest api", "docker", "kubernetes", "aws", "gcp", "azure", "c++",
            "algorithms", "data structures", "system design"
    );

    public ResumeAnalysisResult analyzeResume(MultipartFile file, String jobDescription) {
        String resumeText = extractTextFromPdf(file).toLowerCase();
        String jdText = jobDescription.toLowerCase();

        Set<String> resumeSkills = extractSkills(resumeText);
        Set<String> jdSkills = extractSkills(jdText);

        List<String> foundSkills = new ArrayList<>();
        List<String> missingSkills = new ArrayList<>();

        for (String skill : jdSkills) {
            if (resumeSkills.contains(skill)) {
                foundSkills.add(skill);
            } else {
                missingSkills.add(skill);
            }
        }

        double matchPercentage = 0.0;
        if (!jdSkills.isEmpty()) {
            matchPercentage = (double) foundSkills.size() / jdSkills.size() * 100.0;
        }

        return ResumeAnalysisResult.builder()
                .matchPercentage(Math.round(matchPercentage * 100.0) / 100.0)
                .foundSkills(foundSkills)
                .missingSkills(missingSkills)
                .build();
    }

    private String extractTextFromPdf(MultipartFile file) {
        try (PDDocument document = PDDocument.load(file.getInputStream())) {
            PDFTextStripper stripper = new PDFTextStripper();
            return stripper.getText(document);
        } catch (IOException e) {
            throw new RuntimeException("Failed to parse PDF file", e);
        }
    }

    private Set<String> extractSkills(String text) {
        // Basic extraction: check if skill words exist in the text.
        // For multi-word skills like "spring boot", simply checking contains is enough for MVP.
        return SKILL_DICTIONARY.stream()
                .filter(text::contains)
                .collect(Collectors.toSet());
    }
}
