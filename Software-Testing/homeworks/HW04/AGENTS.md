# AI Agent Configuration

> **Rules:** AI must not proceed to the next workflow step until the human explicitly provides `APPROVED`. If feedback is provided (`FAILED: feedback`), revise and wait for a new review. Never self-approve.

## 1. Project Overview

**Subject:** Web Automation Testing — E-Commerce SUT (EShop)

**Features under test:**

| FR ID | Feature Name                     | Actor      | App            |
| ----- | -------------------------------- | ---------- | -------------- |
| FR-01 | Account Registration             | Guest User | frontend-web   |
| FR-03 | Forgot Password & Password Reset | Guest User | frontend-web   |
| FR-17 | Coupon Management (CRUD)         | Admin      | frontend-admin |

**Stack:** Playwright · TypeScript · Multi-browser (Chromium / Firefox / WebKit) · Data-driven JSON

**Scope:** Black-box functional testing only.

- ✅ FR-01 through FR-19 (as defined in the SRS)
- ❌ FR-21 (UI / visual requirements) — out of scope
- ❌ SEC-01 through SEC-07 (security requirements) — out of scope
- ❌ GUI testing, accessibility testing, performance testing — out of scope

**Requirements source:** `docs/system-requirements-specification.md` — this is the single source of truth. Do not infer requirements from other sources.

## 2. Skills Map

Six skills are available. Read this section before reaching for any skill — it tells you which skill handles which task and what to load from each.

### 2.1 `wat-spec`

| Field           | Value                                                                               |
| --------------- | ----------------------------------------------------------------------------------- |
| SKILL.md        | `skills/wat-spec/SKILL.md`                                                          |
| Purpose         | Analyse one FR from the SRS and produce a structured feature specification document |
| Invoke          | `/wat-spec`                                                                         |
| Required inputs | `FR_ID`, `SRS_PATH`, `OUTPUT_DIR`                                                   |
| Output          | `docs/fr-{xx}/fr-{xx}-spec.md`                                                      |
| When to use     | Before test design — when a spec document does not yet exist for an FR              |

### 2.2 `wat-design`

| Field           | Value                                                                                                                      |
| --------------- | -------------------------------------------------------------------------------------------------------------------------- |
| SKILL.md        | `skills/wat-design/SKILL.md`                                                                                               |
| Purpose         | Design a complete test case suite for one FR using Domain Testing (EP + BVA) and Error Guessing; extract test data to JSON |
| Invoke          | `/wat-design`                                                                                                              |
| Required inputs | `FR_ID`, `SPEC_PATH`, `OUTPUT_DIR`                                                                                         |
| Output          | `docs/fr-{xx}/fr-{xx}-test-cases.md` · `e2e/data/fr-{xx}-data.json`                                                        |
| When to use     | After the spec for the FR has been approved                                                                                |

**`functional-test-design` usage within `wat-design`:**

- Invoke `/domain-testing` for every input field with explicit constraints (length, range, format, enum).
- Invoke `/error-guessing` after domain testing to augment with fault-attack cases.
- **Do NOT invoke** any other sub-skill (decision tables, state-transition, use-case testing) unless the spec explicitly describes combinatorial rule logic or a state machine.

### 2.3 `wat-build`

| Field           | Value                                                                                                                                        |
| --------------- | -------------------------------------------------------------------------------------------------------------------------------------------- |
| SKILL.md        | `skills/wat-build/SKILL.md`                                                                                                                  |
| Purpose         | Write, fix, and maintain Playwright TypeScript automation scripts for one FR                                                                 |
| Invoke          | `/wat-build`                                                                                                                                 |
| Required inputs | `FR_ID`, `TC_PATH`, `E2E_DIR`                                                                                                                |
| Modes           | **Build** (generate POM + fixture + spec) · **Fix** (targeted correction from feedback) · **Record** (fill Actual Result + Status in TC doc) |
| Output          | `e2e/pages/*/…page.ts` · `e2e/tests/*/fr-{xx}-…spec.ts` · updated `TC_PATH` (Record mode)                                                    |
| When to use     | After test cases have been approved; again when human review identifies script issues; again after test execution to record results          |

**Test accounts (for authentication and UI exploration):**

| Role          | Email             | Password    | App                                      |
| ------------- | ----------------- | ----------- | ---------------------------------------- |
| Standard User | `test@eshop.com`  | `Test1234!` | frontend-web (`http://localhost:5173`)   |
| Admin User    | `admin@eshop.com` | `Admin123!` | frontend-admin (`http://localhost:5174`) |

> Actual credentials are read from `.env` at runtime. The values above are the SRS defaults and match the fallbacks in `global-setup.ts`.

**Playwright MCP subagent — mandatory pre-step for Mode 1 (Build):**

Before writing any POM or spec code, the main agent must dispatch the **`ui-explorer` subagent** (`.agents/agents/ui-explorer.md`) to inspect the live SUT screens relevant to the FR being automated. The subagent navigates to each screen using Playwright MCP, captures the accessibility tree and visible locator candidates, and returns a structured UI context report.

The main agent then uses this report — not guesswork — to write locators in the POM. This is the only reliable way to produce correct, non-fragile selectors for a React SPA without access to source code.

**Subagent dispatch protocol:**

1. Main agent identifies all screens the FR touches (from `TC_PATH` Preconditions and Test Steps columns).
2. Main agent dispatches `ui-explorer` subagent with:
   - List of URLs/paths to explore
   - Auth role required (`user` / `admin` / `none`)
   - Specific elements to locate (form fields, buttons, error messages, navigation items)
3. `ui-explorer` subagent:
   - Opens each URL using Playwright MCP
   - Logs in with the appropriate test account if required
   - Captures accessibility tree snapshot for each screen
   - Identifies the best available locator for each element (getByRole > getByLabel > getByTestId > getByText > CSS)
   - Returns a structured report (see `.agents/agents/ui-explorer.md`)
4. Main agent reads the report and uses the confirmed locators when writing POM getters. No locator is written from memory.

**`playwright-skill` usage within `wat-build`:**

Always read before writing any code:

- `core/locators.md`
- `core/assertions-and-waiting.md`
- `pom/page-object-model.md`

Read when the condition applies:

| Condition                                                 | Guide                               |
| --------------------------------------------------------- | ----------------------------------- |
| Writing or updating a `*.fixture.ts`                      | `core/fixtures-and-hooks.md`        |
| Configuring data loading                                  | `core/test-data-management.md`      |
| Feature is unauthenticated OR needs storageState handling | `core/authentication.md`            |
| Feature includes form submission and validation           | `core/forms-and-validation.md`      |
| SUT is a React application                                | `core/react.md`                     |
| Deciding POM vs fixture vs helper                         | `pom/pom-vs-fixtures-vs-helpers.md` |

**Fallback:** If the required information is not in the guides above, search other files in `playwright-skill` by **file name and stated purpose** — do not scan all files. Prioritise `core/` over `pom/`. Never read `ci/`, `migration/`, `playwright-cli/`, `core/visual-regression.md`, `core/accessibility.md`, or `core/security-testing.md`.

### 2.4 `wat-report`

| Field           | Value                                                                                 |
| --------------- | ------------------------------------------------------------------------------------- |
| SKILL.md        | `skills/wat-report/SKILL.md`                                                          |
| Purpose         | Group failing TCs by root cause and produce a structured bug report                   |
| Invoke          | `/wat-report`                                                                         |
| Required inputs | `FR_ID`, `TC_PATH` (must have Actual Result + Status filled)                          |
| Output          | `docs/fr-{xx}/fr-{xx}-bug-report.md`                                                  |
| When to use     | After test execution and Mode 3 Record, only when at least one TC has `Status = Fail` |

### 2.5 `functional-test-design` (supporting — used inside `wat-design`)

| Field               | Value                                                                            |
| ------------------- | -------------------------------------------------------------------------------- |
| SKILL.md            | `skills/functional-test-design/SKILL.md`                                         |
| GitHub              | https://github.com/phatnguyen975/functional-test-design                          |
| Author              | phatnguyen975                                                                    |
| Purpose             | Provide EP/BVA and error-guessing techniques for test case design                |
| Active sub-skills   | `/domain-testing` · `/error-guessing`                                            |
| Inactive sub-skills | All others — do not invoke                                                       |
| When to use         | Invoked internally during `wat-design` execution — not invoked directly by human |

### 2.6 `ai-audit-log` (human-invoked only)

| Field       | Value                                                                         |
| ----------- | ----------------------------------------------------------------------------- |
| SKILL.md    | `skills/ai-audit-log/SKILL.md`                                                |
| GitHub      | https://github.com/phatnguyen975/ai-audit-log                                 |
| Author      | phatnguyen975                                                                 |
| Purpose     | Log AI interactions (prompt → output) for the AI Audit Report                 |
| Invoke      | `/ai-audit-log` — **human invokes manually after each AI session**            |
| Output      | `docs/audit/ai/<fullname>-YYYY-MM.log.md`                                     |
| When to use | After each `/wat-spec`, `/wat-design`, `/wat-build`, or `/wat-report` session |
| Automation  | Never invoked automatically — always a deliberate human action                |

### 2.7 `playwright-skill` (supporting — used inside `wat-build`)

| Field       | Value                                                                                                                |
| ----------- | -------------------------------------------------------------------------------------------------------------------- |
| SKILL.md    | `skills/playwright-skill/SKILL.md`                                                                                   |
| GitHub      | https://github.com/testdino-hq/playwright-skill                                                                      |
| Author      | testdino-hq                                                                                                          |
| Purpose     | Provide Playwright best-practice guides for locators, assertions, fixtures, POM, authentication, and data management |
| When to use | Invoked internally during `wat-build` execution — not invoked directly by human                                      |

## 3. Repository Structure

### 3.1 `docs/` — Documentation and Artefacts

```
docs/
├── system-requirements-specification.md   ← SRS — DO NOT MODIFY
│
├── fr-01/                                 ← FR-01: Account Registration
│   ├── fr-01-spec.md                      ← output: wat-spec
│   ├── fr-01-test-cases.md                ← output: wat-design (updated by wat-build Mode 3)
│   └── fr-01-bug-report.md                ← output: wat-report (only if failures exist)
│
├── fr-03/                                 ← FR-03: Forgot Password & Password Reset
│   ├── fr-03-spec.md
│   ├── fr-03-test-cases.md
│   └── fr-03-bug-report.md
│
└── fr-17/                                 ← FR-17: Coupon Management (CRUD)
    ├── fr-17-spec.md
    ├── fr-17-test-cases.md
    └── fr-17-bug-report.md
```

> `docs/` contains documents only — no code, no runtime data files. JSON test data lives in `e2e/data/` because scripts read it directly at runtime.

### 3.2 `e2e/` — Playwright Test Project

```
e2e/
├── playwright.config.ts        ← multi-project config: web × 3 browsers, admin × 3 browsers
├── global-setup.ts             ← authenticates web user + admin via API; saves storageState to .auth/
├── global-teardown.ts          ← post-suite cleanup (currently a no-op placeholder)
├── tsconfig.json               ← TypeScript config with path aliases (@fixtures, @pages, @helpers, @data)
├── .env.example                ← env variable template (committed); actual .env is gitignored
│
├── .auth/                      ← storageState files — GITIGNORED
│   ├── user.json               ← web user session (written by global-setup.ts)
│   └── admin.json              ← admin session (written by global-setup.ts)
│
├── fixtures/
│   ├── base.fixture.ts         ← fixture index — ALL spec files import { test, expect } from here
│   └── *.fixture.ts            ← feature-specific fixtures (created by wat-build when needed)
│
├── pages/
│   ├── base.page.ts            ← abstract BasePage class (navigate, waitForURL, getTitle)
│   ├── web/                    ← POM files for frontend-web (FR-01, FR-07)
│   └── admin/                  ← POM files for frontend-admin (FR-17)
│
├── helpers/
│   └── data-loader.ts          ← loadTestData<T>(filename) — reads from data/, returns typed array
│
├── data/                       ← JSON test data files — one per FR
│   ├── fr-01-registration.json
│   ├── fr-03-forgot-password.json
│   └── fr-17-coupon.json
│
└── tests/
    ├── web/                    ← spec files for frontend-web features (FR-01, FR-07)
    └── admin/                  ← spec files for frontend-admin features (FR-17)
```

**Key path aliases** (defined in `tsconfig.json`):

| Alias         | Resolves to      |
| ------------- | ---------------- |
| `@fixtures/*` | `e2e/fixtures/*` |
| `@pages/*`    | `e2e/pages/*`    |
| `@helpers/*`  | `e2e/helpers/*`  |
| `@data/*`     | `e2e/data/*`     |

## 4. Workflow

The following workflow applies to each FR independently. Complete all steps for one FR before starting the next.

```
┌───────────────────────────────────────────────────────────────────┐
│                    WORKFLOW PER FR                                │
├───────────────────────────────────────────────────────────────────┤
│                                                                   │
│  /wat-spec  (FR_ID, SRS_PATH, OUTPUT_DIR)                         │
│       │                                                           │
│       ▼                                                           │
│  ┌──────────────────────────────────────────────────┐             │
│  │  GATE #1 — Spec Review                           │             │
│  │  Human verifies: all sections complete, no       │             │
│  │  fabricated constraints, ACs are falsifiable     │             │
│  │                                                  │             │
│  │  APPROVED → proceed to wat-design                │             │
│  │  FAILED + feedback → revise wat-spec, re-review  │             │
│  └──────────────────────────────────────────────────┘             │
│       │ APPROVED                                                  │
│       ▼                                                           │
│  /wat-design  (FR_ID, SPEC_PATH, OUTPUT_DIR)                      │
│       │                                                           │
│       ▼                                                           │
│  ┌──────────────────────────────────────────────────┐             │
│  │  GATE #2 — Test Case Review                      │             │
│  │  Human verifies: EP tables present, combination  │             │
│  │  rule applied, isolation rule applied, ≥12 TCs,  │             │
│  │  expected results specific, data JSON consistent │             │
│  │                                                  │             │
│  │  APPROVED → proceed to wat-build                 │             │
│  │  FAILED + feedback → revise wat-design, re-review│             │
│  └──────────────────────────────────────────────────┘             │
│       │ APPROVED                                                  │
│       ▼                                                           │
│  /wat-build  (FR_ID, TC_PATH, E2E_DIR)  ← Mode 1: Build           │
│       │                                                           │
│       ▼                                                           │
│  AI self-review → Code Review Checklist (wat-build SKILL.md)      │
│       │                                                           │
│       ▼                                                           │
│  ┌──────────────────────────────────────────────────┐             │
│  │  GATE #3 — Script Review                         │             │
│  │  Human inspects POM, fixture (if any), spec:     │             │
│  │  selectors, assertions, isolation, data loading, │             │
│  │  auth override, TC-ID in test names              │             │
│  │                                                  │             │
│  │  APPROVED → human runs suite                     │             │
│  │  FAILED + feedback → wat-build Mode 2 (Fix),     │             │
│  │                       then re-review GATE #3     │             │
│  └──────────────────────────────────────────────────┘             │
│       │ APPROVED                                                  │
│       ▼                                                           │
│  Human runs: npx playwright test (single browser first,           │
│       │      then full multi-browser run)                         │
│       │                                                           │
│       ├── Script error / crash                                    │
│       │     → /wat-build Mode 2 (Fix) → re-run                    │
│       │                                                           │
│       └── Tests complete (pass or fail)                           │
│             │                                                     │
│             ▼                                                     │
│  /wat-build  (FR_ID, TC_PATH, E2E_DIR)  ← Mode 3: Record          │
│  (provide TC-ID list + actual results + pass/fail status)         │
│       │                                                           │
│       ▼                                                           │
│  ┌──────────────────────────────────────────────────┐             │
│  │  GATE #4 — Actual Results Review                 │             │
│  │  Human verifies Actual Result entries are        │             │
│  │  accurate and Status assignments are correct     │             │
│  │                                                  │             │
│  │  APPROVED → check if any TC has Status = Fail    │             │
│  │  FAILED + feedback → re-record, re-review        │             │
│  └──────────────────────────────────────────────────┘             │
│       │ APPROVED                                                  │
│       │                                                           │
│       ├── No failures → workflow complete for this FR             │
│       │                                                           │
│       └── At least one Status = Fail                              │
│             │                                                     │
│             ▼                                                     │
│  /wat-report  (FR_ID, TC_PATH)                                    │
│       │                                                           │
│       ▼                                                           │
│  ┌──────────────────────────────────────────────────┐             │
│  │  GATE #5 — Bug Report Review                     │             │
│  │  Human verifies: grouping is correct, root cause │             │
│  │  is specific, severity/priority are justified,   │             │
│  │  all failing TCs are accounted for               │             │
│  │                                                  │             │
│  │  APPROVED → human creates GitHub Issues manually │             │
│  │  FAILED + feedback → revise wat-report, re-review│             │
│  └──────────────────────────────────────────────────┘             │
│                                                                   │
│  /ai-audit-log  ← human invokes manually after each AI session    │
│                                                                   │
└───────────────────────────────────────────────────────────────────┘
```

### Human Verification Gate Rules

| Rule                  | Detail                                                                               |
| --------------------- | ------------------------------------------------------------------------------------ |
| Two outcomes only     | Each gate produces either `APPROVED` or `FAILED + specific feedback`                 |
| Feedback format       | State: (1) what is wrong, (2) where exactly, (3) what the correct version should be  |
| No self-approval      | AI must never proceed to the next step without an explicit `APPROVED` from the human |
| No cascading approval | An APPROVED at Gate N does not imply approval at Gate N+1                            |
| Revision loop         | After a fix, the same gate is re-evaluated — not the next one                        |

## 5. Guardrails

Rules that apply globally across all skills and artefacts in this project.

### Testing scope

- Test only functional behaviour — not visual layout, not performance, not security.
- Do not write tests for FR-21 (UI requirements) or SEC-01–07 (security requirements).
- Do not infer requirements not explicitly stated in the SRS.

### Requirements fidelity

- `docs/system-requirements-specification.md` is the single source of truth.
- Never fabricate constraints, rules, or acceptance criteria not present in the SRS.
- When the SRS is ambiguous, record the ambiguity in Test Notes — do not guess.

### Workflow discipline

- Never proceed past a Human Verification Gate without an explicit `APPROVED`.
- Never self-approve. Never cascade approval from one gate to the next.
- Feedback at any gate triggers a revision loop on the same gate, not the next.

### Language

- All artefacts (spec documents, test case documents, bug reports, code comments, this file) must be written in **English**.
- Source documents (SRS) may be in another language — outputs are always English.

## 6. Artefact Linking

The table below shows how artefacts flow between skills for each FR.

| Step | Skill                | Reads                                                               | Writes                                                                               |
| ---- | -------------------- | ------------------------------------------------------------------- | ------------------------------------------------------------------------------------ |
| 1    | `wat-spec`           | `docs/system-requirements-specification.md`                         | `docs/fr-{xx}/fr-{xx}-spec.md`                                                       |
| 2    | `wat-design`         | `docs/fr-{xx}/fr-{xx}-spec.md`                                      | `docs/fr-{xx}/fr-{xx}-test-cases.md` · `e2e/data/fr-{xx}-data.json`                  |
| 3    | `wat-build` (Build)  | `docs/fr-{xx}/fr-{xx}-test-cases.md` · `e2e/data/fr-{xx}-data.json` | `e2e/pages/*/…page.ts` · `e2e/fixtures/…fixture.ts` · `e2e/tests/*/fr-{xx}-…spec.ts` |
| 4    | `wat-build` (Record) | Execution results (human-supplied)                                  | `docs/fr-{xx}/fr-{xx}-test-cases.md` (Actual Result + Status columns)                |
| 5    | `wat-report`         | `docs/fr-{xx}/fr-{xx}-test-cases.md` (with results)                 | `docs/fr-{xx}/fr-{xx}-bug-report.md`                                                 |
