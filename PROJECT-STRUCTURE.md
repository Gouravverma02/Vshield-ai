# VShield.ai — Project Structure

*Day 2 Deliverable — reflects the actual repository as it exists after today's setup, plus where each future day's code will land*

---

## 1. Full Folder Structure (Target State by Day 10)

```
vshield-ai/                                  ← GitHub repository root
│
├── .gitignore                               ← ignores target/, .idea/, *.class, data/ (H2 db files)
├── .gitattributes                           ← Spring-generated, line-ending normalization
├── README.md                                ← project overview + live URL (finalized Day 10)
├── TESTING.md                                ← test log (created Day 8)
├── REFLECTION.md                             ← personal reflection (created Day 10, optional)
│
├── ARCHITECTURE.md                           ← this Day 2 deliverable
├── SCHEMA.md                                 ← this Day 2 deliverable
├── API.md                                    ← this Day 2 deliverable
├── UI-WIREFRAMES.md                          ← this Day 2 deliverable
├── PROJECT-STRUCTURE.md                      ← this Day 2 deliverable (you are here)
│
├── mvnw, mvnw.cmd                            ← Maven wrapper scripts (auto-generated)
├── pom.xml                                   ← Maven project config + dependencies
│
├── .mvn/                                     ← Maven wrapper internals (auto-generated, don't edit)
│
└── src/
    ├── main/
    │   ├── java/
    │   │   └── com/vshield/vshield/
    │   │       │
    │   │       ├── VshieldApplication.java          ← Spring Boot entry point (Day 2 ✅)
    │   │       │
    │   │       ├── controller/                      ← REST API layer — handles HTTP in/out only,
    │   │       │   │                                    no business logic lives here
    │   │       │   ├── HealthController.java         (Day 2 ✅)
    │   │       │   ├── AuthController.java           (Day 3)
    │   │       │   ├── AnalysisController.java       (Day 5)
    │   │       │   └── HistoryController.java        (Day 6)
    │   │       │
    │   │       ├── service/                          ← business logic layer — the "brain" of the app
    │   │       │   ├── DetectionService.java          (Day 4) — the 5 rule-based checks
    │   │       │   ├── UserService.java                (Day 3, if extracted from controller)
    │   │       │   └── HistoryService.java             (Day 6)
    │   │       │
    │   │       ├── repository/                        ← Spring Data JPA interfaces — database access only
    │   │       │   ├── UserRepository.java              (Day 3)
    │   │       │   └── AnalysisRecordRepository.java    (Day 6)
    │   │       │
    │   │       ├── model/                              ← JPA entities — map directly to database tables
    │   │       │   ├── User.java                        (Day 3)
    │   │       │   ├── AnalysisRecord.java               (Day 6)
    │   │       │   ├── AnalysisResult.java                (Day 4) — internal result object,
    │   │       │   │                                          not a DB entity
    │   │       │   └── CheckResult.java                    (Day 4) — helper for individual
    │   │       │                                              rule outcomes
    │   │       │
    │   │       ├── dto/                                 ← request/response shapes exposed via the API —
    │   │       │   │                                        keeps entities from leaking into responses
    │   │       │   ├── SignupRequest.java                 (Day 3)
    │   │       │   ├── LoginRequest.java                   (Day 3)
    │   │       │   └── AnalyzeRequest.java                  (Day 5)
    │   │       │
    │   │       ├── config/                                ← Spring configuration classes
    │   │       │   └── SecurityConfig.java                  (Day 3) — auth rules, password encoder
    │   │       │
    │   │       ├── exception/                              ← centralized error handling
    │   │       │   └── GlobalExceptionHandler.java           (Day 8)
    │   │       │
    │   │       └── util/                                    ← small stateless helpers
    │   │           └── KeywordLists.java                     (Day 4) — hardcoded keyword/domain
    │   │                                                        lists used by DetectionService
    │   │
    │   └── resources/
    │       ├── application.properties         ← server + database config
    │       │                                      (updated Day 3 for file-based H2, per SCHEMA.md §6)
    │       ├── static/                          ← all frontend files — served directly by Spring Boot,
    │       │   │                                    no separate frontend server needed
    │       │   ├── index.html                    (Day 2 ✅)
    │       │   ├── style.css                      (Day 2 ✅ → heavily expanded Day 7)
    │       │   ├── script.js                        (Day 2 ✅, becomes shared/nav helper Day 7)
    │       │   ├── signup.html                       (Day 3)
    │       │   ├── login.html                          (Day 3)
    │       │   ├── auth.js                               (Day 3)
    │       │   ├── dashboard.html                         (Day 3 placeholder → Day 7 polished)
    │       │   ├── analyze.html                             (Day 5)
    │       │   ├── analyze.js                                (Day 5)
    │       │   ├── history.html                                (Day 6)
    │       │   └── history.js                                    (Day 6)
    │       │
    │       └── templates/                        ← auto-generated by Spring Initializr;
    │                                                  unused in v1.0 (we serve static HTML,
    │                                                  not server-rendered templates) — left empty
    │
    └── test/
        └── java/com/vshield/vshield/
            └── DetectionServiceTest.java          ← standalone unit tests for the detection
                                                         engine (Day 4), run independent of the
                                                         web server
```

---

## 2. Why This Structure

**Standard Spring Boot layered architecture** (`controller` → `service` → `repository` → `model`) was chosen over alternatives (e.g., package-by-feature) because:

- It's the structure taught in virtually every Spring Boot tutorial and used in real-world Java teams — directly useful for interviews and future jobs, not just this capstone.
- Each layer has exactly one job, which matters a lot at comfort-level 1/5: when something breaks, you know which folder to look in (HTTP problem → `controller`; logic problem → `service`; data problem → `repository`).
- It matches the Implementation Blueprint's file-by-file plan exactly — every file mentioned in Days 3–8 has an obvious, unambiguous home.

**Frontend lives inside `src/main/resources/static/`** (not a separate `frontend/` folder or project) because:

- Spring Boot serves this folder automatically with zero extra configuration — no CORS setup, no separate dev server, no build step.
- One JAR = one deployable unit, which keeps Day 9 (deployment) dramatically simpler than managing two separate deployments.

**`dto/` is separate from `model/`** because:

- `model/` classes are JPA entities tied directly to database tables (they carry Hibernate annotations, lazy-loading behavior, etc.).
- `dto/` classes are plain objects shaped exactly like what the API should expose — this is what prevents bugs like accidentally returning a user's `passwordHash` in a JSON response, and avoids the `LazyInitializationException` risk called out in the Day 6 blueprint's debugging tips.

**`util/KeywordLists.java` is separate from `service/DetectionService.java`** because:

- Keyword/domain lists will likely be tuned repeatedly during Day 4 and Day 8 testing — keeping them in their own file means editing detection *data* never risks accidentally breaking detection *logic*.

---

## 3. What Should Never Go Where

- **No business logic in `controller/`** — controllers should only: receive the request, call a service method, return the response. If you find yourself writing an `if` statement about scam detection inside a controller, it belongs in `DetectionService` instead.
- **No direct entity returns from any controller** — always map to a DTO first (formalized in ARCHITECTURE.md §6).
- **No raw SQL** — Spring Data JPA repository method names (e.g., `findByUserOrderByCreatedAtDesc`) generate the SQL for us; v1.0 has no query complex enough to need `@Query` or native SQL.
- **No frontend framework folders** (`node_modules/`, `src/components/`, etc.) — per the PRD, frontend is plain HTML/CSS/JS with no build step, so none of this should ever appear in the repository.

---

## 4. Current State vs. Target State

As of the end of Day 2, the repository already matches the target structure through the items marked **(Day 2 ✅)** above: `VshieldApplication.java`, `HealthController.java`, `application.properties`, and the three static frontend files. Every folder referenced by a later day (`service/`, `repository/`, `model/`, `dto/`, `config/`, `exception/`, `util/`) does not exist yet and will be created fresh on the day it's first needed — exactly as laid out in the Implementation Blueprint. This document is what confirms, in advance, that there is one unambiguous right place for every file the rest of the capstone will create.
