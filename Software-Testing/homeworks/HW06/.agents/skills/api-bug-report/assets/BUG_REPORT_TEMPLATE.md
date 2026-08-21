# Bug Report: {METHOD} {PATH}

**Feature:** {feature_id} | **Endpoint:** `{METHOD} {PATH}`  
**Total Bugs:** {N} | **Generated:** {YYYY-MM-DD}  
**Status:** Draft — Pending human review

> **Reviewer instructions:**
>
> 1. Verify accuracy of Steps to Reproduce and Expected Results against the contract
> 2. Confirm or override AI-suggested Severity and Priority for each bug
> 3. Add screenshots to Evidence sections before submitting to GitHub Issues
> 4. Change Status from `Draft` to `Reviewed` when done

## Non-Bug Failures (Excluded from Bug Report)

> List any FAIL TCs that were determined to be script errors or environment issues — not real SUT defects. If none, write "None."

| TC ID         | Reason Excluded                                                           |
| ------------- | ------------------------------------------------------------------------- |
| {TC-FR01-xxx} | {e.g., "Collection script error — wrong variable name used in assertion"} |

## BUG-{feature_id}-001

**Title:** {Concise description of the incorrect behavior, < 80 characters}  
**Severity:** Critical / High / Medium / Low _(AI-suggested — confirm or override)_  
**Priority:** P1 / P2 / P3 / P4 _(AI-suggested — confirm or override)_  
**Root Cause Category:** AUTH / VALIDATION / BUSINESS_LOGIC / SCHEMA / SECURITY / STATE  
**Status:** Open  
**Related TCs:** {TC-{feature_id}-SEC-001, TC-{feature_id}-FR-005, ...}

### Description

{Describe what the bug is, what behavior was observed, and why it is a defect. Reference the specific contract clause violated, e.g., "Violates BR-01: email must be unique" or "Violates SEC-01: SQL injection must be rejected with 400, not cause a 500 error."}

### Steps to Reproduce

1. Start the API server (e.g., `node server.js` on `localhost:3000`).
2. {Describe any prerequisite state — e.g., "Ensure no user with email test@example.com exists."}
3. Send the following request:

   ```
   {METHOD} {PATH}
   Content-Type: application/json
   Authorization: Bearer {token type if needed}

   {
     "field": "value"
   }
   ```

4. Observe the response.

### Expected Result

> _Source: CONTRACT.md — {section reference, e.g., "Section 4.2 — 400 Bad Request" or "BR-01"}_

{Describe the correct behavior as specified in the contract. Quote or closely paraphrase the relevant contract section.}

### Actual Result

**HTTP Status:** {e.g., 500}  
**Response Body:**

```json
{
  "observed": "response body"
}
```

{Describe the incorrect behavior observed — be specific about what was wrong.}

### Evidence

- **Newman Report:** `{path/to/feature_id-report.html}`
- **Screenshot:** _(Add screenshot before submitting to GitHub Issues)_

### Impact

{Describe the concrete risk to users or the system. Examples:

- "Any user can register multiple accounts with the same email address, bypassing uniqueness constraints."
- "SQL injection in the email field may allow an attacker to read or modify database contents."
- "Admin endpoint accessible without authentication exposes all user data to unauthenticated callers."}

### Notes

{Optional: Additional context, related issues, or suggested fix direction. Leave blank if not applicable.}

## BUG-{feature_id}-002

**Title:** {Title}  
**Severity:** Critical / High / Medium / Low _(AI-suggested)_  
**Priority:** P1 / P2 / P3 / P4 _(AI-suggested)_  
**Root Cause Category:** AUTH / VALIDATION / BUSINESS_LOGIC / SCHEMA / SECURITY / STATE  
**Status:** Open  
**Related TCs:** {...}

### Description

{...}

### Steps to Reproduce

1. {...}

### Expected Result

> _Source: CONTRACT.md — {...}_

{...}

### Actual Result

**HTTP Status:** {...}  
**Response Body:**

```json
{}
```

{...}

### Evidence

- **Newman Report:** `{path/to/report.html}`
- **Screenshot:** _(Add before submitting)_

### Impact

{...}

### Notes

{...}

## Bug Summary Table

> Fill this in after all bug entries are written. Useful for quick triage.

| Bug ID               | Title   | Category   | Severity   | Priority   | Status |
| -------------------- | ------- | ---------- | ---------- | ---------- | ------ |
| BUG-{feature_id}-001 | {Title} | {Category} | {Severity} | {Priority} | Open   |
| BUG-{feature_id}-002 | {Title} | {Category} | {Severity} | {Priority} | Open   |
