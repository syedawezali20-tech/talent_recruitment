mvn clean test# Talent Recruitment API

A production-style Spring Boot backend for an HRMS Talent & Recruitment module.

## Overview
This project provides a clean, secure REST API for managing candidates, jobs, and recruiter authentication using Spring Boot 3, Spring Security, JWT, and MySQL.

## Features
- JWT-based authentication and authorization
- Role-based access control for ADMIN, HR, and RECRUITER
- Candidate CRUD with search and pagination
- Job CRUD with search and pagination
- Bean Validation
- Global exception handling
- Swagger/OpenAPI documentation
- MySQL integration with Spring Data JPA
- JUnit 5 and Mockito tests

## Technology Stack
- Java 17
- Spring Boot 3.3.2
- Maven
- Spring Web
- Spring Data JPA
- Hibernate
- MySQL
- Spring Security
- JWT (jjwt)
- Bean Validation
- Lombok
- Springdoc OpenAPI
- JUnit 5
- Mockito

## Project Architecture
The application follows a layered architecture:
- controller: REST endpoints
- service: business logic
- service.impl: concrete implementations
- repository: persistence logic
- entity: JPA persistence entities
- dto: request/response payloads
- security: JWT configuration and authentication handling
- exception: global error handling
- config: OpenAPI and security configuration

## Package Structure
```text
com.example.talentrecruitment
├── auth
│   ├── controller
│   ├── dto
│   ├── entity
│   ├── repository
│   └── service
├── candidate
│   ├── controller
│   ├── dto
│   ├── entity
│   ├── repository
│   └── service
├── common
│   ├── exception
│   └── ApiResponse.java
├── config
├── job
│   ├── controller
│   ├── dto
│   ├── entity
│   ├── repository
│   └── service
├── security
├── TalentRecruitmentApplication.java
└── resources
    └── application.properties
```

## Database Configuration
Create a MySQL database named `talent_recruitment_db`.

Example:
```sql
CREATE DATABASE talent_recruitment_db;
```

Set environment variables before running the app:
```bash
export DB_URL=jdbc:mysql://localhost:3306/talent_recruitment_db?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
export DB_USERNAME=root
export DB_PASSWORD=password
export JWT_SECRET=your-super-secret-key-should-be-long-enough
```

Application configuration is in `src/main/resources/application.properties`.

## How to Run
1. Ensure MySQL is running.
2. Create the database.
3. Set the required environment variables.
4. Run:

```bash
mvn clean install
mvn spring-boot:run
```

## Authentication Flow
1. Register a user using `POST /api/auth/register`.
2. Login using `POST /api/auth/login`.
3. Receive a JWT token.
4. Include the token in the `Authorization` header as `Bearer <token>` for protected endpoints.

## API Endpoints
### Authentication
- `POST /api/auth/register`
- `POST /api/auth/login`

### Candidate
- `POST /api/candidates`
- `GET /api/candidates`
- `GET /api/candidates/{id}`
- `PUT /api/candidates/{id}`
- `DELETE /api/candidates/{id}`

### Job
- `POST /api/jobs`
- `GET /api/jobs`
- `GET /api/jobs/{id}`
- `PUT /api/jobs/{id}`
- `DELETE /api/jobs/{id}`

## Example Requests
### Register
```json
{
  "username": "recruiter",
  "email": "recruiter@example.com",
  "password": "secret123",
  "role": "RECRUITER"
}
```

### Login
```json
{
  "usernameOrEmail": "recruiter",
  "password": "secret123"
}
```

### Create Candidate
```json
{
  "firstName": "Jane",
  "lastName": "Doe",
  "email": "jane.doe@example.com",
  "phone": "+91 9876543210",
  "skills": "Java, Spring Boot, SQL",
  "experience": 5,
  "resumeUrl": "https://example.com/resume.pdf",
  "status": "ACTIVE"
}
```

### Update Candidate
```json
{
  "firstName": "Jane",
  "lastName": "Doe",
  "email": "jane.doe@example.com",
  "phone": "+91 9876543210",
  "skills": "Java, Spring Boot, Microservices",
  "experience": 6,
  "resumeUrl": "https://example.com/resume-2.pdf",
  "status": "HIRED"
}
```

### Create Job
```json
{
  "title": "Java Developer",
  "department": "Engineering",
  "description": "Build and maintain backend services and APIs.",
  "location": "Hyderabad",
  "experienceRequired": 3,
  "employmentType": "FULL_TIME",
  "status": "OPEN"
}
```

### Update Job
```json
{
  "title": "Senior Java Developer",
  "department": "Engineering",
  "description": "Lead backend development and architecture.",
  "location": "Hyderabad",
  "experienceRequired": 5,
  "employmentType": "FULL_TIME",
  "status": "OPEN"
}
```

## Swagger URL
Open the Swagger UI in your browser:
```text
http://localhost:8080/swagger-ui.html
```

## Testing Instructions
```bash
mvn test
```

## Future Enhancements
- Application management
- Interview scheduling
- Recruitment pipeline
- Resume upload
- Resume parsing
- AI resume screening
- Email notifications
- Recruitment dashboard
- Docker deployment
- Role-based advanced permissions

## Notes
The current version intentionally keeps Candidate and Job independent, while preparing the architecture for a future `Application` entity relationship:
`Candidate 1 --- * Application * --- 1 Job`
