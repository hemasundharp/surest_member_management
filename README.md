# surest_member_management

Surest Member Management API

An API to manage Users, Roles, and Members with JWT login and role-based access.

Features

- Manage Users, Roles, Members (CRUD)
- JWT Authentication (login with token)
- Role-based access: ADMIN full, USER read-only
- DTO Mapping using MapStruct
- Database migration with Flyway
- Swagger docs for API testing

Tech Stack

- Java 17+
- Spring Boot
- Spring Security + JWT
- JPA / Hibernate + PostgreSQL/MySQL
- MapStruct for DTO mapping
- Flyway for DB migrations
- Swagger / OpenAPI 3
- Gradle build tool

Project Structure

src/main/java/com/surest/api/
  config/       - Security, JWT
  controller/   - REST endpoints
  dto/          - Data Transfer Objects
  mapper/       - MapStruct mappers
  model/        - Entities
  repository/   - JPA repositories
  service/      - Service interfaces
  service/impl/ - Service implementations
  exception/    - Custom exceptions and handlers
  SurestMemberManagementApplication.java

Security

- Login to get JWT token
- ROLE_ADMIN - full access
- ROLE_USER - read-only access
