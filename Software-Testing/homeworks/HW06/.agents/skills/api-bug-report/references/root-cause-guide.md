# Root Cause Analysis Guide — `api-bug-report`

This guide defines how to identify the root cause category for each failed TC and how to distinguish one bug from another.

**Source:** [IEEE 1044-2009 — Standard Classification for Software Anomalies](https://standards.ieee.org/ieee/1044/3448/), [ISTQB Foundation Level Syllabus v4.0 — Defect Management](https://istqb.org/wp-content/uploads/2024/11/ISTQB_CTFL_Syllabus_v4.0.1.pdf)

## Root Cause Categories

### `AUTH` — Authentication or Authorization Issue

**Definition:** The API fails to correctly enforce who can call it and what they can do.

**Indicators:**

- Endpoint accessible without a valid token (missing authentication check)
- Endpoint accessible with a lower-privilege token than required (missing role check)
- Valid token from User A can access resources owned by User B (IDOR)
- Token validation logic is incorrect (expired tokens accepted, wrong signature accepted)

**Typical failing TC categories:** TC-SEC (auth bypass, role escalation, IDOR)

**Example:** Admin-only endpoint returns 200 when called with a regular user token instead of 403.

### `VALIDATION` — Input Validation Missing or Incorrect

**Definition:** The API accepts input it should reject, or rejects input it should accept, due to incomplete or wrong validation logic.

**Indicators:**

- Required field accepted when missing (no 400 returned)
- Field accepted with value outside documented constraints (e.g., negative price accepted)
- Field accepted with wrong type (string accepted where integer expected)
- Duplicate value accepted when uniqueness is required

**Typical failing TC categories:** TC-FR (domain partition), TC-ERR (error handling)

**Example:** Registration accepts a password of 3 characters when the contract requires minimum 8.

### `BUSINESS_LOGIC` — Business Rule Not Implemented Correctly

**Definition:** The API processes the request but produces an incorrect outcome — a documented business rule is violated.

**Indicators:**

- Correct inputs produce incorrect response body (wrong data, wrong calculation)
- Duplicate resource created when uniqueness is required
- Default value not applied
- Conditional rule not enforced (e.g., coupon applied when `min_order_amount` not met)

**Typical failing TC categories:** TC-FR (happy path fails), TC-ST (state transition produces wrong system state)

**Example:** Checkout applies a coupon discount even when the order total is below `min_order_amount`.

### `SCHEMA` — Response Shape Does Not Match Contract

**Definition:** The API returns a response with incorrect structure — missing fields, wrong types, extra undocumented fields, or wrong HTTP status code.

**Indicators:**

- Required response field is absent
- Field present with wrong type (string instead of integer)
- HTTP status code differs from documented (e.g., 200 returned instead of 201)
- `Content-Type` header missing or incorrect
- Error response body structure inconsistent with contract (raw exception object instead of `{"error": "..."}`)

**Typical failing TC categories:** TC-SCH (schema validation)

**Example:** Successful registration returns `{"message": "..."}` but the contract also specifies an `id` field — `id` is missing from the response.

### `SECURITY` — Security Vulnerability

**Definition:** The API is exploitable — it does not defend against documented security threats, or it exposes sensitive information in responses.

**Indicators:**

- SQL/NoSQL injection payload causes 500 error (query executed) instead of 400 (rejected)
- XSS payload stored and returned unescaped
- Sensitive data (passwords, tokens, internal paths) appears in response
- Mass assignment: restricted field (e.g., `role`) accepted and applied
- Rate limiting not enforced

**Typical failing TC categories:** TC-SEC

**Example:** Sending `' OR '1'='1` in the email field causes a 500 Internal Server Error, indicating the input is being interpolated into a SQL query.

### `STATE` — State Transition Logic Incorrect

**Definition:** The API changes system state in a way that does not match the documented state machine, or it fails to transition when it should.

**Indicators:**

- Entity status field not updated after a successful call
- Transition allowed from a state where it should be blocked
- Transition blocked from a state where it should be allowed
- Concurrent calls produce inconsistent state

**Typical failing TC categories:** TC-ST (state transition)

**Example:** Canceling an order with status `pending` returns 400 instead of 200 — the cancellation is blocked when the contract says it should be allowed.

## Bug Grouping Decision Rules

### Same Bug — Merge into One Entry

Group multiple failing TCs into one bug when ALL of the following are true:

1. Same root cause category
2. Same underlying behavioral defect (same validation missing, same security check absent)
3. Same contract clause violated (same BR-xx or SEC-xx)

### Separate Bugs — Create Distinct Entries

Create separate bug entries when ANY of the following differ:

1. Different root cause category (e.g., one is VALIDATION, another is AUTH)
2. Different behavioral defect (e.g., SQL injection via email field vs. SQL injection via name field — same category, but different injection vectors may have different fixes)
3. Different contract clause violated

### Borderline Cases

- Multiple TC-SEC failures of the same attack type (e.g., SQL injection) in different fields: use judgment based on whether they likely share the same fix. If a single sanitization fix would address all of them → one bug. If they require separate fixes → separate bugs.
- TC-FR failure and TC-ERR failure pointing to the same missing validation: merge into one VALIDATION bug.

## Steps to Reproduce — Derivation Rules

Steps to Reproduce must be self-contained and executable by a developer who has not seen the TC file.

**Required elements:**

1. Environment setup (e.g., "Start the API server on localhost:3000")
2. Prerequisite state (e.g., "No user with email test@example.com exists in the database")
3. The exact request: method, URL, headers, and body — use the TC's Input column as the source
4. The observed response (status code + body)

**Format:**

```
1. Start the API server.
2. Send the following request:
   POST /api/register
   Content-Type: application/json
   {
     "name": "Test",
     "email": "' OR '1'='1",
     "password": "Password123!"
   }
3. Observe the response.
```

**Do not:**

- Reference TC IDs in Steps to Reproduce (a developer should not need to look up the TC)
- Omit the request body or headers
- Use placeholder text like `{some value}` — use the actual values from the TC Input column
