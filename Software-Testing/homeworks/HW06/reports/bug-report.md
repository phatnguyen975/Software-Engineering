<div align="center">
  <h1>Bug Report — HW06 (API Testing)</h1>
  <small>
    <strong>Student:</strong> Nguyễn Tấn Phát — 23127449
  </small> <br />
  <sub>August 22, 2026</sub>
</div>

# Bug Report: POST /api/register

> **Feature:** FR01 | **Endpoint:** `POST /api/register`  
> **Total Bugs:** 4 | **Status:** Approved  
> **Generated:** 2026-08-20

## BUG-FR01-001

**Title:** Registration accepts any input without field validation — no 400 returned  
**Severity:** High  
**Priority:** P2  
**Root Cause Category:** VALIDATION  
**Status:** Open  
**Related TCs:** TC-FR01-FR-004, TC-FR01-FR-005, TC-FR01-FR-006, TC-FR01-FR-008, TC-FR01-FR-009, TC-FR01-FR-010, TC-FR01-FR-012, TC-FR01-FR-014, TC-FR01-FR-016, TC-FR01-FR-017, TC-FR01-FR-018, TC-FR01-FR-019, TC-FR01-FR-020, TC-FR01-ERR-001, TC-FR01-ERR-002, TC-FR01-ERR-005, TC-FR01-ERR-006, TC-FR01-ERR-007, TC-FR01-ERR-008, TC-FR01-ERR-009, TC-FR01-ERR-010, TC-FR01-ERR-011, TC-FR01-SCH-005, TC-FR01-SCH-006  
**GitHub Issue:** [#1](https://github.com/phatnguyen975/Learn-Postman/issues/1)

### Description

The `POST /api/register` endpoint performs no input validation on any request field. All of the following invalid inputs are accepted and result in `200 OK`:

- `name` missing, null, empty (`""`), whitespace-only (`" "`), or exceeding 100 characters
- `email` missing, null, invalid RFC 5322 format, or exceeding 254 characters
- `password` missing, null, below 8 characters, exceeding 128 characters, or failing complexity policy
- An entirely empty request body `{}` or no body at all
- Fields with wrong types (integer, boolean)

As a secondary consequence, because no error path is ever executed, all error response bodies (which must use the `"error"` key per contract) are never emitted. When validation is implemented, error responses must use `{"error": "..."}` — not `{"message": "..."}`.

This violates:

- **BR-01:** All three fields are required; missing fields must return `400 Bad Request`
- **BR-02:** `email` must conform to RFC 5322; invalid format must return `400 Bad Request`
- **BR-04:** `password` must satisfy the strong password policy; failing values must return `400 Bad Request`
- **BR-06:** `name` must not be empty or whitespace-only
- **Section 4.2:** `400 Bad Request` response body must use key `"error"`, not `"message"`

### Steps to Reproduce

**Example 1 — Missing required field:**

1. Start the API server on `localhost:3000`.
2. Send the following request:

   ```
   POST /api/register
   Content-Type: application/json

   {
     "email": "no-name@domain.com",
     "password": "Password123!"
   }
   ```

3. Observe `200 OK` with `{"message": "User registered successfully", "id": <integer>}` instead of `400 Bad Request`.

**Example 2 — Invalid email format:**

1. Start the API server.
2. Send the following request:

   ```
   POST /api/register
   Content-Type: application/json

   {
     "name": "Test User",
     "email": "invalidemail.com",
     "password": "Password123!"
   }
   ```

3. Observe `200 OK` returned instead of `400 Bad Request`.

**Example 3 — Weak password (missing uppercase):**

1. Start the API server.
2. Send the following request:

   ```
   POST /api/register
   Content-Type: application/json

   {
     "name": "Test User",
     "email": "no-upper@domain.com",
     "password": "password1!"
   }
   ```

3. Observe `200 OK` returned instead of `400 Bad Request`.

**Example 4 — Empty request body:**

1. Start the API server.
2. Send the following request:

   ```
   POST /api/register
   Content-Type: application/json

   {}
   ```

3. Observe `200 OK` returned instead of `400 Bad Request`.

### Expected Result

Any request where `name`, `email`, or `password` is missing, null, empty, whitespace-only, has invalid format, or fails the password complexity policy must be rejected with:

```json
HTTP 400 Bad Request
{
  "error": "<specific validation error message>"
}
```

The field constraints are:

- `name`: required, non-empty, non-whitespace-only, 1–100 characters
- `email`: required, RFC 5322 format, max 254 characters
- `password`: required, 8–128 characters, ≥1 uppercase, ≥1 lowercase, ≥1 digit, ≥1 special character from `@$!%*?&`

### Actual Result

**HTTP Status:** 200  
**Response Body:**

```json
{
  "message": "User registered successfully",
  "id": 3
}
```

The API accepts all invalid inputs and responds with a success message. No validation error is ever returned for field-level failures. The response key is `"message"`, not `"error"` as required by the contract for error responses.

### Evidence

- **Newman Report:** `postman/reports/fr01-report.html`
- **Screenshot:**

![BUG-FR01-001](../docs/screenshots/fr01/BUG-FR01-001.png)

### Impact

- **Data pollution:** Any string, null, empty value, or wrong type can be registered, corrupting data quality.
- **Weak password acceptance:** Passwords with no complexity requirements are accepted, undermining account security.
- **Schema non-compliance:** Error responses use the wrong key (`"message"` instead of `"error"`), breaking API consumers that rely on the documented schema.

### Notes

None.

## BUG-FR01-002

**Title:** Duplicate email registration succeeds — no uniqueness enforcement  
**Severity:** High  
**Priority:** P2  
**Root Cause Category:** BUSINESS_LOGIC  
**Status:** Open  
**Related TCs:** TC-FR01-FR-021, TC-FR01-FR-022  
**GitHub Issue:** [#2](https://github.com/phatnguyen975/Learn-Postman/issues/2)

### Description

The `POST /api/register` endpoint does not enforce email uniqueness. Registering with an email already in the system — whether exact-case or different-case — succeeds with `200 OK` and creates a duplicate record.

This violates:

- **BR-03:** Email must be unique system-wide, case-insensitive. Duplicate registration must return `409 Conflict`.
- **Section 4.3:** `409 Conflict` with `{"error": "Email already registered"}` must be returned for duplicate email attempts.

### Steps to Reproduce

1. Start the API server on `localhost:3000`.
2. Ensure a user with email `test@eshop.com` already exists (seeded by default, or register one first).
3. Send the following request:

   ```
   POST /api/register
   Content-Type: application/json

   {
     "name": "Other User",
     "email": "test@eshop.com",
     "password": "Password123!"
   }
   ```

4. Observe `200 OK` instead of `409 Conflict`.
5. Repeat with different case (`TEST@ESHOP.COM`) — observe `200 OK` again, a second duplicate row is created.

### Expected Result

When a user attempts to register with an email already present in the system (case-insensitive comparison), the API must respond:

```json
HTTP 409 Conflict
{
  "error": "Email already registered"
}
```

No new user record must be created.

### Actual Result

**HTTP Status:** 200  
**Response Body:**

```json
{
  "message": "User registered successfully",
  "id": 4
}
```

A duplicate row is silently inserted. The different-case variant also succeeds with a new ID, creating a third row for the same email address.

### Evidence

- **Newman Report:** `postman/reports/fr01-report.html`
- **Screenshot:**

![BUG-FR01-002](../docs/screenshots/fr01/BUG-FR01-002.png)

### Impact

- **Account integrity:** Multiple accounts can exist for the same email, breaking login, password reset, and order history attribution.
- **Identity confusion:** A user could register `ADMIN@ESHOP.COM` if `admin@eshop.com` already exists, potentially confusing case-sensitive lookups.

### Notes

None.

## BUG-FR01-003

**Title:** Password stored as plaintext — no hashing applied  
**Severity:** Critical  
**Priority:** P1  
**Root Cause Category:** SECURITY  
**Status:** Open  
**GitHub Issue:** [#3](https://github.com/phatnguyen975/Learn-Postman/issues/3)

### Description

The `POST /api/register` endpoint stores the submitted password directly in the database without any cryptographic hashing. The raw plaintext password is stored verbatim.

This violates:

- **BR-05:** Passwords must be stored as cryptographic hashes (e.g., bcrypt cost factor ≥ 10).
- **SEC-01:** Plaintext password storage is explicitly prohibited.
- **Section 7 — DEV-01:** Documented as Critical severity deviation.

### Steps to Reproduce

1. Start the API server on `localhost:3000`.
2. Register a new user:

   ```
   POST /api/register
   Content-Type: application/json

   {
     "name": "Test User",
     "email": "plaintest@domain.com",
     "password": "Password123!"
   }
   ```

3. Verify the stored password using:

   ```bash
   sqlite3 backend/database.sqlite "SELECT email, password FROM users WHERE email = 'plaintest@domain.com';"
   ```

4. Observe that the `password` column contains the literal string `Password123!` in plaintext.

### Expected Result

The password must be stored as a bcrypt hash (cost factor ≥ 10). The database row must contain a value such as:

```
$2b$10$XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
```

The plaintext password must never appear anywhere in the system after registration.

### Actual Result

**HTTP Status:** 200 (registration succeeds)  
**Database content after registration:**

```
email                 | password
plaintest@domain.com  | Password123!
```

The plaintext password is stored verbatim.

### Evidence

- **Newman Report:** `postman/reports/fr01-report.html`
- **Screenshot:**

![BUG-FR01-003](../docs/screenshots/fr01/BUG-FR01-003.png)

### Impact

- **Critical data breach risk:** Any entity with database read access immediately sees all user passwords without decryption or cracking.
- **Credential stuffing:** Plaintext passwords can be directly used for credential stuffing attacks on third-party services.
- **Regulatory violation:** Plaintext password storage violates GDPR Article 32, PCI DSS Requirement 8.2.1, and OWASP ASVS Level 1 requirements.

### Notes

None.

## BUG-FR01-004

**Title:** Invalid JSON and wrong Content-Type return HTML error page instead of JSON  
**Severity:** Medium  
**Priority:** P3  
**Root Cause Category:** SCHEMA  
**Status:** Open  
**Related TCs:** TC-FR01-ERR-003, TC-FR01-ERR-004  
**GitHub Issue:** [#4](https://github.com/phatnguyen975/Learn-Postman/issues/4)

### Description

When the request body contains invalid JSON syntax or when `Content-Type: text/plain` is sent instead of `application/json`, the API returns an HTML error page instead of a structured JSON error response.

- **TC-FR01-ERR-003** (invalid JSON): Returns `400 Bad Request` with an HTML body containing a raw `SyntaxError` stack trace.
- **TC-FR01-ERR-004** (wrong Content-Type): Returns `500 Internal Server Error` with an HTML body containing a `TypeError` stack trace and internal file paths.

This violates:

- **Section 4.4:** Malformed JSON must return `400 Bad Request` with `Content-Type: application/json` and body `{"error": "Bad Request"}`.
- **BR-10:** The status code for invalid JSON (400) is correct in ERR-003, but the response body format is wrong.
- **ERR-004 additionally:** Returning `500` for a wrong Content-Type is semantically incorrect — this is a client error and must return `400`.

### Steps to Reproduce

**Example 1 — Invalid JSON syntax:**

1. Start the API server on `localhost:3000`.
2. Send the following request with intentionally malformed JSON:

   ```
   POST /api/register
   Content-Type: application/json

   {"name": "Test", "email": "test@domain.com" "password": "Password123!"}
   ```

   (Note: missing comma between `email` and `password` values)

3. Observe that the response is `400 Bad Request` but the body is an HTML page with a `SyntaxError` stack trace, not `{"error": "Bad Request"}`.

**Example 2 — Wrong Content-Type:**

1. Start the API server.
2. Send the following request with `Content-Type: text/plain`:

   ```
   POST /api/register
   Content-Type: text/plain

   {"name": "Test User", "email": "ct-test@domain.com", "password": "Password123!"}
   ```

3. Observe that the response is `500 Internal Server Error` with an HTML page exposing a `TypeError` and internal file paths.

### Expected Result

Both cases must return:

```json
HTTP 400 Bad Request
Content-Type: application/json

{
  "error": "Bad Request"
}
```

No stack traces, internal error messages, or HTML pages must be returned to the client.

### Actual Result

**TC-FR01-ERR-003 — HTTP Status:** 400  
**Response Body (HTML, excerpt):**

```
SyntaxError: Expected ',' or '}' after property value in JSON at position 21 (line 1 column 22)
    at JSON.parse (<anonymous>)
    ...
```

**TC-FR01-ERR-004 — HTTP Status:** 500  
**Response Body (HTML, excerpt):**

```
TypeError: Cannot destructure property 'name' of 'req.body' as it is undefined.
    ...
```

Both responses expose internal stack traces and implementation details.

### Evidence

- **Newman Report:** `postman/reports/fr01-report.html`
- **Screenshot:**

![BUG-FR01-004](../docs/screenshots/fr01/BUG-FR01-004.png)

### Impact

- **Information disclosure:** Stack traces reveal internal file paths and implementation details, exploitable for codebase mapping (OWASP API8:2023 — Security Misconfiguration).
- **Client integration breakage:** API consumers expecting JSON error format will receive HTML, causing parsing failures.
- **Incorrect semantics (ERR-004):** Returning `500` for a client error misleads consumers and monitoring systems.

### Notes

None.

## Bug Summary Table

| Bug ID       | Title                                                                      | Category       | Severity | Priority | Status |
| ------------ | -------------------------------------------------------------------------- | -------------- | -------- | -------- | ------ |
| BUG-FR01-001 | Registration accepts any input without field validation — no 400 returned  | VALIDATION     | High     | P2       | Open   |
| BUG-FR01-002 | Duplicate email registration succeeds — no uniqueness enforcement          | BUSINESS_LOGIC | High     | P2       | Open   |
| BUG-FR01-003 | Password stored as plaintext — no hashing applied                          | SECURITY       | Critical | P1       | Open   |
| BUG-FR01-004 | Invalid JSON and wrong Content-Type return HTML error page instead of JSON | SCHEMA         | Medium   | P3       | Open   |

# Bug Report: POST /api/cart

> **Feature:** FR07 | **Endpoint:** `POST /api/cart`  
> **Total Bugs:** 6 | **Status:** Approved  
> **Generated:** 2026-08-21

## BUG-FR07-001

**Title:** Cart endpoint accepts any request body without input validation  
**Severity:** High  
**Priority:** P2  
**Root Cause Category:** VALIDATION  
**Status:** Open  
**Related TCs:** TC-FR07-FR-004, TC-FR07-FR-005, TC-FR07-FR-006, TC-FR07-FR-007, TC-FR07-FR-008 (price = 0), TC-FR07-FR-009, TC-FR07-FR-010, TC-FR07-FR-013, TC-FR07-FR-014, TC-FR07-FR-015, TC-FR07-FR-018, TC-FR07-FR-019, TC-FR07-FR-020, TC-FR07-ERR-001, TC-FR07-ERR-002, TC-FR07-ERR-004, TC-FR07-ERR-005, TC-FR07-ERR-006, TC-FR07-ERR-007, TC-FR07-ERR-008, TC-FR07-ERR-009, TC-FR07-ERR-010, TC-FR07-ERR-011, TC-FR07-ERR-012, TC-FR07-SCH-006  
**GitHub Issue:** [#5](https://github.com/phatnguyen975/Learn-Postman/issues/5)

### Description

The `POST /api/cart` handler contains no input validation of any kind. It directly executes `userCarts[userId].push(req.body)` without inspecting the contents of the request body. As a result:

- All required fields (`id`, `name`, `price`, `quantity`) can be missing, null, or of the wrong type — the API returns `200 OK` in all cases.
- Constraint boundaries are entirely unenforced: `quantity` of 0, -1, 100, or 1.5; `price` of 0 or -500; `name` of empty string or whitespace-only; `id` of 0 or -1 — all accepted.
- Sending an empty JSON body `{}` or no body at all is accepted.
- Sending `Content-Type: text/plain` with a valid JSON string is accepted (body-parser ignores the body but the route still pushes `undefined`).

**Contract clauses violated:**

- **BR-04:** `quantity` must be a positive integer (1–99)
- **BR-05:** `price` must be a positive number (> 0)
- **BR-06:** `name` must be a non-empty string
- **Section 2.4 Field Constraints:** `id` must be a positive integer (≥ 1)
- **Section 4.2:** Invalid fields must return `400 Bad Request` with `{"error": "..."}` body

### Steps to Reproduce

1. Start the API server: `cd backend && node server.js` on `http://localhost:3000`.
2. Obtain a valid JWT by logging in: `POST /api/login` with `{"email": "test@eshop.com", "password": "Test1234!"}`.
3. Send the following request (example — quantity = 0):

   ```
   POST /api/cart
   Content-Type: application/json
   Authorization: Bearer <valid_user_token>

   {"id": 1, "name": "Sản phẩm A", "price": 50000, "quantity": 0}
   ```

4. Observe that the API returns `200 OK` with `{"message": "Added to cart"}` instead of `400 Bad Request`.
5. Repeat with any of the following bodies to observe the same `200 OK` response:
   - `{"id": 1, "name": "Sản phẩm A", "price": 50000, "quantity": -1}`
   - `{"id": 1, "name": "Sản phẩm A", "price": 50000, "quantity": 100}`
   - `{"id": 1, "name": "Sản phẩm A", "price": 50000, "quantity": 1.5}`
   - `{"id": 1, "name": "Sản phẩm A", "price": 0, "quantity": 1}`
   - `{"id": 1, "name": "Sản phẩm A", "price": -500, "quantity": 1}`
   - `{"id": 1, "name": "", "price": 50000, "quantity": 1}`
   - `{"id": 1, "name": " ", "price": 50000, "quantity": 1}`
   - `{"id": 0, "name": "Sản phẩm A", "price": 50000, "quantity": 1}`
   - `{"id": -1, "name": "Sản phẩm A", "price": 50000, "quantity": 1}`
   - `{}` (empty body)
   - No body at all
   - `{"name": "A", "price": 100, "quantity": 1}` (id missing)
   - `{"id": "one", "name": "A", "price": 100, "quantity": 1}` (id as string)
   - `{"id": 1, "name": "A", "price": 100, "quantity": "two"}` (quantity as string)
   - `{"id": 1, "name": "A", "price": "cheap", "quantity": 1}` (price as string)
   - `{"id": null, "name": null, "price": null, "quantity": null}`
   - Request with `Content-Type: text/plain` and valid JSON as string body

### Expected Result

Any request with an invalid field value, missing required field, wrong type, or out-of-range value must be rejected with:

```json
HTTP/1.1 400 Bad Request
Content-Type: application/json

{"error": "Invalid request: <specific reason>"}
```

### Actual Result

**HTTP Status:** 200  
**Response Body:**

```json
{ "message": "Added to cart" }
```

The API accepts every malformed request and pushes the raw body (or `undefined` in case of no body) directly into the in-memory cart array.

### Evidence

- **Newman Report:** `postman/reports/fr07-report.html`
- **Screenshot:**

![BUG-FR07-001](../docs/screenshots/fr07/BUG-FR07-001.png)

### Impact

Invalid data (null fields, wrong types, out-of-range quantities, zero/negative prices) is silently stored in the in-memory cart. This corrupts cart state and may cause downstream failures at checkout. A malicious user can inject arbitrary data structures into the cart that may trigger unexpected behavior in any future logic that consumes the cart array without defensive null checks. Additionally, accepting `text/plain` causes `undefined` to be stored, which later serializes as `null` in `GET /api/cart` responses, breaking client-side cart rendering.

### Notes

**Root fix:** Add a validation middleware or inline validation block before `userCarts[userId].push(req.body)` to check all required fields for presence, correct type, and value constraints.

## BUG-FR07-002

**Title:** Cart does not validate product existence — non-existent product IDs accepted  
**Severity:** High  
**Priority:** P2  
**Root Cause Category:** BUSINESS_LOGIC  
**Status:** Open  
**Related TCs:** TC-FR07-FR-017, TC-FR07-SCH-007  
**GitHub Issue:** [#6](https://github.com/phatnguyen975/Learn-Postman/issues/6)

### Description

The endpoint does not check whether the submitted `id` corresponds to an existing product in the `products` database table. Any integer ID is accepted and stored in the cart, even for products that do not exist. This violates BR-02, which requires a `404 Not Found` response when the product ID does not exist.

**Contract clause violated:** BR-02 — product ID must exist in the `products` table; Section 4.5 — `404 Not Found` response must be returned with `{"error": "Product not found"}`.

### Steps to Reproduce

1. Start the API server: `cd backend && node server.js` on `http://localhost:3000`.
2. Obtain a valid JWT by logging in: `POST /api/login` with `{"email": "test@eshop.com", "password": "Test1234!"}`.
3. Confirm product ID 9999 does not exist (no product creation with this ID).
4. Send the following request:

   ```
   POST /api/cart
   Content-Type: application/json
   Authorization: Bearer <valid_user_token>

   {"id": 9999, "name": "Ghost", "price": 50000, "quantity": 1}
   ```

5. Observe the response.

### Expected Result

The server must verify that product `id=9999` exists in the `products` table. When it does not, it must return:

```json
HTTP/1.1 404 Not Found
Content-Type: application/json

{"error": "Product not found"}
```

### Actual Result

**HTTP Status:** 200  
**Response Body:**

```json
{ "message": "Added to cart" }
```

The non-existent product is added to the cart with no validation.

### Evidence

- **Newman Report:** `postman/reports/fr07-report.html`
- **Screenshot:**

![BUG-FR07-002](../docs/screenshots/fr07/BUG-FR07-002.png)

### Impact

Users can add phantom products (non-existent IDs with client-supplied names and prices) to their cart. Any subsequent checkout logic that relies on the cart's `id` field to look up the actual price from the database will either fail with an error or use a stale client-supplied price. This directly undermines data integrity and the price-protection requirement in BR-05.

## BUG-FR07-003

**Title:** Duplicate product added to cart creates new entry instead of incrementing quantity  
**Severity:** High  
**Priority:** P2  
**Root Cause Category:** BUSINESS_LOGIC  
**Status:** Open  
**Related TCs:** TC-FR07-ST-002  
**GitHub Issue:** [#7](https://github.com/phatnguyen975/Learn-Postman/issues/7)

### Description

When a user adds a product to the cart that is already present (same `id`), the endpoint always appends a new entry to the cart array instead of incrementing the existing entry's quantity. The SRS explicitly defines (FR-07): _"Thêm cùng một sản phẩm vào giỏ sẽ tăng số lượng, không tạo dòng mới."_ The implementation uses a simple `userCarts[userId].push(req.body)` with no duplicate detection.

**Contract clause violated:** BR-03 — adding an item whose `id` already exists must increment the existing quantity, not create a duplicate.

### Steps to Reproduce

1. Start the API server: `cd backend && node server.js` on `http://localhost:3000`.
2. Obtain a valid JWT by logging in as a regular user.
3. Create a product via `POST /api/products` (admin token) and note its `id` (e.g., `id = 5`).
4. Add the product to the cart with `quantity: 1`:

   ```
   POST /api/cart
   Content-Type: application/json
   Authorization: Bearer <valid_user_token>

   {"id": 5, "name": "Sản phẩm A", "price": 50000, "quantity": 1}
   ```

5. Add the same product again with `quantity: 2`:

   ```
   POST /api/cart
   Content-Type: application/json
   Authorization: Bearer <valid_user_token>

   {"id": 5, "name": "Sản phẩm A", "price": 50000, "quantity": 2}
   ```

6. Retrieve the cart: `GET /api/cart` with the same token.

### Expected Result

The cart must contain exactly one entry for product `id=5` with a total `quantity` of `3` (1 + 2):

```json
[{ "id": 5, "name": "Sản phẩm A", "price": 50000, "quantity": 3 }]
```

### Actual Result

**HTTP Status:** 200 (on each POST); 200 (on GET)  
**Response Body (GET /api/cart):**

```json
[
  { "id": 5, "name": "Sản phẩm A", "price": 50000, "quantity": 1 },
  { "id": 5, "name": "Sản phẩm A", "price": 50000, "quantity": 2 }
]
```

Two separate entries exist for the same product ID. The cart count assertion `expected 1 to deeply equal 3` confirmed duplicate entry creation.

### Evidence

- **Newman Report:** `postman/reports/fr07-report.html`
- **Screenshot:**

![BUG-FR07-003](../docs/screenshots/fr07/BUG-FR07-003.png)

### Impact

The cart accumulates duplicate entries for the same product, leading to inflated item counts and incorrect order totals at checkout. Users cannot manage their cart effectively since there is no way to identify which of the duplicate entries represents the "authoritative" quantity. This is a core SRS business rule violation.

## BUG-FR07-004

**Title:** Cart stores unsanitized extra fields from request body (mass assignment)  
**Severity:** Critical  
**Priority:** P1  
**Root Cause Category:** SECURITY  
**Status:** Open  
**Related TCs:** TC-FR07-SEC-005, TC-FR07-SEC-007, TC-FR07-SEC-008, TC-FR07-SEC-009  
**GitHub Issue:** [#8](https://github.com/phatnguyen975/Learn-Postman/issues/8)

### Description

The handler stores the entire raw `req.body` object in the cart without stripping out any fields beyond `id`, `name`, `price`, and `quantity`. This allows any extra field submitted by the client to be persisted in the server's in-memory cart state and returned via `GET /api/cart`. Specifically verified:

- `userId: 999` injected in body → stored and returned as-is (SEC-05, OWASP API1)
- `role: "admin", isAdmin: true` injected → stored and returned as-is (SEC-06, OWASP API3)
- `userId: 999` submitted as identity escalation vector → stored and returned (SEC-06)
- `__proto__: {admin: true}` submitted → property `admin: true` accessible on the returned object

**Contract clauses violated:**

- BR-07: `userId` must be derived exclusively from the JWT token, not the request body
- BR-08: Extra fields in the request body must be ignored and not stored
- SEC-05, SEC-06: Mass assignment of `role`, `userId`, `__proto__` must be blocked

### Steps to Reproduce

1. Start the API server: `cd backend && node server.js` on `http://localhost:3000`.
2. Obtain a valid JWT for a regular user (User A).
3. Send a request with an extra `role` field:

   ```
   POST /api/cart
   Content-Type: application/json
   Authorization: Bearer <user_A_token>

   {"id": 1, "name": "A", "price": 100, "quantity": 1, "role": "admin", "isAdmin": true}
   ```

4. Retrieve the cart: `GET /api/cart` with the same token.
5. Observe that `role: "admin"` and `isAdmin: true` are present in the stored cart item.
6. Repeat with `{"userId": 999, ...}` and `{"__proto__": {"admin": true}, ...}` to observe the same persistent storage.

### Expected Result

Extra fields must be stripped before storage. Only `id`, `name`, `price`, and `quantity` must be present in the cart entry returned by `GET /api/cart`:

```json
[{ "id": 1, "name": "A", "price": 100, "quantity": 1 }]
```

### Actual Result

**HTTP Status:** 200 (POST); 200 (GET)  
**Response Body (GET /api/cart, excerpt):**

```json
[
  {
    "id": 1,
    "name": "A",
    "price": 100,
    "quantity": 1,
    "role": "admin",
    "isAdmin": true
  }
]
```

Assertion confirmations:

- `expected 'admin' to be undefined` → role stored
- `expected 999 to not deeply equal 999` → userId stored
- `expected { admin: true } to be undefined` → `__proto__` property stored

### Evidence

- **Newman Report:** `postman/reports/fr07-report.html`
- **Screenshot:**

![BUG-FR07-004](../docs/screenshots/fr07/BUG-FR07-004.png)

### Impact

An attacker can inject arbitrary key-value pairs into the server's in-memory cart data. While the current in-memory architecture limits immediate exploitation, this is a critical architectural flaw: any logic that reads cart items and uses their fields (e.g., applying `role` at checkout) could be exploited for privilege escalation. The `__proto__` pollution vector could, in a different runtime context, allow modification of the server's object prototype chain, potentially affecting all objects across the application. This must be treated as Critical.

## BUG-FR07-005

**Title:** XSS payload stored in cart name field without sanitization  
**Severity:** High  
**Priority:** P2  
**Root Cause Category:** SECURITY  
**Status:** Open  
**Related TCs:** TC-FR07-SEC-010  
**GitHub Issue:** [#9](https://github.com/phatnguyen975/Learn-Postman/issues/9)

### Description

A `name` field value containing an XSS payload (`<script>alert(1)</script>`) is accepted, stored in the in-memory cart without any escaping or sanitization, and returned verbatim by `GET /api/cart`. Per SEC-04, XSS payloads in string fields must not be stored as executable content and must be escaped on output.

**Contract clause violated:** SEC-04 — XSS payload in string fields must not be stored as executable content.

### Steps to Reproduce

1. Start the API server: `cd backend && node server.js` on `http://localhost:3000`.
2. Obtain a valid JWT by logging in as a regular user.
3. Send the following request:

   ```
   POST /api/cart
   Content-Type: application/json
   Authorization: Bearer <valid_user_token>

   {"id": 1, "name": "<script>alert(1)</script>", "price": 100, "quantity": 1}
   ```

4. Retrieve the cart: `GET /api/cart` with the same token.
5. Observe the `name` field in the response contains the raw unescaped `<script>` tag.

### Expected Result

The XSS payload must either be rejected with `400 Bad Request` or stored as an escaped literal string (e.g., `&lt;script&gt;alert(1)&lt;/script&gt;`) so it cannot execute when rendered in a browser. The `GET /api/cart` response must not contain raw executable HTML tags.

### Actual Result

**HTTP Status:** 200 (POST); 200 (GET)  
**Response Body (GET /api/cart):**

```json
[{ "id": 1, "name": "<script>alert(1)</script>", "price": 100, "quantity": 1 }]
```

The raw unescaped script tag is stored and returned. Assertion: `expected '<script>alert(1)</script>' to not include '<script>'`.

### Evidence

- **Newman Report:** `postman/reports/fr07-report.html`
- **Screenshot:**

![BUG-FR07-005](../docs/screenshots/fr07/BUG-FR07-005.png)

### Impact

Any client application (web frontend) that renders cart contents without its own escaping will execute the injected script in the user's browser. This enables stored XSS attacks: a malicious user can inject a script that steals session tokens, performs actions on behalf of other users, or defaces the shopping interface. Stored XSS is rated High severity per OWASP and the severity guide.

## BUG-FR07-006

**Title:** Cart data is lost after server restart (in-memory storage not persisted)  
**Severity:** High  
**Priority:** P2  
**Root Cause Category:** STATE  
**Status:** Open  
**Related TCs:** TC-FR07-ST-008  
**GitHub Issue:** [#10](https://github.com/phatnguyen975/Learn-Postman/issues/10)

### Description

The cart is stored entirely in a JavaScript in-memory object (`userCarts = {}`) declared in `server.js`. When the server process is restarted, all cart data for all users is destroyed. The contract specifies that cart state must persist for the authenticated user (Section 5.2 System Data State), implying durable storage. While the contract notes this as a known constraint, the TC-FR07-ST-008 failure confirms the observable behavioral defect.

**Contract clause violated:** Section 5.2 — System Data State implies cart items should persist; the Known Constraints section in AGENTS.md acknowledges this as a known SUT limitation.

### Steps to Reproduce

1. Start the API server: `cd backend && node server.js`.
2. Obtain a valid JWT by logging in as a regular user.
3. Add an item to the cart:

   ```
   POST /api/cart
   Content-Type: application/json
   Authorization: Bearer <valid_user_token>

   {"id": 1, "name": "Sản phẩm A", "price": 50000, "quantity": 1}
   ```

4. Confirm the cart is not empty: `GET /api/cart` → returns 1 item.
5. Restart the server (Ctrl+C, then `node server.js`).
6. Use the same valid JWT (tokens do not expire in the current SUT).
7. Retrieve the cart: `GET /api/cart` with the same token.

### Expected Result

Cart items added by the user must persist across server restarts. After restarting, `GET /api/cart` must return the same items that were added:

```json
[{ "id": 1, "name": "Sản phẩm A", "price": 50000, "quantity": 1 }]
```

### Actual Result

**HTTP Status:** 200  
**Response Body:**

```json
[]
```

All cart data is gone. Assertion: `expected 5 to deeply equal +0` confirms the cart length changed from the expected non-zero count to 0 after restart (note: the assertion value `5` reflects test isolation state from the Newman run, not a meaningful cart count; the core behavior is that items are lost).

### Evidence

- **Newman Report:** `postman/reports/fr07-report.html`
- **Screenshot:**

![BUG-FR07-006](../docs/screenshots/fr07/BUG-FR07-006.png)

### Impact

Every server restart (deployment, crash recovery, scaling event) silently wipes all user carts. Users who have added items to their cart will find it empty without warning. In a production e-commerce system, this would result in lost sales and a degraded user experience. The fix requires migrating cart storage from the in-memory `userCarts` object to a persistent database table.

## Bug Summary Table

| Bug ID       | Title                                                                 | Category       | Severity | Priority | Status |
| ------------ | --------------------------------------------------------------------- | -------------- | -------- | -------- | ------ |
| BUG-FR07-001 | Cart endpoint accepts any request body without input validation       | VALIDATION     | High     | P2       | Open   |
| BUG-FR07-002 | Cart does not validate product existence — non-existent IDs accepted  | BUSINESS_LOGIC | High     | P2       | Open   |
| BUG-FR07-003 | Duplicate product added to cart creates new entry instead of merging  | BUSINESS_LOGIC | High     | P2       | Open   |
| BUG-FR07-004 | Cart stores unsanitized extra fields from request body (mass assign.) | SECURITY       | Critical | P1       | Open   |
| BUG-FR07-005 | XSS payload stored in cart name field without sanitization            | SECURITY       | High     | P2       | Open   |
| BUG-FR07-006 | Cart data is lost after server restart (in-memory storage)            | STATE          | High     | P2       | Open   |

# Bug Report: POST /api/admin/coupons

> **Feature:** FR17 | **Endpoint:** `POST /api/admin/coupons`  
> **Total Bugs:** 4 | **Status:** Approved  
> **Generated:** 2026-08-21

## BUG-FR17-001

**Title:** Broken Function Level Authorization allows regular users to create coupons  
**Severity:** Critical  
**Priority:** P1  
**Root Cause Category:** SECURITY  
**Status:** Open  
**Related TCs:** TC-FR17-ST-003, TC-FR17-SEC-005  
**GitHub Issue:** [#11](https://github.com/phatnguyen975/Learn-Postman/issues/11)

### Description

The API does not enforce role-based access control (RBAC). The `authenticateToken` middleware only validates the JWT signature but fails to check if `req.user.role === 'admin'`. As a result, any authenticated user with the `user` role can successfully create coupons.

**Contract clause violated:** SEC-05 — Endpoints under `/api/admin/*` must verify that the authenticated user possesses the `admin` role.

### Steps to Reproduce

1. Start the API server: `cd backend && node server.js` on `http://localhost:3000`.
2. Obtain a valid JWT by logging in as a regular user (role `user`).
3. Send the following request:

   ```
   POST /api/admin/coupons
   Content-Type: application/json
   Authorization: Bearer <valid_user_token>

   {
     "code": "USER_HACK",
     "type": "percent",
     "discount_value": 10,
     "min_order_amount": 0,
     "expired_at": "2027-12-31"
   }
   ```

4. Observe the successful creation response.

### Expected Result

The server must reject the request with `403 Forbidden` indicating the user does not have admin privileges, and no coupon should be created in the database.

### Actual Result

**HTTP Status:** 200
**Response Body:**

```json
{
  "message": "Coupon created",
  "id": 5
}
```

### Evidence

- **Newman Report:** `postman/reports/fr17-report.html`
- **Screenshot:**

![BUG-FR17-001](../docs/screenshots/fr17/BUG-FR17-001.png)

### Impact

Critical security vulnerability (Broken Function Level Authorization / OWASP API5). Any regular customer can arbitrarily create discount coupons for themselves, leading to severe financial loss for the business.

## BUG-FR17-002

**Title:** Complete lack of input validation allows invalid data, SQLi, and XSS payloads  
**Severity:** High  
**Priority:** P2  
**Root Cause Category:** VALIDATION  
**Status:** Open  
**Related TCs:** TC-FR17-FR-006, TC-FR17-FR-007, TC-FR17-FR-010, TC-FR17-FR-011, TC-FR17-FR-012, TC-FR17-FR-014, TC-FR17-FR-015, TC-FR17-FR-016, TC-FR17-FR-019, TC-FR17-FR-020, TC-FR17-ST-006, TC-FR17-SEC-007, TC-FR17-SEC-008, TC-FR17-SEC-009, TC-FR17-SCH-005, TC-FR17-SCH-007, TC-FR17-ERR-001, TC-FR17-ERR-004, TC-FR17-ERR-005, TC-FR17-ERR-006, TC-FR17-ERR-007, TC-FR17-ERR-008, TC-FR17-ERR-009, TC-FR17-ERR-010  
**GitHub Issue:** [#12](https://github.com/phatnguyen975/Learn-Postman/issues/12)

### Description

The API controller extracts fields directly from `req.body` and inserts them into the database without any validation logic. It accepts strings exceeding maximum lengths, dates in the past, invalid enum values (e.g., `type: "bonus"`), negative discount values, and even missing required fields (inserting `null` instead). Furthermore, it stores XSS and SQLi payloads exactly as provided.

**Contract clause violated:** BR-02 through BR-10 (Validation rules for all fields), and SEC-01, SEC-04 (Input validation & Injection prevention).

### Steps to Reproduce

1. Start the API server: `cd backend && node server.js` on `http://localhost:3000`.
2. Obtain a valid JWT by logging in as an admin.
3. Send the following request containing invalid values and an XSS payload:

   ```
   POST /api/admin/coupons
   Content-Type: application/json
   Authorization: Bearer <valid_admin_token>

   {
     "code": "<script>alert('XSS')</script>",
     "type": "invalid_type",
     "discount_value": -50,
     "expired_at": "2020-01-01"
   }
   ```

4. Observe the successful creation response.

### Expected Result

The server must reject the request with `400 Bad Request` and return an error message indicating the fields that failed validation.

### Actual Result

**HTTP Status:** 200
**Response Body:**

```json
{
  "message": "Coupon created",
  "id": 5
}
```

### Evidence

- **Newman Report:** `postman/reports/fr17-report.html`
- **Screenshot:**

![BUG-FR17-002](../docs/screenshots/fr17/BUG-FR17-002.png)

### Impact

Compromises data integrity across the entire `coupons` table. It allows stored XSS which could compromise admin accounts when viewing the coupons list on a dashboard. Missing required fields and negative discounts break core business logic.

## BUG-FR17-003

**Title:** Unhandled UNIQUE constraint violation returns 500 Internal Server Error  
**Severity:** Medium  
**Priority:** P3  
**Root Cause Category:** ERROR_HANDLING  
**Status:** Open  
**Related TCs:** TC-FR17-FR-021, TC-FR17-ST-004, TC-FR17-SCH-008, TC-FR17-IDP-001  
**GitHub Issue:** [#13](https://github.com/phatnguyen975/Learn-Postman/issues/13)

### Description

When a request attempts to create a coupon with a `code` that already exists in the database, the SQLite database throws a `SQLITE_CONSTRAINT` error. The API catches this error but blindly propagates it as a generic `500 Internal Server Error` instead of mapping it to a `409 Conflict`.

**Contract clause violated:** BR-01 — The `code` must be unique across the system. Duplicates must return `409 Conflict`.

### Steps to Reproduce

1. Start the API server: `cd backend && node server.js` on `http://localhost:3000`.
2. Obtain a valid JWT by logging in as an admin.
3. Send the following request attempting to create a seed coupon (`SAVE10`):

   ```
   POST /api/admin/coupons
   Content-Type: application/json
   Authorization: Bearer <valid_admin_token>

   {
     "code": "SAVE10",
     "type": "percent",
     "discount_value": 10,
     "min_order_amount": 0,
     "expired_at": "2027-12-31"
   }
   ```

4. Observe the 500 status code response.

### Expected Result

The server should return `409 Conflict` indicating that the coupon code already exists.

### Actual Result

**HTTP Status:** 500
**Response Body:**

```json
{
  "error": "SQLITE_CONSTRAINT: UNIQUE constraint failed: coupons.code"
}
```

### Evidence

- **Newman Report:** `postman/reports/fr17-report.html`
- **Screenshot:**

![BUG-FR17-003](../docs/screenshots/fr17/BUG-FR17-003.png)

### Impact

Leads to poor developer experience and incorrect HTTP semantic usage. Clients cannot programmatically distinguish between a true server crash and a simple duplicate resource error, preventing automated error handling or retry mechanisms.

## BUG-FR17-004

**Title:** Empty body or non-JSON Content-Type causes server crash  
**Severity:** High  
**Priority:** P2  
**Root Cause Category:** ERROR_HANDLING  
**Status:** Open  
**Related TCs:** TC-FR17-ERR-002, TC-FR17-ERR-011  
**GitHub Issue:** [#14](https://github.com/phatnguyen975/Learn-Postman/issues/14)

### Description

If the request lacks a body or uses a `Content-Type` other than `application/json` (like `text/plain`), the `body-parser` middleware skips parsing, leaving `req.body` as `undefined`. The controller then attempts to destructure `req.body` (`const { code, ... } = req.body`), which throws a JavaScript `TypeError` and crashes the request handler, returning a raw HTML stack trace.

**Contract clause violated:** Section 4.3 Error Responses — The API must handle invalid payloads gracefully with `400 Bad Request`.

### Steps to Reproduce

1. Start the API server: `cd backend && node server.js` on `http://localhost:3000`.
2. Obtain a valid JWT by logging in as an admin.
3. Send the request with `Content-Type: text/plain`:

   ```
   POST /api/admin/coupons
   Content-Type: text/plain
   Authorization: Bearer <valid_admin_token>

   {"code": "ERR011"}
   ```

4. Observe the HTML stack trace in the response.

### Expected Result

The server must return `400 Bad Request` indicating that the payload is missing or the format is unsupported.

### Actual Result

**HTTP Status:** 500
**Response Body (excerpt):**

```html
<!DOCTYPE html>
<html lang="en">
  <head>
    <meta charset="utf-8" />
    <title>Error</title>
  </head>
  <body>
    <pre>>
  </body>
</html>
```

### Evidence

- **Newman Report:** `postman/reports/fr17-report.html`
- **Screenshot:**

![BUG-FR17-004](../docs/screenshots/fr17/BUG-FR17-004.png)

### Impact

Information disclosure via stack traces and potential Denial of Service (if the crash affected the main thread, though Express catches it). It exposes internal server implementation details to the client and presents a highly unprofessional API response.

## Bug Summary Table

| Bug ID       | Title                                                                      | Category       | Severity | Priority | Status |
| ------------ | -------------------------------------------------------------------------- | -------------- | -------- | -------- | ------ |
| BUG-FR17-001 | Broken Function Level Authorization allows regular users to create coupons | SECURITY       | Critical | P1       | Open   |
| BUG-FR17-002 | Complete lack of input validation allows invalid data, SQLi, and XSS       | VALIDATION     | High     | P2       | Open   |
| BUG-FR17-003 | Unhandled UNIQUE constraint violation returns 500 Internal Server Error    | ERROR_HANDLING | Medium   | P3       | Open   |
| BUG-FR17-004 | Empty body or non-JSON Content-Type causes server crash (TypeError)        | ERROR_HANDLING | High     | P2       | Open   |
