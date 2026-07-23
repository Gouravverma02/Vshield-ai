# VShield.ai — Setup Guide

*Day 3 Deliverable — how to get this project running from a fresh machine*

This guide lets anyone (including future-you, on a different computer) get VShield.ai running locally from scratch.

---

## 1. Prerequisites

| Tool | Minimum Version | Verified Working Version | Check with |
|---|---|---|---|
| Java (JDK) | 17+ | 22.0.1 | `java -version` |
| Git | any recent | 2.51.0 | `git --version` |
| IntelliJ IDEA (Community or Ultimate) | any recent | 2025.2.1 | Search "IntelliJ" in Start menu |
| Maven | bundled via wrapper — no separate install needed | — | N/A (uses `mvnw`/`mvnw.cmd` in the repo) |

No Node.js, no npm, no Python — this is a pure Java + static HTML/CSS/JS project, kept intentionally simple per the PRD.

---

## 2. Clone the Repository

```bash
git clone https://github.com/Gouravverma02/vshield-ai.git
cd vshield-ai
```

## 3. Open in IntelliJ IDEA

1. Launch IntelliJ IDEA.
2. **File → Open** (or click **Open** on the welcome screen).
3. Select the `vshield-ai` folder itself (the one containing `pom.xml`).
4. Click **Trust Project** if prompted.
5. Wait for Maven to finish importing dependencies (progress bar at the bottom of the IDE).

## 4. Run the Application

1. Open `src/main/java/com/vshield/vshield/VshieldApplication.java`.
2. Click the green **▶** icon next to `public class VshieldApplication`.
3. Wait for the console to show:
   ```
   Started VshieldApplication in X.XXX seconds
   ```

The app is now running at **http://localhost:8080**.

## 5. Verify It's Working

| Check | URL | Expected Result |
|---|---|---|
| Backend alive | `http://localhost:8080/api/health` | `{"status":"UP","service":"VShield.ai Backend"}` |
| Frontend loads | `http://localhost:8080/index.html` | Navy page with "VShield.ai" heading |
| Database console | `http://localhost:8080/h2-console` | H2 login screen (see connection details below) |

### H2 Console Connection Details
- **Driver Class:** `org.h2.Driver`
- **JDBC URL:** `jdbc:h2:file:./data/vshielddb`
- **User Name:** `sa`
- **Password:** *(leave blank)*

---

## 6. Testing Authentication Endpoints (Manual, No Extra Tools)

Since Postman/curl aren't required for this project, endpoints can be tested directly from the browser once logged into a page served by the app (any `static/*.html` page), using the browser's Developer Tools Console, or via a temporary test button in HTML (as used during Day 3 development — see DAY3-SUMMARY.md).

Example signup request:
```javascript
fetch('/api/auth/signup', {
  method: 'POST',
  headers: { 'Content-Type': 'application/json' },
  body: JSON.stringify({ email: 'you@example.com', password: 'YourPass123' })
})
  .then(res => res.json())
  .then(data => console.log(data));
```

Example login request:
```javascript
fetch('/api/auth/login', {
  method: 'POST',
  headers: { 'Content-Type': 'application/json' },
  body: JSON.stringify({ email: 'you@example.com', password: 'YourPass123' })
})
  .then(res => res.json())
  .then(data => console.log(data));
```

**Note:** Browser DevTools blocks pasting into the Console by default as a security measure. Type `allow pasting` and press Enter first if you want to paste code directly there — or simply add a temporary `<button onclick="...">` to a static HTML page, as we did during Day 3 development, which avoids the console entirely.

---

## 7. Common Setup Issues

| Problem | Cause | Fix |
|---|---|---|
| Port 8080 already in use | Another instance already running | Stop the other instance, or change `server.port` in `application.properties` |
| `/index.html` shows a 404 error | App wasn't restarted after adding static files | Static files are only scanned at startup — stop and restart the app |
| H2 console login fails | Wrong JDBC URL | Must exactly match `spring.datasource.url` in `application.properties`: `jdbc:h2:file:./data/vshielddb` |
| Database resets unexpectedly | Using in-memory mode by mistake | Confirm `application.properties` uses `jdbc:h2:file:...` not `jdbc:h2:mem:...` |
| Signup returns "email already exists" unexpectedly | Testing with the same email repeatedly | Use a new email, or check the `USERS` table via H2 console and delete the test row |

---

## 8. Project Documentation Index

| Document | Purpose |
|---|---|
| `README.md` | Project overview |
| `VShield_PRD.docx` | What we're building and why (Day 1) |
| `VShield_Blueprint.docx` + `Blueprint_Addendum_Day2.docx` | Day-by-day build plan (Days 1–2) |
| `ARCHITECTURE.md` | System design, diagrams, data flow (Day 2) |
| `SCHEMA.md` | Database table design (Day 2) |
| `API.md` | Every endpoint's contract (Day 2) |
| `UI-WIREFRAMES.md` | Screen designs and user flow (Day 2) |
| `PROJECT-STRUCTURE.md` | Folder structure and rationale (Day 2, updated Day 3) |
| `SETUP.md` | This document (Day 3) |
| `ENVIRONMENT.md` | All config values and environment details (Day 3) |
| `DAY3-SUMMARY.md` | What was built today (Day 3) |
