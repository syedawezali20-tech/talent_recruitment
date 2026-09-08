package com.example.talentrecruitment.auth.controller;

import com.example.talentrecruitment.auth.dto.AuthResponse;
import com.example.talentrecruitment.auth.dto.ChangePasswordRequest;
import com.example.talentrecruitment.auth.dto.ForgotPasswordRequest;
import com.example.talentrecruitment.auth.dto.LoginRequest;
import com.example.talentrecruitment.auth.dto.RefreshTokenRequest;
import com.example.talentrecruitment.auth.dto.RegisterRequest;
import com.example.talentrecruitment.auth.dto.ResetPasswordRequest;
import com.example.talentrecruitment.auth.dto.TokenRefreshResponse;
import com.example.talentrecruitment.auth.entity.User;
import com.example.talentrecruitment.auth.service.AuthenticationService;
import com.example.talentrecruitment.common.ApiResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(
        name = "Authentication",
        description = "User registration, login, password reset, password change and session APIs"
)
public class AuthenticationController {


    private final AuthenticationService authenticationService;


    // ============================================================
    // REGISTER
    // ============================================================

    @PostMapping("/register")
    @Operation(
            summary = "Register a new user",
            description = "Creates a new recruiter/admin/hr account"
    )
    public ResponseEntity<ApiResponse<String>> register(
            @Valid @RequestBody RegisterRequest request) {

        User user =
                authenticationService.register(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        ApiResponse.success(
                                "User registered successfully",
                                user.getUsername()
                        )
                );
    }


    // ============================================================
    // LOGIN
    // ============================================================

    @PostMapping("/login")
    @Operation(
            summary = "Login user",
            description = "Authenticates a user and returns access and refresh tokens"
    )
    public ResponseEntity<ApiResponse<AuthResponse>> login(
            @Valid @RequestBody LoginRequest request) {

        AuthResponse response =
                authenticationService.login(request);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Login successful",
                        response
                )
        );
    }


    // ============================================================
    // REFRESH ACCESS TOKEN
    // ============================================================

    @PostMapping("/refresh")
    @Operation(
            summary = "Refresh access token",
            description = "Creates a new access token using a valid refresh token"
    )
    public ResponseEntity<ApiResponse<TokenRefreshResponse>> refreshToken(
            @Valid @RequestBody RefreshTokenRequest request) {

        TokenRefreshResponse response =
                authenticationService.refreshAccessToken(
                        request.getRefreshToken()
                );

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Token refreshed successfully",
                        response
                )
        );
    }


    // ============================================================
    // LOGOUT
    // ============================================================

    @PostMapping("/logout")
    @Operation(
            summary = "Logout current session",
            description = "Revokes the refresh-token-backed application session"
    )
    public ResponseEntity<ApiResponse<String>> logout(
            @Valid @RequestBody RefreshTokenRequest request) {

        authenticationService.logout(
                request.getRefreshToken()
        );

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Logged out successfully",
                        null
                )
        );
    }


    // ============================================================
    // LOGOUT ALL SESSIONS
    // ============================================================

    @PostMapping("/logout-all")
    @Operation(
            summary = "Logout all sessions",
            description = "Revokes all active sessions of the authenticated user"
    )
    public ResponseEntity<ApiResponse<String>> logoutAll(
            Authentication authentication) {

        String username =
                authentication.getName();

        User user =
                authenticationService.getUserByUsername(
                        username
                );

        authenticationService.logoutAll(user);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "All sessions logged out successfully",
                        null
                )
        );
    }


    // ============================================================
    // FORGOT PASSWORD
    // ============================================================

    @PostMapping("/forgot-password")
    @Operation(
            summary = "Forgot password",
            description = "Sends a password reset link to the user's email"
    )
    public ResponseEntity<ApiResponse<String>> forgotPassword(
            @Valid @RequestBody ForgotPasswordRequest request) {

        boolean emailRegistered =
                authenticationService.forgotPassword(
                        request.getEmail()
                );

        if (!emailRegistered) {

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(
                            ApiResponse.success(
                                    "Email is not registered.",
                                    null
                            )
                    );
        }

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Password reset link has been sent to your email.",
                        null
                )
        );
    }


    // ============================================================
    // RESET PASSWORD
    // ============================================================

    @PostMapping("/reset-password")
    @Operation(
            summary = "Reset password",
            description = "Resets the user's password using a valid reset token"
    )
    public ResponseEntity<ApiResponse<String>> resetPassword(
            @Valid @RequestBody ResetPasswordRequest request) {

        authenticationService.resetPassword(
                request.getToken(),
                request.getNewPassword()
        );

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Password updated successfully",
                        null
                )
        );
    }


    // ============================================================
    // CHANGE PASSWORD
    // ============================================================

    @PostMapping("/change-password")
    @Operation(
            summary = "Change password",
            description = "Changes the password of the currently authenticated user"
    )
    public ResponseEntity<ApiResponse<String>> changePassword(
            @Valid @RequestBody ChangePasswordRequest request,
            Authentication authentication) {

        String username =
                authentication.getName();

        authenticationService.changePassword(
                username,
                request.getCurrentPassword(),
                request.getNewPassword()
        );

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Password changed successfully",
                        null
                )
        );
    }
}