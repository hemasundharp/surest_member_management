# 🧩 Surest Member Management API

A **Spring Boot 3.x** based RESTful API for managing **Users, Roles, and Members** with **JWT authentication**, **role-based access control (RBAC)**, and **MapStruct DTO mapping**.

---

## 🚀 Features

✅ **User Management** — CRUD operations for users  
✅ **Role Management** — Supports multiple roles per user  
✅ **Member Management** — Pagination, sorting, and filtering  
✅ **JWT Authentication** — Secure access with Bearer tokens  
✅ **Role-Based Authorization** — Admin/User access control  
✅ **MapStruct Integration** — DTO ↔ Entity mapping  
✅ **Flyway Migrations** — Database versioning and seed data  
✅ **Swagger (OpenAPI 3)** — Interactive API documentation  
✅ **Lombok + JPA + Validation** — Clean, boilerplate-free code

---

## 🧱 Project Structure

```
surest_member_management/
├── build.gradle                 # Gradle build configuration
├── gradle/                      # Gradle wrapper files
├── settings.gradle              # Project settings
├── src/
│   ├── main/
│   │   ├── java/com/surest/api/
│   │   │   ├── config/          # Security, JWT, and cache configs
│   │   │   ├── controller/      # REST controllers
│   │   │   ├── dto/             # Data Transfer Objects
│   │   │   ├── exception/       # Custom exceptions and handlers
│   │   │   ├── mapper/          # MapStruct mappers
│   │   │   ├── model/           # JPA entities
│   │   │   ├── repository/      # Spring Data JPA repositories
│   │   │   ├── service/         # Service interfaces
│   │   │   ├── service/impl/    # Service implementations
│   │   │   └── SurestMemberManagementApplication.java
│   │   └── resources/
│   │       ├── application.yml  # App configuration
│   │       ├── db/migration/    # Flyway migration scripts
│   │       └── logback-spring.xml
│   └── test/
│       ├── java/com/surest/api/ # Unit and integration tests
│       └── resources/
└── README.md
```

---

## ⚙️ Tech Stack

| Layer | Technology |
|-------|-------------|
| **Language** | Java 17+ |
| **Framework** | Spring Boot 3.x |
| **Security** | Spring Security + JWT |
| **ORM** | Hibernate / JPA |
| **Database** | PostgreSQL / MySQL (configurable) |
| **Migrations** | Flyway |
| **Mapping** | MapStruct |
| **Docs** | Swagger / OpenAPI 3 |
| **Build Tool** | Gradle |
| **Logging** | SLF4J + Logback |

---

## 🧩 API Overview

| Module | Endpoint | Role Access | Description |
|---------|-----------|-------------|--------------|
| **Auth** | `/api/auth/login` | Public | Login & obtain JWT token |
| **User** | `/api/users/**` | Admin Only | Manage users |
| **Role** | `/api/roles/**` | Admin / User | Manage or view roles |
| **Member** | `/api/members/**` | Admin / User | Manage or view members |

---

## 🔐 Security Overview

- **Authentication**: via JWT (`Authorization: Bearer <token>`)
- **Authorization**: 
  - `ROLE_ADMIN` → Full access
  - `ROLE_USER` → Read-only access for Members & Roles
- `JwtRequestFilter` handles token validation and sets the `SecurityContext`

---

## 🧭 DTO Mapping with MapStruct

MapStruct is used to convert between Entities and DTOs efficiently.

**Example:**

```java
@Mapper(componentModel = "spring")
public interface MemberMapper {
    MemberDTO toDto(Member member);
    Member toEntity(MemberDTO dto);
}
```

Generated sources are stored in:
```
build/generated/sources/annotationProcessor/java/main/
```

---

## 🗃️ Database Migration

Flyway auto-runs on startup.  
Migration scripts live in `src/main/resources/db/migration`.

Example:
```sql
-- V1__create_roles_table.sql
CREATE TABLE roles (
    id UUID PRIMARY KEY,
    name VARCHAR(50) UNIQUE NOT NULL
);
```

---

## 📦 Running the Application

### 🖥️ Local Setup

1. **Clone repository**
   ```bash
   git clone https://github.com/<your-org>/surest_member_management.git
   cd surest_member_management
   ```

2. **Configure environment**
   Update `src/main/resources/application.yml`:
   ```yaml
   spring:
     datasource:
       url: jdbc:postgresql://localhost:5432/surest_db
       username: postgres
       password: password
     jpa:
       hibernate:
         ddl-auto: validate
     flyway:
       enabled: true
   jwt:
     secret: your-secret-key
     expiration: 3600000
   ```

3. **Run**
   ```bash
   ./gradlew bootRun
   ```

4. **Access**
   - API: [http://localhost:8080](http://localhost:8080)
   - Swagger UI: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

---

## 🧪 Testing

Run all tests:

```bash
./gradlew test
```

View reports:

```
build/reports/tests/test/index.html
```

---

## 🧭 Git Workflow

```bash
# Start from develop
git checkout develop

# Create feature branch
git checkout -b feature/add-member-mapper

# Commit logically
git add src/main/java/com/surest/api/mapper/MemberMapper.java
git commit -m "feat(mapper): add MemberMapper using MapStruct"

# Push & create PR
git push origin feature/add-member-mapper
```

---

## 🧰 Useful Gradle Tasks

| Task | Description |
|------|--------------|
| `./gradlew clean` | Clean build artifacts |
| `./gradlew build` | Compile and run tests |
| `./gradlew bootRun` | Run Spring Boot app |
| `./gradlew test` | Run tests |
| `./gradlew flywayMigrate` | Apply migrations manually |

---

## 🧑‍💻 Authors

**Surest Dev Team**  
> API design, security, and infrastructure by Surest Engineering.

---

## 📜 License

This project is licensed under the [MIT License](LICENSE).

---

## 🧠 Notes

- Always use **feature branches** for new work.
- Never commit generated code under `build/`.
- Use **Conventional Commits**:
  ```
  feat: new feature
  fix: bug fix
  chore: non-functional change
  docs: documentation update
  refactor: internal refactor
  ```

---

> 💡 Tip: You can generate up-to-date Swagger/OpenAPI docs automatically by visiting:
> [http://localhost:8080/v3/api-docs](http://localhost:8989/v3/api-docs)

---
