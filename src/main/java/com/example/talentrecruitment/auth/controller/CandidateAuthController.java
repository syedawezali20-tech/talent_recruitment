package com.example.talentrecruitment.auth.controller;

import com.example.talentrecruitment.auth.dto.CandidateRegisterRequest;
import com.example.talentrecruitment.auth.service.CandidateAuthService;
import com.example.talentrecruitment.common.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth/candidate")
@RequiredArgsConstructor
public class CandidateAuthController {

    private final CandidateAuthService candidateAuthService;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<String> registerCandidate(
            @Valid @RequestBody CandidateRegisterRequest request) {

        return candidateAuthService.registerCandidate(request);
    }
}