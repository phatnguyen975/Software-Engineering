# Feature Specification — FR-01: Account Registration

> **Source SRS:** `docs/system-requirements-specification.md`  
> **Generated:** 2026-07-28  
> **Status:** DRAFT

## 1. Feature Overview

| Field                   | Value                                                                             |
| ----------------------- | --------------------------------------------------------------------------------- |
| FR ID                   | FR-01                                                                             |
| Feature Name            | Account Registration                                                              |
| Primary Actor           | Guest User (unauthenticated visitor)                                              |
| Secondary Actors        | Email Delivery Service                                                            |
| Authentication Required | No                                                                                |
| Entry Point             | `/register`                                                                       |
| Actor Goal              | Create a new personal account to access authenticated features of the application |

## 2. Input Fields & Constraints

| Field Name       | Data Type | Required | Min | Max | Format / Pattern                                           | Allowed Values | Notes                                                     |
| ---------------- | --------- | -------- | --- | --- | ---------------------------------------------------------- | -------------- | --------------------------------------------------------- |
| Full Name        | string    | Yes      | 2   | 50  | Printable characters; leading/trailing whitespace stripped | —              | —                                                         |
| Email            | email     | Yes      | —   | 254 | RFC 5321 compliant; must contain exactly one `@`           | —              | Must be unique across all registered accounts             |
| Password         | password  | Yes      | 8   | 72  | Must contain ≥1 uppercase, ≥1 lowercase, ≥1 digit          | —              | Not stored in plaintext; bcrypt-hashed before persistence |
| Confirm Password | password  | Yes      | —   | —   | Must match Password field value exactly                    | —              | Not persisted; used only for client-side validation       |

## 3. Business Rules

**BR-01:** The email address provided at registration must not already exist in the user accounts table.

> **Source:** SRS §FR-01.3 — "Email must be unique"

**BR-02:** The password must be a minimum of 8 characters and contain at least one uppercase letter, one lowercase letter, and one digit.

> **Source:** SRS §FR-01.4 — "Password policy"

**BR-03:** The Confirm Password value must be identical to the Password value before the form may be submitted.

> **Source:** SRS §FR-01.4 — "Password confirmation"

**BR-04:** A verification email must be dispatched to the registered address upon successful account creation.

> **Source:** SRS §FR-01.5 — "Post-registration actions"

**BR-05:** The new account is created in an inactive state until the verification email link is clicked.

> **Source:** SRS §FR-01.5 — "Account activation"

## 4. Success Paths

### SP-01: Successful Registration with Immediate Redirect

```
Actor:   Navigates to /register
System:  Renders the registration form with fields: Full Name, Email, Password, Confirm Password
Actor:   Enters valid Full Name
Actor:   Enters a unique, valid Email address
Actor:   Enters a Password that satisfies the password policy (BR-02)
Actor:   Enters the same value in Confirm Password
Actor:   Submits the form
System:  Validates all fields against constraints (BR-01 through BR-03)
System:  Creates a new user account in inactive state (BR-05)
System:  Dispatches a verification email to the provided address (BR-04)
System:  Redirects the actor to the login page with a success notification
Outcome: A new inactive account exists; verification email is in transit; actor is on the login page
```

## 5. Failure Paths

### FP-01: Duplicate Email Address

```
Trigger: The submitted email address already exists in the user accounts table (BR-01)
Actor:   Submits the registration form with a duplicate email
System:  Validates the email against existing records
System:  Rejects the submission without creating an account
System:  Displays an inline error on the Email field: "This email address is already registered"
Outcome: No account is created; the form remains populated; actor sees the field-level error
```

### FP-02: Password Does Not Meet Policy

```
Trigger: The Password field value does not satisfy BR-02
Actor:   Submits the form with a non-compliant password
System:  Validates the password against the policy
System:  Displays an inline error on the Password field describing the unmet rule
Outcome: No account is created; the form remains populated; actor sees the field-level error
```

### FP-03: Passwords Do Not Match

```
Trigger: The Confirm Password value differs from the Password value (BR-03)
Actor:   Submits the form
System:  Detects the mismatch
System:  Displays an inline error on the Confirm Password field: "Passwords do not match"
Outcome: No account is created; the form remains populated
```

### FP-04: Required Field Left Blank

```
Trigger: One or more required fields are empty on submission
Actor:   Submits the form with one or more empty required fields
System:  Validates presence of all required fields
System:  Displays a "This field is required" error adjacent to each empty field
Outcome: No account is created; all errors are shown simultaneously
```

## 6. Acceptance Criteria

**AC-01:** Successful registration redirects to login page

```
Given  a guest user is on /register
When   the user submits the form with valid Full Name, unique Email, policy-compliant Password, and matching Confirm Password
Then   the system creates a new user account, sends a verification email, and redirects the user to the login page with a success message
```

_Maps to: BR-01, BR-02, BR-03, BR-04, BR-05, SP-01_

**AC-02:** Duplicate email is rejected with field-level error

```
Given  a guest user is on /register and the email address they enter is already registered
When   the user submits the form
Then   the system displays an inline error on the Email field and does not create a new account
```

_Maps to: BR-01, FP-01_

**AC-03:** Non-compliant password is rejected

```
Given  a guest user is on /register
When   the user submits the form with a password that does not meet the complexity policy
Then   the system displays an inline error on the Password field describing the unmet rule and does not create a new account
```

_Maps to: BR-02, FP-02_

**AC-04:** Mismatched passwords are rejected

```
Given  a guest user is on /register
When   the user submits the form with a Password and Confirm Password that do not match
Then   the system displays an inline error on the Confirm Password field and does not create a new account
```

_Maps to: BR-03, FP-03_

**AC-05:** Empty required fields produce field-level errors

```
Given  a guest user is on /register
When   the user submits the form with one or more required fields left blank
Then   the system displays a "This field is required" error adjacent to each empty field simultaneously and does not create an account
```

_Maps to: FP-04_

## 7. Out of Scope

- Does not cover the login flow after account creation (covered by FR-02).
- Does not cover the email verification link click and account activation flow (covered by FR-03).
- Does not cover password reset or recovery (covered by FR-03).
- Does not cover OAuth or third-party social login.
- Does not cover UI layout, typography, colour, or responsive design concerns.
- Does not cover performance under concurrent registration load.
- Does not cover security testing such as brute-force protection or SQL injection resistance.

## 8. Dependencies

| Dependency             | Type   | Hard / Soft | Notes                                                                                                                                                                            |
| ---------------------- | ------ | ----------- | -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| Email Delivery Service | System | Soft        | Required to verify BR-04 (verification email dispatched); functional registration flow can be tested without a live email service by asserting the API response or checking logs |
| User Accounts Table    | Data   | Hard        | Must be accessible and writable; tests that verify duplicate-email rejection (BR-01) require at least one pre-existing account                                                   |

## 9. Test Notes

- **Seed data:** At least one registered account must exist before executing duplicate-email test cases (AC-02, FP-01). Suggested seed email: `existing@example.com`.
- **Environment:** The email delivery service may be mocked or stubbed in the test environment. If a real service is used, a dedicated test mailbox is required.
- **Risks:** BR-05 (inactive state until email verification) may affect test setup for downstream features that require a fully active account — verify whether the test environment bypasses activation.
- **Open questions:** The SRS does not specify the maximum allowed Full Name length explicitly (50 was inferred from common practice — ⚠ confirm with the development team).
