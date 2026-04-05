# Finance Platform Backend

Spring Boot backend for a finance dashboard system with role-based access control, financial record management, dashboard analytics, validation, and H2-backed persistence.

Detailed design and code walkthrough: [Detailed Documentation](docs/DETAILED_DOCUMENTATION.md)

API testing artifacts:

- Postman collection: `postman/Finance Platform API.postman_collection.json`
- Postman environment: `postman/Finance Platform Local.postman_environment.json`

## What This Supports

- user management with `VIEWER`, `ANALYST`, and `ADMIN` roles
- active/inactive user status
- financial record CRUD with soft delete
- filtering by date range, category, and type
- dashboard summary, category totals, trends, and recent activity
- backend-enforced access control
- structured validation and error responses
- H2 in-memory persistence for local/demo use

## Tech Stack

- Java 17
- Spring Boot 4.0.5
- Spring Web
- Spring Data JPA
- Spring Security
- Jakarta Bean Validation
- H2 Database
- JUnit 5 + Spring Security Test

## Running Locally

Start the app:

```bash
./mvnw spring-boot:run
```

Run tests:

```bash
./mvnw test
```

## GitHub Submission Notes

- commit the source code, docs, and the `postman/` folder
- keep exported Postman files in `postman/`
- import the Postman collection and environment together for ready-to-run local API testing
- do not commit `target/` or local IDE files

App URL:

- `http://localhost:8080`

H2 console:

- `http://localhost:8080/h2-console`
- enabled for local database inspection
- JDBC URL: `jdbc:h2:mem:financeDB`
- username: `sa`
- password: empty

## Seed Users

| Email | Password | Role |
|---|---|---|
| `admin@finance.com` | `admin123` | `ADMIN` |
| `analyst@finance.com` | `analyst123` | `ANALYST` |
| `viewer@finance.com` | `viewer123` | `VIEWER` |

## Access Model

| Capability | VIEWER | ANALYST | ADMIN |
|---|---|---|---|
| View dashboard APIs | Yes | Yes | Yes |
| View records | No | Yes | Yes |
| Create/update/delete records | No | No | Yes |
| Manage users | No | No | Yes |

## Endpoint Reference

Base URL:

- `http://localhost:8080`

Authentication:

- all API endpoints use HTTP Basic Auth
- use one of the seeded email/password combinations above

### User Management Endpoints

#### `POST /api/users`

Access:

- `ADMIN`

Purpose:

- create a new user

Request body:

```json
{
  "name": "Risk Analyst",
  "email": "risk@finance.com",
  "password": "risk123",
  "role": "ANALYST"
}
```

Success response:

- `201 Created`

```json
{
  "id": 4,
  "name": "Risk Analyst",
  "email": "risk@finance.com",
  "role": "ANALYST",
  "status": "ACTIVE",
  "createdAt": "2026-04-05T11:00:00",
  "updatedAt": "2026-04-05T11:00:00"
}
```

Common failures:

- `400` invalid input
- `409` email already exists

#### `GET /api/users`

Access:

- `ADMIN`

Purpose:

- list all users

Success response:

- `200 OK`

```json
[
  {
    "id": 1,
    "name": "Admin User",
    "email": "admin@finance.com",
    "role": "ADMIN",
    "status": "ACTIVE",
    "createdAt": "2026-04-05T11:00:00",
    "updatedAt": "2026-04-05T11:00:00"
  },
  {
    "id": 2,
    "name": "Analyst User",
    "email": "analyst@finance.com",
    "role": "ANALYST",
    "status": "ACTIVE",
    "createdAt": "2026-04-05T11:00:00",
    "updatedAt": "2026-04-05T11:00:00"
  }
]
```

#### `GET /api/users/{id}`

Access:

- `ADMIN`

Purpose:

- fetch a single user by ID

Example:

- `GET /api/users/1`

Success response:

- `200 OK`

```json
{
  "id": 1,
  "name": "Admin User",
  "email": "admin@finance.com",
  "role": "ADMIN",
  "status": "ACTIVE",
  "createdAt": "2026-04-05T11:00:00",
  "updatedAt": "2026-04-05T11:00:00"
}
```

Common failure:

- `404` user not found

#### `PUT /api/users/{id}`

Access:

- `ADMIN`

Purpose:

- update user name and email

Request body:

```json
{
  "name": "Lead Analyst",
  "email": "analyst.lead@finance.com"
}
```

Success response:

- `200 OK`

```json
{
  "id": 2,
  "name": "Lead Analyst",
  "email": "analyst.lead@finance.com",
  "role": "ANALYST",
  "status": "ACTIVE",
  "createdAt": "2026-04-05T11:00:00",
  "updatedAt": "2026-04-05T11:05:00"
}
```

Common failures:

- `400` invalid request body
- `404` user not found
- `409` email already in use

#### `PATCH /api/users/{id}/role`

Access:

- `ADMIN`

Purpose:

- change user role only

Request body:

```json
{
  "role": "VIEWER"
}
```

Success response:

- `200 OK`

```json
{
  "id": 2,
  "name": "Analyst User",
  "email": "analyst@finance.com",
  "role": "VIEWER",
  "status": "ACTIVE",
  "createdAt": "2026-04-05T11:00:00",
  "updatedAt": "2026-04-05T11:10:00"
}
```

Common failures:

- `400` invalid or missing role
- `404` user not found

#### `PATCH /api/users/{id}/status`

Access:

- `ADMIN`

Purpose:

- activate or deactivate a user

Request body:

```json
{
  "status": "INACTIVE"
}
```

Success response:

- `200 OK`

```json
{
  "id": 3,
  "name": "Viewer User",
  "email": "viewer@finance.com",
  "role": "VIEWER",
  "status": "INACTIVE",
  "createdAt": "2026-04-05T11:00:00",
  "updatedAt": "2026-04-05T11:15:00"
}
```

Common failures:

- `400` invalid or missing status
- `404` user not found

### Financial Record Endpoints

#### `POST /api/records`

Access:

- `ADMIN`

Purpose:

- create a new financial record

Request body:

```json
{
  "amount": 15000.50,
  "type": "INCOME",
  "category": "SALARY",
  "date": "2025-01-15",
  "description": "January salary credit"
}
```

Success response:

- `201 Created`

```json
{
  "id": 20,
  "amount": 15000.50,
  "type": "INCOME",
  "category": "SALARY",
  "date": "2025-01-15",
  "description": "January salary credit",
  "createdBy": 1,
  "createdAt": "2026-04-05T11:20:00",
  "updatedAt": "2026-04-05T11:20:00"
}
```

Common failures:

- `400` invalid body
- `401` missing or invalid credentials
- `403` non-admin user

#### `GET /api/records`

Access:

- `ANALYST`, `ADMIN`

Purpose:

- list records with optional filtering and pagination

Supported query parameters:

- `startDate`
- `endDate`
- `category`
- `type`
- `page`
- `size`
- `sort`

Example request:

```http
GET /api/records?startDate=2025-03-01&endDate=2025-03-31&category=SALARY&type=INCOME&page=0&size=20&sort=date,desc
```

Success response:

- `200 OK`

```json
{
  "content": [
    {
      "id": 3,
      "amount": 75000.00,
      "type": "INCOME",
      "category": "SALARY",
      "date": "2025-03-01",
      "description": "March salary",
      "createdBy": 1,
      "createdAt": "2026-04-05T11:00:00",
      "updatedAt": "2026-04-05T11:00:00"
    }
  ],
  "totalElements": 1,
  "totalPages": 1,
  "size": 20,
  "number": 0
}
```

Common failures:

- `400` invalid query parameter or invalid date range
- `403` viewer access denied

#### `GET /api/records/{id}`

Access:

- `ANALYST`, `ADMIN`

Purpose:

- fetch one active financial record

Example:

- `GET /api/records/1`

Success response:

- `200 OK`

```json
{
  "id": 1,
  "amount": 75000.00,
  "type": "INCOME",
  "category": "SALARY",
  "date": "2025-01-01",
  "description": "January salary",
  "createdBy": 1,
  "createdAt": "2026-04-05T11:00:00",
  "updatedAt": "2026-04-05T11:00:00"
}
```

Common failure:

- `404` record not found or soft-deleted

#### `GET /api/records/recent`

Access:

- `ANALYST`, `ADMIN`

Purpose:

- fetch the latest 10 records

Success response:

- `200 OK`

```json
[
  {
    "id": 19,
    "amount": 3000.00,
    "type": "INCOME",
    "category": "INVESTMENT",
    "date": "2025-03-20",
    "description": "Stock sale proceeds",
    "createdBy": 1,
    "createdAt": "2026-04-05T11:00:00",
    "updatedAt": "2026-04-05T11:00:00"
  }
]
```

#### `PUT /api/records/{id}`

Access:

- `ADMIN`

Purpose:

- replace all editable fields of a record

Request body:

```json
{
  "amount": 16000.00,
  "type": "INCOME",
  "category": "SALARY",
  "date": "2025-01-15",
  "description": "Corrected January salary"
}
```

Success response:

- `200 OK`

```json
{
  "id": 1,
  "amount": 16000.00,
  "type": "INCOME",
  "category": "SALARY",
  "date": "2025-01-15",
  "description": "Corrected January salary",
  "createdBy": 1,
  "createdAt": "2026-04-05T11:00:00",
  "updatedAt": "2026-04-05T11:25:00"
}
```

Common failures:

- `400` invalid body
- `404` record not found

#### `DELETE /api/records/{id}`

Access:

- `ADMIN`

Purpose:

- soft-delete a record

Example:

- `DELETE /api/records/1`

Success response:

- `204 No Content`

Common failure:

- `404` record not found

### Dashboard Endpoints

#### `GET /api/dashboard/summary`

Access:

- `VIEWER`, `ANALYST`, `ADMIN`

Purpose:

- get total income, total expenses, net balance, and active record count

Success response with seeded data:

- `200 OK`

```json
{
  "totalIncome": 243000.00,
  "totalExpenses": 164500.00,
  "netBalance": 78500.00,
  "recordCount": 19
}
```

#### `GET /api/dashboard/categories`

Access:

- `VIEWER`, `ANALYST`, `ADMIN`

Purpose:

- get category-wise totals

Success response:

- `200 OK`

```json
[
  {
    "category": "SALARY",
    "totalAmount": 225000.00,
    "count": 3
  },
  {
    "category": "INSURANCE",
    "totalAmount": 45000.00,
    "count": 1
  }
]
```

#### `GET /api/dashboard/trends`

Access:

- `VIEWER`, `ANALYST`, `ADMIN`

Purpose:

- get month-wise income and expense trends

Success response:

- `200 OK`

```json
[
  {
    "year": 2025,
    "month": 3,
    "income": 78000.00,
    "expense": 51800.00
  },
  {
    "year": 2025,
    "month": 2,
    "income": 83000.00,
    "expense": 37700.00
  }
]
```

#### `GET /api/dashboard/recent`

Access:

- `VIEWER`, `ANALYST`, `ADMIN`

Purpose:

- get recent activity for dashboard widgets

Success response:

- `200 OK`

```json
[
  {
    "id": 19,
    "amount": 3000.00,
    "type": "INCOME",
    "category": "INVESTMENT",
    "date": "2025-03-20",
    "description": "Stock sale proceeds",
    "createdBy": 1,
    "createdAt": "2026-04-05T11:00:00",
    "updatedAt": "2026-04-05T11:00:00"
  }
]
```

## Quick Manual Testing Examples

Dashboard summary as viewer:

```bash
curl -u viewer@finance.com:viewer123 http://localhost:8080/api/dashboard/summary
```

Filtered records as analyst:

```bash
curl -u analyst@finance.com:analyst123 "http://localhost:8080/api/records?category=SALARY&type=INCOME"
```

Create record as admin:

```bash
curl -u admin@finance.com:admin123 \
  -H "Content-Type: application/json" \
  -d '{"amount":100.00,"type":"EXPENSE","category":"SNACKS","date":"2025-03-10","description":"Team snacks"}' \
  http://localhost:8080/api/records
```

## Validation and Error Handling

The API validates request bodies and query parameters and returns structured JSON errors with appropriate status codes such as:

- `400 Bad Request` for invalid input
- `401 Unauthorized` for missing or invalid credentials
- `403 Forbidden` for role violations
- `404 Not Found` for missing resources
- `409 Conflict` for duplicate resources
- `422 Unprocessable Content` for business rule violations

Example error shape:

```json
{
  "timestamp": "2025-01-15T10:30:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Amount must be positive",
  "path": "/api/records"
}
```

## Persistence Notes

- uses H2 in-memory storage
- data resets on application restart
- intended for local development/demo simplicity
- can be upgraded to PostgreSQL or another persistent database later

## Project Layout

- `src/main/java/com/finance/platform/controller` for REST controllers
- `src/main/java/com/finance/platform/service` for service contracts
- `src/main/java/com/finance/platform/service/impl` for business logic
- `src/main/java/com/finance/platform/repository` for JPA repositories
- `src/main/java/com/finance/platform/entity` for persistence entities
- `src/main/java/com/finance/platform/security` for authentication and authorization
- `src/main/java/com/finance/platform/exception` for API error handling
- `src/test/java/com/finance/platform` for tests

## Notes

- authentication is implemented with HTTP Basic for simplicity
- records are soft-deleted rather than physically removed
- dashboard aggregations are computed in the database layer
- paginated record responses currently use Spring `Page` serialization
