package com.tracker.service;

import com.tracker.dto.RecommendationResponse;
import com.tracker.model.Application;
import com.tracker.model.ApplicationStatus;
import com.tracker.repository.ApplicationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RecommendationServiceTest {

    @Mock
    private ApplicationRepository applicationRepository;

    @InjectMocks
    private RecommendationService recommendationService;

    @Test
    void testGetRecommendations() {
        UUID userId = UUID.randomUUID();

        Application app1 = new Application();
        app1.setCompanyName("TechCorp");
        app1.setStatus(ApplicationStatus.INTERVIEW);

        when(applicationRepository.findByUserId(userId)).thenReturn(List.of(app1));

        RecommendationResponse response = recommendationService.getRecommendations(userId, List.of("Java", "Spring Boot", "React"));

        assertNotNull(response);
        assertTrue(response.getSuitableRoles().contains("Backend Developer"));
        assertTrue(response.getCompaniesToTarget().contains("TechCorp"));
    }
}
