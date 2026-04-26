package com.tracker.controller;

import com.tracker.dto.RecommendationResponse;
import com.tracker.model.User;
import com.tracker.repository.UserRepository;
import com.tracker.service.RecommendationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recommendations")
@RequiredArgsConstructor
public class RecommendationController {

    private final RecommendationService recommendationService;
    private final UserRepository userRepository;

    @PostMapping
    public ResponseEntity<RecommendationResponse> getRecommendations(
            Authentication authentication,
            @RequestBody List<String> userSkills) {
        
        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        return ResponseEntity.ok(recommendationService.getRecommendations(user.getId(), userSkills));
    }
}
