# VShield.ai — System Architecture

*Day 2 Deliverable — AB Talks 60-Day Claude AI Challenge Capstone*

This document is the single source of truth for how VShield.ai's components fit together. It complements the PRD (what we're building) and the Implementation Blueprint (how we build it day by day).

---

## 1. Component Diagram

VShield.ai is a monolithic Spring Boot application: one deployable JAR serves both the REST API and the static frontend. There is no separate frontend server, no microservices, and no external AI/ML service — this keeps deployment (Day 9) simple and matches the PRD's rule-based, dependency-light design.

```mermaid
graph TB
    subgraph Browser["🌐 Browser (Client)"]
        UI[HTML / CSS / JavaScript<br/>index.html, signup.html, login.html,<br/>dashboard.html, analyze.html, history.html]
    end

    subgraph SpringBoot["☕ Spring Boot Application (Single JAR)"]
        direction TB
        SEC[Spring Security Filter Chain<br/>Session-based Auth]
        subgraph Controllers["Controller Layer"]
            AuthC[AuthController<br/>/api/auth/*]
            AnalyzeC[AnalysisController<br/>/api/analyze]
            HistC[HistoryController<br/>/api/history/*]
            HealthC[HealthController<br/>/api/health]
        end
        subgraph Services["Service Layer"]
            DetectS[DetectionService<br/>5 Rule-Based Checks]
            UserS[UserService]
            HistS[HistoryService]
        end
        subgraph Repos["Repository Layer (Spring Data JPA)"]
            UserR[UserRepository]
            AnalysisR[AnalysisRecordRepository]
        end
    end

    subgraph DB["🗄 H2 Database (File-Based)"]
        UsersTable[(USERS)]
        RecordsTable[(ANALYSIS_RECORDS)]
    end

    UI -->|"HTTPS/HTTP fetch() calls<br/>JSON request/response"| SEC
    SEC --> Controllers
    AuthC --> UserS
    AnalyzeC --> DetectS
    AnalyzeC --> HistS
    HistC --> HistS
    UserS --> UserR
    HistS --> AnalysisR
    UserR --> UsersTable
    AnalysisR --> RecordsTable

    style Browser fill:#CADCFC,stroke:#1E2761,color:#1B1B2F
    style SpringBoot fill:#1E2761,stroke:#1E2761,color:#FFFFFF
    style DB fill:#F4F6FB,stroke:#1E2761,color:#1B1B2F
```

**Key design decision:** `DetectionService` is a pure, standalone Java service with no dependency on Spring Web or the database (per Day 4 of the blueprint). It's called by `AnalysisController` but could be unit-tested entirely on its own. This keeps the detection logic explainable, portable, and easy to debug.

---

## 2. Data Flow — "Paste and Analyze" (Core Product Loop)

This is the primary flow the entire product is built around.

```mermaid
sequenceDiagram
    actor User
    participant Browser as Browser (analyze.html)
    participant Sec as Spring Security
    participant AC as AnalysisController
    participant DS as DetectionService
    participant HS as HistoryService
    participant DB as H2 Database

    User->>Browser: Paste suspicious text, click "Analyze"
    Browser->>Sec: POST /api/analyze { text: "..." } (with session cookie)
    Sec->>Sec: Verify active session (logged in?)
    alt Not authenticated
        Sec-->>Browser: 401 Unauthorized
        Browser-->>User: Redirect to login.html
    else Authenticated
        Sec->>AC: Forward request
        AC->>DS: analyze(text)
        DS->>DS: Run 5 rule checks:<br/>urgency, links, sender mismatch,<br/>sensitive info, generic tone
        DS->>DS: Compute risk score (0-100)
        DS->>DS: Map score to verdict<br/>(Safe/Suspicious/Dangerous)
        DS-->>AC: AnalysisResult { score, verdict, reasons, nextSteps }
        AC->>HS: save(user, text, result)
        HS->>DB: INSERT INTO ANALYSIS_RECORDS
        DB-->>HS: Saved record
        AC-->>Browser: 200 OK + JSON result
        Browser-->>User: Render verdict badge,<br/>reasons, next steps
    end
```

---

## 3. Request Lifecycle (Every API Call)

All `/api/**` requests (except `/api/auth/signup`, `/api/auth/login`, and `/api/health`) pass through this lifecycle:

```mermaid
flowchart LR
    A[Browser sends HTTP request] --> B{Spring Security<br/>Filter Chain}
    B -->|Public route| D[Controller]
    B -->|Protected route,<br/>no valid session| C[401 Unauthorized<br/>JSON error response]
    B -->|Protected route,<br/>valid session| D[Controller]
    D --> E[DTO Validation<br/>@Valid annotations]
    E -->|Invalid| F[400 Bad Request<br/>Validation error JSON]
    E -->|Valid| G[Service Layer<br/>Business Logic]
    G --> H[Repository Layer<br/>Spring Data JPA]
    H --> I[(H2 Database)]
    I --> H
    H --> G
    G --> J[Controller builds response]
    J --> K[200 OK + JSON]
    G -->|Unexpected error| L[GlobalExceptionHandler<br/>500 with clean JSON,<br/>no stack trace exposed]
```

---

## 4. AI Interaction

**None in v1.0.** Per PRD Section 6 and Section 10, VShield.ai's detection engine is intentionally **rule-based only** — no calls to Claude, OpenAI, or any external AI/ML API. This is a deliberate architectural choice:

- Every verdict is fully explainable (a manager can see exactly which of the 5 rules triggered).
- No API costs, no rate limits, no network dependency for the core feature.
- Faster and more predictable for a live demo.

This is documented here so no future day accidentally introduces an AI dependency without an explicit scope-change discussion. AI-assisted detection is captured in PRD Section 12 (Future Scope, v2.0+) only.

---

## 5. External Services

| Service | Purpose | When Used |
|---|---|---|
| **GitHub** | Source control, backup, deployment trigger | Every day, and specifically Day 9 (deploy) |
| **Free-tier hosting platform** (Render/Railway — finalized Day 9) | Runs the live public JAR | Day 9 onward |
| **Spring Initializr** (start.spring.io) | One-time project scaffolding | Day 2 only (already used) |

No payment gateways, no email services (no email verification per PRD), no third-party analytics, and no CDN dependencies beyond an optional Google Font link (Day 7 polish). This keeps the external surface area minimal — fewer things that can break during the demo.

---

## 6. Architectural Principles Guiding Every Day Going Forward

1. **Single deployable unit.** Frontend and backend ship together in one JAR — no CORS complexity, no separate hosting for frontend/backend.
2. **Explainability over cleverness.** `DetectionService` must always be able to say *why* it flagged something — this is a product requirement, not just an implementation detail.
3. **Session-based auth, not JWT.** Simpler to reason about at comfort-level 1/5, sufficient for a single-server v1.0 with no mobile app.
4. **DTOs at the boundary.** Controllers never return JPA entities directly (avoids lazy-loading errors and accidentally leaking internal fields) — this is formalized here and will be implemented starting Day 3.
