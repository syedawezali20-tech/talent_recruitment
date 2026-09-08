package com.example.talentrecruitment.config;

import com.example.talentrecruitment.security.GoogleOAuth2FailureHandler;
import com.example.talentrecruitment.security.GoogleOAuth2SuccessHandler;
import com.example.talentrecruitment.security.JwtAuthenticationFilter;

import lombok.RequiredArgsConstructor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;

import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    private final UserDetailsService userDetailsService;

    private final GoogleOAuth2SuccessHandler googleOAuth2SuccessHandler;

    private final GoogleOAuth2FailureHandler googleOAuth2FailureHandler;

    private final PasswordEncoder passwordEncoder;


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http)
            throws Exception {

        http
                // CORS
                .cors(cors ->
                        cors.configurationSource(corsConfigurationSource())
                )

                // Disable CSRF because JWT is used
                .csrf(csrf -> csrf.disable())

                // Stateless JWT authentication
                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        )
                )

                // Authorization rules
                .authorizeHttpRequests(auth -> auth

                        // Swagger
                        .requestMatchers(
                                "/swagger-ui.html",
                                "/swagger-ui/**",
                                "/v3/api-docs/**"
                        ).permitAll()

                        // Authentication APIs
                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/auth/register",
                                "/api/auth/login",
                                "/api/auth/candidate/register",
                                "/api/auth/forgot-password",
                                "/api/auth/reset-password",
                                "/api/auth/logout",
                                "/api/auth/refresh"
                        ).permitAll()

                        // Google OAuth2
                        .requestMatchers(
                                "/oauth2/**",
                                "/login/oauth2/**"
                        ).permitAll()

                        // Actuator health endpoint
                        .requestMatchers(
                                "/actuator/health"
                        ).permitAll()

                        // Candidate and Job GET APIs
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/candidates/**",
                                "/api/jobs/**"
                        ).authenticated()

                        // Candidate and Job POST APIs
                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/candidates/**",
                                "/api/jobs/**"
                        ).authenticated()

                        // Candidate and Job PUT APIs
                        .requestMatchers(
                                HttpMethod.PUT,
                                "/api/candidates/**",
                                "/api/jobs/**"
                        ).authenticated()

                        // Candidate and Job DELETE APIs
                        .requestMatchers(
                                HttpMethod.DELETE,
                                "/api/candidates/**",
                                "/api/jobs/**"
                        ).authenticated()

                        // Everything else
                        .anyRequest().authenticated()
                )

                // Authentication provider
                .authenticationProvider(authenticationProvider())

                // JWT filter
                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                )

                // Google OAuth2
                .oauth2Login(oauth2 ->
                        oauth2
                                .successHandler(
                                        googleOAuth2SuccessHandler
                                )
                                .failureHandler(
                                        googleOAuth2FailureHandler
                                )
                );

        return http.build();
    }


    /**
     * CORS configuration
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration configuration =
                new CorsConfiguration();

        configuration.setAllowedOrigins(
                List.of(
                        "http://localhost:3000",
                        "http://192.168.30.11:3000"
                )
        );

        configuration.setAllowedMethods(
                List.of(
                        "GET",
                        "POST",
                        "PUT",
                        "DELETE",
                        "OPTIONS"
                )
        );

        configuration.setAllowedHeaders(
                List.of("*")
        );

        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration(
                "/**",
                configuration
        );

        return source;
    }


    @Bean
    public AuthenticationProvider authenticationProvider() {

        DaoAuthenticationProvider authProvider =
                new DaoAuthenticationProvider();

        authProvider.setUserDetailsService(
                userDetailsService
        );

        authProvider.setPasswordEncoder(
                passwordEncoder
        );

        return authProvider;
    }


    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config
    ) throws Exception {

        return config.getAuthenticationManager();
    }
}