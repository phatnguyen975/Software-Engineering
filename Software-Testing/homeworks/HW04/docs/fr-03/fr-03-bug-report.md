# Bug Report — FR-03: Forgot Password & Password Reset

> **Feature:** FR-03 — Forgot Password & Password Reset  
> **Test Case Source:** `docs/fr-03/fr-03-test-cases.md`  
> **Total Failing TCs:** 13  
> **Bugs Identified:** 5  
> **Generated:** 2026-08-06

## BUG-FR03-001

### Summary

| Field        | Value                                                                                                                                                                                                                                                                                                             |
| ------------ | ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| Bug ID       | BUG-FR03-001                                                                                                                                                                                                                                                                                                      |
| Title        | Password reset rejects policy-compliant passwords with "password too weak" error                                                                                                                                                                                                                                  |
| Feature      | FR-03 — Forgot Password & Password Reset                                                                                                                                                                                                                                                                          |
| Root Cause   | The password policy validation rejects passwords that satisfy all documented constraints (≥8 chars, ≥1 uppercase, ≥1 lowercase, ≥1 digit, ≥1 special char from `@$!%*?&`). The endpoint returns an error indicating a weak password for inputs that should be accepted, preventing any successful password reset. |
| Affects TCs  | TC-FR03-001, TC-FR03-003, TC-FR03-004                                                                                                                                                                                                                                                                             |
| GitHub Issue | [#36](https://github.com/phatnguyen975/eshop-sut/issues/36)                                                                                                                                                                                                                                                       |

### Severity & Priority

| Field    | Value    | Reason                                                                                                                                      |
| -------- | -------- | ------------------------------------------------------------------------------------------------------------------------------------------- |
| Severity | Critical | The core password reset flow is completely broken for all tested policy-compliant passwords; no user can successfully reset their password. |
| Priority | High     | This defect blocks the entire FR-03 happy path and must be resolved before the feature can be released.                                     |

### Environment

| Field       | Value                     |
| ----------- | ------------------------- |
| Browser     | Chromium, Firefox, WebKit |
| OS          | Windows 11                |
| Web URL     | http://localhost:5173     |
| Admin URL   | N/A                       |
| SUT Version | Not recorded              |

### Steps to Reproduce

1. Navigate to `http://localhost:5173/forgot-password`.
2. Enter a registered email address (e.g. `user001@eshop.com`) and click Submit.
3. Note the OTP displayed on screen.
4. Enter the correct OTP in the OTP field.
5. Enter a policy-compliant password (e.g. `NewPass1!` — 9 chars, uppercase, lowercase, digit, special char).
6. Enter the same value in the Confirm New Password field.
7. Click Submit.

**Observe:** The system displays an error message "Mật khẩu quá yếu! Phải dài tối thiểu 8 ký tự, gồm chữ hoa, chữ thường, số và KÝ TỰ ĐẶC BIỆT." and does not redirect to the Login page. Password is not reset.

### Expected Result

System resets the password and redirects the user to the Login page.

### Actual Result

System displayed "Mật khẩu quá yếu! Phải dài tối thiểu 8 ký tự, gồm chữ hoa, chữ thường, số và KÝ TỰ ĐẶC BIỆT." and did not redirect to Login. The same error appears for all tested compliant passwords including the 8-character boundary case (`NewPass1!`) and the 9-character case (`NewPass1!c`).

### Evidence

- **Screenshot:**

![BUG-FR03-001-01.png](../../screenshots/fr-03/BUG-FR03-001-01.png)
![BUG-FR03-001-02.png](../../screenshots/fr-03/BUG-FR03-001-02.png)

- **Playwright Report:** `playwright-report/fr-03/index.html`

### Notes

All three affected TCs use passwords that satisfy every constraint listed in BR-05. The error message appears consistently across Chromium, Firefox, and WebKit, indicating this is a backend validation defect rather than a browser-specific rendering issue. The server-side policy enforcer may be applying undocumented constraints not reflected in the SRS.

## BUG-FR03-002

### Summary

| Field        | Value                                                                                                                                                                                                  |
| ------------ | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ |
| Bug ID       | BUG-FR03-002                                                                                                                                                                                           |
| Title        | System generates 4-digit OTP instead of the required 6-digit OTP                                                                                                                                       |
| Feature      | FR-03 — Forgot Password & Password Reset                                                                                                                                                               |
| Root Cause   | The OTP generation logic produces a 4-digit token instead of the 6-digit token required by BR-01 and BR-07. This violates the OTP length constraint at the point of generation, before any user input. |
| Affects TCs  | TC-FR03-002                                                                                                                                                                                            |
| GitHub Issue | [#37](https://github.com/phatnguyen975/eshop-sut/issues/37)                                                                                                                                            |

### Severity & Priority

| Field    | Value | Reason                                                                                                                              |
| -------- | ----- | ----------------------------------------------------------------------------------------------------------------------------------- |
| Severity | Major | OTP generation is functional but produces tokens of the wrong length, violating BR-01 and BR-07; users receive a non-compliant OTP. |
| Priority | High  | An incorrectly-lengthed OTP undermines the security guarantee of the feature and must be corrected before release.                  |

### Environment

| Field       | Value                     |
| ----------- | ------------------------- |
| Browser     | Chromium, Firefox, WebKit |
| OS          | Windows 11                |
| Web URL     | http://localhost:5173     |
| Admin URL   | N/A                       |
| SUT Version | Not recorded              |

### Steps to Reproduce

1. Navigate to `http://localhost:5173/forgot-password`.
2. Enter a registered email address (e.g. `user002@eshop.com`) and click Submit.
3. Observe the OTP code displayed on screen.

**Observe:** The displayed OTP contains exactly 4 digits instead of the required 6 digits.

### Expected Result

System generates and displays a 6-digit numeric OTP (BR-01, BR-07).

### Actual Result

System generated a 4-digit OTP instead of the expected 6-digit OTP.

### Evidence

- **Screenshot:**

![BUG-FR03-002.png](../../screenshots/fr-03/BUG-FR03-002.png)

- **Playwright Report:** `playwright-report/fr-03/index.html`

### Notes

This defect is detectable at the point of OTP display after Step 1 submission, independently of any Step 2 interaction. The OTP length constraint is defined in BR-01 and BR-07.

## BUG-FR03-003

### Summary

| Field        | Value                                                                                                                                                                                                                                                                                                                                                      |
| ------------ | ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| Bug ID       | BUG-FR03-003                                                                                                                                                                                                                                                                                                                                               |
| Title        | Step 2 submission validates password policy before OTP, masking OTP rejection errors                                                                                                                                                                                                                                                                       |
| Feature      | FR-03 — Forgot Password & Password Reset                                                                                                                                                                                                                                                                                                                   |
| Root Cause   | The processes Step 2 submission by evaluating the New Password field before validating the OTP. As a result, when an invalid OTP is submitted alongside a password the server considers weak, the password validation error is returned instead of the OTP rejection error, masking the OTP defect entirely and providing misleading feedback to the user. |
| Affects TCs  | TC-FR03-009, TC-FR03-013, TC-FR03-014, TC-FR03-018                                                                                                                                                                                                                                                                                                         |
| GitHub Issue | [#38](https://github.com/phatnguyen975/eshop-sut/issues/38)                                                                                                                                                                                                                                                                                                |

### Severity & Priority

| Field    | Value | Reason                                                                                                                                                                         |
| -------- | ----- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ |
| Severity | Major | OTP validation (BR-07) — the core security check of the password reset flow — is bypassed by an incorrect processing order; the security constraint is not enforced correctly. |
| Priority | High  | Incorrect OTP validation order undermines the security of the password reset flow and must be fixed before release.                                                            |

### Environment

| Field       | Value                     |
| ----------- | ------------------------- |
| Browser     | Chromium, Firefox, WebKit |
| OS          | Windows 11                |
| Web URL     | http://localhost:5173     |
| Admin URL   | N/A                       |
| SUT Version | Not recorded              |

### Steps to Reproduce

1. Navigate to `http://localhost:5173/forgot-password`.
2. Enter a registered email address (e.g. `user009@eshop.com`) and click Submit.
3. Note the OTP displayed on screen.
4. Enter an **incorrect** 6-digit OTP (e.g. `000000`) that does not match the generated one.
5. Enter a policy-compliant new password (e.g. `NewPass1!`).
6. Enter the same value in the Confirm New Password field.
7. Click Submit.

**Observe:** The system displays "Mật khẩu quá yếu! Phải dài tối thiểu 8 ký tự, gồm chữ hoa, chữ thường, số và KÝ TỰ ĐẶC BIỆT." instead of an OTP validation error.

### Expected Result

System displays an error message indicating the OTP is invalid. Password is not reset. User remains on Step 2.

### Actual Result

System displayed "Mật khẩu quá yếu! Phải dài tối thiểu 8 ký tự, gồm chữ hoa, chữ thường, số và KÝ TỰ ĐẶC BIỆT." instead of indicating the OTP is invalid. This occurs regardless of whether the OTP is wrong (TC-FR03-009), contains non-numeric characters (TC-FR03-013), exceeds 6 digits (TC-FR03-014), or belongs to a different email's session (TC-FR03-018).

### Evidence

- **Screenshot:**

![BUG-FR03-003.png](../../screenshots/fr-03/BUG-FR03-003.png)

- **Playwright Report:** `playwright-report/fr-03/index.html`

### Notes

This defect is compounded by BUG-FR03-001: because the backend rejects all tested passwords as "too weak," the OTP validation error is never surfaced regardless of OTP validity. The correct fix requires both resolving BUG-FR03-001 and enforcing OTP validation ahead of password validation in the request processing pipeline.

## BUG-FR03-004

### Summary

| Field        | Value                                                                                                                                                                                            |
| ------------ | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ |
| Bug ID       | BUG-FR03-004                                                                                                                                                                                     |
| Title        | Confirm New Password field is missing from Step 2 of the Forgot Password form                                                                                                                    |
| Feature      | FR-03 — Forgot Password & Password Reset                                                                                                                                                         |
| Root Cause   | The Step 2 form does not render a Confirm New Password input field. The field required by BR-06 is entirely absent from the UI, making it impossible for the user to confirm their new password. |
| Affects TCs  | TC-FR03-011                                                                                                                                                                                      |
| GitHub Issue | [#39](https://github.com/phatnguyen975/eshop-sut/issues/39)                                                                                                                                      |

### Severity & Priority

| Field    | Value | Reason                                                                                                                                  |
| -------- | ----- | --------------------------------------------------------------------------------------------------------------------------------------- |
| Severity | Major | A required form field (BR-06) is entirely absent; users cannot confirm their password, and the password mismatch check cannot function. |
| Priority | High  | This is a missing UI component for a mandatory business rule; it must be implemented before the feature can be released.                |

### Environment

| Field       | Value                     |
| ----------- | ------------------------- |
| Browser     | Chromium, Firefox, WebKit |
| OS          | Windows 11                |
| Web URL     | http://localhost:5173     |
| Admin URL   | N/A                       |
| SUT Version | Not recorded              |

### Steps to Reproduce

1. Navigate to `http://localhost:5173/forgot-password`.
2. Enter a registered email address and click Submit.
3. Wait for Step 2 to appear.
4. Inspect the Step 2 form for the presence of a "Confirm New Password" input field.

**Observe:** No "Confirm New Password" field is present in the Step 2 form.

### Expected Result

Step 2 displays a Confirm New Password field. System displays an inline error indicating passwords do not match when a mismatched value is submitted (BR-06).

### Actual Result

The UI is missing a 'Confirm New Password' field entirely.

### Evidence

- **Screenshot:**

![BUG-FR03-004.png](../../screenshots/fr-03/BUG-FR03-004.png)

- **Playwright Report:** `playwright-report/fr-03/index.html`

### Notes

The absence of this field means that BR-06 (password confirmation must match new password) is completely untestable and unenforced in the current build. TC-FR03-011 is the only TC directly covering this element, but its absence also affects all other Step 2 TCs that reference the Confirm New Password field.

## BUG-FR03-005

### Summary

| Field        | Value                                                                                                                                                                                                                                                                                                                                            |
| ------------ | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ |
| Bug ID       | BUG-FR03-005                                                                                                                                                                                                                                                                                                                                     |
| Title        | Step 1 email input does not validate format client-side; malformed inputs are forwarded to the server                                                                                                                                                                                                                                            |
| Feature      | FR-03 — Forgot Password & Password Reset                                                                                                                                                                                                                                                                                                         |
| Root Cause   | The Step 1 form does not perform client-side email format validation before submitting. Inputs that violate RFC 5321 format (missing `@`, missing domain, leading whitespace, SQL injection payload) bypass format checks and are forwarded to the server, which returns a generic "User not found" error rather than a format validation error. |
| Affects TCs  | TC-FR03-006, TC-FR03-007, TC-FR03-017, TC-FR03-019                                                                                                                                                                                                                                                                                               |
| GitHub Issue | [#40](https://github.com/phatnguyen975/eshop-sut/issues/40)                                                                                                                                                                                                                                                                                      |

### Severity & Priority

| Field    | Value  | Reason                                                                                                                                               |
| -------- | ------ | ---------------------------------------------------------------------------------------------------------------------------------------------------- |
| Severity | Minor  | The feature does not crash and a server-side error is displayed; however, the error message is incorrect and the expected format feedback is absent. |
| Priority | Medium | Poor validation feedback degrades UX and may expose the system to injection payloads; should be resolved in the current cycle.                       |

### Environment

| Field       | Value                     |
| ----------- | ------------------------- |
| Browser     | Chromium, Firefox, WebKit |
| OS          | Windows 11                |
| Web URL     | http://localhost:5173     |
| Admin URL   | N/A                       |
| SUT Version | Not recorded              |

### Steps to Reproduce

1. Navigate to `http://localhost:5173/forgot-password`.
2. Enter a malformed email address in the Email field (e.g. `testemaileshop.com` — missing `@`).
3. Click Submit.

**Observe:** The system displays "Lỗi: User not found" instead of an email format validation error.

### Expected Result

System displays a format validation error on the Email field. No OTP is generated. User remains on Step 1.

### Actual Result

System displayed "Lỗi: User not found" instead of an email format validation error. The malformed input was forwarded to the server, which looked up the value as-is and returned a not-found error. This behaviour is consistent across TC-FR03-006 (missing `@`), TC-FR03-007 (missing domain), TC-FR03-017 (leading whitespace), and TC-FR03-019 (SQL injection payload).

### Evidence

- **Screenshot:**

![BUG-FR03-005.png](../../screenshots/fr-03/BUG-FR03-005.png)

- **Playwright Report:** `playwright-report/fr-03/index.html`

### Notes

TC-FR03-017 (leading whitespace) is a boundary case: the SRS states the system should either trim the whitespace and process the email or display a format error. The system instead sends the untrimmed value to the server and returns "User not found" — neither of the two acceptable outcomes. TC-FR03-019 (SQL injection) does not result in a system crash or data loss, but the fact that the payload reaches the server without format rejection is a concern. Both cases are covered under this single root cause (absent client-side format validation).
