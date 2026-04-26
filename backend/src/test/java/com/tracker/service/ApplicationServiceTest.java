package com.tracker.service;

import com.tracker.dto.ApplicationRequest;
import com.tracker.dto.ApplicationResponse;
import com.tracker.model.Application;
import com.tracker.model.ApplicationStatus;
import com.tracker.model.User;
import com.tracker.repository.ApplicationRepository;
import com.tracker.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ApplicationServiceTest {

    @Mock
    private ApplicationRepository applicationRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ApplicationService applicationService;

    private User testUser;
    private Application testApplication;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(UUID.randomUUID());
        testUser.setEmail("test@example.com");

        testApplication = new Application();
        testApplication.setId(UUID.randomUUID());
        testApplication.setUser(testUser);
        testApplication.setCompanyName("Google");
        testApplication.setRole("Software Engineer");
        testApplication.setStatus(ApplicationStatus.APPLIED);
        testApplication.setAppliedDate(LocalDate.now());
    }

    @Test
    void testAddApplication() {
        ApplicationRequest request = new ApplicationRequest();
        request.setCompanyName("Google");
        request.setRole("Software Engineer");
        request.setStatus(ApplicationStatus.APPLIED);
        request.setAppliedDate(LocalDate.now());

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(applicationRepository.save(any(Application.class))).thenReturn(testApplication);

        ApplicationResponse response = applicationService.addApplication("test@example.com", request);

        assertNotNull(response);
        assertEquals("Google", response.getCompanyName());
        verify(applicationRepository, times(1)).save(any(Application.class));
    }

    @Test
    void testGetApplications() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(applicationRepository.findByUserId(testUser.getId())).thenReturn(List.of(testApplication));

        List<ApplicationResponse> responses = applicationService.getApplications("test@example.com");

        assertEquals(1, responses.size());
        assertEquals("Google", responses.get(0).getCompanyName());
    }

    @Test
    void testUpdateApplication() {
        ApplicationRequest request = new ApplicationRequest();
        request.setCompanyName("Google Updated");
        request.setRole("Software Engineer");
        request.setStatus(ApplicationStatus.INTERVIEW);
        request.setAppliedDate(LocalDate.now());

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(applicationRepository.findById(testApplication.getId())).thenReturn(Optional.of(testApplication));
        when(applicationRepository.save(any(Application.class))).thenReturn(testApplication);

        ApplicationResponse response = applicationService.updateApplication("test@example.com", testApplication.getId(), request);

        assertNotNull(response);
        assertEquals(ApplicationStatus.INTERVIEW, testApplication.getStatus());
        assertEquals("Google Updated", testApplication.getCompanyName());
    }
}
