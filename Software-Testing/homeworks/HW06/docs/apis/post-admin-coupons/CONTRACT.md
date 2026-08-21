# API Contract: POST /api/admin/coupons

> **Status:** Approved  
> **Last updated:** 2026-08-21  
> **Source documents:** `docs/sut/api-spec.md`, `docs/sut/srs.md`

## 1. Overview

| Property          | Value                                                              |
| ----------------- | ------------------------------------------------------------------ |
| **Endpoint**      | `POST /api/admin/coupons`                                          |
| **Feature**       | FR-17 — Coupon Management (Admin CRUD)                             |
| **Auth required** | Yes                                                                |
| **Auth type**     | Bearer JWT                                                         |
| **Role required** | `admin`                                                            |
| **Idempotency**   | Not idempotent — repeated calls with the same `code` must conflict |

## 2. Request

### 2.1 Headers

| Header          | Required | Value / Format     | Notes                                                 |
| --------------- | -------- | ------------------ | ----------------------------------------------------- |
| `Authorization` | Yes      | `Bearer <JWT>`     | Must be a valid JWT token with `role = 'admin'` claim |
| `Content-Type`  | Yes      | `application/json` | Body must be sent as JSON                             |

### 2.2 Path Parameters

N/A

### 2.3 Query Parameters

N/A

### 2.4 Request Body

```json
{
  "code": "TET2026",
  "type": "percent",
  "discount_value": 15,
  "min_order_amount": 200000,
  "expired_at": "2026-12-31",
  "max_uses_per_user": 1
}
```

#### Field Constraints

| Field               | Type    | Required | Constraints                                                                                                                                                                                                                                                                                             | Default | Notes                                                                                |
| ------------------- | ------- | -------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- | ------- | ------------------------------------------------------------------------------------ |
| `code`              | string  | Yes      | Must be unique across all coupons. Length: 3–20 characters [inferred — OWASP Input Validation, e-commerce coupon convention]. Characters: alphanumeric (A–Z, a–z, 0–9) and hyphen only [inferred — e-commerce coupon convention]. Case-insensitive uniqueness check [inferred — e-commerce convention]. | N/A     | Defined in SRS FR-17 as required and unique                                          |
| `type`              | string  | Yes      | Allowed values: `"percent"` or `"fixed"` only. Any other value is rejected.                                                                                                                                                                                                                             | N/A     | Defined in SRS FR-17; both values described in FR-09 formula                         |
| `discount_value`    | integer | Yes      | Must be a positive integer (> 0). When `type = "percent"`: must be in range 1–100 [inferred — numeric range convention, inference-guide §Numeric Fields]. When `type = "fixed"`: must be > 0.                                                                                                           | N/A     | Defined in SRS FR-17 as "dương" (positive)                                           |
| `min_order_amount`  | integer | Yes      | Must be an integer ≥ 0. Represents the minimum cart total (in VND) required to apply this coupon.                                                                                                                                                                                                       | `0`     | Defined in SRS FR-17 as "≥ 0"; value of 0 means no minimum                           |
| `expired_at`        | string  | Yes      | ISO 8601 date format: `YYYY-MM-DD` [inferred — ISO 8601, inference-guide §Date/Datetime]. Must be a future date relative to the time of creation [inferred — e-commerce expiry date convention, inference-guide §Date/Datetime].                                                                        | N/A     | Database column type is `DATETIME`; date-only input is accepted per API spec example |
| `max_uses_per_user` | integer | No       | Must be an integer ≥ 1. Defines how many times a single user may apply this coupon.                                                                                                                                                                                                                     | `1`     | Defined in SRS FR-17 as "≥ 1"; may be omitted and server defaults to 1               |

> **Note on `is_active`:** The coupon entity has an `is_active` flag. When created via this endpoint, `is_active` defaults to `1` (active). The caller cannot set `is_active` through this endpoint — activation state is not caller-controlled at creation time [inferred — standard resource creation convention; mass assignment protection].

## 3. Business Rules

> Rules derived from `srs.md` (FR-17, FR-09, FR-12) and e-commerce best practices. Each rule has a unique ID for traceability in test cases and bug reports.

- **BR-01:** The `code` field must be unique across all coupons in the system. A request that duplicates an existing `code` must be rejected with a conflict response. Case-insensitive comparison is expected [inferred — e-commerce coupon convention]. _(Source: SRS FR-17 — "code (duy nhất)")_
- **BR-02:** The `type` field is a closed enumeration. Only the string values `"percent"` and `"fixed"` are valid. Any other value — including empty string, null, numeric value, or any other string — must be rejected. _(Source: SRS FR-09, FR-17)_
- **BR-03:** The `discount_value` must be strictly positive (> 0). For `type = "percent"`, the value must additionally not exceed 100, as a discount percentage greater than 100 is semantically invalid [inferred — e-commerce discount convention, inference-guide §Numeric Fields]. For `type = "fixed"`, the value must be > 0 with no upper constraint specified. _(Source: SRS FR-17 — "discount_value (dương)")_
- **BR-04:** The `min_order_amount` must be a non-negative integer (≥ 0). A value of 0 means there is no minimum order requirement. Negative values are invalid. _(Source: SRS FR-17 — "min_order_amount (≥ 0)")_
- **BR-05:** The `expired_at` must represent a future date at the time of the API request. A coupon whose expiry date is already in the past at creation time provides no business value and must be rejected [inferred — e-commerce expiry date convention, inference-guide §Date/Datetime Fields]. _(Source: SRS FR-17 — "expired_at" required)_
- **BR-06:** The `max_uses_per_user` must be a positive integer ≥ 1. A value of 0 would prevent any user from ever applying the coupon, which is semantically invalid. _(Source: SRS FR-17 — "max_uses_per_user (≥ 1)")_
- **BR-07:** The fields `code`, `type`, `discount_value`, `min_order_amount`, and `expired_at` are strictly required. A request missing any one of these must be rejected with a 400 response. The `max_uses_per_user` field is optional; if omitted, the server defaults it to `1`. _(Source: SRS FR-17)_
- **BR-08:** This endpoint is accessible only to users with `role = 'admin'`. A request carrying a valid JWT for a regular (`role = 'user'`) account must be rejected with 403 Forbidden. Authentication alone (valid token, any role) is insufficient — explicit role authorization is required. _(Source: SRS FR-12, SEC-03)_
- **BR-09:** A newly created coupon is activated immediately (`is_active = 1`) and is available for customer use. The `is_active` field is not exposed in the request schema — the creation endpoint does not allow the caller to set the activation state [inferred — standard resource creation convention; activation managed separately]. _(Source: SRS FR-09 — condition C1: `"is_active = 1"`)_
- **BR-10:** Extra fields sent in the request body that are not part of the defined schema (e.g., `id`, `is_active`, `created_at`, `role`) must be silently ignored or explicitly rejected — they must not be persisted [inferred — OWASP Mass Assignment prevention, API Security Top 10 API6:2023]. _(Source: SRS FR-12, SEC-06 principle)_
- **BR-11:** The request body must be valid JSON. A non-JSON body or a malformed JSON body must result in a `400 Bad Request` response. The server must not expose raw parse error details in the response [inferred — Express body-parser behavior, RFC 9110]. _(Source: inferred)_

## 4. Response Definitions

> Each distinct response scenario has its own subsection. Response bodies containing nested JSON do not render in flat tables.

### 4.1 200 OK — Coupon Created Successfully

**Trigger:** Request carries a valid admin JWT, all required fields are present and pass all constraints, `code` does not already exist in the system, and `expired_at` is a future date.

> **Note on status code:** Based on the SUT specification, the success status code for this endpoint is `200 OK` rather than the typical `201 Created` for resource creation.

```json
{
  "message": "Coupon created",
  "id": 5
}
```

**Headers returned:**

- `Content-Type: application/json`

### 4.2 400 Bad Request — Missing Required Field

**Trigger:** One or more of the required fields (`code`, `type`, `discount_value`, `min_order_amount`, `expired_at`) is absent from the request body, or the body is entirely empty.

```json
{
  "error": "Missing required field: {field_name}"
}
```

**Headers returned:**

- `Content-Type: application/json`

### 4.3 400 Bad Request — Invalid Field Value

**Trigger:** A field is present but fails a constraint check. Examples:

- `type` is not `"percent"` or `"fixed"` (e.g., `"bonus"`, `""`, `null`)
- `discount_value` ≤ 0, or > 100 when `type = "percent"`
- `min_order_amount` < 0
- `max_uses_per_user` < 1 (e.g., 0 or negative)
- `expired_at` is a past date
- `expired_at` is not a valid date format (e.g., `"31-12-2026"`, `"tomorrow"`)
- `code` is shorter than 3 or longer than 20 characters [inferred]

```json
{
  "error": "Invalid value for field: {field_name}. {reason}"
}
```

**Headers returned:**

- `Content-Type: application/json`

### 4.4 400 Bad Request — Malformed JSON Body

**Trigger:** The request body is not valid JSON (syntax error, truncated payload, wrong Content-Type).

```json
{
  "error": "Invalid request body"
}
```

**Headers returned:**

- `Content-Type: application/json`

### 4.5 401 Unauthorized — Missing or Absent Token

**Trigger:** The `Authorization` header is absent entirely, or its value is empty.

```json
{
  "error": "Unauthorized"
}
```

**Headers returned:**

- `Content-Type: application/json`

> [inferred — RFC 7519, OWASP JWT Security Cheat Sheet] Missing token → 401. The `WWW-Authenticate` header should also be returned per RFC 7235.

### 4.6 403 Forbidden — Insufficient Role (User Token)

**Trigger:** The `Authorization` header contains a syntactically valid JWT, but the token's `role` claim is `"user"` rather than `"admin"`.

```json
{
  "error": "Forbidden"
}
```

**Headers returned:**

- `Content-Type: application/json`

> (Source: SRS FR-12, SEC-03)

### 4.7 403 Forbidden — Expired or Tampered Token

**Trigger:** The `Authorization` header is present, but the JWT is expired, has an invalid signature, or its payload has been modified.

```json
{
  "error": "Forbidden"
}
```

**Headers returned:**

- `Content-Type: application/json`

> [inferred — OWASP JWT Security Cheat Sheet] Invalid/expired/tampered token → 403.

### 4.8 409 Conflict — Duplicate Coupon Code

**Trigger:** A coupon with the same `code` value (case-insensitive [inferred — e-commerce convention]) already exists in the system.

```json
{
  "error": "Coupon code already exists"
}
```

**Headers returned:**

- `Content-Type: application/json`

> [inferred — RFC 9110 §15.5.10] The correct status for a uniqueness constraint violation is `409 Conflict`, not `500 Internal Server Error`.

### 4.9 500 Internal Server Error — Unexpected Server Error

**Trigger:** An unexpected condition occurs on the server side (e.g., database connectivity failure, unhandled exception) that is not attributable to client input.

```json
{
  "error": "Internal server error"
}
```

**Headers returned:**

- `Content-Type: application/json`

> The 500 response must not expose internal stack traces, raw database error messages (e.g., SQLite constraint text), or system file paths to the client.

## 5. State Transitions

### 5.1 HTTP Response State

> Different input conditions produce different HTTP status codes from this endpoint.

| Input Condition                                                     | HTTP Status | Notes                                     |
| ------------------------------------------------------------------- | ----------- | ----------------------------------------- |
| Valid admin token + all fields valid + unique `code`                | 200         | Coupon created                            |
| Valid admin token + missing required field                          | 400         | Validation failure — missing field        |
| Valid admin token + invalid field value (type, value, date, length) | 400         | Validation failure — constraint violation |
| Valid admin token + malformed/non-JSON body                         | 400         | Parse failure                             |
| No `Authorization` header (token absent)                            | 401         | Missing token                             |
| Valid JWT with `role = 'user'`                                      | 403         | Insufficient role (SEC-03, FR-12)         |
| Expired JWT, tampered JWT, or JWT with invalid signature            | 403         | Invalid token                             |
| Valid admin token + `code` already exists in system                 | 409         | Uniqueness conflict [inferred — RFC 9110] |
| Unhandled server-side exception (DB down, etc.)                     | 500         | Must not expose internal details          |

### 5.2 System Data State

> This endpoint inserts a new row into the `coupons` table on success.

| Entity    | Field        | Before               | After                                     | Condition                                        |
| --------- | ------------ | -------------------- | ----------------------------------------- | ------------------------------------------------ |
| `coupons` | (entire row) | Row does not exist   | New row inserted with all provided values | Successful creation (200)                        |
| `coupons` | `is_active`  | (row does not exist) | `1` — active, set by server default       | Always on success; caller cannot override        |
| `coupons` | `id`         | (row does not exist) | Auto-incremented integer assigned by DB   | Always on success                                |
| `coupons` | (entire row) | Row does not exist   | No change — row NOT inserted              | Any error response (400 / 401 / 403 / 409 / 500) |

### 5.3 Field-Level State Preconditions

> Does this endpoint's behavior depend on the current state of any existing entity?

| Entity    | State Field | Allowed Values to Proceed     | Blocked Values                         | Effect When Blocked                    |
| --------- | ----------- | ----------------------------- | -------------------------------------- | -------------------------------------- |
| `coupons` | `code`      | Code does not yet exist in DB | Code already exists in `coupons` table | 409 Conflict — duplicate code rejected |

> No other field-level state precondition applies. This endpoint creates a new resource; it does not read or modify any existing coupon's state.

## 6. Security Rules

| Rule ID | Description                                                                                                                                        | Applicable Test Vector                                                                                                |
| ------- | -------------------------------------------------------------------------------------------------------------------------------------------------- | --------------------------------------------------------------------------------------------------------------------- |
| SEC-02  | **Authentication required:** The endpoint must reject any request without a valid JWT in `Authorization` header.                                   | Send request with no `Authorization` header → expect 401                                                              |
| SEC-02  | **Malformed token:** A syntactically invalid JWT (not a valid 3-part base64 structure) must be rejected.                                           | Send `Authorization: Bearer not.a.validtoken` → expect 403                                                            |
| SEC-02  | **Expired token:** A JWT with an `exp` claim in the past must be rejected even if signature is valid.                                              | Send request with an intentionally expired JWT → expect 403                                                           |
| SEC-03  | **Role check — user token:** A valid JWT for `role = 'user'` must not grant access to this admin endpoint. Token validity alone is not sufficient. | Send request with a regular user's valid token → expect 403                                                           |
| SEC-03  | **Role check — forged admin token:** Attempt to forge a JWT with `"role":"admin"` payload using a guessed or publicly known secret key.            | Craft a JWT with `{"id":2,"role":"admin"}` using known secret → behavior reveals whether secret is adequately secured |
| SEC-05  | **SQL Injection via `code`:** The `code` field is stored in the database and must be handled via parameterized query, never string concatenation.  | Send `"code": "'; DROP TABLE coupons; --"` → expect 400 (validation) or safe insert; must NOT cause DB corruption     |
| SEC-05  | **SQL Injection via `type`:** The `type` field is also persisted and must be parameterized.                                                        | Send `"type": "' OR '1'='1"` → expect 400 (invalid enum); must NOT cause unintended DB query execution                |
| SEC-04  | **Stored XSS via `code`:** The `code` field is stored and later displayed on Admin UI coupon management pages.                                     | Send `"code": "<script>alert(1)</script>"` → verify value is safely escaped/encoded when retrieved and displayed      |
| SEC-06  | **Mass assignment — `is_active`:** The caller must not be able to set `is_active` to 0 at creation time, bypassing the default active state.       | Send body with `"is_active": 0` → coupon should still be created with `is_active = 1`, not 0                          |
| SEC-06  | **Mass assignment — `id`:** The caller must not be able to pre-set the coupon's primary key `id`.                                                  | Send body with `"id": 9999` → server must assign auto-increment ID, not accept caller-supplied value                  |
