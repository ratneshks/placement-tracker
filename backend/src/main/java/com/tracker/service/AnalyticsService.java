package com.tracker.service;

import com.tracker.dto.AnalyticsResponse;
import com.tracker.model.Application;
import com.tracker.model.ApplicationStatus;
import com.tracker.model.Interview;
import com.tracker.repository.ApplicationRepository;
import com.tracker.repository.InterviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AnalyticsService {

    private final ApplicationRepository applicationRepository;
    private final InterviewRepository interviewRepository;

    public AnalyticsResponse getAnalytics(UUID userId) {
        List<Application> applications = applicationRepository.findByUserId(userId);

        int totalApplications = applications.size();
        int totalOffers = 0;
        int totalRejections = 0;
        int totalInterviews = 0;

        for (Application app : applications) {
            if (app.getStatus() == ApplicationStatus.SELECTED) totalOffers++;
            if (app.getStatus() == ApplicationStatus.REJECTED) totalRejections++;
            if (app.getStatus() == ApplicationStatus.INTERVIEW) totalInterviews++;
        }

        double selectionRate = 0.0;
        if (totalApplications > 0) {
            selectionRate = (double) totalOffers / totalApplications * 100.0;
        }

        Map<String, Integer> weakAreas = identifyWeakAreas(applications);

        return AnalyticsResponse.builder()
                .totalApplications(totalApplications)
                .totalInterviews(totalInterviews)
                .totalOffers(totalOffers)
                .totalRejections(totalRejections)
                .selectionRate(Math.round(selectionRate * 100.0) / 100.0)
                .weakAreas(weakAreas)
                .build();
    }

    private Map<String, Integer> identifyWeakAreas(List<Application> applications) {
        Map<String, Integer> weakAreas = new HashMap<>();

        for (Application app : applications) {
            List<Interview> interviews = interviewRepository.findByApplicationId(app.getId());
            for (Interview interview : interviews) {
                if (interview.getOutcome() == ApplicationStatus.REJECTED && interview.getTopicsCovered() != null) {
                    String[] topics = interview.getTopicsCovered().split(",");
                    for (String topic : topics) {
                        String cleanTopic = topic.trim().toLowerCase();
                        if (!cleanTopic.isEmpty()) {
                            weakAreas.put(cleanTopic, weakAreas.getOrDefault(cleanTopic, 0) + 1);
                        }
                    }
                }
            }
        }
        return weakAreas;
    }
}
