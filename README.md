Talent Recruitment API

A full-stack Talent Recruitment application built using Spring Boot, Spring Security, JWT, Google OAuth2, PostgreSQL, and Next.js.

The application provides authentication, candidate registration, job management, password management, refresh-token sessions, Google OAuth2 login, idle-session timeout, structured logging, application health monitoring, and environment-based configuration.

1. Project Overview

The Talent Recruitment system is designed to manage recruitment-related operations such as:

User authentication
User registration
Candidate registration
Candidate management
Job creation and management
Job searching and filtering
JWT-based authentication
Refresh-token sessions
Password reset
Change password
Brute-force login protection
Google OAuth2 login
Session and logout management
Idle-session timeout
Structured application logging
Application health monitoring
Development, test, and production configurations
2. Technology Stack
Backend
Java 17
Spring Boot 3.3.2
Spring Security
Spring Data JPA
Hibernate
Maven
PostgreSQL
JWT
Google OAuth2
Spring Boot Actuator
Logback
Swagger / OpenAPI
Frontend
Next.js
React
TypeScript
Tailwind CSS
Database
PostgreSQL
3. Architecture

The application follows a layered architecture.

Frontend
   ↓
Spring Security
   ↓
JWT Authentication / OAuth2
   ↓
Controller
   ↓
Service
   ↓
Repository
   ↓
JPA / Hibernate
   ↓
PostgreSQL

For authentication:

User
 ↓
Login / Google OAuth2
 ↓
Authentication Service
 ↓
JWT Access Token
 +
Refresh Token
 ↓
User Session
 ↓
PostgreSQL
4. Main Features
Authentication

The application supports:

Username/password login
User registration
Candidate registration
JWT authentication
Refresh tokens
Logout
Logout from all sessions
Password reset
Change password
Brute-force protection
Google OAuth2 login
Recruitment

The application supports:

Candidate registration
Candidate management
Job creation
Job updates
Job deletion
Job searching
Job filtering
Pagination
Sorting
Security

Security features include:

BCrypt password hashing
JWT authentication
Refresh-token rotation
Refresh-token hashing
Session revocation
Account lockout after repeated failed login attempts
Password complexity validation
Google OAuth2 authentication
CORS configuration
Stateless Spring Security configuration
5. Project Structure

The project is divided into backend and frontend applications.

demo/
│
├── talent-recruitment-api/
│   │
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/
│   │   │   │   └── com/
│   │   │   │       └── example/
│   │   │   │           └── talentrecruitment/
│   │   │   │               │
│   │   │   │               ├── auth/
│   │   │   │               │   ├── controller/
│   │   │   │               │   ├── dto/
│   │   │   │               │   ├── entity/
│   │   │   │               │   ├── repository/
│   │   │   │               │   └── service/
│   │   │   │               │
│   │   │   │               ├── candidate/
│   │   │   │               ├── job/
│   │   │   │               ├── security/
│   │   │   │               ├── config/
│   │   │   │               ├── exception/
│   │   │   │               └── TalentRecruitmentApplication.java
│   │   │   │
│   │   │   └── resources/
│   │   │       ├── application.properties
│   │   │       ├── application-dev.properties
│   │   │       ├── application-test.properties
│   │   │       ├── application-prod.properties
│   │   │       └── logback-spring.xml
│   │   │
│   │   └── test/
│   │
│   ├── logs/
│   ├── pom.xml
│   └── README.md
│
└── talent-recruitment-frontend/
    │
    ├── app/
    ├── components/
    ├── lib/
    ├── public/
    ├── package.json
    └── ...
6. Backend Packages
auth

Contains authentication-related functionality.

Examples:

Login
Registration
Forgot password
Reset password
Change password
Refresh token
Logout
Google OAuth2
controller

Controllers receive HTTP requests and return HTTP responses.

For example:

POST /api/auth/login

is handled by:

AuthenticationController
service

Services contain business logic.

For example:

AuthenticationController
        ↓
AuthenticationService
        ↓
UserRepository

The controller should not contain complex business logic.

repository

Repositories communicate with the database through Spring Data JPA.

Example:

public interface UserRepository extends JpaRepository<User, Long>

Spring Data JPA provides common database operations such as:

save()
findById()
findAll()
delete()
Custom queries
7. Authentication Flow

The normal login flow is:

User
 ↓
Next.js Login Page
 ↓
POST /api/auth/login
 ↓
AuthenticationController
 ↓
AuthenticationService
 ↓
AuthenticationManager
 ↓
UserDetailsService
 ↓
PostgreSQL
 ↓
Password Verification
 ↓
Generate JWT Access Token
 ↓
Generate Refresh Token
 ↓
Create UserSession
 ↓
Return Authentication Response
 ↓
Frontend
 ↓
Dashboard

The access token is used to authenticate API requests.

The refresh token is used to obtain a new access token when required.

8. JWT Authentication

JWT is used for access-token authentication.

The JWT can contain information such as:

Username
Role
Issuer
Expiration
Other authentication claims

The frontend sends the token using:

Authorization: Bearer <access-token>

The request then reaches:

JwtAuthenticationFilter

The filter:

Reads the Authorization header.
Extracts the JWT.
Validates the token.
Extracts the username.
Loads the user.
Creates an authenticated SecurityContext.
Allows the request to continue.
9. Refresh Token and User Sessions

Refresh tokens are used to maintain authenticated sessions without making the access token extremely long-lived.

The application stores a hash of the refresh token rather than storing the raw refresh token.

The flow is:

Login
 ↓
Generate Refresh Token
 ↓
Hash Refresh Token
 ↓
Create UserSession
 ↓
Store Hash in PostgreSQL
 ↓
Return Refresh Token to Client

The user_sessions table contains information such as:

Session ID
User ID
Token hash
Created time
Expiry time
Last-used time
Revoked status
IP address
User agent
10. Refresh Token Rotation

When a refresh token is used:

Refresh Token
 ↓
Hash Token
 ↓
Find UserSession
 ↓
Check Session
 ↓
Check Expiration
 ↓
Check Revocation
 ↓
Revoke Old Session
 ↓
Generate New Refresh Token
 ↓
Create New Session
 ↓
Generate New Access Token
 ↓
Return New Tokens

This provides refresh-token rotation.

An old refresh token should not remain permanently usable.

11. Logout

Logout is handled through:

POST /api/auth/logout

The backend:

Receives the refresh token.
Hashes it.
Finds the corresponding session.
Marks the session as revoked.
Saves the session.

The current implementation revokes the refresh-token-backed session.

An already-issued JWT access token is not immediately revoked by the current implementation. Therefore, production access tokens should have a relatively short lifetime.

12. Logout All Sessions

The application also supports:

POST /api/auth/logout-all

This revokes all active sessions belonging to the user.

The flow is:

User
 ↓
Logout All
 ↓
Find Active User Sessions
 ↓
Set revoked = true
 ↓
Save Sessions

This is useful when a user wants to terminate all active sessions.

13. Password Requirements

The application enforces password complexity requirements.

A valid password must contain:

Minimum 12 characters
Maximum 64 characters
At least one lowercase letter
At least one uppercase letter
At least one number
At least one special character

Allowed special characters include:

@$!%*?&

Example validation pattern:

^(?=.{12,64}$)(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&]).*$

Passwords are never stored as plain text.

They are hashed using:

BCrypt
14. Password Reset

The forgot-password flow is:

Login Page
 ↓
Forgot Password
 ↓
Next.js /forgot-password
 ↓
POST /api/auth/forgot-password
 ↓
AuthenticationController
 ↓
AuthenticationService
 ↓
Generate Reset Token
 ↓
Save Token in PostgreSQL
 ↓
EmailService
 ↓
Email Provider
 ↓
Reset Link
 ↓
Next.js /reset-password
 ↓
POST /api/auth/reset-password
 ↓
Validate Token
 ↓
Hash New Password
 ↓
Update User
 ↓
Revoke Existing Sessions

Reset tokens have a limited lifetime.

After a successful password reset, existing refresh-token sessions are revoked.

15. Change Password

Authenticated users can change their password using:

POST /api/auth/change-password

The backend:

Gets the authenticated username.
Finds the user.
Verifies the current password.
Checks the new password.
Prevents using the same password.
Hashes the new password.
Saves the user.
Revokes existing sessions.

The user must authenticate again after the password change.

16. Brute-Force Protection

The application includes failed-login tracking.

The users table contains:

failed_login_attempts
account_locked_until

The login process checks whether the account is currently locked.

After repeated failed authentication attempts:

Failed Login
 ↓
Increment failedLoginAttempts
 ↓
Reach Maximum Attempts
 ↓
Lock Account
 ↓
accountLockedUntil = current time + lock duration

The current configuration locks the account after 5 failed attempts for 15 minutes.

Successful login resets:

failedLoginAttempts = 0
accountLockedUntil = null
17. Google OAuth2 Login

The application supports Google OAuth2/OIDC login.

The flow is:

User
 ↓
Click Google Login
 ↓
Google Authorization
 ↓
Google Account Selection
 ↓
Google Authentication
 ↓
Spring OAuth2 Callback
 ↓
GoogleOAuth2SuccessHandler
 ↓
GoogleOAuth2Service
 ↓
Find / Create / Link User
 ↓
Generate JWT
 ↓
Generate Refresh Token
 ↓
Create UserSession
 ↓
Redirect to Next.js
 ↓
OAuth2 Success Page
 ↓
Store Authentication Information
 ↓
Dashboard

The configured Google scopes include:

openid
profile
email

Google acts as the identity provider.

The application then creates its own application authentication session.

18. Google OAuth2 Failure Flow

When Google authentication is cancelled or fails:

User
 ↓
Google OAuth2
 ↓
Cancel / Failure
 ↓
GoogleOAuth2FailureHandler
 ↓
Redirect to Next.js
 ↓
Display Authentication Error

This prevents the user from being left on an unexpected backend error page.

19. Candidate Registration

Candidate registration is handled through:

POST /api/auth/candidate/register

The flow is:

Registration Page
 ↓
Candidate Registration Form
 ↓
Next.js
 ↓
lib/api.ts
 ↓
POST /api/auth/candidate/register
 ↓
CandidateAuthController
 ↓
CandidateAuthService
 ↓
Validate Candidate
 ↓
Create User
 ↓
Create Candidate
 ↓
PostgreSQL

Candidate information can then be accessed through the recruitment APIs according to the configured authorization rules.

20. Job Management

The Job API uses:

/api/jobs

Available operations include:

POST   /api/jobs
GET    /api/jobs
GET    /api/jobs/{id}
PUT    /api/jobs/{id}
DELETE /api/jobs/{id}

The GET endpoint supports filtering and pagination.

Example parameters:

department
location
status
page
size
sortBy
direction

Example:

GET /api/jobs?department=IT&location=Hyderabad&page=0&size=10
21. Candidate APIs

Candidate APIs use:

/api/candidates

Candidate operations are protected by Spring Security.

The application uses:

CandidateController
CandidateService
CandidateRepository

to process candidate-related requests.

22. Security Configuration

Spring Security is configured using:

SecurityConfig.java

Important security configuration includes:

CORS
CSRF configuration
Stateless sessions
JWT authentication filter
Authentication provider
OAuth2 login
Endpoint authorization

The application uses:

SessionCreationPolicy.STATELESS

for API authentication.

JWT authentication is performed using:

JwtAuthenticationFilter
23. CORS

The backend allows configured frontend origins.

Development origin:

http://localhost:3000

The configured local network frontend origin can also be used during development.

CORS controls which browser-based frontend applications are allowed to communicate with the backend.

CORS does not replace authentication or authorization.

24. Exception Handling

The application uses a global exception handler:

GlobalExceptionHandler

It handles errors such as:

Resource not found
Bad request
Validation errors
Bad credentials
Access denied
JWT-related errors
Illegal arguments
Unexpected exceptions

This provides consistent API error responses.

25. API Response Structure

The project uses a common:

ApiResponse

structure.

This helps provide consistent responses from API endpoints.

Typical responses contain information such as:

success
message
data

This makes frontend API handling more predictable.

26. Swagger / OpenAPI

Swagger is available for API documentation.

Development URL:

http://localhost:8080/swagger-ui.html

OpenAPI JSON:

http://localhost:8080/v3/api-docs

Swagger can be used to:

View endpoints
Understand request models
Understand response models
Test APIs
Verify authentication behavior
27. Application Profiles

The application supports separate configuration profiles:

dev
test
prod

Configuration files:

application-dev.properties
application-test.properties
application-prod.properties

The purpose of profiles is to prevent development configuration from being used accidentally in production.

28. Development Profile

Development uses:

application-dev.properties

The database configuration is externalized.

Example:

spring.datasource.url=${DB_URL:jdbc:postgresql://localhost:5432/talent_recruitment_db}
spring.datasource.username=${DB_USERNAME:postgres}
spring.datasource.password=${DB_PASSWORD}
spring.datasource.driver-class-name=org.postgresql.Driver

The password is intentionally not written into the properties file.

29. Test Profile

Test uses:

application-test.properties

Example:

spring.datasource.url=${TEST_DB_URL:jdbc:postgresql://localhost:5432/talent_recruitment_test}
spring.datasource.username=${DB_USERNAME:postgres}
spring.datasource.password=${DB_PASSWORD}
spring.datasource.driver-class-name=org.postgresql.Driver

The test database should be separate from the development database.

30. Production Profile

Production uses:

application-prod.properties

Example:

spring.datasource.url=${DB_URL}
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}
spring.datasource.driver-class-name=org.postgresql.Driver

Production secrets should be supplied through environment variables or a secure secret-management system.

Credentials should not be committed to Git.

31. Environment Variables

For local development in PowerShell:

$env:DB_USERNAME="postgres"
$env:DB_PASSWORD="your-database-password"
$env:DB_URL="jdbc:postgresql://localhost:5432/talent_recruitment_db"
$env:SPRING_PROFILES_ACTIVE="dev"

Then run:

mvn spring-boot:run

Verify the active profile in the startup logs.

32. Why Environment Variables Are Used

Instead of:

spring.datasource.password=MyRealPassword

the application uses:

spring.datasource.password=${DB_PASSWORD}

This means:

Spring Boot
 ↓
Reads DB_PASSWORD environment variable
 ↓
Uses its value as database password

The actual secret therefore does not need to be stored inside the source code.

The same approach can be used for:

Database credentials
JWT secrets
Google OAuth credentials
Email credentials
Other sensitive configuration
33. Structured Logging

The application uses structured logging for important application events.

Structured logs make logs easier to:

Search
Filter
Analyze
Send to centralized logging systems
Monitor in production

A structured log can look like:

{
  "timestamp": "2026-09-08T15:20:10",
  "level": "INFO",
  "logger_name": "CandidateAuthService",
  "message": "Candidate registration successful"
}
34. Important Business Logs

Important operations can generate logs such as:

Candidate registration successful
User login successful
Login failed
Account locked
Password reset requested
Password reset successful
Password changed
Refresh token rotated
User session revoked
Google OAuth2 login successful

Errors should also be logged with sufficient diagnostic information.

35. Sensitive Information Must Not Be Logged

The application must never log sensitive authentication information such as:

Passwords
JWT access tokens
Refresh tokens
JWT secrets
Database passwords
OAuth client secrets
Email passwords
Reset tokens

For example, do not do:

log.info("User password: {}", password);

and do not do:

log.info("JWT token: {}", token);

Instead, log safe information such as:

log.info("User login successful for username={}", username);

Usernames and email addresses should also be handled according to the organization's privacy requirements.

36. Log File

Application logs are written to:

logs/talent-recruitment.log

The log directory should not be committed to Git.

The .gitignore should contain entries such as:

target/
logs/
*.log
.env
37. Viewing Logs During Development

Start the backend:

cd "C:\Users\Enfec Solutions\Desktop\demo"
mvn spring-boot:run

Open another PowerShell terminal:

cd "C:\Users\Enfec Solutions\Desktop\demo"
Get-Content .\logs\talent-recruitment.log -Wait

The second terminal will continuously display new log entries.

Then perform an application operation such as candidate registration or login.

The corresponding application log should appear.

38. Searching Logs

To search for candidate registration logs:

Select-String -Path ".\logs\talent-recruitment.log" -Pattern "Candidate registration successful"

To view the latest entries:

Get-Content .\logs\talent-recruitment.log -Tail 20

To continuously watch the file:

Get-Content .\logs\talent-recruitment.log -Wait
39. Spring Boot Actuator

Spring Boot Actuator provides application monitoring and management endpoints.

The project uses Actuator for application health monitoring.

The main endpoint is:

/actuator/health

Development URL:

http://localhost:8080/actuator/health
40. Health Check

A successful health response can look like:

{
  "status": "UP"
}

This means the application is running and its health check is reporting an operational state.

Actuator is useful for:

Health checks
Monitoring
Container orchestration
Deployment systems
Load balancers
Operational troubleshooting
41. Actuator Configuration

The application exposes only the required Actuator endpoints.

Example:

management.endpoints.web.exposure.include=health,info

Health details can be controlled using:

management.endpoint.health.show-details=when_authorized

Sensitive Actuator endpoints should not be publicly exposed unnecessarily.

42. Verifying Actuator

Start the application:

mvn spring-boot:run

Then open:

http://localhost:8080/actuator/health

Or use PowerShell:

Invoke-WebRequest http://localhost:8080/actuator/health

The application startup log should also indicate that Actuator endpoints have been exposed.

43. Production Readiness Verification

Before committing the production-readiness changes, verify:

Application
mvn clean test
Development Profile
$env:SPRING_PROFILES_ACTIVE="dev"
mvn spring-boot:run
Health

Open:

http://localhost:8080/actuator/health
Logs

Check:

logs/talent-recruitment.log
Configuration

Verify:

application-dev.properties
application-test.properties
application-prod.properties
Logging Configuration

Verify:

logback-spring.xml
Git Ignore

Verify that:

target/
logs/
*.log
.env

are ignored.

44. Development Run

From the backend project:

cd "C:\Users\Enfec Solutions\Desktop\demo"

Set the development profile:

$env:SPRING_PROFILES_ACTIVE="dev"

Set database configuration:

$env:DB_USERNAME="postgres"
$env:DB_PASSWORD="your-database-password"
$env:DB_URL="jdbc:postgresql://localhost:5432/talent_recruitment_db"

Start the application:

mvn spring-boot:run

Expected result:

Tomcat started on port 8080
Started TalentRecruitmentApplication

The active profile should indicate:

dev
45. Frontend Run

Open another terminal.

Go to:

cd "C:\Users\Enfec Solutions\Desktop\demo\talent-recruitment-frontend"

Install dependencies if required:

npm install

Start the frontend:

npm run dev

The frontend normally runs on:

http://localhost:3000
46. Frontend API Configuration

The frontend uses:

NEXT_PUBLIC_API_URL

The API utility file is:

lib/api.ts

The backend base URL is configured using:

const BASE_URL =
  process.env.NEXT_PUBLIC_API_URL || "http://localhost:8080";

This allows the frontend API URL to be changed without modifying every API call.

47. Idle Session Timeout

The frontend includes an idle-session timeout.

The demo timeout is:

30 seconds

User activity such as:

Mouse movement
Mouse click
Keyboard input
Scrolling
Touch

resets the idle timer.

The flow is:

User Logs In
 ↓
Session Active
 ↓
User Interacts
 ↓
Idle Timer Resets
 ↓
User Stops Interacting
 ↓
30 Seconds Pass
 ↓
Idle Timeout Reached
 ↓
Logout
 ↓
Refresh Session Revoked
 ↓
Tokens Removed From Client Storage
 ↓
Redirect To Login

The 30-second timeout is suitable for demonstration/testing but is generally too aggressive for normal production use.

48. Database

The application uses PostgreSQL.

Development database:

talent_recruitment_db

Test database:

talent_recruitment_test

Important database tables include entities related to:

users
candidates
jobs
password reset tokens
user sessions

Business data is stored in PostgreSQL.

Application logs are stored separately from business data.

49. Logging vs Database

Application logging and database storage serve different purposes.

Database

Stores application/business information such as:

Users
Candidates
Jobs
Sessions
Password reset information
Logs

Record application events such as:

Login succeeded
Login failed
Candidate registered
Password changed
Exception occurred
Session revoked

Therefore, logs are not normally stored in the business tables.

In production, logs can instead be sent to centralized logging systems.

50. Security Recommendations

The following rules should be followed before production deployment:

Never commit passwords.
Never commit JWT secrets.
Never commit Google client secrets.
Never commit email passwords.
Never log JWTs.
Never log refresh tokens.
Never log passwords.
Never log reset tokens.
Use HTTPS.
Use secure cookies for production browser authentication.
Use short-lived access tokens.
Rotate refresh tokens.
Revoke refresh sessions when required.
Protect Actuator endpoints.
Restrict CORS origins.
Use strong database credentials.
Keep dependencies updated.
Use separate development, test, and production databases.
51. Git Ignore

The following should generally not be committed:

target/
logs/
*.log
.env

Example .gitignore:

target/
logs/
*.log
.env
.env.*
!.env.example

Do not add actual passwords or secrets to .env.example.

Use placeholders instead.

52. Testing

Run the backend tests using:

mvn clean test

The command:

mvn clean test

does the following:

clean
 ↓
Removes previous build output
 ↓
compile
 ↓
Compile application
 ↓
test
 ↓
Run automated tests

The build should be checked before pushing changes.

53. Build Verification

Run:

mvn clean package

This verifies that the project can be compiled and packaged successfully.

For a development run:

mvn spring-boot:run
54. Important URLs
Frontend
http://localhost:3000
Backend
http://localhost:8080
Swagger
http://localhost:8080/swagger-ui.html
OpenAPI
http://localhost:8080/v3/api-docs
Health
http://localhost:8080/actuator/health
Google OAuth2
http://localhost:8080/oauth2/authorization/google
55. Important Authentication Endpoints
POST /api/auth/register
POST /api/auth/login
POST /api/auth/candidate/register
POST /api/auth/forgot-password
POST /api/auth/reset-password
POST /api/auth/change-password
POST /api/auth/refresh
POST /api/auth/logout
POST /api/auth/logout-all
56. Important Recruitment Endpoints
Jobs
POST   /api/jobs
GET    /api/jobs
GET    /api/jobs/{id}
PUT    /api/jobs/{id}
DELETE /api/jobs/{id}
Candidates
GET    /api/candidates/**
POST   /api/candidates/**
PUT    /api/candidates/**
DELETE /api/candidates/**

Actual authorization depends on the configured Spring Security rules and roles.

57. Production Configuration Principles

The production environment should follow these principles:

Source Code
     ↓
Configuration Placeholders
     ↓
Environment Variables / Secret Manager
     ↓
Production Credentials

Production credentials should not be embedded inside:

Java source code
Properties files
README
Git commits
Logs
Frontend code
58. Development Configuration Principles

Development configuration is intentionally easier to run locally.

The development environment can use:

Local PostgreSQL
Local backend
Local frontend
Local Google OAuth configuration
Local email configuration

while still keeping sensitive values externalized through environment variables.

59. Test Configuration Principles

Testing should use a separate environment/database.

This prevents automated tests from accidentally modifying development or production data.

Recommended structure:

Development
    ↓
talent_recruitment_db

Testing
    ↓
talent_recruitment_test

Production
    ↓
Production Database
60. Production-Readiness Checklist

Before creating the pull request, verify all of the following.

Logging
 Structured logging configured
 Important business operations logged
 Errors logged
 Passwords not logged
 JWTs not logged
 Refresh tokens not logged
 Secrets not logged
 Log files ignored by Git
Actuator
 Spring Boot Actuator configured
 /actuator/health works
 Health status is UP
 Only required endpoints exposed
 Sensitive Actuator endpoints protected
Profiles
 application-dev.properties
 application-test.properties
 application-prod.properties
 Development profile works
 Test configuration uses separate database
 Production configuration does not contain hardcoded credentials
Environment Variables
 DB username externalized
 DB password externalized
 DB URL externalized
 JWT secret externalized
 Google credentials externalized
 Email credentials externalized
 No real secrets committed
Build
 mvn clean test passes
 mvn clean package succeeds
 Application starts with dev profile
 Frontend starts successfully
 Login works
 Candidate registration works
 Job APIs work
 Actuator health works
 Logging works
61. Git Workflow

Check the current branch:

git branch

Check modified files:

git status

Review the changes:

git diff
62. Check for Secrets Before Commit

Search the project for potentially hardcoded credentials.

For example:

git grep -n -i "password"

Also check:

git grep -n -i "secret"

and:

git grep -n -i "client-secret"

Review every result manually.

Do not blindly delete legitimate configuration placeholders or variable names.

63. Stage Changes

After verification:

git add .

Check what is staged:

git status

Review staged changes:

git diff --cached
64. Commit

Create a meaningful commit:

git commit -m "Add production readiness logging monitoring and profiles"

A good commit message should explain the purpose of the change.

65. Push Feature Branch

Verify the current branch:

git branch --show-current

Push the feature branch:

git push origin feature/forgot-password
66. Pull Request

After pushing the feature branch:

feature branch
      ↓
Pull Request
      ↓
develop
      ↓
Testing / Integration
      ↓
main

The pull request should explain:

What Changed
Added structured logging
Added Actuator health monitoring
Added dev/test/prod profiles
Externalized configuration
Added environment-variable based credentials
Updated documentation
Verification

Mention that:

mvn clean test

was executed and the application was tested using the development profile.

Also mention that:

/actuator/health

was verified.

67. Production Deployment Concept

The production deployment architecture can be represented as:

User
 ↓
HTTPS
 ↓
Frontend
 ↓
Backend API
 ↓
Spring Security
 ↓
Application Services
 ↓
PostgreSQL

Monitoring:

Backend
 ↓
Actuator
 ↓
Health Monitoring

Logging:

Backend
 ↓
Structured Logs
 ↓
Log File / Centralized Logging System

Configuration:

Deployment Environment
 ↓
Environment Variables / Secret Manager
 ↓
Spring Boot
 ↓
Production Configuration
68. Security Architecture

The authentication architecture is:

                ┌───────────────┐
                │     User      │
                └───────┬───────┘
                        │
             ┌──────────┴──────────┐
             │                     │
      Username/Password       Google OAuth2
             │                     │
             └──────────┬──────────┘
                        ↓
               Spring Security
                        ↓
                 Authentication
                        ↓
                JWT Access Token
                        +
                  Refresh Token
                        ↓
                   User Session
                        ↓
                    PostgreSQL
69. Overall Application Flow

The complete application can be viewed as:

                         USER
                           │
                           ▼
                    Next.js Frontend
                           │
                           ▼
                    Spring Boot API
                           │
                           ▼
                    Spring Security
                           │
             ┌─────────────┴─────────────┐
             │                           │
             ▼                           ▼
       JWT Authentication          Google OAuth2
             │                           │
             └─────────────┬─────────────┘
                           ▼
                      Controllers
                           │
                           ▼
                        Services
                           │
                           ▼
                      Repositories
                           │
                           ▼
                       PostgreSQL

Supporting systems:

Spring Boot
   │
   ├── Actuator
   │      └── Health Monitoring
   │
   ├── Structured Logging
   │      └── Application Logs
   │
   └── Environment Configuration
          ├── dev
          ├── test
          └── prod
70. Conclusion

The Talent Recruitment application provides a full-stack recruitment platform with authentication, candidate management, job management, secure password handling, refresh-token sessions, Google OAuth2 authentication, session management, structured logging, monitoring, and environment-based configuration.

The production-readiness phase focuses on making the application easier to:

Monitor
Debug
Configure
Test
Deploy
Maintain
Secure

The key principle is:

Application Code
       +
Secure Configuration
       +
Structured Logging
       +
Health Monitoring
       +
Automated Testing
       =
Production-Ready Development Process

Note: Before calling the project fully production-ready, additional production hardening should be completed, especially secure browser token handling, HTTPS, secret management, access-token revocation strategy, and deployment infrastructure.