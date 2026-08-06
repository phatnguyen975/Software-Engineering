<div align="center">
  <h1>Bug Report — HW04 (Automation Testing)</h1>
  <small>
    <strong>Student:</strong> Nguyễn Tấn Phát — 23127449
  </small> <br />
  <sub>August 06, 2026</sub>
</div>

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

![BUG-FR01-001-01.png](../screenshots/fr-01/BUG-FR01-001-01.png)

- **Screenshot (Scenario B - Invalid Email):**

![BUG-FR01-001-02.png](../screenshots/fr-01/BUG-FR01-001-02.png)

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

![BUG-FR01-002.png](../screenshots/fr-01/BUG-FR01-002.png)

- **Playwright Report:** `playwright-report/fr-01/index.html`

### Notes

Both failing TCs relate to this single missing UI element:

- **TC-FR01-016** — Tests mismatched passwords; cannot proceed because the field to enter a different Confirm Password value does not exist.
- **TC-FR01-017** — Tests an empty Confirm Password; cannot proceed because the field does not exist.

This defect is independent of BUG-FR01-001. Even if the password validation regex is corrected, these two test cases will continue to fail until the Confirm Password field is added to the registration form.

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

![BUG-FR03-001-01.png](../screenshots/fr-03/BUG-FR03-001-01.png)
![BUG-FR03-001-02.png](../screenshots/fr-03/BUG-FR03-001-02.png)

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

![BUG-FR03-002.png](../screenshots/fr-03/BUG-FR03-002.png)

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

![BUG-FR03-003.png](../screenshots/fr-03/BUG-FR03-003.png)

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

![BUG-FR03-004.png](../screenshots/fr-03/BUG-FR03-004.png)

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

![BUG-FR03-005.png](../screenshots/fr-03/BUG-FR03-005.png)

- **Playwright Report:** `playwright-report/fr-03/index.html`

### Notes

TC-FR03-017 (leading whitespace) is a boundary case: the SRS states the system should either trim the whitespace and process the email or display a format error. The system instead sends the untrimmed value to the server and returns "User not found" — neither of the two acceptable outcomes. TC-FR03-019 (SQL injection) does not result in a system crash or data loss, but the fact that the payload reaches the server without format rejection is a concern. Both cases are covered under this single root cause (absent client-side format validation).

# Bug Report — FR-17: Coupon Management (CRUD)

> **Feature:** FR-17 — Coupon Management (CRUD)  
> **Test Case Source:** `docs/fr-17/fr-17-test-cases.md`  
> **Total Failing TCs:** 6  
> **Bugs Identified:** 4  
> **Generated:** 2026-08-05

## BUG-FR17-001

### Summary

| Field        | Value                                                                                                                                                                                                                                                                                              |
| ------------ | -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| Bug ID       | BUG-FR17-001                                                                                                                                                                                                                                                                                       |
| Title        | Coupon creation allows invalid `discount_value` without server-side rejection                                                                                                                                                                                                                      |
| Feature      | FR-17 — Coupon Management (CRUD)                                                                                                                                                                                                                                                                   |
| Root Cause   | The coupon creation API endpoint does not enforce the `discount_value` field constraints (must be > 0; must be ≤ 100 when `type` is `percent`). Requests with `discount_value = 0`, `-1`, or `101` (percent type) are accepted and persisted, allowing semantically invalid coupons to be created. |
| Affects TCs  | TC-FR17-010, TC-FR17-011, TC-FR17-012                                                                                                                                                                                                                                                              |
| GitHub Issue | [#32](https://github.com/phatnguyen975/eshop-sut/issues/32)                                                                                                                                                                                                                                        |

### Severity & Priority

| Field    | Value | Reason                                                                                                                                                                                                                            |
| -------- | ----- | --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| Severity | Major | Coupons with a zero, negative, or out-of-range percentage discount can be created and applied, resulting in incorrect price calculations for customers. The feature partially works but a key business rule is silently violated. |
| Priority | High  | `discount_value` validation is a core acceptance criterion (AC-06, AC-14) and must be enforced before any production release of the coupon feature.                                                                               |

### Environment

| Field       | Value                     |
| ----------- | ------------------------- |
| Browser     | Chromium, Firefox, WebKit |
| OS          | Windows 11                |
| Web URL     | N/A                       |
| Admin URL   | http://localhost:5174     |
| SUT Version | Not recorded              |

### Steps to Reproduce

**Variant A — discount_value = 0 (TC-FR17-010):**

1. Navigate to `http://localhost:5174` and log in as admin.
2. Navigate to the coupon management section.
3. Click "Mã Giảm Giá".
4. Fill in all required fields with valid values; set `discount_value` to `0`.
5. Submit the form.

**Observe:** The system accepts the submission, creates a new coupon with `discount_value = 0`, and displays it in the coupon list. No validation error is shown.

**Variant B — discount_value = -1 (TC-FR17-011):**

Steps 1–3 same as above; at step 4 set `discount_value` to `-1` and submit.

**Observe:** The system accepts the submission and persists a coupon with a negative discount value.

**Variant C — type = percent, discount_value = 101 (TC-FR17-012):**

Steps 1–3 same as above; at step 4 set `type` to `percent` and `discount_value` to `101`, then submit.

**Observe:** The system accepts the submission and persists a coupon where the percentage discount exceeds 100%.

### Expected Result

Form rejected. Validation error displayed on the `discount_value` field indicating it must be greater than zero (Variants A & B) / cannot exceed 100 for percent type (Variant C). No coupon is created.

### Actual Result

The system failed to reject the input and successfully created a coupon with a `discount_value` equal to zero (Variant A), a negative value (Variant B), or a value exceeding 100 (Variant C) across all 3 browsers.

### Evidence

- **Screenshot:**

![BUG-FR17-001.png](../screenshots/fr-17/BUG-FR17-001.png)

- **Playwright Report:** `playwright-report/fr-17/index.html`

### Notes

All three variants reproduce consistently across Chromium, Firefox, and WebKit. The defect is confined to server-side validation — the frontend form does not prevent submission of these invalid values, and the API silently accepts them. TC-FR17-003 (`discount_value = 0.01`) and TC-FR17-004 (`discount_value = 100`) both pass, confirming the boundary endpoints themselves are not the issue; only the rejection logic for out-of-range values is missing.

## BUG-FR17-002

### Summary

| Field        | Value                                                                                                                                                                                                                                                                                            |
| ------------ | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ |
| Bug ID       | BUG-FR17-002                                                                                                                                                                                                                                                                                     |
| Title        | Coupon creation accepts invalid `type` enum value without server-side rejection                                                                                                                                                                                                                  |
| Feature      | FR-17 — Coupon Management (CRUD)                                                                                                                                                                                                                                                                 |
| Root Cause   | The coupon creation API endpoint does not validate the `type` field against the allowed enum values (`percent`, `fixed`). When an invalid value such as `"flat"` is submitted directly (bypassing the UI dropdown), the API accepts and persists the record without returning an error response. |
| Affects TCs  | TC-FR17-009                                                                                                                                                                                                                                                                                      |
| GitHub Issue | [#33](https://github.com/phatnguyen975/eshop-sut/issues/33)                                                                                                                                                                                                                                      |

### Severity & Priority

| Field    | Value | Reason                                                                                                                                                                         |
| -------- | ----- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ |
| Severity | Major | A coupon with an unrecognised type value can be persisted in the database, which may cause runtime errors or undefined behaviour when the coupon is later applied at checkout. |
| Priority | High  | Enum validation at the API level is a mandatory guard against malformed data and is required by AC-05. Must be fixed before release.                                           |

### Environment

| Field       | Value                     |
| ----------- | ------------------------- |
| Browser     | Chromium, Firefox, WebKit |
| OS          | Windows 11                |
| Web URL     | N/A                       |
| Admin URL   | http://localhost:5174     |
| SUT Version | Not recorded              |

### Steps to Reproduce

1. Navigate to `http://localhost:5174` and log in as admin.
2. Navigate to the coupon management section.
3. Click "Mã Giảm Giá".
4. Using browser DevTools or a direct API call, set the `type` field to the value `"flat"` (bypassing the UI dropdown which restricts to valid options).
5. Fill all remaining fields with valid values.
6. Submit the form / send the API request.

   ```bash
   TOKEN=$(curl -s -X POST http://localhost:3000/api/login \
     -H "Content-Type: application/json" \
     -d '{"email": "admin@eshop.com", "password": "Admin123!"}' | grep -o '"token":"[^"]*' | cut -d'"' -f4)

   curl -i -X POST http://localhost:3000/api/admin/coupons \
     -H "Content-Type: application/json" \
     -H "Authorization: Bearer $TOKEN" \
     -d '{
       "code": "FLAT123",
       "type": "flat",
       "discount_value": 10,
       "expired_at": "2099-12-31",
       "min_order_amount": 0,
       "max_uses_per_user": 1
     }'
   ```

**Observe:** The system accepts the request, creates a coupon with `type = "flat"`, and displays it in the coupon list. No validation error is returned.

### Expected Result

Form rejected. Validation error displayed on the `type` field indicating only `"percent"` and `"fixed"` are accepted values. No coupon is created.

### Actual Result

The system failed to reject the input and successfully created a coupon with an invalid type value (`"flat"`) across all 3 browsers.

### Evidence

- **Screenshot:**

![BUG-FR17-002.png](../screenshots/fr-17/BUG-FR17-002.png)

- **Playwright Report:** `playwright-report/fr-17/index.html`

### Notes

The UI dropdown naturally prevents a user from selecting an invalid type in normal browser usage. This defect is exploitable via direct API access or form manipulation (e.g. DevTools, curl). Server-side enum validation is necessary regardless of UI constraints.

## BUG-FR17-003

### Summary

| Field        | Value                                                                                                                                                                                                                                          |
| ------------ | ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| Bug ID       | BUG-FR17-003                                                                                                                                                                                                                                   |
| Title        | Coupon creation allows negative `min_order_amount` without server-side rejection                                                                                                                                                               |
| Feature      | FR-17 — Coupon Management (CRUD)                                                                                                                                                                                                               |
| Root Cause   | The coupon creation API endpoint does not enforce the constraint that `min_order_amount` must be ≥ 0. A request with `min_order_amount = -1` is accepted and persisted, creating a coupon with a semantically invalid minimum order threshold. |
| Affects TCs  | TC-FR17-014                                                                                                                                                                                                                                    |
| GitHub Issue | [#34](https://github.com/phatnguyen975/eshop-sut/issues/34)                                                                                                                                                                                    |

### Severity & Priority

| Field    | Value  | Reason                                                                                                                                                                                        |
| -------- | ------ | --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| Severity | Major  | A coupon with a negative minimum order amount could be applicable to any order regardless of value, bypassing the intended minimum order restriction and causing unintended discounts.        |
| Priority | Medium | The defect affects an edge case (manually entering a negative value) rather than the primary creation flow. The impact is real but lower than `discount_value` or `type` validation failures. |

### Environment

| Field       | Value                     |
| ----------- | ------------------------- |
| Browser     | Chromium, Firefox, WebKit |
| OS          | Windows 11                |
| Web URL     | N/A                       |
| Admin URL   | http://localhost:5174     |
| SUT Version | Not recorded              |

### Steps to Reproduce

1. Navigate to `http://localhost:5174` and log in as admin.
2. Navigate to the coupon management section.
3. Click "Mã Giảm Giá".
4. Fill in all required fields with valid values; set `min_order_amount` to `-1`.
5. Submit the form.

**Observe:** The system accepts the submission, creates a new coupon with `min_order_amount = -1`, and displays it in the coupon list. No validation error is shown.

### Expected Result

Form rejected. Validation error displayed on the `min_order_amount` field indicating it must be ≥ 0. No coupon is created.

### Actual Result

The system failed to reject the input and successfully created a coupon with a negative `min_order_amount` across all 3 browsers.

### Evidence

- **Screenshot:**

![BUG-FR17-003.png](../screenshots/fr-17/BUG-FR17-003.png)

- **Playwright Report:** `playwright-report/fr-17/index.html`

### Notes

TC-FR17-002 (`min_order_amount = 0`) and the positive tests confirm the field itself works correctly for valid values. The defect is isolated to the absence of a lower-bound check (< 0) on the server side.

## BUG-FR17-004

### Summary

| Field        | Value                                                                                                                                                                                                                                                                                                                                 |
| ------------ | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| Bug ID       | BUG-FR17-004                                                                                                                                                                                                                                                                                                                          |
| Title        | Coupon creation persists `code` with leading whitespace without trimming or rejection                                                                                                                                                                                                                                                 |
| Feature      | FR-17 — Coupon Management (CRUD)                                                                                                                                                                                                                                                                                                      |
| Root Cause   | The coupon creation API endpoint does not sanitise the `code` field by trimming leading or trailing whitespace before persisting the value. A `code` submitted as `" NEWCODE"` (with one leading space) is stored as-is, creating a coupon that differs from `"NEWCODE"` in storage but is visually indistinguishable in the list UI. |
| Affects TCs  | TC-FR17-016                                                                                                                                                                                                                                                                                                                           |
| GitHub Issue | [#35](https://github.com/phatnguyen975/eshop-sut/issues/35)                                                                                                                                                                                                                                                                           |

### Severity & Priority

| Field    | Value | Reason                                                                                                                                                                                      |
| -------- | ----- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| Severity | Minor | The feature remains functional for typical admin usage; the defect only manifests when whitespace is deliberately or accidentally included in the code field. No data loss or crash occurs. |
| Priority | Low   | This is an edge-case input sanitisation issue that can be deferred to a future cycle without blocking core coupon creation or application flows.                                            |

### Environment

| Field       | Value                     |
| ----------- | ------------------------- |
| Browser     | Chromium, Firefox, WebKit |
| OS          | Windows 11                |
| Web URL     | N/A                       |
| Admin URL   | http://localhost:5174     |
| SUT Version | Not recorded              |

### Steps to Reproduce

1. Navigate to `http://localhost:5174` and log in as admin.
2. Navigate to the coupon management section.
3. Click "Mã Giảm Giá".
4. In the `code` field, enter `" NEWCODE"` (a single leading space followed by "NEWCODE").
5. Fill all remaining fields with valid values.
6. Submit the form.

**Observe:** The system accepts the submission and displays a coupon in the list. The persisted code includes the leading space character, which is not trimmed.

### Expected Result

System either trims the whitespace and creates coupon with code `"NEWCODE"`, or rejects the input with a validation error. A coupon with a space-prefixed code must not be persisted.

### Actual Result

The system successfully created and persisted a coupon with a code containing leading whitespace across all 3 browsers.

### Evidence

- **Screenshot:**

![BUG-FR17-004.png](../screenshots/fr-17/BUG-FR17-004.png)

- **Playwright Report:** `playwright-report/fr-17/index.html`

### Notes

The two acceptable resolutions are: (1) server trims whitespace silently and stores `"NEWCODE"`, or (2) server rejects the input with a validation error. The current behaviour — persisting the space-prefixed value — is incorrect under either interpretation. A whitespace-only `code` should also be rejected, though that case was not explicitly tested in this suite.
