package com.example.talentrecruitment.auth.service;

import com.example.talentrecruitment.auth.dto.CandidateRegisterRequest;
import com.example.talentrecruitment.auth.entity.User;
import com.example.talentrecruitment.auth.entity.UserRole;
import com.example.talentrecruitment.auth.repository.UserRepository;

import com.example.talentrecruitment.candidate.entity.Candidate;
import com.example.talentrecruitment.candidate.entity.CandidateStatus;
import com.example.talentrecruitment.candidate.repository.CandidateRepository;

import com.example.talentrecruitment.common.ApiResponse;
import com.example.talentrecruitment.common.exception.BadRequestException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CandidateAuthService {

    private final UserRepository userRepository;
    private final CandidateRepository candidateRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public ApiResponse<String> registerCandidate(
            CandidateRegisterRequest request) {

        log.info(
                "Candidate registration started for username={} email={}",
                request.getUsername(),
                request.getEmail()
        );

        // 1. Check whether username already exists
        if (userRepository.existsByUsername(request.getUsername())) {

            log.warn(
                    "Candidate registration rejected because username already exists: {}",
                    request.getUsername()
            );

            throw new BadRequestException("Username already exists");
        }

        // 2. Check whether email already exists in users table
        if (userRepository.existsByEmail(request.getEmail())) {

            log.warn(
                    "Candidate registration rejected because email already exists"
            );

            throw new BadRequestException("Email already exists");
        }

        // 3. Check whether candidate email already exists
        if (candidateRepository.existsByEmail(request.getEmail())) {

            log.warn(
                    "Candidate registration rejected because candidate email already exists"
            );

            throw new BadRequestException(
                    "Candidate with this email already exists"
            );
        }

        // 4. Create candidate login account
        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(
                        passwordEncoder.encode(request.getPassword())
                )
                .role(UserRole.CANDIDATE)
                .build();

        User savedUser = userRepository.save(user);

        log.info(
                "Candidate login account created successfully with userId={}",
                savedUser.getId()
        );

        // 5. Create candidate profile
        Candidate candidate = Candidate.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .skills(request.getSkills())
                .experience(request.getExperience())
                .resumeUrl(request.getResumeUrl())
                .status(CandidateStatus.ACTIVE)
                .build();

        Candidate savedCandidate =
                candidateRepository.save(candidate);

        log.info(
                "Candidate profile created successfully with candidateId={}",
                savedCandidate.getId()
        );

        // 6. Return successful registration response
        log.info(
                "Candidate registration completed successfully for userId={}",
                savedUser.getId()
        );

        return ApiResponse.success(
                "Candidate registered successfully",
                savedUser.getUsername()
        );
    }
}