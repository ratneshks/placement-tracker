package com.tracker.controller;

import com.tracker.dto.ApplicationRequest;
import com.tracker.dto.ApplicationResponse;
import com.tracker.service.ApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/applications")
@RequiredArgsConstructor
public class ApplicationController {

    private final ApplicationService applicationService;

    @PostMapping
    public ResponseEntity<ApplicationResponse> createApplication(
            Authentication authentication,
            @RequestBody ApplicationRequest request) {
        return ResponseEntity.ok(applicationService.addApplication(authentication.getName(), request));
    }

    @GetMapping
    public ResponseEntity<List<ApplicationResponse>> getApplications(Authentication authentication) {
        return ResponseEntity.ok(applicationService.getApplications(authentication.getName()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApplicationResponse> updateApplication(
            Authentication authentication,
            @PathVariable UUID id,
            @RequestBody ApplicationRequest request) {
        return ResponseEntity.ok(applicationService.updateApplication(authentication.getName(), id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteApplication(
            Authentication authentication,
            @PathVariable UUID id) {
        applicationService.deleteApplication(authentication.getName(), id);
        return ResponseEntity.noContent().build();
    }
}
