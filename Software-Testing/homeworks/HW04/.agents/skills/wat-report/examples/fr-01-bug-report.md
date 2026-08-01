# Bug Report — FR-01: Account Registration

> **Source TC document:** `docs/fr-01/fr-01-test-cases.md`  
> **Generated:** 2026-07-30  
> **Bugs identified:** 2

## Grouping Analysis (internal — not part of final report)

```
TC-FR01-008 | Password mismatch
            | Actual: page stayed on /register, no error on Confirm Password field. Server returned 422.
            | Layer: UI rendering (422 response not surfaced)

TC-FR01-009 | All fields empty
            | Actual: blank screen after submit; no field-level errors displayed. Server returned 422.
            | Layer: UI rendering (422 response not surfaced)

TC-FR01-017 | Uppercase email
            | Actual: new account created for EXISTING@EXAMPLE.COM duplicate (case variant). No error shown.
            | Layer: Server-side validation (email uniqueness not case-insensitive)

Grouping result:
  BUG-FR01-001 ← TC-FR01-008, TC-FR01-009 (same layer: UI does not handle 422 from POST /api/register)
  BUG-FR01-002 ← TC-FR01-017 (different layer: server uniqueness check is case-sensitive)
```

## BUG-FR01-001

### Summary

| Field        | Value                                                                                                                                                                                                                                                                                     |
| ------------ | ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| Bug ID       | BUG-FR01-001                                                                                                                                                                                                                                                                              |
| Title        | Registration form fails to display validation errors when server returns HTTP 422                                                                                                                                                                                                         |
| Feature      | FR-01 — Account Registration                                                                                                                                                                                                                                                              |
| Root Cause   | The frontend does not handle HTTP 422 responses from `POST /api/register`. When the server rejects a submission (e.g. password mismatch, empty required fields), the API returns a 422 with an error body, but the UI renders no field-level error messages and does not notify the user. |
| Affects TCs  | TC-FR01-008, TC-FR01-009                                                                                                                                                                                                                                                                  |
| GitHub Issue | [Issue 1](/path/to/issue/1)                                                                                                                                                                                                                                                               |

### Severity & Priority

| Field    | Value | Reason                                                                                                                                                            |
| -------- | ----- | ----------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| Severity | Major | Core registration validation is non-functional; users receive no feedback on submission errors and cannot complete registration without guessing what went wrong. |
| Priority | High  | Blocks the primary registration flow and its acceptance criteria; must be resolved before user-facing release.                                                    |

### Environment

| Field       | Value                                                       |
| ----------- | ----------------------------------------------------------- |
| Browser     | Chromium 124, Firefox 125, WebKit (reproduced on all three) |
| OS          | Windows 11                                                  |
| Web URL     | http://localhost:5173                                       |
| Admin URL   | N/A                                                         |
| SUT Version | Not recorded                                                |

### Steps to Reproduce

1. Navigate to `http://localhost:5173/register`.
2. Enter a valid Full Name (e.g. `Nguyen Van A`).
3. Enter a valid unique Email (e.g. `test.user@example.com`).
4. Enter a valid Password (e.g. `ValidPass1`).
5. Enter a **different** value in Confirm Password (e.g. `DifferentPass1`).
6. Click the **Register** button.

**Observe:** The page remains on `/register`. No error message appears on the Confirm Password field or anywhere else on the page. The browser DevTools Network tab shows `POST /api/register` returned HTTP 422.

### Expected Result

Field-level error displayed on the Confirm Password field: "Passwords do not match." No account is created.

### Actual Result

Page remained on `/register`. No error message was displayed on the Confirm Password field. Form submitted silently. Browser console showed HTTP 422 from `POST /api/register` but the UI did not surface the error body.

### Evidence

- **Screenshot:** `playwright-report/data/screenshot-TC-FR01-008-chromium.png`
- **Playwright Report:** `playwright-report/index.html`
- **Trace:** `playwright-report/trace-TC-FR01-008.zip`

### Notes

The same behaviour was observed for TC-FR01-009 (all fields empty). In both cases the server correctly rejected the request with HTTP 422 but the frontend rendered no error feedback. Likely cause: the frontend error handler for `/api/register` does not parse or display the 422 response body.

## BUG-FR01-002

### Summary

| Field        | Value                                                                                                                                                                                                                                     |
| ------------ | ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| Bug ID       | BUG-FR01-002                                                                                                                                                                                                                              |
| Title        | Registration allows duplicate account when email is submitted in a different case                                                                                                                                                         |
| Feature      | FR-01 — Account Registration                                                                                                                                                                                                              |
| Root Cause   | The server-side uniqueness check for the email field is case-sensitive. Submitting `EXISTING@EXAMPLE.COM` when `existing@example.com` is already registered passes the uniqueness check and creates a duplicate account, violating BR-01. |
| Affects TCs  | TC-FR01-017                                                                                                                                                                                                                               |
| GitHub Issue | [Issue 2](/path/to/issue/2)                                                                                                                                                                                                               |

### Severity & Priority

| Field    | Value | Reason                                                                                                                                    |
| -------- | ----- | ----------------------------------------------------------------------------------------------------------------------------------------- |
| Severity | Major | Data integrity defect: duplicate user accounts can be created, leading to login ambiguity and potential access control issues.            |
| Priority | High  | Violates BR-01 (email must be unique) which is a core business rule; exploitable by any user with knowledge of an existing email address. |

### Environment

| Field       | Value                 |
| ----------- | --------------------- |
| Browser     | Chromium 124          |
| OS          | Windows 11            |
| Web URL     | http://localhost:5173 |
| Admin URL   | N/A                   |
| SUT Version | Not recorded          |

### Steps to Reproduce

1. Ensure an account with email `existing@example.com` exists in the system.
2. Navigate to `http://localhost:5173/register`.
3. Enter a valid Full Name (e.g. `Nguyen Van A`).
4. Enter `EXISTING@EXAMPLE.COM` (uppercase variant) as the Email.
5. Enter a valid Password (e.g. `ValidPass1`).
6. Enter the same value in Confirm Password.
7. Click the **Register** button.

**Observe:** Registration succeeds and the user is redirected to `/login` with a success message. A second account with the uppercase email variant now exists alongside the original lowercase account.

### Expected Result

System treats email comparison as case-insensitive and returns a field-level error: "This email address is already registered." No new account is created.

### Actual Result

Registration succeeded. System redirected to `/login` with message "Registration successful. Please verify your email." A duplicate account was created for `EXISTING@EXAMPLE.COM` alongside the existing `existing@example.com` account.

### Evidence

- **Screenshot:** `playwright-report/data/screenshot-TC-FR01-017-chromium.png`
- **Playwright Report:** `playwright-report/index.html`
- **Trace:** not available

### Notes

Not reproduced across additional browsers because the defect is server-side (the duplicate account is created regardless of client browser). Cross-browser testing for this bug is not necessary — the issue is in the API uniqueness constraint, not in the frontend.
