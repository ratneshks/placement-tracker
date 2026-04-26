package com.tracker.controller;

import com.tracker.dto.ResumeAnalysisResult;
import com.tracker.service.ResumeAnalyzerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/resume")
@RequiredArgsConstructor
public class ResumeAnalyzerController {

    private final ResumeAnalyzerService resumeAnalyzerService;

    @PostMapping("/analyze")
    public ResponseEntity<ResumeAnalysisResult> analyzeResume(
            @RequestParam("file") MultipartFile file,
            @RequestParam("jobDescription") String jobDescription) {
        
        return ResponseEntity.ok(resumeAnalyzerService.analyzeResume(file, jobDescription));
    }
}
