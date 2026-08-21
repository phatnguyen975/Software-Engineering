# Instruction: Security Testing

**Technique:** OWASP API Security Top 10 (2023)  
**Source:** [OWASP API Security Top 10 — 2023 Edition](https://owasp.org/API-Security/editions/2023/en/0x00-header/)  
**Delegates to:** Standalone — not delegated to a sub-routine

## Purpose

Generate TCs that verify the API resists common API-level attacks and enforces authentication, authorization, and input security controls as specified in the contract's Security Rules section.

## When to Apply

Always. Read Section 6 (Security Rules) of the contract. Generate one or more TCs per listed security rule. If the section is empty or marked N/A, mark TC-SEC as `N/A — no security rules defined in contract`.

## Security Test Categories

For each applicable rule in the contract, apply the corresponding test vectors below:

### API1 — Broken Object Level Authorization (BIDOR / IDOR)

**When to apply:** API accepts a resource ID (path param or body) that could reference another user's resource.

Test vectors:

- Authenticated as User A, request resource owned by User B using User B's ID
- Authenticated as User A, request resource owned by User A but with a manipulated ID (e.g., ID ± 1)
- Expected: `403 Forbidden` or `404 Not Found` — never return another user's data

### API2 — Broken Authentication

**When to apply:** Endpoint requires authentication.

Test vectors:

- Request with no `Authorization` header → expect `401`
- Request with malformed token (`Authorization: Bearer invalid`) → expect `401`
- Request with expired token → expect `401`
- Request with token signed with wrong secret → expect `401`
- Request with valid token belonging to deleted/disabled user → expect `401`

### API3 — Broken Object Property Level Authorization (Mass Assignment)

**When to apply:** Request body contains fields; there are restricted fields the caller should not be able to set (e.g., `role`, `is_admin`, `id`).

Test vectors:

- Include restricted field in request body (e.g., `{"role": "admin"}` in registration)
- Include system-managed field (e.g., `{"id": 999}` or `{"created_at": "2000-01-01"}`)
- Expected: field is ignored OR `400 Bad Request` — never applied to the resource

### API4 — Unrestricted Resource Consumption (Rate Limiting)

**When to apply:** Contract specifies a rate limit rule. → Handled by `references/rate-limiting.md` if applicable. Do not duplicate here.

### API5 — Broken Function Level Authorization (Role Escalation)

**When to apply:** Endpoint requires a specific role (e.g., admin).

Test vectors:

- Request with no token → expect `401`
- Request with valid token but insufficient role (e.g., `user` token on admin endpoint) → expect `403`
- Request with valid admin token → expect success
- Attempt to access admin endpoint by modifying JWT payload (if not properly signed) → expect `401`

### API7 — Server Side Request Forgery (SSRF)

**When to apply:** Request body or params accept a URL or URI field.

Test vectors:

- Submit `http://localhost/internal-service` as URL value
- Submit `http://169.254.169.254/latest/meta-data/` (AWS metadata endpoint)
- Expected: request rejected with `400` or URL sanitized — never fetched

### API8 — Security Misconfiguration

**When to apply:** Always check response headers and error verbosity.

Test vectors:

- Verify error responses do not expose stack traces, internal paths, or DB error messages
- Verify response does not include sensitive headers (`X-Powered-By`, server version)
- Verify `Content-Type: application/json` is set on all JSON responses

### API9 — Improper Inventory Management

Not applicable at per-endpoint TC level — system-level concern. Skip.

### API10 — Unsafe Consumption of APIs

Not applicable at per-endpoint TC level — integration concern. Skip.

### Injection Attacks (SQL, NoSQL, Command)

**When to apply:** String fields are accepted and may be used in database queries or system commands.

Test vectors — try each in every applicable string field:

```
SQL injection:    ' OR '1'='1
SQL injection:    '; DROP TABLE users; --
NoSQL injection:  {"$gt": ""}
Command injection: ; ls -la
LDAP injection:   *)(uid=*))(|(uid=*
```

Expected: `400 Bad Request` or input rejected — never a `500` caused by query error, never data returned from injected query.

### XSS (Cross-Site Scripting)

**When to apply:** String fields that are stored and may be rendered in a UI.

Test vectors:

```
<script>alert(1)</script>
<img src=x onerror=alert(1)>
javascript:alert(1)
```

Expected: stored value is escaped on retrieval, or input rejected with `400`. Never stored and rendered raw.

## Data-Driven Eligibility

- Injection and XSS TCs: Partially data-driven — multiple payloads can be parameterized into a JSON data file
- Auth bypass TCs: Not data-driven — each auth scenario requires a distinct pre-request setup
- Role escalation TCs: Not data-driven — each role requires a distinct token

## Output Format

Produce rows for the TC-SEC table in `test-cases.md`:

| ID                      | Title                           | Attack Type                                                                          | Payload / Vector                      | Expected Result                   | Security Rule                    | Status | Actual Result |
| ----------------------- | ------------------------------- | ------------------------------------------------------------------------------------ | ------------------------------------- | --------------------------------- | -------------------------------- | ------ | ------------- |
| TC-{feature_id}-SEC-001 | {Action + Function + Condition} | {SQL Injection / Auth Bypass / Mass Assignment / IDOR / XSS / Role Escalation / ...} | {Exact payload or attack description} | {HTTP status — expected behavior} | {SEC-01 / OWASP API1:2023 / ...} | —      | —             |
| TC-{feature_id}-SEC-002 |                                 |                                                                                      |                                       |                                   |                                  | —      | —             |
