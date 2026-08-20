package com.example.talentrecruitment.candidate.dto;

import com.example.talentrecruitment.candidate.entity.CandidateStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Request model used to create or update a candidate")
public class CandidateRequest {

    @Schema(
            description = "Candidate first name",
            example = "Alex"
    )
    @NotBlank(message = "First name is required")
    private String firstName;

    @Schema(
            description = "Candidate last name",
            example = "Kumar"
    )
    @NotBlank(message = "Last name is required")
    private String lastName;

    @Schema(
            description = "Candidate email address",
            example = "alex@gmail.com"
    )
    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    private String email;

    @Schema(
            description = "Candidate phone number",
            example = "+91-9876543210"
    )
    @NotBlank(message = "Phone is required")
    @Pattern(
            regexp = "^[+]?[(]?[0-9]{1,4}[)]?[-\\s\\./0-9]*$",
            message = "Phone number format is invalid"
    )
    private String phone;

    @Schema(
            description = "Candidate skills",
            example = "Java, Spring Boot, PostgreSQL"
    )
    private String skills;

    @Schema(
            description = "Years of professional experience",
            example = "3"
    )
    @Min(
            value = 0,
            message = "Experience cannot be negative"
    )
    private Integer experience;

    @Schema(
            description = "URL of the candidate resume",
            example = "https://example.com/resumes/alex.pdf"
    )
    private String resumeUrl;

    @Schema(
            description = "Candidate status",
            example = "ACTIVE"
    )
    private CandidateStatus status;
}