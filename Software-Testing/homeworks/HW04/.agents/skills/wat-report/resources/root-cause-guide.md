# Root Cause Analysis Guide — wat-report

Detailed procedure for analysing failing test cases and grouping them by shared root cause before writing bug report entries.

## What Is a Root Cause?

A root cause is the **single underlying defect** that produces one or more observable failures. It is distinct from:

- **Symptom** — what the tester observed (e.g. "no error message shown")
- **Effect** — what the user experiences (e.g. "cannot register")
- **Root cause** — why the system behaved incorrectly (e.g. "server-side uniqueness check not returning a 409 response with an error body")

Root cause analysis locates the defect at the **system behaviour level** — not at the UI symptom level and not at the code-implementation level (which requires access to source code).

## Step 1 — Collect All Failing Test Cases

Read the test case document and extract every row where `Status = Fail`. Create a working list:

```
Failing TCs:
  TC-FR01-008 | Actual: no error shown on Confirm Password field; server returned 422
  TC-FR01-009 | Actual: page shows blank screen after submit with all empty fields
  TC-FR01-017 | Actual: duplicate account created for EXISTING@EXAMPLE.COM
```

## Step 2 — Analyse Each Failure

For each failing TC, answer these four questions:

1. **What did the system do?** (from `Actual Result`)
2. **What should the system have done?** (from `Expected Result`)
3. **At which system layer did the failure occur?**
   - Client-side validation (browser, before any network request)
   - Server-side validation (API response)
   - UI rendering (correct API response, but UI did not display it)
   - Data persistence (wrong data written or not written)
4. **What input condition triggered the failure?** (from `Input Data`)

Write a one-sentence root cause hypothesis for each TC before grouping.

## Step 3 — Apply Grouping Rules

Group TCs that share the same root cause. Use these rules in order:

### Rule 1 — Same system layer + same input condition → same bug

If two TCs fail at the same layer (e.g. both fail because client-side validation does not trigger) AND involve the same input condition category (e.g. both involve password-related fields), they are the same bug.

```
TC-FR01-006 (missing uppercase) → client-side password validation not triggered
TC-FR01-007 (missing digit)     → client-side password validation not triggered
→ SAME BUG: BUG-FR01-001
```

### Rule 2 — Same API endpoint returning wrong response → same bug

If two TCs fail because the same API endpoint either does not return the expected error code or does not return an error body, they are the same bug.

```
TC-FR01-008 (password mismatch) → POST /api/register returns 422, UI does not surface it
TC-FR01-009 (all fields empty)  → POST /api/register returns 422, UI does not surface it
→ SAME BUG: BUG-FR01-002  (UI does not handle 422 responses from /api/register)
```

### Rule 3 — Same UI element missing or incorrect → same bug

If two TCs fail because the same UI component is absent, renders incorrectly, or displays wrong content, they are the same bug.

```
TC-FR01-004 (duplicate email, Chromium) → error banner not rendered
TC-FR01-004 (duplicate email, Firefox)  → error banner not rendered
→ SAME BUG: BUG-FR01-003  (cross-browser: same UI element, same failure)
```

### Rule 4 — Different layers or different triggers → different bugs

If one TC fails because of missing client-side validation and another fails because of incorrect server response, they are different bugs even if both produce "no error message shown" as the symptom.

## Step 4 — Verify Grouping Completeness

Before assigning Bug IDs, verify:

- Every failing TC appears in exactly one bug entry (as the primary or in `Affects TCs`).
- No passing TC is included in any bug entry.
- No two bug entries describe the same root cause in different words.

## Step 5 — Assign Bug IDs

After grouping is confirmed, assign IDs sequentially: `BUG-FR{XX}-001`, `BUG-FR{XX}-002`, ...

Order bugs by severity (Critical first, then Major, Minor, Trivial) so the most impactful defects appear first in the report.

## Determining Steps to Reproduce

Derive Steps to Reproduce directly from the failing TC's `Test Steps` column. Adapt the language to be written from the perspective of a developer or tester reproducing the bug manually — not as an automation instruction.

```
TC Test Step:                       Steps to Reproduce:
"3. Enter Email"             →      "3. Enter an email address that is already registered (e.g. existing@example.com)"
"6. Click Register"          →      "6. Click the Register button"
```

Always end with: `"Observe: {what happens — the actual result}"`

## Evidence Sources

Evidence for bug reports comes exclusively from the test execution artefacts:

| Source                            | What to record                                 |
| --------------------------------- | ---------------------------------------------- |
| Playwright HTML report            | Screenshot filename and path                   |
| Playwright trace                  | Trace file path (if available)                 |
| Browser DevTools (if noted in TC) | HTTP status code, response body, console error |
| `Actual Result` column in TC doc  | The observed behaviour — quote verbatim        |

Do not fabricate evidence. If no screenshot exists, write: `"Screenshot: not captured — test runner did not save artefact for this run."`
