package com.example.talentrecruitment.candidate.controller;

import com.example.talentrecruitment.candidate.dto.CandidateRequest;
import com.example.talentrecruitment.candidate.dto.CandidateResponse;
import com.example.talentrecruitment.candidate.service.CandidateService;
import com.example.talentrecruitment.common.ApiResponse;
import com.example.talentrecruitment.common.exception.BadRequestException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
@Tag(name = "Candidate", description = "Candidate management APIs")
@SecurityRequirement(name = "bearerAuth")
public class CandidateController {

    private final CandidateService candidateService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','HR','RECRUITER')")
    @Operation(
            summary = "Create candidate",
            description = "Creates a new candidate in the recruitment system."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "201",
                    description = "Candidate created successfully"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Invalid candidate data"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Authentication required"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "403",
                    description = "Access denied"
            )
    })
    public ResponseEntity<ApiResponse<CandidateResponse>> createCandidate(
            @Valid @RequestBody CandidateRequest request) {

        log.info("Creating new candidate");

        CandidateResponse response =
                candidateService.createCandidate(request);

        log.info("Candidate created successfully with id: {}",
                response.getId());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(
                        ApiResponse.success(
                                "Candidate created successfully",
                                response));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','HR','RECRUITER')")
    @Operation(
            summary = "Get candidates",
            description = "Retrieves candidates using optional filters such as name, email, phone, skill, experience and status. Supports pagination and sorting."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Candidates retrieved successfully"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Invalid filter, pagination or sorting parameters"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Authentication required"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "403",
                    description = "Access denied"
            )
    })
    public ResponseEntity<ApiResponse<Object>> getAllCandidates(

            @Parameter(
                    description = "Filter by first name",
                    example = "Alex"
            )
            @RequestParam(required = false)
            String firstName,

            @Parameter(
                    description = "Filter by last name",
                    example = "Kumar"
            )
            @RequestParam(required = false)
            String lastName,

            @Parameter(
                    description = "Filter by email",
                    example = "alex@gmail.com"
            )
            @RequestParam(required = false)
            String email,

            @Parameter(
                    description = "Filter by phone number",
                    example = "+91-9876543210"
            )
            @RequestParam(required = false)
            String phone,

            @Parameter(
                    description = "Filter by skill",
                    example = "Java"
            )
            @RequestParam(required = false)
            String skill,

            @Parameter(
                    description = "Filter by years of experience",
                    example = "3"
            )
            @RequestParam(required = false)
            Integer experience,

            @Parameter(
                    description = "Filter by candidate status",
                    example = "ACTIVE"
            )
            @RequestParam(required = false)
            String status,

            @Parameter(
                    description = "Page number starting from 0",
                    example = "0"
            )
            @RequestParam(defaultValue = "0")
            int page,

            @Parameter(
                    description = "Number of records per page",
                    example = "10"
            )
            @RequestParam(defaultValue = "10")
            int size,

            @Parameter(
                    description = "Field used for sorting",
                    example = "createdAt"
            )
            @RequestParam(defaultValue = "id")
            String sortBy,

            @Parameter(
                    description = "Sort direction: asc or desc",
                    example = "asc"
            )
            @RequestParam(defaultValue = "asc")
            String direction) {

        log.info(
                "Fetching candidates - page: {}, size: {}, sortBy: {}, direction: {}",
                page,
                size,
                sortBy,
                direction
        );

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

        if (!allowedSortFields.contains(sortBy)) {

            log.warn("Invalid sort field requested: {}", sortBy);

            throw new BadRequestException(
                    "Invalid sort field: " + sortBy);
        }

        if (!direction.equalsIgnoreCase("asc")
                && !direction.equalsIgnoreCase("desc")) {

            log.warn("Invalid sort direction requested: {}", direction);

            throw new BadRequestException(
                    "Invalid sort direction: " + direction);
        }

        Sort sort = direction.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable =
                PageRequest.of(page, size, sort);

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

        log.info(
                "Candidates retrieved successfully - count: {}, total: {}",
                result.getNumberOfElements(),
                result.getTotalElements()
        );

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
    @Operation(
            summary = "Get candidate by ID",
            description = "Retrieves a candidate using the candidate ID."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Candidate retrieved successfully"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Authentication required"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "403",
                    description = "Access denied"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Candidate not found"
            )
    })
    public ResponseEntity<ApiResponse<CandidateResponse>> getCandidateById(

            @Parameter(
                    description = "Unique candidate ID",
                    example = "1"
            )
            @PathVariable Long id) {

        log.info("Fetching candidate with id: {}", id);

        CandidateResponse response =
                candidateService.getCandidateById(id);

        log.info("Candidate retrieved successfully with id: {}", id);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Candidate retrieved successfully",
                        response));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','HR','RECRUITER')")
    @Operation(
            summary = "Update candidate",
            description = "Updates an existing candidate using the candidate ID."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Candidate updated successfully"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Invalid candidate data"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Authentication required"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "403",
                    description = "Access denied"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Candidate not found"
            )
    })
    public ResponseEntity<ApiResponse<CandidateResponse>> updateCandidate(

            @Parameter(
                    description = "Unique candidate ID",
                    example = "1"
            )
            @PathVariable Long id,

            @Valid @RequestBody CandidateRequest request) {

        log.info("Updating candidate with id: {}", id);

        CandidateResponse response =
                candidateService.updateCandidate(id, request);

        log.info("Candidate updated successfully with id: {}", id);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Candidate updated successfully",
                        response));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    @Operation(
            summary = "Delete candidate",
            description = "Deletes an existing candidate. Only ADMIN and HR users are authorized."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Candidate deleted successfully"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Authentication required"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "403",
                    description = "Access denied"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Candidate not found"
            )
    })
    public ResponseEntity<ApiResponse<Void>> deleteCandidate(

            @Parameter(
                    description = "Unique candidate ID",
                    example = "1"
            )
            @PathVariable Long id) {

        log.info("Deleting candidate with id: {}", id);

        candidateService.deleteCandidate(id);

        log.info("Candidate deleted successfully with id: {}", id);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Candidate deleted successfully",
                        null));
    }
}