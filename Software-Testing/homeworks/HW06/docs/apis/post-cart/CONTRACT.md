# API Contract: POST /api/cart

> **Status:** Approved  
> **Last updated:** 2026-08-20  
> **Source documents:** `docs/sut/api-spec.md`, `docs/sut/srs.md`

## 1. Overview

| Property          | Value                                    |
| ----------------- | ---------------------------------------- |
| **Endpoint**      | `POST /api/cart`                         |
| **Feature**       | FR-07 — Shopping Cart (Add Item to Cart) |
| **Auth required** | Yes                                      |
| **Auth type**     | Bearer JWT                               |
| **Role required** | `user` (any authenticated user)          |
| **Idempotency**   | Not idempotent                           |

## 2. Request

### 2.1 Headers

| Header          | Required | Value / Format     | Notes                                                            |
| --------------- | -------- | ------------------ | ---------------------------------------------------------------- |
| `Authorization` | Yes      | `Bearer <JWT>`     | Token must be valid and signed with system secret                |
| `Content-Type`  | Yes      | `application/json` | Body is parsed as JSON; missing this header causes parse failure |

### 2.2 Path Parameters

N/A

### 2.3 Query Parameters

N/A

### 2.4 Request Body

```json
{
  "id": 1,
  "name": "Sản phẩm A",
  "price": 100000,
  "quantity": 2
}
```

#### Field Constraints

| Field      | Type    | Required | Constraints                                                                                                                                    | Default | Notes                                                                                                                                                                                                           |
| ---------- | ------- | -------- | ---------------------------------------------------------------------------------------------------------------------------------------------- | ------- | --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `id`       | integer | Yes      | Must be a positive integer (≥ 1); must reference an existing product in the `products` table [inferred — e-commerce data integrity convention] | N/A     | Identifies the product being added to the cart. Sent by client. Server should validate product existence [inferred — e-commerce convention]. Implementation currently does NOT validate.                        |
| `name`     | string  | Yes      | Non-empty string; maxLength: 255 characters [inferred — OWASP Input Validation, matches product name limit in FR-15]                           | N/A     | Display name of the product. Should match the actual product name in the DB. Client-supplied; not re-validated by server in current implementation.                                                             |
| `price`    | number  | Yes      | Must be a positive number (> 0); precision up to 2 decimal places [inferred — e-commerce numeric field convention, OWASP]                      | N/A     | Unit price of the product in VND. **Critical:** Server must NOT trust client-supplied price for order total calculations (see BR-05). Implementation currently stores client-supplied value without validation. |
| `quantity` | integer | Yes      | Integer in range 1–99 [inferred — e-commerce quantity convention per inference-guide.md]                                                       | N/A     | Number of units to add. Must be a whole positive number. Implementation currently accepts any value including 0, negatives, and non-integers.                                                                   |

## 3. Business Rules

- **BR-01:** The endpoint requires a valid JWT token. Requests without an `Authorization` header must be rejected with `401 Unauthorized`. Requests with a malformed, expired, or tampered JWT must be rejected with `403 Forbidden`.
- **BR-02:** The `id` field must correspond to a product that exists in the `products` database table. Attempting to add a non-existent product ID must be rejected with `404 Not Found`. [inferred — e-commerce data integrity convention]
- **BR-03:** Adding an item whose `id` already exists in the authenticated user's cart must **increment the existing item's quantity** by the requested `quantity` value, rather than creating a new cart entry. (Source: SRS FR-07 — "Thêm cùng một sản phẩm vào giỏ sẽ tăng số lượng, không tạo dòng mới.") **Current implementation violates this rule** — it always appends a new entry via `push()`.
- **BR-04:** The `quantity` field must be a positive integer (≥ 1). Values of 0, negative numbers, non-integers (e.g., 1.5), or non-numeric strings must be rejected with `400 Bad Request`. [inferred — e-commerce convention]
- **BR-05:** The `price` field supplied by the client must be a positive number (> 0). Values of 0, negative numbers, or non-numeric types must be rejected with `400 Bad Request`. [inferred — e-commerce convention]. Additionally, the server **must NOT use the client-supplied `price` as the authoritative price** for checkout total calculation — it must re-fetch the price from the `products` table at checkout time to prevent price manipulation. (Source: SRS FR-08 — "Backend phải tự tính lại tổng tiền.")
- **BR-06:** The `name` field must be a non-empty string. An empty string, null, or whitespace-only value must be rejected with `400 Bad Request`. [inferred — OWASP Input Validation Cheat Sheet]
- **BR-07:** Cart state is scoped to the authenticated user. Each user has their own independent cart. A user cannot add items to another user's cart by any means (e.g., by supplying a different `userId` in the body). The `userId` is derived exclusively from the JWT token payload, not from the request body.
- **BR-08:** Additional (extra) fields in the request body beyond `id`, `name`, `price`, and `quantity` must be ignored. They must not be stored or processed. [inferred — OWASP Mass Assignment prevention; current implementation stores the entire `req.body` object including any extra fields]
- **BR-09:** The `quantity` field total for any single product in the cart should not exceed a maximum per-line threshold of 99 units. [inferred — e-commerce quantity management convention]

## 4. Response Definitions

### 4.1 200 OK — Item Added Successfully

**Trigger:** Valid JWT token provided; request body contains all required fields; product ID exists (when BR-02 enforced); quantity and price are valid positive numbers.

```json
{
  "message": "Added to cart"
}
```

**Headers returned:**

- `Content-Type: application/json`

**Notes:** The response does NOT return the updated cart state or a cart item ID. Only the fixed message string is returned.

### 4.2 400 Bad Request — Invalid Field Values

**Trigger:** Any required field fails validation: `quantity` ≤ 0, `price` ≤ 0, `name` is empty or null, `id` is not a positive integer. [inferred — RFC 9110, OWASP Input Validation]

```json
{
  "error": "Invalid request: <specific reason>"
}
```

**Notes:** Current implementation accepts all values without validation and always returns 200.

### 4.3 401 Unauthorized — Missing Token

**Trigger:** The `Authorization` header is absent from the request.

```json
{
  "error": "Unauthorized"
}
```

**Headers returned:**

- `Content-Type: application/json`

**Notes:** Returned by the `authenticateToken` middleware.

### 4.4 403 Forbidden — Invalid / Expired / Tampered Token

**Trigger:** The `Authorization` header is present but the token is: (a) malformed/cannot be parsed, (b) signed with an incorrect secret, (c) has an invalid structure, or (d) refers to a deleted user.

```json
{
  "error": "Forbidden"
}
```

**Headers returned:**

- `Content-Type: application/json`

**Notes:** Returned by `jwt.verify()` callback in `authenticateToken` middleware. Note: the current implementation JWT tokens have **no expiry** (`jwt.sign()` is called without an `exp` claim), so token expiry cannot be tested against the live server naturally. A forged token with a past `exp` can be used for this test.

### 4.5 404 Not Found — Product Not Found

**Trigger:** The `id` field does not correspond to any product in the `products` database table. [inferred — e-commerce data integrity convention]

```json
{
  "error": "Product not found"
}
```

**Notes:** Current implementation skips this check entirely and adds any body to the cart regardless of product existence. This is a bug (BUG-FR07-004).

### 4.6 400 Bad Request — Malformed JSON Body

**Trigger:** The request body is not valid JSON (e.g., missing closing brace, unquoted key, trailing comma).

```json
{
  "error": "..."
}
```

**Notes:** Express `body-parser` middleware handles this automatically before the route handler is reached. The exact error message format depends on the body-parser version.

### 4.7 500 Internal Server Error — Unexpected Server Error

**Trigger:** Unhandled exception or unexpected error in the route handler.

```json
{
  "error": "<error message>"
}
```

## 5. State Transitions

### 5.1 HTTP Response State

| Input Condition                                   | HTTP Status | Notes                                      |
| ------------------------------------------------- | ----------- | ------------------------------------------ |
| Valid token + valid body with existing product ID | `200`       | Item added (or merged per BR-03)           |
| No `Authorization` header                         | `401`       | Rejected by `authenticateToken` middleware |
| Invalid / tampered / unsigned token               | `403`       | Rejected by `jwt.verify()`                 |
| Valid token + `quantity` ≤ 0                      | `400`       |                                            |
| Valid token + `price` ≤ 0                         | `400`       |                                            |
| Valid token + `id` not found in products table    | `404`       |                                            |
| Valid token + `name` is empty or null             | `400`       |                                            |
| Malformed JSON body                               | `400`       | Handled by body-parser middleware          |

### 5.2 System Data State

| Entity | Field   | Before                                | After                                                                       | Condition                         |
| ------ | ------- | ------------------------------------- | --------------------------------------------------------------------------- | --------------------------------- |
| `Cart` | `items` | Does not contain the product `id`     | Cart contains one new item: `{ id, name, price, quantity }`                 | First time adding this product    |
| `Cart` | `items` | Already contains entry with same `id` | Quantity of existing entry is incremented by requested quantity (per BR-03) | Adding same product again         |
| `Cart` | `items` | Any state                             | No change                                                                   | Request rejected (401, 403, etc.) |

### 5.3 Field-Level State Preconditions

| Entity      | State Field | Allowed Values to Proceed              | Blocked Values             | Effect When Blocked |
| ----------- | ----------- | -------------------------------------- | -------------------------- | ------------------- |
| JWT `users` | `role`      | Any authenticated user (user or admin) | Unauthenticated (no token) | `401 Unauthorized`  |
| JWT payload | `exp`       | Not expired (or no `exp` claim)        | Expired `exp` timestamp    | `403 Forbidden`     |

> **Note:** The current implementation does not enforce a role restriction on this endpoint (any authenticated user including admin can call it). Per inferred e-commerce convention [BR-01 note], only `user`-role accounts should have shopping cart access, but this is not enforced in the current SUT.

## 6. Security Rules

| Rule ID | Description                                                                                                                                                 | Applicable Test Vector                                                                                                                                                                                                                  |
| ------- | ----------------------------------------------------------------------------------------------------------------------------------------------------------- | --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| SEC-02  | All cart operations require a valid JWT token. Unauthenticated access must return 401.                                                                      | Send `POST /api/cart` with no `Authorization` header. Expect `401`.                                                                                                                                                                     |
| SEC-02a | Tampered or forged tokens must be rejected. Only tokens signed with the system secret are valid.                                                            | Send request with a JWT signed with a different secret (e.g., `wrong_secret`). Expect `403`.                                                                                                                                            |
| SEC-02b | Token structure must be valid. Malformed, truncated, or non-JWT strings in `Authorization` must be rejected.                                                | Send `Authorization: Bearer not_a_jwt`. Expect `403`.                                                                                                                                                                                   |
| SEC-05  | Cart resources must be strictly scoped to the authenticated user. A user cannot inject a different `userId` via the body to manipulate another user's cart. | Send valid token for User A; include `"userId": <User_B_id>` in the body. Cart should be updated for User A only (userId derived from token, not body).                                                                                 |
| SEC-05a | IDOR via token reuse: using User B's token should only affect User B's cart.                                                                                | Obtain token for User B. Use it in a request. Verify cart update applies only to User B's cart, not User A's.                                                                                                                           |
| SEC-06  | Mass assignment: extra fields in the request body (e.g., `role`, `userId`, `__proto__`, `isAdmin`) must not be stored or processed.                         | Send body with extra fields: `{ "id":1, "name":"X", "price":100, "quantity":1, "role":"admin", "userId":999 }`. Verify cart entry does not contain these extra fields. Current implementation stores entire `req.body` — this is a bug. |
| SEC-05b | Prototype pollution: sending `__proto__` or `constructor` keys in body must not affect the server object prototype.                                         | Send `{ "id":1, "name":"X", "price":100, "quantity":1, "__proto__": {"admin": true} }`. Server must not be affected.                                                                                                                    |
| SEC-04  | XSS payload in string fields must not be stored as executable content and must be escaped on output.                                                        | Send `"name": "<script>alert(1)</script>"` in body. Verify it is stored as a literal string (not executed). Check GET /api/cart response escaping.                                                                                      |
| SEC-RL  | Rate limiting on cart operations. [inferred — OWASP API Security Top 10] **Known constraint: SUT does NOT implement rate limiting.**                        | Send 50+ rapid consecutive POST /api/cart requests. Expect `429 Too Many Requests`. **Expected result: currently returns 200 (known N/A for this SUT).**                                                                                |
