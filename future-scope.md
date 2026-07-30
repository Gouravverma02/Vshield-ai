# VShield.ai — Future Scope

*How this specific project could evolve over the next 3, 6, and 12 months.*

---

## 3 Months: Solidify the Core, Remove Free-Tier Friction

**Goal: turn the MVP into a tool people would actually keep using week over week.**

- **Persistent database.** Migrate from file-based H2 to Render's free PostgreSQL tier (or Supabase free tier) — eliminates the ephemeral-storage data-reset limitation documented since Day 6. This is the single highest-leverage change: it's the difference between "demo" and "tool."
- **Account Risk Guard (v2.0 module, already scoped in the PRD).** A second analysis mode: instead of vetting an inbound message, audit the *user's own* connected account (starting with a manual checklist — 2FA enabled?, recent login locations, connected third-party apps — before any OAuth integration).
- **Distributed rate limiting.** Replace the in-memory `LoginRateLimiter` with a Redis-backed or database-backed counter, so it survives restarts and scales past a single instance.
- **Email verification** (explicitly deferred from v1.0 per the PRD) — a free transactional email tier (e.g., Resend's free tier, 100 emails/day) would close this gap without cost.
- **Basic analytics dashboard** for a user's own history: verdict distribution over time, most common flagged reasons — turns raw history into insight.

## 6 Months: From Personal Tool to Team Tool

**Goal: match the PRD's actual target user — agencies, not just individuals.**

- **Team/agency accounts.** Multiple staff under one agency account, shared client history, role-based permissions (admin vs. analyst) — the PRD's primary persona was always "social media manager at an agency," and v1.0 only served the individual use case.
- **Client-tagging on analyses.** Let a manager tag which client an analysis was for, enabling per-client reporting — directly serves the "managing multiple clients" pain point from the original interview.
- **Browser extension (Chrome/Edge).** One-click "Check with VShield" from Gmail or DMs, calling the existing `/api/analyze` endpoint — the API is already built; this is a new client, not new backend work.
- **Expanded detection rules**, informed by real usage data collected over the prior 3 months: new scam patterns, additional known-brand list expansion, sender-reputation heuristics.
- **AI-assisted secondary pass (optional, not default).** For messages the rule engine scores as borderline (31-50), optionally offer an AI-powered second opinion using a free-tier LLM API — kept opt-in and clearly labeled, preserving the core promise of explainable, rule-based-by-default detection.

## 12 Months: A Real Product

**Goal: something that could plausibly acquire real users outside a portfolio context.**

- **Public launch with a real domain**, marketing site distinct from the app itself, and a genuine free tier + optional paid tier (agencies with 10+ seats, advanced analytics, priority support).
- **Integrations**: Slack/Discord bot for teams that triage inbound deals in a shared channel; Zapier/Make integration so a flagged "Dangerous" result can trigger an automated Slack alert.
- **Scam pattern database, shared anonymously across users** (opt-in) — when enough users flag a similar pattern (e.g., the same scam domain appearing across multiple accounts), the system could surface "X other users have flagged this same sender" — a network-effect feature that rule-based-only detection can't achieve alone, but doesn't require abandoning the explainability principle.
- **Mobile app** (React Native or similar) — same API, new client, addressing that managers often triage DMs on their phone, not a laptop.
- **Formal security audit / bug bounty**, appropriate once real user data is at stake at meaningful scale — a natural maturity step once the product has genuine users beyond a portfolio piece.

---

Each phase builds directly on architecture decisions already made in v1.0 — the layered backend, the DTO boundary, the explainable rule engine, and the REST API are all designed to extend rather than be replaced.
