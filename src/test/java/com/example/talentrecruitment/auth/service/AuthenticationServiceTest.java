package com.example.talentrecruitment.auth.service;

import com.example.talentrecruitment.auth.dto.AuthResponse;
import com.example.talentrecruitment.auth.dto.LoginRequest;
import com.example.talentrecruitment.auth.entity.User;
import com.example.talentrecruitment.auth.entity.UserRole;
import com.example.talentrecruitment.auth.repository.PasswordResetTokenRepository;
import com.example.talentrecruitment.auth.repository.UserRepository;
import com.example.talentrecruitment.auth.repository.UserSessionRepository;
import com.example.talentrecruitment.candidate.repository.CandidateRepository;
import com.example.talentrecruitment.common.exception.BadRequestException;
import com.example.talentrecruitment.security.JwtService;
import com.example.talentrecruitment.security.RefreshTokenService;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private CandidateRepository candidateRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private PasswordResetTokenRepository passwordResetTokenRepository;

    @Mock
    private EmailService emailService;

    @Mock
    private UserSessionRepository userSessionRepository;

    @Mock
    private RefreshTokenService refreshTokenService;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private AuthenticationService authenticationService;


    // ============================================================
    // LOGIN - VALID CREDENTIALS
    // ============================================================

    @Test
    void login_shouldReturnAuthResponse_whenCredentialsAreValid() {

        // --------------------------------------------------------
        // Arrange
        // --------------------------------------------------------

        User user = User.builder()
                .id(1L)
                .username("recruiter")
                .email("recruiter@example.com")
                .password("encodedPassword")
                .role(UserRole.RECRUITER)
                .build();

        LoginRequest loginRequest = LoginRequest.builder()
                .usernameOrEmail("recruiter")
                .password("secret123")
                .build();

        UsernamePasswordAuthenticationToken authenticated =
                new UsernamePasswordAuthenticationToken(
                        "recruiter",
                        "secret123",
                        Collections.singletonList(
                                new org.springframework.security.core.authority.SimpleGrantedAuthority(
                                        "ROLE_RECRUITER"
                                )
                        )
                );

        // --------------------------------------------------------
        // Mock authentication
        // --------------------------------------------------------

        when(
                authenticationManager.authenticate(
                        any(UsernamePasswordAuthenticationToken.class)
                )
        ).thenReturn(authenticated);

        // --------------------------------------------------------
        // Mock user lookup
        // --------------------------------------------------------

        when(
                userRepository.findByUsernameOrEmail("recruiter")
        ).thenReturn(Optional.of(user));

        // --------------------------------------------------------
        // Mock access token
        // --------------------------------------------------------

        when(
                jwtService.generateToken(any())
        ).thenReturn("sample.jwt.token");

        // --------------------------------------------------------
        // Mock refresh token
        // --------------------------------------------------------

        when(
                refreshTokenService.generateRefreshToken()
        ).thenReturn("sample.refresh.token");

        // --------------------------------------------------------
        // Mock refresh token hash
        // --------------------------------------------------------

        when(
                refreshTokenService.hashToken(
                        "sample.refresh.token"
                )
        ).thenReturn("sample.refresh.token.hash");

        // --------------------------------------------------------
        // Mock UserSession save
        // --------------------------------------------------------

        when(
                userSessionRepository.save(any())
        ).thenAnswer(
                invocation -> invocation.getArgument(0)
        );

        // --------------------------------------------------------
        // Act
        // --------------------------------------------------------

        AuthResponse response =
                authenticationService.login(loginRequest);

        // --------------------------------------------------------
        // Assert
        // --------------------------------------------------------

        assertNotNull(response);

        assertEquals(
                "sample.jwt.token",
                response.getToken()
        );

        assertEquals(
                "sample.refresh.token",
                response.getRefreshToken()
        );

        assertEquals(
                "recruiter",
                response.getUsername()
        );

        assertEquals(
                "RECRUITER",
                response.getRole()
        );
    }


    // ============================================================
    // LOGIN - INVALID CREDENTIALS
    // ============================================================

    @Test
    void login_shouldThrowBadRequest_whenCredentialsAreInvalid() {

        // --------------------------------------------------------
        // Arrange
        // --------------------------------------------------------

        LoginRequest loginRequest = LoginRequest.builder()
                .usernameOrEmail("baduser")
                .password("wrong")
                .build();

        User user = User.builder()
                .id(2L)
                .username("baduser")
                .email("baduser@example.com")
                .password("encodedPassword")
                .role(UserRole.RECRUITER)
                .build();

        // --------------------------------------------------------
        // User exists
        // --------------------------------------------------------

        when(
                userRepository.findByUsernameOrEmail("baduser")
        ).thenReturn(Optional.of(user));

        // --------------------------------------------------------
        // Authentication fails
        // --------------------------------------------------------

        when(
                authenticationManager.authenticate(
                        any(UsernamePasswordAuthenticationToken.class)
                )
        ).thenThrow(
                new BadCredentialsException(
                        "Bad credentials"
                )
        );

        // --------------------------------------------------------
        // Act + Assert
        // --------------------------------------------------------

        assertThrows(
                BadRequestException.class,
                () -> authenticationService.login(loginRequest)
        );
    }
}