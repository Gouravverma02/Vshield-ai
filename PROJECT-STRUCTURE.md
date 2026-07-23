# VShield.ai — Project Structure

*Updated Day 3 — reflects the actual repository state after today's authentication scaffold*

---

## 1. Full Folder Structure (Target State by Day 10)

Status legend: **✅ Built** | ⬜ Planned (future day)

```
vshield-ai/                                  ← GitHub repository root
│
├── .gitignore                               ← ✅ (ignores target/, .idea/, *.class, data/)
├── .gitattributes                           ← ✅ Spring-generated
├── README.md                                ← ✅ (finalized Day 10)
├── TESTING.md                                ⬜ (Day 8)
├── REFLECTION.md                             ⬜ (Day 10, optional)
│
├── ARCHITECTURE.md                           ← ✅ (Day 2)
├── SCHEMA.md                                 ← ✅ (Day 2)
├── API.md                                    ← ✅ (Day 2)
├── UI-WIREFRAMES.md                          ← ✅ (Day 2)
├── PROJECT-STRUCTURE.md                      ← ✅ (Day 2, updated Day 3 — you are here)
├── Blueprint_Addendum_Day2.docx              ← ✅ (Day 2)
├── SETUP.md                                  ← ✅ (Day 3)
├── ENVIRONMENT.md                            ← ✅ (Day 3)
├── DAY3-SUMMARY.md                           ← ✅ (Day 3)
│
├── mvnw, mvnw.cmd                            ← ✅ Maven wrapper scripts
├── pom.xml                                   ← ✅ Maven project config + dependencies
│
├── .mvn/                                     ← ✅ Maven wrapper internals
│
├── data/                                     ← ✅ H2 database file lives here (git-ignored)
│   └── vshielddb.mv.db
│
└── src/
    ├── main/
    │   ├── java/
    │   │   └── com/vshield/vshield/
    │   │       │
    │   │       ├── VshieldApplication.java          ← ✅ (Day 2)
    │   │       │
    │   │       ├── Controller/                       ← REST API layer
    │   │       │   ├── HealthController.java          ← ✅ (Day 2)
    │   │       │   ├── AuthController.java             ← ✅ (Day 3) — signup, login, /me
    │   │       │   ├── AnalysisController.java          ⬜ (Day 5)
    │   │       │   └── HistoryController.java            ⬜ (Day 6)
    │   │       │
    │   │       ├── service/                          ← business logic layer
    │   │       │   ├── DetectionService.java          ⬜ (Day 4) — the 5 rule-based checks
    │   │       │   └── HistoryService.java              ⬜ (Day 6)
    │   │       │
    │   │       ├── repository/                        ← Spring Data JPA interfaces
    │   │       │   ├── UserRepository.java              ← ✅ (Day 3)
    │   │       │   └── AnalysisRecordRepository.java     ⬜ (Day 6)
    │   │       │
    │   │       ├── model/                              ← JPA entities + internal result objects
    │   │       │   ├── User.java                        ← ✅ (Day 3)
    │   │       │   ├── AnalysisRecord.java                ⬜ (Day 6)
    │   │       │   ├── AnalysisResult.java                 ⬜ (Day 4)
    │   │       │   └── CheckResult.java                     ⬜ (Day 4)
    │   │       │
    │   │       ├── dto/                                 ← request/response shapes
    │   │       │   ├── SignupRequest.java                 ← ✅ (Day 3)
    │   │       │   ├── LoginRequest.java                    ← ✅ (Day 3)
    │   │       │   └── AnalyzeRequest.java                    ⬜ (Day 5)
    │   │       │
    │   │       ├── config/                                ← Spring configuration classes
    │   │       │   └── SecurityConfig.java                  ← ✅ (Day 3) — auth rules, password encoder
    │   │       │
    │   │       ├── exception/                              ← centralized error handling
    │   │       │   └── GlobalExceptionHandler.java           ⬜ (Day 8)
    │   │       │
    │   │       └── util/                                    ← small stateless helpers
    │   │           └── KeywordLists.java                     ⬜ (Day 4)
    │   │
    │   └── resources/
    │       ├── application.properties         ← ✅ updated Day 3 (file-based H2, full config)
    │       ├── static/
    │       │   ├── index.html                    ← ✅ (Day 2)
    │       │   ├── style.css                      ← ✅ (Day 2, expanded Day 7)
    │       │   ├── script.js                        ← ✅ (Day 2)
    │       │   ├── signup.html                       ⬜ (Day 3/4 — polished form, not yet built)
    │       │   ├── login.html                          ⬜ (Day 3/4)
    │       │   ├── auth.js                               ⬜ (Day 3/4)
    │       │   ├── dashboard.html                         ⬜ (Day 3 placeholder → Day 7 polished)
    │       │   ├── analyze.html                             ⬜ (Day 5)
    │       │   ├── analyze.js                                ⬜ (Day 5)
    │       │   ├── history.html                                ⬜ (Day 6)
    │       │   └── history.js                                    ⬜ (Day 6)
    │       │
    │       └── templates/                        ← unused in v1.0 (static HTML, not server-rendered)
    │
    └── test/
        └── java/com/vshield/vshield/
            └── DetectionServiceTest.java          ⬜ (Day 4)
```

---

## 2. What Changed Since Day 2

- **`Controller/AuthController.java`, `repository/UserRepository.java`, `model/User.java`, `config/SecurityConfig.java`, `dto/SignupRequest.java`, `dto/LoginRequest.java`** — all newly created today, exactly matching the folders anticipated in Day 2's design.
- **`data/` folder** — new; holds the H2 database file. Not part of the original Day 2 structure diagram since it didn't exist until file-based mode was configured today. Now documented and confirmed git-ignored.
- **`application.properties`** — went from a single auto-generated line to a fully documented configuration file (see ENVIRONMENT.md for the complete reference).

**One naming note carried forward:** the controller package is capitalized `Controller/` (not `controller/`) because that's how IntelliJ auto-created it back on Day 2 when the `HealthController` was first built, and today's `AuthController` was added to the same existing package for consistency. This is purely cosmetic (Java doesn't enforce package naming case, only convention) — flagged here for awareness, not treated as a bug requiring a disruptive rename mid-build.

---

## 3. Validation Against SCHEMA.md and API.md

| Design Document | What It Specified | What Was Actually Built | Match? |
|---|---|---|---|
| SCHEMA.md §2 | `USERS` table: id, email (unique), password_hash, created_at | `User.java` entity — identical fields and constraints | ✅ |
| API.md §2 | `POST /api/auth/signup` — validation, 201/400/409 responses | `AuthController.signup()` — matches exactly | ✅ |
| API.md §3 | `POST /api/auth/login` — 200/400/401, generic error message | `AuthController.login()` — matches exactly | ✅ |
| API.md §5 | `GET /api/auth/me` — 200/401 | `AuthController.me()` — matches exactly | ✅ |
| ARCHITECTURE.md §6 | DTOs at the boundary, never expose entities directly | Confirmed — `AuthController` returns `Map<String,Object>` responses, never a raw `User` object (so `passwordHash` can never leak) | ✅ |

No deviations found — today's implementation is a direct, verified match to Day 2's design.

---

## 4. What Remains Unambiguous for Day 4 Onward

Every folder needed for the rest of the build (`service/`, remaining `model/` classes, remaining `dto/` classes, `exception/`, `util/`) is pre-planned and has zero ambiguity about where new files belong — Day 4 can begin writing `DetectionService.java` immediately without any structural decisions left to make.
