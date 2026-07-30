# VShield.ai — 30-Day Growth Plan

*A realistic day-by-day roadmap from v1.0.0 MVP to a significantly more complete product.*

Each day assumes roughly 1-3 hours of focused work, building directly on the previous day. Skip a day and resume where you left off — nothing here is time-sensitive.

## Week 1: Persistent Data & Reliability (Days 1-7)

1. **Day 1:** Create a free Render PostgreSQL database (or Supabase). Document connection details in `ENVIRONMENT.md`.
2. **Day 2:** Update `application.properties` and `pom.xml` to support PostgreSQL alongside H2 (Spring profiles: `local` uses H2, `production` uses Postgres).
3. **Day 3:** Migrate `User` and `AnalysisRecord` entities — test locally against a local Postgres instance (Docker) before touching production.
4. **Day 4:** Deploy the Postgres-backed version to Render. Verify signup/login/analyze/history all persist across a manual restart.
5. **Day 5:** Write a data migration/backup script (simple `pg_dump` cron reminder, even manual for now) — protect against future data loss.
6. **Day 6:** Add integration tests for `AuthController` and `AnalysisController` using Spring Boot Test + an in-memory test database.
7. **Day 7:** Review week 1: confirm zero data loss across 3 consecutive days of testing. Update `TESTING.md`.

## Week 2: Distributed-Safe Security & Email (Days 8-14)

8. **Day 8:** Replace in-memory `LoginRateLimiter` with a database-backed table (`login_attempts`) — survives restarts now.
9. **Day 9:** Sign up for a free Resend (or similar) account. Add email-sending capability to the backend.
10. **Day 10:** Implement email verification on signup — unverified accounts can log in but see a "verify your email" banner.
11. **Day 11:** Implement password reset flow: request-reset endpoint, emailed token, reset-password page.
12. **Day 12:** Add a `PasswordResetToken` entity with expiration; write tests for expired/reused tokens.
13. **Day 13:** UI polish for the new signup/login flows (verification banner, reset password pages) matching existing design system.
14. **Day 14:** Full regression test of the entire auth flow, including new email features. Update `API.md`.

## Week 3: Account Risk Guard Module (Days 15-21)

15. **Day 15:** Design `AccountRiskCheck` schema — a simple manual checklist model (2FA enabled?, recent password change?, unfamiliar login locations?) rather than OAuth integration.
16. **Day 16:** Build `AccountRiskChecklistController` — `POST /api/risk-check` accepting a simple form of yes/no answers.
17. **Day 17:** Build the scoring logic — similar weighted-rule pattern to `DetectionService`, reused conceptually.
18. **Day 18:** Build `risk-check.html` frontend page, matching existing design system.
19. **Day 19:** Add risk-check history alongside message-analysis history — extend `history.html` to show both types.
20. **Day 20:** Write documentation: update `SCHEMA.md`, `API.md`, `ARCHITECTURE.md` for the new module.
21. **Day 21:** Full test pass on the new module; deploy; verify live.

## Week 4: Team Accounts & Client Tagging (Days 22-28)

22. **Day 22:** Design `Agency` and `AgencyMember` entities — one agency has many users, one role field (admin/analyst).
23. **Day 23:** Build agency signup flow — first user creates the agency, subsequent users join via invite code.
24. **Day 24:** Add `client` field (free text or tag) to `AnalysisRecord` — let users label which client an analysis was for.
25. **Day 25:** Build a simple per-client history filter on `history.html`.
26. **Day 26:** Add role-based access: analysts can create/view their own checks; admins can view all agency checks.
27. **Day 27:** UI updates: agency switcher in the navbar, member management page.
28. **Day 28:** Full regression test across single-user and agency-mode flows.

## Final Stretch (Days 29-30)

29. **Day 29:** Full security review of the new agency/role features specifically — verify an analyst truly cannot see another agency's data, the same rigor applied to `AnalysisRecordRepository` on Day 6.
30. **Day 30:** Update README, take new screenshots, write a "30 days later" retrospective, and tag a new release: `v1.1.0`.

---

**Guiding principle throughout:** every new feature should be built, tested, and deployed with the same discipline as the original 10-day sprint — small verified steps, not big untested leaps.
