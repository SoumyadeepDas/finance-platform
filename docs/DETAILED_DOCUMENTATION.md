# Finance Platform Detailed Documentation

## 1. Purpose

This project is a Spring Boot backend for a finance dashboard system. It provides:

- user management with role-based access control
- financial record CRUD with soft delete
- dashboard analytics and recent activity feeds
- input validation and structured error responses
- JPA/H2 persistence for local development and demonstration

The embedded H2 console is intended for admin-only local inspection and is protected by the same application security layer.

The backend is designed to serve a frontend dashboard cleanly through REST APIs.

---

## 2. Tech Stack

- Java 17 target
- Spring Boot 4.0.5
- Spring Web
- Spring Data JPA
- Spring Security
- Jakarta Bean Validation
- H2 in-memory database
- JUnit 5 + Spring Security test support

Build file: [pom.xml](../pom.xml)

---

## 3. Project Structure


Main areas:

- `controller`
  Handles HTTP requests and responses.
- `service`
  Defines service contracts.
- `service/impl`
  Implements business logic.
- `repository`
  Encapsulates database access through Spring Data JPA.
- `entity`
  JPA entities persisted to the database.
- `dto`
  Request and response models exposed at the API boundary.
- `security`
  Authentication and authorization setup.
- `exception`
  Custom exceptions and global exception handling.
- `config`
  Startup seeding and application configuration.

Entry point:

- [FinancePlatformApplication.java](../src/main/java/com/finance/platform/FinancePlatformApplication.java)

---

## 4. Architecture Overview

Request flow:

1. A client sends an HTTP request with Basic Auth credentials.
2. Spring Security authenticates the user through `CustomUserDetailsService`.
3. URL-based role checks are applied in `SecurityConfig`.
4. The controller validates and maps request input.
5. The service layer enforces business rules and orchestration.
6. The repository layer queries or mutates the database.
7. DTOs are returned as JSON responses.

High-level layering:

- Controller: HTTP boundary
- Service: business logic and invariants
- Repository: persistence and query logic
- Entity/DTO: data modeling

---

## 5. Domain Model

### 5.1 User

Entity file:

- [User.java](../src/main/java/com/finance/platform/entity/User.java)

Fields:

- `id`: database-generated primary key
- `name`: display name
- `email`: unique login identifier
- `password`: BCrypt-hashed password
- `role`: `VIEWER`, `ANALYST`, `ADMIN`
- `status`: `ACTIVE`, `INACTIVE`
- `createdAt`: set once on insert
- `updatedAt`: updated on every mutation

Behavior:

- `email` is unique
- `status=INACTIVE` prevents login
- timestamps are managed via JPA lifecycle hooks

### 5.2 FinancialRecord

Entity file:

- [FinancialRecord.java](../src/main/java/com/finance/platform/entity/FinancialRecord.java)

Fields:

- `id`: database-generated primary key
- `amount`: positive monetary amount stored as `BigDecimal`
- `type`: `INCOME` or `EXPENSE`
- `category`: uppercase normalized category string
- `date`: business date of the transaction
- `description`: optional notes
- `createdBy`: immutable user id of the creator
- `deleted`: soft-delete flag
- `createdAt`: insert timestamp
- `updatedAt`: last update timestamp

Behavior:

- amount must be positive
- soft-deleted records remain in the database but are hidden from active reads
- `createdBy` is set from the authenticated user and is not client-controlled

---

## 6. Roles and Access Control

Security configuration:

- [SecurityConfig.java](../src/main/java/com/finance/platform/security/SecurityConfig.java)

Authentication:

- HTTP Basic Auth
- user lookup by email
- password verification through BCrypt
- inactive users cannot authenticate

Role model:

- `VIEWER`
  Can access dashboard APIs only.
- `ANALYST`
  Can read financial records and access dashboard APIs.
- `ADMIN`
  Full access to users, records, and dashboard APIs.

### 6.1 Access Matrix

| Endpoint | VIEWER | ANALYST | ADMIN |
|---|---|---|---|
| `GET /api/dashboard/summary` | Yes | Yes | Yes |
| `GET /api/dashboard/categories` | Yes | Yes | Yes |
| `GET /api/dashboard/trends` | Yes | Yes | Yes |
| `GET /api/dashboard/recent` | Yes | Yes | Yes |
| `GET /api/records` | No | Yes | Yes |
| `GET /api/records/{id}` | No | Yes | Yes |
| `GET /api/records/recent` | No | Yes | Yes |
| `POST /api/records` | No | No | Yes |
| `PUT /api/records/{id}` | No | No | Yes |
| `DELETE /api/records/{id}` | No | No | Yes |
| `GET /api/users` | No | No | Yes |
| `GET /api/users/{id}` | No | No | Yes |
| `POST /api/users` | No | No | Yes |
| `PUT /api/users/{id}` | No | No | Yes |
| `PATCH /api/users/{id}/role` | No | No | Yes |
| `PATCH /api/users/{id}/status` | No | No | Yes |

---

## 7. API Modules

Complete endpoint catalog with request/response examples:

- [README.md endpoint reference](../README.md#endpoint-reference)

### 7.1 User Management

Controller:

- [UserController.java](../src/main/java/com/finance/platform/controller/UserController.java)

Endpoints:

- `POST /api/users`
  Create a user.
- `GET /api/users`
  List all users.
- `GET /api/users/{id}`
  Fetch one user.
- `PUT /api/users/{id}`
  Update name and email.
- `PATCH /api/users/{id}/role`
  Change role.
- `PATCH /api/users/{id}/status`
  Change account status.

Important notes:

- only admins can use these endpoints
- password is never returned in responses
- invalid role/status patch values return `400`

### 7.2 Financial Records

Controller:

- [FinancialRecordController.java](../src/main/java/com/finance/platform/controller/FinancialRecordController.java)

Endpoints:

- `POST /api/records`
  Create a record.
- `GET /api/records/{id}`
  Fetch one active record.
- `GET /api/records`
  Paginated list with optional filters.
- `GET /api/records/recent`
  Recent 10 records.
- `PUT /api/records/{id}`
  Full update.
- `DELETE /api/records/{id}`
  Soft delete.

Filtering supported on `GET /api/records`:

- `startDate`
- `endDate`
- `category`
- `type`
- plus Spring pageable parameters:
  `page`, `size`, `sort`

Example:

```http
GET /api/records?startDate=2025-03-01&endDate=2025-03-31&category=SALARY&type=INCOME&page=0&size=20&sort=date,desc
```

Filtering behavior:

- `category` is matched after normalizing input to uppercase
- `type` accepts enum values such as `INCOME` and `EXPENSE`
- invalid date ranges where `endDate < startDate` return `400`

### 7.3 Dashboard APIs

Controller:

- [DashboardController.java](../src/main/java/com/finance/platform/controller/DashboardController.java)

Endpoints:

- `GET /api/dashboard/summary`
  Returns total income, total expenses, net balance, and record count.
- `GET /api/dashboard/categories`
  Returns category totals and counts.
- `GET /api/dashboard/trends`
  Returns monthly income and expense trends.
- `GET /api/dashboard/recent`
  Returns recent activity for dashboard widgets.

These endpoints are available to all authenticated roles.

---

## 7.4 Endpoint Summary Table

| Method | Path | Access | Purpose |
|---|---|---|---|
| `POST` | `/api/users` | `ADMIN` | Create user |
| `GET` | `/api/users` | `ADMIN` | List users |
| `GET` | `/api/users/{id}` | `ADMIN` | Get user by ID |
| `PUT` | `/api/users/{id}` | `ADMIN` | Update user profile |
| `PATCH` | `/api/users/{id}/role` | `ADMIN` | Change user role |
| `PATCH` | `/api/users/{id}/status` | `ADMIN` | Change user status |
| `POST` | `/api/records` | `ADMIN` | Create financial record |
| `GET` | `/api/records` | `ANALYST`, `ADMIN` | List/filter records |
| `GET` | `/api/records/{id}` | `ANALYST`, `ADMIN` | Get one record |
| `GET` | `/api/records/recent` | `ANALYST`, `ADMIN` | Get latest 10 records |
| `PUT` | `/api/records/{id}` | `ADMIN` | Update record |
| `DELETE` | `/api/records/{id}` | `ADMIN` | Soft-delete record |
| `GET` | `/api/dashboard/summary` | `VIEWER`, `ANALYST`, `ADMIN` | Summary totals |
| `GET` | `/api/dashboard/categories` | `VIEWER`, `ANALYST`, `ADMIN` | Category totals |
| `GET` | `/api/dashboard/trends` | `VIEWER`, `ANALYST`, `ADMIN` | Monthly trends |
| `GET` | `/api/dashboard/recent` | `VIEWER`, `ANALYST`, `ADMIN` | Recent dashboard activity |

---

## 8. DTOs

DTO package:

- `src/main/java/com/finance/platform/dto`

Main request DTOs:

- `CreateUserRequest`
- `UpdateUserRequest`
- `CreateRecordRequest`
- `UpdateRecordRequest`

Main response DTOs:

- `UserResponse`
- `RecordResponse`
- `DashboardSummary`
- `CategoryAggregation`
- `MonthlyTrend`
- `ApiError`

Why DTOs are used:

- prevent direct entity exposure
- enforce request validation
- avoid leaking sensitive fields like password
- keep API contracts stable as entities evolve

---

## 9. Validation and Error Handling

Validation is applied at the controller boundary through Jakarta Bean Validation.

Examples:

- `amount` must be at least `0.01`
- `category` max length is `50`
- `description` max length is `500`
- `email` must be valid
- `password` must be between `6` and `100` characters

Global handler:

- [GlobalExceptionHandler.java](../src/main/java/com/finance/platform/exception/GlobalExceptionHandler.java)

Custom exceptions:

- `ResourceNotFoundException` -> `404`
- `DuplicateResourceException` -> `409`
- `BusinessRuleException` -> `422`
- `IllegalArgumentException` -> `400`
- `MethodArgumentTypeMismatchException` -> `400`

Security error responses:

- unauthenticated -> handled by `CustomAuthEntryPoint`
- forbidden -> handled by `CustomAccessDeniedHandler`

Error response shape:

```json
{
  "timestamp": "2025-01-15T10:30:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Amount must be positive",
  "path": "/api/records"
}
```

Validation errors may also include:

```json
{
  "fieldErrors": {
    "amount": "Amount must be positive (minimum 0.01)"
  }
}
```

---

## 10. Persistence

Database:

- H2 in-memory database

Configuration:

- [application.properties](../src/main/resources/application.properties)

Important notes:

- data is reset when the application restarts
- H2 console is enabled
- `spring.jpa.open-in-view=false` is set
- the JDBC URL is generated at runtime unless explicitly configured

Repository highlights:

- [UserRepository.java](../src/main/java/com/finance/platform/repository/UserRepository.java)
- [FinancialRecordRepository.java](../src/main/java/com/finance/platform/repository/FinancialRecordRepository.java)

Notable query features:

- aggregate totals in SQL
- category grouping in SQL
- monthly trend aggregation in SQL
- specification-based filtering for records

---

## 11. Seed Data

Startup seeding:

- [DataSeeder.java](../src/main/java/com/finance/platform/config/DataSeeder.java)

Seeded users:

| Email | Password | Role |
|---|---|---|
| `admin@finance.com` | `admin123` | `ADMIN` |
| `analyst@finance.com` | `analyst123` | `ANALYST` |
| `viewer@finance.com` | `viewer123` | `VIEWER` |

Seeded records:

- 19 financial records
- includes salary, rent, utilities, groceries, investment, insurance, transport, and education

Current seeded dashboard totals:

- total income: `243000.00`
- total expenses: `164500.00`
- net balance: `78500.00`
- record count: `19`

---

## 12. Example Flows

### 12.1 Viewer Flow

Allowed:

- load dashboard summary
- load category chart
- load trend chart
- load dashboard recent activity

Blocked:

- any record listing or record CRUD
- any user management

### 12.2 Analyst Flow

Allowed:

- everything a viewer can do
- list records
- filter records
- inspect recent records

Blocked:

- create/update/delete records
- any user management

### 12.3 Admin Flow

Allowed:

- everything an analyst can do
- create/update/delete records
- create/update users
- change roles and statuses

---

## 13. Testing

Tests live in:

- `src/test/java/com/finance/platform`

Current tests:

- [FinancePlatformApplicationTests.java](../src/test/java/com/finance/platform/FinancePlatformApplicationTests.java)
  Confirms the Spring context loads.
- [FinancePlatformApiIntegrationTests.java](../src/test/java/com/finance/platform/FinancePlatformApiIntegrationTests.java)
  Verifies:
  - viewer can access dashboard but not records
  - analyst can read filtered records
  - analyst cannot create records
  - invalid date ranges return `400`
  - invalid query parameter values return `400`
  - H2 console is not publicly accessible

Run tests:

```bash
./mvnw test
```

---

## 14. Running Locally

Start the app:

```bash
./mvnw spring-boot:run
```

Application:

- default server URL: `http://localhost:8080`

H2 console:

- `http://localhost:8080/h2-console`
- requires `ADMIN` application credentials via HTTP Basic Auth
- JDBC URL: use the generated in-memory JDBC URL shown in startup logs
- username: `sa`
- password: empty

---

## 15. Current Limitations

This project is intentionally simple and currently has these tradeoffs:

- no token-based authentication; it uses HTTP Basic
- no production database configuration
- no record ownership scoping beyond `createdBy`
- no full audit/event history beyond timestamps and soft delete
- no search beyond explicit filters
- no API documentation generator such as OpenAPI/Swagger
- pagination uses default Spring `Page` JSON serialization, which is acceptable for now but not ideal for long-term API stability

---

## 16. Suggested Next Improvements

If this project is taken further, the most useful next steps are:

1. add OpenAPI documentation
2. move from Basic Auth to JWT-based authentication
3. add controller-level tests for user management endpoints
4. add record ownership or tenant boundaries if needed
5. move from H2 to PostgreSQL for persistent environments
6. standardize paginated responses via DTOs

---

## 17. Reference Files

Core files worth reading first:

- [README.md](../README.md)
- [FinancePlatformApplication.java](../src/main/java/com/finance/platform/FinancePlatformApplication.java)
- [SecurityConfig.java](../src/main/java/com/finance/platform/security/SecurityConfig.java)
- [FinancialRecordController.java](../src/main/java/com/finance/platform/controller/FinancialRecordController.java)
- [DashboardController.java](../src/main/java/com/finance/platform/controller/DashboardController.java)
- [UserController.java](../src/main/java/com/finance/platform/controller/UserController.java)
- [FinancialRecordServiceImpl.java](../src/main/java/com/finance/platform/service/impl/FinancialRecordServiceImpl.java)
- [DashboardServiceImpl.java](../src/main/java/com/finance/platform/service/impl/DashboardServiceImpl.java)
- [UserServiceImpl.java](../src/main/java/com/finance/platform/service/impl/UserServiceImpl.java)
- [FinancialRecordRepository.java](../src/main/java/com/finance/platform/repository/FinancialRecordRepository.java)
- [GlobalExceptionHandler.java](../src/main/java/com/finance/platform/exception/GlobalExceptionHandler.java)
