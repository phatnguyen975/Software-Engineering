# Test Cases — FR-01: Account Registration

> **Spec source:** `docs/fr-01/fr-01-spec.md`  
> **Generated:** 2026-07-28  
> **Techniques:** Domain Testing (EP + BVA) · Error Guessing

## Part 1 — Analysis Trail

### 1.1 Equivalence Partition Tables

#### Field: Full Name (string, required, min=2, max=50)

| Class ID | Partition Description                                   | Type    | Representative            |
| -------- | ------------------------------------------------------- | ------- | ------------------------- |
| EP-FN-01 | Length within valid range [2, 50], printable chars only | Valid   | `Nguyen Van A` (13 chars) |
| EP-FN-02 | Length below minimum (< 2)                              | Invalid | `A` (1 char)              |
| EP-FN-03 | Length above maximum (> 50)                             | Invalid | 51-char string            |
| EP-FN-04 | Empty string                                            | Invalid | `""`                      |
| EP-FN-05 | Whitespace only                                         | Invalid | `"   "`                   |

#### Field: Email (string, required, max=254, unique)

| Class ID | Partition Description                        | Type    | Representative         |
| -------- | -------------------------------------------- | ------- | ---------------------- |
| EP-EM-01 | Valid RFC 5321 email, not already registered | Valid   | `newuser@example.com`  |
| EP-EM-02 | Email already registered in the system       | Invalid | `existing@example.com` |
| EP-EM-03 | Missing `@` symbol                           | Invalid | `invalidemail.com`     |
| EP-EM-04 | Missing domain part                          | Invalid | `user@`                |
| EP-EM-05 | Missing local part                           | Invalid | `@example.com`         |
| EP-EM-06 | Empty string                                 | Invalid | `""`                   |
| EP-EM-07 | Length above maximum (> 254 chars)           | Invalid | 255-char email string  |

#### Field: Password (string, required, min=8, max=72)

**Constraints:** ≥1 uppercase, ≥1 lowercase, ≥1 digit

| Class ID | Partition Description                     | Type    | Representative     |
| -------- | ----------------------------------------- | ------- | ------------------ |
| EP-PW-01 | Meets all policy rules, length in [8, 72] | Valid   | `ValidPass1`       |
| EP-PW-02 | Length below minimum (< 8)                | Invalid | `Short1` (6 chars) |
| EP-PW-03 | No uppercase letter                       | Invalid | `nouppercase1`     |
| EP-PW-04 | No lowercase letter                       | Invalid | `NOLOWER1`         |
| EP-PW-05 | No digit                                  | Invalid | `NoDigitHere`      |
| EP-PW-06 | Empty string                              | Invalid | `""`               |
| EP-PW-07 | Length above maximum (> 72)               | Invalid | 73-char string     |

#### Field: Confirm Password (string, required — must equal Password)

| Class ID | Partition Description          | Type    | Representative   |
| -------- | ------------------------------ | ------- | ---------------- |
| EP-CP-01 | Exactly matches Password field | Valid   | Same as Password |
| EP-CP-02 | Does not match Password field  | Invalid | `DifferentPass1` |
| EP-CP-03 | Empty string                   | Invalid | `""`             |

### 1.2 Boundary Value Analysis

#### Full Name — length boundary (min=2, max=50)

| BVA Point | Value             | EP Class | TC Type               |
| --------- | ----------------- | -------- | --------------------- |
| min − 1   | 1 char (`"A"`)    | EP-FN-02 | Negative              |
| min       | 2 chars (`"AB"`)  | EP-FN-01 | Edge (lower boundary) |
| min + 1   | 3 chars (`"ABC"`) | EP-FN-01 | Positive              |
| max − 1   | 49 chars          | EP-FN-01 | Positive              |
| max       | 50 chars          | EP-FN-01 | Edge (upper boundary) |
| max + 1   | 51 chars          | EP-FN-03 | Negative              |

#### Password — length boundary (min=8, max=72)

| BVA Point | Value                       | EP Class | TC Type               |
| --------- | --------------------------- | -------- | --------------------- |
| min − 1   | 7 chars (`"Short1a"`)       | EP-PW-02 | Negative              |
| min       | 8 chars (`"Valid1aA"`)      | EP-PW-01 | Edge (lower boundary) |
| min + 1   | 9 chars (`"Valid1aAb"`)     | EP-PW-01 | Positive              |
| max − 1   | 71 chars (policy-compliant) | EP-PW-01 | Positive              |
| max       | 72 chars (policy-compliant) | EP-PW-01 | Edge (upper boundary) |
| max + 1   | 73 chars                    | EP-PW-07 | Negative              |

### 1.3 Error Guessing Catalogue

| #     | Fault Class                          | Applied? | Test Case(s)                                                               |
| ----- | ------------------------------------ | -------- | -------------------------------------------------------------------------- |
| EG-01 | Empty / blank inputs                 | ✓        | TC-FR01-010 (all fields empty)                                             |
| EG-02 | Boundary-adjacent special characters | ✓        | TC-FR01-016 (special chars in Full Name)                                   |
| EG-03 | Maximum-length exact string          | ✓        | TC-FR01-014 (Full Name = 50 chars), TC-FR01-015 (Password = 72 chars)      |
| EG-04 | Overlong input                       | ✓        | Covered by EP-FN-03, EP-PW-07                                              |
| EG-05 | Leading / trailing whitespace        | ✓        | TC-FR01-017 (email with leading space)                                     |
| EG-06 | Duplicate / already-existing data    | ✓        | Covered by EP-EM-02                                                        |
| EG-07 | Incorrect data type                  | N/A      | All fields are string type; type enforcement is handled by HTML input type |
| EG-08 | Case sensitivity                     | ✓        | TC-FR01-018 (email uniqueness case-insensitive check)                      |
| EG-09 | Multi-field interaction              | ✓        | Covered by EP-CP-02                                                        |
| EG-10 | Dependency violation                 | N/A      | Registration requires no prior login                                       |
| EG-11 | SQL / script injection               | ✓        | TC-FR01-019 (SQL injection in Full Name)                                   |
| EG-12 | Unicode / multi-byte characters      | ✓        | TC-FR01-020 (Vietnamese characters in Full Name)                           |
| EG-13 | Repeated submission                  | N/A      | Out of scope for functional registration testing                           |
| EG-14 | Session / auth state mismatch        | N/A      | No auth required for registration                                          |

### 1.4 Combination Matrix (Valid Classes)

Valid EP representatives used:

| Field            | EP class | Representative value  |
| ---------------- | -------- | --------------------- |
| Full Name        | EP-FN-01 | `Nguyen Van A`        |
| Email            | EP-EM-01 | `newuser@example.com` |
| Password         | EP-PW-01 | `ValidPass1`          |
| Confirm Password | EP-CP-01 | `ValidPass1`          |

All valid classes covered in **1 positive test case** (all independent; no multi-class variation needed beyond the boundary edge cases).

Additional positive TCs added for boundary coverage: min-length Full Name (TC-FR01-002), max-length Full Name (TC-FR01-014), min-length Password (TC-FR01-003), max-length Password (TC-FR01-015).

## Part 2 — Test Case Table

| TC-ID       | Title                                                                   | Type     | Technique      | Priority | Preconditions                                                              | Test Steps                                                                                                                                                                                                                 | Input Data                         | Expected Result                                                                                                                                                     | Actual Result | Status | Notes                                                |
| ----------- | ----------------------------------------------------------------------- | -------- | -------------- | -------- | -------------------------------------------------------------------------- | -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- | ---------------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------- | ------------- | ------ | ---------------------------------------------------- |
| TC-FR01-001 | Register account with valid data                                        | Positive | Domain Testing | High     | User is not logged in. No account exists for `newuser@example.com`.        | 1. Navigate to `/register`<br>2. Enter Full Name<br>3. Enter Email<br>4. Enter Password<br>5. Enter matching Confirm Password<br>6. Click Register                                                                         | → ref: fr-01-data.json#TC-FR01-001 | System creates account, dispatches verification email, redirects to `/login` with message "Registration successful. Please verify your email."                      |               |        |                                                      |
| TC-FR01-002 | Register account with minimum-length full name                          | Edge     | Domain Testing | Medium   | User is not logged in. No account exists for test email.                   | 1. Navigate to `/register`<br>2. Enter Full Name (2 chars)<br>3. Enter valid Email<br>4. Enter valid Password<br>5. Enter matching Confirm Password<br>6. Click Register                                                   | → ref: fr-01-data.json#TC-FR01-002 | System accepts the 2-character name and completes registration successfully.                                                                                        |               |        | BVA min boundary                                     |
| TC-FR01-003 | Register account with minimum-length password                           | Edge     | Domain Testing | Medium   | User is not logged in. No account exists for test email.                   | 1. Navigate to `/register`<br>2. Enter valid Full Name<br>3. Enter valid Email<br>4. Enter Password (8 chars, policy-compliant)<br>5. Enter matching Confirm Password<br>6. Click Register                                 | → ref: fr-01-data.json#TC-FR01-003 | System accepts the 8-character password and completes registration successfully.                                                                                    |               |        | BVA min boundary                                     |
| TC-FR01-004 | Register account with duplicate email address                           | Negative | Domain Testing | High     | User is not logged in. Account with `existing@example.com` already exists. | 1. Navigate to `/register`<br>2. Enter valid Full Name<br>3. Enter already-registered Email<br>4. Enter valid Password<br>5. Enter matching Confirm Password<br>6. Click Register                                          | → ref: fr-01-data.json#TC-FR01-004 | Field-level error appears on the Email field: "This email address is already registered." No account is created.                                                    |               |        | BR-01                                                |
| TC-FR01-005 | Register account with invalid email — missing `@` symbol                | Negative | Domain Testing | High     | User is not logged in.                                                     | 1. Navigate to `/register`<br>2. Enter valid Full Name<br>3. Enter email without `@` symbol<br>4. Enter valid Password<br>5. Enter matching Confirm Password<br>6. Click Register                                          | → ref: fr-01-data.json#TC-FR01-005 | Field-level error appears on the Email field indicating invalid email format. No account is created.                                                                |               |        |                                                      |
| TC-FR01-006 | Register account with password missing uppercase letter                 | Negative | Domain Testing | High     | User is not logged in.                                                     | 1. Navigate to `/register`<br>2. Enter valid Full Name<br>3. Enter valid unique Email<br>4. Enter password with no uppercase letter<br>5. Enter matching Confirm Password<br>6. Click Register                             | → ref: fr-01-data.json#TC-FR01-006 | Field-level error on Password field indicates password must contain at least one uppercase letter. No account is created.                                           |               |        | BR-02                                                |
| TC-FR01-007 | Register account with password missing lowercase letter                 | Negative | Domain Testing | High     | User is not logged in.                                                     | 1. Navigate to `/register`<br>2. Enter valid Full Name<br>3. Enter valid unique Email<br>4. Enter password with no lowercase letter<br>5. Enter matching Confirm Password<br>6. Click Register                             | → ref: fr-01-data.json#TC-FR01-007 | Field-level error on Password field indicates password must contain at least one lowercase letter. No account is created.                                           |               |        | BR-02                                                |
| TC-FR01-008 | Register account with password missing digit                            | Negative | Domain Testing | High     | User is not logged in.                                                     | 1. Navigate to `/register`<br>2. Enter valid Full Name<br>3. Enter valid unique Email<br>4. Enter password with no digit<br>5. Enter matching Confirm Password<br>6. Click Register                                        | → ref: fr-01-data.json#TC-FR01-008 | Field-level error on Password field indicates password must contain at least one digit. No account is created.                                                      |               |        | BR-02                                                |
| TC-FR01-009 | Register account with non-matching confirm password                     | Negative | Domain Testing | High     | User is not logged in.                                                     | 1. Navigate to `/register`<br>2. Enter valid Full Name<br>3. Enter valid unique Email<br>4. Enter valid Password<br>5. Enter different value in Confirm Password<br>6. Click Register                                      | → ref: fr-01-data.json#TC-FR01-009 | Field-level error on Confirm Password field: "Passwords do not match." No account is created.                                                                       |               |        | BR-03                                                |
| TC-FR01-010 | Submit registration form with all fields empty                          | Negative | Error Guessing | High     | User is not logged in.                                                     | 1. Navigate to `/register`<br>2. Leave all fields empty<br>3. Click Register                                                                                                                                               | → ref: fr-01-data.json#TC-FR01-010 | "This field is required" errors displayed on all mandatory fields simultaneously. No account is created.                                                            |               |        |                                                      |
| TC-FR01-011 | Register account with single-character full name                        | Negative | Domain Testing | Medium   | User is not logged in.                                                     | 1. Navigate to `/register`<br>2. Enter Full Name (1 char)<br>3. Enter valid unique Email<br>4. Enter valid Password<br>5. Enter matching Confirm Password<br>6. Click Register                                             | → ref: fr-01-data.json#TC-FR01-011 | Field-level error on Full Name field: name must be at least 2 characters. No account is created.                                                                    |               |        | BVA min-1                                            |
| TC-FR01-012 | Register account with full name exceeding maximum length                | Negative | Domain Testing | Low      | User is not logged in.                                                     | 1. Navigate to `/register`<br>2. Enter Full Name (51 chars)<br>3. Enter valid unique Email<br>4. Enter valid Password<br>5. Enter matching Confirm Password<br>6. Click Register                                           | → ref: fr-01-data.json#TC-FR01-012 | Field-level error on Full Name field: name must not exceed 50 characters. No account is created.                                                                    |               |        | BVA max+1                                            |
| TC-FR01-013 | Register account with password below minimum length                     | Negative | Domain Testing | High     | User is not logged in.                                                     | 1. Navigate to `/register`<br>2. Enter valid Full Name<br>3. Enter valid unique Email<br>4. Enter password (7 chars)<br>5. Enter matching Confirm Password<br>6. Click Register                                            | → ref: fr-01-data.json#TC-FR01-013 | Field-level error on Password field: password must be at least 8 characters. No account is created.                                                                 |               |        | BVA min-1                                            |
| TC-FR01-014 | Register account with maximum-length full name                          | Edge     | Domain Testing | Low      | User is not logged in.                                                     | 1. Navigate to `/register`<br>2. Enter Full Name (50 chars)<br>3. Enter valid unique Email<br>4. Enter valid Password<br>5. Enter matching Confirm Password<br>6. Click Register                                           | → ref: fr-01-data.json#TC-FR01-014 | System accepts the 50-character name and completes registration successfully.                                                                                       |               |        | BVA max boundary                                     |
| TC-FR01-015 | Register account with maximum-length password                           | Edge     | Domain Testing | Low      | User is not logged in.                                                     | 1. Navigate to `/register`<br>2. Enter valid Full Name<br>3. Enter valid unique Email<br>4. Enter Password (72 chars, policy-compliant)<br>5. Enter matching Confirm Password<br>6. Click Register                         | → ref: fr-01-data.json#TC-FR01-015 | System accepts the 72-character password and completes registration successfully.                                                                                   |               |        | BVA max boundary                                     |
| TC-FR01-016 | Register account with special characters in full name                   | Edge     | Error Guessing | Low      | User is not logged in.                                                     | 1. Navigate to `/register`<br>2. Enter Full Name containing special characters (e.g. `O'Brien-Smith`)<br>3. Enter valid unique Email<br>4. Enter valid Password<br>5. Enter matching Confirm Password<br>6. Click Register | → ref: fr-01-data.json#TC-FR01-016 | System accepts the name with special characters and completes registration (or displays a clear validation error if special characters are not allowed — per spec). |               |        | EG-02; expected result depends on spec clarification |
| TC-FR01-017 | Register account with email containing leading whitespace               | Negative | Error Guessing | Medium   | User is not logged in.                                                     | 1. Navigate to `/register`<br>2. Enter valid Full Name<br>3. Enter Email with a leading space (`" user@example.com"`)<br>4. Enter valid Password<br>5. Enter matching Confirm Password<br>6. Click Register                | → ref: fr-01-data.json#TC-FR01-017 | System either trims the whitespace and processes the email, or displays an invalid email format error. No duplicate account is created.                             |               |        | EG-05                                                |
| TC-FR01-018 | Register account with email matching existing account in different case | Negative | Error Guessing | Medium   | Account with `existing@example.com` exists.                                | 1. Navigate to `/register`<br>2. Enter Full Name<br>3. Enter `EXISTING@EXAMPLE.COM`<br>4. Enter valid Password<br>5. Enter matching Confirm Password<br>6. Click Register                                                  | → ref: fr-01-data.json#TC-FR01-018 | System treats email comparison as case-insensitive and returns a duplicate email error. No new account is created.                                                  |               |        | EG-08; BR-01                                         |
| TC-FR01-019 | Register account with SQL injection payload in full name                | Negative | Error Guessing | Medium   | User is not logged in.                                                     | 1. Navigate to `/register`<br>2. Enter `'; DROP TABLE users; --` as Full Name<br>3. Enter valid unique Email<br>4. Enter valid Password<br>5. Enter matching Confirm Password<br>6. Click Register                         | → ref: fr-01-data.json#TC-FR01-019 | System stores the value as a literal string (or rejects it with validation error). No SQL error is thrown. Database is not affected.                                |               |        | EG-11                                                |
| TC-FR01-020 | Register account with Vietnamese characters in full name                | Positive | Error Guessing | Low      | User is not logged in.                                                     | 1. Navigate to `/register`<br>2. Enter Full Name with Vietnamese diacritics (e.g. `Nguyễn Văn An`)<br>3. Enter valid unique Email<br>4. Enter valid Password<br>5. Enter matching Confirm Password<br>6. Click Register    | → ref: fr-01-data.json#TC-FR01-020 | System accepts the name with Unicode characters and completes registration successfully.                                                                            |               |        | EG-12                                                |
