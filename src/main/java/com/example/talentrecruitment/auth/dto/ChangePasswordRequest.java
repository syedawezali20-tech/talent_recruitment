package com.example.talentrecruitment.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class ChangePasswordRequest {

    @NotBlank(message = "Current password is required")
    private String currentPassword;

    @NotBlank(message = "New password is required")
    @Size(
        min = 12,
        max = 64,
        message = "Password must be between 12 and 64 characters"
    )
    @Pattern(
        regexp = "^(?=.{12,64}$)(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&]).*$",
        message = "Password must contain at least one uppercase letter, one lowercase letter, one number, and one special character"
    )
    private String newPassword;

    public ChangePasswordRequest() {
    }

    public String getCurrentPassword() {
        return currentPassword;
    }

    public void setCurrentPassword(String currentPassword) {
        this.currentPassword = currentPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}