package com.example.talentrecruitment.job.dto;

import com.example.talentrecruitment.job.entity.EmploymentType;
import com.example.talentrecruitment.job.entity.JobStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Request model used to create or update a job")
public class JobRequest {

    @Schema(
            description = "Job title",
            example = "Java Backend Developer"
    )
    @NotBlank(message = "Title is required")
    private String title;

    @Schema(
            description = "Department for the job",
            example = "IT"
    )
    @NotBlank(message = "Department is required")
    private String department;

    @Schema(
            description = "Detailed job description",
            example = "Develop and maintain Spring Boot REST APIs."
    )
    @NotBlank(message = "Description is required")
    private String description;

    @Schema(
            description = "Job location",
            example = "Hyderabad"
    )
    @NotBlank(message = "Location is required")
    private String location;

    @Schema(
            description = "Required years of experience",
            example = "2"
    )
    @Min(
            value = 0,
            message = "Experience required cannot be negative"
    )
    private Integer experienceRequired;

    @Schema(
            description = "Employment type",
            example = "FULL_TIME"
    )
    private EmploymentType employmentType;

    @Schema(
            description = "Current job status",
            example = "OPEN"
    )
    private JobStatus status;
}