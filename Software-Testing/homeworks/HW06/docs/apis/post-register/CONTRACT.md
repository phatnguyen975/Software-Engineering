# API Contract: POST /api/register

> **Status:** Approved  
> **Last updated:** 2026-08-19  
> **Source documents:** `docs/sut/api-spec.md`, `docs/sut/srs.md`

## 1. Overview

| Property          | Value                                                                                 |
| ----------------- | ------------------------------------------------------------------------------------- |
| **Endpoint**      | `POST /api/register`                                                                  |
| **Feature**       | FR-01 — Account Registration                                                          |
| **Auth required** | No                                                                                    |
| **Auth type**     | None (public endpoint)                                                                |
| **Role required** | None                                                                                  |
| **Idempotency**   | Not idempotent — second call with same email returns 409 Conflict (expected behavior) |

## 2. Request

### 2.1 Headers

| Header          | Required | Value / Format     | Notes                                    |
| --------------- | -------- | ------------------ | ---------------------------------------- |
| `Content-Type`  | Yes      | `application/json` | Required; body must be valid JSON        |
| `Authorization` | No       | N/A                | Endpoint is public; no token is required |

### 2.2 Path Parameters

N/A

### 2.3 Query Parameters

N/A

### 2.4 Request Body

```json
{
  "name": "Nguyen Van A",
  "email": "test@domain.com",
  "password": "Password123!"
}
```

#### Field Constraints

| Field      | Type   | Required | Constraints                                                                                                                                                                                                                                                                            | Default | Notes                                                                                                                                             |
| ---------- | ------ | -------- | -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- | ------- | ------------------------------------------------------------------------------------------------------------------------------------------------- |
| `name`     | string | Yes      | Non-empty; min 1 character; max 100 characters [inferred — OWASP Input Validation Cheat Sheet]; must not be whitespace-only; leading/trailing spaces should be trimmed [inferred — e-commerce convention]                                                                              | N/A     | Accepts Unicode including Vietnamese diacritics. **Current SUT: no validation — NULL and empty string accepted.**                                 |
| `email`    | string | Yes      | RFC 5322 format (`local@domain.tld`); max 254 characters [inferred — RFC 5321 §4.5.3]; must be unique system-wide; case-insensitive comparison [inferred — e-commerce convention]; no whitespace allowed                                                                               | N/A     | **Current SUT: no format validation. No UNIQUE constraint in DB schema. Duplicate emails are NOT rejected — silently inserted as separate rows.** |
| `password` | string | Yes      | Min 8 characters [source — SRS FR-01]; ≥1 uppercase letter A–Z [source — SRS FR-01]; ≥1 lowercase letter a–z [source — SRS FR-01]; ≥1 digit 0–9 [source — SRS FR-01]; ≥1 special character from `@$!%*?&` [source — SRS FR-01]; max 128 characters [inferred — OWASP Auth Cheat Sheet] | N/A     | **Current SUT: no validation — any string accepted including empty. Stored as plaintext (Critical Bug — SEC-01 violation).**                      |

> **`confirmPassword` field:** Required by SRS FR-01 at the UI level. This field does **not** exist at the API level. The backend does not accept or validate `confirmPassword`. N/A at API contract scope.

> **`role` field:** Any `role` value sent in the request body is silently ignored. The `role` column defaults to `'user'`. Role escalation via this endpoint is not possible in the current implementation, but must be verified via a dedicated test case.

## 3. Business Rules

- **BR-01:** All three fields — `name`, `email`, `password` — are **required**. A request missing any of these fields must be rejected with `400 Bad Request`.
- **BR-02:** The `email` field must conform to RFC 5322 email format (`local@domain.tld`). Invalid formats must be rejected with `400 Bad Request`.
- **BR-03:** The `email` address must be **unique** across all registered users. Email uniqueness comparison must be **case-insensitive** [inferred — e-commerce convention]. A duplicate email must be rejected with `409 Conflict`.
- **BR-04:** The `password` must satisfy the strong password policy from SRS FR-01:
  - Minimum **8 characters**
  - At least **1 uppercase letter** (A–Z)
  - At least **1 lowercase letter** (a–z)
  - At least **1 digit** (0–9)
  - At least **1 special character** from the set: `@`, `$`, `!`, `%`, `*`, `?`, `&`
    Passwords failing this policy must be rejected with `400 Bad Request`.
- **BR-05:** The `password` must be stored as a **cryptographic hash** (e.g., bcrypt with cost factor ≥ 10 [inferred — OWASP Auth Cheat Sheet]). Storing or returning plaintext passwords is prohibited (SEC-01).
- **BR-06:** The `name` field must not be empty or consist solely of whitespace characters. Blank names must be rejected with `400 Bad Request`. **[SUT violation: not enforced]**
- **BR-07:** The registered account's `role` is **always** set to `'user'` by default. The caller cannot set the `role` field via this endpoint. Any `role` value in the request body must be **silently ignored**.
- **BR-08:** Upon successful registration, the system creates a new user record with these initial field values:
  - `role` = `'user'`
  - `login_attempts` = `0`
  - `locked_until` = `NULL`
  - `reset_token` = `NULL`
  - `shipping_address` = `NULL`
  - `phone` = `NULL`
- **BR-09:** The success response body must include `message` (string) and `id` (integer). The response must **not** include the `password` field. [Source — api-spec.md §1.1]
- **BR-10:** If the request body is not valid JSON (malformed syntax), the server must return `400 Bad Request`. [Enforced by body-parser middleware]
- **BR-11 [inferred — e-commerce rate limiting convention, OWASP API Security Top 10]:** The registration endpoint should enforce a rate limit of no more than 5 attempts per IP per hour to prevent automated account creation.

## 4. Response Definitions

### 4.1 200 OK — Registration Successful

**Trigger:** All three fields provided with valid values; email does not already exist in system; database INSERT succeeds.

> **Note [inferred — RFC 9110]:** Best practice for resource creation is `201 Created`. The API spec documents `200 OK` as the actual response. Both are noted; test cases cover this discrepancy.

```json
{
  "message": "User registered successfully",
  "id": 1
}
```

**Headers returned:**

- `Content-Type: application/json`

| Field     | Type    | Description                                    |
| --------- | ------- | ---------------------------------------------- |
| `message` | string  | Fixed string: `"User registered successfully"` |
| `id`      | integer | Auto-incremented primary key of the new user   |

### 4.2 400 Bad Request — Validation Failure (Expected Behavior)

**Trigger (contract-correct behavior):** Any of:

- `name` is missing, `null`, empty (`""`), or whitespace-only
- `email` is missing, `null`, empty, or has invalid RFC 5322 format
- `password` is missing, `null`, empty, or fails the strong password policy (BR-04)
- Request body is `{}`
- Request body is not parseable as JSON

> **Note:** The SUT **does NOT return 400** for field-level validation failures. This response is the **expected** behavior per contract, not the current SUT behavior.

```json
{
  "error": "<specific validation error message>"
}
```

**Headers returned:**

- `Content-Type: application/json`

### 4.3 409 Conflict — Duplicate Email (Expected Behavior)

**Trigger (contract-correct behavior):** A user with the same `email` (case-insensitive) already exists in the system.

> **Note:** The SUT **does NOT return 409**. Because the `email` column has no UNIQUE constraint, a duplicate email is silently inserted (returns `200 OK`). If a UNIQUE constraint were added, SQLite would raise a constraint violation which the handler exposes as `500` (see 4.5). This is a multi-layer bug.

```json
{
  "error": "Email already registered"
}
```

**Headers returned:**

- `Content-Type: application/json`

### 4.4 400 Bad Request — Malformed JSON Body

**Trigger:** Request body is not valid JSON syntax, or `Content-Type` header is missing/not `application/json`.

```json
{
  "error": "Bad Request"
}
```

> **Note:** Exact error format depends on body-parser middleware; Express returns its own structure.

**Headers returned:**

- `Content-Type: application/json`

### 4.5 500 Internal Server Error — Database Error / Constraint Violation

**Trigger (actual SUT behavior for some error conditions):** An unhandled database error occurs. The handler passes raw `err.message` to the response. This exposes internal SQLite error messages.

```json
{
  "error": "SQLITE_CONSTRAINT: UNIQUE constraint failed: users.email"
}
```

> **This is a security concern** — raw SQL error messages should never be exposed to clients.

**Headers returned:**

- `Content-Type: application/json`

## 5. State Transitions

### 5.1 HTTP Response State

| Input Condition                                            | Expected HTTP Status        | Actual HTTP Status (SUT)          | Notes                                             |
| ---------------------------------------------------------- | --------------------------- | --------------------------------- | ------------------------------------------------- |
| All fields valid, email not registered                     | `201 Created`               | `200 OK`                          | SUT returns 200 instead of 201                    |
| `name` missing or empty                                    | `400 Bad Request`           | `200 OK` (NULL inserted)          | SUT does not validate                             |
| `email` missing or empty                                   | `400 Bad Request`           | `200 OK` (NULL inserted)          | SUT does not validate                             |
| `email` has invalid format                                 | `400 Bad Request`           | `200 OK` (invalid email inserted) | SUT does not validate                             |
| `password` missing or empty                                | `400 Bad Request`           | `200 OK` (NULL/empty inserted)    | SUT does not validate                             |
| `password` fails complexity rules                          | `400 Bad Request`           | `200 OK` (weak password inserted) | SUT does not validate                             |
| `email` already registered (current DB — no UNIQUE)        | `409 Conflict`              | `200 OK` (duplicate row created)  | No UNIQUE constraint; duplicate silently inserted |
| `email` already registered (if UNIQUE constraint is added) | `409 Conflict`              | `500 Internal Server Error`       | Raw SQLite error exposed                          |
| Malformed JSON body                                        | `400 Bad Request`           | `400 Bad Request`                 | Handled by body-parser                            |
| Database unavailable                                       | `500 Internal Server Error` | `500 Internal Server Error`       | Consistent                                        |

### 5.2 System Data State

| Entity  | Field              | Before              | After                                                                 | Condition                    |
| ------- | ------------------ | ------------------- | --------------------------------------------------------------------- | ---------------------------- |
| `users` | (record)           | Does not exist      | New row inserted                                                      | Successful request           |
| `users` | `name`             | (row did not exist) | Value from request body                                               | Successful request           |
| `users` | `email`            | (row did not exist) | Value from request body (lowercased — expected; actual: stored as-is) | Successful request           |
| `users` | `password`         | (row did not exist) | **Plaintext** value from request body (Bug — should be bcrypt hash)   | Successful request           |
| `users` | `role`             | (row did not exist) | `'user'` (DB default — cannot be overridden)                          | Always on successful request |
| `users` | `login_attempts`   | (row did not exist) | `0` (DB default)                                                      | Always                       |
| `users` | `locked_until`     | (row did not exist) | `NULL`                                                                | Always                       |
| `users` | `reset_token`      | (row did not exist) | `NULL`                                                                | Always                       |
| `users` | `shipping_address` | (row did not exist) | `NULL`                                                                | Always                       |
| `users` | `phone`            | (row did not exist) | `NULL`                                                                | Always                       |

### 5.3 Field-Level State Preconditions

| Entity  | State Field | Allowed Values to Proceed             | Blocked Values                  | Effect When Blocked                                                               |
| ------- | ----------- | ------------------------------------- | ------------------------------- | --------------------------------------------------------------------------------- |
| `users` | `email`     | Email does not exist in `users` table | Email already exists in `users` | Expected: `409 Conflict`; Actual: `200 OK` (duplicate) or `500` (if UNIQUE added) |

## 6. Security Rules

| Rule ID        | Description                                                                                                                                       | Applicable Test Vector                                                                                                                               | Status in SUT                                                      |
| -------------- | ------------------------------------------------------------------------------------------------------------------------------------------------- | ---------------------------------------------------------------------------------------------------------------------------------------------------- | ------------------------------------------------------------------ |
| SEC-01         | Password must not be stored as plaintext; must be hashed with bcrypt or equivalent.                                                               | Register with `"password": "Password123!"`, then attempt login with same password — if login succeeds AND DB row shows plaintext, SEC-01 is violated | **VIOLATED** — SUT stores plaintext                                |
| SEC-01b        | Response body must not contain the `password` field (plaintext or hash).                                                                          | Inspect HTTP `200` response body — verify no `password` key present                                                                                  | **COMPLIANT** — response only returns `{message, id}`              |
| SEC-04 (XSS)   | User-supplied `name` stored in DB must be safely rendered in UI (no raw `innerHTML`). Applies when name is later displayed in user profile pages. | Send `name` = `<script>alert(1)</script>` — verify API accepts or rejects; then verify that if stored, it is escaped when displayed                  | Needs frontend verification                                        |
| SEC-05 (SQLi)  | All DB queries must use parameterized queries. No string concatenation with user input.                                                           | Send `email` = `'; DROP TABLE users;--` — verify server returns error or ignores injection; `users` table must remain intact                         | **COMPLIANT** — uses `?` placeholders                              |
| SEC-05b (SQLi) | SQL injection via `name` field.                                                                                                                   | Send `name` = `'; SELECT * FROM users;--` — verify no injection side effects                                                                         | **COMPLIANT** — parameterized                                      |
| SEC-06         | The `role` field must not be settable by the caller via the registration endpoint (mass assignment).                                              | Send `{"name":"A","email":"sec06@test.com","password":"Pass1!aB","role":"admin"}` — verify created user has `role = 'user'`                          | **COMPLIANT** — server only destructures `{name, email, password}` |
| SEC-02         | Not applicable — this endpoint is intentionally public. No authentication required.                                                               | N/A                                                                                                                                                  | N/A                                                                |
| SEC-03         | Not applicable — this is not an admin endpoint.                                                                                                   | N/A                                                                                                                                                  | N/A                                                                |
| SEC-07         | Not applicable — no OTP generation or verification involved.                                                                                      | N/A                                                                                                                                                  | N/A                                                                |
| Rate Limiting  | Registration should be rate-limited to prevent automated bulk account creation. [inferred — OWASP API Security Top 10]                            | Send 10+ rapid consecutive POST requests — verify `429 Too Many Requests` is returned after threshold                                                | **NOT IMPLEMENTED** — Known Constraint                             |
| Sensitive Data | Error responses must not expose raw internal error details (SQLite messages, stack traces).                                                       | Trigger a DB-level error (duplicate email if UNIQUE constraint is active) — verify response does not contain raw SQL error                           | **VIOLATED** — `err.message` directly exposed                      |
