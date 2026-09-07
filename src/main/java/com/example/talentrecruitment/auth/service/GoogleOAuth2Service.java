package com.example.talentrecruitment.auth.service;

import com.example.talentrecruitment.auth.entity.User;
import com.example.talentrecruitment.auth.entity.UserRole;
import com.example.talentrecruitment.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class GoogleOAuth2Service {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public User processGoogleUser(OAuth2User oauthUser) {

        String googleId = oauthUser.getAttribute("sub");
        String email = oauthUser.getAttribute("email");

        if (googleId == null || email == null) {
            throw new IllegalArgumentException(
                    "Google did not provide required user information"
            );
        }

        log.info("Processing Google login for email: {}", email);

        // 1. Check Google ID
        User user = userRepository
                .findByGoogleId(googleId)
                .orElse(null);

        if (user != null) {

            log.info("Existing Google user found: {}", email);

            return user;
        }

        // 2. Check existing email
        user = userRepository
                .findByEmail(email)
                .orElse(null);

        if (user != null) {

            log.info("Existing local user found. Linking Google account.");

            user.setGoogleId(googleId);
            user.setAuthProvider("GOOGLE");

            return userRepository.save(user);
        }

        // 3. Create new user
        String username = generateUsername(email);

        User newUser = User.builder()
                .username(username)
                .email(email)
                .password(
                        passwordEncoder.encode(
                                UUID.randomUUID().toString()
                        )
                )
                .role(UserRole.CANDIDATE)
                .googleId(googleId)
                .authProvider("GOOGLE")
                .build();

        User savedUser = userRepository.save(newUser);

        log.info(
                "New Google user created: {} with role CANDIDATE",
                email
        );

        return savedUser;
    }

    private String generateUsername(String email) {

        String baseUsername =
                email.substring(0, email.indexOf("@"))
                        .replaceAll("[^a-zA-Z0-9]", "");

        if (baseUsername.isBlank()) {
            baseUsername = "googleuser";
        }

        String username = baseUsername;
        int counter = 1;

        while (userRepository.existsByUsername(username)) {
            username = baseUsername + counter;
            counter++;
        }

        return username;
    }
}