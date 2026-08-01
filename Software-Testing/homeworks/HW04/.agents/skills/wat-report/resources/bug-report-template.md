# Bug Report Template — wat-report

Use this template for every bug entry in the output file. One template block per distinct root cause.

## Template

```markdown
## {BUG-ID}

### Summary

| Field        | Value                                                                                                                                                                                                                                                                                |
| ------------ | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ |
| Bug ID       | {BUG-FR{XX}-{NNN}}                                                                                                                                                                                                                                                                   |
| Title        | {Action + Function + Condition — describes the defect, not the symptom. E.g. "Registration form fails to display error when email is already registered"}                                                                                                                            |
| Feature      | {FR-ID} — {Feature Name}                                                                                                                                                                                                                                                             |
| Root Cause   | {One or two sentences: the specific system behaviour that is incorrect and why it causes the failures. E.g. "The server returns HTTP 422 when a duplicate email is submitted, but the frontend does not handle 422 responses from POST /api/register and renders no error message."} |
| Affects TCs  | {TC-FR{XX}-{NNN}, TC-FR{XX}-{NNN}, ...}                                                                                                                                                                                                                                              |
| GitHub Issue | [Issue #N](/path/to/issue/N)                                                                                                                                                                                                                                                         |

### Severity & Priority

| Field    | Value                                | Reason                                              |
| -------- | ------------------------------------ | --------------------------------------------------- |
| Severity | {Critical / Major / Minor / Trivial} | {One sentence: extent of user or system impact}     |
| Priority | {High / Medium / Low}                | {One sentence: business urgency and release impact} |

### Environment

| Field       | Value                                                                                         |
| ----------- | --------------------------------------------------------------------------------------------- |
| Browser     | {e.g. Chromium 124, Firefox 125, WebKit — list all browsers on which the defect was observed} |
| OS          | {e.g. Windows 11, macOS 14}                                                                   |
| Web URL     | {e.g. http://localhost:5173 — or "N/A" if admin-only}                                         |
| Admin URL   | {e.g. http://localhost:5174 — or "N/A" if web-only}                                           |
| SUT Version | {Git commit hash or tag — or "Not recorded"}                                                  |

### Steps to Reproduce

1. {First step}
2. {Second step}
3. {Continue…}

**Observe:** {Exact observed behaviour — the actual result}

### Expected Result

{Copy verbatim from the `Expected Result` column of the primary failing TC.}

### Actual Result

{Copy verbatim from the `Actual Result` column of the primary failing TC. If grouped TCs have different actual results, describe the common observable behaviour and note any variations.}

### Evidence

- **Screenshot:** {Filename from Playwright report — e.g. `playwright-report/data/screenshot-TC-FR01-008.png` — or "not captured"}
- **Playwright Report:** {Path to HTML report — e.g. `playwright-report/index.html`}
- **Trace:** {Path to trace file — or "not available"}

### Notes

{Additional context: browsers where the defect does not reproduce, related test cases that pass, open questions, or workarounds observed during testing. Write "None." if not applicable.}
```

## Field Guidance

### Title format: Action + Function + Condition

| Component | Describes                                         |
| --------- | ------------------------------------------------- |
| Action    | What the system fails to do (or does incorrectly) |
| Function  | The feature or component involved                 |
| Condition | The input or state that triggers the defect       |

**Examples:**

- ✅ `Registration form fails to display error when email is already registered`
- ✅ `Coupon creation allows duplicate coupon codes`
- ✅ `Cart quantity does not update when item is added twice`
- ❌ `Bug in registration` — too vague
- ❌ `Error message issue` — symptom, not defect description

### Root Cause format

Root cause is written at the **system behaviour** level — not code level, not symptom level.

**Examples:**

- ✅ `POST /api/register returns HTTP 422 for duplicate emails but the frontend does not render any error message when it receives a 4xx response.`
- ✅ `The coupon creation endpoint does not enforce uniqueness on the code field; two coupons with the same code can be persisted.`
- ❌ `Something wrong with validation` — too vague
- ❌ `Missing if statement in controller` — code-level; not derivable from black-box testing

### Affects TCs

List all TC-IDs that were grouped under this root cause. The primary TC (whose steps and expected result are used in the report) should be listed first.

```
Affects TCs: TC-FR01-008, TC-FR01-009
```
