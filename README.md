# 🛡️ VShield.ai

**Rule-based scam & impersonation detection for social media managers.**

Paste a suspicious brand deal, DM, or email — get an instant, explainable verdict on whether it's Safe, Suspicious, or Dangerous, with clear reasons and next steps. Built as a 10-day capstone for the **AB Talks 60-Day Claude AI Challenge**.

🔗 **Live app:** [vshield-ai-furt.onrender.com](https://vshield-ai-furt.onrender.com)
> ⏱️ Free-tier hosting spins down after inactivity — first load may take ~50 seconds to wake up.

---

## The Problem

Social media managers and small agencies field constant inbound offers, sponsorships, and DMs on behalf of the creators they represent. Some are scams — fake agencies trying to steal content, phishing attempts disguised as brand deals, or credential-harvesting messages that lead to account takeovers. There's rarely a fast, structured way to check before responding.

## The Solution

VShield.ai runs any pasted text through **5 explainable, rule-based checks**:

1. **Urgency / pressure language** — "act now," "verify immediately," "limited time"
2. **Suspicious shortened links** — bit.ly, tinyurl, and similar
3. **Sender/brand mismatch** — claims to be a known brand, emails from a generic/mismatched domain
4. **Sensitive info requests** — passwords, OTPs, payment details, gift cards
5. **Generic / mass-sent tone** — templated greetings, excessive punctuation

Each check contributes a weighted score, mapped to a verdict: 🟢 **Safe**, 🟡 **Suspicious**, or 🔴 **Dangerous** — with the exact reasons shown, never a black box.

## Features

- 🔐 Secure signup/login (BCrypt password hashing, rate-limited against brute force)
- 🔍 Instant message analysis with risk score, reasons, and next steps
- 📜 Private history log of every past check, per account
- 🎨 Polished, accessible, responsive UI with loading/empty/error states

## Tech Stack

| Layer | Choice |
|---|---|
| Backend | Java 22, Spring Boot 4, Spring Security, Spring Data JPA |
| Database | H2 (embedded, file-based) |
| Frontend | HTML, CSS, vanilla JavaScript — no framework |
| Hosting | Render (free tier, Docker-based deploy) |

No external AI/ML API is used for detection — it's intentionally rule-based and fully explainable. See [`ARCHITECTURE.md`](ARCHITECTURE.md) for why.

## Documentation

| Doc | Contents |
|---|---|
| [`VShield_PRD.docx`](VShield_PRD.docx) | Product requirements |
| [`ARCHITECTURE.md`](ARCHITECTURE.md) | System design, diagrams, data flow |
| [`SCHEMA.md`](SCHEMA.md) | Database design |
| [`API.md`](API.md) | Every endpoint's contract |
| [`UI-WIREFRAMES.md`](UI-WIREFRAMES.md) | Screen designs and user flow |
| [`PROJECT-STRUCTURE.md`](PROJECT-STRUCTURE.md) | Folder structure and rationale |
| [`SETUP.md`](SETUP.md) | Local setup from scratch |
| [`ENVIRONMENT.md`](ENVIRONMENT.md) | Configuration reference |
| [`TESTING.md`](TESTING.md) | QA, security, and hardening log |
| [`CODE-WALKTHROUGH.md`](CODE-WALKTHROUGH.md) | Line-by-line explanation of the codebase |

## Running Locally

```bash
git clone https://github.com/Gouravverma02/Vshield-ai.git
cd Vshield-ai
```

Open the project in IntelliJ IDEA, let Maven import dependencies, then run `VshieldApplication.java`. Full details in [`SETUP.md`](SETUP.md).

App runs at `http://localhost:8080`.

## Known Limitations

- Free-tier hosting uses ephemeral storage — data resets when the instance spins down from inactivity. See [`TESTING.md`](TESTING.md) for details.
- No email verification or password reset (out of scope for v1.0 — see PRD).

## Roadmap (v2.0+)

- Account Risk Guard — audit a connected social account's own security settings
- AI-assisted detection layer for novel scam patterns
- Team accounts for agencies
- Browser extension for one-click checking

## License

MIT — see [`LICENSE`](LICENSE).

---

*Built with [Claude](https://claude.com) as part of the AB Talks 60-Day Claude AI Challenge.*