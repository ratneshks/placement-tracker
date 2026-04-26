package com.tracker.service;

import com.tracker.dto.RecommendationResponse;
import com.tracker.model.Application;
import com.tracker.model.ApplicationStatus;
import com.tracker.repository.ApplicationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RecommendationService {

    private final ApplicationRepository applicationRepository;

    public RecommendationResponse getRecommendations(UUID userId, List<String> userSkills) {
        List<Application> applications = applicationRepository.findByUserId(userId);

        List<String> suitableRoles = determineRoles(userSkills);
        List<String> companiesToTarget = determineCompanies(applications);

        return RecommendationResponse.builder()
                .suitableRoles(suitableRoles)
                .companiesToTarget(companiesToTarget)
                .build();
    }

    private List<String> determineRoles(List<String> skills) {
        List<String> roles = new ArrayList<>();
        if (skills == null) return roles;

        long backendScore = skills.stream()
                .filter(s -> List.of("java", "spring boot", "python", "sql", "postgresql", "docker").contains(s.toLowerCase()))
                .count();

        long frontendScore = skills.stream()
                .filter(s -> List.of("javascript", "react", "html", "css", "vue", "angular").contains(s.toLowerCase()))
                .count();

        long mlScore = skills.stream()
                .filter(s -> List.of("python", "tensorflow", "pytorch", "machine learning", "data analysis").contains(s.toLowerCase()))
                .count();

        if (backendScore >= 2) roles.add("Backend Developer");
        if (frontendScore >= 2) roles.add("Frontend Developer");
        if (mlScore >= 2) roles.add("Machine Learning Engineer");

        if (roles.isEmpty()) {
            roles.add("Software Engineer Intern"); // default fallback
        }

        return roles;
    }

    private List<String> determineCompanies(List<Application> applications) {
        // Recommend companies where the user has progressed beyond APPLIED (e.g. INTERVIEW or SELECTED)
        // or just suggest generic top tier if no data.
        Map<String, Integer> successCount = new HashMap<>();

        for (Application app : applications) {
            if (app.getStatus() == ApplicationStatus.INTERVIEW || app.getStatus() == ApplicationStatus.SELECTED || app.getStatus() == ApplicationStatus.OA) {
                successCount.put(app.getCompanyName(), successCount.getOrDefault(app.getCompanyName(), 0) + 1);
            }
        }

        List<String> targetCompanies = new ArrayList<>();
        successCount.entrySet().stream()
                .sorted((a, b) -> b.getValue().compareTo(a.getValue()))
                .limit(3)
                .forEach(e -> targetCompanies.add(e.getKey()));

        if (targetCompanies.isEmpty()) {
            targetCompanies.addAll(List.of("Google", "Microsoft", "Amazon", "Startups"));
        }

        return targetCompanies;
    }
}
