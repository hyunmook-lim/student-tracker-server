# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is a Spring Boot 3.5.3 application for student tracking, built with Java 17 and using H2 database. The system manages students, teachers, classrooms, lectures, and questions with full CRUD operations.

## Development Commands

### Build and Run
```bash
# Build the project
./gradlew build

# Run the application  
./gradlew bootRun

# Run tests
./gradlew test
```

### Database Access
- H2 Console: http://localhost:8080/h2-console
- JDBC URL: `jdbc:h2:file:./data/studentdb`
- Username: `sa`
- Password: (empty)

## Architecture

### Entity Relationships
The application follows a multi-tenant classroom model:

- **Student** ↔ **StudentClassroom** ↔ **Classroom** (many-to-many through junction)
- **Teacher** ↔ **TeacherClassroom** ↔ **Classroom** (many-to-many through junction)  
- **Classroom** → **Lecture** (one-to-many)
- **Question** (standalone entity with topics and difficulty)
- **StudentQuestionResult** (tracks student performance on questions)

### Package Structure
```
com.visit.studentTracker/
├── controller/          # REST endpoints with standard CRUD operations
├── service/            # Business logic with @Transactional methods
├── repository/         # JPA repositories extending JpaRepository
├── entity/            # JPA entities with Lombok annotations
└── dto/               # Request/Response DTOs organized by entity
    ├── {entity}/request/
    └── {entity}/response/
```

### Design Patterns
- **Repository Pattern**: JPA repositories for data access
- **Service Layer**: Transactional business logic with DTO conversions
- **REST Controller**: Standard HTTP methods (GET, POST, PATCH, DELETE)
- **Builder Pattern**: Lombok @Builder on entities and DTOs

## Key Technical Details

### Database Configuration
- Uses H2 file-based database stored in `./data/studentdb`
- Hibernate DDL auto-update enabled
- SQL logging enabled for development

### Entity Conventions
- Primary keys: `Long uid` with auto-generation
- Audit fields: `createdAt`, `updatedAt` with `@PrePersist`/`@PreUpdate`
- Soft delete: `isActive` boolean flag
- Relationships: Proper fetch types (LAZY for collections)

### Service Layer Patterns
- Constructor injection for dependencies
- `@Transactional` for write operations, `@Transactional(readOnly = true)` for reads
- DTO conversion methods (`toResponse()`) within services
- Junction table management for many-to-many relationships

### Authentication Notes
- Password encryption is planned but not yet implemented (see StudentService:43)
- Login functionality exists for teachers with `lastLoginAt` tracking
- Students have `isActive` flag (default false), teachers default true