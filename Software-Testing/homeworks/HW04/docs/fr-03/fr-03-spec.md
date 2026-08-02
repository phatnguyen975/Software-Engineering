# Feature Specification — FR-03: Forgot Password & Password Reset

> **Source SRS:** `docs/system-requirements-specification.md`  
> **Generated:** 2026-08-02  
> **Status:** APPROVED

## 1. Feature Overview

| Field                   | Value                                                                                               |
| ----------------------- | --------------------------------------------------------------------------------------------------- |
| FR ID                   | FR-03                                                                                               |
| Feature Name            | Forgot Password & Password Reset (2 Steps)                                                          |
| Primary Actor           | Guest User (unauthenticated user who has a registered account but cannot remember the password)     |
| Secondary Actors        | Email Delivery Service (delivers OTP in production; in demo environment OTP is displayed on-screen) |
| Authentication Required | No                                                                                                  |
| Entry Point             | Not specified in SRS (implied: a "Forgot Password" link on the login page)                          |
| Actor Goal              | Reset a forgotten password by verifying identity via a one-time OTP code and setting a new password |

## 2. Input Fields & Constraints

### Step 1 — Request OTP

| Field Name | Data Type | Required | Min | Max | Format / Pattern                                 | Allowed Values | Notes                                                             |
| ---------- | --------- | -------- | --- | --- | ------------------------------------------------ | -------------- | ----------------------------------------------------------------- |
| Email      | email     | Yes      | —   | —   | RFC 5321 compliant; must contain exactly one `@` | —              | Must be an email address that is already registered in the system |

### Step 2 — Reset Password

| Field Name           | Data Type | Required | Min | Max | Format / Pattern                                                                                      | Allowed Values | Notes                                                                      |
| -------------------- | --------- | -------- | --- | --- | ----------------------------------------------------------------------------------------------------- | -------------- | -------------------------------------------------------------------------- |
| OTP                  | string    | Yes      | 6   | 6   | Exactly 6 numeric digits                                                                              | —              | Must match the OTP generated for the email that requested it (scope-bound) |
| New Password         | password  | Yes      | 8   | —   | Must contain ≥1 uppercase letter, ≥1 lowercase letter, ≥1 digit, and ≥1 special character (`@$!%*?&`) | —              | Same password policy as FR-01                                              |
| Confirm New Password | password  | Yes      | —   | —   | Must match the New Password field value exactly                                                       | —              | Not persisted; used only for client-side validation                        |

## 3. Business Rules

**BR-01:** The system must generate a random 6-digit OTP code when a registered email address is submitted in Step 1.

> **Source:** SRS §FR-03, Step 1 — "Hệ thống sinh mã OTP 6 chữ số ngẫu nhiên"

**BR-02:** In the demo environment, the generated OTP must be displayed directly on the screen (instead of being sent via email).

> **Source:** SRS §FR-03, Step 1 — "trong môi trường demo: hiển thị trực tiếp trên màn hình"

**BR-03:** The user interface must display a Step Indicator showing the current step (e.g., "Step 1 / 2") throughout the flow.

> **Source:** SRS §FR-03, Step 1 — "Giao diện phải hiển thị chỉ báo bước (Step Indicator) — ví dụ: 'Bước 1 / 2'"

**BR-04:** A "Back to Login" button must be available to the user during the flow.

> **Source:** SRS §FR-03, Step 1 — "Có nút Quay lại đăng nhập"

**BR-05:** The new password must comply with the strong password policy defined in FR-01: minimum 8 characters, at least 1 uppercase letter, 1 lowercase letter, 1 digit, and 1 special character (`@`, `$`, `!`, `%`, `*`, `?`, `&`).

> **Source:** SRS §FR-03, Step 2 — "Mật khẩu mới phải tuân thủ điều kiện như FR-01"; SRS §FR-01 — password policy

**BR-06:** The Confirm New Password field value must be identical to the New Password field value.

> **Source:** SRS §FR-03, Step 2 — "Hai trường mật khẩu phải khớp nhau"

**BR-07:** An OTP is valid only for the email address that requested it; it cannot be used for a different email address.

> **Source:** SRS §FR-03, Step 2 — "OTP chỉ hợp lệ cho email đã yêu cầu, không thể dùng cho email khác"

**BR-08:** The email submitted in Step 1 must already be registered in the system.

> **Source:** SRS §FR-03, Step 1 — "Người dùng nhập địa chỉ Email đã đăng ký" (implies the system must validate whether the email exists)

**BR-09:** If the user returns to Step 1 and resubmits their email, the system must generate a new OTP.

> **Source:** Confirmed via stakeholder feedback (replaces unspecified behaviour).

## 4. Success Paths

### SP-01: Complete Forgot Password Flow (Step 1 → Step 2)

```
Actor:   Navigates to the Forgot Password page (from the Login page)
System:  Renders Step 1 form with an Email field; displays Step Indicator showing "Step 1 / 2"; shows a "Back to Login" button
Actor:   Enters a valid, registered email address
Actor:   Submits the Step 1 form
System:  Validates the email is registered in the system (BR-08)
System:  Generates a random 6-digit OTP (BR-01)
System:  Displays the OTP on screen (BR-02, demo environment)
System:  Transitions the UI to Step 2; updates the Step Indicator to "Step 2 / 2" (BR-03)
Actor:   Enters the correct OTP in the OTP field
Actor:   Enters a new password that satisfies the strong password policy (BR-05)
Actor:   Enters the same password in the Confirm New Password field (BR-06)
Actor:   Submits the Step 2 form
System:  Validates the OTP matches the one generated for the requesting email (BR-07)
System:  Validates the new password against the password policy (BR-05)
System:  Validates the two password fields match (BR-06)
System:  Updates the user's password in the database
System:  Redirects the actor to the Login page
Outcome: The user's password is successfully reset; the actor is on the Login page
```

## 5. Failure Paths

### FP-01: Unregistered Email in Step 1

```
Trigger: The email address submitted in Step 1 is not registered in the system (BR-08)
Actor:   Enters an unregistered email address and submits the Step 1 form
System:  Validates the email against the user accounts table
System:  Displays an error message indicating the email is not found or not registered
Outcome: No OTP is generated; the actor remains on Step 1
```

### FP-02: Invalid Email Format in Step 1

```
Trigger: The email address submitted in Step 1 does not conform to a valid email format
Actor:   Enters a malformed email address (e.g., missing @, missing domain) and submits
System:  Rejects the submission due to format validation
System:  Displays a format validation error on the Email field
Outcome: No OTP is generated; the actor remains on Step 1
```

### FP-03: Empty Email in Step 1

```
Trigger: The Email field is left blank on submission
Actor:   Submits the Step 1 form without entering an email
System:  Displays a required-field error on the Email field
Outcome: No OTP is generated; the actor remains on Step 1
```

### FP-04: Incorrect OTP in Step 2

```
Trigger: The OTP entered in Step 2 does not match the generated OTP (BR-07)
Actor:   Enters an incorrect or random OTP and submits the Step 2 form
System:  Validates the OTP against the stored value
System:  Displays an error message indicating the OTP is invalid
Outcome: Password is not reset; the actor remains on Step 2
```

### FP-05: OTP Used for a Different Email (Scope Violation)

```
Trigger: The actor attempts to use an OTP that was generated for a different email address (BR-07)
Actor:   Somehow obtains or guesses an OTP issued to another email and submits it
System:  Validates the OTP against the requesting email's OTP record
System:  Rejects the OTP as invalid
Outcome: Password is not reset; the actor sees an error message
```

### FP-06: New Password Does Not Meet Policy

```
Trigger: The new password entered in Step 2 does not satisfy the strong password policy (BR-05)
Actor:   Enters a password that is too short, or lacks required character types, and submits
System:  Validates the password against the policy rules
System:  Displays an inline error describing the unmet password requirement
Outcome: Password is not reset; the actor remains on Step 2
```

### FP-07: New Password and Confirm Password Do Not Match

```
Trigger: The Confirm New Password value differs from the New Password value (BR-06)
Actor:   Enters mismatched values in the two password fields and submits
System:  Detects the mismatch
System:  Displays an inline error indicating the passwords do not match
Outcome: Password is not reset; the actor remains on Step 2
```

### FP-08: Empty Required Fields in Step 2

```
Trigger: One or more required fields (OTP, New Password, Confirm New Password) are left blank in Step 2
Actor:   Submits the Step 2 form with one or more empty required fields
System:  Validates presence of all required fields
System:  Displays required-field errors adjacent to each empty field
Outcome: Password is not reset; the actor remains on Step 2
```

## 6. Acceptance Criteria

### AC-01: Successful OTP generation for registered email

```
Given  a guest user is on the Forgot Password page (Step 1)
When   the user enters a valid, registered email address and submits the form
Then   the system generates a 6-digit OTP and displays it on screen (demo mode)
```

_Maps to: BR-01, BR-02, BR-08, SP-01_

### AC-02: Step Indicator is displayed throughout the flow

```
Given  a guest user is on the Forgot Password page
When   the page loads (Step 1) and when the user proceeds to Step 2
Then   a Step Indicator is visible showing the current step number (e.g., "Step 1 / 2" then "Step 2 / 2")
```

_Maps to: BR-03, SP-01_

### AC-03: Back to Login button is available

```
Given  a guest user is on the Forgot Password page (Step 1)
When   the page is displayed
Then   a "Back to Login" button is visible and navigates back to the login page when clicked
```

_Maps to: BR-04_

### AC-04: Successful password reset with valid OTP and compliant password

```
Given  a guest user has received a valid OTP for their registered email
When   the user enters the correct OTP, a policy-compliant new password, and matching confirmation password, then submits the Step 2 form
Then   the system resets the password and redirects the user to the Login page
```

_Maps to: BR-05, BR-06, BR-07, SP-01_

### AC-05: Unregistered email is rejected in Step 1

```
Given  a guest user is on the Forgot Password page (Step 1)
When   the user enters an email address that is not registered in the system and submits
Then   the system displays an error message and does not generate an OTP
```

_Maps to: BR-08, FP-01_

### AC-06: Invalid email format is rejected in Step 1

```
Given  a guest user is on the Forgot Password page (Step 1)
When   the user enters a malformed email address and submits
Then   the system displays a validation error on the Email field and does not proceed to Step 2
```

_Maps to: FP-02_

### AC-07: Empty email is rejected in Step 1

```
Given  a guest user is on the Forgot Password page (Step 1)
When   the user submits the form without entering any email
Then   the system displays a required-field error and does not proceed
```

_Maps to: FP-03_

### AC-08: Incorrect OTP is rejected in Step 2

```
Given  a guest user is on the Reset Password page (Step 2)
When   the user enters an OTP that does not match the generated OTP and submits
Then   the system displays an error indicating the OTP is invalid and does not reset the password
```

_Maps to: BR-07, FP-04_

### AC-09: OTP scoped to requesting email cannot be used for another email

```
Given  an OTP was generated for email A
When   the actor attempts to use that OTP in a password reset flow initiated for email B
Then   the system rejects the OTP as invalid
```

_Maps to: BR-07, FP-05_

### AC-10: Non-compliant new password is rejected in Step 2

```
Given  a guest user is on the Reset Password page (Step 2) with a valid OTP
When   the user enters a new password that does not meet the strong password policy and submits
Then   the system displays an inline error describing the unmet requirement and does not reset the password
```

_Maps to: BR-05, FP-06_

### AC-11: Mismatched password confirmation is rejected in Step 2

```
Given  a guest user is on the Reset Password page (Step 2) with a valid OTP
When   the user enters different values in New Password and Confirm New Password fields and submits
Then   the system displays an inline error indicating the passwords do not match and does not reset the password
```

_Maps to: BR-06, FP-07_

### AC-12: Empty required fields in Step 2 are rejected

```
Given  a guest user is on the Reset Password page (Step 2)
When   the user submits the form with one or more required fields left blank
Then   the system displays required-field errors adjacent to each empty field and does not reset the password
```

_Maps to: FP-08_

### AC-13: Re-submitting Step 1 generates a new OTP

```
Given  a guest user has already requested an OTP in Step 1
When   the user navigates back to Step 1, enters the email again, and submits
Then   the system generates a new OTP and displays it on screen
```

_Maps to: BR-09_

## 7. Out of Scope

- Does not cover the Login flow itself (covered by FR-02).
- Does not cover Account Registration (covered by FR-01).
- Does not cover OTP delivery via a real email service — the demo environment displays the OTP on screen.
- Does not cover OTP expiration or time-to-live behaviour (SRS §SEC-07 mentions OTP entropy and expiration, but security requirements are out of scope for functional testing per project rules).
- Does not cover OTP invalidation after successful use (SRS §SEC-07 mentions single-use OTP, but this is a security requirement and out of scope).
- Does not cover brute-force OTP guessing protection or rate limiting.
- Does not cover UI layout, typography, colour, or responsive design concerns.
- Does not cover performance or accessibility testing.

## 8. Dependencies

| Dependency                   | Type        | Hard / Soft | Notes                                                                                                                                                        |
| ---------------------------- | ----------- | ----------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------ |
| FR-01 (Account Registration) | FR          | Hard        | A registered user account must exist before the Forgot Password flow can be tested; the email must be in the user accounts table                             |
| FR-01 Password Policy        | FR          | Hard        | BR-05 references the password policy defined in FR-01; the new password must comply with the same rules (≥8 chars, ≥1 upper, ≥1 lower, ≥1 digit, ≥1 special) |
| User Accounts Table          | Data        | Hard        | Must contain at least one registered email for positive test paths; must be queryable for duplicate/existence checks                                         |
| Email Delivery Service       | System      | Soft        | In production, OTP is sent via email; in demo environment, OTP is displayed on screen — soft dependency as demo mode bypasses email delivery                 |
| Login Page                   | Environment | Soft        | The "Back to Login" button (BR-04) links to the login page; login page must be accessible for navigation verification                                        |

## 9. Test Notes

- **Seed data:** At least one registered user account must exist before testing (e.g., the default test user `test@eshop.com` / `Test1234!`). A second registered account is needed for OTP scope-bound testing (AC-09 / FP-05) — either register a second account as a test prerequisite or use the admin account `admin@eshop.com`.
- **Environment:** The SUT must be running in demo mode so that the OTP is displayed directly on screen. No real email service is required.
- **OTP capture:** Tests must capture the OTP value displayed on screen in Step 1 and use it in Step 2. The test framework needs a mechanism to read the OTP from the UI (e.g., locating the OTP display element and extracting its text content).
- **Risks:**
  - The SRS does not specify what happens when an empty or partially-filled OTP is submitted — behaviour should be verified empirically.
  - The SRS does not specify whether the OTP has an expiration time in the demo environment — only SEC-07 (out of scope) mentions OTP expiration. Testers should note any time-limited behaviour observed during testing.
  - The SRS does not explicitly specify the error messages for invalid OTP or unregistered email — exact message wording must be discovered during UI exploration.
- **Open questions:** None.
