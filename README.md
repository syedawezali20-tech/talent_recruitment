# Talent Recruitment API

A production-style Spring Boot backend for an HRMS Talent & Recruitment module.

The application provides secure REST APIs for managing candidates, jobs, recruiter authentication, candidate registration, password management, Google OAuth2 authentication, JWT-based authentication, refresh-token sessions, application health monitoring, and structured application logging.

---

## Overview

This project is the backend API for the Talent & Recruitment module of an HRMS application.

The backend is built using Spring Boot 3.3.2 and follows a layered architecture separating controllers, services, repositories, entities, DTOs, security, configuration, and exception handling.

The application uses PostgreSQL as the database and Spring Security for authentication and authorization.

---

## Features

### Authentication and Security

- JWT-based authentication and authorization
- Role-based access control
- ADMIN, HR, RECRUITER, and CANDIDATE roles
- BCrypt password hashing
- Google OAuth2 authentication
- Refresh-token based session management
- Refresh-token rotation
- Server-side refresh-session revocation
- Logout
- Logout from all sessions
- Password reset using email
- Password change
- Password reset token expiration
- Account lockout after repeated failed login attempts
- Stateless Spring Security configuration
- CORS configuration

### Candidate Management

- Candidate registration
- Candidate login account creation
- Candidate profile creation
- Candidate CRUD operations
- Candidate search
- Candidate filtering
- Candidate pagination
- Candidate status management

### Job Management

- Job CRUD operations
- Job search
- Job filtering
- Job pagination
- Department filtering
- Location filtering
- Job status filtering
- Employment type management

### Validation and Error Handling

- Bean Validation
- Password policy validation
- Global exception handling
- Standardized API responses
- JWT exception handling
- Authentication error handling
- Access denied handling
- Resource not found handling

### Monitoring and Production Readiness

- Spring Boot Actuator
- Health endpoint
- Application information endpoint
- Structured JSON logging
- Log file rotation
- Development profile
- Test profile
- Production profile
- Environment-variable based configuration
- Externalized database credentials
- Externalized JWT secret
- Externalized Google OAuth credentials
- Externalized email credentials

### Documentation and Testing

- Swagger/OpenAPI documentation
- JUnit 5
- Mockito
- Maven test lifecycle

---

# Technology Stack

- Java 17
- Spring Boot 3.3.2
- Maven
- Spring Web
- Spring Data JPA
- Hibernate
- PostgreSQL
- Spring Security
- JWT (JJWT)
- Google OAuth2
- Spring Boot Actuator
- Spring Boot Mail
- Bean Validation
- Lombok
- Springdoc OpenAPI
- Logback
- Logstash Logback Encoder
- JUnit 5
- Mockito

---

# Project Architecture

The application follows a layered architecture.

```text
Controller
    |
    v
Service
    |
    v
Repository
    |
    v
Database# Talent Recruitment API

A production-ready Spring Boot backend for the Talent & Recruitment module of an HRMS application.

The application provides APIs for authentication, authorization, candidate management, job management, password management, Google OAuth2 authentication, JWT authentication, refresh-token sessions, application monitoring, structured logging, and environment-based configuration.

---

## Table of Contents

1. Project Overview
2. Features
3. Technology Stack
4. Project Structure
5. Application Architecture
6. Authentication Flow
7. JWT Authentication
8. Refresh Token and Session Management
9. Logout
10. Password Security
11. Account Lockout
12. Forgot Password
13. Change Password
14. Google OAuth2
15. Candidate Registration
16. Candidate Management
17. Job Management
18. API Endpoints
19. Database Configuration
20. Environment Variables
21. Application Profiles
22. Development Configuration
23. Test Configuration
24. Production Configuration
25. Structured Logging
26. Log Files
27. Sensitive Information Logging Policy
28. Spring Boot Actuator
29. Swagger / OpenAPI
30. CORS Configuration
31. Running the Application
32. Running Tests
33. Building the Application
34. Frontend Integration
35. Git Workflow
36. Production Security Checklist
37. Future Enhancements
38. License

---

# 1. Project Overview

The Talent Recruitment API is the backend service for the Talent & Recruitment module of an HRMS application.

The backend is developed using Spring Boot and provides REST APIs that allow users such as candidates, recruiters, HR users, and administrators to interact with the recruitment system.

The application supports:

- User authentication
- Candidate registration
- Candidate management
- Job management
- JWT authentication
- Refresh-token sessions
- Google OAuth2 authentication
- Password reset
- Password change
- Account lockout
- Role-based authorization
- Validation
- Global exception handling
- Structured logging
- Application health monitoring
- Environment-specific configuration

---

# 2. Features

## Authentication and Security

- JWT-based authentication
- Role-based authorization
- BCrypt password hashing
- Google OAuth2 authentication
- Refresh-token authentication
- Refresh-token rotation
- Server-side refresh-session storage
- Session revocation
- Logout
- Logout from all sessions
- Forgot password
- Reset password
- Change password
- Password validation
- Failed-login tracking
- Temporary account lockout
- Stateless Spring Security configuration
- CORS configuration

## Candidate Management

- Candidate registration
- Candidate profile creation
- Candidate CRUD operations
- Candidate search
- Candidate filtering
- Candidate pagination
- Candidate status management

## Job Management

- Job creation
- Job retrieval
- Job update
- Job deletion
- Job filtering
- Job pagination
- Department filtering
- Location filtering
- Status filtering
- Employment type management

## Production Readiness

- Spring Boot Actuator
- Health monitoring
- Application information endpoint
- Structured JSON logging
- Rolling log files
- Development profile
- Test profile
- Production profile
- Environment-variable based configuration
- Externalized credentials
- Externalized JWT secret
- Externalized OAuth credentials
- Externalized email credentials

## Documentation and Testing

- Swagger/OpenAPI
- JUnit
- Mockito
- Maven
- Global exception handling
- Standardized API responses

---

# 3. Technology Stack

| Technology | Purpose |
|---|---|
| Java 17 | Programming language |
| Spring Boot 3.3.2 | Backend framework |
| Maven | Build and dependency management |
| Spring Web | REST APIs |
| Spring Data JPA | Database access |
| Hibernate | ORM |
| PostgreSQL | Database |
| Spring Security | Authentication and authorization |
| JWT | Access-token authentication |
| OAuth2 Client | Google authentication |
| Spring Boot Actuator | Monitoring and health checks |
| Spring Boot Mail | Email functionality |
| Spring Validation | Request validation |
| Lombok | Boilerplate reduction |
| Springdoc OpenAPI | Swagger documentation |
| Logback | Application logging |
| Logstash Logback Encoder | Structured JSON logging |
| JUnit | Unit testing |
| Mockito | Mocking and service testing |
| Next.js | Frontend application |

---

# 4. Project Structure

```text
talent-recruitment-api
│
├── src
│   ├── main
│   │   ├── java
│   │   │   └── com
│   │   │       └── example
│   │   │           └── talentrecruitment
│   │   │               │
│   │   │               ├── auth
│   │   │               │   ├── controller
│   │   │               │   ├── dto
│   │   │               │   ├── entity
│   │   │               │   ├── repository
│   │   │               │   └── service
│   │   │               │
│   │   │               ├── candidate
│   │   │               │   ├── controller
│   │   │               │   ├── dto
│   │   │               │   ├── entity
│   │   │               │   ├── repository
│   │   │               │   └── service
│   │   │               │
│   │   │               ├── common
│   │   │               │   ├── exception
│   │   │               │   └── ApiResponse.java
│   │   │               │
│   │   │               ├── config
│   │   │               │
│   │   │               ├── job
│   │   │               │   ├── controller
│   │   │               │   ├── dto
│   │   │               │   ├── entity
│   │   │               │   ├── repository
│   │   │               │   └── service
│   │   │               │
│   │   │               ├── security
│   │   │               │
│   │   │               └── TalentRecruitmentApplication.java
│   │   │
│   │   └── resources
│   │       ├── application.properties
│   │       ├── application-dev.properties
│   │       ├── application-test.properties
│   │       ├── application-prod.properties
│   │       └── logback-spring.xml
│   │
│   └── test
│       └── java
│
├── logs
├── target
├── pom.xml
└── README.md