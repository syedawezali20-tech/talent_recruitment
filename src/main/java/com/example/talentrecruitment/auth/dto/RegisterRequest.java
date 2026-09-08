package com.example.talentrecruitment.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterRequest {

    // =========================
    // USER ACCOUNT DETAILS
    // =========================

    @NotBlank(message = "Username is required")
    @Size(
        min = 3,
        max = 50,
        message = "Username must be between 3 and 50 characters"
    )
    private String username;

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(
        min = 12,
        max = 64,
        message = "Password must be between 12 and 64 characters"
    )
    @Pattern(
        regexp = "^(?=.{12,64}$)(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&]).*$",
        message = "Password must contain at least one uppercase letter, one lowercase letter, one number, and one special character"
    )
    private String password;

    /*
     * Used for existing ADMIN / HR / RECRUITER registration.
     *
     * For candidate registration, send:
     *
     * "role": "CANDIDATE"
     */
    private String role;


    // =========================
    // CANDIDATE DETAILS
    // =========================

    private String firstName;

    private String lastName;

    private String phone;

    private String skills;

    private Integer experience;

    private String resumeUrl;
}