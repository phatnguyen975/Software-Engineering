# Analysis Guide — `api-contract`

This file provides step-by-step guidance for extracting contract information from different types of input documents. Read this before starting Step 2 of the skill workflow.

## 1. Reading API Specification Documents

API specs (Markdown, Swagger, Postman collections) describe intended behavior. They are useful but often incomplete.

Extract:

- Endpoint method and path
- Described request fields and their types
- Example request/response bodies
- Stated error codes
- Any auth requirement mentioned

## 2. Reading SRS / Requirements Documents

SRS documents describe business requirements. Use them to extract:

- Business rules stated in natural language
- Security requirements (often labeled SEC-01, SEC-02, etc.)
- State machine descriptions for order/status flows
- User role definitions and access control rules
- Non-functional requirements that affect behavior (e.g., rate limiting thresholds)

## 3. Identifying State Transitions

State transitions are the most commonly missed contract element. Look for all three types:

### Type 1 — HTTP Response State

Different inputs produce different HTTP response codes from the same endpoint.

Example for `POST /api/register`:

```
Valid input     → 200 OK (user created)
Duplicate email → 409 Conflict
Missing field   → 400 Bad Request
```

### Type 2 — System Data State

The endpoint changes the state of a persistent entity in the database.

Example for `POST /api/register`:

```
Before call: user record does not exist
After call:  user record exists with role = 'user', login_attempts = 0
```

Example for `PUT /api/admin/orders/:id/status`:

```
Before call: order.status = 'pending'
After call:  order.status = 'confirmed' (if valid transition)
```

### Type 3 — Field-Level State Precondition

The endpoint's behavior depends on the current value of a state field in the database.

Example for `PUT /api/orders/:id/cancel`:

```
order.status = 'pending'   → cancellation allowed → 200
order.status = 'delivered' → cancellation blocked → 400
order.status = 'canceled'  → already canceled     → 400
```

## 4. Identifying Security Rules

Map each security concern to a concrete test vector. Use the following categories:

| Category                | What to look for                                       | Example test vector                        |
| ----------------------- | ------------------------------------------------------ | ------------------------------------------ |
| Injection               | String fields passed to SQL/shell without sanitization | `' OR '1'='1` in email field               |
| XSS                     | String fields stored and later rendered                | `<script>alert(1)</script>` in name field  |
| Auth bypass             | Endpoints missing auth middleware                      | Request with no `Authorization` header     |
| Broken auth             | Token validation logic                                 | Expired token, malformed JWT, wrong secret |
| Broken access control   | No role check on admin endpoints                       | User-role token on admin endpoint          |
| IDOR                    | Resource IDs in path/body not scoped to owner          | User A accesses User B's resource          |
| Mass assignment         | Extra fields in body processed without whitelist       | `{"role": "admin"}` in register body       |
| Rate limiting           | No rate limiter on sensitive endpoints                 | 50 consecutive register attempts           |
| Sensitive data exposure | Passwords, tokens in responses                         | Password field in user response            |

## 5. Identifying Idempotency

For each endpoint, answer: **"If an identical request is sent twice, what happens?"**

| Method                   | Typical idempotency                               | What to check                        |
| ------------------------ | ------------------------------------------------- | ------------------------------------ |
| `GET`                    | Idempotent by definition                          | Verify no side effects               |
| `POST` (create)          | NOT idempotent — creates duplicate or returns 409 | Check for UNIQUE constraint handling |
| `PUT` (replace)          | Idempotent — second call produces same result     | Verify no unintended side effects    |
| `PATCH` (partial update) | Usually idempotent                                | Verify                               |
| `DELETE`                 | Idempotent — second call returns 404 or 200       | Check for graceful handling          |

For `POST /api/register`, not idempotent. Second call with same email should return 409. (Check if it actually does.)
