<div align="center">
  <h1>Homework 02 — Main Report</h1>
  <small>
    <strong>Student:</strong> Nguyễn Tấn Phát - 23127449
  </small> <br />
  <sub>June 20, 2026</sub>
</div>

# Table of Contents

[FR-01 — Account Registration (Pool A)](#fr-01--account-registration-pool-a)

- [A. Requirement Analysis](#a-requirement-analysis)
- [B. Domain Analysis (Equivalence Partitioning Walkthrough)](#b-domain-analysis-equivalence-partitioning-walkthrough)
- [C. Boundary Value Analysis (BVA Walkthrough)](#c-boundary-value-analysis-bva-walkthrough)
- [D. Coverage Review & AI Gap Analysis](#d-coverage-review--ai-gap-analysis)

[FR-07 — Shopping Cart (Pool B)](#fr-07--shopping-cart-pool-b)

- [A. Requirement Analysis](#a-requirement-analysis-1)
- [B. Domain Analysis (Equivalence Partitioning Walkthrough)](#b-domain-analysis-equivalence-partitioning-walkthrough-1)
- [C. Boundary Value Analysis (BVA Walkthrough)](#c-boundary-value-analysis-bva-walkthrough-1)
- [D. Coverage Review & AI Gap Analysis](#d-coverage-review--ai-gap-analysis-1)

[FR-17 — Coupon Management for Admin (Pool C)](#fr-17--coupon-management-for-admin-pool-c)

- [A. Requirement Analysis](#a-requirement-analysis-2)
- [B. Domain Analysis (Equivalence Partitioning Walkthrough)](#b-domain-analysis-equivalence-partitioning-walkthrough-2)
- [C. Boundary Value Analysis (BVA Walkthrough)](#c-boundary-value-analysis-bva-walkthrough-2)
- [D. Coverage Review & AI Gap Analysis](#d-coverage-review--ai-gap-analysis-2)

[FR-03 — Forgot Password & Reset Password for Mobile (Pool D)](#fr-03--forgot-password--reset-password-for-mobile-pool-d)

- [A. Requirement Analysis](#a-requirement-analysis-3)
- [B. Domain Analysis (Equivalence Partitioning Walkthrough)](#b-domain-analysis-equivalence-partitioning-walkthrough-3)
- [C. Boundary Value Analysis (BVA Walkthrough)](#c-boundary-value-analysis-bva-walkthrough-3)
- [D. Coverage Review & AI Gap Analysis](#d-coverage-review--ai-gap-analysis-3)

# FR-01 — Account Registration (Pool A)

## A. Requirement Analysis

To begin Domain Testing, we first analyze the requirement to identify all explicit and implicit constraints, actors, and business rules governing **Account Registration**. This step forms the foundation for extracting variables.

### 1. Feature Overview

| Attribute         | Value                                                   |
| ----------------- | ------------------------------------------------------- |
| Feature ID        | FR-01                                                   |
| Feature Name      | Account Registration                                    |
| Test Layer        | Both (Web UI + API)                                     |
| Entry Point (UI)  | `http://localhost:5173/register` → Registration page    |
| Entry Point (API) | `POST http://localhost:3000/api/register`               |
| Actors            | Anonymous (unauthenticated user)                        |
| Auth Required     | No — registration is a public endpoint, no JWT required |

### 2. Input Fields & Constraints

| Field/Param       | Layer    | Type   | Explicit Constraints (from SRS)                                                                                                                                          | Implicit Constraints (Architecture/DB)                                                                                            | API Param Name      |
| ----------------- | -------- | ------ | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------ | --------------------------------------------------------------------------------------------------------------------------------- | ------------------- |
| `name`            | UI + API | string | Required, non-empty (per FR-01)                                                                                                                                          | Likely VARCHAR with max length (DB-level, not explicitly stated in SRS); no format restriction stated                             | `name`              |
| `email`           | UI + API | string | Required; valid format `user@domain.com`; must be unique in the system (per FR-01); HTML input `type="email"` (per FR-22)                                                | RFC 5322 email format; DB uniqueness constraint; max length implied by VARCHAR                                                    | `email`             |
| `password`        | UI + API | string | Required; min 8 characters; must contain ≥1 uppercase, ≥1 lowercase, ≥1 digit, ≥1 special char from set `@$!%*?&` (per FR-01); rendered as `type="password"` (per FR-22) | Characters outside the allowed special-char set may be silently rejected or accepted (G4 split candidate)                         | `password`          |
| `confirmPassword` | UI only  | string | Required; must exactly match `password` field (per FR-01); rendered as `type="password"` (per FR-22)                                                                     | This field is UI-side only — API spec does NOT include `confirmPassword` in the request body; mismatch must be caught client-side | _(not in API body)_ |

> **Note:** The API spec (`POST /api/register`) only accepts three fields: `name`, `email`, `password`. The `confirmPassword` field is a UI-only control — the API does not validate it. This means password-mismatch testing is a **UI-only** test scenario.

### 3. Business Rules

- **[BR-01]** The `name` field is mandatory; an empty or missing `name` must be rejected. (per FR-01)
- **[BR-02]** The `email` field is mandatory and must conform to the format `user@domain.com`. (per FR-01)
- **[BR-03]** The `email` must be unique — if the email already exists in the system, registration must be rejected. (per FR-01)
- **[BR-04]** The `password` must be at least 8 characters long. (per FR-01)
- **[BR-05]** The `password` must contain at least 1 uppercase letter (A–Z). (per FR-01)
- **[BR-06]** The `password` must contain at least 1 lowercase letter (a–z). (per FR-01)
- **[BR-07]** The `password` must contain at least 1 digit (0–9). (per FR-01)
- **[BR-08]** The `password` must contain at least 1 special character from the set `@$!%*?&` — characters outside this set (e.g., `#`, `^`, `(`) are NOT considered valid special characters. (per FR-01)
- **[BR-09]** The `confirmPassword` (UI only) must exactly match `password`; if the two fields do not match, the system must reject the submission. (per FR-01)
- **[BR-10]** After successful registration, the user must be redirected to the Login page. (per FR-01)
- **[BR-11]** Passwords must NOT be stored as plaintext — they must be hashed before persistence. (per SEC-01)
- **[BR-12]** The registration API (`POST /api/register`) does not require a JWT token — it is a public endpoint. (per SEC-02, inferred: no auth guard on this endpoint)
- **[BR-13]** All user input displayed back on the UI (e.g., name in toast/welcome messages) must be properly escaped — no raw HTML rendering. (per SEC-04)
- **[BR-14]** Database queries for email uniqueness check and user insertion must use parameterized queries, not string concatenation. (per SEC-05)

### 4. Expected Outputs

#### 4.1 Success Path

- **HTTP:** `200 OK` + `{"message": "User registered successfully", "id": <new_user_id>}` (per API spec §1.1)
- **UI:** The user is redirected to the Login page after successful registration. (per FR-01)
- **DB:** A new row is inserted into the `users` table with the provided `name` and `email`; `password` is stored as a hash (bcrypt or equivalent), NOT plaintext. (per SEC-01)

#### 4.2 Failure Paths

- **Empty `name`:** HTTP `400 Bad Request` + error message indicating `name` is required. (per FR-01, BR-01)
- **Invalid email format:** HTTP `400 Bad Request` + error message indicating invalid email format. (per FR-01, BR-02)
- **Duplicate email:** HTTP `400 Bad Request` (or `409 Conflict`) + error message indicating the email is already registered. (per FR-01, BR-03)
- **Password too short (< 8 chars):** HTTP `400 Bad Request` + error message indicating password does not meet requirements. (per FR-01, BR-04)
- **Password missing uppercase:** HTTP `400 Bad Request` + password validation error. (per FR-01, BR-05)
- **Password missing lowercase:** HTTP `400 Bad Request` + password validation error. (per FR-01, BR-06)
- **Password missing digit:** HTTP `400 Bad Request` + password validation error. (per FR-01, BR-07)
- **Password missing valid special char:** HTTP `400 Bad Request` + password validation error. (per FR-01, BR-08)
- **Password contains only invalid special chars (e.g., `#`, `^`):** HTTP `400 Bad Request` — the special char requirement is NOT satisfied by out-of-set characters. (per FR-01, BR-08)
- **`confirmPassword` ≠ `password` (UI only):** The UI must prevent form submission and display an error above the submit button indicating passwords do not match. No API call is made. (per FR-01, BR-09, FR-22)

### 5. GUI Requirements Applicable (FR-21~24)

> **Platform:** Web UI — HTML/DOM semantics checks apply.

- **[GUI-01]** The registration page must have **exactly one `<h1>` tag** describing the page content (e.g., "Đăng ký tài khoản"). (per FR-21)
- **[GUI-02]** All mandatory fields (`name`, `email`, `password`, `confirmPassword`) must have a `*` symbol next to their labels. (per FR-22)
- **[GUI-03]** The email input must use `type="email"` (enables HTML5 format validation in browser). (per FR-22)
- **[GUI-04]** Both password fields must use `type="password"` (characters must be masked/hidden). (per FR-22)
- **[GUI-05]** Error messages must appear **above the submit button**, not below it. (per FR-22)
- **[GUI-06]** The registration form is a **single-step form** (no Step Indicator required — Step Indicator only applies to forms with 2+ steps). (per FR-22)
- **[GUI-07]** The submit/register button must use **blue (positive action color)** per the color consistency rule. (per FR-21)
- **[GUI-08]** Breadcrumb is **not required** for the registration page — it is required only for sub-pages (Shopping Cart, Checkout, Product Detail). (per FR-23)
- **[GUI-09]** Tab Order must follow top-to-bottom, left-to-right focus sequence: `name` → `email` → `password` → `confirmPassword` → submit button. (per FR-21)
- **[GUI-10]** No toast notification is mandated by SRS for registration success — the redirect to the Login page serves as the success feedback. (per FR-01, FR-24 — no explicit toast requirement for this FR)

### 6. Security Requirements Applicable (SEC-xx)

- **[SEC-01]** Password must be hashed before storage. Test indirectly: after registration, attempt login with the submitted password via `POST /api/login` — if it succeeds, password hashing is functioning. Direct DB inspection is required to confirm no plaintext storage.
- **[SEC-02]** The `POST /api/register` endpoint must NOT require a JWT — it must be accessible without any `Authorization` header. If a token is accidentally required, unauthenticated users cannot register.
- **[SEC-04]** If the registered `name` is ever displayed back on the UI (e.g., in a welcome message or profile), it must be escaped. Test by registering with `name = <script>alert(1)</script>` and verifying no script executes.
- **[SEC-05]** The email uniqueness check and user insertion must use parameterized queries. This is a code-level concern; indirect testing via SQL injection payload in `email` field (e.g., `' OR '1'='1`) can detect concatenation vulnerabilities.

> **Note:** SEC-03 (role=admin check), SEC-06 (role field protection), and SEC-07 (OTP) do NOT apply to FR-01.

### 7. Notes for Domain Testing

- **Input variables identified:** `name`, `email`, `password`, `confirmPassword` (UI only)
- **Output variables identified:** HTTP status code, JSON response body (`message`, `id`), UI redirect behavior, DB row created (hashed password), error message text and position
- **Boundary candidates:**
  - `password` length: LB = 8 characters (min); no explicit upper bound stated (UB = system/DB limit, e.g., 255)
  - `name` length: no explicit bound stated in SRS — implicit DB VARCHAR limit is a boundary candidate
  - `email` length: no explicit bound stated — implicit DB limit applies
- **High-risk areas:**
  - Password special character set restriction (`@$!%*?&` only) — chars outside this set (e.g., `#`, `^`, `(`, `)`) look like valid special chars but must be rejected
  - `confirmPassword` mismatch — UI-only validation, not present in API body
  - Duplicate email handling — must be tested both via UI and direct API call
  - Password exactly at boundary (length = 7 → reject, length = 8 → accept)
  - Password meets length but missing one category (e.g., all lowercase + digit + special, no uppercase)
- **AI blind spot warnings:**
  - `confirmPassword` field does NOT appear in the API spec — AI may forget to test password mismatch via UI, or may mistakenly try to send `confirmPassword` to the API
  - Special chars OUTSIDE the allowed set (e.g., `#`, `_`, `(`) are a hidden invalid class — AI tends to only test "no special char at all" and misses "wrong special char"
  - Email already exists is a stateful test — the test environment must have a pre-existing email to test against (e.g., `test@eshop.com`)
  - The API success response returns `"id"` (new user ID) — this should be validated as a positive integer in the response body

## B. Domain Analysis (Equivalence Partitioning Walkthrough)

Based on the identified constraints, we proceed to Step 1: Identifying Variables (both input and output) and Step 2 & 3: Dividing them into valid and invalid Equivalence Classes applying the 4 EP guidelines.

### Step 1: Input & Output Variable Identification

#### 1.1 Input Variables

##### Direct Inputs (UI Form / API Body)

| #   | Variable          | Source             | Type   | Description                                                                               |
| --- | ----------------- | ------------------ | ------ | ----------------------------------------------------------------------------------------- |
| I1  | `name`            | UI form + API body | string | Full name of the registering user; required, non-empty                                    |
| I2  | `email`           | UI form + API body | string | Email address; must be valid format and unique in the system                              |
| I3  | `password`        | UI form + API body | string | Password; min 8 chars; must have uppercase, lowercase, digit, special char from `@$!%*?&` |
| I4  | `confirmPassword` | UI form **only**   | string | Must exactly match `password`; this field is **not sent to the API**                      |

##### Indirect Inputs (Hidden / System State)

| #   | Variable              | Source              | Type    | Description                                                                                                        |
| --- | --------------------- | ------------------- | ------- | ------------------------------------------------------------------------------------------------------------------ |
| I5  | `email_uniqueness`    | DB state            | boolean | Whether the provided email already exists in the `users` table; drives BR-03                                       |
| I6  | `password_char_set`   | Input content       | enum    | Whether special chars used are from the allowed set `@$!%*?&` or outside it; drives BR-08 (G4 split)               |
| I7  | `auth_token_presence` | HTTP request header | boolean | Whether an `Authorization: Bearer` token is present; must NOT be required for this public endpoint (BR-12, SEC-02) |

#### 1.2 Output Variables

##### Direct Outputs (Visible)

| #   | Variable                 | Channel | Description                                                                        |
| --- | ------------------------ | ------- | ---------------------------------------------------------------------------------- |
| O1  | HTTP status code         | API     | `200 OK` on success; `400 Bad Request` or `409 Conflict` on failure                |
| O2  | Response body: `message` | API     | `"User registered successfully"` on success; error description string on failure   |
| O3  | Response body: `id`      | API     | Positive integer — new user ID returned on success; absent on failure              |
| O4  | UI redirect              | UI      | On success, browser navigates to Login page (per BR-10, FR-01)                     |
| O5  | UI error message         | UI      | Validation error text displayed **above the submit button** on failure (per FR-22) |

##### Indirect Outputs (Hidden / State Changes)

| #   | Variable                    | Channel | Description                                                                                                      |
| --- | --------------------------- | ------- | ---------------------------------------------------------------------------------------------------------------- |
| O6  | DB: new user record         | State   | A new row inserted in the `users` table with `name`, `email`, hashed `password` (per SEC-01)                     |
| O7  | DB: password storage format | State   | `password` field in DB must be a hash (bcrypt), NOT plaintext (per SEC-01, BR-11)                                |
| O8  | DOM: `<h1>` count           | DOM     | Registration page must have **exactly 1** `<h1>` element (per FR-21, GUI-01)                                     |
| O9  | DOM: email input type       | DOM     | Email `<input>` must have `type="email"` attribute (per FR-22, GUI-03)                                           |
| O10 | DOM: password input types   | DOM     | Both password `<input>` fields must have `type="password"` attribute (per FR-22, GUI-04)                         |
| O11 | DOM: required field markers | DOM     | All 4 mandatory fields must display a `*` next to their labels (per FR-22, GUI-02)                               |
| O12 | DOM: error message position | DOM     | Error messages rendered above the submit button, not below (per FR-22, GUI-05)                                   |
| O13 | DOM: submit button color    | DOM     | Submit button must be blue (positive action color) (per FR-21, GUI-07)                                           |
| O14 | XSS safety: `name` display  | UI      | If `name` is echoed back (e.g., profile/welcome), it must be escaped — no raw HTML rendering (per SEC-04, BR-13) |

#### 1.3 Variable Summary for EP

- **Total inputs identified:** 7 (4 direct + 3 indirect)
- **Total outputs identified:** 14 (5 direct + 9 indirect)
- **Variables requiring EP (input variables for equivalence partitioning):**
  - `name` (I1)
  - `email` (I2)
  - `password` (I3)
  - `confirmPassword` (I4) — UI channel only
  - `email_uniqueness` (I5) — System state variable
  - `password_char_set` (I6) — Hidden enum, G4 split candidate
  - `auth_token_presence` (I7) — SEC-02 test variable
- **Boundary candidates:**
  - `password` **length**: explicit LB = 8 chars; UB = implicit DB/system limit (→ BVA target)
  - `name` **length**: no explicit SRS bound; implicit DB VARCHAR limit (→ BVA candidate)
  - `email` **length**: no explicit SRS bound; implicit DB VARCHAR limit (→ BVA candidate)

> **Blind Spot Check (per domain-identifier skill Section 7):**
>
> - `confirmPassword` captured as I4 (UI-only, not in API body)
> - `email_uniqueness` captured as I5 (DB state — stateful hidden input)
> - `password_char_set` captured as I6 (G4 split: in-set vs out-of-set special chars)
> - `auth_token_presence` captured as I7 (SEC-02: endpoint must be public)
> - DOM outputs O8–O13 captured for Web UI channel
> - Password hashing output O7 captured (SEC-01 indirect test)

### Step 2: Equivalence Classes

#### Variable: `name` (I1) — Guideline 3 (Must-Be: non-empty) + B1 (empty/null)

| Class ID | Type    | Description                                                                                                   | Representative Value          |
| -------- | ------- | ------------------------------------------------------------------------------------------------------------- | ----------------------------- |
| EC01     | Valid   | Non-empty string name                                                                                         | `"Nguyen Van A"`              |
| EC02     | Invalid | Empty string (B1: required field left blank)                                                                  | `""`                          |
| EC03     | Invalid | Null / field omitted from API body (B1: missing)                                                              | _(omit `name` key from JSON)_ |
| EC24     | Valid   | Name containing HTML/XSS injection payload — tests escaped rendering on UI, not rejection (G4 split — SEC-04) | `"<script>alert(1)</script>"` |

> **Guideline applied:** G3 — binary must-be condition: name must be non-empty. B1 extension adds empty and null cases. G4 split adds EC24: a valid name containing HTML/script tags verifies the system accepts the input but renders it safely on UI (per SEC-04, O14). Rejection is NOT expected — safe display is the pass criterion.

#### Variable: `email` (I2) — Guideline 3 (Must-Be: valid format) + Guideline 3 (Must-Be: unique in DB) + B1

| Class ID | Type    | Description                                        | Representative Value                |
| -------- | ------- | -------------------------------------------------- | ----------------------------------- |
| EC04     | Valid   | Valid format AND not yet registered in DB          | `"newuser@test.com"`                |
| EC05     | Invalid | Invalid format — missing `@` symbol                | `"invalidemail"`                    |
| EC06     | Invalid | Invalid format — missing domain after `@`          | `"user@"`                           |
| EC07     | Invalid | Invalid format — missing local part before `@`     | `"@domain.com"`                     |
| EC08     | Invalid | Email already exists in DB (duplicate — per BR-03) | `"test@eshop.com"` _(pre-existing)_ |
| EC09     | Invalid | Empty string (B1: required field left blank)       | `""`                                |
| EC10     | Invalid | Null / field omitted from API body (B1: missing)   | _(omit `email` key from JSON)_      |

> **Guidelines applied:** G3 × 2 — (1) email format must be valid; (2) email must be unique in DB. B1 extension adds empty and null cases. EC05–EC07 cover three structurally distinct invalid format sub-cases (G4 split within the invalid class).

#### Variable: `password` (I3) — Guideline 1 (Range: length ≥ 8) + Guideline 3 × 4 (Must-Be: char types) + Guideline 4 (Split: special char set)

| Class ID | Type    | Description                                                                  | Representative Value                                       |
| -------- | ------- | ---------------------------------------------------------------------------- | ---------------------------------------------------------- |
| EC11     | Valid   | Length ≥ 8; has uppercase, lowercase, digit, and special char from `@$!%*?&` | `"Test@123"`                                               |
| EC12     | Invalid | Length < 8 (G1: below lower bound)                                           | `"Te@1"` _(4 chars — has all char types, isolates length)_ |
| EC13     | Invalid | Missing uppercase letter (G3)                                                | `"test@123"` _(8 chars)_                                   |
| EC14     | Invalid | Missing lowercase letter (G3)                                                | `"TEST@123"` _(8 chars)_                                   |
| EC15     | Invalid | Missing digit (G3)                                                           | `"Test@abc"` _(8 chars)_                                   |
| EC16     | Invalid | Missing any special character (G3)                                           | `"Test1234"` _(8 chars)_                                   |
| EC17     | Invalid | Special character present but OUTSIDE allowed set `@$!%*?&` (G4)             | `"Test#123"` _(`#` not in set)_                            |
| EC18     | Invalid | Empty string (B1: required field left blank)                                 | `""`                                                       |
| EC19     | Invalid | Null / field omitted from API body (B1: missing)                             | _(omit `password` key from JSON)_                          |

> **Guidelines applied:** G1 for length range (≥ 8); G3 × 4 for each mandatory character category; G4 split to distinguish "no special char" (EC16) from "special char outside allowed set" (EC17). B1 adds empty and null. Representatives are designed to isolate exactly one violation each.

#### Variable: `confirmPassword` (I4) — Guideline 3 (Must-Be: matches `password`) + B1 — **UI channel only**

| Class ID | Type    | Description                                            | Representative Value              |
| -------- | ------- | ------------------------------------------------------ | --------------------------------- |
| EC20     | Valid   | Matches `password` field exactly                       | Same value as EC11 (`"Test@123"`) |
| EC21     | Invalid | Does not match `password` field (mismatch — per BR-09) | `"DifferentPass@1"`               |
| EC22     | Invalid | Empty confirmPassword (B1: required field left blank)  | `""`                              |

> **Guideline applied:** G3 — binary must-be: confirmPassword must equal password. B1 adds empty case. **UI-only variable — no corresponding API class.**

#### Variable: `auth_token_presence` (I7) — Guideline 3 (Must-Be: public endpoint, no JWT required — SEC-02)

| Class ID | Type  | Description                                                      | Representative Value          |
| -------- | ----- | ---------------------------------------------------------------- | ----------------------------- |
| EC23     | Valid | No `Authorization` header — anonymous request to public endpoint | _(omit Authorization header)_ |

> **Guideline applied:** G3 — SEC-02 compliance: `POST /api/register` must be accessible without a JWT. The valid class is "no token present → HTTP 200".  
> **Note on I5 (`email_uniqueness`) and I6 (`password_char_set`):** These indirect variables are folded into the EP classes of their parent variables — I5 → EC08 (duplicate email), I6 → EC17 (out-of-set special char). They do not require separate EP tables.

#### EP Class Summary

| Variable               | Guideline(s)          | Valid ECs  | Invalid ECs | Total ECs |
| ---------------------- | --------------------- | ---------- | ----------- | --------- |
| `name` (I1)            | G3 + B1 + G4 (SEC-04) | EC01, EC24 | EC02, EC03  | 4         |
| `email` (I2)           | G3 × 2 + B1           | EC04       | EC05–EC10   | 7         |
| `password` (I3)        | G1 + G3×4 + G4 + B1   | EC11       | EC12–EC19   | 9         |
| `confirmPassword` (I4) | G3 + B1               | EC20       | EC21, EC22  | 3         |
| `auth_token` (I7)      | G3                    | EC23       | —           | 1         |
| **TOTAL**              |                       | **6**      | **18**      | **24**    |

### Step 3: Test Case Optimization

#### 3.1 Valid Classes Coverage (Combination Rule)

Core valid classes (EC01, EC04, EC11, EC20, EC23) combined into one single happy-path TC. EC24 (XSS security) is tested separately as FR01-EP-020 due to its distinct output verification requirement.

| TC ID       | Valid ECs Combined               | Test Data Summary                                                                                                      | Channel  |
| ----------- | -------------------------------- | ---------------------------------------------------------------------------------------------------------------------- | -------- |
| FR01-EP-001 | EC01 + EC04 + EC11 + EC20 + EC23 | name=`"Nguyen Van A"`, email=`"newuser@test.com"`, password=`"Test@123"`, confirmPassword=`"Test@123"`, no auth header | UI + API |

#### 3.2 Invalid Classes Coverage (Isolation Rule)

Each TC isolates **exactly ONE** invalid class. All other inputs are drawn from valid classes (EC01, EC04, EC11, EC20, EC23).

**`name` invalid classes:**

| TC ID       | Invalid EC Tested                      | Other Inputs (all valid)                             | Channel  |
| ----------- | -------------------------------------- | ---------------------------------------------------- | -------- |
| FR01-EP-002 | EC02 — empty `name` (`""`)             | email=valid, password=valid, confirm=valid, no token | UI + API |
| FR01-EP-003 | EC03 — null/missing `name` in API body | email=valid, password=valid _(no confirm in API)_    | API      |

**`email` invalid classes:**

| TC ID       | Invalid EC Tested                                      | Other Inputs (all valid)                            | Channel  |
| ----------- | ------------------------------------------------------ | --------------------------------------------------- | -------- |
| FR01-EP-004 | EC05 — invalid email: no `@` (`"invalidemail"`)        | name=valid, password=valid, confirm=valid, no token | UI + API |
| FR01-EP-005 | EC06 — invalid email: no domain (`"user@"`)            | name=valid, password=valid, confirm=valid, no token | UI + API |
| FR01-EP-006 | EC07 — invalid email: no local part (`"@domain.com"`)  | name=valid, password=valid, confirm=valid, no token | UI + API |
| FR01-EP-007 | EC08 — email already exists in DB (`"test@eshop.com"`) | name=valid, password=valid, confirm=valid, no token | UI + API |
| FR01-EP-008 | EC09 — empty email (`""`)                              | name=valid, password=valid, confirm=valid, no token | UI + API |
| FR01-EP-009 | EC10 — null/missing `email` in API body                | name=valid, password=valid _(no confirm in API)_    | API      |

**`password` invalid classes:**

| TC ID       | Invalid EC Tested                                      | Other Inputs (all valid)                                            | Channel  |
| ----------- | ------------------------------------------------------ | ------------------------------------------------------------------- | -------- |
| FR01-EP-010 | EC12 — password < 8 chars (`"Te@1"`, 4 chars)          | name=valid, email=valid, confirm=mirrors invalid password, no token | UI + API |
| FR01-EP-011 | EC13 — missing uppercase (`"test@123"`)                | name=valid, email=valid, confirm=mirrors invalid password, no token | UI + API |
| FR01-EP-012 | EC14 — missing lowercase (`"TEST@123"`)                | name=valid, email=valid, confirm=mirrors invalid password, no token | UI + API |
| FR01-EP-013 | EC15 — missing digit (`"Test@abc"`)                    | name=valid, email=valid, confirm=mirrors invalid password, no token | UI + API |
| FR01-EP-014 | EC16 — missing special char entirely (`"Test1234"`)    | name=valid, email=valid, confirm=mirrors invalid password, no token | UI + API |
| FR01-EP-015 | EC17 — special char outside allowed set (`"Test#123"`) | name=valid, email=valid, confirm=mirrors invalid password, no token | UI + API |
| FR01-EP-016 | EC18 — empty password (`""`)                           | name=valid, email=valid, confirm=mirrors invalid password, no token | UI + API |
| FR01-EP-017 | EC19 — null/missing `password` in API body             | name=valid, email=valid _(no confirm in API)_                       | API      |

> **Note on FR01-EP-010 to FR01-EP-016:** `confirmPassword` mirrors the invalid password value to avoid triggering a second invalid class (mismatch). The only invalid condition under test is the password rule itself.

**`confirmPassword` invalid classes (UI only):**

| TC ID       | Invalid EC Tested                                       | Other Inputs (all valid)                                 | Channel |
| ----------- | ------------------------------------------------------- | -------------------------------------------------------- | ------- |
| FR01-EP-018 | EC21 — confirmPassword ≠ password (`"DifferentPass@1"`) | name=valid, email=valid, password=`"Test@123"`, no token | UI      |
| FR01-EP-019 | EC22 — empty confirmPassword (`""`)                     | name=valid, email=valid, password=`"Test@123"`, no token | UI      |

**`name` security class (EC24 — UI + DOM channel):**

| TC ID       | EC Tested                                                      | Other Inputs (all valid)                                           | Channel  |
| ----------- | -------------------------------------------------------------- | ------------------------------------------------------------------ | -------- |
| FR01-EP-020 | EC24 — `name` with XSS payload (`"<script>alert(1)</script>"`) | email=valid, password=`"Test@123"`, confirm=`"Test@123"`, no token | UI + DOM |

> **Expected result for FR01-EP-020:** HTTP 200 — system accepts registration. On UI, the name is displayed as escaped text; no script executes in the browser. Verify via DevTools DOM inspection: confirm name rendered as `&lt;script&gt;alert(1)&lt;/script&gt;` (per SEC-04, O14).

#### 3.3 EC Coverage Summary

| Total ECs | Valid ECs | Invalid ECs | TCs for Valid | TCs for Invalid | Security TCs | Total TCs |
| --------- | --------- | ----------- | ------------- | --------------- | ------------ | --------- |
| 24        | 6         | 18          | 1             | 18              | 1            | **20**    |

## C. Boundary Value Analysis (BVA Walkthrough)

For every continuous or ordered variable identified in the previous step, we apply Step 4: The 9-point Boundary Value Analysis strategy to thoroughly test the edges of valid and invalid classes.

### Boundary Variables Identified

| Variable          | Data Type               | LB                                           | UB                                           | Increment | Note                                  |
| ----------------- | ----------------------- | -------------------------------------------- | -------------------------------------------- | --------- | ------------------------------------- |
| `password` length | integer (string length) | 8 chars (explicit, per FR-01)                | Unspecified in SRS                           | 1 char    | Must also satisfy all char-type rules |
| `name` length     | integer (string length) | 1 char (implicit: non-empty, per FR-01)      | Unspecified in SRS (assumed DB VARCHAR ~255) | 1 char    | SRS only states "non-empty"           |
| `email` length    | integer (string length) | Implicit (~5 chars for minimum valid format) | Unspecified in SRS (assumed DB VARCHAR ~255) | 1 char    | SRS specifies format, not length      |

> **Note on UB:** For all 3 variables, no explicit upper bound is stated in the SRS. The value 255 is used as the assumed architectural boundary (typical SQLite/MySQL VARCHAR default). UB-1/UB/UB+1 points are tested against this assumed value, and the +α point tests the system beyond any reasonable limit.

### BVA Table 1: `password` (string length)

**Constraint:** length ≥ 8 characters (per FR-01, BR-04)  
**LB = 8 | UB = unspecified | +α = 300 chars**

> **Isolation:** All other inputs are valid in each TC (name=`"Nguyen Van A"`, unique email, `confirmPassword` mirrors password value, no auth token).

| TC ID        | BVA Point          | Test Value                                         | Length | Valid/Invalid    | Expected Result                                                           |
| ------------ | ------------------ | -------------------------------------------------- | ------ | ---------------- | ------------------------------------------------------------------------- |
| FR01-BVA-001 | −α (absolute min)  | `""`                                               | 0      | Invalid          | Reject — HTTP 400: password is required (per BR-04, BR-11)                |
| FR01-BVA-002 | LB−1               | `"Te@1aBc"` _(all char types, 7 chars)_            | 7      | Invalid          | Reject — HTTP 400: password must be at least 8 characters (per FR-01)     |
| FR01-BVA-003 | LB (exact minimum) | `"Test@123"` _(all char types, 8 chars)_           | 8      | **Valid**        | Accept — HTTP 200: `{"message": "User registered successfully", "id": N}` |
| FR01-BVA-004 | LB+1               | `"Test@1234"` _(9 chars)_                          | 9      | **Valid**        | Accept — HTTP 200 (per FR-01)                                             |
| FR01-BVA-005 | Nominal            | `"TestPassword1!"` _(15 chars)_                    | 15     | **Valid**        | Accept — HTTP 200 (per FR-01)                                             |
| FR01-BVA-006 | +α (very long)     | `"Aa1@" + "A"×296` _(300 chars, meets char rules)_ | 300    | Invalid (likely) | Reject — HTTP 400 or 500 (implicit DB/system limit exceeded)              |

> **Note on FR01-BVA-003, 004, 005:** These are success cases — each must use a **unique email** not previously registered in the DB (e.g., `bva003@test.com`, `bva004@test.com`, `bva005@test.com`). Clean up after execution (delete test user via Admin API or DB).

### BVA Table 2: `name` (string length)

**Constraint:** length ≥ 1 (non-empty, per FR-01, BR-01) | UB = unspecified in SRS (assumed DB VARCHAR = 255)  
**LB = 1 | UB = 255 (assumed) | +α = 500 chars**

> **Note:** Since LB = 1, the LB−1 point (0 chars) is identical to the −α point (empty string). Both are merged into FR01-BVA-007.  
> **Isolation:** All other inputs valid (unique email per TC, password=`"Test@123"`, confirmPassword=`"Test@123"`, no auth token).

| TC ID        | BVA Point                | Test Value       | Length | Valid/Invalid    | Expected Result                                                         |
| ------------ | ------------------------ | ---------------- | ------ | ---------------- | ----------------------------------------------------------------------- |
| FR01-BVA-007 | −α / LB−1 (empty)        | `""`             | 0      | Invalid          | Reject — HTTP 400: name is required (per FR-01, BR-01)                  |
| FR01-BVA-008 | LB (exact minimum)       | `"A"`            | 1      | **Valid**        | Accept — HTTP 200 (per FR-01)                                           |
| FR01-BVA-009 | LB+1                     | `"AB"`           | 2      | **Valid**        | Accept — HTTP 200 (per FR-01)                                           |
| FR01-BVA-010 | Nominal                  | `"Nguyen Van A"` | 12     | **Valid**        | Accept — HTTP 200 (per FR-01)                                           |
| FR01-BVA-011 | UB−1 (assumed 254 chars) | `"A"×254`        | 254    | **Valid**        | Accept — HTTP 200 (within assumed DB VARCHAR limit)                     |
| FR01-BVA-012 | UB (assumed 255 chars)   | `"A"×255`        | 255    | **Valid**        | Accept — HTTP 200 (at assumed DB VARCHAR limit)                         |
| FR01-BVA-013 | UB+1 (assumed 256 chars) | `"A"×256`        | 256    | Invalid (likely) | Reject or truncate — behavior depends on DB schema (not defined in SRS) |
| FR01-BVA-014 | +α (very long)           | `"A"×500`        | 500    | Invalid (likely) | Reject or server error — exceeds any reasonable DB limit                |

### BVA Table 3: `email` (string length)

**Constraint:** LB = implicit (minimum valid format ~5 chars e.g. `a@b.c`) | UB = unspecified in SRS (assumed DB VARCHAR = 255)  
**+α = 300 chars**

> **Note:** The SRS specifies email **format** validity, not explicit length constraints. This BVA table targets **architectural** boundaries only. LB boundary points are omitted since format validity already governs the minimum (covered in EP). UB tests focus on the assumed DB VARCHAR limit.  
> **Isolation:** All other inputs valid (name=`"Nguyen Van A"`, unique email per TC, password=`"Test@123"`, confirmPassword=`"Test@123"`, no auth token).  
> **Test email construction:** `"a"×(n−9) + "@test.com"` produces a valid-format email of exactly n characters.

| TC ID        | BVA Point                | Test Value                            | Length | Valid/Invalid    | Expected Result                                    |
| ------------ | ------------------------ | ------------------------------------- | ------ | ---------------- | -------------------------------------------------- |
| FR01-BVA-015 | Nominal                  | `"newuser@test.com"`                  | 16     | **Valid**        | Accept — HTTP 200 (per FR-01)                      |
| FR01-BVA-016 | UB (assumed 255 chars)   | `"a"×246 + "@test.com"` _(255 chars)_ | 255    | **Valid**        | Accept — HTTP 200 (at assumed DB VARCHAR limit)    |
| FR01-BVA-017 | UB+1 (assumed 256 chars) | `"a"×247 + "@test.com"` _(256 chars)_ | 256    | Invalid (likely) | Reject or truncate — behavior depends on DB schema |
| FR01-BVA-018 | +α (very long)           | `"a"×291 + "@test.com"` _(300 chars)_ | 300    | Invalid (likely) | Reject or server error                             |

### BVA Summary

| Variable          | LB       | UB (assumed)  | BVA Points Generated                           | Valid TCs | Invalid TCs | Total BVA TCs |
| ----------------- | -------- | ------------- | ---------------------------------------------- | --------- | ----------- | ------------- |
| `password` length | 8        | unspecified   | −α, LB−1, LB, LB+1, Nominal, +α                | 3         | 3           | 6             |
| `name` length     | 1        | 255 (assumed) | −α/LB−1, LB, LB+1, Nominal, UB−1, UB, UB+1, +α | 5         | 3           | 8             |
| `email` length    | implicit | 255 (assumed) | Nominal, UB, UB+1, +α                          | 2         | 2           | 4             |
| **Total**         |          |               |                                                | **10**    | **8**       | **18**        |

## D. Coverage Review & AI Gap Analysis

### 1. EP Guidelines Compliance

| Variable               | Guideline(s) Applied         | Valid ECs      | Invalid ECs | Verdict |
| ---------------------- | ---------------------------- | -------------- | ----------- | ------- |
| `name` (I1)            | G3 + B1 + G4 (SEC-04)        | 2 (EC01, EC24) | 2 (EC02–03) | PASS    |
| `email` (I2)           | G3 × 2 + G4 (sub-split) + B1 | 1 (EC04)       | 6 (EC05–10) | PASS    |
| `password` (I3)        | G1 + G3 × 4 + G4 + B1        | 1 (EC11)       | 8 (EC12–19) | PASS    |
| `confirmPassword` (I4) | G3 + B1 (UI only)            | 1 (EC20)       | 2 (EC21–22) | PASS    |
| `auth_token` (I7)      | G3 (SEC-02)                  | 1 (EC23)       | 0           | PASS    |

### 2. Missing Classes Detection

#### B1 — Empty / Null Classes

| Variable          | Empty String EC | Null/Missing EC | Status |
| ----------------- | --------------- | --------------- | ------ |
| `name`            | EC02            | EC03            | PASS   |
| `email`           | EC09            | EC10            | PASS   |
| `password`        | EC18            | EC19            | PASS   |
| `confirmPassword` | EC22            | N/A (UI-only)   | PASS   |

#### B2 — Cross-field Classes

| Cross-field Dependency       | EC Present | Status |
| ---------------------------- | ---------- | ------ |
| `confirmPassword ≠ password` | EC21       | PASS   |

#### B3 — DB-State Classes

| DB-State Dependency          | EC Present | Status |
| ---------------------------- | ---------- | ------ |
| `email` already exists in DB | EC08       | PASS   |

#### B4 — Security-Specific Classes

| Security Class                                             | EC Present                | Status          |
| ---------------------------------------------------------- | ------------------------- | --------------- |
| Public endpoint: no JWT required (SEC-02)                  | EC23                      | PASS            |
| XSS payload in `name` field (SEC-04: name displayed on UI) | EC24 (added after review) | PASS (resolved) |

#### B5 — Boundary Edge Cases

| Edge Case                                         | EC Present | Status |
| ------------------------------------------------- | ---------- | ------ |
| Password: special char in allowed set (`@$!%*?&`) | EC11       | PASS   |
| Password: special char OUTSIDE allowed set        | EC17       | PASS   |

#### B6 — State-Transition Classes

FR-01 is a single-step form — no multi-step workflow. **N/A**

#### B7 — Implicit / Architectural Classes

| Architectural Boundary              | BVA TC Present                  | Status |
| ----------------------------------- | ------------------------------- | ------ |
| `password` length DB VARCHAR (~255) | +α FR01-BVA-006                 | PASS   |
| `name` length DB VARCHAR (~255)     | UB/UB+1/+α FR01-BVA-012/013/014 | PASS   |
| `email` length DB VARCHAR (~255)    | UB/UB+1/+α FR01-BVA-016/017/018 | PASS   |

### 3. Rule Violations Found

#### Isolation Rule Scan (all 18 invalid TCs + FR01-EP-020)

All 18 invalid TCs verified: each contains exactly 1 invalid input; all other inputs drawn from valid classes.

- FR01-EP-010 to 016: `confirmPassword` mirrors invalid password value — correctly remains in EC20 ("matches password") since EC20's condition is "matches `password` field", not "is a strong password".
- FR01-EP-020: EC24 is a **valid** class (security payload accepted by system) — no isolation concern.

**No Isolation Rule violations found.**

#### Combination Rule Scan

| TC ID       | Valid ECs Combined       | All Valid ECs Covered? | Verdict |
| ----------- | ------------------------ | ---------------------- | ------- |
| FR01-EP-001 | EC01+EC04+EC11+EC20+EC23 | Covers 5 of 6          | PASS    |
| FR01-EP-020 | EC24 (security split)    | Covers EC24 separately | PASS    |

> EC24 is tested separately (FR01-EP-020) because its output verification (DOM/XSS check) differs from the standard success path (FR01-EP-001). This is a justified exception to the combination rule.

### 4. BVA Completeness

| Variable          | BVA Table | Points Generated        | −α   | +α   | Missing           | Verdict |
| ----------------- | --------- | ----------------------- | ---- | ---- | ----------------- | ------- |
| `password` length | Yes       | 6 (FR01-BVA-001 to 006) | PASS | PASS | None              | PASS    |
| `name` length     | Yes       | 8 (FR01-BVA-007 to 014) | PASS | PASS | None              | PASS    |
| `email` length    | Yes       | 4 (FR01-BVA-015 to 018) | N/A  | PASS | LB N/A (implicit) | PASS    |
| Date fields       | N/A       | —                       | —    | —    | —                 | N/A     |
| Numeric fields    | N/A       | —                       | —    | —    | —                 | N/A     |

### 5. AI Gap Analysis

#### What AI Generated Correctly

1. Identified `confirmPassword` as **UI-only** (not in API body) — prevents incorrect API test design
2. Applied **G4 split** to `password` special chars — EC16 (missing) vs EC17 (out-of-set) — flagged as commonly missed in skill Section 7
3. Applied **B1** (empty + null) to all required fields without omission
4. Applied **B2** (cross-field) with EC21 — confirmPassword mismatch class
5. Applied **B3** (DB-state) with EC08 — duplicate email class
6. Implemented the **"mirror" approach** for password invalid TCs to prevent defect masking in confirmPassword
7. Applied **BVA to string length fields** — `name`, `email`, `password` all covered
8. Made **architectural assumption** of DB VARCHAR=255 for implicit UB boundaries
9. Constructed **valid-format boundary emails** precisely using `"a"×(n−9) + "@test.com"` formula

#### What AI Missed

| #   | Missing Item                                      | Description                                                                                                                                                                      | Root Cause                                                                                                                                                                       |
| --- | ------------------------------------------------- | -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- | -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| 1   | **EC24 — XSS payload in `name`**                  | No EC for `name = "<script>alert(1)</script>"`. Output variable O14 (XSS safety) was correctly identified but did not translate into a G4 input split.                           | **Feature complexity** — XSS is an output-side security requirement. AI correctly mapped O14 as an output but did not auto-generate the corresponding input EC via G4 splitting. |
| 2   | **Self-generated EC17 (out-of-set special char)** | EC17 was generated correctly, but only after explicit human prompting referencing the allowed set `@$!%*?&`. Without the prompt hint, this class would likely have been omitted. | **AI limitation** — the skill itself flags EC17 as a "commonly missed" class (Section 7). Human compensated via targeted prompt.                                                 |

#### Root Cause Summary

| Category           | Count | Description                                                                      |
| ------------------ | ----- | -------------------------------------------------------------------------------- |
| Feature complexity | 1     | EC24 (XSS): output-side SEC-04 requirement not auto-translated to input EC       |
| AI limitation      | 1     | EC17 (out-of-set special char) required explicit human prompting to be generated |
| Prompt quality     | 0     | No gaps attributable purely to insufficient context                              |

#### Lesson Learned

1. **AI struggles with Output-to-Input reverse engineering (Security/State constraints):** While the AI effectively maps validation rules directly tied to input fields (e.g., length, missing data), it fails to auto-generate Equivalence Classes for constraints defined on the output side or UI rendering side (e.g., SEC-04 XSS escaping).
   - **Mitigation Strategy for future FRs:** The QA Engineer must manually cross-reference the `Output Variables` table against the generated `Equivalence Classes`. If an output behavior (like security escaping, specific error UI, or DB state change) does not have a dedicated input trigger in the EC table, the human must manually prompt the AI to add it via a G4 split.
2. **AI exhibits "Happy Path Bias" and requires explicit prompting for nuanced Negative Testing:** The AI successfully generates standard invalid classes (B1 empty/null, G1 length bounds), but routinely misses edge-case negative classes (e.g., characters _outside_ a specific allowed set like EC17).
   - **Mitigation Strategy for future FRs:** Do not rely on the AI's autonomous generation for complex domain constraints. Always review the "Common AI Blind Spots" or "EShop-Specific EP Patterns" in the skill instructions and explicitly force the AI to include these specific invalid classes in the initial Prompt.

### 6. Final Summary After Review

| Category      | Before Review | Added            | After Review |
| ------------- | ------------- | ---------------- | ------------ |
| Valid ECs     | 5             | +1 (EC24)        | **6**        |
| Invalid ECs   | 18            | 0                | **18**       |
| BVA Points    | 18            | 0                | **18**       |
| EP TCs        | 19            | +1 (FR01-EP-020) | **20**       |
| BVA TCs       | 18            | 0                | **18**       |
| **Total TCs** | **37**        | **+1**           | **38**       |

# FR-07 — Shopping Cart (Pool B)

## A. Requirement Analysis

To begin Domain Testing, we first analyze the requirement to identify all explicit and implicit constraints, actors, and business rules governing **Shopping Cart**. This step forms the foundation for extracting variables.

### 1. Feature Overview

| Attribute         | Value                                                                                   |
| ----------------- | --------------------------------------------------------------------------------------- |
| Feature ID        | FR-07                                                                                   |
| Feature Name      | Shopping Cart                                                                           |
| Test Layer        | Both (Web UI + API)                                                                     |
| Entry Point (UI)  | `http://localhost:5173/cart`                                                            |
| Entry Point (API) | `GET http://localhost:3000/api/cart` · `POST http://localhost:3000/api/cart` (add item) |
| Actors            | Logged-in User only                                                                     |
| Auth Required     | Yes — User JWT (`Authorization: Bearer <token>`)                                        |

### 2. Input Fields & Constraints

| Field/Param | Layer    | Type    | Explicit Constraints (from SRS)                                                                                | Implicit Constraints (Architecture/DB)                                                 | API Param Name |
| ----------- | -------- | ------- | -------------------------------------------------------------------------------------------------------------- | -------------------------------------------------------------------------------------- | -------------- |
| `id`        | API      | integer | Required; must reference an existing product in DB                                                             | Must be a positive integer; non-existent ID should be rejected                         | `id`           |
| `name`      | API      | string  | Required; product name displayed in cart view                                                                  | Max length constrained by DB column (typically 255 chars); must not be empty           | `name`         |
| `price`     | API      | number  | Required; unit price of the product (per FR-15: price > 0); displayed in ₫ with thousand-separator (per FR-21) | Must be a positive numeric value; floating-point precision TBD by DB schema            | `price`        |
| `quantity`  | API + UI | integer | Required; must be a positive integer (≥ 1) per FR-06; UI has +/- buttons to adjust quantity                    | Minimum = 1; no explicit maximum specified in SRS (implicit: system/DB capacity limit) | `quantity`     |
| `JWT Token` | Header   | string  | Required; valid user JWT must be sent in `Authorization: Bearer <token>` header (per SEC-02)                   | Token must be unexpired and have a valid signature                                     | Header only    |

> **Note on duplicate-product rule (BR-03):** When the same product `id` is added again via `POST /api/cart`, the quantity is merged (incremented), **not** inserted as a new row.

### 3. Business Rules

- **[BR-01]** The cart is only accessible to authenticated users. An unauthenticated request to any cart API must receive HTTP 401. (per FR-07, SEC-02)
- **[BR-02]** The cart page must display items with the following columns: **Product**, **Unit Price**, **Quantity** (with +/− adjustment buttons), **Subtotal**, **Action** (delete). (per FR-07)
- **[BR-03]** Adding the same product to the cart again must **increment quantity**, not create a new duplicate row. (per FR-07)
- **[BR-04]** `quantity` must be a positive integer ≥ 1. A quantity of 0 or negative is invalid. (per FR-06, FR-07)
- **[BR-05]** The delete action for a cart item must trigger a **confirmation dialog** before executing. The item is only removed after the user confirms. (per FR-07, FR-24)
- **[BR-06]** The cart page must display a **"Continue Shopping"** button (`Tiếp tục mua sắm`) that navigates back to the homepage. (per FR-07)
- **[BR-07]** The total amount label must read exactly **"Tổng cộng"** — not "Tổng tạm tính" or any other label. (per FR-07)
- **[BR-08]** When the cart is empty, the page must display an **illustration image and a clear empty-state message**. (per FR-07, FR-24)
- **[BR-09]** All prices must be displayed with the `₫` symbol and thousand-separator formatting (e.g., `100,000 ₫`). (per FR-21)
- **[BR-10]** After clicking "Add to Cart" from the Product Detail page (FR-06), a **visual feedback** must appear: toast notification or cart badge update. (per FR-06, FR-24)
- **[BR-11]** The cart badge in the Navigation Bar must display the **current count** of items in the cart. (per FR-23)
- **[BR-12]** The cart page must be accessible via a **Breadcrumb** navigation element. (per FR-23)
- **[BR-13]** The Navbar must **highlight** the "Giỏ hàng" (Cart) link when the user is on the cart page. (per FR-23)
- **[BR-14]** The cart page must have exactly **one `<h1>` tag** describing the page content. (per FR-21, FR-05)
- **[BR-15]** All product images displayed in the cart must have a non-empty `alt` attribute. (per FR-24)
- **[BR-16]** User input (e.g., product names) displayed in the cart must be **safely escaped** — no raw HTML rendering / XSS risk. (per SEC-04)

### 4. Expected Outputs

#### 4.1 Success Paths

**GET /api/cart — Retrieve cart:**

- HTTP: `200 OK`
- Response body: JSON array of cart items, each with `id`, `name`, `price`, `quantity`
- UI: Cart page renders table with all cart items, subtotals per row, and grand total labeled "Tổng cộng"
- DB: No change (read-only)

**POST /api/cart — Add item (new product):**

- HTTP: `200 OK` (or `201 Created` — confirm against actual API)
- Response body: Updated cart state or success message
- UI: Cart badge in navbar increments; toast notification appears on product detail page
- DB: New row inserted into cart table for this user + product

**POST /api/cart — Add item (existing product, merge):**

- HTTP: `200 OK`
- Response body: Updated cart with incremented quantity
- UI: Cart badge updates; no duplicate row created in cart display
- DB: `quantity` field of existing cart row is incremented by the added amount

**Update quantity via +/- buttons (UI):**

- HTTP: Underlying API call (likely `PUT /api/cart/:id` or re-POST — to be confirmed via testing)
- UI: Subtotal for that row updates immediately; grand total updates
- DB: `quantity` updated for the corresponding cart item

**Delete item (after confirm dialog):**

- HTTP: Underlying DELETE API (to be confirmed via testing)
- UI: Item removed from cart table; grand total recalculates; if last item removed → empty state shown
- DB: Cart row deleted

**Empty cart state:**

- UI: Illustration image shown + friendly empty-state message; "Continue Shopping" button visible
- DB: No cart rows for this user

#### 4.2 Failure Paths

- **No JWT token** → `GET /api/cart` or `POST /api/cart`: HTTP **401 Unauthorized** (per SEC-02)
- **Invalid/expired JWT token** → HTTP **401 Unauthorized**
- **`quantity` ≤ 0** → System must reject or treat as invalid input; expected: HTTP 4xx + error message (exact behavior TBD from SRS — flagged as high-risk)
- **`id` references non-existent product** → Expected: HTTP 4xx + error message (per implicit constraint)
- **Missing required field** (`id`, `name`, `price`, or `quantity` absent in POST body) → HTTP 4xx + validation error
- **Delete without confirmation** → Action must NOT proceed if user dismisses the confirm dialog

### 5. GUI Requirements Applicable (FR-21–24)

> **Platform:** Web UI — HTML/DOM semantics checks apply.

- **[GUI-01]** Cart page must contain exactly **one `<h1>` tag** with descriptive text. (per FR-21)
- **[GUI-02]** All product images in the cart must have a **non-empty `alt` attribute**. (per FR-24)
- **[GUI-03]** The cart link in the Navbar must display a **badge** showing the number of items in the cart. (per FR-23)
- **[GUI-04]** The Navbar must **highlight** the cart navigation item when the user is on the cart page. (per FR-23)
- **[GUI-05]** A **Breadcrumb** component must be present on the cart page. (per FR-23)
- **[GUI-06]** When the cart is empty, an **illustration + friendly message** (Empty State) must be displayed. (per FR-07, FR-24)
- **[GUI-07]** Deleting a cart item must trigger a **confirm dialog** before the delete is executed. (per FR-07, FR-24)
- **[GUI-08]** After clicking "Add to Cart" from product detail, a **toast notification or badge update** must appear. (per FR-06, FR-24)
- **[GUI-09]** The total label must read **exactly** "Tổng cộng" (not "Tổng tạm tính" or similar). (per FR-07)
- **[GUI-10]** Positive action buttons (e.g., "Continue Shopping", "Checkout") use **blue color**; destructive action buttons (delete) use **red color**. (per FR-21)
- **[GUI-11]** All currency values must be formatted as `xxx,xxx ₫` with thousand-separator. (per FR-21)
- **[GUI-12]** Tab order in the cart form/table must flow **top-to-bottom, left-to-right**. (per FR-21)
- **[GUI-13]** No Step Indicator is required for the cart page (single-step, not a multi-step form). (per FR-22 — N/A for this FR)

### 6. Security Requirements Applicable (SEC-xx)

- **[SEC-02]** All cart API endpoints (`GET /api/cart`, `POST /api/cart`) require a **valid JWT token**. Requests without a token must return HTTP 401. (per SEC-02)
- **[SEC-04]** Product names and user-provided data displayed in the cart must be **HTML-escaped**. Injecting `<script>alert(1)</script>` as a product name must NOT execute. (per SEC-04)
- _SEC-01, SEC-03, SEC-05, SEC-06, SEC-07 — Not directly applicable to Shopping Cart FR-07._

### 7. Notes for Domain Testing

- **Input variables identified:** `id` (integer), `name` (string), `price` (numeric), `quantity` (positive integer), `JWT token` (auth state: absent / valid user / valid admin)
- **Output variables identified:** HTTP status code, response body (cart item list / success message / error message), UI cart table state, cart badge count, DB cart row state (created / updated / deleted), empty-state UI display, confirm dialog behavior
- **Boundary candidates:**
  - `quantity`: boundary at 0 (invalid), 1 (lower bound — valid), very large values (upper boundary — system limit)
  - `price`: boundary at 0 (should be rejected per FR-15), positive values
  - `id`: valid existing ID vs. non-existent ID
- **High-risk areas:**
  - **Quantity = 0:** SRS does not explicitly state behavior when quantity is set to 0 via the +/- UI buttons — does it remove the item, or reject the action? This is a known gap.
  - **Duplicate product add:** Merge vs. new-row behavior (BR-03) — commonly mis-implemented
  - **Total label text:** Exact string "Tổng cộng" vs. "Tổng tạm tính" — easy to miss in testing
  - **JWT not sent:** Cart APIs must be protected (SEC-02) — test all 3 auth states
  - **Confirm dialog bypass:** API can be called directly without the UI confirm dialog — test via API channel
- **AI blind spot warnings:**
  - Quantity = 0 (edge between "reduce to zero" and "remove item") — AI might not generate this class
  - Adding a product whose `id` does not exist in the product table — implicit constraint, not stated in FR-07 body
  - Badge count accuracy after multi-item add/delete operations
  - XSS via product name in cart display (SEC-04) — often overlooked in cart feature analysis

## B. Domain Analysis (Equivalence Partitioning Walkthrough)

Based on the identified constraints, we proceed to Step 1: Identifying Variables (both input and output) and Step 2 & 3: Dividing them into valid and invalid Equivalence Classes applying the 4 EP guidelines.

### Step 1: Input & Output Variable Identification

#### 1.1 Input Variables

##### Direct Inputs (UI Form / API Body)

| #   | Variable   | Source              | Type    | Description                                                           |
| --- | ---------- | ------------------- | ------- | --------------------------------------------------------------------- |
| I1  | `id`       | API body            | integer | ID of the product to add to cart. Must reference an existing product. |
| I2  | `name`     | API body            | string  | Name of the product. Displayed in cart UI.                            |
| I3  | `price`    | API body            | number  | Unit price of the product. Must be > 0 per FR-15.                     |
| I4  | `quantity` | API body + UI (+/-) | integer | Number of units to add/set. Must be ≥ 1 per FR-06.                    |

##### Indirect Inputs (Hidden / System State)

| #   | Variable                    | Source                  | Type       | Description                                                                                                      |
| --- | --------------------------- | ----------------------- | ---------- | ---------------------------------------------------------------------------------------------------------------- |
| I5  | `auth_token`                | HTTP request header     | JWT string | `Authorization: Bearer <token>` header. Absent / valid user JWT / valid admin JWT. Required by SEC-02.           |
| I6  | `user_auth_state`           | Session / token payload | enum       | Derived from token: `anonymous` (no token) / `authenticated_user` / `authenticated_admin`. Drives 401 vs 200.    |
| I7  | `duplicate_product_in_cart` | DB state                | boolean    | Whether the same `id` already exists as a row in this user's cart. Determines merge vs. insert behavior (BR-03). |
| I8  | `cart_empty_state`          | DB state                | boolean    | Whether the user's cart currently has zero items. Determines empty-state UI display (BR-08).                     |
| I9  | `confirm_dialog_response`   | UI state (user action)  | boolean    | User's response to the delete confirmation dialog: `confirmed` or `dismissed` (BR-05).                           |
| I10 | `product_exists_in_db`      | DB state                | boolean    | Whether the given `id` actually exists in the products table (implicit constraint from API spec).                |

#### 1.2 Output Variables

##### Direct Outputs (Visible)

| #   | Variable                   | Channel | Description                                                                                      |
| --- | -------------------------- | ------- | ------------------------------------------------------------------------------------------------ |
| O1  | HTTP status code           | API     | `200 OK` (success) / `401 Unauthorized` (no/invalid token) / `4xx` (invalid input/missing field) |
| O2  | Response body (JSON)       | API     | Array of cart items on GET; success message or updated cart on POST; error message on failure    |
| O3  | Cart item table            | UI      | Table rendered with columns: Product, Unit Price, Quantity (+/- buttons), Subtotal, Action       |
| O4  | Row subtotal display       | UI      | Per-row `price × quantity` calculation displayed as formatted `₫` value (per FR-21, BR-09)       |
| O5  | Grand total display        | UI      | Sum of all row subtotals, labeled exactly **"Tổng cộng"** (per BR-07)                            |
| O6  | Toast notification         | UI      | Visual feedback shown after "Add to Cart" action on Product Detail page (per BR-10, FR-24)       |
| O7  | Error message              | UI      | Validation or auth error displayed on screen (above submit — per FR-22 if applicable)            |
| O8  | Empty state display        | UI      | Illustration + friendly text when cart has zero items (per BR-08, FR-24)                         |
| O9  | Confirm dialog             | UI      | Modal/dialog that appears before item deletion is executed (per BR-05)                           |
| O10 | "Continue Shopping" button | UI      | Button visible on cart page; navigates back to homepage (per BR-06)                              |

##### Indirect Outputs (Hidden / State Changes)

| #   | Variable                    | Channel    | Description                                                                                       |
| --- | --------------------------- | ---------- | ------------------------------------------------------------------------------------------------- |
| O11 | DB cart row — insert        | State      | After POST with new `id`: new row created in cart table for user + product (per BR-03)            |
| O12 | DB cart row — update        | State      | After POST with existing `id`: `quantity` field incremented, no duplicate row (per BR-03)         |
| O13 | DB cart row — delete        | State      | After confirmed delete: cart row removed from DB (per BR-05)                                      |
| O14 | DB cart row — qty update    | State      | After +/- button action in UI: `quantity` field updated in DB (per BR-04)                         |
| O15 | Cart badge count (navbar)   | UI + State | Integer badge on navbar "Giỏ hàng" link, reflects current number of cart items (per BR-11, FR-23) |
| O16 | Navbar highlight state      | DOM        | "Giỏ hàng" nav link is visually highlighted/active when on cart page (per BR-13, FR-23)           |
| O17 | DOM: `<h1>` count           | DOM        | Cart page has exactly one `<h1>` tag (per BR-14, FR-21)                                           |
| O18 | DOM: image `alt` attributes | DOM        | All `<img>` elements in cart have non-empty `alt` attributes (per BR-15, FR-24)                   |
| O19 | DOM: breadcrumb presence    | DOM        | Breadcrumb navigation element exists on cart page (per BR-12, FR-23)                              |
| O20 | XSS safety in product name  | DOM        | Product name displayed in cart is HTML-escaped; `<script>` tags not executed (per BR-16, SEC-04)  |

#### 1.3 Variable Summary for EP

- **Total inputs identified:** 10 (4 direct + 6 indirect)
- **Total outputs identified:** 20 (10 direct + 10 indirect)
- **Variables requiring EP:**
  - `quantity` (I4): Ordered numeric — valid range ≥ 1; BVA also applies
  - `price` (I3): Ordered numeric — must be > 0; BVA also applies
  - `id` (I1): Discrete — valid (exists in DB) vs. invalid (non-existent)
  - `name` (I2): String — valid (non-empty) vs. invalid (empty/too long)
  - `auth_token` (I5): Tri-state enum — absent / valid-user / valid-admin
  - `user_auth_state` (I6): Enum — anonymous vs. authenticated
  - `duplicate_product_in_cart` (I7): Boolean — first-time add (insert) vs. repeat-add (merge)
  - `cart_empty_state` (I8): Boolean — non-empty cart vs. empty cart
  - `confirm_dialog_response` (I9): Boolean — confirmed vs. dismissed
  - `product_exists_in_db` (I10): Boolean — exists vs. non-existent
- **Boundary candidates:**
  - `quantity` (I4): −∞ → 0 (invalid) → 1 (LB, valid) → 2 (LB+1) → large number (UB TBD)
  - `price` (I3): 0 (invalid LB) → positive number (valid)
  - `name` (I2): empty string (invalid) → 1 char (LB) → 255 chars (UB) → 256+ chars (UB+1)
- **AI Blind Spot Checklist (verified):**

| Blind Spot                                                  | Variable Added? |
| :---------------------------------------------------------- | :-------------- |
| `duplicate_product_in_cart` state (merge vs. insert)        | I7              |
| `cart_item_count` badge as output                           | O15             |
| `confirm_dialog_response` as an input (dismiss = no delete) | I9              |
| XSS safety in product name output (SEC-04)                  | O20             |
| `product_exists_in_db` — non-existent id                    | I10             |
| `cart_empty_state` — drives empty-state UI rendering        | I8              |

### Step 2: Equivalence Classes

#### Variable I1: `id` — G3 (Must-Be: valid reference) + G4 (splitting by type)

> G3 applied: id must exist in DB (boolean condition: exists / not-exists).  
> G4 applied: split further by data type (integer vs. non-integer).

| Class ID | Type    | Description                                     | Representative |
| -------- | ------- | ----------------------------------------------- | -------------- |
| EC01     | Valid   | Positive integer, references existing product   | `1`            |
| EC02     | Invalid | Positive integer, NOT in products DB table      | `99999`        |
| EC03     | Invalid | `id` field is null or missing from request body | `null`         |
| EC04     | Invalid | Non-integer type (string passed instead of int) | `"abc"`        |

#### Variable I2: `name` — G3 (Must-Be: non-empty) + G1 (length ≤ 255 implicit) + G4 (XSS split)

> G3 applied: must be non-empty.  
> G1 applied: implicit DB VARCHAR(255) upper bound.  
> G4 applied: split valid class into normal string vs. XSS payload — same input domain, different output behavior (SEC-04 tests output safety, not input rejection).

| Class ID | Type       | Description                                               | Representative                |
| -------- | ---------- | --------------------------------------------------------- | ----------------------------- |
| EC05     | Valid      | Non-empty string, length 1–255 chars                      | `"Laptop ABC"`                |
| EC06     | Valid (G4) | XSS-payload string — tests safe output rendering (SEC-04) | `"<script>alert(1)</script>"` |
| EC07     | Invalid    | Empty string `""`                                         | `""`                          |
| EC08     | Invalid    | `name` field null or missing from request body            | `null`                        |
| EC09     | Invalid    | String exceeds DB limit (> 255 chars)                     | 300-character string          |

#### Variable I3: `price` — G1 (Continuous range: price > 0)

> G1 applied: 1 valid class (> 0) + 2 invalid classes (= 0, < 0).  
> G3 applied additionally: null/missing is a separate must-be violation.

| Class ID | Type    | Description                   | Representative |
| -------- | ------- | ----------------------------- | -------------- |
| EC10     | Valid   | Positive number (> 0)         | `100000`       |
| EC11     | Invalid | Price equals zero exactly     | `0`            |
| EC12     | Invalid | Negative price (< 0)          | `-50000`       |
| EC13     | Invalid | `price` field null or missing | `null`         |

#### Variable I4: `quantity` — G1 (Continuous range: quantity ≥ 1)

> G1 applied: 1 valid class (≥ 1) + 2 invalid classes (= 0, < 0).  
> G3 applied: null/missing is a separate violation.  
> G4 applied: non-integer decimal — same numeric domain, different parsing behavior.  
> **User-specified:** `quantity = 0` is a mandatory separate invalid class (boundary between "minimum valid" and "remove item" ambiguity).

| Class ID | Type    | Description                                            | Representative |
| -------- | ------- | ------------------------------------------------------ | -------------- |
| EC14     | Valid   | Integer ≥ 1                                            | `2`            |
| EC15     | Invalid | Quantity = 0 exactly (boundary — remove-or-reject gap) | `0`            |
| EC16     | Invalid | Negative integer (< 0)                                 | `-1`           |
| EC17     | Invalid | `quantity` field null or missing                       | `null`         |
| EC18     | Invalid | Non-integer decimal value                              | `1.5`          |

#### Variable I5: `auth_token` — G2 (Discrete set of token states)

> G2 applied: token state is a discrete set — 1 valid class per meaningful state + 1 invalid catch-all.  
> Auth states for FR-07 (user-facing feature): absent / valid-user / valid-admin / invalid.  
> Note: Admin also holds a valid JWT; cart access with admin token tests whether SEC-02 enforcement is role-agnostic (per FR-07, SEC-02).

| Class ID | Type       | Description                                        | Representative                 |
| -------- | ---------- | -------------------------------------------------- | ------------------------------ |
| EC19     | Valid      | Valid user JWT in Authorization header             | `Bearer <user_jwt>`            |
| EC20     | Valid (G2) | Valid admin JWT — tests whether admin can use cart | `Bearer <admin_jwt>`           |
| EC21     | Invalid    | No Authorization header (anonymous)                | (header absent)                |
| EC22     | Invalid    | Invalid or expired token                           | `Bearer invalid.expired.token` |

> **Note:** I6 (`user_auth_state`) is fully derived from I5. EC19–EC22 cover both variables. No additional ECs required for I6.

#### Variable I7: `duplicate_product_in_cart` — G3 (Boolean DB state: first-add vs. repeat-add)

> G3 applied: binary must-be condition — is the product already in this user's cart?  
> **User-specified:** Merge behavior (EC24) is a mandatory separate valid class (per BR-03).

| Class ID | Type       | Description                                                                   | Representative                                  |
| -------- | ---------- | ----------------------------------------------------------------------------- | ----------------------------------------------- |
| EC23     | Valid      | Product NOT yet in cart — first-time add → INSERT new row                     | Cart contains no entry for id=1; POST with id=1 |
| EC24     | Valid (G4) | Product ALREADY in cart — repeat add → MERGE (increment quantity, no new row) | Cart has id=1 qty=1; POST with id=1 again       |

#### Variable I8: `cart_empty_state` — G3 (Boolean: non-empty vs. empty)

> G3 applied: binary state — cart has items vs. cart is empty. Both are valid states that drive different UI outputs (BR-02 table view vs. BR-08 empty state).

| Class ID | Type    | Description                             | Representative                          |
| -------- | ------- | --------------------------------------- | --------------------------------------- |
| EC25     | Valid-A | Cart has ≥ 1 item — table view rendered | Cart with id=1, qty=2                   |
| EC26     | Valid-B | Cart has 0 items — empty-state UI shown | Cart with no items (just authenticated) |

#### Variable I9: `confirm_dialog_response` — G3 (Boolean: confirmed vs. dismissed)

> G3 applied: binary user decision after delete button is pressed. Both outcomes are valid user actions; they drive different system responses.

| Class ID | Type    | Description                                          | Representative         |
| -------- | ------- | ---------------------------------------------------- | ---------------------- |
| EC27     | Valid-A | User clicks **Confirm** in dialog → item deleted     | Click "Confirm" button |
| EC28     | Valid-B | User clicks **Dismiss/Cancel** in dialog → item kept | Click "Cancel" button  |

#### Variable I10: `product_exists_in_db` — G3 (Boolean DB state)

> G3 applied: already captured via `id` classes.  
> EC01 maps to `product_exists_in_db = true`; EC02 maps to `product_exists_in_db = false`. No additional ECs needed — cross-reference only.

| Cross-ref   | Mapped EC | Explanation                     |
| ----------- | --------- | ------------------------------- |
| I10 = true  | EC01      | Product ID exists in DB         |
| I10 = false | EC02      | Product ID does not exist in DB |

### Step 3: Test Case Optimization

#### 3.1 Valid Classes Coverage (Combination Rule)

> Combine as many valid ECs as possible into the minimum number of test cases.  
> Valid ECs: EC01, EC05, EC06, EC10, EC14, EC19, EC20, EC23, EC24, EC25, EC26, EC27, EC28

| TC ID       | Valid Classes Covered                    | Test Data Summary                                                                                           |
| ----------- | ---------------------------------------- | ----------------------------------------------------------------------------------------------------------- |
| FR07-EP-001 | EC01, EC05, EC10, EC14, EC19, EC23, EC25 | Add new product (id=1, name="Laptop ABC", price=100000, qty=2) with user JWT to non-empty cart → Happy Path |
| FR07-EP-002 | EC01, EC05, EC10, EC14, EC19, EC24       | Add same id again to cart → quantity merges (BR-03), no duplicate row                                       |
| FR07-EP-003 | EC19, EC26                               | Authenticated user views empty cart → empty-state UI shown (BR-08)                                          |
| FR07-EP-004 | EC01, EC19, EC27                         | Click delete → confirm dialog appears → user confirms → item removed (BR-05)                                |
| FR07-EP-005 | EC01, EC19, EC28                         | Click delete → confirm dialog appears → user dismisses → item retained (BR-05)                              |
| FR07-EP-006 | EC06, EC10, EC14, EC19, EC23             | Add product with XSS name; cart renders it escaped — no script execution (SEC-04)                           |
| FR07-EP-007 | EC01, EC05, EC10, EC14, EC20, EC23       | Admin JWT used to access cart → behaves as authenticated user (BR-01, SEC-02)                               |

#### 3.2 Invalid Classes Coverage (Isolation Rule)

> Each invalid TC: exactly 1 invalid EC + all other inputs drawn from valid classes (EC01, EC05, EC10, EC14, EC19).

| TC ID       | Invalid Class Isolated                            | Other Inputs (All Valid)                                |
| ----------- | ------------------------------------------------- | ------------------------------------------------------- |
| FR07-EP-008 | EC02 — `id` not in DB (99999)                     | name="Laptop ABC", price=100000, qty=2, valid user JWT  |
| FR07-EP-009 | EC03 — `id` field null/missing from body          | name="Laptop ABC", price=100000, qty=2, valid user JWT  |
| FR07-EP-010 | EC04 — `id` is non-integer ("abc")                | name="Laptop ABC", price=100000, qty=2, valid user JWT  |
| FR07-EP-011 | EC07 — `name` is empty string ("")                | id=1, price=100000, qty=2, valid user JWT               |
| FR07-EP-012 | EC08 — `name` field null/missing from body        | id=1, price=100000, qty=2, valid user JWT               |
| FR07-EP-013 | EC09 — `name` is > 255 chars string               | id=1, price=100000, qty=2, valid user JWT               |
| FR07-EP-014 | EC11 — `price` = 0                                | id=1, name="Laptop ABC", qty=2, valid user JWT          |
| FR07-EP-015 | EC12 — `price` < 0 (-50000)                       | id=1, name="Laptop ABC", qty=2, valid user JWT          |
| FR07-EP-016 | EC13 — `price` field null/missing                 | id=1, name="Laptop ABC", qty=2, valid user JWT          |
| FR07-EP-017 | EC15 — `quantity` = 0 (boundary gap)              | id=1, name="Laptop ABC", price=100000, valid user JWT   |
| FR07-EP-018 | EC16 — `quantity` < 0 (-1)                        | id=1, name="Laptop ABC", price=100000, valid user JWT   |
| FR07-EP-019 | EC17 — `quantity` field null/missing              | id=1, name="Laptop ABC", price=100000, valid user JWT   |
| FR07-EP-020 | EC18 — `quantity` is decimal (1.5)                | id=1, name="Laptop ABC", price=100000, valid user JWT   |
| FR07-EP-021 | EC21 — No Authorization header (anonymous access) | id=1, name="Laptop ABC", price=100000, qty=2 (no token) |
| FR07-EP-022 | EC22 — Invalid/expired JWT token                  | id=1, name="Laptop ABC", price=100000, qty=2, bad token |

#### 3.3 EC Coverage Summary

| Total ECs | Valid ECs | Invalid ECs | TCs for Valid | TCs for Invalid | Total TCs |
| --------- | --------- | ----------- | ------------- | --------------- | --------- |
| 28        | 13        | 15          | 7             | 15              | 22        |

## C. Boundary Value Analysis (BVA Walkthrough)

For every continuous or ordered variable identified in the previous step, we apply Step 4: The 9-point Boundary Value Analysis strategy to thoroughly test the edges of valid and invalid classes.

### Boundary Variables Identified

| Variable      | Data Type        | LB  | UB          | Increment | Note                                       |
| ------------- | ---------------- | --- | ----------- | --------- | ------------------------------------------ |
| `quantity`    | integer          | 1   | unspecified | 1         | Per FR-06/FR-07 (must be positive integer) |
| `price`       | number (integer) | 1   | unspecified | 1         | Per FR-15 (must be > 0)                    |
| `name` length | integer (string) | 1   | 255         | 1 char    | Implicit DB VARCHAR(255) constraint        |

### BVA Table 1: `quantity` (integer)

**Constraint:** `quantity >= 1`  
**LB = 1, UB = unspecified**

| TC ID        | BVA Point       | Test Value | Valid/Invalid    | Expected Result                              |
| ------------ | --------------- | ---------- | ---------------- | -------------------------------------------- |
| FR07-BVA-001 | -α (empty)      | `null`     | Invalid          | Reject: quantity is required                 |
| FR07-BVA-002 | LB-1            | `0`        | Invalid          | Reject or remove item (boundary gap per SRS) |
| FR07-BVA-003 | LB (exact)      | `1`        | Valid            | Accept: quantity set to 1                    |
| FR07-BVA-004 | LB+1            | `2`        | Valid            | Accept: quantity set to 2                    |
| FR07-BVA-005 | Nominal         | `5`        | Valid            | Accept: quantity set to 5                    |
| FR07-BVA-006 | UB-1            | N/A        | —                | —                                            |
| FR07-BVA-007 | UB              | N/A        | —                | —                                            |
| FR07-BVA-008 | UB+1            | N/A        | —                | —                                            |
| FR07-BVA-009 | +α (very large) | `9999`     | Invalid (likely) | Reject (system limit exceeded) or accept     |

### BVA Table 2: `price` (number)

**Constraint:** `price > 0` (minimum 1 ₫)  
**LB = 1, UB = unspecified**

| TC ID        | BVA Point       | Test Value   | Valid/Invalid    | Expected Result                          |
| ------------ | --------------- | ------------ | ---------------- | ---------------------------------------- |
| FR07-BVA-010 | -α (empty)      | `null`       | Invalid          | Reject: price is required                |
| FR07-BVA-011 | LB-1            | `0`          | Invalid          | Reject: price must be > 0                |
| FR07-BVA-012 | LB (exact)      | `1`          | Valid            | Accept: item added with 1 ₫ price        |
| FR07-BVA-013 | LB+1            | `2`          | Valid            | Accept: item added with 2 ₫ price        |
| FR07-BVA-014 | Nominal         | `100000`     | Valid            | Accept: item added with 100,000 ₫ price  |
| FR07-BVA-015 | UB-1            | N/A          | —                | —                                        |
| FR07-BVA-016 | UB              | N/A          | —                | —                                        |
| FR07-BVA-017 | UB+1            | N/A          | —                | —                                        |
| FR07-BVA-018 | +α (very large) | `2000000000` | Invalid (likely) | Reject (system limit exceeded) or accept |

### BVA Table 3: `name` (string length)

**Constraint:** length between 1 and 255 chars (implicit DB constraint)  
**LB = 1, UB = 255**

| TC ID        | BVA Point      | Test Value   | Length | Valid/Invalid | Expected Result                      |
| ------------ | -------------- | ------------ | ------ | ------------- | ------------------------------------ |
| FR07-BVA-019 | -α (empty)     | `""`         | 0      | Invalid       | Reject: name is required             |
| FR07-BVA-020 | LB-1           | `""`         | 0      | Invalid       | (Same as -α)                         |
| FR07-BVA-021 | LB (exact)     | `"A"`        | 1      | Valid         | Accept: product added                |
| FR07-BVA-022 | LB+1           | `"AB"`       | 2      | Valid         | Accept: product added                |
| FR07-BVA-023 | Nominal        | `"Laptop"`   | 6      | Valid         | Accept: product added                |
| FR07-BVA-024 | UB-1           | `"A" x 254`  | 254    | Valid         | Accept: product added                |
| FR07-BVA-025 | UB             | `"A" x 255`  | 255    | Valid         | Accept: product added                |
| FR07-BVA-026 | UB+1           | `"A" x 256`  | 256    | Invalid       | Reject: name too long                |
| FR07-BVA-027 | +α (very long) | `"A" x 1000` | 1000   | Invalid       | Reject: name too long / system error |

### BVA Summary

| Variable      | Total BVA Points | Valid Points | Invalid Points | BVA TCs Generated |
| ------------- | ---------------- | ------------ | -------------- | ----------------- |
| `quantity`    | 6                | 3            | 3              | 6                 |
| `price`       | 6                | 3            | 3              | 6                 |
| `name` length | 8                | 4            | 4              | 8 (Note 1)        |
| **Total**     |                  |              |                | **20**            |

> **Note 1:** For `name`, `-α` and `LB-1` are identical (length 0). FR07-BVA-019 covers both. The total BVA TCs generated is 20.

## D. Coverage Review & AI Gap Analysis

### 1. EP Guidelines Compliance

| Variable                    | Guideline Applied | Valid Classes | Invalid Classes | Status |
| --------------------------- | ----------------- | ------------- | --------------- | ------ |
| `id`                        | G3 + G4           | 1             | 3               | Pass   |
| `name`                      | G1 + G3 + G4      | 2             | 3               | Pass   |
| `price`                     | G1 + G3           | 1             | 3               | Pass   |
| `quantity`                  | G1 + G3 + G4      | 1             | 4               | Pass   |
| `auth_token`                | G2                | 2             | 2               | Pass   |
| `duplicate_product_in_cart` | G3 + G4           | 2             | 0               | Pass   |
| `cart_empty_state`          | G3                | 2             | 0               | Pass   |
| `confirm_dialog_response`   | G3                | 2             | 0               | Pass   |

### 2. Missing Classes Found

| #   | Missing Class | Reason | Action Taken |
| --- | ------------- | ------ | ------------ |
| —   | None          | —      | —            |

> **Note:** The AI did not miss any required classes because explicit constraints (like duplicate item merge and quantity=0 gap) were pre-emptively forced in the human prompt.

### 3. Rule Violations Found

| TC ID | Violation | Description | Fix Applied |
| ----- | --------- | ----------- | ----------- |
| —     | None      | —           | —           |

### 4. BVA Completeness

| Variable      | BVA Applied | Points Generated | Missing Points |
| ------------- | ----------- | ---------------- | -------------- |
| `quantity`    | Yes         | 6                | None           |
| `price`       | Yes         | 6                | None           |
| `name` length | Yes         | 8                | None           |

### 5. AI Gap Analysis

#### What AI Did Correctly

- Accurately applied G1 to numeric and length boundaries (`price`, `quantity`, `name`).
- Successfully applied G4 to split string inputs into XSS test vectors for SEC-04.
- Flawlessly applied the Isolation Rule to map exactly 1 invalid EC to 1 invalid test case without defect masking.
- Correctly implemented `+α` values for unspecified upper bounds in the SRS.

#### What AI Missed

1. **Implicit State Transitions (Merge vs. Insert)**
   - Description: Without human prompting, AI naturally assumes adding a product to a cart means creating a new row, and would typically miss the `MERGE` behavior when adding a duplicate item (BR-03).
   - Root cause: Feature complexity — state-based transitions often span multiple operations and require understanding the business logic holistically, not just the field types.

2. **The "Remove" Boundary Gap (`quantity` = 0)**
   - Description: The SRS dictates quantity must be >= 1 (FR-06), but doesn't explicitly state whether a `quantity` of 0 submitted via API should return a 400 error or successfully delete the item. AI usually misses this functional ambiguity unless prompted.
   - Root cause: AI limitation / Feature complexity — AI expects a hard pass/fail boundary, struggling with ambiguous domains where a boundary violation actually triggers a different valid workflow (deletion).

#### Root Cause Summary

| Category           | % Share | Description                                                         |
| ------------------ | ------- | ------------------------------------------------------------------- |
| Prompt quality     | 0%      | Human prompt was highly specific and pre-empted common mistakes.    |
| AI limitation      | 50%     | AI relies heavily on explicit SRS rules and struggles with gaps.    |
| Feature complexity | 50%     | Complex state transitions (merge/remove) are hard to map to inputs. |

#### Lesson Learned

1. **AI exhibits a "Strict CRUD Bias" for State-Dependent Operations:** When generating test cases, the AI typically maps user actions 1:1 to standard database operations (e.g., "Add to Cart" = `INSERT`). It struggles to autonomously identify conditional state transitions, such as the `MERGE` behavior (incrementing quantity instead of inserting a duplicate row) dictated by BR-03.
   - **Mitigation Strategy for future FRs:** The QA Engineer must identify business rules that alter the state of existing data rather than creating new data. These state-dependent conditions must be explicitly fed into the AI prompt to force the creation of valid ECs (e.g., "Product already in cart").
2. **AI struggles with "Functional Boundary Ambiguity" (Valid vs. Action-Trigger):** Standard BVA logic assumes boundaries separate a _Valid_ input from an _Error_ (HTTP 4xx). However, in e-commerce (like `quantity` = 0), crossing a boundary often triggers a valid alternative system action (e.g., Deleting the item). The AI will default to treating `0` as a standard validation error unless guided.
   - **Mitigation Strategy for future FRs:** Do not rely on AI to interpret the functional intent of ambiguous boundaries. When a boundary value alters the system workflow (e.g., turning an Update into a Delete), the human must explicitly prompt the AI to treat it as a distinct class with its own specific Expected Result.

### 6. Final Summary After Review

| Category      | Before Review | Added | After Review |
| ------------- | ------------- | ----- | ------------ |
| Valid ECs     | 13            | 0     | 13           |
| Invalid ECs   | 15            | 0     | 15           |
| BVA Points    | 20            | 0     | 20           |
| **Total TCs** | 42            | 0     | 42           |

# FR-17 — Coupon Management for Admin (Pool C)

## A. Requirement Analysis

To begin Domain Testing, we first analyze the requirement to identify all explicit and implicit constraints, actors, and business rules governing **Coupon Management (Admin)**. This step forms the foundation for extracting variables.

### 1. Feature Overview

| Attribute         | Value                                                                            |
| ----------------- | -------------------------------------------------------------------------------- |
| Feature ID        | FR-17                                                                            |
| Feature Name      | Coupon Management — Admin CRUD (Create / Read / Delete)                          |
| Test Layer        | Both (Web Admin UI + API)                                                        |
| Entry Point (UI)  | `http://localhost:5174` → Coupon Management section                              |
| Entry Point (API) | `POST /api/admin/coupons` · `DELETE /api/admin/coupons/:id` · `GET /api/coupons` |
| Actors            | Admin only                                                                       |
| Auth Required     | Yes — Admin JWT (`role = 'admin'` required)                                      |

### 2. Input Fields & Constraints

| Field/Param         | Layer    | Type    | Explicit Constraints (from SRS FR-17)                     | Implicit Constraints (Architecture/DB)                                                                                                                        | API Param Name      |
| ------------------- | -------- | ------- | --------------------------------------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------- | ------------------- |
| `code`              | UI + API | string  | Required; **unique** in the system                        | Likely case-sensitive; DB UNIQUE constraint; max length unknown (typical 50 chars)                                                                            | `code`              |
| `type`              | UI + API | enum    | Required; exactly one of: `"percent"` or `"fixed"`        | Any other value is invalid; stored as VARCHAR/ENUM in DB                                                                                                      | `type`              |
| `discount_value`    | UI + API | number  | Required; must be **positive (> 0)**                      | For `percent`: logical max ≤ 100; for `fixed`: positive float; no explicit upper bound in SRS                                                                 | `discount_value`    |
| `expired_at`        | UI + API | date    | Required; represents coupon expiry date                   | ISO 8601 format `YYYY-MM-DD`; must be parseable as date; no explicit lower bound (past dates allowed per business logic — C2 from FR-09 checks at usage time) | `expired_at`        |
| `min_order_amount`  | UI + API | number  | Required; must be **>= 0** (zero allowed)                 | Non-negative float/integer; 0 means no minimum threshold                                                                                                      | `min_order_amount`  |
| `max_uses_per_user` | UI + API | integer | Required; must be **>= 1**                                | Positive integer; value 0 would mean "never usable" — invalid per SRS; upper bound not stated                                                                 | `max_uses_per_user` |
| `id` (URL param)    | API only | integer | Required for DELETE; must reference an existing coupon ID | Must be a positive integer; non-existent ID → 404                                                                                                             | `:id`               |
| JWT Token (header)  | API      | string  | Required; must be valid; must carry `role = 'admin'`      | Passed via `Authorization: Bearer <token>`; missing → 401; invalid/user-role → 403                                                                            | `Authorization`     |

### 3. Business Rules

- **[BR-01]** The `code` field is required and must not be empty. (per FR-17)
- **[BR-02]** The `code` field must be unique — the system must reject a duplicate coupon code already existing in the database. (per FR-17)
- **[BR-03]** The `type` field is required and must be exactly one of the two enum values: `"percent"` or `"fixed"`. Any other value must be rejected. (per FR-17)
- **[BR-04]** The `discount_value` field is required and must be a positive number (> 0). A value of 0 or any negative number must be rejected. (per FR-17)
- **[BR-05]** The `expired_at` field is required and must be a valid date string. The date must be strictly >= the current date at the time of creation (it is invalid to create an already-expired coupon). The format must be parseable. (per FR-17, logical business rule)
- **[BR-06]** The `min_order_amount` field is required and must be >= 0. A value of 0 is valid (meaning no minimum order threshold). Negative values must be rejected. (per FR-17)
- **[BR-07]** The `max_uses_per_user` field is required and must be >= 1. A value of 0 is invalid — it would make the coupon permanently unusable. (per FR-17)
- **[BR-08]** Only authenticated users with `role = 'admin'` may call the Create (`POST`) and Delete (`DELETE`) coupon admin endpoints. Unauthenticated requests receive HTTP 401; authenticated non-admin users receive HTTP 403. (per FR-12, SEC-02, SEC-03)
- **[BR-09]** The Read (`GET /api/coupons`) endpoint also requires a valid JWT token per the API spec, but it is not listed under `/api/admin/*`. The role requirement should be verified empirically. (per API spec §5.2)
- **[BR-10]** Admin CRUD operations on coupons use the `/api/admin/coupons` path. All `/api/admin/*` endpoints require Admin JWT per FR-12. (per FR-12)
- **[BR-11]** For `type = "percent"`: `discount_amount = total × discount_value / 100`. For `type = "fixed"`: `discount_amount = discount_value`. (per FR-09) — Creation does not compute this; it is relevant to verify that `type` and `discount_value` are stored correctly and applied correctly at checkout.
- **[BR-12]** Deleting a coupon by ID must remove it from the system. Deleting a non-existent coupon ID must return an appropriate error (404 or similar). (per FR-17, implicit)
- **[BR-13]** The Admin Web UI must present the coupon list in a readable table. There is no explicit Edit operation for coupons — only Add and Delete are specified in FR-17. (per FR-17)

### 4. Expected Outputs

#### 4.1 Success Path — Create Coupon (`POST /api/admin/coupons`)

- **HTTP:** `200 OK` or `201 Created` + JSON body confirming creation (exact body not specified in API spec; typically `{"message": "Coupon created", "id": <n>}`)
- **UI:** New coupon row appears in the coupon list table on the Admin UI
- **DB:** New record inserted in `coupons` table with all provided field values; `is_active = 1` by default

#### 4.2 Success Path — Read Coupons (`GET /api/coupons`)

- **HTTP:** `200 OK` + JSON array of coupon objects (each with: `id`, `code`, `type`, `discount_value`, `min_order_amount`, `expired_at`, `max_uses_per_user`, `is_active`)
- **UI:** Admin UI displays coupon table populated with all coupons from DB

#### 4.3 Success Path — Delete Coupon (`DELETE /api/admin/coupons/:id`)

- **HTTP:** `200 OK` + JSON confirmation (e.g., `{"message": "Coupon deleted"}`)
- **UI:** Coupon row disappears from the table after deletion
- **DB:** Record removed from `coupons` table for the given `id`

#### 4.4 Failure Paths

- **Missing required field** (`code`, `type`, `discount_value`, `expired_at`, `min_order_amount`, `max_uses_per_user`): HTTP `400 Bad Request` + error description
- **Duplicate `code`**: HTTP `400 Bad Request` + error message indicating code already exists
- **Invalid `type`** (not `"percent"` or `"fixed"`): HTTP `400 Bad Request` + error message
- **`discount_value` <= 0**: HTTP `400 Bad Request` + validation error
- **`min_order_amount` < 0**: HTTP `400 Bad Request` + validation error
- **`max_uses_per_user` < 1**: HTTP `400 Bad Request` + validation error
- **No JWT Token provided**: HTTP `401 Unauthorized` (per SEC-02)
- **Valid JWT but `role ≠ 'admin'`** (user token): HTTP `403 Forbidden` (per SEC-03, FR-12)
- **Delete with non-existent ID**: HTTP `404 Not Found` + error message
- **Invalid `expired_at` format** (unparseable date string): HTTP `400 Bad Request`

### 5. GUI Requirements Applicable (FR-21~24)

This is a **Web Admin UI** feature (not Mobile). Apply HTML/DOM semantics checks.

- **[GUI-01]** The Admin Coupon Management page must have exactly **one `<h1>` tag** describing the page content (e.g., "Quản lý Mã Giảm Giá"). (per FR-21, FR-05)
- **[GUI-02]** All required fields in the Create Coupon form must have a `*` symbol next to their label: `code *`, `type *`, `discount_value *`, `expired_at *`, `min_order_amount *`, `max_uses_per_user *`. (per FR-22)
- **[GUI-03]** The `expired_at` date input field should use `type="date"` for proper date validation. (per FR-22, implicit)
- **[GUI-04]** Error messages on the Create Coupon form must appear **above** the Submit button, not below it. (per FR-22)
- **[GUI-05]** Action buttons: "Add Coupon" / "Submit" button must use **blue** (positive action). "Delete" button must use **red** (destructive action). (per FR-21)
- **[GUI-06]** Currency values (`min_order_amount`, `discount_value` for fixed type) must be displayed with the `₫` symbol and thousand-separator formatting in the coupon list view. (per FR-21)
- **[GUI-07]** Deleting a coupon via the UI **must show a confirmation dialog** before performing the delete action. (per FR-24, FR-07 pattern)
- **[GUI-08]** If no coupons exist, an **empty state** with icon/illustration and a friendly message must be displayed. (per FR-24)
- **[GUI-09]** The Admin navbar must **highlight** the currently active page/section (Coupon Management). (per FR-23)
- **[GUI-10]** The Create Coupon form is a **single-step form** — no Step Indicator is required (Step Indicator is only required for forms with 2+ steps per FR-22).
- **[GUI-11]** The coupon list table must display monetary values using the `₫` symbol consistently. (per FR-21)

### 6. Security Requirements Applicable (SEC-xx)

- **[SEC-02]** All coupon admin endpoints (`POST /api/admin/coupons`, `DELETE /api/admin/coupons/:id`, `GET /api/coupons`) must require a valid JWT token. A request without a token must return HTTP `401 Unauthorized`. (per SEC-02)
- **[SEC-03]** The `POST /api/admin/coupons` and `DELETE /api/admin/coupons/:id` endpoints must verify that the authenticated user has `role = 'admin'`. A request using a regular user token must return HTTP `403 Forbidden`. (per SEC-03, FR-12)
- **[SEC-04]** The coupon `code` value entered by admin is displayed back on the UI (in the coupon list table). It must be rendered safely — escaped, not injected as raw HTML — to prevent stored XSS. (per SEC-04) — Test by entering `<script>alert(1)</script>` as a `code` value.
- **[SEC-05]** All database queries for coupon operations must use parameterized queries (no SQL injection risk from `code`, `type`, or numeric fields). (per SEC-05) — Test by entering SQL injection payloads in the `code` field.

### 7. Notes for Domain Testing

- **Input variables identified:** `code`, `type`, `discount_value`, `expired_at`, `min_order_amount`, `max_uses_per_user`, `id` (for DELETE), `Authorization` header (JWT token / role)
- **Output variables identified:** HTTP status code, JSON response body (message/id), UI state (coupon list update, form error display, confirm dialog), DB state (coupons table: row inserted / row deleted)
- **Boundary candidates:**
  - `discount_value`: lower boundary = 0 (invalid) vs. 1 (valid); upper boundary = unspecified (test very large values)
  - `discount_value` for `percent` type: logical upper boundary at 100 (100% discount) — SRS doesn't state this explicitly; test 100 and 101
  - `min_order_amount`: lower boundary = 0 (valid, just at boundary) vs. -1 (invalid)
  - `max_uses_per_user`: lower boundary = 1 (valid) vs. 0 (invalid)
  - `code` string length: test empty string (invalid) and very long string (potential DB truncation)
  - `expired_at`: boundary at today's date (today vs. yesterday); yesterday is invalid at creation time
- **High-risk areas:**
  - Role-Auth bypass: user-role JWT calling admin coupon endpoints
  - `type` enum validation: what happens with values like `"PERCENT"` (uppercase), `""` (empty), `"discount"` (unknown)
  - `discount_value = 0`: must be rejected but may be silently accepted
  - `max_uses_per_user = 0`: must be rejected (makes coupon unusable) — high risk of bug
  - Duplicate `code`: race condition / DB constraint handling
  - XSS via `code` field displayed in the coupon list
  - Delete non-existent ID: may return 500 instead of 404
  - Missing fields: partial body — which fields are actually validated vs silently defaulted?
- **AI blind spot warnings:**
  - The SRS allows `min_order_amount = 0` (exactly zero is valid) — AI may incorrectly treat 0 as invalid
  - The SRS does NOT specify an upper bound for `discount_value` — for `percent` type, 101% may or may not be rejected; this is a high-risk untested area
  - `max_uses_per_user = 0` is explicitly prohibited (>= 1 required) — AI may miss this boundary
  - `GET /api/coupons` is listed outside `/api/admin/*` but still requires Auth — role requirement is ambiguous; must test all 3 token states
  - No Edit (PUT) operation exists for coupons per FR-17 — do not design test cases for coupon update
  - `is_active` field is referenced in FR-09 (C1) but is NOT listed as an input field in the Create Coupon API — it may be auto-set to `1`; verify this via DB state check

## B. Domain Analysis (Equivalence Partitioning Walkthrough)

Based on the identified constraints, we proceed to Step 1: Identifying Variables (both input and output) and Step 2 & 3: Dividing them into valid and invalid Equivalence Classes applying the 4 EP guidelines.

### Step 1: Input & Output Variable Identification

#### 1.1 Input Variables

##### Direct Inputs (UI Form / API Body)

| #   | Variable            | Source             | Type    | Description                                                                   |
| --- | ------------------- | ------------------ | ------- | ----------------------------------------------------------------------------- |
| I1  | `code`              | UI form + API body | string  | Unique coupon code to create (e.g. `"SAVE10"`)                                |
| I2  | `type`              | UI form + API body | enum    | Discount type — exactly `"percent"` or `"fixed"`                              |
| I3  | `discount_value`    | UI form + API body | number  | Discount amount; must be > 0                                                  |
| I4  | `expired_at`        | UI form + API body | date    | Coupon expiry date in ISO 8601 format (`YYYY-MM-DD`), must be >= current date |
| I5  | `min_order_amount`  | UI form + API body | number  | Minimum cart total required to use coupon; must be >= 0                       |
| I6  | `max_uses_per_user` | UI form + API body | integer | Maximum times a single user may use this coupon; must be >= 1                 |
| I7  | `id` (URL param)    | API URL path only  | integer | Coupon ID used in `DELETE /api/admin/coupons/:id`                             |

##### Indirect Inputs (Hidden / System State)

| #   | Variable              | Source         | Type       | Description                                                            |
| --- | --------------------- | -------------- | ---------- | ---------------------------------------------------------------------- |
| I8  | `auth_token`          | Request header | JWT string | `Authorization: Bearer <token>`; missing → 401                         |
| I9  | `user_role`           | JWT payload    | enum       | Must be `'admin'`; any other role → 403 (per SEC-03, FR-12)            |
| I10 | `code_uniqueness`     | DB state       | boolean    | Whether the submitted `code` already exists in `coupons` table         |
| I11 | `coupon_id_existence` | DB state       | boolean    | Whether the `:id` in DELETE request references a real coupon in the DB |

#### 1.2 Output Variables

##### Direct Outputs (Visible)

| #   | Variable                     | Channel | Description                                                                                                            |
| --- | ---------------------------- | ------- | ---------------------------------------------------------------------------------------------------------------------- |
| O1  | HTTP status code             | API     | `200`/`201` success; `400` bad input; `401` no token; `403` wrong role; `404` not found                                |
| O2  | Response body (JSON)         | API     | `{"message": "Coupon created", "id": N}` on create; `{"message": "Coupon deleted"}` on delete; error string on failure |
| O3  | UI coupon list — new row     | UI      | After successful CREATE, new coupon row appears in Admin coupon table                                                  |
| O4  | UI coupon list — row removed | UI      | After successful DELETE, coupon row disappears from Admin coupon table                                                 |
| O5  | UI form error message        | UI      | Validation error displayed **above** the Submit button (per FR-22)                                                     |
| O6  | UI confirmation dialog       | UI      | Delete action must trigger a confirm dialog before executing (per FR-24)                                               |

##### Indirect Outputs (Hidden / State Changes)

| #   | Variable                           | Channel | Description                                                                       |
| --- | ---------------------------------- | ------- | --------------------------------------------------------------------------------- |
| O7  | DB `coupons` table — INSERT        | State   | After CREATE: new row exists in DB with all correct field values; `is_active = 1` |
| O8  | DB `coupons` table — DELETE        | State   | After DELETE: row with matching `id` no longer exists in DB                       |
| O9  | DOM: `<h1>` count                  | DOM     | Admin Coupon Management page has exactly **one** `<h1>` tag (per FR-21)           |
| O10 | DOM: required `*` labels           | DOM     | All 6 mandatory fields have `*` symbol beside their form label (per FR-22)        |
| O11 | DOM: `type="date"` on `expired_at` | DOM     | Date input field uses `type="date"` HTML attribute (per FR-22, implicit)          |
| O12 | DOM: button color semantics        | DOM     | Submit/Add button is blue; Delete button is red (per FR-21)                       |

#### 1.3 Variable Summary for EP

- **Total inputs identified:** 11 (7 direct + 4 indirect)
- **Total outputs identified:** 12 (6 direct + 6 indirect)
- **Variables requiring EP:** `code`, `type`, `discount_value`, `expired_at`, `min_order_amount`, `max_uses_per_user`, `id`, `auth_token`, `user_role`, `code_uniqueness`, `coupon_id_existence`
- **Boundary candidates:**
  - `discount_value` — lower bound = 0 (invalid) / 1 (valid); no explicit upper bound (test large values; for `percent` type, 100 and 101 are critical)
  - `min_order_amount` — lower bound = -1 (invalid) / 0 (valid boundary)
  - `max_uses_per_user` — lower bound = 0 (invalid) / 1 (valid boundary)
  - `code` string length — empty string (invalid); very long string (DB truncation risk)
  - `expired_at` — past dates (invalid at creation per logical rule); invalid format strings; far-future dates

### Step 2: Equivalence Classes

#### Variable: `code` — Guideline 3 (Must-Be: non-empty, required) + Guideline 4 (Splitting: uniqueness + security)

| Class ID | Type       | Description                                                                      | Representative Value          |
| -------- | ---------- | -------------------------------------------------------------------------------- | ----------------------------- |
| EC01     | Valid      | Non-empty string, not yet in DB (unique)                                         | `"SUMMER25"`                  |
| EC02     | Valid (G4) | Code contains HTML/script characters — accepted but must display safely (SEC-04) | `"<script>alert(1)</script>"` |
| EC03     | Invalid    | Empty string `""`                                                                | `""`                          |
| EC04     | Invalid    | Field missing from request body (null / omitted)                                 | _(field omitted)_             |
| EC05     | Invalid    | Code already exists in DB (duplicate violation)                                  | `"SAVE10"` (pre-existing)     |

#### Variable: `type` — Guideline 2 (Discrete Set: `{"percent", "fixed"}`)

| Class ID | Type    | Description                                          | Representative Value |
| -------- | ------- | ---------------------------------------------------- | -------------------- |
| EC06     | Valid   | Enum value `"percent"`                               | `"percent"`          |
| EC07     | Valid   | Enum value `"fixed"`                                 | `"fixed"`            |
| EC08     | Invalid | Unknown string outside the enum set                  | `"discount"`         |
| EC09     | Invalid | Correct value but wrong case (case-sensitivity test) | `"PERCENT"`          |
| EC10     | Invalid | Empty string `""`                                    | `""`                 |
| EC11     | Invalid | Field missing from request body (null / omitted)     | _(field omitted)_    |

#### Variable: `discount_value` — Guideline 1 (Continuous Range: > 0) + Guideline 4 (Splitting for `percent` type: logical upper bound 100)

| Class ID | Type         | Description                                                               | Representative Value |
| -------- | ------------ | ------------------------------------------------------------------------- | -------------------- |
| EC12     | Valid        | Positive number, within normal operating range                            | `15`                 |
| EC13     | Invalid      | Exactly 0 — at boundary, violates `> 0` rule (per FR-17)                  | `0`                  |
| EC14     | Invalid      | Negative number — below minimum boundary                                  | `-10`                |
| EC15     | Invalid      | Field missing from request body (null / omitted)                          | _(field omitted)_    |
| EC16     | Invalid      | Non-numeric string — wrong data type                                      | `"abc"`              |
| EC17     | Valid (G4)   | Exactly 100 — logical upper bound for `percent` type (100% discount)      | `100`                |
| EC18     | Invalid (G4) | Exceeds 100 for `percent` type — SRS silent; logically should be rejected | `101`                |

> **AI Blind Spot Note:** `discount_value = 0` (EC13) must be an isolated invalid TC — AI may silently accept zero as a positive number. EC18 (101%) is an implicit boundary; SRS does not explicitly prohibit it — flag result if accepted.

#### Variable: `expired_at` — Guideline 3 (Must-Be: valid date, required) + Guideline 4 (Splitting: past date vs. future date)

| Class ID | Type         | Description                                                        | Representative Value |
| -------- | ------------ | ------------------------------------------------------------------ | -------------------- |
| EC19     | Valid        | Future date in ISO 8601 format — coupon will be active             | `"2099-12-31"`       |
| EC20     | Invalid (G4) | Past date in ISO 8601 format, cannot create already-expired coupon | `"2020-01-01"`       |
| EC21     | Invalid      | Valid date but wrong format (DD-MM-YYYY instead of YYYY-MM-DD)     | `"31-12-2099"`       |
| EC22     | Invalid      | Non-date arbitrary string — unparseable                            | `"notadate"`         |
| EC23     | Invalid      | Field missing from request body (null / omitted)                   | _(field omitted)_    |

#### Variable: `min_order_amount` — Guideline 1 (Continuous Range: >= 0)

| Class ID | Type    | Description                                                        | Representative Value |
| -------- | ------- | ------------------------------------------------------------------ | -------------------- |
| EC24     | Valid   | Exactly 0 — valid lower boundary; means no minimum order threshold | `0`                  |
| EC25     | Valid   | Positive value — normal operating range                            | `200000`             |
| EC26     | Invalid | Exactly -1 — below lower boundary                                  | `-1`                 |
| EC27     | Invalid | Field missing from request body (null / omitted)                   | _(field omitted)_    |

> **AI Blind Spot Note:** `min_order_amount = 0` (EC24) is **explicitly valid** per FR-17 (`>= 0`). Do NOT treat 0 as invalid.

#### Variable: `max_uses_per_user` — Guideline 1 (Continuous Range: >= 1, integer)

| Class ID | Type    | Description                                                         | Representative Value |
| -------- | ------- | ------------------------------------------------------------------- | -------------------- |
| EC28     | Valid   | Exactly 1 — valid lower boundary                                    | `1`                  |
| EC29     | Valid   | Integer greater than 1 — normal operating range                     | `3`                  |
| EC30     | Invalid | Exactly 0 — below lower boundary; makes coupon permanently unusable | `0`                  |
| EC31     | Invalid | Negative integer — clearly below minimum                            | `-1`                 |
| EC32     | Invalid | Field missing from request body (null / omitted)                    | _(field omitted)_    |
| EC33     | Invalid | Non-integer float value — violates integer constraint               | `1.5`                |

> **AI Blind Spot Note:** `max_uses_per_user = 0` (EC30) is **explicitly prohibited** by FR-17 (`>= 1`). This is a high-risk class — the system may silently accept 0.

#### Variable: `id` (URL path param for DELETE) — Guideline 3 (Must-Be: valid existing coupon ID) + Guideline 1 (positive integer)

| Class ID | Type    | Description                                           | Representative Value |
| -------- | ------- | ----------------------------------------------------- | -------------------- |
| EC34     | Valid   | Positive integer referencing an existing coupon in DB | `1` (pre-existing)   |
| EC35     | Invalid | Positive integer but coupon does not exist in DB      | `99999`              |
| EC36     | Invalid | Non-numeric string — wrong data type in URL path      | `"abc"`              |
| EC37     | Invalid | Zero or negative integer — invalid ID value           | `0`                  |

#### Variable: `auth_token` + `user_role` — Guideline 2 (Discrete Set: auth states)

| Class ID | Type    | Description                                                             | Representative Value             |
| -------- | ------- | ----------------------------------------------------------------------- | -------------------------------- |
| EC38     | Valid   | Valid JWT with `role = 'admin'` — authorized for all admin endpoints    | Admin JWT from `admin@eshop.com` |
| EC39     | Invalid | No token — `Authorization` header missing → HTTP 401 (per SEC-02)       | _(header omitted)_               |
| EC40     | Invalid | Valid JWT but `role = 'user'` — non-admin token → HTTP 403 (per SEC-03) | User JWT from `test@eshop.com`   |
| EC41     | Invalid | Malformed / expired / invalid JWT string → HTTP 401 (per SEC-02)        | `"Bearer invalidtoken123"`       |

### Step 3: Test Case Optimization

#### 3.1 Valid Classes Coverage (Combination Rule)

All valid classes combined efficiently into 6 test cases.

**Baseline valid inputs (used where not otherwise specified):**

- `code`: `"NEWCODE01"`, `type`: `"percent"`, `discount_value`: `15`, `expired_at`: `"2099-12-31"`, `min_order_amount`: `100000`, `max_uses_per_user`: `1`, `auth`: Admin JWT

| TC ID       | Operation | Valid ECs Covered                        | Test Data Summary                                                                                                                          |
| ----------- | --------- | ---------------------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------ |
| FR17-EP-001 | POST      | EC01, EC06, EC12, EC19, EC25, EC28, EC38 | `code="SUMMER25"`, `type="percent"`, `discount_value=15`, future expiry, `min_order_amount=200000`, `max_uses=1` — Full happy path         |
| FR17-EP-002 | POST      | EC01, EC07, EC12, EC19, EC24, EC29, EC38 | `code="BIGDEAL01"`, `type="fixed"`, `discount_value=50000`, future expiry, `min_order_amount=0`, `max_uses=3` — Fixed type, zero min order |
| FR17-EP-003 | POST      | EC02, EC06, EC12, EC19, EC25, EC28, EC38 | `code="<script>alert(1)</script>"`, all other valid — SEC-04 XSS safety check; expect 200 OK + safe display                                |
| FR17-EP-004 | DELETE    | EC34, EC38                               | DELETE `/api/admin/coupons/1` with admin JWT — delete existing coupon happy path                                                           |
| FR17-EP-005 | GET       | EC38                                     | GET `/api/coupons` with admin JWT — retrieve coupon list                                                                                   |
| FR17-EP-006 | UI / DOM  | EC38                                     | Navigate to UI with Admin JWT — Verify static DOM elements (`h1`, required `*`, `type="date"`, button semantics)                           |

> **Valid ECs covered:** EC01, EC02, EC06, EC07, EC12, EC17, EC19, EC24, EC25, EC28, EC29, EC34, EC38 **(13/13 — 100% coverage)**

#### 3.2 Invalid Classes Coverage (Isolation Rule)

Each TC contains **exactly 1 invalid input**. All other inputs drawn from valid baseline.

**Valid baseline for POST (unless noted):** `code="NEWCODE01"`, `type="percent"`, `discount_value=15`, `expired_at="2099-12-31"`, `min_order_amount=100000`, `max_uses_per_user=1`, `auth=Admin JWT`

| TC ID       | Operation | Invalid EC Tested                              | Invalid Input                          | Other Inputs                    |
| ----------- | --------- | ---------------------------------------------- | -------------------------------------- | ------------------------------- |
| FR17-EP-007 | POST      | EC03 — empty `code`                            | `code=""`                              | All others: valid baseline      |
| FR17-EP-008 | POST      | EC04 — missing `code` field                    | `code` omitted from body               | All others: valid baseline      |
| FR17-EP-009 | POST      | EC05 — duplicate `code` already in DB          | `code="SAVE10"` (pre-existing)         | All others: valid baseline      |
| FR17-EP-010 | POST      | EC08 — `type` outside enum set                 | `type="discount"`                      | All others: valid baseline      |
| FR17-EP-011 | POST      | EC09 — `type` uppercase (case-sensitivity)     | `type="PERCENT"`                       | All others: valid baseline      |
| FR17-EP-012 | POST      | EC10 — `type` empty string                     | `type=""`                              | All others: valid baseline      |
| FR17-EP-013 | POST      | EC11 — `type` field missing                    | `type` omitted from body               | All others: valid baseline      |
| FR17-EP-014 | POST      | EC13 — `discount_value` exactly 0              | `discount_value=0`                     | All others: valid baseline      |
| FR17-EP-015 | POST      | EC14 — `discount_value` negative               | `discount_value=-10`                   | All others: valid baseline      |
| FR17-EP-016 | POST      | EC15 — `discount_value` field missing          | `discount_value` omitted               | All others: valid baseline      |
| FR17-EP-017 | POST      | EC16 — `discount_value` non-numeric string     | `discount_value="abc"`                 | All others: valid baseline      |
| FR17-EP-018 | POST      | EC18 — `discount_value=101` for `percent` type | `type="percent"`, `discount_value=101` | All others: valid baseline      |
| FR17-EP-019 | POST      | EC20 — `expired_at` past date                  | `expired_at="2020-01-01"`              | All others: valid baseline      |
| FR17-EP-020 | POST      | EC21 — `expired_at` wrong format (DD-MM-YYYY)  | `expired_at="31-12-2099"`              | All others: valid baseline      |
| FR17-EP-021 | POST      | EC22 — `expired_at` non-date string            | `expired_at="notadate"`                | All others: valid baseline      |
| FR17-EP-022 | POST      | EC23 — `expired_at` field missing              | `expired_at` omitted                   | All others: valid baseline      |
| FR17-EP-023 | POST      | EC26 — `min_order_amount` negative (-1)        | `min_order_amount=-1`                  | All others: valid baseline      |
| FR17-EP-024 | POST      | EC27 — `min_order_amount` field missing        | `min_order_amount` omitted             | All others: valid baseline      |
| FR17-EP-025 | POST      | EC30 — `max_uses_per_user` exactly 0           | `max_uses_per_user=0`                  | All others: valid baseline      |
| FR17-EP-026 | POST      | EC31 — `max_uses_per_user` negative            | `max_uses_per_user=-1`                 | All others: valid baseline      |
| FR17-EP-027 | POST      | EC32 — `max_uses_per_user` field missing       | `max_uses_per_user` omitted            | All others: valid baseline      |
| FR17-EP-028 | POST      | EC33 — `max_uses_per_user` non-integer float   | `max_uses_per_user=1.5`                | All others: valid baseline      |
| FR17-EP-029 | DELETE    | EC35 — non-existent coupon ID                  | `/api/admin/coupons/99999`             | Auth: admin JWT                 |
| FR17-EP-030 | DELETE    | EC36 — non-numeric ID in URL path              | `/api/admin/coupons/abc`               | Auth: admin JWT                 |
| FR17-EP-031 | DELETE    | EC37 — ID = 0 (invalid zero)                   | `/api/admin/coupons/0`                 | Auth: admin JWT                 |
| FR17-EP-032 | POST      | EC39 — no token (POST create)                  | `Authorization` header omitted         | All body fields: valid baseline |
| FR17-EP-033 | POST      | EC40 — user JWT calling admin POST endpoint    | `Authorization: Bearer <user JWT>`     | All body fields: valid baseline |
| FR17-EP-034 | POST      | EC41 — invalid/malformed JWT (POST)            | `Authorization: Bearer invalidtoken`   | All body fields: valid baseline |
| FR17-EP-035 | DELETE    | EC39 — no token (DELETE)                       | `Authorization` header omitted         | `id`: existing (1)              |
| FR17-EP-036 | DELETE    | EC40 — user JWT calling admin DELETE endpoint  | `Authorization: Bearer <user JWT>`     | `id`: existing (1)              |
| FR17-EP-037 | GET       | EC39 — no token (GET coupons)                  | `Authorization` header omitted         | N/A (GET, no body)              |
| FR17-EP-038 | GET       | EC40 — user JWT calling GET /api/coupons       | `Authorization: Bearer <user JWT>`     | N/A (GET, no body)              |

> **Invalid ECs covered:** EC03–EC05, EC08–EC11, EC13–EC16, EC18, EC20–EC23, EC26–EC27, EC30–EC33, EC35–EC37, EC39–EC41 **(28/28 — 100% coverage)**

#### 3.3 EC Coverage Summary

| Total ECs | Valid ECs | Invalid ECs | TCs for Valid | TCs for Invalid | Total TCs |
| --------- | --------- | ----------- | ------------- | --------------- | --------- |
| 41        | 13        | 28          | 6             | 32              | **38**    |

## C. Boundary Value Analysis (BVA Walkthrough)

For every continuous or ordered variable identified in the previous step, we apply Step 4: The 9-point Boundary Value Analysis strategy to thoroughly test the edges of valid and invalid classes.

### Boundary Variables Identified

From EP Step 2 and the requirement analysis, the following ordered/numeric variables have boundary constraints and require BVA:

| Variable               | Data Type         | LB           | UB                                      | Increment   | Source           |
| ---------------------- | ----------------- | ------------ | --------------------------------------- | ----------- | ---------------- |
| `discount_value`       | number (float)    | >0 (LB = 1)  | unspecified (semantic: 100 for percent) | 1 (integer) | FR-17            |
| `min_order_amount`     | number (float)    | >=0 (LB = 0) | unspecified                             | 1           | FR-17            |
| `max_uses_per_user`    | integer           | >=1 (LB = 1) | unspecified                             | 1           | FR-17            |
| `expired_at`           | date (YYYY-MM-DD) | >= today     | unspecified                             | 1 day       | Logical BR-05    |
| `code` (string length) | integer (length)  | >=1 (LB = 1) | unspecified                             | 1 char      | FR-17 (implicit) |

> **Variables excluded from BVA:** `type` (discrete enum, no ordering), `id` (not length-constrained, covered by EP), `auth_token`/`user_role` (discrete states, not ordered numeric).

### BVA Table 1: `discount_value` (numeric)

**Constraint:** must be > 0 (per FR-17, BR-04)  
**LB = 1 (first valid positive integer; 0 is the exact invalid boundary), UB = unspecified**  
**Semantic UB for `type="percent"`: 100 is meaningful; 101 exceeds full discount**

| TC ID        | BVA Point       | Test Value  | Type Param  | Valid/Invalid    | Expected Result                                                             |
| ------------ | --------------- | ----------- | ----------- | ---------------- | --------------------------------------------------------------------------- |
| FR17-BVA-001 | -α (null/empty) | _(omitted)_ | `"percent"` | Invalid          | HTTP 400 — `discount_value` is required (per BR-04, FR-17)                  |
| FR17-BVA-002 | LB-1 (below LB) | `0`         | `"percent"` | Invalid          | HTTP 400 — `discount_value` must be > 0 (per BR-04, FR-17)                  |
| FR17-BVA-003 | LB (exact)      | `1`         | `"percent"` | Valid            | HTTP 200/201 — coupon created successfully (per FR-17)                      |
| FR17-BVA-004 | LB+1            | `2`         | `"percent"` | Valid            | HTTP 200/201 — coupon created successfully (per FR-17)                      |
| FR17-BVA-005 | Nominal         | `50`        | `"percent"` | Valid            | HTTP 200/201 — coupon created successfully (per FR-17)                      |
| FR17-BVA-006 | Semantic UB-1   | `99`        | `"percent"` | Valid            | HTTP 200/201 — coupon created successfully (per FR-17)                      |
| FR17-BVA-007 | Semantic UB     | `100`       | `"percent"` | Valid            | HTTP 200/201 — 100% discount coupon accepted (per FR-17; SRS silent on UB)  |
| FR17-BVA-008 | Semantic UB+1   | `101`       | `"percent"` | Invalid (likely) | HTTP 400 — exceeds 100% for percent type; SRS silent — **flag if accepted** |
| FR17-BVA-009 | +α (very large) | `9999999`   | `"percent"` | Invalid (likely) | HTTP 400 or system error — test DB storage limit (per implicit constraint)  |

> **Note on UB:** The SRS does not state an explicit upper bound for `discount_value`. The semantic boundary at 100 applies only to `type="percent"`. For `type="fixed"`, there is no semantic UB — BVA-007 and BVA-008 use `"percent"` type specifically to probe this semantic boundary. BVA-009 applies to both types.

**All other inputs in BVA-001 through BVA-009 use valid baseline:** `code="BVATEST01"`, `type="percent"` (unless noted), `expired_at="2099-12-31"`, `min_order_amount=100000`, `max_uses_per_user=1`, auth=Admin JWT.

### BVA Table 2: `min_order_amount` (numeric)

**Constraint:** must be >= 0 (per FR-17, BR-06) — zero is explicitly valid  
**LB = 0, UB = unspecified**

| TC ID        | BVA Point       | Test Value  | Valid/Invalid  | Expected Result                                                                    |
| ------------ | --------------- | ----------- | -------------- | ---------------------------------------------------------------------------------- |
| FR17-BVA-010 | -α (null/empty) | _(omitted)_ | Invalid        | HTTP 400 — `min_order_amount` is required (per BR-06, FR-17)                       |
| FR17-BVA-011 | LB-1 (below LB) | `-1`        | Invalid        | HTTP 400 — `min_order_amount` must be >= 0 (per BR-06, FR-17)                      |
| FR17-BVA-012 | LB (exact)      | `0`         | Valid          | HTTP 200/201 — zero min order accepted; coupon has no threshold (per BR-06, FR-17) |
| FR17-BVA-013 | LB+1            | `1`         | Valid          | HTTP 200/201 — coupon created successfully (per FR-17)                             |
| FR17-BVA-014 | Nominal         | `100000`    | Valid          | HTTP 200/201 — coupon created successfully (per FR-17)                             |
| FR17-BVA-015 | +α (very large) | `999999999` | Valid (likely) | HTTP 200/201 — test DB storage limit for large monetary value                      |

> **Note on LB:** `min_order_amount = 0` is **explicitly valid** per FR-17 (`>= 0`). This is a high-risk boundary — AI tends to treat 0 as invalid. This TC (BVA-012) must PASS.

**All other inputs use valid baseline:** `code="BVATEST02"`, `type="percent"`, `discount_value=15`, `expired_at="2099-12-31"`, `max_uses_per_user=1`, auth=Admin JWT.

### BVA Table 3: `max_uses_per_user` (integer)

**Constraint:** must be >= 1 (per FR-17, BR-07) — zero is explicitly invalid  
**LB = 1, UB = unspecified**

| TC ID        | BVA Point       | Test Value  | Valid/Invalid  | Expected Result                                                                               |
| ------------ | --------------- | ----------- | -------------- | --------------------------------------------------------------------------------------------- |
| FR17-BVA-016 | -α (null/empty) | _(omitted)_ | Invalid        | HTTP 400 — `max_uses_per_user` is required (per BR-07, FR-17)                                 |
| FR17-BVA-017 | LB-1 (below LB) | `0`         | Invalid        | HTTP 400 — `max_uses_per_user` must be >= 1; value 0 makes coupon unusable (per BR-07, FR-17) |
| FR17-BVA-018 | LB (exact)      | `1`         | Valid          | HTTP 200/201 — coupon created; each user may use it exactly once (per BR-07, FR-17)           |
| FR17-BVA-019 | LB+1            | `2`         | Valid          | HTTP 200/201 — coupon created successfully (per FR-17)                                        |
| FR17-BVA-020 | Nominal         | `10`        | Valid          | HTTP 200/201 — coupon created successfully (per FR-17)                                        |
| FR17-BVA-021 | +α (very large) | `9999`      | Valid (likely) | HTTP 200/201 — test DB storage limit for large integer value                                  |

> **Note on LB:** `max_uses_per_user = 0` is **explicitly prohibited** — this is a high-risk class where the system may silently accept 0, making the coupon permanently unusable in practice. BVA-017 is a critical security/correctness test.

**All other inputs use valid baseline:** `code="BVATEST03"`, `type="percent"`, `discount_value=15`, `expired_at="2099-12-31"`, `min_order_amount=100000`, auth=Admin JWT.

### BVA Table 4: `expired_at` (date — boundary at today)

**Constraint:** must be >= today's date at creation time (per logical BR-05)  
**LB = today, UB = unspecified (any future date is valid)**

> **Reference date for test cases:** `TODAY` = current system date at execution time (e.g., 2026-06-18). Testers must substitute actual date at execution time.

| TC ID        | BVA Point        | Test Value      | Valid/Invalid  | Expected Result                                                                          |
| ------------ | ---------------- | --------------- | -------------- | ---------------------------------------------------------------------------------------- |
| FR17-BVA-022 | -α (null/empty)  | _(omitted)_     | Invalid        | HTTP 400 — `expired_at` is required (per BR-05, FR-17)                                   |
| FR17-BVA-023 | LB-1 (yesterday) | `TODAY - 1 day` | Invalid        | HTTP 400 — past date is invalid; cannot create already-expired coupon (per BR-05, FR-17) |
| FR17-BVA-024 | LB (today)       | `TODAY`         | Valid          | HTTP 200/201 — coupon expires at end of today; created successfully (per BR-05)          |
| FR17-BVA-025 | LB+1 (tomorrow)  | `TODAY + 1 day` | Valid          | HTTP 200/201 — coupon created with tomorrow's expiry (per FR-17)                         |
| FR17-BVA-026 | Nominal          | `"2027-12-31"`  | Valid          | HTTP 200/201 — standard future expiry date (per FR-17)                                   |
| FR17-BVA-027 | +α (far future)  | `"9999-12-31"`  | Valid (likely) | HTTP 200/201 — test DB storage limit for very far future date                            |

> **Critical execution note:** BVA-023 (LB-1 = yesterday) and BVA-024 (LB = today) **must be computed dynamically at execution time**. Use `date -d "yesterday" +%Y-%m-%d` and `date +%Y-%m-%d` in the test script to get the correct values. Do NOT hardcode dates that will become stale.

**All other inputs use valid baseline:** `code="BVATEST04"`, `type="percent"`, `discount_value=15`, `min_order_amount=100000`, `max_uses_per_user=1`, auth=Admin JWT.

### BVA Table 5: `code` (string length)

**Constraint:** must be non-empty (length >= 1) per FR-17; no explicit upper bound in SRS  
**LB = 1 character, UB = unspecified (typical DB VARCHAR limit = 50 or 255 chars)**

| TC ID        | BVA Point       | Test Value   | Length | Valid/Invalid    | Expected Result                                                                  |
| ------------ | --------------- | ------------ | ------ | ---------------- | -------------------------------------------------------------------------------- |
| FR17-BVA-028 | -α (empty)      | `""`         | 0      | Invalid          | HTTP 400 — `code` must not be empty (per BR-01, FR-17)                           |
| FR17-BVA-029 | LB (1 char)     | `"A"`        | 1      | Valid            | HTTP 200/201 — single-char code accepted (per FR-17)                             |
| FR17-BVA-030 | LB+1 (2 chars)  | `"AB"`       | 2      | Valid            | HTTP 200/201 — coupon created successfully (per FR-17)                           |
| FR17-BVA-031 | Nominal         | `"SUMMER25"` | 8      | Valid            | HTTP 200/201 — typical coupon code length (per FR-17)                            |
| FR17-BVA-032 | UB-1 (49 chars) | `"A" × 49`   | 49     | Valid (likely)   | HTTP 200/201 — near typical DB VARCHAR(50) limit                                 |
| FR17-BVA-033 | UB (50 chars)   | `"A" × 50`   | 50     | Valid (likely)   | HTTP 200/201 — at typical DB VARCHAR(50) limit                                   |
| FR17-BVA-034 | UB+1 (51 chars) | `"A" × 51`   | 51     | Invalid (likely) | HTTP 400 or DB truncation — test whether system rejects or silently truncates    |
| FR17-BVA-035 | +α (very long)  | `"A" × 300`  | 300    | Invalid (likely) | HTTP 400 or system error — DB truncation/overflow risk (per implicit constraint) |

> **Note on UB:** The SRS does not state a maximum `code` length. The 50-char boundary is a hypothesis based on typical SQLite VARCHAR conventions. UB-1, UB, UB+1 assume 50 as the likely limit — **flag actual DB schema column length during execution**. If the actual column is VARCHAR(255), adjust UB to 255.

**All other inputs use valid baseline:** `type="percent"`, `discount_value=15`, `expired_at="2099-12-31"`, `min_order_amount=100000`, `max_uses_per_user=1`, auth=Admin JWT.

### BVA Summary

| Variable            | LB    | UB (Semantic/Implicit)   | BVA Points Tested | Valid TCs | Invalid TCs | Total BVA TCs |
| ------------------- | ----- | ------------------------ | ----------------- | --------- | ----------- | ------------- |
| `discount_value`    | 1     | 100 (percent semantic)   | 9                 | 5         | 4           | 9             |
| `min_order_amount`  | 0     | unspecified              | 6                 | 4         | 2           | 6             |
| `max_uses_per_user` | 1     | unspecified              | 6                 | 4         | 2           | 6             |
| `expired_at`        | today | unspecified (far future) | 6                 | 4         | 2           | 6             |
| `code` length       | 1     | unspecified (assumed 50) | 8                 | 5         | 3           | 8             |
| **Total**           |       |                          | **35**            | **22**    | **13**      | **35**        |

## D. Coverage Review & AI Gap Analysis

### 1. EP Guidelines Compliance

| Variable            | Guideline Applied | Valid Classes | Invalid Classes | Status |
| ------------------- | ----------------- | ------------- | --------------- | ------ |
| `code`              | G3 + G4           | 2             | 3               | Pass   |
| `type`              | G2                | 2             | 4               | Pass   |
| `discount_value`    | G1 + G4           | 2             | 5               | Pass   |
| `expired_at`        | G3 + G4           | 1             | 4               | Pass   |
| `min_order_amount`  | G1                | 2             | 2               | Pass   |
| `max_uses_per_user` | G1                | 2             | 4               | Pass   |
| `id`                | G1 + G3           | 1             | 3               | Pass   |
| `auth` + `role`     | G2                | 1             | 3               | Pass   |

### 2. Missing Classes Found

| #   | Missing Class                            | Reason                                       | Action Taken                            |
| --- | ---------------------------------------- | -------------------------------------------- | --------------------------------------- |
| 1   | `expired_at` past date at creation       | AI strictly followed SRS silence on creation | Corrected EC20 to Invalid during review |
| 2   | Implicit limits (e.g. `code` length 255) | Often omitted unless explicitly modeled      | Covered by `+α` in BVA Phase            |

### 3. Rule Violations Found

| TC ID | Violation | Description                                                                             | Fix Applied |
| ----- | --------- | --------------------------------------------------------------------------------------- | ----------- |
| —     | None      | All invalid TCs correctly follow the Isolation Rule. Valid TCs follow Combination Rule. | —           |

### 4. BVA Completeness

| Variable            | BVA Applied | Points Generated | Missing Points |
| ------------------- | ----------- | ---------------- | -------------- |
| `discount_value`    | Yes         | 9                | None           |
| `min_order_amount`  | Yes         | 6                | None           |
| `max_uses_per_user` | Yes         | 6                | None           |
| `expired_at`        | Yes         | 6                | None           |
| `code` length       | Yes         | 8                | None           |

### 5. AI Gap Analysis

#### What AI Did Correctly

- Successfully identified all direct and indirect input variables, including system state (`code_uniqueness`) and request headers (`auth_token`).
- Perfectly enforced the Isolation Rule across 32 invalid equivalence partition test cases, ensuring no defect masking could occur.
- Efficiently applied the Combination Rule to compress 13 valid classes into 5 happy-path test cases.
- Implemented high-quality dynamic boundary generation for date fields (`TODAY`, `TODAY - 1 day`) to prevent brittle hardcoded dates.

#### What AI Missed

1. **`expired_at` past date invalidity at creation time**
   - **Description:** Initially, the AI classified past dates for `expired_at` as a Valid class, observing that FR-09's expiry check happens at usage time and the creation SRS was silent.
   - **Root cause:** Feature complexity / AI limitation — The AI strictly adhered to the literal explicit constraints of the SRS and failed to apply the implicit logical domain rule that creating an already-expired coupon serves no valid business purpose.

#### Root Cause Summary

| Category           | % Share | Description                                                  |
| ------------------ | ------- | ------------------------------------------------------------ |
| Prompt quality     | 0%      | Human explicitly provided high-risk constraints.             |
| AI limitation      | 50%     | Struggles to prioritize logical deduction over literal text. |
| Feature complexity | 50%     | SRS silence created ambiguity requiring human override.      |

#### Lesson Learned

AI excels at extracting explicit constraints from the SRS but struggles with implicit business logic and common sense. When the SRS is silent on a specific detail (like whether a newly created coupon can already be expired), the AI will default to a literal interpretation and allow it. As a QA engineer, my core value in collaborating with AI is providing the real-world business context and enforcing logical boundaries that the documentation misses.

### 6. Final Summary After Review

| Category      | Before Review | Added/Modified | After Review |
| ------------- | ------------- | -------------- | ------------ |
| Valid ECs     | 14            | -1             | 13           |
| Invalid ECs   | 27            | +1             | 28           |
| BVA Points    | 35            | 0              | 35           |
| **Total TCs** | 73            | 0              | 73           |

# FR-03 — Forgot Password & Reset Password for Mobile (Pool D)

## A. Requirement Analysis

To begin Domain Testing, we first analyze the requirement to identify all explicit and implicit constraints, actors, and business rules governing **Forgot Password & Reset Password**. This step forms the foundation for extracting variables.

### 1. Feature Overview

| Attribute         | Value                                                      |
| ----------------- | ---------------------------------------------------------- |
| Feature ID        | FR-03                                                      |
| Feature Name      | Forgot Password & Reset Password (Mobile)                  |
| Test Layer        | Both (Mobile UI + API)                                     |
| Entry Point (UI)  | React Native App (Forgot Password Screen)                  |
| Entry Point (API) | `POST /api/forgot-password` and `POST /api/reset-password` |
| Actors            | Anonymous                                                  |
| Auth Required     | No                                                         |

### 2. Input Fields & Constraints

| Field/Param        | Layer    | Type   | Constraints                                                                            | Source        |
| ------------------ | -------- | ------ | -------------------------------------------------------------------------------------- | ------------- |
| email (Step 1)     | UI + API | string | Valid format, must be an already registered email                                      | FR-03         |
| email (Step 2)     | UI + API | string | Valid format, must exactly match the email that requested the OTP                      | FR-03, SEC-07 |
| OTP / resetToken   | UI + API | string | Exactly 6 random digits, not expired, not previously used                              | FR-03, SEC-07 |
| newPassword        | UI + API | string | Min 8 chars, at least 1 uppercase, 1 lowercase, 1 digit, 1 special char from `@$!%*?&` | FR-03, FR-01  |
| confirmNewPassword | UI       | string | Must exactly match `newPassword`                                                       | FR-03         |

### 3. Business Rules

- [BR-01] Email must be an existing, registered email in the system to request an OTP (per FR-03).
- [BR-02] New password must be at least 8 characters long and contain at least 1 uppercase letter, 1 lowercase letter, 1 digit, and 1 special character from the set `@$!%*?&` (per FR-03, FR-01).
- [BR-03] Confirm new password must exactly match the new password (per FR-03).
- [BR-04] OTP must be exactly 6 digits, requested by the same email, have not expired, and not used before (per FR-03, SEC-07).
- [BR-05] OTP is strictly bound to the requesting email and cannot be used to reset the password for a different email (per FR-03, SEC-07).

### 4. Expected Outputs

#### 4.1 Success Path

- HTTP: `200 OK`
  - `POST /api/forgot-password` returns `{"message": "Mã đặt lại mật khẩu đã được tạo", "resetToken": "..."}`
  - `POST /api/reset-password` returns `{"message": "Password reset successfully"}`
- UI:
  - After requesting OTP: Transitions from Step 1 to Step 2, Step Indicator updates.
  - After resetting password: Redirects to Login screen with a success toast/message.
- DB: User's password hash is updated. The used OTP is invalidated or deleted.

#### 4.2 Failure Paths

- Invalid/Unregistered Email: HTTP error (e.g., 400/404) + error message indicating email not found or invalid format.
- Invalid OTP (wrong, expired, reused, cross-email): HTTP error (e.g., 400) + error message indicating invalid OTP.
- Weak Password: HTTP error (e.g., 400) + error message detailing the password requirements.
- Passwords Do Not Match: UI validation error displayed above the submit button.

### 5. GUI Requirements Applicable (FR-21~24)

- [GUI-01] All required fields must have a `*` symbol next to the label (per FR-22).
- [GUI-02] Error messages must appear ABOVE the submit button (per FR-22).
- [GUI-03] The form has 2 steps, so it must display a Step Indicator (e.g., "Bước 1 / 2") (per FR-22, FR-03).
- [GUI-04] Must have a "Quay lại đăng nhập" (Back to login) button (per FR-03).
- [GUI-05] Positive action buttons must use blue color (per FR-21).
- [GUI-06] Password fields must obscure input (e.g., using `secureTextEntry` in React Native) (per FR-22).
- [GUI-07] The Email input field must trigger the email-optimized virtual keyboard (e.g., `keyboardType="email-address"`) (Mobile equivalent of `type="email"` per FR-22).

### 6. Security Requirements Applicable (SEC-xx)

- [SEC-01] Password must not be stored as plaintext in the database.
- [SEC-07] OTP must have sufficient entropy (6 digits), be time-limited, and be invalidated after use. It must be strictly bound to the requesting email.

### 7. Notes for Domain Testing

- **Input variables identified:** email (Step 1), email (Step 2), OTP / resetToken, newPassword, confirmNewPassword
- **Output variables identified:** HTTP status, HTTP response body, UI step transition, UI error message, DB password hash update, DB OTP status
- **Boundary candidates:** newPassword length (8 chars limit), OTP length (exactly 6 digits)
- **High-risk areas:** OTP cross-email attack (using valid OTP from email A to reset email B), OTP reuse attack (resetting twice with same OTP), password validation logic with characters outside the specific special character set.
- **AI blind spot warnings:** Missing cross-email OTP validation class, forgetting to test OTP reuse, missing check for exactly 6 digits OTP, assuming `confirmPassword` is sent to API (it's UI only).

## B. Domain Analysis (Equivalence Partitioning Walkthrough)

Based on the identified constraints, we proceed to Step 1: Identifying Variables (both input and output) and Step 2 & 3: Dividing them into valid and invalid Equivalence Classes applying the 4 EP guidelines.

### Step 1: Input & Output Variable Identification

#### 1.1 Input Variables

##### Direct Inputs (UI Form / API Body)

| #   | Variable             | Source             | Type   | Description                         |
| --- | -------------------- | ------------------ | ------ | ----------------------------------- |
| I1  | `email_step1`        | UI form + API body | string | Email entered to request OTP        |
| I2  | `email_step2`        | UI form + API body | string | Email entered during password reset |
| I3  | `otp_code`           | UI form + API body | string | 6-digit reset token/OTP             |
| I4  | `newPassword`        | UI form + API body | string | The new password                    |
| I5  | `confirmNewPassword` | UI form only       | string | Password confirmation               |

##### Indirect Inputs (Hidden / System State)

| #   | Variable                 | Source      | Type    | Description                                            |
| --- | ------------------------ | ----------- | ------- | ------------------------------------------------------ |
| I6  | `email_registered_state` | DB state    | boolean | Whether the email exists in the database               |
| I7  | `otp_expiry_state`       | System time | boolean | Whether the OTP is still within its validity window    |
| I8  | `otp_used_state`         | DB state    | boolean | Whether the OTP has already been used                  |
| I9  | `otp_bound_email`        | DB state    | string  | The email address the OTP was originally generated for |

#### 1.2 Output Variables

##### Direct Outputs (Visible)

| #   | Variable             | Channel | Description                                         |
| --- | -------------------- | ------- | --------------------------------------------------- |
| O1  | HTTP status code     | API     | 200 OK or 4xx/5xx Error                             |
| O2  | Response body        | API     | Success message or error details                    |
| O3  | UI step transition   | UI      | Move from Step 1 to Step 2                          |
| O4  | UI redirect          | UI      | Navigate to Login page after successful reset       |
| O5  | UI error message     | UI      | Validation errors above submit button               |
| O6  | UI step indicator    | UI      | Update from "Bước 1 / 2" to "Bước 2 / 2"            |
| O9  | UI_secure_text_entry | UI      | Password fields correctly apply `secureTextEntry`   |
| O10 | UI_keyboard_type     | UI      | Email field triggers `keyboardType="email-address"` |
| O11 | UI_required_marker   | UI      | Required fields display the `*` symbol              |
| O12 | UI_button_color      | UI      | Positive action buttons are blue                    |
| O13 | UI_back_button       | UI      | "Quay lại đăng nhập" button is present              |

##### Indirect Outputs (Hidden / State Changes)

| #   | Variable         | Channel | Description                                        |
| --- | ---------------- | ------- | -------------------------------------------------- |
| O7  | DB password hash | State   | User's password record updated in database         |
| O8  | DB OTP state     | State   | OTP is invalidated or deleted after successful use |

#### 1.3 Variable Summary for EP

- **Total inputs identified:** 9 (5 direct + 4 indirect)
- **Total outputs identified:** 13 (11 direct + 2 indirect)
- **Variables requiring EP:** `email_step1`, `email_step2`, `otp_code`, `newPassword`, `confirmNewPassword`, `email_registered_state`, `otp_expiry_state`, `otp_used_state`, `otp_bound_email`
- **Boundary candidates:** `newPassword` (length min 8 characters), `otp_code` (length exactly 6 digits)

### Step 2: Equivalence Classes

#### Variable: `email_step1` & `email_registered_state` — Guideline 3 × 2 (Must-Be: format & exists in DB) + Guideline 4 (sub-split) + B1

| Class ID | Type    | Description                                    | Representative Value           |
| -------- | ------- | ---------------------------------------------- | ------------------------------ |
| EC01     | Valid   | Valid format AND exists in DB                  | `"test@eshop.com"`             |
| EC02     | Invalid | Invalid format — missing `@` symbol            | `"invalidemail"`               |
| EC03     | Invalid | Invalid format — missing domain after `@`      | `"user@"`                      |
| EC04     | Invalid | Invalid format — missing local part before `@` | `"@domain.com"`                |
| EC05     | Invalid | Valid format, but does NOT exist in DB         | `"unknown@eshop.com"`          |
| EC06     | Invalid | Empty string (B1)                              | `""`                           |
| EC07     | Invalid | Null / missing in API body (B1)                | _(omit `email` key from JSON)_ |

#### Variable: `email_step2` — Guideline 3 × 2 (Must-Be: format & match step 1) + Guideline 4 (sub-split) + B1

| Class ID | Type    | Description                                    | Representative Value           |
| -------- | ------- | ---------------------------------------------- | ------------------------------ |
| EC08     | Valid   | Valid format AND matches `email_step1`         | `"test@eshop.com"`             |
| EC09     | Invalid | Valid format, but does NOT match `email_step1` | `"different@eshop.com"`        |
| EC10     | Invalid | Invalid format — missing `@` symbol            | `"invalidemail"`               |
| EC11     | Invalid | Invalid format — missing domain after `@`      | `"user@"`                      |
| EC12     | Invalid | Invalid format — missing local part before `@` | `"@domain.com"`                |
| EC13     | Invalid | Empty string (B1)                              | `""`                           |
| EC14     | Invalid | Null / missing in API body (B1)                | _(omit `email` key from JSON)_ |

#### Variable: `otp_code` & Context — Guideline 1 (Range) + Guideline 3 (Must-Be) + B1

| Class ID | Type    | Description                                          | Representative Value                  |
| -------- | ------- | ---------------------------------------------------- | ------------------------------------- |
| EC15     | Valid   | Correct OTP digits for correct email, active, unused | `"123456"`                            |
| EC16     | Invalid | Wrong OTP digits                                     | `"999999"`                            |
| EC17     | Invalid | OTP from a different email (cross-email attack)      | OTP generated for `"admin@eshop.com"` |
| EC18     | Invalid | OTP already used (reuse attempt)                     | `"123456"` (already used)             |
| EC19     | Invalid | OTP expired                                          | `"123456"` (expired 1 min ago)        |
| EC20     | Invalid | OTP length < 6 digits                                | `"12345"`                             |
| EC21     | Invalid | OTP length > 6 digits                                | `"1234567"`                           |
| EC22     | Invalid | Empty OTP string (B1)                                | `""`                                  |
| EC23     | Invalid | Null / missing in API body (B1)                      | _(omit `resetToken` key)_             |

#### Variable: `newPassword` — Guideline 1 (Range: length ≥ 8) + Guideline 3 × 4 (char types) + Guideline 4 (split special char) + B1

| Class ID | Type    | Description                                                                  | Representative Value          |
| -------- | ------- | ---------------------------------------------------------------------------- | ----------------------------- |
| EC24     | Valid   | Length ≥ 8; has uppercase, lowercase, digit, and special char from `@$!%*?&` | `"Test@123"`                  |
| EC25     | Invalid | Length < 8 (G1)                                                              | `"Te@1"` (4 chars)            |
| EC26     | Invalid | Missing uppercase letter (G3)                                                | `"test@123"`                  |
| EC27     | Invalid | Missing lowercase letter (G3)                                                | `"TEST@123"`                  |
| EC28     | Invalid | Missing digit (G3)                                                           | `"Test@abc"`                  |
| EC29     | Invalid | Missing any special character (G3)                                           | `"Test1234"`                  |
| EC30     | Invalid | Special character present but OUTSIDE allowed set `@$!%*?&` (G4)             | `"Test#123"` (`#` not in set) |
| EC31     | Invalid | Empty string (B1)                                                            | `""`                          |
| EC32     | Invalid | Null / missing in API body (B1)                                              | _(omit `newPassword` key)_    |

#### Variable: `confirmNewPassword` — Guideline 3 (Must-Be: match `newPassword`) + B1 — UI channel only

| Class ID | Type    | Description                                   | Representative Value              |
| -------- | ------- | --------------------------------------------- | --------------------------------- |
| EC33     | Valid   | Exactly matches `newPassword` field           | Same value as EC23 (`"Test@123"`) |
| EC34     | Invalid | Does not match `newPassword` field (mismatch) | `"DifferentPass@1"`               |
| EC35     | Invalid | Empty confirm password (B1)                   | `""`                              |

### Step 3: Test Case Optimization

#### 3.1 Valid Classes Coverage (Combination Rule)

| TC ID       | Valid Classes Covered        | Test Data Summary                                              | Channel          |
| ----------- | ---------------------------- | -------------------------------------------------------------- | ---------------- |
| FR03-EP-001 | EC01, EC08, EC15, EC24, EC33 | Step 1: Valid email. Step 2: Valid OTP, New Pw, and Confirm Pw | UI + API + State |

#### 3.2 Invalid Classes Coverage (Isolation Rule)

_Note on Isolation Rule for `newPassword` constraints: To prevent defect masking (e.g., getting a "passwords do not match" error instead of a "weak password" error), `confirmNewPassword` must always exactly mirror the invalid `newPassword` value._

| TC ID       | Invalid Class Tested                                 | Other Inputs                                                | Channel  |
| ----------- | ---------------------------------------------------- | ----------------------------------------------------------- | -------- |
| FR03-EP-002 | EC02 (Email Step 1 invalid: no `@`)                  | N/A (Fails at Step 1)                                       | UI + API |
| FR03-EP-003 | EC03 (Email Step 1 invalid: no domain)               | N/A (Fails at Step 1)                                       | UI + API |
| FR03-EP-004 | EC04 (Email Step 1 invalid: no local part)           | N/A (Fails at Step 1)                                       | UI + API |
| FR03-EP-005 | EC05 (Email Step 1 does not exist in DB)             | N/A (Fails at Step 1)                                       | UI + API |
| FR03-EP-006 | EC06 (Email Step 1 empty)                            | N/A (Fails at Step 1)                                       | UI + API |
| FR03-EP-007 | EC07 (Email Step 1 missing in API body)              | N/A (Fails at Step 1)                                       | API      |
| FR03-EP-008 | EC09 (Email Step 2 mismatch)                         | otp = valid, newPw = valid, confirmPw = valid               | API      |
| FR03-EP-009 | EC10 (Email Step 2 invalid: no `@`)                  | otp = valid, newPw = valid, confirmPw = valid               | API      |
| FR03-EP-010 | EC11 (Email Step 2 invalid: no domain)               | otp = valid, newPw = valid, confirmPw = valid               | API      |
| FR03-EP-011 | EC12 (Email Step 2 invalid: no local part)           | otp = valid, newPw = valid, confirmPw = valid               | API      |
| FR03-EP-012 | EC13 (Email Step 2 empty)                            | otp = valid, newPw = valid, confirmPw = valid               | API      |
| FR03-EP-013 | EC14 (Email Step 2 missing in API body)              | otp = valid, newPw = valid, confirmPw = valid               | API      |
| FR03-EP-014 | EC16 (Wrong OTP digits)                              | email2 = valid, newPw = valid, confirmPw = valid            | UI + API |
| FR03-EP-015 | EC17 (OTP from different email - cross-email attack) | email2 = valid, newPw = valid, confirmPw = valid            | UI + API |
| FR03-EP-016 | EC18 (OTP already used - reuse attempt)              | email2 = valid, newPw = valid, confirmPw = valid            | UI + API |
| FR03-EP-017 | EC19 (OTP expired)                                   | email2 = valid, newPw = valid, confirmPw = valid            | UI + API |
| FR03-EP-018 | EC20 (OTP length < 6)                                | email2 = valid, newPw = valid, confirmPw = valid            | UI + API |
| FR03-EP-019 | EC21 (OTP length > 6)                                | email2 = valid, newPw = valid, confirmPw = valid            | UI + API |
| FR03-EP-020 | EC22 (OTP empty)                                     | email2 = valid, newPw = valid, confirmPw = valid            | UI + API |
| FR03-EP-021 | EC23 (OTP missing in API body)                       | email2 = valid, newPw = valid, confirmPw = valid            | API      |
| FR03-EP-022 | EC25 (newPassword length < 8)                        | email2 = valid, otp = valid, confirmPw = mirrors invalid Pw | UI + API |
| FR03-EP-023 | EC26 (newPassword missing uppercase)                 | email2 = valid, otp = valid, confirmPw = mirrors invalid Pw | UI + API |
| FR03-EP-024 | EC27 (newPassword missing lowercase)                 | email2 = valid, otp = valid, confirmPw = mirrors invalid Pw | UI + API |
| FR03-EP-025 | EC28 (newPassword missing digit)                     | email2 = valid, otp = valid, confirmPw = mirrors invalid Pw | UI + API |
| FR03-EP-026 | EC29 (newPassword missing special char)              | email2 = valid, otp = valid, confirmPw = mirrors invalid Pw | UI + API |
| FR03-EP-027 | EC30 (newPassword special char OUTSIDE allowed set)  | email2 = valid, otp = valid, confirmPw = mirrors invalid Pw | UI + API |
| FR03-EP-028 | EC31 (newPassword empty)                             | email2 = valid, otp = valid, confirmPw = mirrors invalid Pw | UI + API |
| FR03-EP-029 | EC32 (newPassword missing in API body)               | email2 = valid, otp = valid _(no confirm in API)_           | API      |
| FR03-EP-030 | EC34 (confirmNewPassword mismatch)                   | email2 = valid, otp = valid, newPw = valid                  | UI       |
| FR03-EP-031 | EC35 (confirmNewPassword empty)                      | email2 = valid, otp = valid, newPw = valid                  | UI       |

#### 3.3 EC Coverage Summary

| Total ECs | Valid ECs | Invalid ECs | TCs for Valid | TCs for Invalid | Total TCs |
| --------- | --------- | ----------- | ------------- | --------------- | --------- |
| 35        | 5         | 30          | 1             | 30              | 31        |

## C. Boundary Value Analysis (BVA Walkthrough)

For every continuous or ordered variable identified in the previous step, we apply Step 4: The 9-point Boundary Value Analysis strategy to thoroughly test the edges of valid and invalid classes.

### Boundary Variables Identified

| Variable             | Data Type               | LB  | UB             | Increment | Note                                    |
| -------------------- | ----------------------- | --- | -------------- | --------- | --------------------------------------- |
| `newPassword` length | integer (string length) | 8   | unspecified    | 1 char    | Per FR-01/FR-03 password strength rules |
| `otp_code` length    | integer (string length) | 6   | 6              | 1 char    | Exact length required (6 digits)        |
| `email_step1` length | integer (string length) | N/A | 255 (implicit) | 1 char    | Typical DB VARCHAR limit constraint     |

### BVA Table 1: `newPassword` (string length)

**Constraint:** length >= 8 characters  
**LB = 8, UB = unspecified**

| TC ID        | BVA Point      | Test Value                         | Length | Valid/Invalid    | Expected Result            |
| ------------ | -------------- | ---------------------------------- | ------ | ---------------- | -------------------------- |
| FR03-BVA-001 | -α (empty)     | `""`                               | 0      | Invalid          | Reject: missing password   |
| FR03-BVA-002 | LB-1           | `"Test@12"`                        | 7      | Invalid          | Reject: password too short |
| FR03-BVA-003 | LB (exact)     | `"Test@123"`                       | 8      | Valid            | Accept (Success)           |
| FR03-BVA-004 | LB+1           | `"Test@1234"`                      | 9      | Valid            | Accept (Success)           |
| FR03-BVA-005 | Nominal        | `"TestPassword12!"`                | 15     | Valid            | Accept (Success)           |
| FR03-BVA-006 | +α (very long) | `"T" + "e".repeat(290) + "st@123"` | 300    | Invalid (likely) | Reject or system error     |

_Note on Isolation Rule:_ For all points, `confirmNewPassword` must EXACTLY mirror the test value for `newPassword`. This prevents defect masking where the system might reject the request due to a "passwords do not match" error rather than correctly testing the password length rule.

### BVA Table 2: `otp_code` (string length)

**Constraint:** length exactly 6 digits  
**LB = 6, UB = 6**

| TC ID        | BVA Point      | Test Value  | Length | Valid/Invalid | Expected Result                   |
| ------------ | -------------- | ----------- | ------ | ------------- | --------------------------------- |
| FR03-BVA-007 | -α (empty)     | `""`        | 0      | Invalid       | Reject: missing OTP               |
| FR03-BVA-008 | LB-1           | `"12345"`   | 5      | Invalid       | Reject: invalid OTP format/length |
| FR03-BVA-009 | LB/UB (exact)  | `"123456"`  | 6      | Valid         | Accept (OTP verified)             |
| FR03-BVA-010 | UB+1           | `"1234567"` | 7      | Invalid       | Reject: invalid OTP format/length |
| FR03-BVA-011 | +α (very long) | `{"1"×100}` | 100    | Invalid       | Reject or system error            |

### BVA Table 3: `email_step1` (string length)

**Constraint:** length bounded by typical DB VARCHAR limit  
**LB = implicit, UB = 255 (implicit)**

| TC ID        | BVA Point      | Test Value                | Length | Valid/Invalid    | Expected Result        |
| ------------ | -------------- | ------------------------- | ------ | ---------------- | ---------------------- |
| FR03-BVA-012 | UB-1           | `{"a"×245} + "@test.com"` | 254    | Valid            | Accept (OTP requested) |
| FR03-BVA-013 | UB (exact)     | `{"a"×246} + "@test.com"` | 255    | Valid            | Accept (OTP requested) |
| FR03-BVA-014 | UB+1           | `{"a"×247} + "@test.com"` | 256    | Invalid (likely) | Reject: email too long |
| FR03-BVA-015 | +α (very long) | `{"a"×291} + "@test.com"` | 300    | Invalid (likely) | Reject or system error |

_Note:_ Testing `email_step1` length is sufficient to cover DB boundaries. `email_step2` uses identical backend logic for the same underlying DB column constraint.

### BVA Summary

| Variable             | Total BVA Points | Valid Points | Invalid Points | BVA TCs Generated |
| -------------------- | ---------------- | ------------ | -------------- | ----------------- |
| `newPassword` length | 6                | 3            | 3              | 6                 |
| `otp_code` length    | 5                | 1            | 4              | 5                 |
| `email_step1` length | 4                | 2            | 2              | 4                 |
| **Total**            | **15**           | **6**        | **9**          | **15**            |

## D. Coverage Review & AI Gap Analysis

### 1. EP Guidelines Compliance

| Variable             | Guideline Applied     | Valid Classes | Invalid Classes | Status |
| -------------------- | --------------------- | ------------- | --------------- | ------ |
| `email_step1`        | G3 × 2 + G4 + B1      | 1             | 6               | Pass   |
| `email_step2`        | G3 × 2 + G4 + B1      | 1             | 6               | Pass   |
| `otp_code`           | G1 + G3 + B1          | 1             | 8               | Pass   |
| `newPassword`        | G1 + G3 × 4 + G4 + B1 | 1             | 8               | Pass   |
| `confirmNewPassword` | G3 + B1 (UI only)     | 1             | 2               | Pass   |

### 2. Missing Classes Found

| #   | Missing Class                                       | Reason                                           | Action Taken                  |
| --- | --------------------------------------------------- | ------------------------------------------------ | ----------------------------- |
| 1   | Mobile GUI constraints as explicit Output Variables | Output extraction defaulted to backend/web terms | Added O9-O13 via human prompt |
| 2   | OTP cross-email attack (SEC-07)                     | Hidden security rule                             | Added EC17 via human prompt   |
| 3   | OTP reuse attempt (SEC-07)                          | Hidden state-transition rule                     | Added EC18 via human prompt   |

### 3. Rule Violations Found

| TC ID | Violation | Description                                                  | Fix Applied |
| ----- | --------- | ------------------------------------------------------------ | ----------- |
| —     | None      | All invalid TCs correctly isolate exactly one invalid class. | —           |

### 4. BVA Completeness

| Variable             | BVA Applied | Points Generated | Missing Points                         |
| -------------------- | ----------- | ---------------- | -------------------------------------- |
| `newPassword` length | Yes         | 6                | None                                   |
| `otp_code` length    | Yes         | 5                | None (Added +α via human prompt)       |
| `email_step1` length | Yes         | 4                | None (Math corrected via human prompt) |

### 5. AI Gap Analysis

#### What AI Did Correctly

- Identified core input variables and DB state dependencies flawlessly in Step 1.
- Implemented the Isolation Rule perfectly for complex password constraints, including using the "mirror value" strategy for `confirmNewPassword` to prevent defect masking.
- Applied the Combination Rule to successfully group all valid classes into a single Happy Path TC.
- Recognized that `confirmNewPassword` is a UI-only variable and successfully excluded it from API body mutation tests.

#### What AI Missed

1. **Extreme upper boundary (+α) for exact-length fields**
   - Description: AI missed the +α BVA point for `otp_code`, assuming that since the constraint is exactly 6 digits, it only needed to test `LB-1`, `Exact`, and `UB+1`. It failed to test a huge value for buffer overflow.
   - Root cause: Feature complexity / AI limitation — AI tends to follow strict constraint bounds and misses extreme security/infrastructure edge cases unless explicitly prompted.
2. **Mobile GUI attributes as explicit outputs**
   - Description: In Step 1, AI extracted backend logic but missed mapping mobile UI constraints (like `secureTextEntry` or `keyboardType`) as formal Output Variables.
   - Root cause: AI limitation — AI struggles to elevate UI presentation requirements to the same level of structural importance as backend state transitions without prompting.
3. **Exact string length arithmetic for BVA**
   - Description: AI miscalculated the exact number of characters needed for the local part of an email to reach a total string length of exactly 254, 255, and 256.
   - Root cause: AI limitation — LLMs are notoriously imprecise with basic arithmetic and character counting inside string generation formulas.

#### Root Cause Summary

| Category           | % Share | Description                                                               |
| ------------------ | ------- | ------------------------------------------------------------------------- |
| Prompt quality     | 0%      | Context was fully provided                                                |
| AI limitation      | 75%     | Math errors, missing extreme security limits, missing UI variable mapping |
| Feature complexity | 25%     | SEC-07 cross-feature rules and exact-length boundaries                    |

#### Lesson Learned

When generating test cases for Mobile applications and security-critical features, AI models heavily default to Web DOM conventions and literal boundary definitions. QA Engineers must actively intervene to translate web requirements into mobile-native UI constraints (e.g., enforcing `secureTextEntry` or specific virtual keyboard types as explicit test outputs). Furthermore, LLMs possess inherent limitations in arithmetic (failing to correctly calculate string padding for exact boundary lengths) and often miss extreme overflow boundaries (+α) for strictly constrained fields like a 6-digit OTP. Human oversight is absolutely mandatory to enforce mathematical precision, prevent defect masking via the Isolation Rule, and uncover hidden security state vulnerabilities such as OTP cross-email attacks.

### 6. Final Summary After Review

| Category      | Before Review | Added | After Review |
| ------------- | ------------- | ----- | ------------ |
| Valid ECs     | 5             | 0     | 5            |
| Invalid ECs   | 30            | 0     | 30           |
| BVA Points    | 15            | 0     | 15           |
| EP TCs        | 31            | 0     | 31           |
| BVA TCs       | 15            | 0     | 15           |
| **Total TCs** | 46            | 0     | 46           |
