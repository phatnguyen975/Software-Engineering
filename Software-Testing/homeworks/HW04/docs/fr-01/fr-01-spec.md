# Feature Specification — FR-01: Account Registration

> **Source SRS:** `docs/system-requirements-specification.md`  
> **Generated:** 2026-08-02  
> **Status:** APPROVED

## 1. Feature Overview

| Field                   | Value                                                                             |
| ----------------------- | --------------------------------------------------------------------------------- |
| FR ID                   | FR-01                                                                             |
| Feature Name            | Account Registration                                                              |
| Primary Actor           | Guest User (unauthenticated visitor)                                              |
| Secondary Actors        | None                                                                              |
| Authentication Required | No                                                                                |
| Entry Point             | Registration page on Frontend Web (`http://localhost:5173`)                       |
| Actor Goal              | Create a new personal account to access authenticated features of the application |

## 2. Input Fields & Constraints

| Field Name       | Data Type | Required | Min | Max | Format / Pattern                                                                                                                                | Allowed Values | Notes                                                       |
| ---------------- | --------- | -------- | --- | --- | ----------------------------------------------------------------------------------------------------------------------------------------------- | -------------- | ----------------------------------------------------------- |
| Full Name        | string    | Yes      | —   | —   | —                                                                                                                                               | —              | —                                                           |
| Email            | email     | Yes      | —   | —   | Valid email format (`user@domain.com`); HTML5 `type="email"` validation applies (stated in SRS §FR-02)                                          | —              | Must be unique across all registered accounts in the system |
| Password         | password  | Yes      | 8   | —   | Must contain ≥1 uppercase letter, ≥1 lowercase letter, ≥1 digit, ≥1 special character from set `@$!%*?&`. Other special characters are invalid. | —              | —                                                           |
| Confirm Password | password  | Yes      | —   | —   | Must exactly match the Password field value                                                                                                     | —              | Used for validation only; not persisted separately          |

## 3. Business Rules

**BR-01:** The user must provide three mandatory fields to register: Full Name, Email, and Password.

> **Source:** SRS §FR-01 — "Người dùng phải cung cấp: Họ Tên, Email, Mật khẩu."

**BR-02:** The email address must have a valid format (`user@domain.com`) and must be unique across all accounts in the system.

> **Source:** SRS §FR-01 — "Email phải có định dạng hợp lệ (`user@domain.com`) và là duy nhất trong hệ thống."

**BR-03:** The password must be at least 8 characters long and contain at least one uppercase letter, one lowercase letter, one digit, and one special character from the exact set `@`, `$`, `!`, `%`, `*`, `?`, `&`. Any other special character is invalid.

> **Source:** SRS §FR-01 — "Yêu cầu mật khẩu mạnh: Tối thiểu 8 ký tự, có ít nhất 1 chữ hoa, 1 chữ thường, 1 chữ số và 1 ký tự đặc biệt (`@`, `$`, `!`, `%`, `*`, `?`, `&`)."

**BR-04:** A Confirm Password field must be present, and the system must reject the form submission if the Confirm Password value does not match the Password value.

> **Source:** SRS §FR-01 — "Phải có trường Xác nhận mật khẩu — hệ thống từ chối nếu hai trường không khớp."

**BR-05:** Upon successful registration, the user must be redirected to the Login page.

> **Source:** SRS §FR-01 — "Sau khi đăng ký thành công, người dùng được chuyển tới trang Đăng nhập."

**BR-06:** The Email field must use HTML input `type="email"` (providing built-in HTML5 format validation).

> **Source:** SRS §FR-02 — "Trường email phải dùng `type="email"` (có validate HTML5 format)."

## 4. Success Paths

### SP-01: Successful Registration with All Valid Fields

```
Actor:   Navigates to the registration page
System:  Renders the registration form with fields: Full Name, Email, Password, Confirm Password
Actor:   Enters a valid Full Name
Actor:   Enters a unique, valid Email address
Actor:   Enters a Password that satisfies the password policy (BR-03)
Actor:   Enters the same value in the Confirm Password field (matching Password)
Actor:   Submits the form
System:  Validates all fields against constraints (BR-01 through BR-04, BR-06)
System:  Creates a new user account in the system
System:  Redirects the actor to the Login page (BR-05)
Outcome: A new account exists in the system; the actor is on the Login page
```

## 5. Failure Paths

### FP-01: Duplicate Email Address

```
Trigger: The submitted email address already exists in the user accounts table (BR-02)
Actor:   Fills in the registration form with an email that is already registered
Actor:   Submits the form
System:  Validates the email against existing records
System:  Rejects the submission without creating an account
System:  Displays an error message indicating the email is already registered
Outcome: No account is created; the form remains on the registration page; actor sees the error
```

### FP-02: Invalid Email Format

```
Trigger: The submitted email does not conform to the valid email format (BR-02, BR-06)
Actor:   Enters an email without proper format (e.g. missing @, missing domain)
Actor:   Submits the form
System:  Validates the email format (HTML5 type="email" validation and/or server-side validation)
System:  Rejects the submission
System:  Displays an error message indicating the email format is invalid
Outcome: No account is created; the form remains populated; actor sees the format error
```

### FP-03: Password Too Short (Less Than 8 Characters)

```
Trigger: The Password field value has fewer than 8 characters (BR-03)
Actor:   Enters a password with fewer than 8 characters
Actor:   Submits the form
System:  Validates the password against the minimum length requirement
System:  Displays an error on the Password field
Outcome: No account is created; the form remains populated
```

### FP-04: Password Missing Uppercase Letter

```
Trigger: The Password field value does not contain at least one uppercase letter (BR-03)
Actor:   Enters a password without any uppercase letter
Actor:   Submits the form
System:  Validates the password against the complexity rules
System:  Displays an error on the Password field describing the unmet rule
Outcome: No account is created; the form remains populated
```

### FP-05: Password Missing Lowercase Letter

```
Trigger: The Password field value does not contain at least one lowercase letter (BR-03)
Actor:   Enters a password without any lowercase letter
Actor:   Submits the form
System:  Validates the password against the complexity rules
System:  Displays an error on the Password field describing the unmet rule
Outcome: No account is created; the form remains populated
```

### FP-06: Password Missing Digit

```
Trigger: The Password field value does not contain at least one digit (BR-03)
Actor:   Enters a password without any digit
Actor:   Submits the form
System:  Validates the password against the complexity rules
System:  Displays an error on the Password field describing the unmet rule
Outcome: No account is created; the form remains populated
```

### FP-07: Password Missing Special Character

```
Trigger: The Password field value does not contain at least one special character from the set @$!%*?& (BR-03)
Actor:   Enters a password without any special character from the allowed set
Actor:   Submits the form
System:  Validates the password against the complexity rules
System:  Displays an error on the Password field describing the unmet rule
Outcome: No account is created; the form remains populated
```

### FP-08: Confirm Password Does Not Match Password

```
Trigger: The Confirm Password value differs from the Password value (BR-04)
Actor:   Enters a Password and a different value in Confirm Password
Actor:   Submits the form
System:  Detects the mismatch between the two password fields
System:  Displays an error indicating the passwords do not match
Outcome: No account is created; the form remains populated
```

### FP-09: Required Field Left Blank

```
Trigger: One or more required fields (Full Name, Email, Password, Confirm Password) are empty on submission (BR-01)
Actor:   Submits the form with one or more empty required fields
System:  Validates presence of all required fields
System:  Displays an error for each empty required field
Outcome: No account is created; errors are shown for blank fields
```

### FP-10: Password Contains Invalid Special Character

```
Trigger: The Password field value contains a special character outside of the allowed set @$!%*?& (BR-03)
Actor:   Enters a password containing an invalid special character
Actor:   Submits the form
System:  Validates the password against the complexity rules
System:  Displays an error on the Password field describing the unmet rule
Outcome: No account is created; the form remains populated
```

## 6. Acceptance Criteria

### AC-01: Successful registration redirects to Login page

```
Given  a guest user is on the registration page
When   the user submits the form with a valid Full Name, a unique valid Email, a policy-compliant Password, and a matching Confirm Password
Then   the system creates a new user account and redirects the user to the Login page
```

_Maps to: BR-01, BR-02, BR-03, BR-04, BR-05, SP-01_

### AC-02: Duplicate email is rejected

```
Given  a guest user is on the registration page and the email address entered is already registered in the system
When   the user submits the form with all other fields valid
Then   the system displays an error message indicating the email is already registered and does not create a new account
```

_Maps to: BR-02, FP-01_

### AC-03: Invalid email format is rejected

```
Given  a guest user is on the registration page
When   the user submits the form with an email that does not conform to the valid email format
Then   the system displays an error message indicating the email format is invalid and does not create a new account
```

_Maps to: BR-02, BR-06, FP-02_

### AC-04: Password shorter than 8 characters is rejected

```
Given  a guest user is on the registration page
When   the user submits the form with a password that has fewer than 8 characters
Then   the system displays an error on the Password field and does not create a new account
```

_Maps to: BR-03, FP-03_

### AC-05: Password without uppercase letter is rejected

```
Given  a guest user is on the registration page
When   the user submits the form with a password that lacks an uppercase letter
Then   the system displays an error on the Password field describing the unmet rule and does not create a new account
```

_Maps to: BR-03, FP-04_

### AC-06: Password without lowercase letter is rejected

```
Given  a guest user is on the registration page
When   the user submits the form with a password that lacks a lowercase letter
Then   the system displays an error on the Password field describing the unmet rule and does not create a new account
```

_Maps to: BR-03, FP-05_

### AC-07: Password without digit is rejected

```
Given  a guest user is on the registration page
When   the user submits the form with a password that lacks a digit
Then   the system displays an error on the Password field describing the unmet rule and does not create a new account
```

_Maps to: BR-03, FP-06_

### AC-08: Password without special character is rejected

```
Given  a guest user is on the registration page
When   the user submits the form with a password that lacks a special character from the set @$!%*?&
Then   the system displays an error on the Password field describing the unmet rule and does not create a new account
```

_Maps to: BR-03, FP-07_

### AC-09: Mismatched passwords are rejected

```
Given  a guest user is on the registration page
When   the user submits the form with a Password and Confirm Password that do not match
Then   the system displays an error indicating the passwords do not match and does not create a new account
```

_Maps to: BR-04, FP-08_

### AC-10: Empty required fields produce errors

```
Given  a guest user is on the registration page
When   the user submits the form with one or more required fields left blank
Then   the system displays an error for each empty required field and does not create an account
```

_Maps to: BR-01, FP-09_

### AC-11: Email field uses HTML5 type="email"

```
Given  a guest user is on the registration page
When   the user inspects the Email input field
Then   the field has the HTML attribute type="email" providing built-in browser format validation
```

_Maps to: BR-06_

### AC-12: Password with invalid special character is rejected

```
Given  a guest user is on the registration page
When   the user submits the form with a password that contains a special character outside the allowed set @$!%*?&
Then   the system displays an error on the Password field and does not create a new account
```

_Maps to: BR-03, FP-10_

## 7. Out of Scope

- Does not cover the login flow after account creation (covered by FR-02).
- Does not cover password reset or recovery (covered by FR-03).
- Does not cover personal profile management after registration (covered by FR-04).
- Does not cover OAuth or third-party social login (not mentioned in SRS).
- Does not cover email verification or account activation flow (not mentioned in FR-01 of the SRS).
- Does not cover UI layout, typography, colour scheme, or responsive design concerns (GUI testing is out of scope per project rules).
- Does not cover performance under concurrent registration load.
- Does not cover security testing such as SQL injection, XSS, or brute-force protection (SEC requirements are out of scope).

## 8. Dependencies

| Dependency               | Type   | Hard / Soft | Notes                                                                                                                                            |
| ------------------------ | ------ | ----------- | ------------------------------------------------------------------------------------------------------------------------------------------------ |
| User Accounts Table      | Data   | Hard        | Must be accessible and writable; duplicate-email tests (AC-02, FP-01) require at least one pre-existing account with a known email               |
| Frontend Web Application | System | Hard        | Registration page must be served at `http://localhost:5173`; SUT must be running                                                                 |
| Backend API              | System | Hard        | POST endpoint for registration must be available at `http://localhost:3000`                                                                      |
| FR-02 (Login)            | FR     | Soft        | Registration success is verified by redirect to Login page; the login page must exist but login functionality itself is not tested in this scope |

## 9. Test Notes

- **Seed data:** At least one registered account must exist before executing duplicate-email test cases (AC-02, FP-01). The default test account `test@eshop.com` / `Test1234!` can serve this purpose as it is pre-seeded in the SUT.
- **Environment:** SUT (Backend API + Frontend Web) must be running. No email delivery service is needed — the SRS does not mention email verification in FR-01 (only a redirect to Login page upon success).
- **Risks:**
  - The SRS does not specify the exact error messages the system should display for each validation failure. Test design must discover actual messages from the SUT.
  - The `type="email"` requirement is stated in FR-02 (Login) context but applies to all email fields system-wide — confirm this applies to the registration form as well.
- **Open questions:** None.
