package com.example.talentrecruitment.security;

import com.example.talentrecruitment.auth.entity.User;
import com.example.talentrecruitment.auth.entity.UserSession;
import com.example.talentrecruitment.auth.repository.UserSessionRepository;
import com.example.talentrecruitment.auth.service.GoogleOAuth2Service;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import lombok.RequiredArgsConstructor;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class GoogleOAuth2SuccessHandler
        implements AuthenticationSuccessHandler {

    private final GoogleOAuth2Service googleOAuth2Service;

    private final JwtService jwtService;

    private final UserDetailsService userDetailsService;

    private final RefreshTokenService refreshTokenService;

    private final UserSessionRepository userSessionRepository;

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException, ServletException {

        // ============================================================
        // 1. GET AUTHENTICATED GOOGLE USER
        // ============================================================

        OAuth2User oauthUser =
                (OAuth2User) authentication.getPrincipal();


        // ============================================================
        // 2. FIND / LINK / CREATE LOCAL USER
        // ============================================================

        User user =
                googleOAuth2Service.processGoogleUser(
                        oauthUser
                );


        // ============================================================
        // 3. LOAD OUR APPLICATION USER DETAILS
        // ============================================================

        UserDetails userDetails =
                userDetailsService.loadUserByUsername(
                        user.getUsername()
                );


        // ============================================================
        // 4. GENERATE ACCESS TOKEN
        // ============================================================

        String accessToken =
                jwtService.generateToken(userDetails);


        // ============================================================
        // 5. GENERATE REFRESH TOKEN
        // ============================================================

        String refreshToken =
                refreshTokenService.generateRefreshToken();


        // ============================================================
        // 6. HASH REFRESH TOKEN
        // ============================================================

        String refreshTokenHash =
                refreshTokenService.hashToken(
                        refreshToken
                );


        // ============================================================
        // 7. CREATE APPLICATION SESSION
        // ============================================================

        LocalDateTime createdAt =
                LocalDateTime.now();

        LocalDateTime expiresAt =
                createdAt.plusDays(7);

        UserSession session =
                new UserSession(
                        user,
                        refreshTokenHash,
                        createdAt,
                        expiresAt,
                        request.getRemoteAddr(),
                        request.getHeader("User-Agent")
                );


        // ============================================================
        // 8. SAVE APPLICATION SESSION
        // ============================================================

        userSessionRepository.save(session);


        // ============================================================
        // 9. URL ENCODE VALUES
        // ============================================================

        String encodedAccessToken =
                URLEncoder.encode(
                        accessToken,
                        StandardCharsets.UTF_8
                );

        String encodedRefreshToken =
                URLEncoder.encode(
                        refreshToken,
                        StandardCharsets.UTF_8
                );

        String encodedUsername =
                URLEncoder.encode(
                        user.getUsername(),
                        StandardCharsets.UTF_8
                );

        String encodedRole =
                URLEncoder.encode(
                        user.getRole().name(),
                        StandardCharsets.UTF_8
                );


        // ============================================================
        // 10. BUILD NEXT.JS REDIRECT URL
        //
        // DEMO ONLY
        //
        // We are using query parameters temporarily so the
        // Next.js success page can reliably read the values.
        //
        // Production should use a safer server-side/cookie-based
        // authentication exchange.
        // ============================================================

        String redirectUrl =
                "http://localhost:3000/oauth2/success"
                        + "?token="
                        + encodedAccessToken
                        + "&refreshToken="
                        + encodedRefreshToken
                        + "&username="
                        + encodedUsername
                        + "&role="
                        + encodedRole;


        // ============================================================
        // 11. DEBUG INFORMATION
        // ============================================================

        System.out.println(
                "========================================"
        );

        System.out.println(
                "GOOGLE OAUTH SUCCESS"
        );

        System.out.println(
                "Access Token generated: "
                        + !accessToken.isBlank()
        );

        System.out.println(
                "Refresh Token generated: "
                        + !refreshToken.isBlank()
        );

        System.out.println(
                "Username: "
                        + user.getUsername()
        );

        System.out.println(
                "Role: "
                        + user.getRole().name()
        );

        System.out.println(
                "Refresh session saved successfully"
        );

        System.out.println(
                "Redirect URL:"
        );

        System.out.println(
                redirectUrl
        );

        System.out.println(
                "========================================"
        );


        // ============================================================
        // 12. SEND REDIRECT TO NEXT.JS
        // ============================================================

        response.sendRedirect(
                redirectUrl
        );
    }
}