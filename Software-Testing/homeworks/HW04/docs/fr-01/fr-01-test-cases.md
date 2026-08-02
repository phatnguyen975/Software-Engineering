# Test Case Document — FR-01: Account Registration

> **Spec Source:** `docs/fr-01/fr-01-spec.md`  
> **Technique:** Domain Testing (EP + BVA) + Error Guessing  
> **Generated:** 2026-08-02  
> **Status:** APPROVED

## Section 1 — Analysis Trail

### 1.1 Equivalence Partition Tables

#### Field: Full Name

| Class ID | Partition Description             | Type    | Representative   |
| -------- | --------------------------------- | ------- | ---------------- |
| EP-FN-01 | Non-empty string (standard ASCII) | Valid   | `"Nguyen Van A"` |
| EP-FN-02 | Empty string                      | Invalid | `""`             |

#### Field: Email

| Class ID | Partition Description                        | Type    | Representative          |
| -------- | -------------------------------------------- | ------- | ----------------------- |
| EP-EM-01 | Valid format, unique (not registered)        | Valid   | `"newuser@example.com"` |
| EP-EM-02 | Valid format, duplicate (already registered) | Invalid | `"test@eshop.com"`      |
| EP-EM-03 | Missing `@` symbol                           | Invalid | `"invalidemail.com"`    |
| EP-EM-04 | Missing domain part (local part + `@` only)  | Invalid | `"user@"`               |
| EP-EM-05 | Missing local part (`@` + domain only)       | Invalid | `"@example.com"`        |
| EP-EM-06 | Empty string                                 | Invalid | `""`                    |

#### Field: Password

| Class ID | Partition Description                                             | Type    | Representative    |
| -------- | ----------------------------------------------------------------- | ------- | ----------------- |
| EP-PW-01 | ≥8 chars, has upper, lower, digit, valid special char (`@$!%*?&`) | Valid   | `"ValidP@ss1"`    |
| EP-PW-02 | Fewer than 8 characters (all other rules met)                     | Invalid | `"Abc1!xy"`       |
| EP-PW-03 | No uppercase letter (≥8 chars, has digit + valid special)         | Invalid | `"validp@ss1"`    |
| EP-PW-04 | No lowercase letter (≥8 chars, has digit + valid special)         | Invalid | `"VALIDP@SS1"`    |
| EP-PW-05 | No digit (≥8 chars, has upper + lower + valid special)            | Invalid | `"ValidP@ssword"` |
| EP-PW-06 | No special character at all (≥8 chars, has upper + lower + digit) | Invalid | `"ValidPass12"`   |
| EP-PW-07 | Contains special char outside allowed set `@$!%*?&` (e.g. `#`)    | Invalid | `"ValidP#ss1"`    |
| EP-PW-08 | Empty string                                                      | Invalid | `""`              |

#### Field: Confirm Password

| Class ID | Partition Description                 | Type    | Representative       |
| -------- | ------------------------------------- | ------- | -------------------- |
| EP-CP-01 | Exactly matches Password value        | Valid   | _(Same as Password)_ |
| EP-CP-02 | Non-empty but does not match Password | Invalid | `"DifferentP@ss1"`   |
| EP-CP-03 | Empty string                          | Invalid | `""`                 |

### 1.2 Boundary Value Analysis

#### Field: Password — Length Boundary (min = 8, max = undefined)

| BVA Point | Length | Example Value           | EP Class | TC Type  |
| --------- | ------ | ----------------------- | -------- | -------- |
| min − 1   | 7      | `"Abc1!xy"` (7 chars)   | EP-PW-02 | Negative |
| min       | 8      | `"AbcD1!xy"` (8 chars)  | EP-PW-01 | Edge     |
| min + 1   | 9      | `"AbcD1!xyz"` (9 chars) | EP-PW-01 | Positive |

### 1.3 Error Guessing — Fault-Attack Catalogue

| #     | Applied? | Description                                                                                                    |
| ----- | -------- | -------------------------------------------------------------------------------------------------------------- |
| EG-01 | ✅       | Empty Full Name, Email, Password, Confirm Password — each generates an isolated negative TC                    |
| EG-02 | ✅       | Invalid special character in password (e.g. `#`) — already captured as EP-PW-07                                |
| EG-03 | N/A      | Maximum-length string — no max length constraint defined for any field                                         |
| EG-04 | N/A      | Overlong input — no max length constraint defined                                                              |
| EG-05 | ✅       | Email with leading/trailing whitespace (e.g. `" newuser@example.com"`) — not covered by EP classes; adds TC    |
| EG-06 | ✅       | Duplicate email — already captured as EP-EM-02                                                                 |
| EG-07 | N/A      | Incorrect data type — all inputs are strings submitted via web form; not applicable                            |
| EG-08 | ✅       | Uppercase version of an already-registered email (e.g. `"TEST@ESHOP.COM"`) — tests case-insensitive uniqueness |
| EG-09 | ✅       | Confirm Password does not match Password — already captured as EP-CP-02                                        |
| EG-10 | N/A      | Feature is unauthenticated; no login session required                                                          |
| EG-11 | N/A      | SQL/script injection — security testing is out of scope                                                        |
| EG-12 | ✅       | Full Name containing Unicode/Vietnamese characters (e.g. `"Nguyễn Văn An"`) — adds a positive edge TC          |
| EG-13 | N/A      | Repeated submission — out of scope for black-box functional testing                                            |
| EG-14 | N/A      | Feature is unauthenticated; session state is irrelevant                                                        |

### 1.4 Valid-Class Combination Matrix

All four fields have exactly one valid EP class each. Two positive test cases achieve full valid-class coverage:

| Row | Full Name                 | Email                        | Password                            | Confirm Password | Rationale                                  |
| --- | ------------------------- | ---------------------------- | ----------------------------------- | ---------------- | ------------------------------------------ |
| P-1 | EP-FN-01 `"Nguyen Van A"` | EP-EM-01 unique valid        | EP-PW-01 (exactly 8 chars, BVA-min) | EP-CP-01 (match) | Covers all valid classes; BVA min boundary |
| P-2 | EG-12 `"Nguyễn Văn An"`   | EP-EM-01 (different address) | EP-PW-01 (>8 chars)                 | EP-CP-01 (match) | Covers Unicode Full Name; longer password  |

### 1.5 Deduplication Summary

After merging EP, BVA, and error-guessing results:

- BVA min=8 → merged into P-1 (avoids redundant positive TC).
- EG-01 empty cases → each is a distinct field being empty → kept as separate isolated TCs.
- EG-08 uppercase duplicate email → distinct input from EP-EM-02 (different string value, different fault hypothesis) → kept.
- EG-05 whitespace email → distinct from all EP-EM cases → kept.
- EG-12 Unicode name → merged into P-2 positive TC.
- EP-CP-03 (empty Confirm Password) and EP-PW-08 (empty Password) → different fields; both kept.

**Total unique TCs after deduplication: 18**

## Section 2 — Test Case Table

| TC-ID       | Title                                                               | Type     | Technique      | Priority | Preconditions                                                       | Test Steps                                                                                                                                                                                                                                                  | Input Data                         | Expected Result                                                                                                                                           | Actual Result | Status | Notes                                   |
| ----------- | ------------------------------------------------------------------- | -------- | -------------- | -------- | ------------------------------------------------------------------- | ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- | ---------------------------------- | --------------------------------------------------------------------------------------------------------------------------------------------------------- | ------------- | ------ | --------------------------------------- |
| TC-FR01-001 | Register account with valid data at minimum password length         | Edge     | Domain Testing | High     | SUT is running. No account exists for the test email address.       | 1. Navigate to the registration page<br>2. Enter Full Name<br>3. Enter a unique valid Email<br>4. Enter Password (exactly 8 chars, policy-compliant)<br>5. Enter matching Confirm Password<br>6. Click the Register button                                  | → ref: fr-01-data.json#TC-FR01-001 | System creates the account and redirects the user to the Login page (`/login`).                                                                           |               |        | BVA min boundary (password length = 8)  |
| TC-FR01-002 | Register account with valid data and Unicode full name              | Positive | Error Guessing | Medium   | SUT is running. No account exists for the test email address.       | 1. Navigate to the registration page<br>2. Enter Full Name (contains Vietnamese diacritics)<br>3. Enter a unique valid Email<br>4. Enter Password (>8 chars, policy-compliant)<br>5. Enter matching Confirm Password<br>6. Click the Register button        | → ref: fr-01-data.json#TC-FR01-002 | System creates the account and redirects the user to the Login page (`/login`).                                                                           |               |        | EG-12: Unicode characters in name field |
| TC-FR01-003 | Register account with empty Full Name field                         | Negative | Domain Testing | High     | SUT is running.                                                     | 1. Navigate to the registration page<br>2. Leave Full Name empty<br>3. Enter a unique valid Email<br>4. Enter a policy-compliant Password<br>5. Enter matching Confirm Password<br>6. Click the Register button                                             | → ref: fr-01-data.json#TC-FR01-003 | System does not create an account. An error message is displayed on the Full Name field indicating it is required.                                        |               |        | EP-FN-02                                |
| TC-FR01-004 | Register account with empty Email field                             | Negative | Domain Testing | High     | SUT is running.                                                     | 1. Navigate to the registration page<br>2. Enter a valid Full Name<br>3. Leave Email empty<br>4. Enter a policy-compliant Password<br>5. Enter matching Confirm Password<br>6. Click the Register button                                                    | → ref: fr-01-data.json#TC-FR01-004 | System does not create an account. An error message is displayed on the Email field indicating it is required.                                            |               |        | EP-EM-06; EG-01                         |
| TC-FR01-005 | Register account with duplicate email address                       | Negative | Domain Testing | High     | SUT is running. Account with email `test@eshop.com` already exists. | 1. Navigate to the registration page<br>2. Enter a valid Full Name<br>3. Enter the already-registered Email<br>4. Enter a policy-compliant Password<br>5. Enter matching Confirm Password<br>6. Click the Register button                                   | → ref: fr-01-data.json#TC-FR01-005 | System does not create an account. An error message is displayed indicating the email address is already registered.                                      |               |        | EP-EM-02; BR-02                         |
| TC-FR01-006 | Register account with email missing @ symbol                        | Negative | Domain Testing | Medium   | SUT is running.                                                     | 1. Navigate to the registration page<br>2. Enter a valid Full Name<br>3. Enter an email without the `@` symbol<br>4. Enter a policy-compliant Password<br>5. Enter matching Confirm Password<br>6. Click the Register button                                | → ref: fr-01-data.json#TC-FR01-006 | System does not create an account. An error message is displayed on the Email field indicating the email format is invalid.                               |               |        | EP-EM-03; BR-02, BR-06                  |
| TC-FR01-007 | Register account with email missing domain part                     | Negative | Domain Testing | Medium   | SUT is running.                                                     | 1. Navigate to the registration page<br>2. Enter a valid Full Name<br>3. Enter an email with no domain (e.g. `user@`)<br>4. Enter a policy-compliant Password<br>5. Enter matching Confirm Password<br>6. Click the Register button                         | → ref: fr-01-data.json#TC-FR01-007 | System does not create an account. An error message is displayed on the Email field indicating the email format is invalid.                               |               |        | EP-EM-04                                |
| TC-FR01-008 | Register account with email missing local part                      | Negative | Domain Testing | Medium   | SUT is running.                                                     | 1. Navigate to the registration page<br>2. Enter a valid Full Name<br>3. Enter an email with no local part (e.g. `@example.com`)<br>4. Enter a policy-compliant Password<br>5. Enter matching Confirm Password<br>6. Click the Register button              | → ref: fr-01-data.json#TC-FR01-008 | System does not create an account. An error message is displayed on the Email field indicating the email format is invalid.                               |               |        | EP-EM-05                                |
| TC-FR01-009 | Register account with password shorter than 8 characters            | Negative | Domain Testing | High     | SUT is running.                                                     | 1. Navigate to the registration page<br>2. Enter a valid Full Name<br>3. Enter a unique valid Email<br>4. Enter a 7-character password that meets all other policy rules<br>5. Enter matching Confirm Password<br>6. Click the Register button              | → ref: fr-01-data.json#TC-FR01-009 | System does not create an account. An error message is displayed on the Password field indicating the minimum length requirement is not met.              |               |        | EP-PW-02; BVA min−1                     |
| TC-FR01-010 | Register account with password missing uppercase letter             | Negative | Domain Testing | High     | SUT is running.                                                     | 1. Navigate to the registration page<br>2. Enter a valid Full Name<br>3. Enter a unique valid Email<br>4. Enter a password with no uppercase letter<br>5. Enter matching Confirm Password<br>6. Click the Register button                                   | → ref: fr-01-data.json#TC-FR01-010 | System does not create an account. An error message is displayed on the Password field indicating an uppercase letter is required.                        |               |        | EP-PW-03                                |
| TC-FR01-011 | Register account with password missing lowercase letter             | Negative | Domain Testing | High     | SUT is running.                                                     | 1. Navigate to the registration page<br>2. Enter a valid Full Name<br>3. Enter a unique valid Email<br>4. Enter a password with no lowercase letter<br>5. Enter matching Confirm Password<br>6. Click the Register button                                   | → ref: fr-01-data.json#TC-FR01-011 | System does not create an account. An error message is displayed on the Password field indicating a lowercase letter is required.                         |               |        | EP-PW-04                                |
| TC-FR01-012 | Register account with password missing digit                        | Negative | Domain Testing | High     | SUT is running.                                                     | 1. Navigate to the registration page<br>2. Enter a valid Full Name<br>3. Enter a unique valid Email<br>4. Enter a password with no digit<br>5. Enter matching Confirm Password<br>6. Click the Register button                                              | → ref: fr-01-data.json#TC-FR01-012 | System does not create an account. An error message is displayed on the Password field indicating a digit is required.                                    |               |        | EP-PW-05                                |
| TC-FR01-013 | Register account with password missing any special character        | Negative | Domain Testing | High     | SUT is running.                                                     | 1. Navigate to the registration page<br>2. Enter a valid Full Name<br>3. Enter a unique valid Email<br>4. Enter a password with no special character<br>5. Enter matching Confirm Password<br>6. Click the Register button                                  | → ref: fr-01-data.json#TC-FR01-013 | System does not create an account. An error message is displayed on the Password field indicating a special character from the set `@$!%*?&` is required. |               |        | EP-PW-06                                |
| TC-FR01-014 | Register account with password containing invalid special character | Negative | Domain Testing | High     | SUT is running.                                                     | 1. Navigate to the registration page<br>2. Enter a valid Full Name<br>3. Enter a unique valid Email<br>4. Enter a password containing a special character not in `@$!%*?&` (e.g. `#`)<br>5. Enter matching Confirm Password<br>6. Click the Register button | → ref: fr-01-data.json#TC-FR01-014 | System does not create an account. An error message is displayed on the Password field indicating the special character used is not allowed.              |               |        | EP-PW-07; AC-12                         |
| TC-FR01-015 | Register account with empty Password field                          | Negative | Domain Testing | High     | SUT is running.                                                     | 1. Navigate to the registration page<br>2. Enter a valid Full Name<br>3. Enter a unique valid Email<br>4. Leave Password empty<br>5. Enter any value in Confirm Password<br>6. Click the Register button                                                    | → ref: fr-01-data.json#TC-FR01-015 | System does not create an account. An error message is displayed on the Password field indicating it is required.                                         |               |        | EP-PW-08; EG-01                         |
| TC-FR01-016 | Register account with mismatched Confirm Password                   | Negative | Domain Testing | High     | SUT is running.                                                     | 1. Navigate to the registration page<br>2. Enter a valid Full Name<br>3. Enter a unique valid Email<br>4. Enter a policy-compliant Password<br>5. Enter a different value in Confirm Password<br>6. Click the Register button                               | → ref: fr-01-data.json#TC-FR01-016 | System does not create an account. An error message is displayed on the Confirm Password field indicating the passwords do not match.                     |               |        | EP-CP-02; BR-04                         |
| TC-FR01-017 | Register account with empty Confirm Password field                  | Negative | Domain Testing | High     | SUT is running.                                                     | 1. Navigate to the registration page<br>2. Enter a valid Full Name<br>3. Enter a unique valid Email<br>4. Enter a policy-compliant Password<br>5. Leave Confirm Password empty<br>6. Click the Register button                                              | → ref: fr-01-data.json#TC-FR01-017 | System does not create an account. An error message is displayed on the Confirm Password field indicating it is required or the passwords do not match.   |               |        | EP-CP-03; EG-01                         |
| TC-FR01-018 | Register account with email containing leading whitespace           | Negative | Error Guessing | Medium   | SUT is running.                                                     | 1. Navigate to the registration page<br>2. Enter a valid Full Name<br>3. Enter an email with a leading space (e.g. `" user@example.com"`)<br>4. Enter a policy-compliant Password<br>5. Enter matching Confirm Password<br>6. Click the Register button     | → ref: fr-01-data.json#TC-FR01-018 | System does not create an account. An error message is displayed on the Email field indicating the email format is invalid.                               |               |        | EG-05: leading whitespace in email      |
