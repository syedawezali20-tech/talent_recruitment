package com.example.talentrecruitment.auth.service;

import com.example.talentrecruitment.auth.dto.AuthResponse;
import com.example.talentrecruitment.auth.dto.LoginRequest;
import com.example.talentrecruitment.auth.dto.RegisterRequest;
import com.example.talentrecruitment.auth.dto.TokenRefreshResponse;
import com.example.talentrecruitment.auth.entity.PasswordResetToken;
import com.example.talentrecruitment.auth.entity.User;
import com.example.talentrecruitment.auth.entity.UserRole;
import com.example.talentrecruitment.auth.entity.UserSession;
import com.example.talentrecruitment.auth.repository.PasswordResetTokenRepository;
import com.example.talentrecruitment.auth.repository.UserRepository;
import com.example.talentrecruitment.auth.repository.UserSessionRepository;
import com.example.talentrecruitment.candidate.entity.Candidate;
import com.example.talentrecruitment.candidate.repository.CandidateRepository;
import com.example.talentrecruitment.common.exception.BadRequestException;
import com.example.talentrecruitment.common.exception.ResourceNotFoundException;
import com.example.talentrecruitment.security.JwtService;
import com.example.talentrecruitment.security.RefreshTokenService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationService {

    private final UserRepository userRepository;
    private final CandidateRepository candidateRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    // Forgot password dependencies
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final EmailService emailService;

    // Refresh token / session dependencies
    private final UserSessionRepository userSessionRepository;
    private final RefreshTokenService refreshTokenService;


    // ============================================================
    // REGISTER
    // ============================================================

    public User register(RegisterRequest request) {

        log.info(
                "Registration request received for username: {}",
                request.getUsername()
        );

        if (userRepository.existsByUsername(request.getUsername())) {

            log.warn(
                    "Registration failed - username already exists: {}",
                    request.getUsername()
            );

            throw new BadRequestException(
                    "Username already exists"
            );
        }

        if (userRepository.existsByEmail(request.getEmail())) {

            log.warn(
                    "Registration failed - email already exists: {}",
                    request.getEmail()
            );

            throw new BadRequestException(
                    "Email already exists"
            );
        }

        UserRole role = resolveRole(request.getRole());

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(
                        passwordEncoder.encode(
                                request.getPassword()
                        )
                )
                .role(role)
                .build();

        User savedUser = userRepository.save(user);

        log.info(
                "User account created successfully: {} with role: {}",
                savedUser.getUsername(),
                savedUser.getRole()
        );

        if (role == UserRole.CANDIDATE) {

            log.info(
                    "Creating candidate profile for: {}",
                    savedUser.getUsername()
            );

            if (candidateRepository.existsByEmail(
                    request.getEmail()
            )) {

                log.warn(
                        "Candidate registration failed - candidate email already exists: {}",
                        request.getEmail()
                );

                throw new BadRequestException(
                        "Candidate with this email already exists"
                );
            }

            validateCandidateRegistration(request);

            Candidate candidate = Candidate.builder()
                    .firstName(request.getFirstName())
                    .lastName(request.getLastName())
                    .email(request.getEmail())
                    .phone(request.getPhone())
                    .skills(request.getSkills())
                    .experience(request.getExperience())
                    .resumeUrl(request.getResumeUrl())
                    .build();

            Candidate savedCandidate =
                    candidateRepository.save(candidate);

            log.info(
                    "Candidate profile created successfully. Candidate ID: {}, Username: {}",
                    savedCandidate.getId(),
                    savedUser.getUsername()
            );
        }

        return savedUser;
    }


    // ============================================================
    // LOGIN
    // ============================================================

    public AuthResponse login(LoginRequest request) {

        String usernameOrEmail =
                request.getUsernameOrEmail();

        log.info(
                "Login attempt for: {}",
                usernameOrEmail
        );

        // --------------------------------------------------------
        // 1. Find user from database
        // --------------------------------------------------------

        User user =
                userRepository
                        .findByUsernameOrEmail(
                                usernameOrEmail
                        )
                        .orElseThrow(() ->
                                new BadRequestException(
                                        "Invalid username or password"
                                )
                        );

        // --------------------------------------------------------
        // 2. Check whether account is currently locked
        // --------------------------------------------------------

        if (user.getAccountLockedUntil() != null) {

            if (user.getAccountLockedUntil()
                    .isAfter(LocalDateTime.now())) {

                log.warn(
                        "Login blocked - account is locked: {}",
                        user.getUsername()
                );

                throw new BadRequestException(
                        "Account is temporarily locked. Please try again later."
                );
            }

            // Lock period has expired
            user.setFailedLoginAttempts(0);
            user.setAccountLockedUntil(null);

            userRepository.save(user);

            log.info(
                    "Account lock expired. Failed login attempts reset for: {}",
                    user.getUsername()
            );
        }

        // --------------------------------------------------------
        // 3. Authenticate username/email + password
        // --------------------------------------------------------

        try {

            Authentication authentication =
                    authenticationManager.authenticate(
                            new UsernamePasswordAuthenticationToken(
                                    usernameOrEmail,
                                    request.getPassword()
                            )
                    );

            if (!authentication.isAuthenticated()) {

                throw new BadCredentialsException(
                        "Authentication failed"
                );
            }

        } catch (BadCredentialsException e) {

            // ----------------------------------------------------
            // 4. Failed login - increase attempt count
            // ----------------------------------------------------

            int failedAttempts =
                    user.getFailedLoginAttempts() + 1;

            user.setFailedLoginAttempts(
                    failedAttempts
            );

            // ----------------------------------------------------
            // 5. Lock account after 5 failed attempts
            // ----------------------------------------------------

            if (failedAttempts >= 5) {

                user.setAccountLockedUntil(
                        LocalDateTime.now().plusMinutes(15)
                );

                userRepository.save(user);

                log.warn(
                        "Account locked for 15 minutes: {}",
                        user.getUsername()
                );

                throw new BadRequestException(
                        "Too many failed login attempts. Account locked for 15 minutes."
                );
            }

            // ----------------------------------------------------
            // 6. Save failed attempt count
            // ----------------------------------------------------

            userRepository.save(user);

            log.warn(
                    "Failed login attempt {} for username: {}",
                    failedAttempts,
                    user.getUsername()
            );

            throw new BadRequestException(
                    "Invalid username or password"
            );
        }

        // --------------------------------------------------------
        // 7. Successful login - reset failed attempts
        // --------------------------------------------------------

        user.setFailedLoginAttempts(0);
        user.setAccountLockedUntil(null);

        userRepository.save(user);

        // --------------------------------------------------------
        // 8. Create Spring Security UserDetails
        // --------------------------------------------------------

        UserDetails userDetails =
                org.springframework.security.core.userdetails.User
                        .withUsername(user.getUsername())
                        .password(user.getPassword())
                        .authorities(
                                "ROLE_" + user.getRole().name()
                        )
                        .build();

        // --------------------------------------------------------
        // 9. Generate access token
        // --------------------------------------------------------

        String accessToken =
                jwtService.generateToken(userDetails);

        // --------------------------------------------------------
        // 10. Generate refresh token
        // --------------------------------------------------------

        String refreshToken =
                refreshTokenService.generateRefreshToken();

        // --------------------------------------------------------
        // 11. Hash refresh token before storing
        // --------------------------------------------------------

        String refreshTokenHash =
                refreshTokenService.hashToken(
                        refreshToken
                );

        // --------------------------------------------------------
        // 12. Create database session
        // --------------------------------------------------------

        LocalDateTime createdAt =
                LocalDateTime.now();

        LocalDateTime expiresAt =
                createdAt.plusDays(7);

        UserSession userSession =
                new UserSession(
                        user,
                        refreshTokenHash,
                        createdAt,
                        expiresAt,
                        null,
                        null
                );

        // --------------------------------------------------------
        // 13. Save session in PostgreSQL
        // --------------------------------------------------------

        userSessionRepository.save(userSession);

        log.info(
                "User session created successfully for username: {}",
                user.getUsername()
        );

        log.info(
                "User logged in successfully: {} with role: {}",
                user.getUsername(),
                user.getRole()
        );

        // --------------------------------------------------------
        // 14. Return access token + refresh token
        // --------------------------------------------------------

        return AuthResponse.builder()
                .token(accessToken)
                .refreshToken(refreshToken)
                .username(user.getUsername())
                .role(user.getRole().name())
                .build();
    }


    // ============================================================
    // REFRESH ACCESS TOKEN
    // ============================================================

    @Transactional
    public TokenRefreshResponse refreshAccessToken(
            String refreshToken
    ) {

        if (refreshToken == null ||
                refreshToken.isBlank()) {

            throw new BadRequestException(
                    "Refresh token is required"
            );
        }

        // --------------------------------------------------------
        // 1. Hash refresh token received from client
        // --------------------------------------------------------

        String tokenHash =
                refreshTokenService.hashToken(
                        refreshToken
                );

        // --------------------------------------------------------
        // 2. Find matching session
        // --------------------------------------------------------

        UserSession session =
                userSessionRepository
                        .findByTokenHash(tokenHash)
                        .orElseThrow(() ->
                                new BadRequestException(
                                        "Invalid refresh token"
                                )
                        );

        // --------------------------------------------------------
        // 3. Check whether session was revoked
        // --------------------------------------------------------

        if (session.isRevoked()) {

            throw new BadRequestException(
                    "Refresh token has been revoked"
            );
        }

        // --------------------------------------------------------
        // 4. Check whether refresh token expired
        // --------------------------------------------------------

        if (session.getExpiresAt()
                .isBefore(LocalDateTime.now())) {

            throw new BadRequestException(
                    "Refresh token has expired"
            );
        }

        // --------------------------------------------------------
        // 5. Get user from session
        // --------------------------------------------------------

        User user =
                session.getUser();

        // --------------------------------------------------------
        // 6. Create Spring Security UserDetails
        // --------------------------------------------------------

        UserDetails userDetails =
                org.springframework.security.core.userdetails.User
                        .withUsername(user.getUsername())
                        .password(user.getPassword())
                        .authorities(
                                "ROLE_" + user.getRole().name()
                        )
                        .build();

        // --------------------------------------------------------
        // 7. Generate new access token
        // --------------------------------------------------------

        String newAccessToken =
                jwtService.generateToken(
                        userDetails
                );

        // --------------------------------------------------------
        // 8. Revoke old refresh token
        // --------------------------------------------------------

        session.setRevoked(true);

        session.setLastUsedAt(
                LocalDateTime.now()
        );

        userSessionRepository.save(session);

        // --------------------------------------------------------
        // 9. Generate new refresh token
        // --------------------------------------------------------

        String newRefreshToken =
                refreshTokenService.generateRefreshToken();

        // --------------------------------------------------------
        // 10. Hash new refresh token
        // --------------------------------------------------------

        String newRefreshTokenHash =
                refreshTokenService.hashToken(
                        newRefreshToken
                );

        // --------------------------------------------------------
        // 11. Create new session
        // --------------------------------------------------------

        LocalDateTime newCreatedAt =
                LocalDateTime.now();

        LocalDateTime newExpiresAt =
                newCreatedAt.plusDays(7);

        UserSession newSession =
                new UserSession(
                        user,
                        newRefreshTokenHash,
                        newCreatedAt,
                        newExpiresAt,
                        session.getIpAddress(),
                        session.getUserAgent()
                );

        // --------------------------------------------------------
        // 12. Save new session
        // --------------------------------------------------------

        userSessionRepository.save(newSession);

        // --------------------------------------------------------
        // 13. Return new access token + refresh token
        // --------------------------------------------------------

        return TokenRefreshResponse.builder()
                .token(newAccessToken)
                .refreshToken(newRefreshToken)
                .build();
    }


    // ============================================================
    // LOGOUT
    // ============================================================

    @Transactional
    public void logout(String refreshToken) {

        if (refreshToken == null ||
                refreshToken.isBlank()) {

            return;
        }

        String tokenHash =
                refreshTokenService.hashToken(
                        refreshToken
                );

        userSessionRepository
                .findByTokenHash(tokenHash)
                .ifPresent(session -> {

                    session.setRevoked(true);

                    session.setLastUsedAt(
                            LocalDateTime.now()
                    );

                    userSessionRepository.save(
                            session
                    );
                });

        log.info(
                "User session logged out successfully"
        );
    }


    // ============================================================
    // LOGOUT ALL SESSIONS
    // ============================================================

    @Transactional
    public void logoutAll(User user) {

        List<UserSession> sessions =
                userSessionRepository
                        .findByUserAndRevokedFalse(user);

        for (UserSession session : sessions) {

            session.setRevoked(true);
        }

        userSessionRepository.saveAll(sessions);

        log.info(
                "All sessions revoked for username: {}",
                user.getUsername()
        );
    }


    // ============================================================
    // GET USER BY USERNAME
    // ============================================================

    public User getUserByUsername(
            String username
    ) {

        return userRepository
                .findByUsername(username)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found"
                        )
                );
    }


    // ============================================================
    // FORGOT PASSWORD
    // ============================================================

    public boolean forgotPassword(String email) {

        log.info(
                "Password reset request received for email: {}",
                email
        );

        User user =
                userRepository
                        .findByEmail(email)
                        .orElse(null);

        // --------------------------------------------------------
        // Email does not exist
        // --------------------------------------------------------

        if (user == null) {

            log.warn(
                    "Password reset requested for unregistered email: {}",
                    email
            );

            return false;
        }

        // --------------------------------------------------------
        // Generate reset token
        // --------------------------------------------------------

        String token =
                UUID.randomUUID().toString();

        // --------------------------------------------------------
        // Token valid for 30 minutes
        // --------------------------------------------------------

        LocalDateTime expiryDate =
                LocalDateTime.now().plusMinutes(30);

        // --------------------------------------------------------
        // Create password reset token entity
        // --------------------------------------------------------

        PasswordResetToken passwordResetToken =
                new PasswordResetToken(
                        token,
                        user,
                        expiryDate
                );

        // --------------------------------------------------------
        // Save token
        // --------------------------------------------------------

        passwordResetTokenRepository.save(
                passwordResetToken
        );

        // --------------------------------------------------------
        // Create frontend reset URL
        // --------------------------------------------------------

        String resetLink =
                "http://localhost:3000/reset-password?token="
                        + token;

        // --------------------------------------------------------
        // Send email
        // --------------------------------------------------------

        emailService.sendPasswordResetEmail(
                user.getEmail(),
                resetLink
        );

        log.info(
                "Password reset email sent successfully to: {}",
                email
        );

        return true;
    }


    // ============================================================
    // RESET PASSWORD
    // ============================================================

    @Transactional
    public void resetPassword(
            String token,
            String newPassword
    ) {

        log.info(
                "Password reset attempt received"
        );

        PasswordResetToken passwordResetToken =
                passwordResetTokenRepository
                        .findByToken(token)
                        .orElseThrow(() ->
                                new BadRequestException(
                                        "Invalid password reset token"
                                )
                        );

        // --------------------------------------------------------
        // Check if token already used
        // --------------------------------------------------------

        if (passwordResetToken.isUsed()) {

            throw new BadRequestException(
                    "Password reset token has already been used"
            );
        }

        // --------------------------------------------------------
        // Check expiry
        // --------------------------------------------------------

        if (passwordResetToken
                .getExpiryDate()
                .isBefore(LocalDateTime.now())) {

            throw new BadRequestException(
                    "Password reset token has expired"
            );
        }

        // --------------------------------------------------------
        // Get user
        // --------------------------------------------------------

        User user =
                passwordResetToken.getUser();

        // --------------------------------------------------------
        // Encode new password
        // --------------------------------------------------------

        user.setPassword(
                passwordEncoder.encode(
                        newPassword
                )
        );

        // --------------------------------------------------------
        // Save user
        // --------------------------------------------------------

        userRepository.save(user);

        // --------------------------------------------------------
        // Mark reset token as used
        // --------------------------------------------------------

        passwordResetToken.setUsed(true);

        passwordResetTokenRepository.save(
                passwordResetToken
        );

        // --------------------------------------------------------
        // Revoke all active sessions
        // --------------------------------------------------------

        logoutAll(user);

        log.info(
                "Password reset successfully completed for username: {}",
                user.getUsername()
        );
    }


    // ============================================================
    // CHANGE PASSWORD
    // ============================================================

    @Transactional
    public void changePassword(
            String username,
            String currentPassword,
            String newPassword
    ) {

        log.info(
                "Password change request received for username: {}",
                username
        );

        // --------------------------------------------------------
        // Find current user
        // --------------------------------------------------------

        User user =
                userRepository
                        .findByUsername(username)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "User not found"
                                )
                        );

        // --------------------------------------------------------
        // Verify current password
        // --------------------------------------------------------

        if (!passwordEncoder.matches(
                currentPassword,
                user.getPassword()
        )) {

            log.warn(
                    "Password change failed - incorrect current password for username: {}",
                    username
            );

            throw new BadRequestException(
                    "Current password is incorrect"
            );
        }

        // --------------------------------------------------------
        // Prevent same password
        // --------------------------------------------------------

        if (passwordEncoder.matches(
                newPassword,
                user.getPassword()
        )) {

            throw new BadRequestException(
                    "New password must be different from the current password"
            );
        }

        // --------------------------------------------------------
        // Encode new password
        // --------------------------------------------------------

        String encodedPassword =
                passwordEncoder.encode(
                        newPassword
                );

        // --------------------------------------------------------
        // Save new password
        // --------------------------------------------------------

        user.setPassword(encodedPassword);

        userRepository.save(user);

        // --------------------------------------------------------
        // Revoke all existing sessions
        // --------------------------------------------------------

        logoutAll(user);

        log.info(
                "Password changed successfully for username: {}",
                username
        );
    }


    // ============================================================
    // ROLE RESOLUTION
    // ============================================================

    private UserRole resolveRole(
            String roleText
    ) {

        if (roleText == null ||
                roleText.isBlank()) {

            return UserRole.RECRUITER;
        }

        try {

            return UserRole.valueOf(
                    roleText.trim().toUpperCase()
            );

        } catch (IllegalArgumentException e) {

            throw new BadRequestException(
                    "Invalid role. Allowed roles: ADMIN, HR, RECRUITER, CANDIDATE"
            );
        }
    }


    // ============================================================
    // CANDIDATE VALIDATION
    // ============================================================

    private void validateCandidateRegistration(
            RegisterRequest request
    ) {

        if (request.getFirstName() == null ||
                request.getFirstName().isBlank()) {

            throw new BadRequestException(
                    "First name is required for candidate registration"
            );
        }

        if (request.getLastName() == null ||
                request.getLastName().isBlank()) {

            throw new BadRequestException(
                    "Last name is required for candidate registration"
            );
        }

        if (request.getPhone() == null ||
                request.getPhone().isBlank()) {

            throw new BadRequestException(
                    "Phone is required for candidate registration"
            );
        }

        if (request.getExperience() == null ||
                request.getExperience() < 0) {

            throw new BadRequestException(
                    "Experience must be 0 or greater"
            );
        }
    }
}