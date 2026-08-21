# Inference Guide — `api-contract`

When `allow_inference = true`, use this guide to fill gaps with industry-standard defaults.

**Rules for inference:**

- Only use defaults from the sources cited below (OWASP, IETF RFCs, NIST, framework docs)
- Every inferred value must be labeled visibly in the contract output
- If a field's behavior varies significantly across the industry, mark it `Unknown` rather than picking an arbitrary default
- Inference applies to missing constraints only — never override an observed implementation detail

## Authentication & Authorization

### JWT Tokens

> **Source:** [RFC 7519](https://datatracker.ietf.org/doc/html/rfc7519), [OWASP JWT Security Cheat Sheet](https://cheatsheetseries.owasp.org/cheatsheets/JSON_Web_Token_Cheat_Sheet.html)

| Behavior                       | Standard default                       |
| ------------------------------ | -------------------------------------- |
| Token location                 | `Authorization: Bearer <token>` header |
| Missing token response         | `401 Unauthorized`                     |
| Invalid/expired token response | `401 Unauthorized`                     |
| Insufficient role response     | `403 Forbidden`                        |

### Role-Based Access Control

> **Source:** [OWASP Access Control Cheat Sheet](https://cheatsheetseries.owasp.org/cheatsheets/Access_Control_Cheat_Sheet.html)

- Admin endpoints must require both authentication AND an explicit role check
- Default user role at registration: `user` (not `admin`)
- Role escalation via user-facing endpoints must not be possible (mass assignment protection)

## Input Validation

### String Fields

> **Source:** [OWASP Input Validation Cheat Sheet](https://cheatsheetseries.owasp.org/cheatsheets/Input_Validation_Cheat_Sheet.html)

| Field type               | Typical constraint                         | Notes                                                                   |
| ------------------------ | ------------------------------------------ | ----------------------------------------------------------------------- |
| Display name / full name | 2–100 characters                           | Varies; 100 is a common upper bound                                     |
| Username                 | 3–50 characters, alphanumeric + underscore |                                                                         |
| Email address            | RFC 5322 format, max 254 characters        | [RFC 5321](https://datatracker.ietf.org/doc/html/rfc5321#section-4.5.3) |
| Free-text description    | 0–1000 characters                          | Adjust to domain                                                        |
| Coupon / promo code      | 3–20 characters, alphanumeric              | Domain convention                                                       |

### Password Fields

> **Source:** [NIST SP 800-63B §5.1.1](https://pages.nist.gov/800-63-3/sp800-63b.html#memsecret), [OWASP Authentication Cheat Sheet](https://cheatsheetseries.owasp.org/cheatsheets/Authentication_Cheat_Sheet.html)

| Requirement      | NIST / OWASP guidance                                                                                                                 |
| ---------------- | ------------------------------------------------------------------------------------------------------------------------------------- |
| Minimum length   | 8 characters (NIST minimum); 12 recommended                                                                                           |
| Maximum length   | At least 64 characters must be accepted                                                                                               |
| Complexity rules | NIST discourages mandatory complexity rules; OWASP recommends at least 1 uppercase + 1 digit + 1 special character for legacy systems |
| Storage          | Must be hashed (bcrypt, Argon2, PBKDF2) — never plaintext                                                                             |
| Breach check     | NIST recommends checking against known-breached password lists                                                                        |

### Numeric Fields

| Field type              | Typical constraint                            |
| ----------------------- | --------------------------------------------- |
| Price / monetary amount | Greater than 0; precision to 2 decimal places |
| Discount percentage     | 0 < value ≤ 100                               |
| Discount fixed amount   | Greater than 0                                |
| Quantity                | Integer, 1–99 (e-commerce convention)         |

### Date / Datetime Fields

| Field type  | Constraint                                       |
| ----------- | ------------------------------------------------ |
| Expiry date | Must be in the future at time of creation        |
| Date format | ISO 8601: `YYYY-MM-DD` or `YYYY-MM-DDTHH:MM:SSZ` |

## HTTP Response Codes

> **Source:** [RFC 9110](https://www.rfc-editor.org/rfc/rfc9110)

| Scenario                          | Standard code                                                              |
| --------------------------------- | -------------------------------------------------------------------------- |
| Resource created successfully     | `200 OK` (if body returned) or `201 Created` (if Location header returned) |
| Request body validation failure   | `400 Bad Request`                                                          |
| Missing or invalid auth token     | `401 Unauthorized`                                                         |
| Valid token but insufficient role | `403 Forbidden`                                                            |
| Resource not found                | `404 Not Found`                                                            |
| Duplicate unique resource         | `409 Conflict`                                                             |
| Request body schema error         | `422 Unprocessable Entity` (preferred over 400 for semantic errors)        |
| Rate limit exceeded               | `429 Too Many Requests`                                                    |
| Unexpected server error           | `500 Internal Server Error`                                                |

## Security Defaults by System Type

### E-Commerce

> **Source:** [OWASP API Security Top 10](https://owasp.org/API-Security/editions/2023/en/0x00-header/), [PCI DSS v4.0](https://www.pcisecuritystandards.org/document_library/) (for payment-adjacent features)

| Concern                      | Default assumption                                                                |
| ---------------------------- | --------------------------------------------------------------------------------- |
| Rate limiting — registration | Max 5 attempts per IP per hour                                                    |
| Rate limiting — login        | Max 5 attempts per account per 15 minutes → temporary lockout                     |
| Account lockout              | 3–5 failed login attempts → lockout for 5–30 minutes                              |
| Password storage             | bcrypt with cost factor ≥ 10                                                      |
| Session token                | JWT with short expiry (15–60 minutes) + refresh token                             |
| IDOR on orders/cart          | Resources scoped to authenticated user; user cannot access other users' resources |
| Admin endpoints              | Require `role = 'admin'` check in addition to authentication                      |
| Coupon code uniqueness       | `code` field must be unique per system                                            |
| Price integrity              | Server must compute final price — client-supplied price must not be trusted       |

### Fintech

> **Source:** [OWASP ASVS](https://owasp.org/www-project-application-security-verification-standard/), [PSD2 / Open Banking standards](https://www.eba.europa.eu/regulation-and-policy/payment-services-and-electronic-money/regulatory-technical-standards-on-strong-customer-authentication-and-secure-communication-under-psd2)

- Strong Customer Authentication (SCA) for transactions above threshold
- All financial transactions must be logged with non-repudiation
- Amount fields must use decimal precision (avoid floating point)
- Idempotency keys required on payment creation endpoints

### Healthcare

> **Source:** [HIPAA Security Rule](https://www.hhs.gov/hipaa/for-professionals/security/index.html), [SMART on FHIR](https://docs.smarthealthit.org/)

- All PHI fields must be encrypted at rest and in transit
- Access logs must be retained
- Role-based access must be granular (clinician vs. admin vs. patient)

## Idempotency Defaults

| Method                   | Default idempotency behavior                                  |
| ------------------------ | ------------------------------------------------------------- |
| `POST` (create resource) | Not idempotent — second call creates duplicate or returns 409 |
| `PUT` (full replace)     | Idempotent                                                    |
| `PATCH` (partial update) | Idempotent in most implementations                            |
| `DELETE`                 | Idempotent — second call returns 404 or 204                   |

## Labeling Convention

Every inferred value in the contract must use this exact label format:

```
[inferred — {source}]
```

Examples:

```
minLength: 8 [inferred — NIST SP 800-63B §5.1.1]
Max 5 attempts per IP per hour [inferred — e-commerce rate limiting convention, OWASP]
Response: 409 Conflict [inferred — RFC 9110, duplicate resource convention]
```

This makes human review targeted and traceable.
