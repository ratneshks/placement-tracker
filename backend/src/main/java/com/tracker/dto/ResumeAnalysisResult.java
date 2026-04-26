package com.tracker.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ResumeAnalysisResult {
    private double matchPercentage;
    private List<String> foundSkills;
    private List<String> missingSkills;
}
