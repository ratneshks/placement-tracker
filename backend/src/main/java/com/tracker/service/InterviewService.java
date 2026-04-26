package com.tracker.service;

import com.tracker.dto.InterviewRequest;
import com.tracker.model.Application;
import com.tracker.model.Interview;
import com.tracker.repository.ApplicationRepository;
import com.tracker.repository.InterviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InterviewService {

    private final InterviewRepository interviewRepository;
    private final ApplicationRepository applicationRepository;

    public Interview addInterview(String email, InterviewRequest request) {
        Application application = applicationRepository.findById(request.getApplicationId())
                .orElseThrow(() -> new RuntimeException("Application not found"));

        if (!application.getUser().getEmail().equals(email)) {
            throw new RuntimeException("Unauthorized");
        }

        Interview interview = Interview.builder()
                .application(application)
                .questionsAsked(request.getQuestionsAsked())
                .topicsCovered(request.getTopicsCovered())
                .outcome(request.getOutcome())
                .feedback(request.getFeedback())
                .interviewDate(request.getInterviewDate())
                .build();

        return interviewRepository.save(interview);
    }

    public List<Interview> getInterviewsForApplication(String email, UUID applicationId) {
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Application not found"));

        if (!application.getUser().getEmail().equals(email)) {
            throw new RuntimeException("Unauthorized");
        }

        return interviewRepository.findByApplicationId(applicationId);
    }
}
