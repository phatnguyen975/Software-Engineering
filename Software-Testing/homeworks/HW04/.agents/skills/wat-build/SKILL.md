---
name: wat-build
description: >
  Writes, fixes, and maintains Playwright TypeScript automation scripts for a
  single functional requirement. Operates in three distinct modes: Build (generate
  POM, fixtures, and spec file from a test case document), Fix (apply targeted
  corrections from human review feedback), and Record (update Actual Result and
  Status columns in the test case document after a test run). Use this skill
  whenever the user invokes /wat-build, provides an FR identifier and a path to
  a test case document, and asks to generate automation scripts, fix a specific
  issue in a script, or record test execution results. Trigger on phrases such as
  "build scripts for FR-XX", "fix selector in FR-XX spec", "record results for
  FR-XX", "generate Playwright test for FR-XX", or any request to create or
  maintain automation files from a test case document.
---

# wat-build Skill

Generates and maintains Playwright TypeScript automation scripts from a structured test case document. The skill operates in three modes and interacts directly with the `e2e/` project directory.

## Quick Reference

| Item                    | Value                                                                                                  |
| ----------------------- | ------------------------------------------------------------------------------------------------------ |
| Inputs                  | `FR_ID`, `TC_PATH`, `E2E_DIR` (all modes)                                                              |
| Mode 1 — Build          | Reads TC doc + data JSON → writes POM, fixture (if needed), spec                                       |
| Mode 2 — Fix            | Receives human feedback → applies targeted fix → explains change                                       |
| Mode 3 — Record         | Receives execution results → updates TC doc Actual Result + Status                                     |
| Output language         | **English** (all code, comments, and doc updates)                                                      |
| Playwright skill guides | [`resources/playwright-guide-routing.md`](resources/playwright-guide-routing.md)                       |
| Coding rules            | [`resources/coding-rules.md`](resources/coding-rules.md)                                               |
| File conventions        | [`resources/file-conventions.md`](resources/file-conventions.md)                                       |
| Examples (all modes)    | [`examples/examples.md`](examples/examples.md) — POM, spec, assertion patterns, Mode 3 Record, fixture |

## When to Use

- **Mode 1 — Build:** A test case document and data JSON exist for `FR_ID` and you need Playwright scripts generated from them.
- **Mode 2 — Fix:** A human review has identified specific issues in an existing script (fragile selectors, missing assertions, flaky waits, hardcoded data, isolation violations).
- **Mode 3 — Record:** A test run has completed and the test case document needs to be updated with Actual Result and Pass/Fail status.

## When NOT to Use

- No test case document exists — do not write scripts from a raw spec or SRS.
- To design test cases or decide what to test — that is a separate test-design activity.
- To run tests — this skill writes and maintains files; execution is a human step.
- To generate test reports or bug reports — those are separate activities.
- To write non-Playwright automation (Selenium, Cypress, etc.).

## Inputs

All three inputs are **required** for every mode.

| Parameter | Description                               | Example                          |
| --------- | ----------------------------------------- | -------------------------------- |
| `FR_ID`   | Identifier of the feature being automated | `FR-01`                          |
| `TC_PATH` | Path to the test case document            | `docs/fr-01/fr-01-test-cases.md` |
| `E2E_DIR` | Root path of the Playwright project       | `e2e/`                           |

**Mode 2 additional input:** Human feedback describing the specific issue(s) to fix.

**Mode 3 additional input:** Execution results — list of TC-IDs with their Actual Result text and Pass/Fail status.

## Outputs

### Mode 1 — Build

| File                | Path                                                     | Created or updated                                          |
| ------------------- | -------------------------------------------------------- | ----------------------------------------------------------- |
| Page Object         | `{E2E_DIR}/pages/{web\|admin}/{feature}.page.ts`         | Created if absent; updated if partial                       |
| Fixture (if needed) | `{E2E_DIR}/fixtures/{feature}.fixture.ts`                | Created only when feature requires dedicated setup/teardown |
| Spec file           | `{E2E_DIR}/tests/{web\|admin}/fr-{xx}-{feature}.spec.ts` | Always created                                              |

See [`resources/file-conventions.md`](resources/file-conventions.md) for naming rules and when to create a fixture.

### Mode 2 — Fix

- Updated version of the affected file(s) with the fix applied.
- A brief explanation (≤ 5 lines) of what was changed and why.

### Mode 3 — Record

- Updated `TC_PATH` file with `Actual Result` and `Status` columns filled.

## Mode Selection

Determine the mode from the human's invocation before reading any files:

```
Human provides TC_PATH + E2E_DIR with no existing spec files? → Mode 1 (Build)

Human provides feedback describing a problem in an existing script? → Mode 2 (Fix)

Human provides a list of TC-IDs with execution results? → Mode 3 (Record)
```

If the mode is ambiguous, ask the human to clarify before proceeding.

## playwright-skill Guide Routing

Read [`resources/playwright-guide-routing.md`](resources/playwright-guide-routing.md) for the complete routing table. Summary:

**Always read before writing any code (Mode 1):**

- `core/locators.md` — selector hierarchy and anti-patterns
- `core/assertions-and-waiting.md` — web-first assertions, avoiding `waitForTimeout`
- `pom/page-object-model.md` — POM structure and method patterns

**Read when the feature involves the listed concern:**

- `core/fixtures-and-hooks.md` — when writing a `*.fixture.ts` file
- `core/test-data-management.md` — when configuring data loading
- `core/authentication.md` — when the feature requires auth-state handling
- `core/forms-and-validation.md` — when the feature includes form submission and validation
- `core/react.md` — when the SUT is a React application
- `pom/pom-vs-fixtures-vs-helpers.md` — when deciding whether to create a POM, fixture, or helper

**Fallback rule:** If required information is not found in the guides above, search other files within `playwright-skill` by matching the **file name and its stated purpose** to the problem at hand. Read `core/` first, then `pom/`. Do not scan all files blindly. Never read `ci/`, `migration/`, `playwright-cli/`, `core/visual-regression.md`, `core/accessibility.md`, or `core/security-testing.md` — these are out of scope.

## Build Process (Mode 1)

Read [`resources/coding-rules.md`](resources/coding-rules.md) before writing any code. Then follow these steps in order:

### Step 1 — Read inputs

1. Read `TC_PATH` (test case document) in full.
2. Locate and read `{E2E_DIR}/data/fr-{xx}-data.json`.
3. Read the `E2E_DIR` structure to understand existing files (avoid overwriting shared code).

### Step 2 — Dispatch ui-explorer subagent

Before reading any playwright-skill guides or writing any code, dispatch the **`ui-explorer` subagent** to inspect the live SUT screens for this FR.

**What to provide to the subagent:**

- All URL paths the FR touches (derived from TC `Preconditions` and `Test Steps` columns).
- The auth role required to access each screen (`user` / `admin` / `none`).
- The specific elements to locate: every form field, button, error message, notification, and navigation item referenced in the test cases.

**What the subagent returns:**

- A structured UI context report listing confirmed locator strategies for each requested element, ranked by selector quality (see `AGENTS.md` §2.3 for the full dispatch protocol).

**Do not proceed to Step 3 until the subagent has returned its report.**

### Step 3 — Read playwright-skill guides

Apply the routing table in [`resources/playwright-guide-routing.md`](resources/playwright-guide-routing.md). Read all "always" guides plus any conditional guides that apply to this feature.

### Step 4 — Determine files to create

- Identify the target subdirectory: `tests/web/` or `tests/admin/` based on the FR's actor.
- Check whether a POM for this feature already exists in `pages/`.
- Decide whether a feature fixture is needed (see `file-conventions.md`).

### Step 5 — Write the Page Object Model

- One class per page/screen the feature interacts with.
- Extend `BasePage` from `pages/base.page.ts`.
- Expose locators as `readonly` getters using semantic selectors.
- Expose action methods (`fill`, `submit`, `click`) — no assertions inside POM.

### Step 6 — Write the fixture (if needed)

- Extend `base.fixture.ts` — never replace it.
- Fixture handles precondition setup and teardown only.
- Register the new fixture in `fixtures/base.fixture.ts` imports.

### Step 7 — Write the spec file

- Import `test` and `expect` from `@fixtures/base.fixture`.
- **Unauthenticated features:** add `test.use({ storageState: { cookies: [], origins: [] } })` at file level.
- Group test cases with `test.describe('FR-{XX}: {Feature Name}')`.
- Use `for...of` loop over data array — never `test.each()`.
- Each `test()` block maps to one TC-ID; use TC-ID as the test name prefix.
- Ensure at least **3 distinct assertion pattern types** across the spec file.
- Apply `beforeEach` / `afterEach` via fixture for setup and teardown.

### Step 8 — Self-review

Run the **Code Review Checklist** section before outputting any file.

## Fix Process (Mode 2)

1. Read the human's feedback carefully — identify the exact file, line, and issue.
2. Read the affected file.
3. Apply the **minimum change** that resolves the issue — do not rewrite unrelated code.
4. Re-run the Code Review Checklist on the changed section.
5. Output the corrected file and a brief explanation (≤ 5 lines: what changed, why).

## Record Process (Mode 3)

1. Read `TC_PATH`.
2. For each TC-ID in the provided results list:
   - Find the matching row in the test case table.
   - Fill `Actual Result` with the exact observed behaviour (not "same as expected" — write what actually happened).
   - Set `Status` to `Pass` or `Fail`.
3. Do not modify any other column.
4. Write the updated file back to `TC_PATH`.

## Human Review Gates

**Do not proceed to the next step until the human explicitly approves (`APPROVED`) or provides feedback (`FAILED: feedback`).**

```
┌──────────────────────────────────────────────────────────┐
│  [Mode 1: Build]                                         │
│                                                          │
│  Step 6 complete → AI self-review                        │
│       │                                                  │
│       ▼                                                  │
│  ┌────────────────────────────────┐                      │
│  │  HUMAN REVIEW GATE             │                      │
│  │  Human inspects generated      │                      │
│  │  POM, fixture (if any), spec   │                      │
│  │                                │                      │
│  │  APPROVED → human runs suite   │                      │
│  │  FAILED: feedback → Mode 2     │                      │
│  └────────────────────────────────┘                      │
│       │ APPROVED                                         │
│       ▼                                                  │
│  Human runs: npx playwright test (single browser first)  │
│       │                                                  │
│       ├─ Script error / crash → Mode 2 (fix)             │
│       └─ Tests execute → provide results to Mode 3       │
│                                                          │
│  [Mode 3: Record]                                        │
│       │                                                  │
│       ▼                                                  │
│  ┌────────────────────────────────┐                      │
│  │  HUMAN REVIEW GATE             │                      │
│  │  Human verifies Actual Result  │                      │
│  │  entries are accurate          │                      │
│  │                                │                      │
│  │  APPROVED → proceed            │                      │
│  │  FAILED: feedback → re-record  │                      │
│  └────────────────────────────────┘                      │
└──────────────────────────────────────────────────────────┘
```

## Code Review Checklist

Run before outputting any file in Mode 1 or Mode 2.

**Selectors**

- [ ] No CSS class selectors used where `getByRole`, `getByLabel`, or `getByTestId` would work.
- [ ] No XPath used.
- [ ] No brittle selectors that depend on element position (`:nth-child`, `nth-of-type`).

**Data**

- [ ] All input values read from the data JSON via `loadTestData()` — no hardcoded strings.
- [ ] `for...of` loop used — `test.each()` is absent.
- [ ] Data file path uses the `@data/` alias or a relative path from `E2E_DIR`.

**Assertions**

- [ ] At least 3 distinct assertion pattern types present in the spec file (e.g. `toBeVisible`, `toHaveURL`, `toHaveText`, `toHaveValue`, `toHaveCount`, `toBeEnabled`).
- [ ] No assertion inside a POM class.
- [ ] `expect(locator).toBeVisible()` used instead of `waitForTimeout()` for waiting.

**Isolation**

- [ ] Each `test()` block is independent — no shared mutable state between tests.
- [ ] Setup performed in `beforeEach` or fixture; teardown in `afterEach` or fixture.
- [ ] No `test.only()` left in any file.

**Auth**

- [ ] Features requiring auth use `storageState` from `.auth/` (injected via project config).
- [ ] Features NOT requiring auth have `test.use({ storageState: { cookies: [], origins: [] } })` at file level.
- [ ] No manual login steps inside any `test()` block.

**Structure**

- [ ] Spec imports `test` and `expect` from `@fixtures/base.fixture` (not from `@playwright/test`).
- [ ] POM extends `BasePage` from `pages/base.page.ts`.
- [ ] New fixture (if created) is registered in `fixtures/base.fixture.ts`.
- [ ] File names follow `*.page.ts` and `*.fixture.ts` conventions.
- [ ] TC-ID used as prefix in each test name: `'TC-FR{XX}-{NNN}: {title}'`.

## Core Principles

- **Test cases drive scripts** — every `test()` block must map to exactly one TC-ID. Do not invent tests not present in the TC document.
- **POM encapsulates interaction; tests express intent** — locators and actions live in the POM; assertions and data live in the spec.
- **Minimum viable change in Fix mode** — surgical edits only. Rewriting a file to fix a typo introduces unnecessary diff noise and review burden.
- **Traceability** — TC-ID in the test name is the link between the test result and the test case document. Never omit it.
- **No assertions in POM** — assertions belong in the spec file. A POM method that asserts couples two concerns and makes failures harder to diagnose.

## Anti-Patterns

| Anti-pattern                                                    | Why it is harmful                                                                        |
| --------------------------------------------------------------- | ---------------------------------------------------------------------------------------- |
| Using CSS class selectors as primary locators                   | Classes change with styling updates; causes spurious failures unrelated to functionality |
| Using `waitForTimeout(n)`                                       | Introduces arbitrary delays; makes tests slow and flaky                                  |
| Using `test.each()`                                             | Not supported by Playwright's TypeScript runner in this project; use `for...of`          |
| Hardcoding input values in the spec                             | Decouples the spec from its data file; creates drift                                     |
| Writing assertions inside POM methods                           | Couples UI interaction with test expectation; hides failures                             |
| Running tests that depend on execution order                    | Violates isolation; a single flaky precondition cascades into multiple failures          |
| Importing `test` from `@playwright/test` directly in spec files | Bypasses the fixture extension chain; breaks custom fixtures                             |
| Rewriting the entire spec in Fix mode                           | Creates unnecessary diff; risks introducing new issues while fixing one                  |
| Leaving `test.only()` in committed code                         | Silently skips the rest of the suite in CI                                               |

## Best Practices

- Read the TC document's `Preconditions` column before writing `beforeEach` — it defines exactly what state must exist before each test.
- Use `test.describe` to group all TCs for one FR — it scopes `beforeEach`/`afterEach` and makes the HTML report readable.
- Prefer `getByRole` with the `name` option over `getByLabel` when the form field has an accessible role — it is the most resilient locator type.
- Write one POM method per user action (e.g. `fillRegistrationForm()`, `submitForm()`) — avoid a single monolithic `doEverything()` method.
- Name POM action methods as verbs: `fill*`, `submit*`, `click*`, `select*`.
- Name POM locator getters as nouns: `emailInput`, `submitButton`, `errorMessage`.
- After writing the spec, read it top-to-bottom as if you are a reviewer who has never seen it — if any test's intent is unclear from the test name and assertions alone, add a comment.
- In Fix mode, always state the root cause of the issue before applying the fix — a fix without a root-cause diagnosis is likely to recur.

## Process Quality Checklist

Verify overall execution of the skill, independent of code quality.

- [ ] Mode was determined before any files were read or written.
- [ ] All three inputs (`FR_ID`, `TC_PATH`, `E2E_DIR`) were provided before starting.
- [ ] `ui-explorer` subagent was dispatched and returned a UI context report before any POM code was written (Mode 1).
- [ ] All POM locators are derived from the `ui-explorer` subagent report — none written from memory (Mode 1).
- [ ] Required playwright-skill guides were read before writing any code (Mode 1).
- [ ] Every TC-ID in the test case document has a corresponding `test()` block in the spec (Mode 1).
- [ ] Code Review Checklist was completed before outputting any file (Modes 1 and 2).
- [ ] In Mode 2: the fix explanation was provided alongside the corrected file.
- [ ] In Mode 3: every TC-ID in the result list was updated; no other columns were modified.
- [ ] No references to other skills, downstream workflows, or tool names appear in any output file.
- [ ] All output files are written in English.

## Common Rationalisations to Reject

- _"I'll use `page.locator('.submit-btn')` — it's shorter."_ → CSS class selectors are fragile. Use `getByRole('button', { name: /submit/i })`.
- _"I'll add `waitForTimeout(2000)` to let the animation finish."_ → Use `expect(locator).toBeVisible()` or `expect(locator).toBeHidden()` instead — Playwright retries automatically.
- _"The TC document only has 12 cases, but I'll add a few more I thought of."_ → Tests not in the TC document are untraceable and unreviewed. Raise them as additions to the TC document first.
- _"I'll put the email assertion inside the POM's `fillEmail()` method to save a line."_ → Assertions in POM methods hide failures and violate the single-responsibility principle.
- _"In Fix mode, I'll rewrite the whole spec while I'm in there."_ → Minimum viable change only. Unrequested rewrites introduce risk and invalidate the human's prior review.
- _"I'll import `test` from `@playwright/test` directly — it's the same thing."_ → It bypasses the custom fixture chain. Always import from `@fixtures/base.fixture`.
