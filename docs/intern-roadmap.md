# Intern Roadmap

This project ships with **partial** functionality on purpose. Use the implemented user registration, login, and patient-update slices as the pattern to copy. The **Doctor** resource has no schema and no code—design it from scratch.

Product requirements for each remaining capability are in [tasks/README.md](tasks/README.md), one file per capability. This roadmap is the suggested pace. Follow the task file for what the product must do.

Suggested pace: 1–2 milestones per week. Adjust freely.

## Roles reminder

| Role | Intended capabilities |
|------|------------------------|
| `PATIENT` | Register, view own chart, book appointments (future) |
| `DOCTOR` | Update patient charts, manage appointments |
| `ADMIN` | Everything doctors can do + user administration |

---

## Phase 0 — Orientation (Day 1)

**Goal:** Run the stack and understand the reference slices.

Acceptance criteria:

- [ ] `docker compose up --build` succeeds
- [ ] Swagger UI loads and login works with the seeded admin
- [ ] You can identify controller → service → repository for users and patients
- [ ] You can explain how JWT auth and `@PreAuthorize` protect `PUT /api/v1/patients/{id}`

---

## Phase 1 — Doctor domain design (Week 1)

**Goal:** Propose the Doctor resource with **no production code yet**.

Deliverables:

- [ ] Short design note (`docs/design/doctor.md`) covering:
  - Fields (e.g. full name, specialty, license number, department)
  - Indexes and uniqueness rules
  - Relationship to `Patient` (embedded `primaryDoctorName` today vs. `primaryDoctorId` reference)
  - API sketch (paths, methods, roles)
- [ ] Review/approval from mentor before coding

Acceptance criteria:

- [ ] Design answers create/read/update/delete + list/search
- [ ] Migration plan described (indexes + optional seed doctors)
- [ ] Authz matrix defined per endpoint

---

## Phase 2 — Doctor persistence + migration (Week 1–2)

**Goal:** Create Mongo collection support the way patients/users do.

Deliverables:

- [ ] `DoctorDocument` model
- [ ] `DoctorRepository`
- [ ] Mongock change unit for indexes (and optional seeds)
- [ ] Unit tests for any mapping/normalization helpers

Acceptance criteria:

- [ ] App starts with the new migration applied
- [ ] Unique constraints behave as designed
- [ ] No Doctor HTTP API yet (or only behind a feature flag—prefer none until Phase 3)

---

## Phase 3 — Doctor API vertical slice (Week 2)

**Goal:** Implement at least one full Doctor operation end-to-end, then complete CRUD.

Recommended order (mirror the starter style):

1. `POST /api/v1/doctors` (create)
2. `GET /api/v1/doctors/{id}`
3. `GET /api/v1/doctors` (pagination + search)
4. `PUT /api/v1/doctors/{id}`
5. `DELETE /api/v1/doctors/{id}`

Acceptance criteria for each endpoint:

- [ ] Validation + `ApiError` responses
- [ ] Correct role checks
- [ ] Service unit tests
- [ ] Integration tests (auth success/failure + validation)
- [ ] OpenAPI annotations updated

---

## Phase 4 — Wire Doctors into Patients (Week 3)

**Goal:** Replace or complement the free-text `primaryDoctorName` on patients.

Options (pick one with mentor):

- Store `primaryDoctorId` on `PatientDocument` and resolve doctor details in responses
- Keep `primaryDoctorName` denormalized and sync on doctor rename

Acceptance criteria:

- [ ] Migration strategy for existing seeded patients
- [ ] Patient update/create validation rejects unknown doctors (if using references)
- [ ] Tests cover the relationship rules

Also complete remaining Patient APIs using the update slice as a template:

- [ ] `POST /api/v1/patients`
- [ ] `GET /api/v1/patients/{id}`
- [ ] `GET /api/v1/patients`
- [ ] `DELETE /api/v1/patients/{id}`

---

## Phase 5 — Appointments (Week 3–4)

**Goal:** Introduce a new aggregate from scratch (similar ambition to Doctors).

Suggested model: `Appointment` with `patientId`, `doctorId`, `scheduledAt`, `status`, `reason`.

Acceptance criteria:

- [ ] A patient can book an appointment with an existing doctor
- [ ] Double-booking the same doctor at the same time fails
- [ ] Cancel marks the appointment cancelled
- [ ] Doctors can list upcoming appointments
- [ ] Full test + OpenAPI coverage

---

## Phase 6 — User administration & hardening (Week 4+)

Stretch goals:

- [ ] Admin endpoints to list users, change roles, enable/disable accounts
- [ ] Refresh tokens or shorter-lived access tokens
- [ ] Audit fields / request logging
- [ ] CI workflow running `mvn test`
- [ ] Rate limiting / lockout for login brute force

---

## Definition of Done (every feature)

1. Code follows existing package and naming patterns
2. Validations and authorization are explicit
3. Mongock migration added when persistence shape changes
4. Unit + integration tests pass locally
5. Swagger documents the new surface area
6. Roadmap checkboxes updated in this file

## Reference endpoints (already done)

Use these as the gold standard:

- `POST /api/v1/users`
- `POST /api/v1/auth/login`
- `PUT /api/v1/patients/{id}`
