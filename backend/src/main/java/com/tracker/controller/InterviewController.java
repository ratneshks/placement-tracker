package com.tracker.controller;

import com.tracker.dto.InterviewRequest;
import com.tracker.model.Interview;
import com.tracker.service.InterviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/interviews")
@RequiredArgsConstructor
public class InterviewController {

    private final InterviewService interviewService;

    @PostMapping
    public ResponseEntity<Interview> addInterview(
            Authentication authentication,
            @RequestBody InterviewRequest request) {
        return ResponseEntity.ok(interviewService.addInterview(authentication.getName(), request));
    }

    @GetMapping("/application/{applicationId}")
    public ResponseEntity<List<Interview>> getInterviews(
            Authentication authentication,
            @PathVariable UUID applicationId) {
        return ResponseEntity.ok(interviewService.getInterviewsForApplication(authentication.getName(), applicationId));
    }
}
