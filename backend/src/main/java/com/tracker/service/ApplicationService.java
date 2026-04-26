package com.tracker.service;

import com.tracker.dto.ApplicationRequest;
import com.tracker.dto.ApplicationResponse;
import com.tracker.model.Application;
import com.tracker.model.User;
import com.tracker.repository.ApplicationRepository;
import com.tracker.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final UserRepository userRepository;

    public ApplicationResponse addApplication(String email, ApplicationRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Application application = Application.builder()
                .user(user)
                .companyName(request.getCompanyName())
                .role(request.getRole())
                .status(request.getStatus())
                .appliedDate(request.getAppliedDate())
                .build();

        Application saved = applicationRepository.save(application);
        return mapToResponse(saved);
    }

    public List<ApplicationResponse> getApplications(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return applicationRepository.findByUserId(user.getId())
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public ApplicationResponse updateApplication(String email, UUID applicationId, ApplicationRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Application not found"));

        if (!application.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized");
        }

        application.setCompanyName(request.getCompanyName());
        application.setRole(request.getRole());
        application.setStatus(request.getStatus());
        application.setAppliedDate(request.getAppliedDate());

        Application updated = applicationRepository.save(application);
        return mapToResponse(updated);
    }

    public void deleteApplication(String email, UUID applicationId) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Application not found"));

        if (!application.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized");
        }

        applicationRepository.delete(application);
    }

    private ApplicationResponse mapToResponse(Application application) {
        return ApplicationResponse.builder()
                .id(application.getId())
                .companyName(application.getCompanyName())
                .role(application.getRole())
                .status(application.getStatus())
                .appliedDate(application.getAppliedDate())
                .lastUpdated(application.getLastUpdated())
                .build();
    }
}
