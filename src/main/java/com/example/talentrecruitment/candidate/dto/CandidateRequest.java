package com.example.talentrecruitment.candidate.dto;

import com.example.talentrecruitment.candidate.entity.CandidateStatus;
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
public class CandidateRequest {

    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    private String email;

    @NotBlank(message = "Phone is required")
    @Pattern(regexp = "^[+]?[(]?[0-9]{1,4}[)]?[-\\s\\./0-9]*$", message = "Phone number format is invalid")
    private String phone;

    private String skills;

    @Min(value = 0, message = "Experience cannot be negative")
    private Integer experience;

    private String resumeUrl;

    private CandidateStatus status;
}
