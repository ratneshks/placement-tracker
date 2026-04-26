package com.tracker.dto;

import com.tracker.model.ApplicationStatus;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
public class InterviewRequest {
    private UUID applicationId;
    private String questionsAsked;
    private String topicsCovered;
    private ApplicationStatus outcome;
    private String feedback;
    private LocalDate interviewDate;
}
