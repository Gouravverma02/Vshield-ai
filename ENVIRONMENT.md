# VShield.ai — Environment Configuration

*Day 3 Deliverable — every tool, config value, and setting the project depends on*

---

## 1. Local Development Environment

| Item | Value |
|---|---|
| Operating System (developer machine) | Windows |
| Java Version | 22.0.1 (OpenJDK) |
| Java Home | `C:\Program Files\Java\jdk-22` |
| IDE | IntelliJ IDEA Community Edition 2025.2.1 |
| Git Version | 2.51.0.windows.1 |
| Project Location | `C:\Users\verma\IdeaProjects\vshield-ai` |
| Build Tool | Maven (via bundled wrapper `mvnw` / `mvnw.cmd`) |

**No `.env` file or OS-level environment variables are required.** All configuration lives in `src/main/resources/application.properties`, committed to the repository (safe to commit because it contains no secrets — no API keys, no real passwords, no external credentials, matching the PRD's "no external AI/API dependency" design).

---

## 2. `application.properties` — Full Reference

```properties
# Application identity
spring.application.name=vshield

# Server settings
server.port=8080

# Database connection - H2 file-based storage
spring.datasource.url=jdbc:h2:file:./data/vshielddb
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=

# JPA / Hibernate settings
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true

# H2 web console
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console
```

| Property | Value | Purpose |
|---|---|---|
| `spring.application.name` | `vshield` | Cosmetic label shown in logs |
| `server.port` | `8080` | Port the app runs on locally (will differ once deployed — see Day 9 notes below) |
| `spring.datasource.url` | `jdbc:h2:file:./data/vshielddb` | Points to a real file-based database at `<project root>/data/vshielddb.mv.db`, so data survives restarts |
| `spring.datasource.driverClassName` | `org.h2.Driver` | Tells Spring which JDBC driver to use |
| `spring.datasource.username` / `password` | `sa` / *(blank)* | H2's default development credentials — not used in production since v1.0 has no separate DB server |
| `spring.jpa.hibernate.ddl-auto` | `update` | Automatically creates/updates database tables to match our `@Entity` classes — no manual SQL needed during development |
| `spring.jpa.show-sql` / `format_sql` | `true` | Prints the actual SQL Hibernate runs to the console — useful for debugging, will likely be turned off before Day 9 deployment to reduce log noise |
| `spring.h2.console.enabled` | `true` | Enables the browser-based database viewer at `/h2-console` — **development only** |
| `spring.h2.console.path` | `/h2-console` | The URL path for that console |

---

## 3. Maven Dependencies (`pom.xml`)

| Dependency | Purpose |
|---|---|
| `spring-boot-starter-web` | REST API + embedded Tomcat web server |
| `spring-boot-devtools` | Auto-restart on code changes during development |
| `spring-boot-starter-data-jpa` | ORM — turns Java classes into database tables |
| `h2` | Embedded relational database |
| `spring-boot-starter-security` | Authentication framework, password hashing (BCrypt) |
| `spring-boot-starter-validation` | `@Valid`, `@NotBlank`, `@Email`, `@Size` annotations for input validation |

---

## 4. Database File Location

The H2 database file lives at:
```
vshield-ai/data/vshielddb.mv.db
```

This folder is **excluded from Git** via `.gitignore` (database contents — including real or test user data — should never be committed to source control). If this file is deleted, the app will simply recreate an empty database on next startup (tables rebuilt automatically via `ddl-auto=update`), but all existing data will be lost — this is expected development behavior, not a bug.

---

## 5. Ports and Local URLs

| URL | Purpose |
|---|---|
| `http://localhost:8080` | Base application URL |
| `http://localhost:8080/index.html` | Frontend landing page |
| `http://localhost:8080/api/health` | Backend health check (public) |
| `http://localhost:8080/api/auth/signup` | Signup endpoint (public, POST only) |
| `http://localhost:8080/api/auth/login` | Login endpoint (public, POST only) |
| `http://localhost:8080/api/auth/me` | Session check (protected, GET) |
| `http://localhost:8080/h2-console` | Database inspection tool (dev only) |

---

## 6. Security Configuration Summary

Defined in `com.vshield.vshield.config.SecurityConfig`:

- **Public routes** (no login required): `/api/health`, `/api/auth/**`, `/h2-console/**`, all static frontend files (`/`, `*.html`, `*.css`, `*.js`)
- **Protected routes** (login required): everything else, including all future `/api/analyze` and `/api/history/**` endpoints
- **Password hashing:** BCrypt, via Spring Security's `BCryptPasswordEncoder`
- **Session mechanism:** server-side `HttpSession` — `userId` and `userEmail` are stored in the session upon successful login; no JWT, no client-stored tokens (per ARCHITECTURE.md's stated design principle)
- **CSRF protection:** disabled — appropriate for a stateless-style JSON API called via `fetch()`, not a traditional server-rendered form app

---

## 7. Known Environment Deviations from Original Blueprint

Documented fully in `Blueprint_Addendum_Day2.docx`; summarized here for quick reference:

| Item | Blueprint Said | Actual |
|---|---|---|
| Java version | 17 | 22 (backward compatible; Day 9 deployment must configure the host for Java 22) |
| H2 mode | Not specified | File-based (`jdbc:h2:file:...`), applied Day 3 before building `User` entity |

---

## 8. Environment Variables Needed for Day 9 (Deployment) — Preview Only

Not yet configured (deployment happens Day 9), but noted here for forward planning:

| Variable | Purpose | Status |
|---|---|---|
| `PORT` | Some free hosting platforms require the app to read its port from an environment variable instead of a hardcoded `8080` | To be addressed Day 9 |
| Database file path | File-based H2 needs a writable disk location on the host | To be confirmed once the Day 9 hosting platform is chosen |

No secrets, API keys, or credentials are anticipated for v1.0, since the PRD explicitly excludes external AI/API dependencies and OAuth.
