package com.example.talentrecruitment.candidate.service;

import com.example.talentrecruitment.candidate.dto.CandidateRequest;
import com.example.talentrecruitment.candidate.dto.CandidateResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CandidateService {

    CandidateResponse createCandidate(CandidateRequest request);

    CandidateResponse getCandidateById(Long id);

    Page<CandidateResponse> getAllCandidates(
            String firstName,
            String lastName,
            String email,
            String phone,
            String skill,
            Integer experience,
            String status,
            Pageable pageable);

    CandidateResponse updateCandidate(Long id, CandidateRequest request);

    void deleteCandidate(Long id);
}