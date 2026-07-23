# VShield.ai — Day 3 Summary

*Project Setup & Foundation — Authentication Scaffold*

---

## 🎯 Today's Objective

Build the project's foundation: finalize the database connection, scaffold authentication, and prove a working end-to-end "signup → database → login" flow — without yet building the polished signup/login UI (that's Day 3 of the original Blueprint's *feature* work, addressed via clean HTML forms starting tomorrow).

---

## ✅ What Was Completed Today

### Configuration
- Switched H2 database from in-memory to **file-based** (`jdbc:h2:file:./data/vshielddb`), so data now survives app restarts.
- Full `application.properties` configured: server settings, JPA/Hibernate behavior, H2 console access.

### Code — Authentication Foundation

| File | Package | Purpose |
|---|---|---|
| `User.java` | `model` | JPA entity mapping to the `USERS` table (id, email, passwordHash, createdAt) |
| `UserRepository.java` | `repository` | Database access — `findByEmail`, `existsByEmail`, plus all standard CRUD via `JpaRepository` |
| `SecurityConfig.java` | `config` | Defines public vs. protected routes; configures BCrypt password hashing |
| `SignupRequest.java` | `dto` | Validated request shape for signup (`@Email`, `@Size(min=8)`) |
| `LoginRequest.java` | `dto` | Validated request shape for login |
| `AuthController.java` | `Controller` | Implements `POST /api/auth/signup`, `POST /api/auth/login`, `GET /api/auth/me` |

### Verified Working End-to-End
1. ✅ App starts cleanly with file-based database connected
2. ✅ `/api/health` remains public (no login prompt)
3. ✅ Signup creates a real user with a BCrypt-hashed password
4. ✅ User confirmed present in the `USERS` table via H2 console (id, email, hash, timestamp all correct)
5. ✅ Duplicate signup correctly blocked with `409`-style error message
6. ✅ Login with correct credentials returns success and starts a session
7. ✅ All protected-route rules from `SecurityConfig` match API.md's authentication spec

### Repository
- Confirmed branching strategy: single `main` branch, direct commits (appropriate for a solo 10-day capstone — documented rationale in today's conversation).
- All code changes tracked in Git, ready to commit and push.

---

## 🐞 Issues Encountered & Resolved

| Issue | Resolution |
|---|---|
| F12 opened Airplane Mode instead of DevTools (laptop hardware shortcut conflict) | Used browser menu (⋮ → More tools → Developer tools) instead |
| Browser DevTools blocked pasting into Console (security feature) | Typed `allow pasting`, and ultimately used a temporary in-page test button instead of the console, avoiding manual retyping errors entirely |
| Manual retyping of JS in console introduced typos (`JSON.Stringify`, missing brackets) | Solved by switching to the in-page button approach — copy-paste into a code editor is far less error-prone than retyping in a console |

None of these were code or architecture problems — all were tooling/workflow friction, now resolved.

---

## 📂 Files Created Today

```
src/main/java/com/vshield/vshield/
├── model/User.java
├── repository/UserRepository.java
├── config/SecurityConfig.java
├── dto/SignupRequest.java
├── dto/LoginRequest.java
└── Controller/AuthController.java
```

Plus modified: `src/main/resources/application.properties`

---

## 🚧 What's Ready to Build Tomorrow (Day 4)

Per the Implementation Blueprint, Day 4 is the **Detection Engine** — the core "brain" of VShield.ai:

- `DetectionService.java` — the 5 rule-based checks (urgency language, suspicious links, sender mismatch, sensitive-info requests, generic tone)
- `AnalysisResult.java` and `CheckResult.java` — result model objects
- `KeywordLists.java` — the keyword/domain data the detection rules run against
- Standalone unit testing of the detection logic — no web server or UI involved yet, per the Blueprint's design (this logic is tested in isolation before being wired to any endpoint on Day 5)

**Nothing from today blocks this work.** The database and authentication foundation are stable and fully independent of the detection engine's logic.

---

## 🎯 Tomorrow's Objective

Build and thoroughly test the rule-based detection engine in isolation — proving it correctly scores and explains at least 8 sample texts (mix of scam and safe) — before any API wiring happens on Day 5. This is intentionally a "pure logic" day: no new UI, no new endpoints, just the core algorithm that makes VShield.ai actually work.
