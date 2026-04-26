package com.tracker.dto;

import com.tracker.model.ApplicationStatus;
import lombok.Data;

import java.time.LocalDate;

@Data
public class ApplicationRequest {
    private String companyName;
    private String role;
    private ApplicationStatus status;
    private LocalDate appliedDate;
}
