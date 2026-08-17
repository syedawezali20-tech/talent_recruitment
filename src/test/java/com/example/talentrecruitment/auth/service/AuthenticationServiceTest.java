package com.example.talentrecruitment.auth.service;

import com.example.talentrecruitment.auth.dto.AuthResponse;
import com.example.talentrecruitment.auth.dto.LoginRequest;
import com.example.talentrecruitment.auth.entity.User;
import com.example.talentrecruitment.auth.entity.UserRole;
import com.example.talentrecruitment.auth.repository.UserRepository;
import com.example.talentrecruitment.common.exception.BadRequestException;
import com.example.talentrecruitment.security.JwtService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthenticationService authenticationService;

    @Test
    void login_shouldReturnAuthResponse_whenCredentialsAreValid() {
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

        UsernamePasswordAuthenticationToken authenticated = new UsernamePasswordAuthenticationToken(
                "recruiter",
                "secret123",
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_RECRUITER"))
        );

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authenticated);
        when(userRepository.findByUsernameOrEmail("recruiter")).thenReturn(Optional.of(user));
        when(jwtService.generateToken(any())).thenReturn("sample.jwt.token");

        AuthResponse response = authenticationService.login(loginRequest);

        assertNotNull(response);
        assertEquals("sample.jwt.token", response.getToken());
        assertEquals("recruiter", response.getUsername());
        assertEquals("RECRUITER", response.getRole());
    }

    @Test
    void login_shouldThrowBadRequest_whenCredentialsAreInvalid() {
        LoginRequest loginRequest = LoginRequest.builder()
                .usernameOrEmail("baduser")
                .password("wrong")
                .build();

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadRequestException("Invalid username or password"));

        assertThrows(BadRequestException.class, () -> authenticationService.login(loginRequest));
    }
}
