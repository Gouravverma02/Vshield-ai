# VShield.ai — Testing & Hardening Log

*Day 8 — Testing, Debugging & Production Optimization*

This document records the senior-level QA/security/performance review conducted on Day 8, what was found, what was fixed, and what remains as a known, accepted limitation for this capstone's scope.

---

## 1. Review Methodology

The codebase was reviewed end-to-end as a Senior QA Engineer, Security Reviewer, and Performance Engineer would approach a pre-launch review: reading every controller, checking error paths, testing edge cases manually, and verifying production-safety of configuration defaults.

---

## 2. Issues Found & Fixed

| # | Category | Issue | Fix | Status |
|---|---|---|---|---|
| 1 | Security | No global exception handler — unhandled errors could leak stack traces / internal class names to the client | Added `GlobalExceptionHandler` (`@RestControllerAdvice`) catching validation errors, malformed JSON, 404s, and unexpected exceptions, each returning a clean `{"error": "..."}` message | ✅ Fixed & verified |
| 2 | Security | `server.error.include-stacktrace` defaulted to Spring Boot's dev-friendly setting, exposing full stack traces on errors (confirmed via manual test) | Set `include-stacktrace=never`, `include-message=never`, `include-exception=false` in `application.properties` | ✅ Fixed & verified |
| 3 | Security | H2 database console (`/h2-console`) was reachable from the public internet on the deployed app — a real attack surface | Gated behind a `vshield.h2-console.enabled` property, defaulting to `false`; only enabled locally via `application-local.properties` and an IntelliJ VM option (`-Dspring.profiles.active=local`) | ✅ Fixed & verified locally; disabled by default in production |
| 4 | Security | No protection against brute-force password guessing on login | Added `LoginRateLimiter` — in-memory, 5 failed attempts per email per 15-minute window, returns `429 Too Many Requests` with a clear message | ✅ Fixed & verified (tested with 6 consecutive wrong-password attempts) |
| 5 | Security | Session cookie not marked `Secure` (would allow transmission over plain HTTP in a MITM scenario) | Added `server.servlet.session.cookie.secure`, controlled by `vshield.cookie-secure` property — will be set `true` on Render (HTTPS-only), stays `false` for local HTTP development | ✅ Fixed; production value set at deployment |
| 6 | Security | Clickjacking: frame-options were fully disabled to accommodate the H2 console, even when H2 console wasn't needed | `SecurityConfig` now only disables frame protection when H2 console is explicitly enabled (local dev); production denies framing entirely | ✅ Fixed |
| 7 | UX / Robustness | Analyze textarea had no client-side length limit — user could paste tens of thousands of characters before discovering the 5000-char server limit | Added `maxlength="5000"` to the textarea | ✅ Fixed |
| 8 | Production readiness | Default Spring "Whitelabel Error Page" (unstyled, exposes internal exception class names) shown for any unmatched route | Added custom `error.html` matching brand styling; combined with the `GlobalExceptionHandler`, API-style 404s now return clean JSON instead | ✅ Fixed & verified |
| 9 | Code quality | Duplicate "get user from session" logic repeated in `AnalysisController` and `HistoryController` | Extracted into shared `SessionUserHelper` utility | ✅ Fixed |
| 10 | Performance / log hygiene | `spring.jpa.show-sql=true` printed every SQL statement to logs in all environments, including production | Set to `false` by default; re-enabled only via the local development profile | ✅ Fixed |

---

## 3. Manual Test Checklist — Results

| Test | Expected | Result |
|---|---|---|
| H2 console accessible locally (with `-Dspring.profiles.active=local`) | Loads normally | ✅ Pass |
| 6 consecutive wrong-password login attempts | 6th attempt blocked with rate-limit message | ✅ Pass |
| Visit a nonexistent route (`/doesnotexist`) | Clean JSON 404, no stack trace | ✅ Pass |
| Full signup → analyze → history → logout → login flow | All steps succeed | ✅ Pass (re-verified after all Day 8 changes) |
| Analyze with Safe / Suspicious / Dangerous sample text | Correct verdicts, scores, reasons | ✅ Pass (unchanged from Day 4/5 verification) |
| Duplicate signup with existing email | `409 Conflict`, friendly message | ✅ Pass |
| Login with correct credentials after rate-limit window resets | Succeeds normally | ✅ Pass (rate limiter correctly clears on success) |

---

## 4. Known Limitations (Accepted for This Scope)

These are documented deliberately, not overlooked:

- **In-memory rate limiting** resets if the app restarts or redeploys, and would not work correctly across multiple server instances. Acceptable for a single-instance free-tier deployment; a production system at scale would use Redis or a database-backed counter.
- **Free-tier ephemeral database** (documented since Day 6) — data is lost when Render's free instance spins down and restarts. Not a code defect; a production deployment would use a persistent managed database.
- **No email verification or password reset flow** — explicitly out of scope per the PRD (Section 7, "Explicitly Out of Scope for v1.0").
- **No CAPTCHA or IP-based blocking** on login — the email-based rate limiter provides meaningful protection for this project's scope without adding a paid CAPTCHA service.

---

## 5. Release Readiness Assessment

As of the end of Day 8, VShield.ai has been reviewed for: bugs, edge cases, error handling, form validation, loading/empty states, security (auth, rate limiting, information disclosure, clickjacking, cookie security), and code quality (duplication).

**Verdict: Ready for the intended launch — a public capstone demo on free-tier hosting** — with the known, documented limitations above understood as acceptable trade-offs for this project's scope and hosting tier, not oversights.
