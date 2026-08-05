# Bug Report — FR-01: Account Registration

> **Feature:** FR-01 — Account Registration  
> **Test Case Source:** `docs/fr-01/fr-01-test-cases.md`  
> **Total Failing TCs:** 9  
> **Bugs Identified:** 2  
> **Generated:** 2026-08-04

## BUG-FR01-001

### Summary

| Field        | Value                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                 |
| ------------ | --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| Bug ID       | BUG-FR01-001                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                          |
| Title        | Registration form rejects SRS-compliant passwords and displays a password strength error when submitting valid or invalid email inputs                                                                                                                                                                                                                                                                                                                                                                                |
| Feature      | FR-01 — Account Registration                                                                                                                                                                                                                                                                                                                                                                                                                                                                                          |
| Root Cause   | The client-side password validation regex is more restrictive than the SRS specification. A password that satisfies all SRS constraints (≥8 characters, ≥1 uppercase, ≥1 lowercase, ≥1 digit, ≥1 special character from `@$!%*?&`) is still rejected by the frontend validator with the error "Mật khẩu quá yếu!". As a result, any form submission using an SRS-compliant password triggers the password error before other field-level validations (email format, duplicate email) are ever evaluated or displayed. |
| Affects TCs  | TC-FR01-001, TC-FR01-002, TC-FR01-005, TC-FR01-006, TC-FR01-007, TC-FR01-008, TC-FR01-018                                                                                                                                                                                                                                                                                                                                                                                                                             |
| GitHub Issue | [#30](https://github.com/phatnguyen975/eshop-sut/issues/30)                                                                                                                                                                                                                                                                                                                                                                                                                                                           |

### Severity & Priority

| Field    | Value | Reason                                                                                                                                                                                                                                          |
| -------- | ----- | ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| Severity | Major | The core registration flow is broken: no account can be created using an SRS-compliant password. Additionally, all email-field validation paths are masked, making it impossible to verify or use email-related error messages.                 |
| Priority | High  | This defect blocks the primary success scenario of FR-01 (account creation) and prevents verification of five other functional requirements. It must be fixed before any meaningful regression testing of the registration feature can proceed. |

### Environment

| Field       | Value                                         |
| ----------- | --------------------------------------------- |
| Browser     | Chromium, Firefox, WebKit — reproduced on all |
| OS          | Windows 11                                    |
| Web URL     | http://localhost:5173                         |
| Admin URL   | N/A                                           |
| SUT Version | Not recorded                                  |

### Steps to Reproduce

**Scenario A: Registration with valid input (e.g. TC-FR01-001)**

1. Navigate to the registration page at `http://localhost:5173/register`.
2. Enter a valid Full Name (e.g. `Test User`).
3. Enter a unique, properly formatted Email (e.g. `newuser@example.com`).
4. Enter a policy-compliant Password (e.g. `Test1234!`).
5. Click the **Register** button.

**Observe:** The system does not create an account. Instead, it displays the password strength error message "Mật khẩu quá yếu!..." despite the password being valid.

**Scenario B: Registration with invalid email format (e.g. TC-FR01-006)**

1. Navigate to the registration page at `http://localhost:5173/register`.
2. Enter a valid Full Name.
3. Enter an email missing the `@` symbol (e.g. `invalid_email.com`).
4. Enter a policy-compliant Password (e.g. `Test1234!`).
5. Click the **Register** button.

**Observe:** The system does not show an email format error. Instead, it prioritises and displays the password strength error message "Mật khẩu quá yếu!...".

### Expected Result

- **For Scenario A:** System creates the account and redirects the user to the Login page (`/login`).
- **For Scenario B:** System does not create an account. An error message is displayed on the Email field indicating the email format is invalid.

### Actual Result

In both scenarios, the system incorrectly displays the password validation error "Mật khẩu quá yếu!..." and halts the process.

- In Scenario A, it wrongly rejects a valid password.
- In Scenario B, the strict password validation triggers first, suppressing and masking the email validation error that should have been displayed.

### Evidence

- **Screenshot (Scenario A - Valid Input):**

![BUG-FR01-001-01.png](../../screenshots/fr-01/BUG-FR01-001-01.png)

- **Screenshot (Scenario B - Invalid Email):**

![BUG-FR01-001-02.png](../../screenshots/fr-01/BUG-FR01-001-02.png)

- **Playwright Report:** `playwright-report/fr-01/index.html`

### Notes

This single root cause is responsible for 7 of the 9 total failing test cases:

- **TC-FR01-001, TC-FR01-002** — Registration with fully valid input fails because the valid password is incorrectly rejected.
- **TC-FR01-005** — Test for duplicate email cannot reach the server-side uniqueness check; the password error fires first.
- **TC-FR01-006, TC-FR01-007, TC-FR01-008** — Tests for malformed email formats cannot surface email-field errors; the password error takes priority.
- **TC-FR01-018** — Test for leading whitespace in the email field cannot surface the email format error; the password error fires first.

Once the password validation regex is corrected to match the SRS policy exactly, the email-field validation errors should surface correctly in TC-FR01-005 through TC-FR01-008 and TC-FR01-018 — however, those cases should be re-run after the fix to confirm.

## BUG-FR01-002

### Summary

| Field        | Value                                                                                                                                                                                                                                                                                              |
| ------------ | -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| Bug ID       | BUG-FR01-002                                                                                                                                                                                                                                                                                       |
| Title        | Registration form is missing the Confirm Password field, preventing password confirmation testing                                                                                                                                                                                                  |
| Feature      | FR-01 — Account Registration                                                                                                                                                                                                                                                                       |
| Root Cause   | The "Confirm Password" (`Xác nhận mật khẩu`) input field is not rendered in the registration form UI. The SRS (BR-04) requires this field to enforce password confirmation before account creation; its absence means the frontend implementation is incomplete with respect to the specification. |
| Affects TCs  | TC-FR01-016, TC-FR01-017                                                                                                                                                                                                                                                                           |
| GitHub Issue | [#31](https://github.com/phatnguyen975/eshop-sut/issues/31)                                                                                                                                                                                                                                        |

### Severity & Priority

| Field    | Value | Reason                                                                                                                                                                                                      |
| -------- | ----- | ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| Severity | Major | A required UI component defined in the SRS is entirely absent. The password confirmation requirement (BR-04) cannot be enforced or tested, leaving users with no safeguard against password entry mistakes. |
| Priority | High  | The missing field is a functional requirement of the registration form (BR-04). Without it, the system does not conform to the specification and the affected acceptance criteria cannot be met.            |

### Environment

| Field       | Value                                         |
| ----------- | --------------------------------------------- |
| Browser     | Chromium, Firefox, WebKit — reproduced on all |
| OS          | Windows 11                                    |
| Web URL     | http://localhost:5173                         |
| Admin URL   | N/A                                           |
| SUT Version | Not recorded                                  |

### Steps to Reproduce

1. Navigate to the registration page at `http://localhost:5173/register`.
2. Inspect the visible form fields on the page.

**Observe:** The registration form displays only three input fields — Full Name, Email, and Password. There is no "Confirm Password" (`Xác nhận mật khẩu`) field present in the form. It is not hidden, collapsed, or conditionally rendered; it is absent entirely.

### Expected Result

System does not create an account. An error message is displayed on the Confirm Password field indicating the passwords do not match (TC-FR01-016), or indicating that the Confirm Password field is required (TC-FR01-017).

### Actual Result

The Confirm Password field is missing from the UI, preventing value entry. The test automation cannot interact with a non-existent element; both TC-FR01-016 and TC-FR01-017 fail immediately because no locator for this field can be resolved.

### Evidence

- **Screenshot:**

![BUG-FR01-002.png](../../screenshots/fr-01/BUG-FR01-002.png)

- **Playwright Report:** `playwright-report/fr-01/index.html`

### Notes

Both failing TCs relate to this single missing UI element:

- **TC-FR01-016** — Tests mismatched passwords; cannot proceed because the field to enter a different Confirm Password value does not exist.
- **TC-FR01-017** — Tests an empty Confirm Password; cannot proceed because the field does not exist.

This defect is independent of BUG-FR01-001. Even if the password validation regex is corrected, these two test cases will continue to fail until the Confirm Password field is added to the registration form.
