package com.tracker.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class RecommendationResponse {
    private List<String> suitableRoles;
    private List<String> companiesToTarget;
}
