package com.example.talentrecruitment.common.exception;

import com.example.talentrecruitment.common.ApiResponse;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;

import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;

import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleResourceNotFound(
            ResourceNotFoundException ex) {

        log.warn("Resource not found: {}", ex.getMessage());

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(ex.getMessage()));
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ApiResponse<Object>> handleBadRequest(
            BadRequestException ex) {

        log.warn("Bad request: {}", ex.getMessage());

        return ResponseEntity
                .badRequest()
                .body(ApiResponse.error(ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Object>> handleValidationException(
            MethodArgumentNotValidException ex) {

        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult()
                .getAllErrors()
                .forEach(error -> {

                    String fieldName =
                            error instanceof FieldError fieldError
                                    ? fieldError.getField()
                                    : error.getObjectName();

                    String message = error.getDefaultMessage();

                    errors.put(fieldName, message);
                });

        log.warn("Request validation failed for {} field(s)",
                errors.size());

        return ResponseEntity
                .badRequest()
                .body(ApiResponse.error("Validation failed: " + errors));
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiResponse<Object>> handleBadCredentials(
            BadCredentialsException ex) {

        /*
         * Do not log username/password here.
         * AuthenticationService handles authentication logging.
         */

        log.warn("Authentication failed due to invalid credentials");

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.error("Invalid username or password"));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Object>> handleAccessDenied(
            AccessDeniedException ex) {

        log.warn("Access denied");

        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(ApiResponse.error("Access denied"));
    }

    @ExceptionHandler({
            MalformedJwtException.class,
            SignatureException.class,
            ExpiredJwtException.class
    })
    public ResponseEntity<ApiResponse<Object>> handleInvalidJwt(
            Exception ex) {

        log.warn("Invalid or expired JWT received");

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.error("Invalid or expired JWT token"));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Object>> handleIllegalArgument(
            IllegalArgumentException ex) {

        log.warn("Illegal argument: {}", ex.getMessage());

        return ResponseEntity
                .badRequest()
                .body(ApiResponse.error(ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleGeneralException(
            Exception ex) {

        /*
         * Log the complete exception internally for debugging.
         *
         * Never return the internal exception details to the client.
         */
        log.error(
                "Unhandled application exception: {}",
                ex.getMessage(),
                ex
        );

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Internal server error"));
    }
}