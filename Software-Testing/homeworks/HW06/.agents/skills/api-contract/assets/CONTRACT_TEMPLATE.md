# API Contract: {METHOD} {PATH}

> **Status:** Draft / Approved  
> **Last updated:** {YYYY-MM-DD}  
> **Source documents:** {list of input_docs used}

## 1. Overview

| Property          | Value                                                  |
| ----------------- | ------------------------------------------------------ |
| **Endpoint**      | `{METHOD} {PATH}`                                      |
| **Feature**       | {Feature name or ID, e.g., FR-01 — User Registration}  |
| **Auth required** | Yes / No                                               |
| **Auth type**     | Bearer JWT / API Key / None                            |
| **Role required** | None / `user` / `admin` / {other role}                 |
| **Idempotency**   | Idempotent / Not idempotent / Conditional: {condition} |

## 2. Request

### 2.1 Headers

| Header          | Required | Value / Format     | Notes                                      |
| --------------- | -------- | ------------------ | ------------------------------------------ |
| `Authorization` | Yes / No | `Bearer <JWT>`     | Required when auth is enforced             |
| `Content-Type`  | Yes      | `application/json` | Required for endpoints with a request body |
| {Custom header} | Yes / No | {format}           | {notes}                                    |

### 2.2 Path Parameters

| Parameter | Type             | Required | Constraints   | Example   |
| --------- | ---------------- | -------- | ------------- | --------- |
| {param}   | string / integer | Yes / No | {constraints} | {example} |

> If none, marks it as `N/A`.

### 2.3 Query Parameters

| Parameter | Type                       | Required | Constraints   | Default   | Example   |
| --------- | -------------------------- | -------- | ------------- | --------- | --------- |
| {param}   | string / integer / boolean | Yes / No | {constraints} | {default} | {example} |

> If none, marks it as `N/A`.

### 2.4 Request Body

```json
{
  "field_name": "value  — type: string | constraints: ..."
}
```

#### Field Constraints

| Field        | Type                                        | Required | Constraints                               | Default          | Notes                     |
| ------------ | ------------------------------------------- | -------- | ----------------------------------------- | ---------------- | ------------------------- |
| `field_name` | string / integer / boolean / array / object | Yes / No | {min, max, pattern, allowed values, etc.} | {default or N/A} | {notes or inferred label} |

## 3. Business Rules

> Rules derived from documentation. Each rule has a unique ID for traceability in test cases and bug reports.

- **BR-01:** {State the rule precisely. Example: "Email address must be unique across all registered users. Comparison is case-insensitive."}
- **BR-02:** {Example: "Password must be stored as a hash. Plaintext storage is prohibited."}
- **BR-03:** {Example: "The `role` field defaults to `user` at registration and cannot be set by the caller."}
- **BR-04:** {Add as many as needed.}

## 4. Response Definitions

> Each distinct response scenario has its own subsection. Do not use a flat table — response bodies may be nested JSON.

### 4.1 {HTTP Status} — {Scenario Name}

**Trigger:** {What input condition or system state causes this response?}

```json
{response body}
```

**Headers returned:**

- `Content-Type: application/json`
- {Any other notable headers}

### 4.2 {HTTP Status} — {Scenario Name}

**Trigger:** {condition}

```json
{response body}
```

> Add one subsection per distinct response scenario. Minimum expected scenarios:
>
> - Success (2xx)
> - Validation failure (400 or 422)
> - Auth failure (401) — if auth is required
> - Role/permission failure (403) — if role check exists
> - Not found (404) — if resource lookup is involved
> - Conflict (409) — if uniqueness constraint exists
> - Rate limit (429) — if rate limiting is applied
> - Server error (500)

## 5. State Transitions

> Cover all three types. Mark N/A explicitly if a type does not apply.

### 5.1 HTTP Response State

> How does the input determine which HTTP response code is returned?

| Input Condition | HTTP Status | Notes |
| --------------- | ----------- | ----- |
| {Condition}     | {Status}    |       |

### 5.2 System Data State

> Does this endpoint create, update, or delete any persistent data? What is the before/after?

| Entity             | Field     | Before                             | After         | Condition           |
| ------------------ | --------- | ---------------------------------- | ------------- | ------------------- |
| `{table/resource}` | `{field}` | {before value or "does not exist"} | {after value} | {when this applies} |

> If this endpoint causes no persistent data change: `N/A`

### 5.3 Field-Level State Preconditions

> Does the behavior of this endpoint depend on the current value of a state field in an existing entity?

| Entity    | State Field      | Allowed Values to Proceed | Blocked Values   | Effect When Blocked |
| --------- | ---------------- | ------------------------- | ---------------- | ------------------- |
| `{table}` | `{status_field}` | {allowed values}          | {blocked values} | {response returned} |

> If no precondition on existing state: `N/A`

## 6. Security Rules

| Rule ID  | Description                                                            | Applicable Test Vector                           |
| -------- | ---------------------------------------------------------------------- | ------------------------------------------------ |
| {SEC-01} | {SQL Injection — string fields passed to queries without sanitization} | `' OR '1'='1` in `{field}`                       |
| {SEC-02} | {XSS — stored string fields rendered in UI}                            | `<script>alert(1)</script>` in `{field}`         |
| {SEC-03} | {Auth bypass — endpoint accessible without token}                      | Request with no `Authorization` header           |
| {SEC-04} | {Broken access control — insufficient role check}                      | Request with `user`-role token on admin endpoint |
| {SEC-05} | {IDOR — resource not scoped to authenticated user}                     | User A token accessing User B's resource ID      |
| {SEC-06} | {Mass assignment — extra fields accepted and processed}                | `{"role": "admin"}` in request body              |
| {SEC-07} | {Rate limiting — no throttle on sensitive endpoint}                    | 50+ rapid consecutive requests                   |

> Remove rows for rules that provably do not apply to this endpoint. Add rows for additional rules found.
