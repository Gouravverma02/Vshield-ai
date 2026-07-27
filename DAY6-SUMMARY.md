# VShield.ai — Day 6 Summary

*Complete the MVP & Deliver a Working Demo*

---

## 🎯 Objective
Finish the last core feature (History Log), add the required footer, and deploy a working live demo — completing the full VShield.ai MVP.

---

## ✅ Completed Today

### Backend — History Log
| File | Purpose |
|---|---|
| `AnalysisRecord.java` (model) | Entity storing every analysis: verdict, score, original text, reasons, next steps, timestamp |
| `AnalysisRecordRepository.java` | `findByUserOrderByCreatedAtDesc`, `findByIdAndUser` (security-scoped queries) |
| `AnalysisController.java` (modified) | Now saves every analysis to the database via `AnalysisRecordRepository` before returning the result |
| `HistoryController.java` | `GET /api/history` (list) and `GET /api/history/{id}` (detail) — both scoped strictly to the logged-in user |

### Frontend — History Screen
| File | Purpose |
|---|---|
| `history.html` | Lists past checks with color-coded verdict badges, newest first; click-to-expand detail popup |
| `history.js` | Fetches and renders the list and detail view; reuses the analyze screen's verdict-card visual language for consistency |
| `dashboard.html`, `analyze.html` (modified) | Added "History" link to shared navbar |

### Footer (Required Deliverable)
Added `"Built with Claude as part of the AB Talks 60-Day Claude AI Challenge."` to every page (`index.html`, `signup.html`, `login.html`, `dashboard.html`, `analyze.html`, `history.html`), styled as a proper bottom bar — confirmed visible on the deployed live version, not just locally.

### Deployment
- **Platform:** Render (free tier, $0/month, no credit card required)
- **Live URL:** https://vshield-ai-furt.onrender.com
- Added a `Dockerfile` (multi-stage Maven build → lightweight JRE runtime) since Render's Java support is Docker-based
- Updated `application.properties` to read `server.port` from Render's `PORT` environment variable, falling back to 8080 locally

---

## 🐞 Real Bugs Found & Fixed Today

### 1. Static file case-sensitivity (`Style.css` vs `style.css`)
**Symptom:** Live site loaded completely unstyled (default browser fonts, no colors), while working perfectly on localhost.
**Cause:** Windows filesystems are case-insensitive (`Style.css` and `style.css` are treated as the same file), but Render's Linux containers are case-sensitive — the HTML's lowercase `href="style.css"` couldn't find a file actually named `Style.css`.
**Fix:** Renamed the file to lowercase `style.css` and verified every HTML file's `<link>` tag matches exactly.
**Lesson:** Always use lowercase, consistent filenames for anything served over the web — this class of bug is invisible during local Windows development and only appears after deployment.

### 2. Missing `/api/auth/logout` endpoint
**Symptom:** After logging out and attempting to log back in with the same, correct credentials, login would fail.
**Cause:** `auth.js`'s `logout()` function called `POST /api/auth/logout`, but this endpoint was never implemented in `AuthController` — the request silently 404'd, meaning the server-side session was **never actually invalidated**, leaving stale session state behind.
**Fix:** Implemented `AuthController.logout()`, which calls `session.invalidate()` and returns a proper confirmation response.
**Additional hardening:** Normalized email input (`.trim().toLowerCase()`) on both signup and login, preventing a related class of "can't log in" bugs caused by inconsistent letter-casing between signup and login attempts.

### 3. Duplicate stylesheet `<link>` tags
Found during code review (IntelliJ's Problems panel) in `signup.html` — a leftover from manual mid-session edits rather than full-file replacement. Not functionally breaking, but cleaned up for code quality. Fixed alongside the footer-positioning corrections on `signup.html` and `login.html`.

### 4. Session cookie security hardening
Added to `application.properties`, ahead of Day 8's dedicated hardening pass, since it was low-effort and directly relevant to a cybersecurity-focused product:
```properties
server.servlet.session.cookie.http-only=true
server.servlet.session.cookie.same-site=lax
server.servlet.session.timeout=30m
```
- `HttpOnly` — session cookie is invisible to JavaScript, blocking a common XSS-based session-theft vector
- `SameSite=Lax` — mitigates CSRF-style cross-site request abuse
- 30-minute timeout — limits the window an idle/stolen session could be misused

---

## ⚠️ Known Limitation (Documented, Not Hidden)

**Render's free tier uses ephemeral disk storage.** When the app spins down after ~15 minutes of inactivity (a free-tier behavior) and later wakes back up, the H2 database file is recreated empty — meaning all signed-up users and analysis history from a prior session are lost.

This is a standard trade-off of free hosting, not a bug in VShield.ai's code, and was anticipated back in Day 2's `Blueprint_Addendum_Day2.docx` and `ENVIRONMENT.md`. For a capstone demo, this is acceptable; a production deployment would use a persistent managed database (e.g., Render's free PostgreSQL tier) instead of file-based H2. This is captured as a clear "what to improve" item rather than something to gloss over.

---

## Verified End-to-End (Local + Live)

| Test | Result |
|---|---|
| Full signup → dashboard → analyze → history flow | ✅ Working locally and live |
| Safe / Suspicious / Dangerous verdict rendering | ✅ Correct colors, scores, reasons on both analyze and history detail views |
| History list shows newest-first, click-to-expand works | ✅ |
| Logout → re-login with same credentials | ✅ Fixed and verified working |
| Footer visible on all 6 pages, live and local | ✅ |
| Live deployment reachable at public URL | ✅ https://vshield-ai-furt.onrender.com |

---

## Ready for Tomorrow (Day 7)

Per the Blueprint, Day 7 is **UI/UX Polish & Full Frontend Consistency**. Groundwork already in good shape:
- Shared navbar pattern already consistent across dashboard/analyze/history
- Verdict-card styling already shared between analyze and history detail views
- Remaining polish: full consistency pass across all 6 pages, review copy/wording, responsive check at narrower widths, and address the duplicate-CSS bloat noted in `style.css` (harmless but worth tidying for code quality)
