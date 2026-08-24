# LMS Backend — Chemistry Teacher Platform

Backend API for a platform where a chemistry teacher (and potentially multiple
teachers in future) can publish study materials, sell class recordings, and
manage students — recordings and paid books stay locked until a student
registers and pays.

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 17 |
| Framework | Spring Boot 4.1.0 |
| Persistence | Spring Data JPA + Hibernate, `jakarta.persistence` |
| Database | PostgreSQL 16 |
| Schema migrations | Flyway |
| Auth | Spring Security 7 + JWT (jjwt) |
| Validation | Jakarta Bean Validation |
| API docs | springdoc-openapi (Swagger UI) |
| Boilerplate reduction | Lombok (DTOs only — models use explicit getters/setters) |

## Architecture

Layered modular monolith — single deployable Spring Boot application,
internally organized into clear modules so it *could* be split into services
later without having paid that complexity cost prematurely.

```
controller  → REST endpoints, grouped by audience (public / student / teacher-admin / super-admin)
service     → business logic
repository  → Spring Data JPA data access
model       → JPA entities
dto         → request/response shapes — controllers never expose entities directly
mapper      → single conversion point per entity (Model -> DTO)
security    → JWT issuing/validation, Spring Security wiring
payment     → PaymentProvider interface (Strategy pattern) — swappable gateway
storage     → StorageService interface — swappable file storage (books/PDFs)
video       → VideoService interface — swappable video hosting (recordings)
exception   → centralized error handling, one consistent JSON error shape
util        → shared constants (API paths, validation messages)
```

### Multi-tenancy

Every content table (`book`, `recording`, `app_order`) carries a `teacher_id`.
A single shared database/schema is used, with tenant isolation enforced in
the service layer (never trust a client-supplied `teacherId` — it's always
resolved server-side from the authenticated user).

### Roles

- `STUDENT` — default role for self-registration (`/auth/register`)
- `TEACHER_ADMIN` — can only be created by a `SUPER_ADMIN` (never self-service,
  since this role can publish paid content)
- `SUPER_ADMIN` — bootstrapped automatically on first run (see `DataSeeder`)
  if none exists yet

### Access control

`AccessControlService` is the single source of truth for "does this user
have access to this item" — every protected content endpoint calls
`hasAccess()`, never re-implements the check inline. Access is tracked in the
`access_grant` table, separate from `app_order`, so manual admin
grants/revokes and future expiry logic never need to reinterpret raw payment
history.

## Getting Started

### Prerequisites
- JDK 17
- Maven
- PostgreSQL 16 (or Docker)

### Run locally

```bash
# 1. Start the database
docker-compose up -d db

# 2. Copy and configure environment values
cp .env.example .env
# edit .env with your local DB credentials if different from defaults

# 3. Run the app
./mvnw spring-boot:run
```

The app starts on `http://localhost:8081`, with all endpoints prefixed by
`/api` (e.g. `http://localhost:8081/api/public/health`).

### First run

On startup, if no `SUPER_ADMIN` account exists, one is created automatically
(see `DataSeeder`) using `app.bootstrap.super-admin-email` /
`app.bootstrap.super-admin-password` from `application.properties` (defaults
provided for local dev — **change these before deploying anywhere real**).

### Verify it's working

```bash
curl http://localhost:8081/api/public/health
# {"status":"ok"}
```

Swagger UI: `http://localhost:8081/api/docs`

## Environment Variables

| Variable | Purpose |
|---|---|
| `DB_URL`, `DB_USERNAME`, `DB_PASSWORD` | PostgreSQL connection |
| `JWT_SECRET` | Signing key for JWTs — **must** be a long random value in any real environment |
| `JWT_ACCESS_EXPIRY_MIN` | Token lifetime in minutes |
| `ALLOWED_ORIGINS` | Comma-separated frontend origin(s) for CORS |
| `SERVER_PORT` | Port the app listens on |
| `app.bootstrap.super-admin-email` / `-password` | Bootstrap super-admin credentials, first run only |
| `app.payment.payhere.*` | PayHere merchant credentials (not yet active — see Roadmap) |
| `app.storage.r2.*` | Cloudflare R2 credentials (not yet active — see Roadmap) |
| `app.video.bunny.*` | Bunny Stream credentials (not yet active — see Roadmap) |

## API Overview

All routes are prefixed with `/api`.

| Method | Path | Auth | Description |
|---|---|---|---|
| POST | `/auth/register` | — | Student self-registration |
| POST | `/auth/login` | — | Login (any role) |
| GET | `/public/health` | — | Health check |
| GET | `/public/books` | — | Published books catalog |
| GET | `/public/books/{id}` | — | Single published book |
| GET | `/public/recordings` | — | Published recordings catalog |
| GET | `/public/recordings/{id}` | — | Single published recording |
| POST | `/super-admin/teachers` | SUPER_ADMIN | Create a teacher account |
| POST | `/teacher-admin/books` | TEACHER_ADMIN | Create a book (draft) |
| GET | `/teacher-admin/books` | TEACHER_ADMIN | List own books (incl. drafts) |
| PATCH | `/teacher-admin/books/{id}/publish` | TEACHER_ADMIN | Publish own book |
| POST | `/teacher-admin/recordings` | TEACHER_ADMIN | Create a recording (draft) |
| GET | `/teacher-admin/recordings` | TEACHER_ADMIN | List own recordings |
| PATCH | `/teacher-admin/recordings/{id}/publish` | TEACHER_ADMIN | Publish own recording |
| POST | `/student/orders` | any authenticated user | Purchase a book or recording |
| GET | `/student/books/{id}/content` | any authenticated user | Access a book's content (gated) |

Full, always-current documentation: Swagger UI at `/api/docs`.

## Known Gaps / Not Yet Production-Ready

This project is under active development. Before real users and real
payments touch it, the following are **not yet done**:

- **Payment is simulated.** Every order is marked `PAID` immediately with no
  real gateway involved (`OrderService`). `PaymentProvider` interface and a
  `PayHereProvider` scaffold exist, but need real merchant credentials and
  the actual checkout/webhook implementation before going live.
- **File/video links are not signed or expiring.** Books return a raw
  `fileUrl`; recordings have no streaming endpoint yet. `StorageService`
  (Cloudflare R2) and `VideoService` (Bunny Stream) interfaces exist as
  scaffolding only.
- **No automated tests yet**, especially needed for `AccessControlService`
  and `OrderService` given real money will depend on their correctness.
- **Error responses currently leak real exception messages** (`GlobalExceptionHandler`)
  — useful for development, must be made environment-aware (generic message
  in production) before deploying.
- **No rate limiting** on `/auth/login` or `/auth/register`.
- **JWT secret and all credentials must be moved to real environment
  variables** before deployment — current `application.properties` defaults
  are for local development only.

## Roadmap

1. Environment-aware error responses (dev vs. prod)
2. Real `PayHereProvider` implementation + webhook handling
3. Real `R2StorageService` (signed book downloads) + `BunnyStreamVideoService`
   (signed recording playback)
4. Unit tests for `AccessControlService` and `OrderService`
5. Student dashboard endpoint (purchase history)
6. Super-admin reporting
7. Deploy (Railway/Render + managed Postgres + Cloudflare DNS)

## Deployment

Dockerized — see `Dockerfile` and `docker-compose.yml`. Recommended hosting:
Railway or Render for the backend + managed Postgres (both build directly
from the `Dockerfile` and handle HTTPS automatically once a domain is
connected). See project notes for the full deployment walkthrough.
