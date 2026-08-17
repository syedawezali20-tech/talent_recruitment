package com.example.talentrecruitment.candidate.service.impl;

import com.example.talentrecruitment.candidate.dto.CandidateRequest;
import com.example.talentrecruitment.candidate.dto.CandidateResponse;
import com.example.talentrecruitment.candidate.entity.Candidate;
import com.example.talentrecruitment.candidate.entity.CandidateStatus;
import com.example.talentrecruitment.candidate.repository.CandidateRepository;
import com.example.talentrecruitment.candidate.service.CandidateService;
import com.example.talentrecruitment.common.exception.BadRequestException;
import com.example.talentrecruitment.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class CandidateServiceImpl implements CandidateService {

    private final CandidateRepository candidateRepository;

    @Override
    public CandidateResponse createCandidate(CandidateRequest request) {
        if (candidateRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Candidate with this email already exists");
        }

        Candidate candidate = Candidate.builder()
                .firstName(request.getFirstName().trim())
                .lastName(request.getLastName().trim())
                .email(request.getEmail().trim())
                .phone(request.getPhone().trim())
                .skills(request.getSkills() != null ? request.getSkills().trim() : null)
                .experience(request.getExperience())
                .resumeUrl(request.getResumeUrl())
                .status(request.getStatus() != null ? request.getStatus() : CandidateStatus.ACTIVE)
                .build();

        Candidate savedCandidate = candidateRepository.save(candidate);
        log.info("Created candidate with id {}", savedCandidate.getId());
        return mapToResponse(savedCandidate);
    }

    @Override
    public CandidateResponse getCandidateById(Long id) {
        Candidate candidate = candidateRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Candidate not found with id: " + id));
        return mapToResponse(candidate);
    }

    @Override
    public Page<CandidateResponse> getAllCandidates(String skill, String status, Pageable pageable) {
        Page<Candidate> candidates;

        if (skill != null && !skill.isBlank() && status != null && !status.isBlank()) {
            CandidateStatus candidateStatus = CandidateStatus.valueOf(status.trim().toUpperCase());
            candidates = candidateRepository.findBySkillsContainingIgnoreCaseAndStatus(skill, candidateStatus, pageable);
        } else if (skill != null && !skill.isBlank()) {
            candidates = candidateRepository.findBySkillsContainingIgnoreCase(skill, pageable);
        } else if (status != null && !status.isBlank()) {
            CandidateStatus candidateStatus = CandidateStatus.valueOf(status.trim().toUpperCase());
            candidates = candidateRepository.findByStatus(candidateStatus, pageable);
        } else {
            candidates = candidateRepository.findAll(pageable);
        }

        return candidates.map(this::mapToResponse);
    }

    @Override
    public CandidateResponse updateCandidate(Long id, CandidateRequest request) {
        Candidate candidate = candidateRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Candidate not found with id: " + id));

        if (!candidate.getEmail().equalsIgnoreCase(request.getEmail()) && candidateRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Candidate with this email already exists");
        }

        candidate.setFirstName(request.getFirstName().trim());
        candidate.setLastName(request.getLastName().trim());
        candidate.setEmail(request.getEmail().trim());
        candidate.setPhone(request.getPhone().trim());
        candidate.setSkills(request.getSkills() != null ? request.getSkills().trim() : null);
        candidate.setExperience(request.getExperience());
        candidate.setResumeUrl(request.getResumeUrl());
        candidate.setStatus(request.getStatus() != null ? request.getStatus() : candidate.getStatus());

        Candidate updatedCandidate = candidateRepository.save(candidate);
        log.info("Updated candidate with id {}", updatedCandidate.getId());
        return mapToResponse(updatedCandidate);
    }

    @Override
    public void deleteCandidate(Long id) {
        Candidate candidate = candidateRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Candidate not found with id: " + id));
        candidateRepository.delete(candidate);
        log.info("Deleted candidate with id {}", id);
    }

    private CandidateResponse mapToResponse(Candidate candidate) {
        return CandidateResponse.builder()
                .id(candidate.getId())
                .firstName(candidate.getFirstName())
                .lastName(candidate.getLastName())
                .email(candidate.getEmail())
                .phone(candidate.getPhone())
                .skills(candidate.getSkills())
                .experience(candidate.getExperience())
                .resumeUrl(candidate.getResumeUrl())
                .status(candidate.getStatus())
                .createdAt(candidate.getCreatedAt())
                .updatedAt(candidate.getUpdatedAt())
                .build();
    }
}
