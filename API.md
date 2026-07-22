# VShield.ai — API Design

*Day 2 Deliverable — no implementation yet, specification only*

Base URL (local dev): `http://localhost:8080`
All request/response bodies are JSON unless noted. Authentication uses a server-side session (`HttpSession`) established at login; the browser's `fetch()` calls automatically include the session cookie, so no manual token handling is needed in the frontend.

---

## Endpoint Summary

| Method | Path | Purpose | Auth Required |
|---|---|---|---|
| GET | `/api/health` | Confirm the backend is alive | No |
| POST | `/api/auth/signup` | Create a new account | No |
| POST | `/api/auth/login` | Log in, start a session | No |
| POST | `/api/auth/logout` | End the session | Yes |
| GET | `/api/auth/me` | Check current login state | Yes |
| POST | `/api/analyze` | Analyze pasted text, get verdict | Yes |
| GET | `/api/history` | List the user's past analyses | Yes |
| GET | `/api/history/{id}` | Get full detail of one past analysis | Yes |

---

## 1. `GET /api/health`

**Purpose:** Confirm the server is running (already implemented Day 2).

- **Request:** none
- **Response `200 OK`:**
```json
{ "status": "UP", "service": "VShield.ai Backend" }
```
- **Validation:** none
- **Authentication:** none (public)
- **Error cases:** none expected; if the server is down, the request simply fails to connect (handled by the browser, not the API).

---

## 2. `POST /api/auth/signup`

**Purpose:** Create a new user account.

- **Request body:**
```json
{ "email": "manager@agency.com", "password": "MySecurePass123" }
```
- **Response `201 Created`:**
```json
{ "id": 1, "email": "manager@agency.com", "message": "Account created successfully" }
```
  *(Never returns `passwordHash`.)*
- **Validation (server-side, `@Valid` on DTO):**
  - `email`: required, must be a valid email format (`@Email`)
  - `password`: required, minimum 8 characters (`@Size(min = 8)`)
- **Authentication:** none required (this is how a session begins)
- **Error cases:**
  | Status | Condition | Body |
  |---|---|---|
  | `400 Bad Request` | Missing/invalid email or password too short | `{ "error": "Validation failed", "details": ["email must be a valid email address"] }` |
  | `409 Conflict` | Email already registered | `{ "error": "An account with this email already exists" }` |

---

## 3. `POST /api/auth/login`

**Purpose:** Authenticate an existing user and start a session.

- **Request body:**
```json
{ "email": "manager@agency.com", "password": "MySecurePass123" }
```
- **Response `200 OK`:**
```json
{ "id": 1, "email": "manager@agency.com", "message": "Login successful" }
```
  *(Session cookie `JSESSIONID` is set automatically by Spring Security in the response headers — no token handling needed in JS.)*
- **Validation:** `email` and `password` both required (non-blank)
- **Authentication:** none required to call this endpoint (it's how you get one)
- **Error cases:**
  | Status | Condition | Body |
  |---|---|---|
  | `400 Bad Request` | Missing fields | `{ "error": "Email and password are required" }` |
  | `401 Unauthorized` | Email not found, or password incorrect | `{ "error": "Invalid email or password" }` |

  *Note: we deliberately return the same generic message for "email not found" and "wrong password" — never reveal which one failed, a standard security practice to avoid confirming which emails have accounts.*

---

## 4. `POST /api/auth/logout`

**Purpose:** End the current session (Day 7 feature).

- **Request:** none (session cookie identifies the user)
- **Response `200 OK`:**
```json
{ "message": "Logged out successfully" }
```
- **Validation:** none
- **Authentication:** Yes — must have an active session
- **Error cases:**
  | Status | Condition | Body |
  |---|---|---|
  | `401 Unauthorized` | No active session | `{ "error": "Not logged in" }` |

---

## 5. `GET /api/auth/me`

**Purpose:** Let the frontend check "am I logged in, and as whom?" — used on page load for `dashboard.html`, `analyze.html`, and `history.html` to redirect to login if the session has expired.

- **Request:** none
- **Response `200 OK`:**
```json
{ "id": 1, "email": "manager@agency.com" }
```
- **Validation:** none
- **Authentication:** Yes
- **Error cases:**
  | Status | Condition | Body |
  |---|---|---|
  | `401 Unauthorized` | No active/valid session | `{ "error": "Not authenticated" }` |

---

## 6. `POST /api/analyze`

**Purpose:** The core feature — run pasted text through the 5-rule detection engine and return a verdict. Automatically saves the result to history.

- **Request body:**
```json
{ "text": "Hi! We are Nike and want to offer you a deal. Act now, verify your account immediately at nike-brand-deals.bit.ly and send your login password to confirm." }
```
- **Response `200 OK`:**
```json
{
  "id": 42,
  "riskScore": 85,
  "verdict": "DANGEROUS",
  "reasons": [
    "Urgent/pressure language detected: 'act now', 'verify immediately'",
    "Suspicious shortened link detected (bit.ly)",
    "Sender claims to be 'Nike' but provides no matching official domain",
    "Message requests sensitive information: password"
  ],
  "nextSteps": [
    "Do not click the link provided",
    "Never share your password with anyone via message",
    "Verify this offer directly through Nike's official/verified channels",
    "Consider blocking and reporting this sender"
  ],
  "createdAt": "2026-07-23T10:15:00"
}
```
- **Validation:**
  - `text`: required, non-blank (`@NotBlank`)
  - `text`: maximum length enforced (e.g., 5,000 characters) — see Day 8 hardening; returns `400` if exceeded
- **Authentication:** Yes
- **Error cases:**
  | Status | Condition | Body |
  |---|---|---|
  | `400 Bad Request` | Empty text, or text exceeds max length | `{ "error": "Text is required and must be under 5000 characters" }` |
  | `401 Unauthorized` | Not logged in / session expired | `{ "error": "Not authenticated" }` |

---

## 7. `GET /api/history`

**Purpose:** List the logged-in user's past analyses, newest first, for `history.html`.

- **Request:** none (session identifies the user; no pagination in v1.0 — acceptable given expected low volume for a capstone demo)
- **Response `200 OK`:**
```json
[
  {
    "id": 42,
    "verdict": "DANGEROUS",
    "riskScore": 85,
    "textPreview": "Hi! We are Nike and want to offer you a deal. Act now...",
    "createdAt": "2026-07-23T10:15:00"
  },
  {
    "id": 41,
    "verdict": "SAFE",
    "riskScore": 5,
    "textPreview": "Hi, this is Sarah from Acme Co. reaching out about...",
    "createdAt": "2026-07-22T16:40:00"
  }
]
```
  *(`textPreview` is the original text truncated to ~80 characters server-side, so the list stays lightweight.)*
- **Validation:** none
- **Authentication:** Yes
- **Error cases:**
  | Status | Condition | Body |
  |---|---|---|
  | `401 Unauthorized` | Not logged in | `{ "error": "Not authenticated" }` |
  | `200 OK` (not an error) | User has no history yet | `[]` (empty array — frontend shows the "no checks yet" empty state) |

---

## 8. `GET /api/history/{id}`

**Purpose:** Retrieve the full detail of one past analysis (for the "click to expand" view in `history.html`).

- **Request:** path parameter `id` (e.g., `/api/history/42`)
- **Response `200 OK`:** same shape as the `/api/analyze` response (full `reasons`, `nextSteps`, `originalText`, etc.)
- **Validation:** `id` must be a valid number (handled by Spring's path variable binding, returns `400` automatically on type mismatch)
- **Authentication:** Yes — **and** the returned record's `user_id` must match the logged-in user's id (critical security check per SCHEMA.md Section 4)
- **Error cases:**
  | Status | Condition | Body |
  |---|---|---|
  | `401 Unauthorized` | Not logged in | `{ "error": "Not authenticated" }` |
  | `404 Not Found` | Record doesn't exist, **or** belongs to a different user | `{ "error": "Analysis record not found" }` |

  *Note: we intentionally return `404` (not `403`) when a record belongs to someone else — this avoids confirming to an attacker that a given ID exists at all, a small but meaningful security detail.*

---

## Global Error Response Format

All error responses (implemented via `GlobalExceptionHandler`, Day 8) follow one consistent shape, so the frontend can handle errors generically:

```json
{ "error": "Human-readable message here" }
```

No raw Java stack traces, no internal class names, are ever returned to the client — matching PRD Non-Functional Requirement: Security.
