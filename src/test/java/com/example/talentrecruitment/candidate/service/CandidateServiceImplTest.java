package com.example.talentrecruitment.candidate.service;

import com.example.talentrecruitment.candidate.dto.CandidateRequest;
import com.example.talentrecruitment.candidate.dto.CandidateResponse;
import com.example.talentrecruitment.candidate.entity.Candidate;
import com.example.talentrecruitment.candidate.entity.CandidateStatus;
import com.example.talentrecruitment.candidate.repository.CandidateRepository;
import com.example.talentrecruitment.candidate.service.impl.CandidateServiceImpl;
import com.example.talentrecruitment.common.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CandidateServiceImplTest {

    @Mock
    private CandidateRepository candidateRepository;

    @InjectMocks
    private CandidateServiceImpl candidateService;

    private Candidate candidate;

    @BeforeEach
    void setUp() {
        candidate = Candidate.builder()
                .id(1L)
                .firstName("Jane")
                .lastName("Doe")
                .email("jane@example.com")
                .phone("1234567890")
                .skills("Java, Spring")
                .experience(5)
                .resumeUrl("https://example.com/resume.pdf")
                .status(CandidateStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    void createCandidate_shouldReturnCandidateResponse() {
        CandidateRequest request = CandidateRequest.builder()
                .firstName("Jane")
                .lastName("Doe")
                .email("jane@example.com")
                .phone("1234567890")
                .skills("Java, Spring")
                .experience(5)
                .resumeUrl("https://example.com/resume.pdf")
                .status(CandidateStatus.ACTIVE)
                .build();

        when(candidateRepository.existsByEmail(any())).thenReturn(false);
        when(candidateRepository.save(any(Candidate.class))).thenReturn(candidate);

        CandidateResponse response = candidateService.createCandidate(request);

        assertNotNull(response);
        assertEquals("Jane", response.getFirstName());
        assertEquals("jane@example.com", response.getEmail());
    }

    @Test
    void getCandidate_shouldReturnCandidate() {
        when(candidateRepository.findById(1L)).thenReturn(Optional.of(candidate));

        CandidateResponse result = candidateService.getCandidateById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Jane", result.getFirstName());
    }

    @Test
    void getCandidate_whenNotFound_shouldThrow() {
        when(candidateRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> candidateService.getCandidateById(99L));
    }
}
