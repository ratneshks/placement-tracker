package com.tracker.dto;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class AnalyticsResponse {
    private int totalApplications;
    private int totalInterviews;
    private int totalOffers;
    private int totalRejections;
    private double selectionRate;
    private Map<String, Integer> weakAreas;
}
