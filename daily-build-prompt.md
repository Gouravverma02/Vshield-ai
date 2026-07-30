# VShield.ai — Daily Build Prompt (30-Day Growth Plan)

Copy this prompt into a new chat each day, replacing only the day number. Attach `30-day-growth-plan.md`, your current `PROJECT-STRUCTURE.md`, and any file you're actively working on if the AI doesn't already have context.

---

```
Day [X] of my VShield.ai 30-Day Growth Plan.

Read 30-day-growth-plan.md and find Day [X]'s milestone. Use it as today's only scope —
do not redesign the project, do not jump ahead to a later day's work, and do not introduce
features not listed in the plan.

Before writing any code, briefly confirm what Day [X-1] left in place (ask me to paste
relevant files or share my repo link if you need current context).

Standing rules:
- Assume I need step-by-step guidance for any manual task (installing packages, configuring
  services, running commands, deploying). Give me exact button names, menu paths, and terminal
  commands. Wait for my confirmation before continuing.
- Prioritize implementation over explanation. Generate complete, final file contents only —
  never snippets, placeholders, or "...existing code...".
- Clearly state whether each file is new or replaces an existing one, and its exact path.
- Use only free tools and services unless I say otherwise.
- If something breaks, debug it fully before moving forward. Never build on top of broken code.
- Treat this as a real production codebase — apply the same rigor around security, error
  handling, and testing that the original 10-day capstone build did.

When today's milestone is complete:
- Verify it works (ask me to test and share results/screenshots).
- Update any documentation this milestone affects (SCHEMA.md, API.md, ARCHITECTURE.md,
  TESTING.md, README.md — whichever apply).
- Help me commit and push with a clear, specific commit message.
- Give me a short summary: what was built today, and what tomorrow's Day [X+1] will cover.

My repo: https://github.com/Gouravverma02/Vshield-ai
My live app: https://vshield-ai-furt.onrender.com
```

---

**Usage note:** Days that involve deployment, external service signups (databases, email providers), or security-sensitive features (Weeks 2-4) should always end with a live verification step before moving to the next day — this mirrors exactly how Days 6, 8, and 9 of the original capstone caught real production issues before they became bigger problems.
