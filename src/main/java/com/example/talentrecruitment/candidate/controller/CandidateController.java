package com.example.talentrecruitment.candidate.controller;

import com.example.talentrecruitment.candidate.dto.CandidateRequest;
import com.example.talentrecruitment.candidate.dto.CandidateResponse;
import com.example.talentrecruitment.candidate.service.CandidateService;
import com.example.talentrecruitment.common.ApiResponse;
import com.example.talentrecruitment.common.exception.BadRequestException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/api/candidates")
@RequiredArgsConstructor
@Tag(name = "Candidate", description = "Candidate management APIs")
@SecurityRequirement(name = "bearerAuth")
public class CandidateController {

    private final CandidateService candidateService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','HR','RECRUITER')")
    @Operation(summary = "Create candidate")
    public ResponseEntity<ApiResponse<CandidateResponse>> createCandidate(
            @Valid @RequestBody CandidateRequest request) {

        CandidateResponse response =
                candidateService.createCandidate(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(
                        ApiResponse.success(
                                "Candidate created successfully",
                                response));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','HR','RECRUITER')")
    @Operation(
            summary = "Get candidates with dynamic filters, sorting and pagination",
            description = "Search candidates using optional filters such as name, email, phone, skill, experience and status. Supports pagination and sorting."
    )
    public ResponseEntity<ApiResponse<Object>> getAllCandidates(

            @RequestParam(required = false)
            String firstName,

            @RequestParam(required = false)
            String lastName,

            @RequestParam(required = false)
            String email,

            @RequestParam(required = false)
            String phone,

            @RequestParam(required = false)
            String skill,

            @RequestParam(required = false)
            Integer experience,

            @RequestParam(required = false)
            String status,

            @RequestParam(defaultValue = "0")
            int page,

            @RequestParam(defaultValue = "10")
            int size,

            @RequestParam(defaultValue = "id")
            String sortBy,

            @RequestParam(defaultValue = "asc")
            String direction) {

        // Allowed fields for sorting
        Set<String> allowedSortFields = Set.of(
                "id",
                "firstName",
                "lastName",
                "email",
                "experience",
                "status",
                "createdAt",
                "updatedAt"
        );

        // Validate sort field
        if (!allowedSortFields.contains(sortBy)) {
            throw new BadRequestException(
                    "Invalid sort field: " + sortBy);
        }

        // Validate sort direction
        if (!direction.equalsIgnoreCase("asc")
                && !direction.equalsIgnoreCase("desc")) {

            throw new BadRequestException(
                    "Invalid sort direction: " + direction);
        }

        // Create sorting
        Sort sort = direction.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        // Create pagination
        Pageable pageable =
                PageRequest.of(page, size, sort);

        // Call service
        Page<CandidateResponse> result =
                candidateService.getAllCandidates(
                        firstName,
                        lastName,
                        email,
                        phone,
                        skill,
                        experience,
                        status,
                        pageable
                );

        // Return standardized response
        return ResponseEntity.ok(
                ApiResponse.success(
                        "Candidates retrieved successfully",
                        result.getContent(),
                        result.getNumber(),
                        result.getSize(),
                        result.getTotalElements(),
                        result.getTotalPages()
                )
        );
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','HR','RECRUITER')")
    @Operation(summary = "Get candidate by id")
    public ResponseEntity<ApiResponse<CandidateResponse>> getCandidateById(
            @PathVariable Long id) {

        CandidateResponse response =
                candidateService.getCandidateById(id);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Candidate retrieved successfully",
                        response));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','HR','RECRUITER')")
    @Operation(summary = "Update candidate")
    public ResponseEntity<ApiResponse<CandidateResponse>> updateCandidate(
            @PathVariable Long id,
            @Valid @RequestBody CandidateRequest request) {

        CandidateResponse response =
                candidateService.updateCandidate(id, request);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Candidate updated successfully",
                        response));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    @Operation(summary = "Delete candidate")
    public ResponseEntity<ApiResponse<Void>> deleteCandidate(
            @PathVariable Long id) {

        candidateService.deleteCandidate(id);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Candidate deleted successfully",
                        null));
    }
}