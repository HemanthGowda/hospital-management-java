# Hospital Management Backend (Intern Starter)

Half-baked Spring Boot + MongoDB hospital API used as a **reference implementation** for interns.

Only a few vertical slices are fully implemented. Everything else—especially the **Doctor** resource—is intentionally missing so interns can design schema, APIs, tests, and docs from the ground up while copying the existing patterns.

## What is already implemented

| Method | Path | Auth | Purpose |
|--------|------|------|---------|
| `POST` | `/api/v1/users` | Public | Register a `PATIENT` account |
| `POST` | `/api/v1/auth/login` | Public | Issue a JWT access token |
| `PUT` | `/api/v1/patients/{id}` | `ADMIN` or `DOCTOR` | Update an existing patient chart |

Supporting pieces included:

- Layered architecture: controller → service → repository
- Bean Validation on request DTOs
- Centralized API error responses
- JWT bearer authentication with role-based access
- Mongock migrations for indexes + seed data
- Unit tests and Testcontainers integration tests
- OpenAPI / Swagger UI
- Docker Compose runtime (API + MongoDB)

## What is intentionally incomplete

- **Doctor** resource: no collection, model, repository, API, or migration. Interns own this end-to-end. Patients currently store `primaryDoctorName` as free text.
- Patient create/list/get/delete
- Appointment booking workflows
- User admin APIs (list, role changes, disable)
- Refresh tokens, password reset, email verification

Each remaining capability is specified in [docs/tasks/README.md](docs/tasks/README.md). See [docs/intern-roadmap.md](docs/intern-roadmap.md) for the suggested pace.

## Architecture

```text
Client
  │
  ▼
PatientController / UserController / AuthController
  │
  ▼
PatientService / UserService / AuthService + JwtService
  │
  ▼
PatientRepository / UserRepository
  │
  ▼
MongoDB (users, patients)  ← Mongock change units
```

Package layout under `src/main/java/com/hospital/management`:

- `auth` — login, JWT filter/service, security principal
- `user` — registration slice
- `patient` — update slice
- `config` — security, Mongo auditing, OpenAPI, Mongock
- `exception` / `api` — shared error model
- `migration` — index + seed change units

## Prerequisites

- Docker Desktop (or Docker Engine + Compose v2)
- Optional for local IDE runs: JDK 21 and Maven 3.9+

## Quick start (Docker only)

```bash
cp .env.example .env
docker compose up --build
```

Services:

- API: [http://localhost:8081](http://localhost:8081)
- Health: [http://localhost:8081/actuator/health](http://localhost:8081/actuator/health)
- Swagger UI: [http://localhost:8081/swagger-ui.html](http://localhost:8081/swagger-ui.html)
- MongoDB: `localhost:27018` / database `hospital_management`

Seeded admin (override via `.env`):

- Email: `admin@hospital.local`
- Password: `ChangeMe123!`

Host ports are `8081` and `27018` so this stack can run beside the library starter. Inside the API container the process still listens on `8080`.

### Example requests

Register:

```bash
curl -s -X POST http://localhost:8081/api/v1/users \
  -H 'Content-Type: application/json' \
  -d '{"email":"patient@example.com","password":"PatientPass1","fullName":"Ada Patient"}'
```

Login:

```bash
TOKEN=$(curl -s -X POST http://localhost:8081/api/v1/auth/login \
  -H 'Content-Type: application/json' \
  -d '{"email":"admin@hospital.local","password":"ChangeMe123!"}' \
  | jq -r .accessToken)
```

Update a patient (replace `{id}` with a seeded patient id from MongoDB or Swagger):

```bash
curl -s -X PUT "http://localhost:8081/api/v1/patients/{id}" \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{
    "fullName":"Ada Lovelace",
    "primaryDoctorName":"Dr. Grace Hopper",
    "medicalRecordNumber":"MRN-100001",
    "dateOfBirth":"1815-12-10",
    "gender":"FEMALE",
    "phone":"+15551230001",
    "bloodGroup":"O+",
    "notes":"Updated notes"
  }'
```

## Configuration

| Variable | Default | Description |
|----------|---------|-------------|
| `MONGODB_URI` | `mongodb://localhost:27018/hospital_management` | Mongo connection string |
| `SERVER_PORT` | `8080` | HTTP port inside the container. Compose maps host port `8081` to it. |
| `JWT_SECRET` | long demo secret | HMAC signing secret (min 32 chars) |
| `JWT_EXPIRATION_MS` | `3600000` | Access token lifetime |
| `JWT_ISSUER` | `hospital-management` | JWT issuer claim |
| `SEED_ADMIN_EMAIL` | `admin@hospital.local` | Admin created by migration |
| `SEED_ADMIN_PASSWORD` | `ChangeMe123!` | Admin password (hashed before insert) |
| `SEED_ADMIN_FULL_NAME` | `Hospital Admin` | Admin display name |

## Tests

```bash
./mvnw test
# or
mvn test
```

- Unit tests cover services and JWT helpers (no Docker required).
- Integration tests use Testcontainers MongoDB (Docker required).

## Local development without packaging the API image

```bash
docker compose up mongodb -d
mvn spring-boot:run -Dspring-boot.run.profiles=local
```

## Patterns interns should copy

1. Keep controllers thin; put business rules in services.
2. Validate input with Jakarta Validation annotations on DTOs.
3. Map documents ↔ responses in dedicated mapper classes.
4. Throw domain exceptions (`ConflictException`, `ResourceNotFoundException`, …) and let `GlobalExceptionHandler` shape HTTP responses.
5. Protect endpoints with `@PreAuthorize` and reuse the JWT filter already in place.
6. Add Mongock `@ChangeUnit` classes for indexes and deterministic seeds.
7. Add both unit and HTTP integration tests for every new feature.
8. Document every new endpoint in OpenAPI annotations and update the intern roadmap status.

## Troubleshooting

| Symptom | Likely fix |
|---------|------------|
| API unhealthy on first boot | Wait for Mongo health + Mongock; check `docker compose logs api` |
| `401` on patient update | Login again; send `Authorization: Bearer <token>` |
| `403` on patient update | Use an `ADMIN`/`DOCTOR` account (seeded admin works) |
| JWT secret error on startup | Set `JWT_SECRET` to ≥ 32 characters |
| Integration tests fail | Ensure Docker is running for Testcontainers |
| Port already in use | Host defaults are `8081` (API) and `27018` (MongoDB) |

## License

Internal training material. Use and extend freely within your internship program.
