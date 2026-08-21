package com.example.talentrecruitment.job.controller;

import com.example.talentrecruitment.common.ApiResponse;
import com.example.talentrecruitment.common.exception.BadRequestException;
import com.example.talentrecruitment.job.dto.JobRequest;
import com.example.talentrecruitment.job.dto.JobResponse;
import com.example.talentrecruitment.job.service.JobService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
@RequestMapping("/api/jobs")
@RequiredArgsConstructor
@Tag(name = "Job", description = "Job management APIs")
@SecurityRequirement(name = "bearerAuth")
public class JobController {

    private final JobService jobService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','HR','RECRUITER')")
    @Operation(
            summary = "Create job",
            description = "Creates a new job in the recruitment system."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "201",
                    description = "Job created successfully"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Invalid job data"
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
    public ResponseEntity<ApiResponse<JobResponse>> createJob(
            @Valid @RequestBody JobRequest request) {

        JobResponse response = jobService.createJob(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                        "Job created successfully",
                        response));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','HR','RECRUITER')")
    @Operation(
            summary = "Get jobs",
            description = "Retrieves jobs using optional department, location and status filters with pagination and sorting."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Jobs retrieved successfully"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Invalid request parameters"
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
    public ResponseEntity<ApiResponse<Object>> getAllJobs(

            @Parameter(
                    description = "Filter jobs by department",
                    example = "IT"
            )
            @RequestParam(required = false)
            String department,

            @Parameter(
                    description = "Filter jobs by location",
                    example = "Hyderabad"
            )
            @RequestParam(required = false)
            String location,

            @Parameter(
                    description = "Filter jobs by status",
                    example = "OPEN"
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
            @RequestParam(defaultValue = "createdAt")
            String sortBy,

            @Parameter(
                    description = "Sort direction: asc or desc",
                    example = "desc"
            )
            @RequestParam(defaultValue = "desc")
            String direction
    ) {

        Set<String> allowedSortFields = Set.of(
                "id",
                "title",
                "department",
                "location",
                "experienceRequired",
                "employmentType",
                "status",
                "createdAt",
                "updatedAt"
        );

        if (!allowedSortFields.contains(sortBy)) {
            throw new BadRequestException(
                    "Invalid sort field: " + sortBy);
        }

        if (!direction.equalsIgnoreCase("asc")
                && !direction.equalsIgnoreCase("desc")) {
            throw new BadRequestException(
                    "Invalid sort direction: " + direction);
        }

        Sort sort = direction.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable =
                PageRequest.of(page, size, sort);

        Page<JobResponse> result =
                jobService.getAllJobs(
                        department,
                        location,
                        status,
                        pageable);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Jobs retrieved successfully",
                        result.getContent(),
                        result.getNumber(),
                        result.getSize(),
                        result.getTotalElements(),
                        result.getTotalPages()
                ));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','HR','RECRUITER')")
    @Operation(
            summary = "Get job by ID",
            description = "Retrieves a job using the job ID."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Job retrieved successfully"
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
                    description = "Job not found"
            )
    })
    public ResponseEntity<ApiResponse<JobResponse>> getJobById(

            @Parameter(
                    description = "Unique job ID",
                    example = "1"
            )
            @PathVariable Long id) {

        JobResponse response = jobService.getJobById(id);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Job retrieved successfully",
                        response));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','HR','RECRUITER')")
    @Operation(
            summary = "Update job",
            description = "Updates an existing job using the job ID."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Job updated successfully"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Invalid job data"
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
                    description = "Job not found"
            )
    })
    public ResponseEntity<ApiResponse<JobResponse>> updateJob(

            @Parameter(
                    description = "Unique job ID",
                    example = "1"
            )
            @PathVariable Long id,

            @Valid @RequestBody JobRequest request) {

        JobResponse response = jobService.updateJob(id, request);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Job updated successfully",
                        response));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    @Operation(
            summary = "Delete job",
            description = "Deletes an existing job. Only ADMIN and HR users are authorized."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Job deleted successfully"
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
                    description = "Job not found"
            )
    })
    public ResponseEntity<ApiResponse<Void>> deleteJob(

            @Parameter(
                    description = "Unique job ID",
                    example = "1"
            )
            @PathVariable Long id) {

        jobService.deleteJob(id);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Job deleted successfully",
                        null));
    }
}